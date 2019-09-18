package org.innovateuk.ifs.controller;

import org.innovateuk.ifs.commons.security.UserAuthenticationService;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.core.MethodParameter;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;

import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.Silent.class)
public class LoggedInUserMethodArgumentResolverTest {
    @Mock
    private WebDataBinderFactory webDataBinderFactory;

    @Mock
    private ModelAndViewContainer modelAndViewContainer;

    @Mock
    private UserAuthenticationService userAuthenticationService;

    @InjectMocks
    private LoggedInUserMethodArgumentResolver loggedInUserMethodArgumentResolver = new LoggedInUserMethodArgumentResolver();

    private Method testMethod;

    @Before
    public void setUp() throws Exception {
        testMethod = TestController.class.getMethod("test", UserResource.class, NotAUserResource.class, UserResource.class);
    }

    @Test
    public void supportsParameter_shouldSupportUserResource() {
        MethodParameter userResourceParameter = new MethodParameter(testMethod, 0);

        assertTrue(loggedInUserMethodArgumentResolver.supportsParameter(userResourceParameter));
    }

    @Test
    public void supportsParameter_shouldNotSupportAnotherType() {
        MethodParameter notAUserResourceParameter = new MethodParameter(testMethod, 1);

        assertFalse(loggedInUserMethodArgumentResolver.supportsParameter(notAUserResourceParameter));
    }

    @Test
    public void supportsParameter_shouldNotSupportModelAttributedUserResource() {
        MethodParameter modelAttributeResourceParameter = new MethodParameter(testMethod, 2);

        assertFalse(loggedInUserMethodArgumentResolver.supportsParameter(modelAttributeResourceParameter));
    }

    @Test
    public void resolveArgument_shouldReturnUserResource() throws Exception {
        MockHttpServletRequest mockRequest = new MockHttpServletRequest();
        mockRequest.addParameter("uid", "123");
        NativeWebRequest webRequest = new ServletWebRequest(mockRequest);

        MethodParameter userResourceParameter = new MethodParameter(testMethod, 0);
        UserResource userResource = newUserResource().withFirstName("Steve").build();

        when(userAuthenticationService.getAuthenticatedUser(isA(HttpServletRequest.class))).thenReturn(userResource);

        UserResource loggedInUser = (UserResource) loggedInUserMethodArgumentResolver.resolveArgument(userResourceParameter, modelAndViewContainer, webRequest, webDataBinderFactory);

        verify(userAuthenticationService, times(1)).getAuthenticatedUser(isA(HttpServletRequest.class));
        verifyNoMoreInteractions(userAuthenticationService);
        assertEquals("Steve", loggedInUser.getFirstName());
    }

    @Controller
    @RequestMapping("/")
    public class TestController {
        @PostMapping
        public void test(UserResource loggedInUserResource,
                         NotAUserResource notALoggedInUserResource,
                         @ModelAttribute UserResource userResourceModelAttribute) {
        }
    }

    private class NotAUserResource {
    }
}