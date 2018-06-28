package org.innovateuk.ifs;

import org.innovateuk.ifs.commons.service.AbstractRestTemplateAdaptor;
import org.innovateuk.ifs.util.Either;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.function.Consumer;

import static org.innovateuk.ifs.util.Either.left;
import static org.mockito.Mockito.mock;
import static org.springframework.http.HttpStatus.SERVICE_UNAVAILABLE;

/**
 * TODO DW - document this class
 */
public class AvailabliltyHelperUtils {

    public static final Either<ResponseEntity<Object>, ResponseEntity<Object>> SERVICE_UNAVAILABLE_RESPONSE_FROM_REST_TEMPLATE =
            left(new ResponseEntity<>(SERVICE_UNAVAILABLE));

    public static void temporarilySwapOutRestTemplateAdaptor(Consumer<AbstractRestTemplateAdaptor> testCode, Object restTemplateAdaptorOwner) {

        // swap out the real RestTemplate adaptor from the owner, so we can mock out the communication with an
        // external system
        AbstractRestTemplateAdaptor originalRestTemplate = (AbstractRestTemplateAdaptor) ReflectionTestUtils.getField(restTemplateAdaptorOwner, "adaptor");
        AbstractRestTemplateAdaptor mockRestTemplate = mock(AbstractRestTemplateAdaptor.class);
        ReflectionTestUtils.setField(restTemplateAdaptorOwner, "adaptor", mockRestTemplate);

        try {

            // run the test code, given the mock rest template
            testCode.accept(mockRestTemplate);
        } finally {

            // and finally swap the original real RestTemplate adaptor back in
            ReflectionTestUtils.setField(restTemplateAdaptorOwner, "adaptor", originalRestTemplate);
        }
    }
}
