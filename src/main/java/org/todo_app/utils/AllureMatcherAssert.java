package org.todo_app.utils;

import io.qameta.allure.Allure;
import org.hamcrest.Matcher;
import org.hamcrest.MatcherAssert;
import org.todo_app.utils.functions.Predicate;

import java.util.function.Supplier;

import static org.todo_app.utils.WaitingUtil.checkConditionDuringPeriod;
import static org.todo_app.utils.WaitingUtil.waitForCondition;

public class AllureMatcherAssert {

    public static <T> void assertThat(T actual, Matcher<? super T> matcher) {
        assertThat("", actual, matcher);
    }

    public static <T> void assertThat(String reason, T actual, Matcher<? super T> matcher) {
        Allure.step(formatForAllure(reason), () -> MatcherAssert.assertThat(reason, actual, matcher));
    }

    public static void assertThat(String reason, boolean assertion) {
        Allure.step(formatForAllure(reason), () -> MatcherAssert.assertThat(reason, assertion));
    }

    public static void assertThatWithWait(String reason, Predicate assertion) {
        Allure.step(formatForAllure(reason), () -> waitForCondition(MatcherAssert::assertThat, reason, assertion));
    }

    public static <T> void assertThatWithWait(String reason, Supplier<T> actual, Matcher<? super T> matcher) {
        Allure.step(formatForAllure(reason), () -> waitForCondition(MatcherAssert::assertThat, reason, actual, matcher));
    }

    public static void assertThatDuringPeriod(String reason, Predicate assertion) {
        Allure.step(formatForAllure(reason),
                () -> checkConditionDuringPeriod(MatcherAssert::assertThat, reason, assertion));
    }

    private static String formatForAllure(String message) {
        final String PLACEHOLDER_IS = "<IS>";
        final String PLACEHOLDER_ISNT = "<ISN'T>";
        final String PLACEHOLDER_FOUND = "<FOUND>";
        final String PLACEHOLDER_NOT_FOUND = "<NOT_FOUND>";

        message = message
                .replace(" doesn't ", " ")
                .replace(" wasn't ", " ")
                .replace(" was ", PLACEHOLDER_ISNT)
                .replace(" is ", PLACEHOLDER_ISNT)
                .replace(" isn't ", PLACEHOLDER_IS)
                .replace("Not found", PLACEHOLDER_FOUND)
                .replace("Found", PLACEHOLDER_NOT_FOUND);

        message = message
                .replace(PLACEHOLDER_IS, " is ")
                .replace(PLACEHOLDER_ISNT, " isn't ")
                .replace(PLACEHOLDER_FOUND, "Found")
                .replace(PLACEHOLDER_NOT_FOUND, "Not found");

        return message;
    }
}
