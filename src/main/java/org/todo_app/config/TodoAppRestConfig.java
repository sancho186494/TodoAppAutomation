package org.todo_app.config;

import org.aeonbits.owner.Config;

@Config.Sources("classpath:todo_app.properties")
public interface TodoAppRestConfig extends Config {

    @Key("baseUrl")
    String baseUrl();

    @Key("baseWsUrl")
    String baseWsUrl();

    @Key("login")
    String login();

    @Key("password")
    String password();
}
