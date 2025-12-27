package com.umerqureshicodes.tidier.WebSocket;

public class StompMessage {
    private String name;

    /**
     * the service will accept messages that contain a name in a STOMP message
     * whose body is a JSON object
     *
     * STOMP message example
     *
     * {
     *     "name": "Fred"
     * }
     *
     */

    public StompMessage(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
