package org.pistonmc.stickypiston.auth;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpException;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONObject;
import org.pistonmc.Piston;
import org.pistonmc.stickypiston.exception.ExceptionHandler;
import org.pistonmc.util.OtherUtils;

import java.io.IOException;
import java.util.UUID;

public class YggdrasilAuthenticationHandler implements AuthenticationHandler {

    private UUID playerUUID;
    private JSONObject playerProfile;

    public void auth(final String username, final String hash, final Callback cb) {
        Thread t = new Thread(new Runnable() {

            @Override
            public void run() {
                CloseableHttpClient client = HttpClients.createDefault();
                HttpGet request = new HttpGet("https://sessionserver.mojang.com/session/minecraft/hasJoined?username=" + username + "&serverId=" + hash);
                CloseableHttpResponse response = null;
                try {
                    response = client.execute(request);
                    String body = IOUtils.toString(response.getEntity().getContent());
                    if (body == null || body.isEmpty()) {
                        cb.onExecute(false);
                        return;
                    }
                    playerProfile = new JSONObject(body);
                    playerUUID = OtherUtils.uuidFromString(playerProfile.getString("id"));
                    cb.onExecute(true);
                } catch (Exception e) {
                    Piston.getLogger().warning(e);
                    cb.onExecute(false);
                } finally {
                    try {
                        client.close();
                        if (response != null) {
                            response.close();
                        }
                    } catch (Exception e) {
                        Piston.getLogger().warning(e);
                        cb.onExecute(false);
                    }
                }
            }

        });
        t.setUncaughtExceptionHandler(new ExceptionHandler());
        t.run();
        // TODO: Make it async, because for some reason when I do t.start() it doesn't work
        //t.start();
    }

    public UUID getUUID() {
        return playerUUID;
    }

    public JSONObject getPlayerProfile() {
        return playerProfile;
    }

    public static interface Callback {

        public void onExecute(boolean result);

    }

}
