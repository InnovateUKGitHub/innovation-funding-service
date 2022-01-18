package org.innovateuk.ifs.util;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class EncodingUtilsTest {

    private static String URL_D = "https://devops.innovateuk.org/documentation/display/IFS/Innovation+Funding+Service";
    private static String URL_E = "https%3A%2F%2Fdevops.innovateuk.org%2Fdocumentation%2Fdisplay%2FIFS%2FInnovation%2BFunding%2BService";

    @Test
    public void urlEncodeDecodeTest() {
        assertThat(EncodingUtils.urlEncode(URL_D), equalTo(URL_E));
    }

    @Test
    public void urlDecode() {
        assertThat(EncodingUtils.urlDecode(URL_E), equalTo(URL_D));
    }
}