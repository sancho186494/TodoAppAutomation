package org.todo_app.utils;

import io.qameta.allure.Step;

import static org.hamcrest.Matchers.equalTo;
import static org.todo_app.utils.AllureMatcherAssert.assertThat;

public class AssertionsUtil {

    @Step("Check response code equals '{1}'")
    public static void checkResponseCode(int actualCode, int expectedCode) {
        assertThat("Response code doesn't match", actualCode, equalTo(expectedCode));
    }
}
