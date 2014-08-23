package org.pistonmc.stickypiston.plugin;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.pistonmc.Piston;
import org.pistonmc.logging.Logger;
import org.pistonmc.logging.Logging;
import org.pistonmc.util.reflection.SimpleObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

public class PluginManager<T extends JavaPlugin> {

    private Logger logger;
    private File folder;
    private final String config;

    private List<T> plugins;

    public PluginManager(Logger logger, File folder, String config) {
        this.logger = logger;
        this.folder = folder;
        this.config = config;
        this.plugins = new ArrayList<>();
    }

    public List<T> getPlugins() {
        return plugins;
    }

    protected T construct(Class<T> cls) throws Exception {
        return cls.getDeclaredConstructor().newInstance();
    }

    public File getPluginsFolder() {
        return folder;
    }

    public JavaPlugin getPlugin(Class<? extends JavaPlugin> clazz) {
        for(JavaPlugin plugin : plugins) {
            if(plugin.getClass().equals(clazz)) {
                return plugin;
            }
        }

        return null;
    }

    public JavaPlugin getPlugin(String name) {
        return getPlugin(name, true);
    }

    public JavaPlugin getPlugin(String name, boolean sensitive) {
        for(JavaPlugin plugin : plugins) {
            String pluginName = plugin.getDescription().getName();
            if(!sensitive) {
                pluginName = pluginName.toLowerCase();
                name = name.toLowerCase();
            }

            if(pluginName.equals(name)) {
                return plugin;
            }
        }

        return null;
    }

    public T load(File file) {
        JarFile jar;
        try {
            jar = new JarFile(file, true);
        } catch(IOException ex) {
            logger.log("Could not load plugin at " + file.getPath() + ": ", ex);
            return null;
        }

        ZipEntry entry = jar.getEntry("plugin.json");
        String string;
        try {
            InputStream stream = jar.getInputStream(entry);
            string = IOUtils.toString(stream);
        } catch(IOException ex) {
            logger.log("Could not load plugin at " + file.getPath() + ": ", ex);
            return null;
        }

        JSONObject manifest;
        try {
            manifest = new JSONObject(string);
        } catch(JSONException ex) {
            logger.log("Could not load " + file.getName() + " because it's " + config + " was not valid: ", ex);
            return null;
        }

        String name;
        try {
            name = manifest.getString("name");
            if(name == null) {
                throw new NullPointerException("name");
            }
        } catch(Exception ex) {
            logger.warning("Could not load " + file.getName() + " because it's " + config + " does not contain a valid name");
            return null;
        }

        String version;
        try {
            version = manifest.getString("version");
            if(version == null) {
                throw new NullPointerException("version");
            }
        } catch(Exception ex) {
            logger.warning("Could not load " + name + " because it's " + config + " does not contain a valid version");
            return null;
        }

        String main;
        try {
            main = manifest.getString("main");
            if(main == null) {
                throw new NullPointerException("main");
            }
        } catch(Exception ex) {
            logger.warning("Could not load " + name + " because it's " + config + " does not contain a valid main class");
            return null;
        }

        List<String> authors = new ArrayList<>();
        if(manifest.has("authors")) {
            try {
                JSONArray array = manifest.getJSONArray("authors");
                for(int i = 0; i < array.length(); i++) {
                    String author = array.getString(i);
                    if(author != null) {
                        authors.add(author);
                    }
                }
            } catch(Exception ex) {
                logger.warning("Ignoring the list of authors from " + name + " because the JSONArray was invalid");
            }
        }

        URLClassLoader loader;
        try {
            loader = ClassPathLoader.addFile(file);
        } catch(IOException ex) {
            logger.info("Invalid plugin.json for " + name + ": Could not load " + file.getName() + " into the classpath");
            return null;
        }

        Class<?> plugin;
        try {
            plugin = Class.forName(main);
        } catch(ClassNotFoundException ex) {
            logger.info("Invalid " + config + " for " + name + ": " + main + " does not exist");
            return null;
        }

        if(!JavaPlugin.class.isAssignableFrom(plugin)) {
            logger.info("Invalid " + config + " for " + name + ": " + main + " is not assignable from " + JavaPlugin.class.getSimpleName());
            return null;
        }

        PluginDescription description = new PluginDescription(name, version, authors);
        try {
            T loaded = load((Class<T>) plugin, description, loader, file);
            plugins.add(loaded);
            return loaded;
        } catch(Exception ex) {
            logger.log("Failed to load " + name + ": ", ex);
        }

        return null;
    }

    public T load(Class<T> clazz, PluginDescription description, URLClassLoader loader, File file) throws Exception {
        T plugin = clazz.newInstance();

        SimpleObject object = new SimpleObject(plugin, clazz);
        object.field("handler").set(this);
        object.field("loader").set(loader);
        object.field("file").set(file);
        object.field("description").set(description);
        object.field("logger").set(Logging.getLogger(description.getName(), logger));
        object.field("dataFolder").set(new File(getPluginsFolder(), description.getName()));
        object.field("config").set(new JSONObject());
        // plugin.readConfig();

        plugin.getLogger().info("Loaded " + description.getName() + " v" + description.getVersion());
        return plugin;
    }

    public boolean unload(JavaPlugin plugin) {
        plugins.remove(plugin);
        plugin.setEnabled(false);
        try {
            ((URLClassLoader) new SimpleObject(plugin, JavaPlugin.class).field("loader").value()).close();
            return true;
        } catch(Exception ex) {
            logger.log("Could not unload " + plugin.getDescription().getName() + ": ", ex);
            return false;
        }
    }

    public void reload(T plugin) {
        File file = (File) new SimpleObject(plugin).field("file").value();
        // unload(plugin);
        plugin.setEnabled(false);
        plugin = load(file);
        if(plugin != null) {
            plugin.setEnabled(true);
        }
    }

    public void reload(boolean enable) {
        for(JavaPlugin plugin : plugins) {
            plugin.setEnabled(false);
        }

        plugins = new ArrayList<>();

        if(!folder.exists()) {
            if(folder.mkdirs()) {
                logger.info("Plugins folder did not exist, created one at " + folder.getPath());
            } else {
                logger.info("Failed to create plugins folder at " + folder.getPath());
                Piston.shutdown();
                return;
            }
        }

        if(!folder.isDirectory()) {
            logger.info("The plugins folder, " + folder.getPath() + " is a directory");
            Piston.shutdown();
            return;
        }

        for(File file : folder.listFiles()) {
            if(file.getName().toLowerCase().endsWith(".jar")) {
                load(file);
            }
        }

        if(enable) {
            for(JavaPlugin plugin : plugins) {
                plugin.setEnabled(true);
            }
        }
    }

}
