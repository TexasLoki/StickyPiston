package org.pistonmc.stickypiston.exception.protocol;

public class ProtocolException extends Exception {

    private String protocol;

    public ProtocolException(String protocol, String message) {
        super(message);
        this.protocol = protocol;
    }

    public String getProtocol() {
        return protocol;
    }

}
