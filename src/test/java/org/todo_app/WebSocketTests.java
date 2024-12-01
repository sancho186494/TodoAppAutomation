package org.todo_app;

import com.google.inject.Inject;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Guice;
import org.testng.annotations.Test;
import org.todo_app.api.TodoAppRestSteps;
import org.todo_app.api.TodoAppWebSocketService;
import org.todo_app.models.TodoTask;
import org.todo_app.models.WsMessage;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.todo_app.utils.AssertionsUtil.checkResponseCode;

@Guice(modules = TodoAppModule.class)
public class WebSocketTests {

    @Inject
    private TodoAppRestSteps todoAppRestSteps;

    @Inject
    private TodoAppWebSocketService wsService;

    @BeforeMethod(alwaysRun = true)
    public void beforeMethod() {
        wsService.connect();
    }

    @AfterMethod(alwaysRun = true)
    public void afterMethod() {
        wsService.closeConnection();
    }

    @Test
    public void checkWebSocket() {
        checkResponseCode(todoAppRestSteps.createTodo(new TodoTask("some task")).code(), 201);
        assertThat("WebSocket log is empty", wsService.getWsMessages().size(), not(equalTo(0)));
    }

    @Test
    public void checkWebSocketCreateMessageType() {
        checkResponseCode(todoAppRestSteps.createTodo(new TodoTask("some task")).code(), 201);
        WsMessage wsMessage = wsService.getWsMessages().stream()
                .findFirst().orElseThrow(() -> new RuntimeException("WebSocket log is empty"));
        assertThat("WebSocket message type not equal 'new_todo'", wsMessage.getType(), equalTo("new_todo"));
    }

    @Test
    public void checkWebSocketMessageTodoExists() {
        TodoTask todoTask = new TodoTask("Test websocket message");
        checkResponseCode(todoAppRestSteps.createTodo(todoTask).code(), 201);
        var wsMessage = wsService.getWsMessages().stream()
                .filter(message -> message.getData().equals(todoTask))
                .findAny();
        assertThat("No message with new todo-task in WebSocket log", wsMessage.isPresent());
    }
}
