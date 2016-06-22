package com.worth.ifs;

import com.worth.ifs.commons.security.UserAuthentication;
import com.worth.ifs.commons.security.UserAuthenticationService;
import com.worth.ifs.controller.ControllerModelAttributeAdvice;
import com.worth.ifs.controller.CustomFormBindingControllerAdvice;
import com.worth.ifs.exception.ErrorControllerAdvice;
import com.worth.ifs.filter.CookieFlashMessageFilter;
import com.worth.ifs.user.resource.UserResource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.mockito.InjectMocks;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.method.annotation.ExceptionHandlerMethodResolver;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;
import org.springframework.web.servlet.mvc.method.annotation.ServletInvocableHandlerMethod;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;

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

        CookieLocaleResolver localeResolver = new CookieLocaleResolver();
        localeResolver.setCookieDomain("domain");

        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setControllerAdvice(
                        new ErrorControllerAdvice(),
                        new CustomFormBindingControllerAdvice(),
                        modelAttributeAdvice()
                )
                .addFilter(new CookieFlashMessageFilter())
                .setLocaleResolver(localeResolver)
                .setHandlerExceptionResolvers(createExceptionResolver())
                .setViewResolvers(viewResolver())
                .build();

        setLoggedInUser(loggedInUser);

    }

    private ControllerModelAttributeAdvice modelAttributeAdvice() {

        ControllerModelAttributeAdvice modelAttributeAdvice = new ControllerModelAttributeAdvice();

        ReflectionTestUtils.setField(modelAttributeAdvice, "userAuthenticationService", new UserAuthenticationService() {
            @Override
            public Authentication getAuthentication(HttpServletRequest request) {
                return new UserAuthentication(getLoggedInUser());
            }

            @Override
            public UserResource getAuthenticatedUser(HttpServletRequest request) {
                return getLoggedInUser();
            }
        });

        return modelAttributeAdvice;
    }

    public ExceptionHandlerExceptionResolver createExceptionResolver() {
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