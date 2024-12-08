package org.todo_app.api;

import org.todo_app.models.TodoTask;
import retrofit2.Call;
import retrofit2.http.*;

import java.util.List;
import java.util.Map;

public interface TodoAppRestService {

    @GET("/todos")
    Call<List<TodoTask>> getTodos();

    @GET("/todos")
    Call<List<TodoTask>> getTodos(@QueryMap Map<String, Object> queryParams);

    @POST("/todos")
    @Headers("Content-Type: application/json")
    Call<Void> createTodo(@Body Map<String, Object> body);

    @POST("/todos")
    @Headers("Content-Type: application/json")
    Call<Void> createTodo(@Body TodoTask body);

    @POST("/todos")
    Call<Void> createTodo(@HeaderMap Map<String, String> headers,
                          @Body TodoTask body);

    @PUT("/todos/{id}")
    @Headers("Content-Type: application/json")
    Call<Void> editTodo(@Path("id") int id,
                        @Body Map<String, Object> body);

    @PUT("/todos/{id}")
    @Headers("Content-Type: application/json")
    Call<Void> editTodo(@Path("id") int id,
                        @Body TodoTask body);

    @PUT("/todos/{id}")
    Call<Void> editTodo(@Path("id") int id,
                        @HeaderMap Map<String, String> headers,
                        @Body TodoTask body);

    @PUT("/todos")
    Call<Void> editTodoNoPathId(@Body TodoTask body);

    @DELETE("/todos/{id}")
    Call<Void> deleteTodo(@Path("id") int id,
                          @Header("Authorization") String credential);

    @DELETE("/todos/{id}")
    Call<Void> deleteTodoNoAuth(@Path("id") int id);
}
