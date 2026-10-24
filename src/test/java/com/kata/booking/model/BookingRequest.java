package com.kata.booking.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Payload used to create or replace a booking.
 *
 * <p>Java names are camelCase; {@link JsonProperty} maps them to the lower-case names of the API.
 */
public record BookingRequest(
        @JsonProperty("roomid") int roomId,
        @JsonProperty("firstname") String firstName,
        @JsonProperty("lastname") String lastName,
        @JsonProperty("depositpaid") boolean depositPaid,
        @JsonProperty("bookingdates") BookingDates bookingDates,
        String email,
        String phone) {

    /** Copy of this request for another guest; room, dates and contact details stay the same. */
    public BookingRequest withGuest(String newFirstName, String newLastName) {
        return new BookingRequest(roomId, newFirstName, newLastName, depositPaid, bookingDates, email, phone);
    }
}
