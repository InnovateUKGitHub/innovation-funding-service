package org.innovateuk.ifs;

import org.innovateuk.ifs.commons.rest.RestResultHandlingHttpMessageConverter;
import org.innovateuk.ifs.rest.ErrorControllerAdvice;
import org.innovateuk.ifs.util.JsonMappingUtil;
import org.junit.Before;
import org.junit.Rule;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.restdocs.JUnitRestDocumentation;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.HandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

import java.util.List;

import static org.innovateuk.ifs.util.CollectionFunctions.simpleFilter;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleFindFirstMandatory;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.test.util.ReflectionTestUtils.getField;

/**
 * Base test class that pre-configures MockMVC for use in controller tests.
 *
 * @param <ControllerType>
 */
abstract public class MockMvcTest<ControllerType> {

    protected MockMvc mockMvc;

    @InjectMocks
    protected ControllerType controller = supplyControllerUnderTest();

    @Rule
    public JUnitRestDocumentation restDocumentation = new JUnitRestDocumentation("build/generated-snippets");

    public abstract ControllerType supplyControllerUnderTest();

    /**
     * We need to register custom {@link HttpMessageConverter}s with MockMVC manually.
     * <p>
     * Unfortunately there's no way to simply add a new one to the custom set of
     * HttpMessageConverters that comes out of the box. Therefore we need to get
     * the original set as a list, add our custom one to the list, and then
     * set this list as the full set of HttpMessageConverters.
     */
    @Before
    public void setupMockMvc() {
        MockMvc originalMvc = MockMvcBuilders.standaloneSetup(controller).build();

        List<HandlerAdapter> defaultHandlerAdapters = (List<HandlerAdapter>) getField(
                getField(originalMvc, "servlet"),
                "handlerAdapters"
        );

        RequestMappingHandlerAdapter requestMappingHandlerAdapter =
                (RequestMappingHandlerAdapter) simpleFindFirstMandatory(
                        defaultHandlerAdapters,
                        handler -> handler instanceof RequestMappingHandlerAdapter
                );

        List<HttpMessageConverter<?>> originalMessageConverters = requestMappingHandlerAdapter.getMessageConverters();

        List<HttpMessageConverter<?>> messageConverters =
                simpleFilter(
                        originalMessageConverters,
                        converter -> !(converter instanceof MappingJackson2HttpMessageConverter)
                );

        messageConverters.add(new RestResultHandlingHttpMessageConverter());

        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setMessageConverters(messageConverters.toArray(new HttpMessageConverter[]{}))
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .setControllerAdvice(new ErrorControllerAdvice())
                .apply(documentationConfiguration(this.restDocumentation)
                               .uris()
                               .withScheme("http")
                               .withHost("localhost")
                               .withPort(8090))
                .build();
    }

    @Before
    public void injectMockitoMocks() {
        MockitoAnnotations.initMocks(this);
    }

    protected String json(Object object) {
        return JsonMappingUtil.toJson(object);
    }
}
