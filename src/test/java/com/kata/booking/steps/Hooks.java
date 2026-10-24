package com.kata.booking.steps;

import com.kata.booking.client.AuthClient;
import com.kata.booking.client.BookingClient;
import com.kata.booking.context.ScenarioContext;
import io.cucumber.java.After;
import io.cucumber.java.Scenario;
import lombok.RequiredArgsConstructor;

/** Cucumber hooks: remove the bookings a scenario created so the shared site stays tidy. */
@RequiredArgsConstructor
public class Hooks {

    private final ScenarioContext context;
    private final BookingClient bookingClient;
    private final AuthClient authClient;

    @After
    public void removeBookingsCreatedByScenario(Scenario scenario) {
        if (context.getCreatedBookingIds().isEmpty()) {
            return;
        }
        try {
            String token = authClient.obtainAdminToken();
            context.getCreatedBookingIds().forEach(id -> bookingClient.delete(id, token));
        } catch (RuntimeException e) {
            scenario.log("Cleanup skipped: " + e.getMessage());
        }
    }
}
