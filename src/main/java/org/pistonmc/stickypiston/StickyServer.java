package org.pistonmc.stickypiston;

import org.pistonmc.Server;
import org.pistonmc.stickypiston.network.protocol.Protocol;

import java.util.Map;

public class StickyServer implements Server {

    private Protocol protocol;
    private Map<String, Protocol> protocols;

}
