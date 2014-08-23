package org.pistonmc.stickypiston.network.protocol;

import org.pistonmc.protocol.Client;

public abstract class Protocol {

    private Client client;

    protected Protocol(Client client) {
        this.client = client;
    }

    public abstract Protocol create(Client client);

}
