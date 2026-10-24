package com.kata.booking.client;

import com.kata.booking.config.ApiConfig;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

/** Builds the Rest-Assured request specification shared by every client. */
public final class RequestSpecFactory {

    private RequestSpecFactory() {
    }

    public static RequestSpecification from(ApiConfig config) {
        RequestSpecBuilder builder = new RequestSpecBuilder()
                .setBaseUri(config.getBaseUrl())
                .setContentType(ContentType.JSON)
                .setAccept(ContentType.JSON);
        if (config.isLogging()) {
            builder.addFilter(new RequestLoggingFilter(LogDetail.ALL))
                    .addFilter(new ResponseLoggingFilter(LogDetail.ALL));
        }
        return builder.build();
    }
}
