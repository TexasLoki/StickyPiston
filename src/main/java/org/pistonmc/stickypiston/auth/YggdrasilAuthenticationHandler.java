package org.pistonmc.stickypiston.auth;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONObject;
import org.pistonmc.Piston;
import org.pistonmc.util.OtherUtils;

import java.io.IOException;
import java.util.UUID;

public class YggdrasilAuthenticationHandler implements AuthenticationHandler {

    private UUID playerUUID;
    private JSONObject playerProfile;

    public void auth(final String username, final String hash, final Callback cb) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                CloseableHttpClient client = HttpClients.createDefault();
                HttpGet request = new HttpGet("https://sessionserver.mojang.com/session/minecraft/hasJoined?username=" + username + "&serverId=" + hash);
                try {
                    CloseableHttpResponse response = client.execute(request);
                    HttpEntity body = response.getEntity();
                    String json = IOUtils.toString(body.getContent());
                    if (json == null || json.isEmpty()) {
                        cb.onExecute(false);
                    } else {
                        playerProfile = new JSONObject(json);
                        playerUUID = OtherUtils.uuidFromString(playerProfile.getString("id"));
                        cb.onExecute(true);
                    }
                } catch (IOException e) {
                    cb.onExecute(false);
                    Piston.getLogger().warning(e);
                }
            }

        }).start();
        cb.onExecute(true);
    }

    @Override
    public UUID getUUID() {
        return playerUUID;
    }

    @Override
    public JSONObject getPlayerProfile() {
        return playerProfile;
    }

    public static interface Callback {

        public void onExecute(boolean result);

    }

}
