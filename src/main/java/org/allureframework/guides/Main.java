package org.allureframework.guides;

import io.qameta.allure.model.Status;
import io.qameta.allure.model.StepResult;
import io.qameta.allure.model.TestResult;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static io.qameta.allure.Allure.getLifecycle;

public class Main {

    private static List<String> steps = new ArrayList<>();
    private static TestResult testResult = new TestResult();

    public static void main(String[] args) {

        String testCase = UUID.randomUUID().toString();
        String fullName = "1 Test";
        startTestCase(testCase, fullName);
        addStep("First step");
        addStep("Second step");
        addStep("One more step");
        stopTestCase(testCase);

        String testCase2 = UUID.randomUUID().toString();
        String fullName2 = "2 Test";
        startTestCase(testCase2, fullName2);
        addStep("Shift left");
        stopTestCase(testCase2);

        String testCase3 = UUID.randomUUID().toString();
        String fullName3 = "2 Test";
        startTestCase(testCase3, fullName3);
        addStep("Go to the next place");
        addStep("Final step");
        stopTestCase(testCase3);
    }

    public static void step(String stepName) {
        String step1 = UUID.randomUUID().toString();
        getLifecycle().startStep(step1, (new StepResult()).setName(stepName));
        getLifecycle().updateStep(step1, (step) -> {
            step.setStatus(Status.PASSED);
        });
        getLifecycle().stopStep(step1);
    }

    public static void addStep(String stepName) {
        steps.add(stepName);
        step(stepName);
    }

    public static void startTestCase(String testCase, String name) {
        testResult = (new TestResult())
                .setUuid(testCase).setFullName(name).setName(name);
        getLifecycle().scheduleTestCase(testResult);
        getLifecycle().startTestCase(testCase);
    }

    public static void stopTestCase(String testCase) {
        getLifecycle().stopTestCase(testCase);
        testResult.setStatus(Status.PASSED);
        testResult.setStop(System.currentTimeMillis());
        getLifecycle().writeTestCase(testCase);
    }
}
