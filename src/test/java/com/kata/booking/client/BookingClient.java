package com.kata.booking.client;

import com.kata.booking.config.ApiConfig;
import com.kata.booking.model.BookingOutcome;
import com.kata.booking.model.BookingRequest;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import lombok.RequiredArgsConstructor;

import java.util.Map;

import static io.restassured.RestAssured.given;

/**
 * Service object for the booking endpoint.
 *
 * <p>All HTTP details (paths, cookies, serialization) live here, so step definitions only
 * describe business behaviour. Methods that need authentication take the token as a parameter;
 * pass {@code null} to call the endpoint without signing in.
 */
@RequiredArgsConstructor
public class BookingClient {

    private static final String BOOKING_PATH = "/booking";
    private static final String BOOKING_BY_ID_PATH = "/booking/{id}";
    private static final String HEALTH_PATH = "/booking/actuator/health";

    private final ApiConfig config;

    public BookingOutcome create(BookingRequest request) {
        return BookingOutcome.from(request(null).body(request).post(BOOKING_PATH));
    }

    /** Sends a hand-written JSON body, used for invalid and malformed payload tests. */
    public Response createRaw(String json) {
        return request(null).body(json).post(BOOKING_PATH);
    }

    public Response getById(String id, String token) {
        return request(token).get(BOOKING_BY_ID_PATH, id);
    }

    public Response replace(int id, BookingRequest request, String token) {
        return request(token).body(request).put(BOOKING_BY_ID_PATH, id);
    }

    public Response patch(int id, Map<String, Object> fields, String token) {
        return request(token).body(fields).patch(BOOKING_BY_ID_PATH, id);
    }

    public Response delete(int id, String token) {
        return request(token).delete(BOOKING_BY_ID_PATH, id);
    }

    public Response health() {
        return request(null).get(HEALTH_PATH);
    }

    private RequestSpecification request(String token) {
        RequestSpecification spec = given().spec(RequestSpecFactory.from(config));
        return token == null ? spec : spec.cookie("token", token);
    }
}
