package org.allureframework.guides;

import io.qameta.allure.Allure;
import io.qameta.allure.Feature;
import io.qameta.allure.junit4.DisplayName;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;

public class StepTest {

    public final AllureTestCaseRule testCase = new AllureTestCaseRule();

    @Rule
    public final RuleChain chain = RuleChain
            .outerRule(testCase);

    @Test
    @Feature("Hello")
    @DisplayName("Hello")
    public void multiCase2Test() {
        testCase.create("110", (result) -> {
            result.setName(" Hello 1 ");
            Allure.label("test", "first");
            Allure.step("Go to the next place");
            Allure.step("First step");
        });

        testCase.create("111", (result) -> {
            result.setName(" Hello 2 ");
            Allure.label("test", "second");
            Allure.step("Go to the next place");
            Allure.step("Second step");
        });

        testCase.create("112", (result) -> {
            result.setName(" Hello 3 ");
            Allure.label("test", "third");
            Allure.step("Go to the next place");
            Allure.step("Third step");
        });
    }

}
