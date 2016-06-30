package com.worth.ifs;

import com.worth.ifs.commons.security.UserAuthentication;
import com.worth.ifs.commons.security.UserAuthenticationService;
import com.worth.ifs.controller.ControllerModelAttributeAdvice;
import com.worth.ifs.controller.ControllerValidationHandlerMethodArgumentResolver;
import com.worth.ifs.controller.ControllerValidationHandlerServletModelAttributeMethodProcessor;
import com.worth.ifs.controller.CustomFormBindingControllerAdvice;
import com.worth.ifs.exception.ErrorControllerAdvice;
import com.worth.ifs.filter.CookieFlashMessageFilter;
import com.worth.ifs.user.resource.UserResource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.mockito.InjectMocks;
import org.springframework.context.MessageSource;
import org.springframework.core.env.Environment;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.method.annotation.ExceptionHandlerMethodResolver;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodArgumentResolverComposite;
import org.springframework.web.servlet.HandlerAdapter;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.ServletInvocableHandlerMethod;
import org.springframework.web.servlet.mvc.method.annotation.ServletModelAttributeMethodProcessor;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import static com.worth.ifs.util.CollectionFunctions.simpleFilterNot;
import static java.util.stream.Collectors.toList;
import static org.springframework.test.util.ReflectionTestUtils.getField;

/**
 * This is the base class for testing Controllers using MockMVC in addition to standard Mockito mocks.  Using MockMVC
 * allows Controllers to be tested via their routes and their responses' HTTP responses tested also.
 */
public abstract class BaseControllerMockMVCTest<ControllerType> extends BaseUnitTest {
    public static final Log LOG = LogFactory.getLog(BaseControllerMockMVCTest.class);

    @InjectMocks
    protected ControllerType controller = supplyControllerUnderTest();

    protected MockMvc mockMvc;

    protected abstract ControllerType supplyControllerUnderTest();

    @Before
    public void setUp() {

        super.setup();

        mockMvc = setupMockMvc(controller, () -> getLoggedInUser(), env, messageSource);

        setLoggedInUser(loggedInUser);
    }

    public static <ControllerType> MockMvc setupMockMvc(ControllerType controller, Supplier<UserResource> loggedInUserSupplier, Environment environment, MessageSource messageSource) {

        CookieLocaleResolver localeResolver = new CookieLocaleResolver();
        localeResolver.setCookieDomain("domain");

        MockMvc mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setControllerAdvice(
                        new ErrorControllerAdvice(),
                        new CustomFormBindingControllerAdvice(),
                        modelAttributeAdvice(loggedInUserSupplier)
                )
                .addFilter(new CookieFlashMessageFilter())
                .setLocaleResolver(localeResolver)
                .setHandlerExceptionResolvers(createExceptionResolver(environment, messageSource))
                .setCustomArgumentResolvers(
                        new ControllerValidationHandlerMethodArgumentResolver()
                )
                .setViewResolvers(viewResolver())
                .build();

        swapInCustomModelAttributeResolvers(mockMvc);
        return mockMvc;
    }

    private static void swapInCustomModelAttributeResolvers(MockMvc mockMvc) {

        List<HandlerAdapter> defaultHandlerAdapters = (List<HandlerAdapter>) getField(getField(mockMvc, "servlet"), "handlerAdapters");

        RequestMappingHandlerAdapter requestMappingHandlerAdapter = (RequestMappingHandlerAdapter) defaultHandlerAdapters.stream().filter(handler -> handler instanceof RequestMappingHandlerAdapter).collect(toList()).get(0);
        HandlerMethodArgumentResolverComposite originalCompositeArgumentResolver = (HandlerMethodArgumentResolverComposite) ReflectionTestUtils.getField(requestMappingHandlerAdapter, "argumentResolvers");
        List<HandlerMethodArgumentResolver> originalResolvers = new ArrayList<>(originalCompositeArgumentResolver.getResolvers());

        List<HandlerMethodArgumentResolver> originalResolversWithoutModelAttributeResolvers =
                simpleFilterNot(originalResolvers, r -> ServletModelAttributeMethodProcessor.class.isAssignableFrom(r.getClass()));

        originalResolversWithoutModelAttributeResolvers.add(new ControllerValidationHandlerServletModelAttributeMethodProcessor(false));
        originalResolversWithoutModelAttributeResolvers.add(new ControllerValidationHandlerServletModelAttributeMethodProcessor(true));

        ReflectionTestUtils.setField(originalCompositeArgumentResolver, "argumentResolvers", originalResolversWithoutModelAttributeResolvers);
    }

    private static ControllerModelAttributeAdvice modelAttributeAdvice(Supplier<UserResource> loggedInUserSupplier) {

        ControllerModelAttributeAdvice modelAttributeAdvice = new ControllerModelAttributeAdvice();

        ReflectionTestUtils.setField(modelAttributeAdvice, "userAuthenticationService", new UserAuthenticationService() {
            @Override
            public Authentication getAuthentication(HttpServletRequest request) {
                return new UserAuthentication(loggedInUserSupplier.get());
            }

            @Override
            public UserResource getAuthenticatedUser(HttpServletRequest request) {
                return loggedInUserSupplier.get();
            }
        });

        return modelAttributeAdvice;
    }

    public static ExceptionHandlerExceptionResolver createExceptionResolver(Environment env, MessageSource messageSource) {
        ExceptionHandlerExceptionResolver exceptionResolver = new ExceptionHandlerExceptionResolver() {
            protected ServletInvocableHandlerMethod getExceptionHandlerMethod(HandlerMethod handlerMethod, Exception exception) {
                Method method = new ExceptionHandlerMethodResolver(ErrorControllerAdvice.class).resolveMethod(exception);
                return new ServletInvocableHandlerMethod(new ErrorControllerAdvice(env, messageSource), method);
            }
        };
        exceptionResolver.afterPropertiesSet();
        return exceptionResolver;
    }

    protected void setLoggedInUserAuthentication(UserAuthentication user) {
        SecurityContextHolder.getContext().setAuthentication(user);
    }

    /**
     * Get the user on the Spring Security ThreadLocals
     */
    protected UserResource getLoggedInUser() {
        UserAuthentication authentication = (UserAuthentication) SecurityContextHolder.getContext().getAuthentication();
        return authentication != null ? authentication.getDetails() : null;
    }

    /**
     * Set a user on the Spring Security ThreadLocals
     *
     * @param user
     */
    protected void setLoggedInUser(UserResource user) {
        SecurityContextHolder.getContext().setAuthentication(new UserAuthentication(user));
    }
}