package org.innovateuk.ifs.sil;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.innovateuk.ifs.org.innovateuk.ifs.rest.RestResultHandlingHttpMessageConverter;
import org.junit.Before;
import org.junit.Rule;
import org.mockito.InjectMocks;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.restdocs.JUnitRestDocumentation;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.HandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.innovateuk.ifs.util.CollectionFunctions.combineLists;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.test.util.ReflectionTestUtils.getField;

public abstract class AbstractEndpointControllerMockMvcTest<T> {

    @InjectMocks
    protected T controller = supplyControllerUnderTest();

    protected MockMvc mockMvc;

    protected abstract T supplyControllerUnderTest();

    protected static ObjectMapper objectMapper;

    static {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Rule
    public JUnitRestDocumentation restDocumentation = new JUnitRestDocumentation("build/generated-snippets");// having to "fake" the request body as JSON because Spring Restdocs does not support other content types other

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
}
