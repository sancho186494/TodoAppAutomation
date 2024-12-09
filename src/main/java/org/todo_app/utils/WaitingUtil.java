package org.todo_app.utils;

import org.hamcrest.Matcher;

import java.time.Duration;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class WaitingUtil {

    private static final Duration TIMEOUT = Duration.ofSeconds(10);
    private static final Duration POLL_INTERVAL = Duration.ofMillis(500);

    public static <T> void waitForCondition(TreeConsumer<String, T, Matcher<? super T>> assertFunction,
                                            String reason, Supplier<T> actual, Matcher<? super T> matcher) {
        long startTime = System.currentTimeMillis();
        T actualValue = null;
        while (Duration.ofMillis(System.currentTimeMillis() - startTime).compareTo(TIMEOUT) < 0) {
            actualValue = actual.get();
            if (actualValue instanceof Optional<?>) {
                actualValue = (T) ((Optional<?>) actualValue).get();
            }
            try {
                assertFunction.accept(reason, actualValue, matcher);
                return;
            } catch (AssertionError ignore) {}
            sleep();
        }
        assertFunction.accept(reason, actualValue, matcher);
    }

    public static void waitForCondition(BiConsumer<String, Boolean> assertFunction, String reason, Predicate assertion) {
        long startTime = System.currentTimeMillis();
        while (Duration.ofMillis(System.currentTimeMillis() - startTime).compareTo(TIMEOUT) < 0) {
            try {
                assertFunction.accept(reason, assertion.test());
                return;
            } catch (AssertionError ignore) {}
            sleep();
        }
        assertFunction.accept(reason, assertion.test());
    }

    public static void checkConditionDuringPeriod(BiConsumer<String, Boolean> assertFunction, String reason, Predicate assertion) {
        long startTime = System.currentTimeMillis();
        while (Duration.ofMillis(System.currentTimeMillis() - startTime).compareTo(TIMEOUT) < 0) {
            assertFunction.accept(reason, assertion.test());
            sleep();
        }
    }

    private static void sleep() {
        try {
            Thread.sleep(POLL_INTERVAL.toMillis());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Waiting interrupted", e);
        }
    }
}
