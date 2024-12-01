package org.todo_app.utils;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class AssertionsUtil {

    public static void checkResponseCode(int actualCode, int expectedCode) {
        assertThat("Response code doesn't match", actualCode, equalTo(expectedCode));
    }
}
