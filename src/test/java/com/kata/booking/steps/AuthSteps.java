package com.kata.booking.steps;

import com.kata.booking.client.AuthClient;
import com.kata.booking.context.ScenarioContext;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import lombok.RequiredArgsConstructor;

import static org.assertj.core.api.Assertions.assertThat;

/** Steps for signing in as hotel staff. */
@RequiredArgsConstructor
public class AuthSteps {

    private final AuthClient authClient;
    private final ScenarioContext context;

    @Given("the administrator is signed in")
    public void administratorIsSignedIn() {
        context.setToken(authClient.obtainAdminToken());
    }

    @When("the administrator signs in with username {string} and password {string}")
    public void administratorSignsIn(String username, String password) {
        context.setLastResponse(authClient.login(username, password));
    }

    @When("the administrator sends a sign-in request without credentials")
    public void administratorSignsInWithoutCredentials() {
        context.setLastResponse(authClient.loginWithoutBody());
    }

    @Then("the sign-in is accepted")
    public void signInIsAccepted() {
        assertThat(context.getLastResponse().statusCode()).isEqualTo(200);
    }

    @Then("a session token is issued")
    public void sessionTokenIsIssued() {
        assertThat(context.getLastResponse().jsonPath().getString("token")).isNotBlank();
    }
}
