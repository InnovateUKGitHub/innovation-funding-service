package org.innovateuk.ifs.shibboleth.api.integrations;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.restassured.specification.RequestSpecification;
import org.innovateuk.ifs.shibboleth.api.IntegrationTestApplication;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.validation.constraints.NotNull;
import java.io.IOException;

import static com.jayway.restassured.RestAssured.with;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(IntegrationTestApplication.class)
@WebIntegrationTest(randomPort = true)
@ActiveProfiles("integration")
public abstract class IntegratedIdentitiesEndpoint {

    protected String blank = " ";
    //
    // HTTP Server Values
    //
    @Value("${local.server.port}")
    @NotNull
    protected int port;
    //
    // Api Key Values
    //
    @Value("${shibboleth.test.data.api.headerName}")
    protected String apiKeyHeader;
    @Value("${shibboleth.test.data.api.validKey}")
    protected String apiKeyValidValue;
    @Value("${shibboleth.test.data.api.invalidKey}")
    protected String apiKeyInvalidValue;
    //
    // Identity Values
    //
    @Value("${shibboleth.test.data.identity.emailForCreate}")
    protected String emailForCreate;
    @Value("${shibboleth.test.data.identity.emailForCreate2nd}")
    protected String emailForCreate2nd;
    @Value("${shibboleth.test.data.identity.duplicateEmailForCreate}")
    protected String duplicateEmailForCreate;
    @Value("${shibboleth.test.data.identity.invalidEmailForCreate}")
    protected String invalidEmailForCreate;
    @Value("${shibboleth.test.data.identity.passwordForCreate}")
    protected String passwordForCreate;
    @Value("${shibboleth.test.data.identity.blacklistedPasswordForCreate}")
    protected String blacklistedPasswordForCreate;
    //
    @Value("${shibboleth.test.data.identity.uuidForDelete}")
    protected String uuidForDelete;
    //
    @Value("${shibboleth.test.data.identity.nonExistentIdentityUuid}")
    protected String nonExistentIdentityUuid;
    //
    @Value("${shibboleth.test.data.identity.emailForFinding}")
    protected String emailForFinding;
    @Value("${shibboleth.test.data.identity.uuidForFinding}")
    protected String uuidForFinding;
    //
    @Value("${shibboleth.test.data.identity.uuidForUpdatingEmail}")
    protected String uuidForUpdatingEmail;
    @Value("${shibboleth.test.data.identity.emailForUpdating}")
    protected String emailForUpdating;
    @Value("${shibboleth.test.data.identity.duplicateEmailForUpdate}")
    protected String duplicateEmailForUpdate;
    @Value("${shibboleth.test.data.identity.invalidEmailForUpdate}")
    protected String invalidEmailForUpdate;
    //
    @Value("${shibboleth.test.data.identity.uuidForUpdatingPassword}")
    protected String uuidForUpdatingPassword;
    @Value("${shibboleth.test.data.identity.passwordForUpdate}")
    protected String passwordForUpdate;


    protected String getBaseUrl() {
        return "http://localhost:" + port;
    }


    protected RequestSpecification withValidAuthentication() {
        return with().header(apiKeyHeader, apiKeyValidValue);
    }


    protected RequestSpecification withNoAuthentication() {
        return with();
    }


    protected RequestSpecification withInvalidAuthentication() {
        return with().header(apiKeyHeader, apiKeyInvalidValue);
    }


    protected byte[] convertToJson(final Object object) throws IOException {
        final ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        return mapper.writeValueAsBytes(object);
    }

}
