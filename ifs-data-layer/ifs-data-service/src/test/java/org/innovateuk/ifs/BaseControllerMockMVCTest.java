package org.innovateuk.ifs;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.commons.lang3.tuple.Pair;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.rest.RestErrorResponse;
import org.innovateuk.ifs.commons.security.authentication.user.UserAuthentication;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.file.service.BasicFileAndContents;
import org.innovateuk.ifs.file.service.FileAndContents;
import org.innovateuk.ifs.file.service.FilesizeAndTypeFileValidator;
import org.innovateuk.ifs.file.transactional.FileHeaderAttributes;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Before;
import org.junit.Rule;
import org.mockito.InjectMocks;
import org.springframework.restdocs.JUnitRestDocumentation;
import org.springframework.restdocs.headers.HeaderDescriptor;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.restdocs.request.ParameterDescriptor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import static org.innovateuk.ifs.InputStreamTestUtil.assertInputStreamContents;
import static org.innovateuk.ifs.LambdaMatcher.createLambdaMatcher;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.id;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.file.builder.FileEntryResourceBuilder.newFileEntryResource;
import static org.innovateuk.ifs.util.JsonMappingUtil.toJson;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.TEXT_PLAIN;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * This is the base class for testing Controllers using MockMVC in addition to standard Mockito mocks.  Using MockMVC
 * allows Controllers to be tested via their routes and their responses' HTTP responses tested also.
 */
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
