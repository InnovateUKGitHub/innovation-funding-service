package org.innovateuk.ifs;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.rest.RestErrorResponse;
import org.innovateuk.ifs.commons.security.authentication.user.UserAuthentication;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Before;
import org.junit.Rule;
import org.junit.experimental.categories.Category;
import org.mockito.InjectMocks;
import org.springframework.restdocs.JUnitRestDocumentation;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

/**
 * This is the base class for testing Controllers using MockMVC in addition to standard Mockito mocks.  Using MockMVC
 * allows Controllers to be tested via their routes and their responses' HTTP responses tested also.
 */
@Category(ControllerTest.class)
public abstract class BaseControllerMockMVCTest<ControllerType> extends BaseUnitTestMocksTest {

    @InjectMocks
    protected ControllerType controller = supplyControllerUnderTest();

    protected MockMvc mockMvc;

    protected abstract ControllerType supplyControllerUnderTest();

    protected static ObjectMapper objectMapper;
    static {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Rule
    public JUnitRestDocumentation restDocumentation = new JUnitRestDocumentation("build/generated-snippets");



    @Before
    public void setupMockMvc() {
        mockMvc = new MockMvcConfigurer()
                .restDocumentation(restDocumentation)
                .getMockMvc(controller);
    }

    private static ResultMatcher contentObject(final Object json) throws JsonProcessingException {
        return content().string(objectMapper.writeValueAsString(json));
    }

    protected static ResultMatcher contentError(final Error error) throws JsonProcessingException {
        return contentObject(new RestErrorResponse(error));
    }

    protected void assertResponseErrorKeyEqual(String expectedKey, Error expectedError, MvcResult mvcResult) throws IOException {
        String content = mvcResult.getResponse().getContentAsString();
        RestErrorResponse restErrorResponse = objectMapper.readValue(content, RestErrorResponse.class);
        assertErrorKeyEqual(expectedKey, expectedError, restErrorResponse);
    }

    private void assertErrorKeyEqual(String expectedErrorKey, Error expectedError, RestErrorResponse restErrorResponse) {
        assertEquals(expectedErrorKey, restErrorResponse.getErrors().get(0).getErrorKey());
        assertEqualsUpNoIncludingStatusCode(expectedErrorKey, restErrorResponse, expectedError);
    }

    protected void assertEqualsUpNoIncludingStatusCode(final RestErrorResponse restErrorResponse, final Error expectedError){
        assertEqualsUpNoIncludingStatusCode(restErrorResponse.getErrors().get(0).getErrorKey(), restErrorResponse, expectedError);
    }

    protected void assertEqualsUpNoIncludingStatusCode(String expectedErrorKey, final RestErrorResponse restErrorResponse, final Error expectedError){
        assertTrue(restErrorResponse.getErrors().size() == 1);
        assertEquals(restErrorResponse.getErrors().get(0).getArguments() , expectedError.getArguments());
        assertEquals(expectedErrorKey , expectedError.getErrorKey());
    }



    protected static void login(UserResource userResource) {
        SecurityContextHolder.getContext().setAuthentication(new UserAuthentication(userResource));
    }
}
