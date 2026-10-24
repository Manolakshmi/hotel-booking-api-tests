package com.kata.booking.client;

import com.kata.booking.config.ApiConfig;
import com.kata.booking.model.Credentials;
import io.restassured.response.Response;
import lombok.RequiredArgsConstructor;

import static io.restassured.RestAssured.given;

/** Service object for the authentication endpoint. */
@RequiredArgsConstructor
public class AuthClient {

    private static final String LOGIN_PATH = "/auth/login";

    private final ApiConfig config;

    public Response login(String username, String password) {
        return given().spec(RequestSpecFactory.from(config))
                .body(new Credentials(username, password))
                .post(LOGIN_PATH);
    }

    public Response loginWithoutBody() {
        return given().spec(RequestSpecFactory.from(config)).post(LOGIN_PATH);
    }

    /** Logs in with the configured admin account and returns the session token. */
    public String obtainAdminToken() {
        Response response = login(config.getUsername(), config.getPassword());
        String token = response.jsonPath().getString("token");
        if (token == null || token.isBlank()) {
            throw new IllegalStateException("Login did not return a token. Status: " + response.statusCode());
        }
        return token;
    }
}
