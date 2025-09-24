package com.umerqureshicodes.tidier.Utilities;

public class WebSocketServiceMessage {

    private final String content;
    private final String montagePath;

    public WebSocketServiceMessage(String content, String montagePath) {
        this.content = content;
        this.montagePath = montagePath;
    }

    public String getContent() {
        return content;
    }

    public String getMontagePath() {
        return montagePath;
    }
}
