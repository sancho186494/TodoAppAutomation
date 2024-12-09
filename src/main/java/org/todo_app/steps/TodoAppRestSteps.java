package org.todo_app.steps;

import com.google.inject.Inject;
import io.qameta.allure.Step;
import org.aeonbits.owner.ConfigFactory;
import org.todo_app.api.TodoAppRestService;
import org.todo_app.config.TodoAppRestConfig;
import org.todo_app.models.TodoTask;
import retrofit2.Call;
import retrofit2.Response;

import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class TodoAppRestSteps {

    @Inject
    private TodoAppRestService service;

    private final TodoAppRestConfig CONFIG = ConfigFactory.create(TodoAppRestConfig.class);

    @Step("Get all todos")
    public Response<List<TodoTask>> getTodos() {
        return executeRequest(service.getTodos());
    }

    @Step("Get todos with method params '{0}'")
    public Response<List<TodoTask>> getTodos(Map<String, Object> queryParams) {
        return executeRequest(service.getTodos(queryParams));
    }

    @Step("Create todo task '{0}'")
    public synchronized Response<Void> createTodo(TodoTask todoTask) {
        return executeRequest(service.createTodo(todoTask));
    }

    @Step("Create todo task '{0}' with request headers '{1}'")
    public Response<Void> createTodo(TodoTask todoTask, Map<String, String> headers) {
        return executeRequest(service.createTodo(headers, todoTask));
    }

    @Step("Create todo task '{0}'")
    public Response<Void> createTodo(Map<String, Object> todoTask) {
        return executeRequest(service.createTodo(todoTask));
    }

    @Step("Edit todo task '{0}'")
    public Response<Void> editTodo(TodoTask todoTask) {
        return executeRequest(service.editTodo(todoTask.getId(), todoTask));
    }

    @Step("Edit todo task '{0}' with request headers '{1}'")
    public Response<Void> editTodo(TodoTask todoTask, Map<String, String> headers) {
        return executeRequest(service.editTodo(todoTask.getId(), headers, todoTask));
    }

    @Step("Edit todo task by id = '{0}' and request body '{1}'")
    public Response<Void> editTodo(int id, Map<String, Object> todoTask) {
        return executeRequest(service.editTodo(id, todoTask));
    }

    @Step("Edit todo task '{0}' without id path request")
    public Response<Void> editTodoNoPathId(TodoTask todoTask) {
        return executeRequest(service.editTodoNoPathId(todoTask));
    }

    @Step("Delete todo task '{0}'")
    public Response<Void> deleteTodo(TodoTask todoTask) {
        return executeRequest(service.deleteTodo(todoTask.getId(), getCredential()));
    }

    @Step("Delete todo task '{0}' without authentication")
    public Response<Void> deleteTodoNoAuth(TodoTask todoTask) {
        return executeRequest(service.deleteTodoNoAuth(todoTask.getId()));
    }

    @Step("Delete todo task by id = '{0}'")
    public Response<Void> deleteTodo(int id) {
        return executeRequest(service.deleteTodo(id, getCredential()));
    }

    @Step("Delete all todos")
    public void deleteAllTodos() {
        Objects.requireNonNull(getTodos().body()).forEach(this::deleteTodo);
    }

    private <T> Response<T> executeRequest(Call<T> call) {
        try {
            return call.execute();
        } catch (IOException e) {
            throw new RuntimeException("Request execution failed: " + e.getMessage(), e);
        }
    }

    private String getCredential() {
        return  "Basic " + Base64.getEncoder().encodeToString((CONFIG.login() + ":" + CONFIG.password()).getBytes());
    }
}
