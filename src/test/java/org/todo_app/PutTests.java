package org.todo_app;

import com.google.inject.Inject;
import org.testng.annotations.BeforeMethod;
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
public class PutTests {

    @Inject
    private TodoAppRestSteps todoAppRestSteps;

    private TodoTask todo;

    @BeforeMethod(alwaysRun = true)
    public void beforeMethod() {
        todo = new TodoTask("New todo task");
        checkResponseCode(todoAppRestSteps.createTodo(todo).code(), 201);
    }

    @Test
    public void checkTodoChange() {
        todo.setText("Changed text").setCompleted(true);
        checkResponseCode(todoAppRestSteps.editTodo(todo).code(), 200);
        assertThat("Not found changed todo", todo, is(in(todoAppRestSteps.getTodos().body())));
    }

    @Test
    public void checkTodoChangeNoPathId() {
        todo.setText("Changed new").setCompleted(true);
        checkResponseCode(todoAppRestSteps.editTodoNoPathId(todo).code(), 405);
        assertThat("Found changed todo", todo, is(not(in(todoAppRestSteps.getTodos().body()))));
    }

    @Test
    public void checkRequiredFields() {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("id", todo.getId());
        requestBody.put("text", todo.getText());
        checkResponseCode(todoAppRestSteps.editTodo(todo.getId(), requestBody).code(), 400);
        requestBody.remove("id");
        requestBody.put("completed", todo.isCompleted());
        checkResponseCode(todoAppRestSteps.editTodo(todo.getId(), requestBody).code(), 400);
        requestBody.remove("text");
        requestBody.put("id", todo.getId());
        checkResponseCode(todoAppRestSteps.editTodo(todo.getId(), requestBody).code(), 400);
    }

    @Test
    public void checkInvalidFieldTypes() {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("id", todo.getId());
        requestBody.put("text", "some text");
        requestBody.put("completed", String.valueOf(todo.isCompleted()));
        checkResponseCode(todoAppRestSteps.editTodo(todo.getId(), requestBody).code(), 400);
        requestBody.put("completed", false);
        requestBody.put("id", String.valueOf(todo.getId()));
        checkResponseCode(todoAppRestSteps.editTodo(todo.getId(), requestBody).code(), 400);
        requestBody.put("id", todo.getId());
        requestBody.put("text", 123);
        checkResponseCode(todoAppRestSteps.editTodo(todo.getId(), requestBody).code(), 400);
    }

    @Test
    public void checkWrongHeader() {
        todo.setCompleted(true);
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "text/plain");
        checkResponseCode(todoAppRestSteps.editTodo(todo, headers).code(), 415);
    }
}
