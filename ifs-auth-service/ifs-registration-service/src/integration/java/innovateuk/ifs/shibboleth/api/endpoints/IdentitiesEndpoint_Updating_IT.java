package org.innovateuk.ifs.shibboleth.api.endpoints;

import com.jayway.restassured.http.ContentType;
import org.innovateuk.ifs.shibboleth.api.integrations.IntegratedIdentitiesEndpoint;
import org.innovateuk.ifs.shibboleth.api.models.ChangeEmail;
import org.innovateuk.ifs.shibboleth.api.models.ChangePassword;
import org.junit.Test;
import org.springframework.http.HttpStatus;

import java.io.IOException;

import static org.hamcrest.Matchers.*;

public class IdentitiesEndpoint_Updating_IT extends IntegratedIdentitiesEndpoint {

    @Test // 200
    public void onUpdateEmailAndItWorks() throws IOException {

        withValidAuthentication().and().

            contentType(ContentType.JSON).
            body(convertToJson(new ChangeEmail(emailForUpdating))).

            put(getBaseUrl() + "/identities/" + uuidForUpdatingEmail + "/email").

            then().assertThat().

            statusCode(is(equalTo(HttpStatus.OK.value()))).

            body(isEmptyOrNullString());
    }


    @Test // 400
    public void onUpdateEmailWithInvalidData() throws IOException {

        // Blank Email
        withValidAuthentication().and().

            contentType(ContentType.JSON).
            body(convertToJson(new ChangeEmail(blank))).

            put(getBaseUrl() + "/identities/" + uuidForUpdatingEmail + "/email").

            then().assertThat().

            statusCode(is(equalTo(HttpStatus.BAD_REQUEST.value()))).

            body("[0].key", is(not(nullValue()))).
            body("[0].key", is(equalTo("EMAIL_MUST_BE_VALID"))).
            body("[0].arguments", is(empty()));

        // Invalid Email
        withValidAuthentication().and().

            contentType(ContentType.JSON).
            body(convertToJson(new ChangeEmail(invalidEmailForUpdate))).

            put(getBaseUrl() + "/identities/" + uuidForUpdatingEmail + "/email").

            then().assertThat().

            statusCode(is(equalTo(HttpStatus.BAD_REQUEST.value()))).

            body("[0].key", is(not(nullValue()))).
            body("[0].key", is(equalTo("EMAIL_MUST_BE_VALID"))).
            body("[0].arguments", is(empty()));
    }


    @Test // 401
    public void onUpdateEmailWithInvalidAuthentication() throws IOException {

        withNoAuthentication().and().

            put(getBaseUrl() + "/identities/" + uuidForUpdatingEmail + "/email").

            then().assertThat().

            statusCode(is(equalTo(HttpStatus.UNAUTHORIZED.value()))).

            body(isEmptyOrNullString());

        withInvalidAuthentication().and().

            put(getBaseUrl() + "/identities/" + uuidForUpdatingEmail + "/email").

            then().assertThat().

            statusCode(is(equalTo(HttpStatus.UNAUTHORIZED.value()))).

            body(isEmptyOrNullString());
    }


    @Test // 404
    public void onUpdateEmailWithNonExistentIdentity() throws IOException {

        withValidAuthentication().and().

            contentType(ContentType.JSON).
            body(convertToJson(new ChangeEmail(duplicateEmailForUpdate))).

            put(getBaseUrl() + "/identities/" + nonExistentIdentityUuid + "/email").

            then().assertThat().

            statusCode(is(equalTo(HttpStatus.NOT_FOUND.value()))).

            body(isEmptyOrNullString());
    }


    @Test // 409
    public void onUpdateEmailWithDuplicateEmail() throws IOException {

        withValidAuthentication().and().

            contentType(ContentType.JSON).
            body(convertToJson(new ChangeEmail(duplicateEmailForUpdate))).

            put(getBaseUrl() + "/identities/" + uuidForUpdatingEmail + "/email").

            then().assertThat().

            statusCode(is(equalTo(HttpStatus.CONFLICT.value()))).
            body("[0].key", is(equalTo("DUPLICATE_EMAIL_ADDRESS"))).
            body("[0].arguments", is(empty()));
    }


