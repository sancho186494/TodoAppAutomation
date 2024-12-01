package org.todo_app;

import com.google.inject.Inject;
import org.testng.annotations.Guice;
import org.testng.annotations.Test;
import org.todo_app.api.TodoAppRestSteps;
import org.todo_app.models.TodoTask;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.todo_app.utils.AssertionsUtil.checkResponseCode;

@Guice(modules = TodoAppModule.class)
public class GetTests {

    @Inject
    private TodoAppRestSteps todoAppRestSteps;

    @Test
    public void checkGetMethod() {
        todoAppRestSteps.createTodo(new TodoTask("Test todo example"));
        var response = todoAppRestSteps.getTodos();
        checkResponseCode(response.code(), 200);
        assertThat("Response body is empty", response.body().size(), not(equalTo(0)));

    }

    @Test
    public void checkEmptyResponse() {
        todoAppRestSteps.getTodos().body().forEach(todo -> todoAppRestSteps.deleteTodo(todo));
        var response = todoAppRestSteps.getTodos();
        checkResponseCode(response.code(), 200);
        assertThat("Response body isn't empty", response.body().size(), equalTo(0));
    }

    @Test
    public void checkResponseHeaders() {
        String message = "Response doesn't contain header";
        var response = todoAppRestSteps.getTodos();
        assertThat(message + " 'content-type: application/json'",
                response.headers().get("content-type"), equalTo("application/json"));
        assertThat(message + " 'content-length'", response.headers().get("content-length"), notNullValue());
    }
}
