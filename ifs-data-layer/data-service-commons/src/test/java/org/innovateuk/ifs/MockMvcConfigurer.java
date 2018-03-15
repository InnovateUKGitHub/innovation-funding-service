package org.innovateuk.ifs;

import org.innovateuk.ifs.commons.rest.RestResultHandlingHttpMessageConverter;
import org.innovateuk.ifs.rest.ErrorControllerAdvice;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.restdocs.JUnitRestDocumentation;
import org.springframework.restdocs.mockmvc.UriConfigurer;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.test.web.servlet.setup.StandaloneMockMvcBuilder;
import org.springframework.web.servlet.HandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

import java.util.List;

import static org.innovateuk.ifs.util.CollectionFunctions.simpleFilter;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleFindFirstMandatory;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.test.util.ReflectionTestUtils.getField;

/**
 * Configure a {@link MockMvc} instance using sensible defaults for testing
 * in the data tier. We should also be able to apply any other optional
 * configuration such as Spring RestDocs (if required).
 */
public class MockMvcConfigurer {

    private UriConfigurer documentConfiguration;

    public MockMvcConfigurer restDocumentation(JUnitRestDocumentation restDocumentation) {
        return restDocumentation(restDocumentation, "http", "localhost", 8090);
    }

    public MockMvcConfigurer restDocumentation(
            JUnitRestDocumentation restDocumentation,
            String scheme,
            String host,
            int port
    ) {
        documentConfiguration = documentationConfiguration(restDocumentation)
                .uris()
                .withScheme(scheme)
                .withHost(host)
                .withPort(port);

        return this;
    }

    /**
     * Build our test {@link MockMvc} instance.
     * <p>
     * We need to register custom {@link HttpMessageConverter}s with MockMVC manually.
     * <p>
     * Unfortunately there's no way to simply add a new one to the custom set of
     * HttpMessageConverters that comes out of the box. Therefore we need to get
     * the original set as a list, add our custom one to the list, and then
     * set this list as the full set of {@link HttpMessageConverter}s.
     */
    public MockMvc getMockMvc(Object controller) {
        List<HandlerAdapter> defaultHandlerAdapters = getDefaultHandlerAdapters(controller);

        RequestMappingHandlerAdapter requestMappingHandlerAdapter =
                (RequestMappingHandlerAdapter) simpleFindFirstMandatory(
                        defaultHandlerAdapters,
                        handler -> handler instanceof RequestMappingHandlerAdapter
                );

        List<HttpMessageConverter<?>> originalMessageConverters =
                requestMappingHandlerAdapter.getMessageConverters();

        List<HttpMessageConverter<?>> messageConverters =
                simpleFilter(
                        originalMessageConverters,
                        converter -> !(converter instanceof MappingJackson2HttpMessageConverter)
                );

        messageConverters.add(new RestResultHandlingHttpMessageConverter());

        StandaloneMockMvcBuilder builder = MockMvcBuilders.standaloneSetup(controller)
                .setMessageConverters(messageConverters.toArray(new HttpMessageConverter[]{}))
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .setControllerAdvice(new ErrorControllerAdvice());

        if (documentConfiguration != null) {
            builder.apply(documentConfiguration);
        }

        return builder.build();
    }

    @SuppressWarnings("unchecked")
    private List<HandlerAdapter> getDefaultHandlerAdapters(Object controller) {
        MockMvc originalMvc = MockMvcBuilders.standaloneSetup(controller).build();

        return (List<HandlerAdapter>) getField(getField(originalMvc, "servlet"), "handlerAdapters");
    }
}
