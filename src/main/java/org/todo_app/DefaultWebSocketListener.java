package org.todo_app;

import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultWebSocketListener extends WebSocketListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultWebSocketListener.class);

    @Override
    public void onOpen(WebSocket webSocket, okhttp3.Response response) {
        LOGGER.info("Connection opened: " + response);
        webSocket.send("Hello, server!");
    }

    @Override
    public void onMessage(WebSocket webSocket, String text) {
        LOGGER.info("Received text message: " + text);
    }

    @Override
    public void onMessage(WebSocket webSocket, ByteString bytes) {
        LOGGER.info("Received binary message: " + bytes.hex());
    }

    @Override
    public void onClosing(WebSocket webSocket, int code, String reason) {
        webSocket.close(1000, null);
        LOGGER.info("Closing connection: Code = " + code + ", Reason = " + reason);
    }

    @Override
    public void onClosed(WebSocket webSocket, int code, String reason) {
        LOGGER.info("Connection closed: Code = " + code + ", Reason = " + reason);
    }

    @Override
    public void onFailure(WebSocket webSocket, Throwable t, okhttp3.Response response) {
        LOGGER.error("Connection error: " + t.getMessage());
    }
}
