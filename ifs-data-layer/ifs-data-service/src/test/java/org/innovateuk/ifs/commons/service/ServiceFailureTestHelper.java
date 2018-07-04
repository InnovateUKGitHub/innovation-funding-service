package org.innovateuk.ifs.commons.service;

import org.innovateuk.ifs.commons.error.Error;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * TODO DW - document this class
 */
public class ServiceFailureTestHelper {

    public static void assertThatServiceFailureIs(ServiceResult<?> serviceResult, Error... expectedErrors) {

        assertThat(serviceResult.isFailure()).withFailMessage("Expected a failure").isTrue();

        List<Error> actualErrors = serviceResult.getErrors();
        assertThat(actualErrors).containsOnly(expectedErrors);
    }
}
