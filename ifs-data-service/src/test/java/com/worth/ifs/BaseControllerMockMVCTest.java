package com.worth.ifs;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.worth.ifs.commons.rest.RestErrorResponse;
import com.worth.ifs.rest.RestResultHandlingHttpMessageConverter;
import org.junit.Before;
import org.junit.Rule;
import org.mockito.InjectMocks;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.restdocs.RestDocumentation;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.HandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import com.worth.ifs.commons.error.Error;

import java.io.IOException;
import java.util.List;

import static com.worth.ifs.util.CollectionFunctions.combineLists;
import static java.util.stream.Collectors.toList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.test.util.ReflectionTestUtils.getField;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

/**
 * This is the base class for testing Controllers using MockMVC in addition to standard Mockito mocks.  Using MockMVC
 * allows Controllers to be tested via their routes and their responses' HTTP responses tested also.
 *
 * Created by dwatson on 02/10/15.
 */
public abstract class BaseControllerMockMVCTest<ControllerType> extends BaseUnitTestMocksTest {

    @InjectMocks
    protected ControllerType controller = supplyControllerUnderTest();

    protected MockMvc mockMvc;

    protected abstract ControllerType supplyControllerUnderTest();

    @Rule
    public final RestDocumentation restDocumentation = new RestDocumentation("build/generated-snippets");

    @Before
    public void setupMockMvc() {

        MockMvc originalMvc = MockMvcBuilders.standaloneSetup(controller).build();

        //
        // We need to register custom MessageConverters with MockMVC manually.  Unfortunately there's no way to simply add a new
        // one to the custom set of MessageConverters that comes out of the box.  Therefore we need to get the original set as
        // a list, add our custom one to the list, and then set this list as the full set of MessageConverters
        //
        List<HandlerAdapter> defaultHandlerAdapters = (List<HandlerAdapter>) getField(getField(originalMvc, "servlet"), "handlerAdapters");
        RequestMappingHandlerAdapter requestMappingHandlerAdapter = (RequestMappingHandlerAdapter) defaultHandlerAdapters.stream().filter(handler -> handler instanceof RequestMappingHandlerAdapter).collect(toList()).get(0);
        List<HttpMessageConverter<?>> originalMessageConverters = requestMappingHandlerAdapter.getMessageConverters();
        List<HttpMessageConverter<?>> nonMappingJackson2Converters = originalMessageConverters.stream().filter(converter -> !(converter instanceof MappingJackson2HttpMessageConverter)).collect(toList());

        RestResultHandlingHttpMessageConverter customHttpMessageConverter = new RestResultHandlingHttpMessageConverter();
        List<HttpMessageConverter<?>> newListOfConverters = combineLists(nonMappingJackson2Converters, customHttpMessageConverter);
        HttpMessageConverter[] newListOfConvertersArray = newListOfConverters.toArray(new HttpMessageConverter[newListOfConverters.size()]);

        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setMessageConverters(newListOfConvertersArray)
                .apply(documentationConfiguration(this.restDocumentation)
                        .uris()
                        .withScheme("http")
                        .withHost("localhost")
                        .withPort(8090))
                .build();
    }

    protected static ResultMatcher contentObject(final Object json) throws JsonProcessingException {
        return content().string(new ObjectMapper().writeValueAsString(json));
    }

    protected static ResultMatcher contentError(final Error error) throws JsonProcessingException {
        return contentObject(new RestErrorResponse(error));
    }

    protected void assertResponseErrorMessageEqual(String expectedMessage, Error expectedError, MvcResult mvcResult) throws IOException {
        String content = mvcResult.getResponse().getContentAsString();
        RestErrorResponse restErrorResponse = new ObjectMapper().readValue(content, RestErrorResponse.class);
        assertErrorMessageEqual(expectedMessage, expectedError, restErrorResponse);
    }

    private void assertErrorMessageEqual(String expectedMessage, Error expectedError, RestErrorResponse restErrorResponse) {
        assertEquals(expectedMessage, restErrorResponse.getErrors().get(0).getErrorMessage());
        assertEqualsUpNoIncludingStatusCode(restErrorResponse, expectedError);
    }

    protected void assertEqualsUpNoIncludingStatusCode(final RestErrorResponse restErrorResponse, final Error expectedError){
        assertTrue(restErrorResponse.getErrors().size() == 1);
        assertEquals(restErrorResponse.getErrors().get(0).getErrorMessage(), expectedError.getErrorMessage());
        assertEquals(restErrorResponse.getErrors().get(0).getArguments() , expectedError.getArguments());
        assertEquals(restErrorResponse.getErrors().get(0).getErrorKey() , expectedError.getErrorKey());
    }
}