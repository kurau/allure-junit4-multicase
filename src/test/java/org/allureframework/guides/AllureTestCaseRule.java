package org.allureframework.guides;

import io.qameta.allure.Allure;
import io.qameta.allure.model.Label;
import io.qameta.allure.model.Status;
import io.qameta.allure.model.StatusDetails;
import io.qameta.allure.model.TestResult;
import io.qameta.allure.util.ResultsUtils;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static io.qameta.allure.Allure.getLifecycle;

public class AllureTestCaseRule extends TestWatcher {

    private static final String ALLURE_ID_LABEL = "AS_ID";

    private static final String IGNORE_TEST_RESULT_MESSAGE = "Not selected in execution";

    private final List<TestResult> children = new ArrayList<>();

    private TestResult parent;

    protected void starting(final Description description) {
        Allure.getLifecycle().updateTestCase((update) -> this.parent = update);
    }

    protected void finished(final Description description) {
        Allure.getLifecycle().updateTestCase((update) -> {
            update.setStatus(Status.SKIPPED);
            update.setStatusDetails(new StatusDetails()
                    .setMessage(IGNORE_TEST_RESULT_MESSAGE));
        });
    }

    public void create(final String id,
                       final Consumer<TestResult> consumer) {
        final String uuid = UUID.randomUUID().toString();
        createTestResult(uuid, parent).ifPresent(testResult -> {
            getLifecycle().scheduleTestCase(uuid, testResult);
            getLifecycle().startTestCase(uuid);
            getLifecycle().updateTestCase(uuid, (result) -> {
                result.getLabels().add(new Label().setName(ALLURE_ID_LABEL).setValue(id));
                if (!children.isEmpty()) {
                    result.setSteps(children.getLast().getSteps());
                }
            });
            try {
                consumer.accept(testResult);
                getLifecycle().updateTestCase(uuid, (result) -> result.setStatus(Status.PASSED));
            } catch (Exception e) {
                getLifecycle().updateTestCase(uuid, (result) -> {
                    ResultsUtils.getStatus(e).ifPresent(result::setStatus);
                    ResultsUtils.getStatusDetails(e).ifPresent(result::setStatusDetails);
                });
            } finally {
                getLifecycle().updateTestCase(uuid, (result) -> {
                    result.setStop(System.currentTimeMillis());
                    this.children.add(result);
                });
                getLifecycle().stopTestCase(uuid);
                getLifecycle().writeTestCase(uuid);
            }
        });
    }

    private static Optional<TestResult> createTestResult(final String uuid, final TestResult parent) {
        if (Objects.nonNull(parent)) {
            final TestResult testResult = new TestResult()
                    .setUuid(uuid);
            final List<Label> labels = parent.getLabels().stream()
                    .filter(l -> !l.getName().equals(ALLURE_ID_LABEL))
                    .collect(Collectors.toList());
            testResult.setName(parent.getName());
            testResult.setDescription(parent.getDescription());
            testResult.setFullName(parent.getFullName());
            testResult.setLabels(labels);
            testResult.setLinks(parent.getLinks());
            testResult.setDescriptionHtml(parent.getDescriptionHtml());
            testResult.setParameters(parent.getParameters());
            return Optional.of(testResult);
        }
        return Optional.empty();
    }

}
