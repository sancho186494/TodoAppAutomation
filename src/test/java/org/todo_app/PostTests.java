package org.todo_app;

import com.google.inject.Inject;
import org.testng.annotations.Guice;
import org.testng.annotations.Test;
import org.todo_app.api.TodoAppRestSteps;
import org.todo_app.models.TodoTask;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.todo_app.utils.AssertionsUtil.checkResponseCode;

@Guice(modules = TodoAppModule.class)
public class PostTests {

    @Inject
    private TodoAppRestSteps todoAppRestSteps;

    @Test
    public void checkTodoCreation() {
        TodoTask todo = new TodoTask("Test todo example");
        int responseCode = todoAppRestSteps.createTodo(todo).code();
        checkResponseCode(responseCode, 201);
        assertThat("New todo-task wasn't created", todo, is(in(todoAppRestSteps.getTodos().body())));
    }

    @Test
    public void checkTodoDuplicateCreation() {
        TodoTask newTodo = new TodoTask("Test todo example");
        checkResponseCode(todoAppRestSteps.createTodo(newTodo).code(), 201);
        checkResponseCode(todoAppRestSteps.createTodo(newTodo).code(), 400);
        int todoCount = (int) todoAppRestSteps.getTodos().body().stream()
                .filter(todo -> todo.equals(newTodo))
                .count();
        assertThat("Todo duplicate was created", todoCount, equalTo(1));
    }

    @Test
    public void checkRequiredFields() {
        TodoTask todo = new TodoTask("Test text");
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("id", todo.getId());
        requestBody.put("text", todo.getText());
        checkResponseCode(todoAppRestSteps.createTodo(requestBody).code(), 400);
        requestBody.remove("id");
        requestBody.put("completed", todo.isCompleted());
        checkResponseCode(todoAppRestSteps.createTodo(requestBody).code(), 400);
        requestBody.remove("text");
        requestBody.put("id", todo.getId());
        checkResponseCode(todoAppRestSteps.createTodo(requestBody).code(), 400);
    }

    @Test
    public void checkInvalidFieldTypes() {
        TodoTask todo = new TodoTask("Some todo example");
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("id", todo.getId());
        requestBody.put("text", "some text");
        requestBody.put("completed", String.valueOf(todo.isCompleted()));
        checkResponseCode(todoAppRestSteps.createTodo(requestBody).code(), 400);
        requestBody.put("completed", false);
        requestBody.put("id", String.valueOf(todo.getId()));
        checkResponseCode(todoAppRestSteps.createTodo(requestBody).code(), 400);
        requestBody.put("id", todo.getId());
        requestBody.put("text", 123);
        checkResponseCode(todoAppRestSteps.createTodo(requestBody).code(), 400);
    }

    @Test
    public void checkWrongHeader() {
        TodoTask todo = new TodoTask("one more todo");
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "text/plain");
        checkResponseCode(todoAppRestSteps.createTodo(todo, headers).code(), 415);
    }
}
