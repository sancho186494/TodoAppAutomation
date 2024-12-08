package org.todo_app;

import com.google.inject.Inject;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.testng.annotations.*;
import org.todo_app.api.TodoAppWebSocketService;
import org.todo_app.models.TodoTask;
import org.todo_app.models.WsMessage;
import org.todo_app.steps.TodoAppRestSteps;

import static java.net.HttpURLConnection.HTTP_CREATED;
import static org.todo_app.utils.AllureMatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.todo_app.utils.AssertionsUtil.checkResponseCode;

@Feature("TodoApp API")
@Story("WebSocket")
@Guice(modules = TodoAppModule.class)
public class WebSocketTests {

    @Inject
    private TodoAppRestSteps todoAppRestSteps;

    @Inject
    private TodoAppWebSocketService wsService;

    @BeforeClass(alwaysRun = true)
    public void beforeClass() throws InterruptedException {
        wsService.connect();
    }

    @AfterMethod(alwaysRun = true)
    public void afterMethod() {
        wsService.clearWsLog();
    }

    @AfterClass(alwaysRun = true)
    public void afterClass() {
        wsService.closeConnection();
    }

    @Test
    public void checkWebSocket() {
        checkResponseCode(todoAppRestSteps.createTodo(new TodoTask("some task")).code(), HTTP_CREATED);
        assertThat("WebSocket log is empty", wsService.getWsMessages().size(), not(equalTo(0)));
    }

    @Test
    public void checkWebSocketCreateMessageType() {
        checkResponseCode(todoAppRestSteps.createTodo(new TodoTask("some task")).code(), HTTP_CREATED);
        WsMessage wsMessage = wsService.getWsMessages().stream()
                .findFirst().orElseThrow(() -> new RuntimeException("WebSocket log is empty"));
        assertThat("WebSocket message type not equal 'new_todo'", wsMessage.getType(), equalTo("new_todo"));
    }

    @Test
    public void checkWebSocketMessageTodoExists() {
        TodoTask todoTask = new TodoTask("Test websocket message");
        checkResponseCode(todoAppRestSteps.createTodo(todoTask).code(), HTTP_CREATED);
        var wsMessage = wsService.getWsMessages().stream()
                .filter(message -> message.getData().equals(todoTask))
                .findAny();
        assertThat("No message with new todo-task in WebSocket log", wsMessage.isPresent());
    }
}
