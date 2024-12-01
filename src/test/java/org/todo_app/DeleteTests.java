package org.todo_app;

import com.google.inject.Inject;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Guice;
import org.testng.annotations.Test;
import org.todo_app.api.TodoAppRestSteps;
import org.todo_app.models.TodoTask;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.todo_app.utils.AssertionsUtil.checkResponseCode;

@Guice(modules = TodoAppModule.class)
public class DeleteTests {

    @Inject
    private TodoAppRestSteps todoAppRestSteps;

    private TodoTask todo;

    @BeforeMethod(alwaysRun = true)
    public void beforeMethod() {
        todo = new TodoTask("New todo task");
        checkResponseCode(todoAppRestSteps.createTodo(todo).code(), 201);
    }

    @Test
    public void checkDeleteMethod() {
        checkResponseCode(todoAppRestSteps.deleteTodo(todo).code(), 204);
        assertThat("Todo wasn't deleted", todo, is(not(in(todoAppRestSteps.getTodos().body()))));
    }

    @Test
    public void checkDeleteMethodNoAuth() {
        checkResponseCode(todoAppRestSteps.deleteTodoNoAuth(todo).code(), 401);
        assertThat("Todo was deleted", todo, is(in(todoAppRestSteps.getTodos().body())));
    }

    @Test
    public void checkDeleteMethodNotFound() {
        checkResponseCode(todoAppRestSteps.deleteTodo(todo.getId() + 1).code(), 404);
    }
}
