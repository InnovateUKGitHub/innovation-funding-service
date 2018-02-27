package org.innovateuk.ifs.transactional;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.commons.rest.RestErrorResponse;
import org.junit.Test;

import java.net.URI;

import static org.innovateuk.ifs.commons.error.CommonErrors.badRequestError;
import static org.innovateuk.ifs.util.JsonMappingUtil.toJson;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Tests for the {@link org.innovateuk.ifs.commons.rest.RestResultHandlingHttpMessageConverter}, to assert that it can take successful
 * RestResults from Controllers and convert them into the "body" of the RestResult, and that it can take failing RestResults
 * and convert them into {@link RestErrorResponse} objects.
 */
public class RestResultHandlingHttpMessageConverterTest extends BaseControllerMockMVCTest<RestResultHandlingTestController> {

    @Test
    public void testSuccessRestResultHandledAsTheBodyOfTheRestResult() throws Exception {

        mockMvc.perform(get(new URI("/rest-result-handling-test-controller"))).
            andExpect(status().isOk()).
            andExpect(content().json(toJson(new ResultObject("Hello!"))));
    }

    @Test
    public void testFailureRestResultHandledAsARestErrorResponse() throws Exception {

        RestErrorResponse errorResponse = new RestErrorResponse(badRequestError("Error!"));

        mockMvc.perform(get(new URI("/bad-request"))).
                andExpect(status().isBadRequest()).
                andExpect(content().json(toJson(errorResponse)));
    }

    @Override
    protected RestResultHandlingTestController supplyControllerUnderTest() {
        return new RestResultHandlingTestController();
    }
}
