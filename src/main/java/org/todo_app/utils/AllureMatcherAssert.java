package org.todo_app.utils;

import io.qameta.allure.Allure;
import org.hamcrest.Matcher;
import org.hamcrest.MatcherAssert;

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

    private static String formatForAllure(String message) {
        return message
                .replaceAll(" doesn't ", " ")
                .replaceAll(" wasn't ", " ")
                .replaceAll(" was ", " wasn't ")
                .replaceAll(" is ", " isn't ")
                .replaceAll(" isn't ", " is ")
                .replaceAll("Not found", "Found")
                .replaceAll("Found", "Not found");
    }
}
