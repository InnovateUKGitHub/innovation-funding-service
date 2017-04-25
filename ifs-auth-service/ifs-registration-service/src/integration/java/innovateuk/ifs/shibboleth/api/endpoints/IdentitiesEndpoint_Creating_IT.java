package org.innovateuk.ifs.shibboleth.api.endpoints;

import com.jayway.restassured.http.ContentType;
import org.innovateuk.ifs.shibboleth.api.integrations.IntegratedIdentitiesEndpoint;
import org.innovateuk.ifs.shibboleth.api.models.NewIdentity;
import org.junit.Test;
import org.springframework.http.HttpStatus;

import java.io.IOException;

import static org.hamcrest.Matchers.*;

public class IdentitiesEndpoint_Creating_IT extends IntegratedIdentitiesEndpoint {

    @Test // 201
    public void onCreationAndItWorks() throws IOException {

        withValidAuthentication().and().

            contentType(ContentType.JSON).
            body(convertToJson(new NewIdentity(emailForCreate, passwordForCreate))).

            post(getBaseUrl() + "/identities").

            then().assertThat().

            statusCode(is(equalTo(HttpStatus.CREATED.value()))).

            body("uuid", is(not(nullValue()))).
            body("created", is(not(nullValue()))).
            body("modified", is(nullValue())).
            body("email", is(equalTo(emailForCreate)));
    }


    @Test // 400
    public void onCreationWithInvalidData() throws IOException {

        // Blank Password
        withValidAuthentication().and().

            contentType(ContentType.JSON).
            body(convertToJson(new NewIdentity(emailForCreate, blank))).

            post(getBaseUrl() + "/identities").

            then().assertThat().

            statusCode(is(equalTo(HttpStatus.BAD_REQUEST.value()))).

            body("[0].key", anyOf(
                    is(equalTo("PASSWORD_MUST_CONTAIN_AT_LEAST_ONE_LOWER_CASE_LETTER")),
                    is(equalTo("PASSWORD_MUST_CONTAIN_AT_LEAST_ONE_UPPER_CASE_LETTER")),
                    is(equalTo("PASSWORD_MUST_CONTAIN_AT_LEAST_ONE_NUMBER")),
                    is(equalTo("PASSWORD_CANNOT_BE_SO_SHORT")),
                    is(equalTo("PASSWORD_MUST_NOT_BE_BLANK")))
            ).
            body("[0].arguments", is(empty()));

        // Blacklisted Password
        withValidAuthentication().and().

            contentType(ContentType.JSON).
            body(convertToJson(new NewIdentity(emailForCreate2nd, blacklistedPasswordForCreate))).

            post(getBaseUrl() + "/identities").

            then().assertThat().

            statusCode(is(equalTo(HttpStatus.BAD_REQUEST.value()))).

            body("[0].key", is(equalTo("INVALID_PASSWORD"))).
            body("[0].arguments", hasItem(equalTo("blacklisted")));

        // Blank Email
        withValidAuthentication().and().

            contentType(ContentType.JSON).
            body(convertToJson(new NewIdentity(blank, passwordForCreate))).

            post(getBaseUrl() + "/identities").

            then().assertThat().

            statusCode(is(equalTo(HttpStatus.BAD_REQUEST.value()))).

            body("[0].key", is(equalTo("EMAIL_MUST_BE_VALID"))).
            body("[0].arguments", is(empty()));

        // Invalid Email
        withValidAuthentication().and().

            contentType(ContentType.JSON).
            body(convertToJson(new NewIdentity(invalidEmailForCreate, passwordForCreate))).

            post(getBaseUrl() + "/identities").

            then().assertThat().

            statusCode(is(equalTo(HttpStatus.BAD_REQUEST.value()))).

            body("[0].key", is(equalTo("EMAIL_MUST_BE_VALID"))).
            body("[0].arguments", is(empty()));
    }


    @Test // 401
    public void onCreationWithInvalidAuthentication() throws IOException {

        withNoAuthentication().and().

            contentType(ContentType.JSON).
            body(convertToJson(new NewIdentity(emailForCreate, passwordForCreate))).

            post(getBaseUrl() + "/identities").

            then().assertThat().

            statusCode(is(equalTo(HttpStatus.UNAUTHORIZED.value()))).

            body(isEmptyOrNullString());

        withInvalidAuthentication().and().

            contentType(ContentType.JSON).
            body(convertToJson(new NewIdentity(emailForCreate, passwordForCreate))).

            post(getBaseUrl() + "/identities").

            then().assertThat().

            statusCode(is(equalTo(HttpStatus.UNAUTHORIZED.value()))).

            body(isEmptyOrNullString());
    }


    @Test // 409
    public void onCreationDuplicateEmail() throws IOException {

        withValidAuthentication().and().

            contentType(ContentType.JSON).
            body(convertToJson(new NewIdentity(duplicateEmailForCreate, passwordForCreate))).

            post(getBaseUrl() + "/identities").

            then().assertThat().

            statusCode(is(equalTo(HttpStatus.CONFLICT.value()))).
            body("[0].key", is(equalTo("DUPLICATE_EMAIL_ADDRESS"))).
            body("[0].arguments", is(empty()));
    }


    @Test // 422
    public void onCreationWithInvalidJson() throws IOException {

        withValidAuthentication().and().

            contentType(ContentType.JSON).
            body("{email:test@test.com}").

            post(getBaseUrl() + "/identities").

            then().assertThat().

            statusCode(is(equalTo(HttpStatus.UNPROCESSABLE_ENTITY.value()))).

            body("message", is(not(nullValue()))).
            body("stacktrace", is(not(nullValue()))).

            body("message", containsString("was expecting double-quote to start field name")).
            body("stacktrace", containsString("HttpMessageNotReadableException"));
    }

}
