package org.innovateuk.ifs.sil;

import org.innovateuk.ifs.commons.service.AbstractRestTemplateAdaptor;
import org.innovateuk.ifs.sil.email.service.RestSilEmailEndpoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

import static org.innovateuk.ifs.AvailabliltyHelperUtils.SERVICE_UNAVAILABLE_RESPONSE_FROM_REST_TEMPLATE;
import static org.innovateuk.ifs.AvailabliltyHelperUtils.temporarilySwapOutRestTemplateAdaptor;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Component
public class EmailServiceAvailabilityHelper {

    @Autowired
    private RestSilEmailEndpoint restSilEmailEndpoint;

    void setupServiceUnavailableResponseExpectationsFromSendEmailCall(AbstractRestTemplateAdaptor mockEmailSilRestTemplate) {
        when(mockEmailSilRestTemplate.restPostWithEntity(any(), any(), any(), any(), eq(HttpStatus.ACCEPTED))).thenReturn(
                SERVICE_UNAVAILABLE_RESPONSE_FROM_REST_TEMPLATE);
    }

    void verifyServiceUnavailableResponseExpectationsFromSendEmailCall(AbstractRestTemplateAdaptor mockEmailSilRestTemplate) {
        verify(mockEmailSilRestTemplate).restPostWithEntity(any(), any(), any(), any(), eq(HttpStatus.ACCEPTED));
    }

    /**
     * Temporarily swaps the SIL Email Service's Rest Template out for a mock one during a test run, and restores the
     * original afterwards
     */
    void withMockSilEmailRestTemplate(Consumer<AbstractRestTemplateAdaptor> testCode) {
        temporarilySwapOutRestTemplateAdaptor(testCode, restSilEmailEndpoint);
    }
}