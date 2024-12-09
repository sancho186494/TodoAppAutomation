package org.todo_app;

import com.google.inject.Inject;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Guice;
import org.testng.annotations.Test;
import org.todo_app.models.TodoTask;
import org.todo_app.steps.TodoAppRestSteps;

import java.util.Map;

import static java.lang.String.valueOf;
import static java.net.HttpURLConnection.*;
import static org.todo_app.utils.AllureMatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.todo_app.utils.AssertionsUtil.checkResponseCode;

@Feature("TodoApp API")
@Story("POST method")
@Guice(modules = TodoAppModule.class)
public class PostTests {

    @Inject
    private TodoAppRestSteps todoAppRestSteps;

    @DataProvider(name = "requiredFieldsData")
    public Object[][] requiredFieldsData() {
        TodoTask todo = new TodoTask("Test text");
        return new Object[][] {
                { Map.of("id", todo.getId(), "text", todo.getText()) },
                { Map.of("completed", todo.isCompleted(), "text", todo.getText()) },
                { Map.of("id", todo.getId(), "completed", todo.isCompleted()) }
        };
    }

    @DataProvider(name = "invalidFieldTypesData")
    public Object[][] invalidFieldTypesData() {
        TodoTask todo = new TodoTask("Some todo example");
        return new Object[][] {
                { Map.of("id", todo.getId(), "text", "some text", "completed", valueOf(todo.isCompleted())) },
                { Map.of("id", valueOf(todo.getId()), "text", "some text", "completed", false) },
                { Map.of("id", todo.getId(), "text", 123, "completed", false) }
        };
    }

    @Test(description = "POST method. New todo task creation.")
    public void checkTodoCreation() {
        TodoTask todo = new TodoTask("Test todo example");
        int responseCode = todoAppRestSteps.createTodo(todo).code();
        checkResponseCode(responseCode, 201);
        assertThat("New todo-task wasn't created", todo, is(in(todoAppRestSteps.getTodos().body())));
    }

    @Test(description = "POST method. Duplicate todo task creation.")
    public void checkTodoDuplicateCreation() {
        TodoTask newTodo = new TodoTask("Test todo example");
        checkResponseCode(todoAppRestSteps.createTodo(newTodo).code(), HTTP_CREATED);
        checkResponseCode(todoAppRestSteps.createTodo(newTodo).code(), HTTP_BAD_REQUEST);
        int todoCount = (int) todoAppRestSteps.getTodos().body().stream()
                .filter(todo -> todo.equals(newTodo))
                .count();
        assertThat("Todo duplicate was created", todoCount, equalTo(1));
    }

    @Test(description = "POST method. Check required fields.", dataProvider = "requiredFieldsData")
    public void checkRequiredFields(Map<String, Object> requestBody) {
        checkResponseCode(todoAppRestSteps.createTodo(requestBody).code(), HTTP_BAD_REQUEST);
    }

    @Test(description = "POST method. Check invalid field values.", dataProvider = "invalidFieldTypesData")
    public void checkInvalidFieldTypes(Map<String, Object> requestBody) {
        checkResponseCode(todoAppRestSteps.createTodo(requestBody).code(), HTTP_BAD_REQUEST);
    }

    @Test(description = "POST method. Check wrong header.")
    public void checkWrongHeader() {
        TodoTask todo = new TodoTask("one more todo");
        checkResponseCode(todoAppRestSteps.createTodo(todo, Map.of("Content-Type", "text/plain")).code(),
                HTTP_UNSUPPORTED_TYPE);
    }
}
