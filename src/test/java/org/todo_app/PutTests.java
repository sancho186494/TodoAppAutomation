package org.todo_app;

import com.google.inject.Inject;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.testng.annotations.BeforeClass;
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
@Story("PUT methods")
@Guice(modules = TodoAppModule.class)
public class PutTests {

    @Inject
    private TodoAppRestSteps todoAppRestSteps;

    private TodoTask todo;

    @DataProvider(name = "invalidRequestBodies")
    public Object[][] invalidRequestBodies() {
        return new Object[][] {
                { Map.of("id", todo.getId(), "text", todo.getText()) },
                { Map.of("completed", todo.isCompleted(), "text", todo.getText()) },
                { Map.of("id", todo.getId(), "completed", todo.isCompleted()) }
        };
    }

    @DataProvider(name = "invalidFieldTypes")
    public Object[][] invalidFieldTypes() {
        return new Object[][] {
                { Map.of("id", todo.getId(), "text", "some text", "completed", valueOf(todo.isCompleted())) },
                { Map.of("id", valueOf(todo.getId()), "text", "some text", "completed", false) },
                { Map.of("id", todo.getId(), "text", 123, "completed", false) }
        };
    }

    @BeforeClass(alwaysRun = true)
    public void beforeClass() {
        todo = new TodoTask("New todo task");
        checkResponseCode(todoAppRestSteps.createTodo(todo).code(), HTTP_CREATED);
    }

    @Test(description = "PUT method. Editing existing todo task.")
    public void a1_checkTodoChange() {
        todo.setText("Changed text").setCompleted(true);
        checkResponseCode(todoAppRestSteps.editTodo(todo).code(), HTTP_OK);
        assertThat("Not found changed todo", todo, is(in(todoAppRestSteps.getTodos().body())));
    }

    @Test(description = "PUT method. Editing existing todo task with no path id in query.")
    public void a2_checkTodoChangeNoPathId() {
        todo.setText("Changed new").setCompleted(true);
        checkResponseCode(todoAppRestSteps.editTodoNoPathId(todo).code(), HTTP_BAD_METHOD);
        assertThat("Found changed todo", todo, is(not(in(todoAppRestSteps.getTodos().body()))));
    }

    @Test(description = "PUT method. Check required fields.", dataProvider = "invalidRequestBodies")
    public void a3_checkRequiredFields(Map<String, Object> requestBody) {
        checkResponseCode(todoAppRestSteps.editTodo(todo.getId(), requestBody).code(), HTTP_BAD_REQUEST);
    }

    @Test(description = "PUT method. Check invalid field values.", dataProvider = "invalidFieldTypes")
    public void a4_checkInvalidFieldTypes(Map<String, Object> requestBody) {
        checkResponseCode(todoAppRestSteps.editTodo(todo.getId(), requestBody).code(), HTTP_BAD_REQUEST);
    }

    @Test(description = "PUT method. Check wrong header.")
    public void a5_checkWrongHeader() {
        todo.setCompleted(true);
        checkResponseCode(todoAppRestSteps.editTodo(todo, Map.of("Content-Type", "text/plain")).code(),
                HTTP_UNSUPPORTED_TYPE);
    }

    @Test(description = "PUT method. Editing todo task by wrong id.")
    public void a6_checkWrongTodoId() {
        todo.setId(todo.getId() + 1).setCompleted(true);
        checkResponseCode(todoAppRestSteps.editTodo(todo).code(), HTTP_NOT_FOUND);
    }
}
