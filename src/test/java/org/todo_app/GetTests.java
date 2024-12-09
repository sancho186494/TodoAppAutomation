package org.todo_app;

import com.google.inject.Inject;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.testng.annotations.Guice;
import org.testng.annotations.Test;
import org.todo_app.models.TodoTask;
import org.todo_app.steps.TodoAppRestSteps;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import static java.net.HttpURLConnection.HTTP_OK;
import static org.todo_app.utils.AllureMatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.todo_app.utils.AssertionsUtil.checkResponseCode;

@Feature("TodoApp API")
@Story("GET method")
@Guice(modules = TodoAppModule.class)
public class GetTests {

    @Inject
    private TodoAppRestSteps todoAppRestSteps;

    @Test(description = "GET method success test")
    public void checkGetMethod() {
        todoAppRestSteps.createTodo(new TodoTask("Test todo example"));
        var response = todoAppRestSteps.getTodos();
        checkResponseCode(response.code(), HTTP_OK);
        assertThat("Response body is empty", response.body().size(), not(equalTo(0)));

    }

    @Test(description = "GET method with empty body test")
    public void checkEmptyResponse() {
        todoAppRestSteps.deleteAllTodos();
        var response = todoAppRestSteps.getTodos();
        checkResponseCode(response.code(), HTTP_OK);
        assertThat("Response body isn't empty", response.body().size(), equalTo(0));
    }

    @Test(description = "GET method headers test")
    public void checkResponseHeaders() {
        String message = "Response doesn't contain header";
        var response = todoAppRestSteps.getTodos();
        assertThat(message + " 'content-type: application/json'",
                response.headers().get("content-type"), equalTo("application/json"));
        assertThat(message + " 'content-length'", response.headers().get("content-length"), notNullValue());
    }

    @Test(description = "GET method with limit query parameter test")
    public void checkLimitQueryParam() {
        int startId = 100;
        int endId = 109;
        int limit = 5;

        todoAppRestSteps.deleteAllTodos();
        IntStream.rangeClosed(startId, endId).forEach(id -> todoAppRestSteps.createTodo(new TodoTask("some todo")));
        var response = todoAppRestSteps.getTodos(Map.of("limit", limit));
        assertThat("Response body size is greater than expected limit " + limit,
                response.body().size(), equalTo(limit));
    }

    @Test(description = "GET method with offset query parameter test")
    public void checkOffsetQueryParam() {
        String todoText = "Some todo";
        int startId = 100;
        int endId = 109;
        int offset = 5;
        List<TodoTask> expectedTodos = new ArrayList<>();

        todoAppRestSteps.deleteAllTodos();
        IntStream.rangeClosed(startId, endId).forEach(id -> {
            TodoTask todoTask = new TodoTask(todoText + id).setId(id);
            todoAppRestSteps.createTodo(todoTask);
            if (id >= startId + offset) {
                expectedTodos.add(todoTask);
            }
        });
        var response = todoAppRestSteps.getTodos(Map.of("offset", offset));
        assertThat("Response body doesn't have expected offset result", expectedTodos.equals(response.body()));
    }
}
