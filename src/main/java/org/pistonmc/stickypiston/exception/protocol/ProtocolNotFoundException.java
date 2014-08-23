package org.pistonmc.stickypiston.exception.protocol;

public class ProtocolNotFoundException extends ProtocolException {

    private static final long serialVersionUID = 396294565259799441L;

    public ProtocolNotFoundException(String version) {
        super(version, "Could not find Protocol v" + version);
    }

}
