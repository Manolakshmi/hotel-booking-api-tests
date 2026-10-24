package com.kata.booking.builder;

import com.kata.booking.model.BookingDates;

import java.time.LocalDate;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Generates stay dates for tests.
 *
 * <p>The test site is shared and keeps bookings between runs, so fixed dates would clash.
 * A random check-in far in the future makes overlaps with other people's bookings very unlikely.
 */
public final class StayDates {

    private static final int EARLIEST_DAYS_AHEAD = 120;
    private static final int LATEST_DAYS_AHEAD = 3000;

    private StayDates() {
    }

    public static BookingDates randomStay(int nights) {
        int daysAhead = ThreadLocalRandom.current().nextInt(EARLIEST_DAYS_AHEAD, LATEST_DAYS_AHEAD);
        LocalDate checkIn = LocalDate.now().plusDays(daysAhead);
        return new BookingDates(checkIn.toString(), checkIn.plusDays(nights).toString());
    }

    public static BookingDates checkOutBeforeCheckIn() {
        BookingDates stay = randomStay(2);
        return new BookingDates(stay.checkOut(), stay.checkIn());
    }

    public static BookingDates inThePast() {
        LocalDate checkIn = LocalDate.now().minusDays(30);
        return new BookingDates(checkIn.toString(), checkIn.plusDays(2).toString());
    }
}
