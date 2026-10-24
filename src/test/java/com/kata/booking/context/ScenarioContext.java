package com.kata.booking.context;

import com.kata.booking.builder.BookingRequestBuilder;
import com.kata.booking.model.BookingOutcome;
import com.kata.booking.model.BookingRequest;
import com.kata.booking.model.BookingResponse;
import io.restassured.response.Response;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * State shared between step-definition classes during one scenario.
 *
 * <p>PicoContainer creates one instance per scenario and injects it through constructors,
 * so scenarios never see each other's data (safe for parallel runs, unlike static fields).
 */
@Getter
@Setter
public class ScenarioContext {

    private BookingRequestBuilder bookingBuilder = BookingRequestBuilder.aValidBooking();
    private BookingRequest lastRequest;
    private BookingOutcome lastOutcome;
    private Response lastResponse;
    private String token;
    private final List<BookingOutcome> outcomes = new ArrayList<>();
    private final List<Integer> createdBookingIds = new ArrayList<>();

    /** Remembers a create-booking attempt; created bookings are tracked for cleanup. */
    public void record(BookingRequest request, BookingOutcome outcome) {
        this.lastRequest = request;
        this.lastOutcome = outcome;
        this.lastResponse = outcome.response();
        this.outcomes.add(outcome);
        if (outcome instanceof BookingOutcome.Created created) {
            createdBookingIds.add(created.booking().bookingId());
        }
    }

    /** Details of the last created booking; fails the scenario with the server's answer otherwise. */
    public BookingResponse createdBooking() {
        if (lastOutcome instanceof BookingOutcome.Created created) {
            return created.booking();
        }
        String detail = lastOutcome == null
                ? "no booking was attempted"
                : "status " + lastOutcome.response().statusCode() + ", body " + lastOutcome.response().asString();
        throw new AssertionError("Expected the booking to be created but " + detail);
    }

    public int lastCreatedBookingId() {
        if (createdBookingIds.isEmpty()) {
            throw new AssertionError("No booking has been created in this scenario");
        }
        return createdBookingIds.get(createdBookingIds.size() - 1);
    }
}
