package com.kata.booking.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/** Check-in and check-out dates in ISO-8601 format (yyyy-MM-dd). */
public record BookingDates(
        @JsonProperty("checkin") String checkIn,
        @JsonProperty("checkout") String checkOut) {
}
