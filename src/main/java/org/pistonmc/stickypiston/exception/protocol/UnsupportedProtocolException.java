package org.pistonmc.stickypiston.exception.protocol;

public class UnsupportedProtocolException extends ProtocolException {

    private static final long serialVersionUID = 396294565259799441L;

    private String reason;

    public UnsupportedProtocolException(String version, String reason) {
        super(version, "Protocol v" + version + " is not supported: " + reason);
        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }

}
