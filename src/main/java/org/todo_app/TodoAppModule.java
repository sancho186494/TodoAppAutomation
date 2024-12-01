package org.todo_app;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import org.aeonbits.owner.ConfigFactory;
import org.todo_app.config.TodoAppRestConfig;
import org.todo_app.api.TodoAppRestService;

import static org.todo_app.RetrofitService.getDefaultRetrofitBuilder;

public class TodoAppModule extends AbstractModule {

    @Provides
    protected TodoAppRestService providesTodoAppRestService() {
        TodoAppRestConfig config = ConfigFactory.create(TodoAppRestConfig.class);
        return getDefaultRetrofitBuilder()
                .baseUrl(config.baseUrl())
                .build()
                .create(TodoAppRestService.class);
    }
}
