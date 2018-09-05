package org.innovateuk.ifs.util;

import org.innovateuk.ifs.origin.BackLinkUtil;
import org.junit.Test;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.util.MapFunctions.asMap;
import static org.junit.Assert.assertEquals;

public class BackLinkUtilTest {

    enum TestEnum {
        ALL_APPLICATIONS
    }

    @Test
    public void buildOriginQueryString() throws Exception {
        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>(asMap(
                "sort", asList("applicationNumber", "innovationArea")
        ));

        String result = BackLinkUtil.buildOriginQueryString(TestEnum.ALL_APPLICATIONS, queryParams);
        String expectedQuery = "?origin=ALL_APPLICATIONS&sort=applicationNumber&sort=innovationArea";

        assertEquals(expectedQuery, result);
    }

    @Test
    public void buildOriginQueryString_encodesReservedChars() throws Exception {
        // Not exhaustive, but at least these characters should be covered
        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>(asMap(
                "p", asList("&", "=", "%", " ")
        ));

        String result = BackLinkUtil.buildOriginQueryString(TestEnum.ALL_APPLICATIONS, queryParams);
        String expectedQuery = "?origin=ALL_APPLICATIONS&p=%26&p=%3D&p=%25&p=%20";

        assertEquals(expectedQuery, result);
    }

    @Test
    public void buildOriginQueryString_originDoesNotAppearTwice() throws Exception {
        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>(asMap(
                "origin", singletonList("ANOTHER_ORIGIN"),
                "param1", singletonList("abc")
        ));

        String result = BackLinkUtil.buildOriginQueryString(TestEnum.ALL_APPLICATIONS, queryParams);
        String expectedQuery = "?origin=ALL_APPLICATIONS&param1=abc";

        assertEquals(expectedQuery, result);
    }


}
