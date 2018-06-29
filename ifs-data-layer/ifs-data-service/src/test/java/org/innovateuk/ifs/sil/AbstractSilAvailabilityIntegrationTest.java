package org.innovateuk.ifs.sil;

import org.innovateuk.ifs.BaseAuthenticationAwareIntegrationTest;
import org.innovateuk.ifs.commons.service.AbstractRestTemplateAdaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;

import java.util.function.Consumer;

@TestPropertySource(properties = {"sil.available=true"})
@DirtiesContext
public abstract class AbstractSilAvailabilityIntegrationTest extends BaseAuthenticationAwareIntegrationTest {

    @Autowired
    private SilAvailabilityHelper silAvailabilityHelper;

    protected void setupServiceUnavailableResponseExpectationsFromSendEmailCall(AbstractRestTemplateAdaptor mockEmailSilRestTemplate) {
        silAvailabilityHelper.setupServiceUnavailableResponseExpectationsFromSendEmailCall(mockEmailSilRestTemplate);
    }

    protected void verifyServiceUnavailableResponseExpectationsFromSendEmailCall(AbstractRestTemplateAdaptor mockEmailSilRestTemplate) {
        silAvailabilityHelper.verifyServiceUnavailableResponseExpectationsFromSendEmailCall(mockEmailSilRestTemplate);
    }

    /**
     * Temporarily swaps out the SIL Email Service's Rest Template out for a mock one during a test run, and restores the
     * original afterwards
     */
    public void withMockSilEmailRestTemplate(Consumer<AbstractRestTemplateAdaptor> testCode) {
        silAvailabilityHelper.withMockSilEmailRestTemplate(testCode);
    }
}