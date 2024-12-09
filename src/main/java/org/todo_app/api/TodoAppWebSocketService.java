package org.todo_app.api;

import com.google.gson.Gson;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;
import org.aeonbits.owner.ConfigFactory;
import org.todo_app.DefaultWebSocketListener;
import org.todo_app.config.TodoAppRestConfig;
import org.todo_app.models.WsMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static java.util.Collections.synchronizedList;

public class TodoAppWebSocketService {

    private WebSocket webSocket;
    private final List<WsMessage> wsMessages = synchronizedList(new ArrayList<>());
    private final CountDownLatch connectionLatch = new CountDownLatch(1);

    public void connect() throws InterruptedException {
        TodoAppRestConfig config = ConfigFactory.create(TodoAppRestConfig.class);
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(config.baseWsUrl())
                .build();
        webSocket = client.newWebSocket(request, new TodoAppWebSocketListener());

        boolean connected = connectionLatch.await(10, TimeUnit.SECONDS);
        if (!connected) {
            throw new RuntimeException("Failed to establish WebSocket connection within timeout");
        }
    }

    public void closeConnection() {
        wsMessages.clear();
        webSocket.cancel();
    }

    public void clearWsLog() {
        wsMessages.clear();
    }

    public List<WsMessage> getWsMessages() {
        return wsMessages;
    }

    private class TodoAppWebSocketListener extends DefaultWebSocketListener {

        @Override
        public void onOpen(WebSocket webSocket, okhttp3.Response response) {
            super.onOpen(webSocket, response);
            connectionLatch.countDown();
        }

        @Override
        public void onMessage(WebSocket webSocket, String text) {
            wsMessages.add(new Gson().fromJson(text, WsMessage.class));
            super.onMessage(webSocket, text);
        }

        @Override
        public void onFailure(WebSocket webSocket, Throwable t, okhttp3.Response response) {
            super.onFailure(webSocket, t, response);
            connectionLatch.countDown();
        }
    }
}
