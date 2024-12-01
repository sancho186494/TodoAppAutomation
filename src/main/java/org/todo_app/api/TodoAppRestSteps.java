package org.todo_app.api;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.inject.Inject;
import org.aeonbits.owner.ConfigFactory;
import org.todo_app.config.TodoAppRestConfig;
import org.todo_app.models.TodoTask;
import retrofit2.Call;
import retrofit2.Response;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Base64;
import java.util.List;
import java.util.Map;

public class TodoAppRestSteps {

    @Inject
    private TodoAppRestService service;

    private final TodoAppRestConfig CONFIG = ConfigFactory.create(TodoAppRestConfig.class);

    public Response<List<TodoTask>> getTodos() {
        return executeRequest(service.getTodos());
    }

    public Response<Void> createTodo(TodoTask todoTask) {
        return executeRequest(service.createTodo(convertToMap(todoTask)));
    }

    public Response<Void> createTodo(TodoTask todoTask, Map<String, String> headers) {
        return executeRequest(service.createTodo(headers, convertToMap(todoTask)));
    }

    public Response<Void> createTodo(Map<String, Object> todoTask) {
        return executeRequest(service.createTodo(todoTask));
    }

    public Response<Void> editTodo(TodoTask todoTask) {
        return executeRequest(service.editTodo(todoTask.getId(), convertToMap(todoTask)));
    }

    public Response<Void> editTodo(TodoTask todoTask, Map<String, String> headers) {
        return executeRequest(service.editTodo(todoTask.getId(), headers, convertToMap(todoTask)));
    }

    public Response<Void> editTodo(int id, Map<String, Object> todoTask) {
        return executeRequest(service.editTodo(id, todoTask));
    }

    public Response<Void> editTodoNoPathId(TodoTask todoTask) {
        return executeRequest(service.editTodoNoPathId(convertToMap(todoTask)));
    }

    public Response<Void> deleteTodo(TodoTask todoTask) {
        return executeRequest(service.deleteTodo(todoTask.getId(), getCredential()));
    }

    public Response<Void> deleteTodoNoAuth(TodoTask todoTask) {
        return executeRequest(service.deleteTodoNoAuth(todoTask.getId()));
    }

    public Response<Void> deleteTodo(int id) {
        return executeRequest(service.deleteTodo(id, getCredential()));
    }

    private <T> Response<T> executeRequest(Call<T> call) {
        try {
            return call.execute();
        } catch (IOException e) {
            throw new RuntimeException("Request execution failed: " + e.getMessage(), e);
        }
    }

    private Map<String, Object> convertToMap(TodoTask todoTask) {
        Gson gson = new Gson();
        String json = gson.toJson(todoTask);
        Type type = new TypeToken<Map<String, Object>>() {}.getType();
        Map<String, Object> map = gson.fromJson(json, type);
        if (map.get("id") instanceof Double) {
            map.put("id", ((Double) map.get("id")).intValue());
        }
        return map;
    }

    private String getCredential() {
        return  "Basic " + Base64.getEncoder().encodeToString((CONFIG.login() + ":" + CONFIG.password()).getBytes());
    }
}
