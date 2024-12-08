package org.todo_app.utils;

import io.qameta.allure.listener.StepLifecycleListener;
import io.qameta.allure.model.StepResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.qameta.allure.model.Status.FAILED;

public class AllureStepLogger implements StepLifecycleListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(AllureStepLogger.class.getSimpleName());

    @Override
    public void beforeStepStart(StepResult result) {
        LOGGER.info("[ -> ] " + result.getName());
    }
    @Override
    public void afterStepStop(StepResult result) {
        if (result.getStatus() == FAILED) {
            LOGGER.error("[ X ] " + result.getName() + " | Step Broken!");
        } else {
            LOGGER.info("[ <- ] " + result.getName() + " | Step Finished!");
        }
    }
}
