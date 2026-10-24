package com.kata.booking.builder;

/** Hand-written request bodies for tests that need invalid JSON structures (Java text blocks). */
public final class RawPayloads {

    public static final String MISSING_GUEST_DETAILS = """
            {
              "roomid": 1,
              "depositpaid": true
            }
            """;

    public static final String MALFORMED_JSON = """
            { "firstname": "Mano", "lastname":
            """;

    private RawPayloads() {
    }
}
