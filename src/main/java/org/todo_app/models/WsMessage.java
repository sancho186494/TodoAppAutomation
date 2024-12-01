package org.todo_app.models;

import com.google.gson.annotations.SerializedName;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class WsMessage {

    @SerializedName("data")
    private TodoTask data;
    @SerializedName("type")
    private String type;
}
