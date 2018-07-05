package org.innovateuk.ifs.user.transactional;

import org.innovateuk.ifs.authentication.resource.CreateUserResponse;
import org.innovateuk.ifs.authentication.service.RestIdentityProviderService;
import org.innovateuk.ifs.commons.service.AbstractRestTemplateAdaptor;
import org.innovateuk.ifs.util.Either;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

import static org.innovateuk.ifs.AvailabliltyHelperUtils.SERVICE_UNAVAILABLE_RESPONSE_FROM_REST_TEMPLATE;
import static org.innovateuk.ifs.AvailabliltyHelperUtils.temporarilySwapOutRestTemplateAdaptor;
import static org.innovateuk.ifs.util.Either.left;
import static org.innovateuk.ifs.util.Either.right;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.SERVICE_UNAVAILABLE;

@Component
class RegistrationApiAvailabilityHelper {

    static final Either<ResponseEntity<Object>, ResponseEntity<Object>> SUCCESSFUL_CREATE_USER_RESPONSE_FROM_REG_API =
            right(new ResponseEntity<>(new CreateUserResponse("a-new-uuid", "thebspig@example.com", null, null), OK));


    static Either<ResponseEntity<Object>, ResponseEntity<Object>> SERVICE_UNAVAILABLE_CREATE_USER_RESPONSE_FROM_REG_API =
            left(new ResponseEntity<>(SERVICE_UNAVAILABLE));

    @Autowired
    private RestIdentityProviderService idpService;

    void setupSuccessfulResponseExpectationsFromCreateUserCall(AbstractRestTemplateAdaptor mockEmailSilRestTemplate) {
        when(mockEmailSilRestTemplate.restPostWithEntity(any(), any(), any(), any(), eq(HttpStatus.CREATED))).thenReturn(
                SUCCESSFUL_CREATE_USER_RESPONSE_FROM_REG_API);
    }

    void setupServiceUnavailableResponseExpectationsFromCreateUserCall(AbstractRestTemplateAdaptor mockEmailSilRestTemplate) {
        when(mockEmailSilRestTemplate.restPostWithEntity(any(), any(), any(), any(), eq(HttpStatus.CREATED))).thenReturn(
                SERVICE_UNAVAILABLE_RESPONSE_FROM_REST_TEMPLATE);
    }

    /**
     * Temporarily swaps out the IDP Service's Rest Template out for a mock one during a test run, and restores the
     * original afterwards
     */
    void withMockIdpRestTemplate(Consumer<AbstractRestTemplateAdaptor> testCode) {
        temporarilySwapOutRestTemplateAdaptor(testCode, idpService);
    }
}
