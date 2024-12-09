package org.todo_app.functions;

@FunctionalInterface
public interface TreeConsumer<T, U, Z> {

    void accept(T t, U u, Z z);
}
