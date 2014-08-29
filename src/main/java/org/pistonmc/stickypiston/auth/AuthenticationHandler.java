package org.pistonmc.stickypiston.auth;

import org.json.JSONObject;

import java.util.UUID;

public interface AuthenticationHandler {

    public UUID getUUID();
    public JSONObject getPlayerProfile();


}
