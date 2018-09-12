package org.innovateuk.ifs.sil;

import org.innovateuk.ifs.BaseAuthenticationAwareIntegrationTest;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.service.AbstractRestTemplateAdaptor;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;

import java.util.function.Consumer;
import java.util.function.Supplier;

import static org.innovateuk.ifs.commons.error.CommonFailureKeys.NOTIFICATIONS_UNABLE_TO_SEND_SINGLE;
import static org.innovateuk.ifs.commons.service.ServiceFailureTestHelper.assertThatServiceFailureIs;
import static org.springframework.http.HttpStatus.SERVICE_UNAVAILABLE;

/**
 * Base test class for tests that cover Services' behaviours when the email service is not available.
 */
@TestPropertySource(properties = {"sil.available=true"})
@DirtiesContext
public abstract class AbstractEmailServiceAvailabilityIntegrationTest extends BaseAuthenticationAwareIntegrationTest {

    @Autowired
    private EmailServiceAvailabilityHelper emailServiceAvailabilityHelper;

    protected void setupServiceUnavailableResponseExpectationsFromSendEmailCall(AbstractRestTemplateAdaptor mockEmailSilRestTemplate) {
        emailServiceAvailabilityHelper.setupServiceUnavailableResponseExpectationsFromSendEmailCall(mockEmailSilRestTemplate);
    }

    protected void verifyServiceUnavailableResponseExpectationsFromSendEmailCall(AbstractRestTemplateAdaptor mockEmailSilRestTemplate) {
        emailServiceAvailabilityHelper.verifyServiceUnavailableResponseExpectationsFromSendEmailCall(mockEmailSilRestTemplate);
    }

    /**
     * Temporarily swaps the SIL Email Service's Rest Template out for a mock one during a test run, and restores the
     * original afterwards
     */
    public void withMockSilEmailRestTemplate(Consumer<AbstractRestTemplateAdaptor> testCode) {
        emailServiceAvailabilityHelper.withMockSilEmailRestTemplate(testCode);
    }

    /**
     * Asserts that an email service Service Unavailable result occurred during the running of the given action, and
     * that this error message is propagated up to the action's result.
     */
    public void withServiceUnavailableFromEmailService(Supplier<ServiceResult<?>> action) {

        withMockSilEmailRestTemplate(mockEmailSilRestTemplate -> {

            setupServiceUnavailableResponseExpectationsFromSendEmailCall(mockEmailSilRestTemplate);

            ServiceResult<?> result = action.get();

            assertThatServiceFailureIs(result, new Error(NOTIFICATIONS_UNABLE_TO_SEND_SINGLE, SERVICE_UNAVAILABLE));

            verifyServiceUnavailableResponseExpectationsFromSendEmailCall(mockEmailSilRestTemplate);
        });
    }
}