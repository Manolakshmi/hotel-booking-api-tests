package com.kata.booking.steps;

import com.kata.booking.client.BookingClient;
import com.kata.booking.context.ScenarioContext;
import com.kata.booking.model.BookingOutcome;
import com.kata.booking.model.BookingRequest;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import lombok.RequiredArgsConstructor;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/** Steps for changing and cancelling bookings (authentication required). */
@RequiredArgsConstructor
public class BookingManagementSteps {

    private static final String UPDATED_FIRST_NAME = "Updated";
    private static final String UPDATED_LAST_NAME = "Guest";

    private final BookingClient bookingClient;
    private final ScenarioContext context;

    @When("the administrator replaces the booking with new guest details")
    public void theAdministratorReplacesTheBooking() {
        BookingRequest updated = context.getLastRequest().withGuest(UPDATED_FIRST_NAME, UPDATED_LAST_NAME);
        context.setLastRequest(updated);
        context.setLastResponse(
                bookingClient.replace(context.lastCreatedBookingId(), updated, context.getToken()));
    }

    @When("the administrator marks the deposit as paid")
    public void theAdministratorMarksTheDepositAsPaid() {
        context.setLastResponse(bookingClient.patch(
                context.lastCreatedBookingId(), Map.of("depositpaid", true), context.getToken()));
    }

    @When("the administrator cancels the booking")
    public void theAdministratorCancelsTheBooking() {
        context.setLastResponse(bookingClient.delete(context.lastCreatedBookingId(), context.getToken()));
    }

    @When("the booking is cancelled without signing in")
    public void theBookingIsCancelledWithoutSigningIn() {
        context.setLastResponse(bookingClient.delete(context.lastCreatedBookingId(), null));
    }

    @Then("the booking shows the new guest details")
    public void theBookingShowsTheNewGuestDetails() {
        Response response = context.getLastResponse();
        assertThat(response.statusCode()).as("response body: %s", response.asString()).isEqualTo(200);
        assertThat(response.jsonPath().getString("firstname")).isEqualTo(UPDATED_FIRST_NAME);
        assertThat(response.jsonPath().getString("lastname")).isEqualTo(UPDATED_LAST_NAME);
    }

    @Then("the deposit is recorded as paid")
    public void theDepositIsRecordedAsPaid() {
        Response response = context.getLastResponse();
        assertThat(response.statusCode()).as("response body: %s", response.asString()).isEqualTo(200);
        assertThat(response.jsonPath().getBoolean("depositpaid")).isTrue();
    }

    /** The OpenAPI spec documents 201 for a successful delete. */
    @Then("the cancellation is accepted")
    public void theCancellationIsAccepted() {
        assertThat(context.getLastResponse().statusCode()).isEqualTo(201);
    }

    @Then("the room can be booked again for the same dates")
    public void theRoomCanBeBookedAgain() {
        BookingRequest again = context.getLastRequest().withGuest("Next", "Guest");
        BookingOutcome outcome = bookingClient.create(again);
        context.record(again, outcome);
        assertThat(outcome).isInstanceOf(BookingOutcome.Created.class);
    }
}
