package com.kata.booking.runner;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

import static io.cucumber.junit.platform.engine.Constants.GLUE_PROPERTY_NAME;

/**
 * Entry point of the test suite.
 *
 * <p>Tells JUnit 5 to hand the feature files to the Cucumber engine and where to find the glue code
 * (step definitions and hooks). Reporting and tag filtering live in junit-platform.properties.
 */
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "com.kata.booking")
public class RunCucumberTest {
}
