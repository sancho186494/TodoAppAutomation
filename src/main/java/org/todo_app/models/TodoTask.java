package org.todo_app.models;

import com.google.gson.annotations.SerializedName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.UUID;

import static java.lang.Math.abs;

@Data
@Accessors(chain = true)
@EqualsAndHashCode
public class TodoTask {

    @SerializedName("id")
    private int id;
    @SerializedName("text")
    private String text;
    @SerializedName("completed")
    private boolean completed;

    public TodoTask() {
        this.id = abs(UUID.randomUUID().hashCode());
    }

    public TodoTask(String text) {
        this.id = abs(UUID.randomUUID().hashCode());
        this.text = text;
    }
}
