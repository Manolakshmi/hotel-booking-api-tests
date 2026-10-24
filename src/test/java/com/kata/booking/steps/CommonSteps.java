package com.kata.booking.steps;

import com.kata.booking.context.ScenarioContext;
import io.cucumber.java.en.Then;
import lombok.RequiredArgsConstructor;

import static org.assertj.core.api.Assertions.assertThat;

/** Assertions that apply to any response, reused by every feature. */
@RequiredArgsConstructor
public class CommonSteps {

    private final ScenarioContext context;

    @Then("the request is refused with status {int}")
    public void requestIsRefusedWithStatus(int expectedStatus) {
        assertThat(context.getLastResponse().statusCode())
                .as("status for response body: %s", context.getLastResponse().asString())
                .isEqualTo(expectedStatus);
    }

    @Then("the request is rejected as a client error")
    public void requestIsRejectedAsClientError() {
        assertThat(context.getLastResponse().statusCode())
                .as("status for response body: %s", context.getLastResponse().asString())
                .isBetween(400, 499);
    }

    @Then("the error message mentions {string}")
    public void errorMessageMentions(String expectedText) {
        assertThat(context.getLastResponse().asString()).contains(expectedText);
    }
}
