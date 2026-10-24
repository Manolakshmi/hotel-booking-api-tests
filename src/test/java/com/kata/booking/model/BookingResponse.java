package com.kata.booking.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Body returned when a booking is created.
 *
 * <p>Observed on the live API: the fields are flat (no nested "booking" object) and email/phone
 * are not echoed back. See BUG_REPORT.md (BUG-03).
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record BookingResponse(
        @JsonProperty("bookingid") int bookingId,
        @JsonProperty("roomid") int roomId,
        @JsonProperty("firstname") String firstName,
        @JsonProperty("lastname") String lastName,
        @JsonProperty("depositpaid") boolean depositPaid,
        @JsonProperty("bookingdates") BookingDates bookingDates) {
}
