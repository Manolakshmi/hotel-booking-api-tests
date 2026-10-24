package com.kata.booking.config;

import lombok.Getter;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.Properties;

/**
 * Central place for environment settings.
 *
 * <p>Values come from config.properties and can be overridden with JVM system properties.
 * It is a plain class with a no-argument constructor so PicoContainer can create it.
 */
@Getter
public class ApiConfig {

    private static final String CONFIG_FILE = "config.properties";

    private final String baseUrl;
    private final String username;
    private final String password;
    private final boolean logging;

    public ApiConfig() {
        Properties properties = loadProperties();
        this.baseUrl = resolve(properties, "api.baseUrl");
        this.username = resolve(properties, "api.username");
        this.password = resolve(properties, "api.password");
        this.logging = Boolean.parseBoolean(resolve(properties, "api.logging"));
    }

    private static Properties loadProperties() {
        try (InputStream input = ApiConfig.class.getClassLoader().getResourceAsStream(CONFIG_FILE)) {
            if (input == null) {
                throw new IllegalStateException(CONFIG_FILE + " not found on the classpath");
            }
            Properties properties = new Properties();
            properties.load(input);
            return properties;
        } catch (IOException e) {
            throw new UncheckedIOException("Could not read " + CONFIG_FILE, e);
        }
    }

    private static String resolve(Properties properties, String key) {
        return System.getProperty(key, properties.getProperty(key));
    }
}
