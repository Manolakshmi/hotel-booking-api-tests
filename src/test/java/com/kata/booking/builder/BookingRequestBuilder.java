package com.kata.booking.builder;

import com.kata.booking.model.BookingDates;
import com.kata.booking.model.BookingRequest;

/**
 * Builder for {@link BookingRequest}. Starts from a valid booking so each test only states
 * the one detail it cares about.
 */
public class BookingRequestBuilder {

    private int roomId = 1;
    private String firstName = "Mano";
    private String lastName = "Tester";
    private boolean depositPaid = true;
    private BookingDates bookingDates = StayDates.randomStay(2);
    private String email = "mano.tester@example.com";
    private String phone = "01234567890";

    public static BookingRequestBuilder aValidBooking() {
        return new BookingRequestBuilder();
    }

    public BookingRequestBuilder withRoomId(int roomId) {
        this.roomId = roomId;
        return this;
    }

    public BookingRequestBuilder withFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public BookingRequestBuilder withLastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    public BookingRequestBuilder withDepositPaid(boolean depositPaid) {
        this.depositPaid = depositPaid;
        return this;
    }

    public BookingRequestBuilder withBookingDates(BookingDates bookingDates) {
        this.bookingDates = bookingDates;
        return this;
    }

    public BookingRequestBuilder withEmail(String email) {
        this.email = email;
        return this;
    }

    public BookingRequestBuilder withPhone(String phone) {
        this.phone = phone;
        return this;
    }

    /** Overrides one text field by its API name, so Scenario Outlines can drive it from a table. */
    public BookingRequestBuilder withField(String fieldName, String value) {
        return switch (fieldName) {
            case "firstname" -> withFirstName(value);
            case "lastname" -> withLastName(value);
            case "email" -> withEmail(value);
            case "phone" -> withPhone(value);
            default -> throw new IllegalArgumentException("Unsupported booking field: " + fieldName);
        };
    }

    public BookingRequest build() {
        return new BookingRequest(roomId, firstName, lastName, depositPaid, bookingDates, email, phone);
    }
}
