package org.pistonmc.stickypiston.auth;

import org.json.JSONObject;
import org.pistonmc.util.OtherUtils;

import java.util.UUID;

public class BungeeAuthenticationHandler implements AuthenticationHandler {

    private UUID playerUUID;
    private JSONObject playerProfile;

    public boolean auth(String[] data) {
        if (data.length == 3 || data.length == 4) {
            playerUUID = OtherUtils.uuidFromString(data[2]);
            if (data.length == 4) {
                playerProfile = new JSONObject(data[3]);
            }

            return true;
        }

        return false;
    }

    public UUID getUUID() {
        return playerUUID;
    }

    public JSONObject getPlayerProfile() {
        return playerProfile;
    }

}
