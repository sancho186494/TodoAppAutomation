package org.todo_app;

import com.google.inject.Inject;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Guice;
import org.testng.annotations.Test;
import org.todo_app.models.TodoTask;
import org.todo_app.steps.TodoAppRestSteps;

import static java.net.HttpURLConnection.*;
import static org.todo_app.utils.AllureMatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.todo_app.utils.AssertionsUtil.checkResponseCode;

@Feature("TodoApp API")
@Story("DELETE method")
@Guice(modules = TodoAppModule.class)
public class DeleteTests {

    @Inject
    private TodoAppRestSteps todoAppRestSteps;

    private TodoTask todo;

    @BeforeMethod(alwaysRun = true)
    public void beforeMethod() {
        todo = new TodoTask("New todo task");
        checkResponseCode(todoAppRestSteps.createTodo(todo).code(), HTTP_CREATED);
    }

    @Test(description = "DELETE method success test")
    public void checkDeleteMethod() {
        checkResponseCode(todoAppRestSteps.deleteTodo(todo).code(), HTTP_NO_CONTENT);
        assertThat("Todo wasn't deleted", todo, is(not(in(todoAppRestSteps.getTodos().body()))));
    }

    @Test(description = "DELETE method without auth test")
    public void checkDeleteMethodNoAuth() {
        checkResponseCode(todoAppRestSteps.deleteTodoNoAuth(todo).code(), HTTP_UNAUTHORIZED);
        assertThat("Todo was deleted", todo, is(in(todoAppRestSteps.getTodos().body())));
    }

    @Test(description = "DELETE method with wrong id test")
    public void checkDeleteMethodNotFound() {
        checkResponseCode(todoAppRestSteps.deleteTodo(todo.getId() + 1).code(), HTTP_NOT_FOUND);
    }
}
