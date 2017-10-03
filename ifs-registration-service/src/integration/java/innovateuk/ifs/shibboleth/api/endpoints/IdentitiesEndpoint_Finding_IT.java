package org.innovateuk.ifs.shibboleth.api.endpoints;

import org.innovateuk.ifs.shibboleth.api.integrations.IntegratedIdentitiesEndpoint;
import org.junit.Test;
import org.springframework.http.HttpStatus;

import java.io.IOException;

import static org.hamcrest.Matchers.*;

public class IdentitiesEndpoint_Finding_IT extends IntegratedIdentitiesEndpoint {

    @Test // 200
    public void onFindingExistingIdentity() throws IOException {

        withValidAuthentication().
            get(getBaseUrl() + "/identities/" + uuidForFinding).

            then().assertThat().

            statusCode(is(equalTo(HttpStatus.OK.value()))).

            body("uuid", is(equalTo(uuidForFinding))).
            body("created", is(not(nullValue()))).
            body("modified", is(nullValue())).
            body("email", is(equalTo(emailForFinding)));
    }


    @Test // 401
    public void onFindWithInvalidAuthentication() throws IOException {

        withNoAuthentication().and().

            get(getBaseUrl() + "/identities/" + uuidForFinding).

            then().assertThat().

            statusCode(is(equalTo(HttpStatus.UNAUTHORIZED.value()))).

            body(isEmptyOrNullString());

        withInvalidAuthentication().and().

            get(getBaseUrl() + "/identities/" + uuidForFinding).

            then().assertThat().

            statusCode(is(equalTo(HttpStatus.UNAUTHORIZED.value()))).

            body(isEmptyOrNullString());
    }


    @Test // 404
    public void onFindingNonExistentIdentity() throws IOException {

        withValidAuthentication().
            get(getBaseUrl() + "/identities/" + nonExistentIdentityUuid).

            then().assertThat().

            statusCode(is(equalTo(HttpStatus.NOT_FOUND.value()))).

            body(isEmptyOrNullString());
    }


    @Test // 404
    public void onFindingWithInvalidUuid() throws IOException {

        withValidAuthentication().
            get(getBaseUrl() + "/identities/z").

            then().assertThat().

            statusCode(is(equalTo(HttpStatus.BAD_REQUEST.value()))).

            body("[0].key", is(equalTo("INVALID_PARAMETER_TYPE"))).
            body("[0].arguments[0]", is(equalTo("uuid"))).
            body("[0].arguments[1]", is(equalTo("uuid"))).
            body("[0].arguments[2]", is(equalTo("UUID"))).
            body("[0].arguments[3]", is(equalTo("z")));
    }
}
