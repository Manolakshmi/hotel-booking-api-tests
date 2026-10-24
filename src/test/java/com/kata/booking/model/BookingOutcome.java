package com.kata.booking.model;

import io.restassured.response.Response;

/**
 * Result of a create-booking call: either the booking was created or the request was rejected.
 *
 * <p>A sealed type lets the compiler know these are the only two possibilities, and callers use
 * pattern matching ({@code instanceof Created created}) instead of casts.
 */
public sealed interface BookingOutcome permits BookingOutcome.Created, BookingOutcome.Rejected {

    /** The raw HTTP response, kept for status, header and timing assertions. */
    Response response();

    /** The service accepted the booking and returned its details. */
    record Created(Response response, BookingResponse booking) implements BookingOutcome {
    }

    /** The service refused the request (validation error, conflict, ...). */
    record Rejected(Response response) implements BookingOutcome {
    }

    static BookingOutcome from(Response response) {
        boolean success = response.statusCode() >= 200 && response.statusCode() < 300;
        return success ? new Created(response, response.as(BookingResponse.class)) : new Rejected(response);
    }
}
