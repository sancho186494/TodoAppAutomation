package org.todo_app;

import com.google.inject.Inject;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.testng.annotations.*;
import org.todo_app.api.TodoAppWebSocketService;
import org.todo_app.models.TodoTask;
import org.todo_app.steps.TodoAppRestSteps;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

import static java.net.HttpURLConnection.HTTP_CREATED;
import static org.todo_app.utils.AllureMatcherAssert.assertThatDuringPeriod;
import static org.todo_app.utils.AllureMatcherAssert.assertThatWithWait;
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

    @Test(description = "WebSocket. Check connection.")
    public void wsConnectionTest() {
        checkResponseCode(todoAppRestSteps.createTodo(new TodoTask("some task")).code(), HTTP_CREATED);
        assertThatWithWait("WebSocket log is empty", () -> !wsService.getWsMessages().isEmpty());
    }

    @Test(description = "WebSocket. Check received messages type.")
    public void checkWsCreateMessageType() {
        checkResponseCode(todoAppRestSteps.createTodo(new TodoTask("some task")).code(), HTTP_CREATED);
        assertThatWithWait("WebSocket message type doesn't equal expected type 'new_todo'",
                () -> wsService.getWsMessages().stream()
                        .anyMatch(message -> message.getType().equals("new_todo")));
    }

    @Test(description = "WebSocket. Check received message with new todo task.")
    public void checkWsMessageTodoExists() {
        TodoTask todoTask = new TodoTask("Test websocket message");
        checkResponseCode(todoAppRestSteps.createTodo(todoTask).code(), HTTP_CREATED);
        assertThatWithWait("Not found message with new todo-task in WebSocket log",
                () -> wsService.getWsMessages().stream()
                        .anyMatch(message -> message.getData().equals(todoTask)));
    }

    @Test(description = "WebSocket. Check large amount of messages.")
    public void checkWsLargeAmountMessagesTest() {
        int start = 0;
        int finish = 100;
        IntStream.range(start, finish).forEach(numb -> todoAppRestSteps.createTodo(new TodoTask("some task")));
        assertThatWithWait("WebSocket log doesn't contain messages count = " + (finish - start),
                () -> wsService.getWsMessages().size() == finish - start);
    }

    @Test(description = "WebSocket. Check no messages except creation todo task.")
    public void checkNoMessageTest() {
        TodoTask todoTask = new TodoTask("Test websocket message");
        checkResponseCode(todoAppRestSteps.createTodo(todoTask).code(), HTTP_CREATED);
        wsService.clearWsLog();
        todoAppRestSteps.getTodos();
        todoAppRestSteps.editTodo(todoTask.setCompleted(true));
        todoAppRestSteps.deleteTodo(todoTask);
        todoAppRestSteps.createTodo(new TodoTask());
        assertThatDuringPeriod("Found message in WebSocket log", () -> wsService.getWsMessages().isEmpty());
    }

    @Test(description = "WebSocket. Check multiply parallel creation todo task messages.")
    public void checkParallelMessagesTest() {
        List<TodoTask> todos = new ArrayList<>();
        IntStream.range(0, 10).forEach(numb -> {
            TodoTask task = new TodoTask("Some task " + numb);
            todos.add(task);
            createThread(task).start();
        });
        todos.forEach(todoTask -> {
            assertThatWithWait("Not found creation message in WebSocket log with task " + todoTask,
                    () -> wsService.getWsMessages().stream().anyMatch(wsMessage -> wsMessage.getData().equals(todoTask)));
        });
    }

    private Thread createThread(TodoTask todoTask) {
        return new Thread(() -> {
            int minDelay = 250;
            int maxDelay = 5000;
            try {
                Thread.sleep(new Random().nextLong((maxDelay - minDelay) + 1) + minDelay);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            todoAppRestSteps.createTodo(todoTask);
        });
    }
}
