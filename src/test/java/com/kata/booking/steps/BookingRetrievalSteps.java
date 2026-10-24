package com.kata.booking.steps;

import com.kata.booking.client.BookingClient;
import com.kata.booking.context.ScenarioContext;
import com.kata.booking.model.BookingRequest;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import lombok.RequiredArgsConstructor;

import static org.assertj.core.api.Assertions.assertThat;

/** Steps for looking up an existing booking. */
@RequiredArgsConstructor
public class BookingRetrievalSteps {

    private final BookingClient bookingClient;
    private final ScenarioContext context;

    @When("the administrator retrieves the booking")
    public void theAdministratorRetrievesTheBooking() {
        retrieve(String.valueOf(context.lastCreatedBookingId()), context.getToken());
    }

    @When("the administrator retrieves booking {string}")
    public void theAdministratorRetrievesBooking(String id) {
        retrieve(id, context.getToken());
    }

    @When("the booking is retrieved without signing in")
    public void theBookingIsRetrievedWithoutSigningIn() {
        retrieve(String.valueOf(context.lastCreatedBookingId()), null);
    }

    @Then("the booking details are returned")
    public void theBookingDetailsAreReturned() {
        Response response = context.getLastResponse();
        BookingRequest sent = context.getLastRequest();
        assertThat(response.statusCode()).as("response body: %s", response.asString()).isEqualTo(200);
        assertThat(response.jsonPath().getString("firstname")).isEqualTo(sent.firstName());
        assertThat(response.jsonPath().getString("lastname")).isEqualTo(sent.lastName());
        assertThat(response.jsonPath().getString("email")).isEqualTo(sent.email());
        assertThat(response.jsonPath().getString("phone")).isEqualTo(sent.phone());
    }

    private void retrieve(String id, String token) {
        context.setLastResponse(bookingClient.getById(id, token));
    }
}
