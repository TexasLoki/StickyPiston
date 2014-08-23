package org.pistonmc.stickypiston.exception.protocol;

public class IllegalProtocolException extends ProtocolException {

    private static final long serialVersionUID = 396294565259799441L;

    private String reason;

    public IllegalProtocolException(String version, String reason) {
        super(version, "Protocol v" + version + " could not be created: " + reason);
        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }

}
