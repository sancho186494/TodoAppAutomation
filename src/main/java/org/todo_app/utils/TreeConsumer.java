package org.todo_app.utils;

@FunctionalInterface
public interface TreeConsumer<T, U, Z> {

    void accept(T t, U u, Z z);
}
