package org.innovateuk.ifs.user.transactional;

import org.innovateuk.ifs.commons.service.AbstractRestTemplateAdaptor;
import org.innovateuk.ifs.sil.email.service.RestSilEmailEndpoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

import static org.innovateuk.ifs.user.transactional.AvailabliltyHelperUtils.SERVICE_UNAVAILABLE_RESPONSE_FROM_REST_TEMPLATE;
import static org.innovateuk.ifs.user.transactional.AvailabliltyHelperUtils.temporarilySwapOutRestTemplateAdaptor;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

@Component
class SilAvailabilityHelper {

    @Autowired
    private RestSilEmailEndpoint restSilEmailEndpoint;

    void setupServiceUnavailableResponseExpectationsFromSendEmailCall(AbstractRestTemplateAdaptor mockEmailSilRestTemplate) {
        when(mockEmailSilRestTemplate.restPostWithEntity(any(), any(), any(), any(), eq(HttpStatus.ACCEPTED))).thenReturn(
                SERVICE_UNAVAILABLE_RESPONSE_FROM_REST_TEMPLATE);
    }

    /**
     * Temporarily swaps out the SIL Email Service's Rest Template out for a mock one during a test run, and restores the
     * original afterwards
     */
    void doWithMockSilEmailRestTemplate(Consumer<AbstractRestTemplateAdaptor> testCode) {
        temporarilySwapOutRestTemplateAdaptor(testCode, restSilEmailEndpoint);
    }
}
