package org.pistonmc.stickypiston.network.protocol;

import com.google.common.collect.Lists;
import org.pistonmc.protocol.Client;
import org.pistonmc.stickypiston.exception.protocol.ProtocolException;
import org.pistonmc.stickypiston.exception.protocol.ProtocolNotFoundException;
import org.pistonmc.stickypiston.plugin.PluginManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProtocolManager extends PluginManager<Protocol> {

    private Map<String, Protocol> protocols;

    public ProtocolManager() {
        protocols = new HashMap<>();
    }

    public void load() {

    }

    public List<Protocol> getProtocols() {
        return Lists.newArrayList(protocols.values());
    }

    public Protocol find(String version) throws ProtocolException {
        return find(version, null);
    }

    public Protocol find(String version, Client client) throws ProtocolException {
        Protocol result = protocols.get(version);
        if(result == null) {
            throw new ProtocolNotFoundException(version);
        }

        return result.create(client);
    }

}
