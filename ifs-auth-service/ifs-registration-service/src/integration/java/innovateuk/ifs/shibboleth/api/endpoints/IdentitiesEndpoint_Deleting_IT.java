package org.innovateuk.ifs.shibboleth.api.endpoints;

import org.innovateuk.ifs.shibboleth.api.integrations.IntegratedIdentitiesEndpoint;
import org.junit.Test;
import org.springframework.http.HttpStatus;

import java.io.IOException;

import static org.hamcrest.Matchers.*;

public class IdentitiesEndpoint_Deleting_IT extends IntegratedIdentitiesEndpoint {

    @Test // 200
    public void onDeletingExistingIdentity() throws IOException {

        withValidAuthentication().
            delete(getBaseUrl() + "/identities/" + uuidForDelete).

            then().assertThat().

            statusCode(is(equalTo(HttpStatus.OK.value()))).

            body(isEmptyOrNullString());
    }


    @Test // 401
    public void onDeletingWithInvalidAuthentication() {

        withNoAuthentication().
            delete(getBaseUrl() + "/identities/" + nonExistentIdentityUuid).

            then().assertThat().

            statusCode(is(equalTo(HttpStatus.UNAUTHORIZED.value()))).

            body(isEmptyOrNullString());

        withInvalidAuthentication().
            delete(getBaseUrl() + "/identities/" + nonExistentIdentityUuid).

            then().assertThat().

            statusCode(is(equalTo(HttpStatus.UNAUTHORIZED.value()))).

            body(isEmptyOrNullString());
    }


    @Test // 404
    public void onDeletingNonExistentIdentity() throws IOException {

        withValidAuthentication().
            delete(getBaseUrl() + "/identities/" + nonExistentIdentityUuid).

            then().assertThat().

            statusCode(is(equalTo(HttpStatus.NOT_FOUND.value()))).

            body(isEmptyOrNullString());
    }

}
