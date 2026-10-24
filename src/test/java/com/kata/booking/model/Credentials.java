package com.kata.booking.model;

/** Login payload for the authentication endpoint. */
public record Credentials(String username, String password) {
}