    @Test // 422
    public void onUpdateEmailWithInvalidJson() throws IOException {

        withValidAuthentication().and().

            contentType(ContentType.JSON).
            body("{email:}").

            put(getBaseUrl() + "/identities/" + uuidForUpdatingEmail + "/email").

            then().assertThat().

            statusCode(is(equalTo(HttpStatus.UNPROCESSABLE_ENTITY.value()))).

            body("message", is(not(nullValue()))).
            body("stacktrace", is(not(nullValue()))).

            body("message", containsString("was expecting double-quote to start field name")).
            body("stacktrace", containsString("HttpMessageNotReadableException"));
    }


    @Test // 200
    public void onUpdatePasswordAndItWorks() throws IOException {

        withValidAuthentication().and().

            contentType(ContentType.JSON).
            body(convertToJson(new ChangePassword(passwordForUpdate))).

            put(getBaseUrl() + "/identities/" + uuidForUpdatingPassword + "/password").

            then().assertThat().

            statusCode(is(equalTo(HttpStatus.OK.value()))).

            body(isEmptyOrNullString());
    }


    @Test // 400
    public void onUpdatePasswordWithBlankPassword() throws IOException {

        // Blank Password
        withValidAuthentication().and().

                contentType(ContentType.JSON).
                body(convertToJson(new ChangePassword(blank))).

                put(getBaseUrl() + "/identities/" + uuidForUpdatingPassword + "/password").

                then().assertThat().

                statusCode(is(equalTo(HttpStatus.BAD_REQUEST.value()))).

                body("[0].key", is(not(nullValue()))).
                body("[0].key", anyOf(
                        is(equalTo("PASSWORD_MUST_CONTAIN_AT_LEAST_ONE_LOWER_CASE_LETTER")),
                        is(equalTo("PASSWORD_MUST_CONTAIN_AT_LEAST_ONE_UPPER_CASE_LETTER")),
                        is(equalTo("PASSWORD_MUST_CONTAIN_AT_LEAST_ONE_NUMBER")),
                        is(equalTo("PASSWORD_CANNOT_BE_SO_SHORT")),
                        is(equalTo("PASSWORD_MUST_NOT_BE_BLANK")))).
                body("[0].arguments", is(empty()));
    }

    @Test // 400
    public void onUpdatePasswordWithTooLongPassword() throws IOException {

        // Blank Password
        withValidAuthentication().and().

                contentType(ContentType.JSON).
                body(convertToJson(new ChangePassword("sdfjdkhg*&^&*89768JHGJHGJHGaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"))).

                put(getBaseUrl() + "/identities/" + uuidForUpdatingPassword + "/password").

                then().assertThat().

                statusCode(is(equalTo(HttpStatus.BAD_REQUEST.value()))).

                body("[0].key", is(not(nullValue()))).
                body("[0].key", is(equalTo("PASSWORD_CANNOT_BE_SO_LONG"))).
                body("[0].arguments", is(empty()));
    }

    @Test // 401
    public void onUpdatePasswordWithInvalidAuthentication() throws IOException {

        withNoAuthentication().and().

            put(getBaseUrl() + "/identities/" + uuidForUpdatingPassword + "/password").

            then().assertThat().

            statusCode(is(equalTo(HttpStatus.UNAUTHORIZED.value()))).

            body(isEmptyOrNullString());

        withInvalidAuthentication().and().

            put(getBaseUrl() + "/identities/" + uuidForUpdatingPassword + "/password").

            then().assertThat().

            statusCode(is(equalTo(HttpStatus.UNAUTHORIZED.value()))).

            body(isEmptyOrNullString());
    }


    @Test // 404
    public void onUpdatePasswordWithNonExistentIdentity() throws IOException {

        withValidAuthentication().and().

            contentType(ContentType.JSON).
            body(convertToJson(new ChangePassword(passwordForUpdate))).

            put(getBaseUrl() + "/identities/" + nonExistentIdentityUuid + "/password").

            then().assertThat().

            statusCode(is(equalTo(HttpStatus.NOT_FOUND.value()))).

            body(isEmptyOrNullString());
    }


    @Test // 422
    public void onUpdatePasswordWithInvalidJson() throws IOException {

        withValidAuthentication().and().

            contentType(ContentType.JSON).
            body("{password:}").

            put(getBaseUrl() + "/identities/" + uuidForUpdatingPassword + "/password").

            then().assertThat().

            statusCode(is(equalTo(HttpStatus.UNPROCESSABLE_ENTITY.value()))).

            body("message", is(not(nullValue()))).
            body("stacktrace", is(not(nullValue()))).

            body("message", containsString("was expecting double-quote to start field name")).
            body("stacktrace", containsString("HttpMessageNotReadableException"));
    }
}
