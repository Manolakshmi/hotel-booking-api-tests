package com.kata.booking.steps;

import com.kata.booking.builder.BookingRequestBuilder;
import com.kata.booking.builder.RawPayloads;
import com.kata.booking.builder.StayDates;
import com.kata.booking.client.BookingClient;
import com.kata.booking.context.ScenarioContext;
import com.kata.booking.model.BookingOutcome;
import com.kata.booking.model.BookingRequest;
import com.kata.booking.model.BookingResponse;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import lombok.RequiredArgsConstructor;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.assertj.core.api.Assertions.assertThat;

/** Steps for booking a room, described in guest-facing language. */
@RequiredArgsConstructor
public class BookingCreateSteps {

    private final BookingClient bookingClient;
    private final ScenarioContext context;

    @Given("a guest with valid contact details")
    public void aGuestWithValidContactDetails() {
        context.setBookingBuilder(BookingRequestBuilder.aValidBooking());
    }

    @Given("a room has been booked")
    public void aRoomHasBeenBooked() {
        submit(context.getBookingBuilder().build());
        context.createdBooking();
    }

    @When("the guest books a room")
    public void theGuestBooksARoom() {
        submit(context.getBookingBuilder().build());
    }

    @When("the guest books room {int} for {int} nights")
    public void theGuestBooksRoomForNights(int roomId, int nights) {
        submit(context.getBookingBuilder()
                .withRoomId(roomId)
                .withBookingDates(StayDates.randomStay(nights))
                .build());
    }

    @When("another guest books the same room for the same dates")
    public void anotherGuestBooksTheSameRoom() {
        submit(context.getLastRequest().withGuest("Another", "Guest"));
    }

    @When("the guest books with {word} set to {string}")
    public void theGuestBooksWithFieldSetTo(String field, String value) {
        submit(context.getBookingBuilder().withField(field, value).build());
    }

    @When("the guest books a room with check-out before check-in")
    public void theGuestBooksWithCheckOutBeforeCheckIn() {
        submit(context.getBookingBuilder().withBookingDates(StayDates.checkOutBeforeCheckIn()).build());
    }

    @When("the guest books a room with a check-in date in the past")
    public void theGuestBooksWithPastCheckIn() {
        submit(context.getBookingBuilder().withBookingDates(StayDates.inThePast()).build());
    }

    @When("a booking request without the required guest details is sent")
    public void aRequestWithoutGuestDetailsIsSent() {
        context.setLastResponse(bookingClient.createRaw(RawPayloads.MISSING_GUEST_DETAILS));
    }

    @When("a booking request with malformed JSON is sent")
    public void aRequestWithMalformedJsonIsSent() {
        context.setLastResponse(bookingClient.createRaw(RawPayloads.MALFORMED_JSON));
    }

    @When("the following guests book a room")
    public void theFollowingGuestsBookARoom(DataTable guests) {
        guests.asMaps().forEach(guest -> submit(context.getBookingBuilder()
                .withFirstName(guest.get("firstname"))
                .withLastName(guest.get("lastname"))
                .withDepositPaid(Boolean.parseBoolean(guest.get("depositpaid")))
                .withBookingDates(StayDates.randomStay(2))
                .build()));
    }

    @Then("the booking is confirmed")
    public void theBookingIsConfirmed() {
        assertThat(context.getLastResponse().statusCode())
                .as("status for response body: %s", context.getLastResponse().asString())
                .isEqualTo(201);
        assertThat(context.createdBooking().bookingId()).isPositive();
    }

    @Then("the confirmation matches the guest and stay details")
    public void theConfirmationMatchesTheRequest() {
        BookingRequest sent = context.getLastRequest();
        BookingResponse confirmed = context.createdBooking();
        assertThat(confirmed.roomId()).isEqualTo(sent.roomId());
        assertThat(confirmed.firstName()).isEqualTo(sent.firstName());
        assertThat(confirmed.lastName()).isEqualTo(sent.lastName());
        assertThat(confirmed.depositPaid()).isEqualTo(sent.depositPaid());
        assertThat(confirmed.bookingDates()).isEqualTo(sent.bookingDates());
    }

    @Then("every booking is confirmed")
    public void everyBookingIsConfirmed() {
        assertThat(context.getOutcomes())
                .isNotEmpty()
                .allSatisfy(outcome -> assertThat(outcome).isInstanceOf(BookingOutcome.Created.class));
    }

    @Then("the confirmation follows the agreed structure")
    public void theConfirmationFollowsTheAgreedStructure() {
        context.getLastResponse().then().body(matchesJsonSchemaInClasspath("schemas/booking-created.json"));
    }

    @Then("the response arrives within {int} ms")
    public void theResponseArrivesWithin(int maxMillis) {
        assertThat(context.getLastResponse().time()).isLessThan((long) maxMillis);
    }

    private void submit(BookingRequest request) {
        context.record(request, bookingClient.create(request));
    }
}
