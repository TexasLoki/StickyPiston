package org.pistonmc.stickypiston.auth;

import org.json.JSONObject;

import java.util.UUID;

public class YggdrasilAuthenticationHandler implements AuthenticationHandler {

    private UUID playerUUID;
    private JSONObject playerProfile;

    public boolean auth() {
        return true;
    }

    @Override
    public UUID getUUID() {
        return playerUUID;
    }

    @Override
    public JSONObject getPlayerProfile() {
        return playerProfile;
    }

}
