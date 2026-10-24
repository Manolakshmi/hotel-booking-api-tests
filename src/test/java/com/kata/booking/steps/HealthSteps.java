package com.kata.booking.steps;

import com.kata.booking.client.BookingClient;
import com.kata.booking.context.ScenarioContext;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import lombok.RequiredArgsConstructor;

import static org.assertj.core.api.Assertions.assertThat;

/** Steps for the service health check. */
@RequiredArgsConstructor
public class HealthSteps {

    private final BookingClient bookingClient;
    private final ScenarioContext context;

    @When("the service health is checked")
    public void theServiceHealthIsChecked() {
        context.setLastResponse(bookingClient.health());
    }

    @Then("the service reports that it is up")
    public void theServiceReportsThatItIsUp() {
        assertThat(context.getLastResponse().statusCode()).isEqualTo(200);
        assertThat(context.getLastResponse().jsonPath().getString("status")).isEqualTo("UP");
    }
}
