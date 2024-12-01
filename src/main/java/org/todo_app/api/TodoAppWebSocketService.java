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

public class TodoAppWebSocketService {

    private WebSocket webSocket;
    private final List<WsMessage> wsMessages = new ArrayList<>();

    public void connect() {
        TodoAppRestConfig config = ConfigFactory.create(TodoAppRestConfig.class);
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(config.baseWsUrl())
                .build();
        webSocket = client.newWebSocket(request, new TodoAppWebSocketListener());
    }

    public void closeConnection() {
        wsMessages.clear();
        webSocket.cancel();
    }

    public List<WsMessage> getWsMessages() {
        return wsMessages;
    }

    private class TodoAppWebSocketListener extends DefaultWebSocketListener {
        @Override
        public void onMessage(WebSocket webSocket, String text) {
            wsMessages.add(new Gson().fromJson(text, WsMessage.class));
            super.onMessage(webSocket, text);
        }
    }
}
