package org.innovateuk.ifs.commons.error;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class CommonErrorsTest {

    @Test
    public void payloadTooLargeError() {
        Error error = CommonErrors.payloadTooLargeError(1048576L);
        assertThat(error.getArguments().get(0), equalTo("1048576"));
        assertThat(error.getArguments().get(1), equalTo("1MB"));
    }
}