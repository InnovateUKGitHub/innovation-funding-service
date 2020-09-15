package org.innovateuk.ifs.exception;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.innovateuk.ifs.BaseUnitTest;
import org.innovateuk.ifs.commons.exception.IFSRuntimeException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.context.MessageSource;
import org.springframework.core.env.Environment;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.ui.ModelMap;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.Locale;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;


public class ErrorControllerAdviceTest extends BaseUnitTest {

    @Mock
    private Environment env;

    @Mock
    private MessageSource messageSource;

    private MockHttpServletRequest httpServletRequest;

    @InjectMocks
    private ErrorControllerAdvice errorControllerAdvice;

    @Before
    public void setUp() {
        super.setup();

        ReflectionTestUtils.setField(errorControllerAdvice, "logoutUrl", "http://site/logout");

        httpServletRequest = new MockHttpServletRequest();
        httpServletRequest.setServerName("site");
    }

    @Test
    public void createErrorModelAndViewWithStatus() {
        Exception exception = new IFSRuntimeException();
        List<Object> arguments = asList("arg 1", "arg 2");
        httpServletRequest.setRequestURI("/test.html");

        when(messageSource.getMessage("error.title.status.500", null, Locale.ENGLISH)).thenReturn("sample title");
        when(messageSource.getMessage("org.innovateuk.ifs.commons.exception.IFSRuntimeException", arguments.toArray(), Locale.ENGLISH)).thenReturn("sample error message");
        when(env.acceptsProfiles("debug")).thenReturn(true);

        ModelAndView mav = errorControllerAdvice.createErrorModelAndViewWithStatus(exception, httpServletRequest, arguments, INTERNAL_SERVER_ERROR);

        ModelMap modelMap = mav.getModelMap();

        assertEquals("error", mav.getViewName());
        assertEquals("sample title", modelMap.get("title"));
        assertNull(modelMap.get("messageForUser"));
        assertEquals("internal_server_error", modelMap.get("errorMessageClass"));
        assertEquals("http://site/test.html", modelMap.get("url"));
        assertEquals("/", modelMap.get("userDashboardLink"));
        assertEquals("http://site/logout", modelMap.get("logoutUrl"));
        assertEquals(exception, modelMap.get("exception"));
        assertEquals(ExceptionUtils.getStackTrace(exception), modelMap.get("stacktrace"));
        assertEquals("sample error message", modelMap.get("message"));
    }

    @Test
    public void createErrorModelAndViewWithStatusAndView() {
        Exception exception = new IFSRuntimeException();
        List<Object> arguments = asList("arg 1", "arg 2");
        httpServletRequest.setRequestURI("/test.html");

        when(messageSource.getMessage("error.title.status.500", null, Locale.ENGLISH)).thenReturn("sample title");
        when(messageSource.getMessage("org.innovateuk.ifs.commons.exception.IFSRuntimeException", arguments.toArray(), Locale.ENGLISH)).thenReturn("sample error message");
        when(env.acceptsProfiles("debug")).thenReturn(true);

        ModelAndView mav = errorControllerAdvice.createErrorModelAndViewWithStatusAndView(exception, httpServletRequest, arguments, INTERNAL_SERVER_ERROR, "other-error-view");

        ModelMap modelMap = mav.getModelMap();

        assertEquals("other-error-view", mav.getViewName());
        assertEquals("sample title", modelMap.get("title"));
        assertNull(modelMap.get("messageForUser"));
        assertEquals("internal_server_error", modelMap.get("errorMessageClass"));
        assertEquals("http://site/test.html", modelMap.get("url"));
        assertEquals("/", modelMap.get("userDashboardLink"));
        assertEquals("http://site/logout", modelMap.get("logoutUrl"));
        assertEquals(exception, modelMap.get("exception"));
        assertEquals(ExceptionUtils.getStackTrace(exception), modelMap.get("stacktrace"));
        assertEquals("sample error message", modelMap.get("message"));
    }

    @Test
    public void createErrorModelAndViewWithStatusAndView_withoutDebug() {
        Exception exception = new IFSRuntimeException();
        List<Object> arguments = asList("arg 1", "arg 2");
        httpServletRequest.setRequestURI("/test.html");

        when(messageSource.getMessage("error.title.status.500", null, Locale.ENGLISH)).thenReturn("sample title");
        when(env.acceptsProfiles("debug")).thenReturn(false);

        ModelAndView mav = errorControllerAdvice.createErrorModelAndViewWithStatusAndView(exception, httpServletRequest, arguments, INTERNAL_SERVER_ERROR, "other-error-view");

        ModelMap modelMap = mav.getModelMap();

        assertEquals("other-error-view", mav.getViewName());
        assertEquals("sample title", modelMap.get("title"));
        assertNull(modelMap.get("messageForUser"));
        assertEquals("internal_server_error", modelMap.get("errorMessageClass"));
        assertEquals("http://site/test.html", modelMap.get("url"));
        assertEquals("/", modelMap.get("userDashboardLink"));
        assertEquals("http://site/logout", modelMap.get("logoutUrl"));
        assertEquals(exception, modelMap.get("exception"));
        assertNull(modelMap.get("stacktrace"));
        assertNull(modelMap.get("message"));
    }

    @Test
    public void createErrorModelAndViewWithTitleAndMessage() {
        Exception exception = new IFSRuntimeException();
        List<Object> arguments = asList("arg 1", "arg 2");
        httpServletRequest.setRequestURI("/test.html");

        when(messageSource.getMessage("sample.title.key", null, Locale.ENGLISH)).thenReturn("sample title");
        when(messageSource.getMessage("sample.message.key", arguments.toArray(), Locale.ENGLISH)).thenReturn("sample user message");
        when(messageSource.getMessage("org.innovateuk.ifs.commons.exception.IFSRuntimeException", arguments.toArray(), Locale.ENGLISH)).thenReturn("sample error message");
        when(env.acceptsProfiles("debug")).thenReturn(true);

        ModelAndView mav = errorControllerAdvice.createErrorModelAndViewWithTitleAndMessage(exception, httpServletRequest, arguments, INTERNAL_SERVER_ERROR, "sample.title.key", "sample.message.key");

        ModelMap modelMap = mav.getModelMap();

        assertEquals("title-and-message-error", mav.getViewName());
        assertEquals("sample title", modelMap.get("title"));
        assertEquals("sample user message", modelMap.get("messageForUser"));
        assertEquals("internal_server_error", modelMap.get("errorMessageClass"));
        assertNull(modelMap.get("url"));
        assertEquals("/", modelMap.get("userDashboardLink"));
        assertEquals("http://site/logout", modelMap.get("logoutUrl"));
        assertEquals(exception, modelMap.get("exception"));
        assertEquals(ExceptionUtils.getStackTrace(exception), modelMap.get("stacktrace"));
        assertEquals("sample error message", modelMap.get("message"));
    }

    @Test
    public void createErrorModelAndViewWithUrlTitleAndMessage() {
        Exception exception = new IFSRuntimeException();
        List<Object> arguments = asList("arg 1", "arg 2");
        httpServletRequest.setRequestURI("/test.html");

        when(messageSource.getMessage("sample.title.key", null, Locale.ENGLISH)).thenReturn("sample title");
        when(messageSource.getMessage("sample.message.key", arguments.toArray(), Locale.ENGLISH)).thenReturn("sample user message");
        when(messageSource.getMessage("org.innovateuk.ifs.commons.exception.IFSRuntimeException", arguments.toArray(), Locale.ENGLISH)).thenReturn("sample error message");
        when(env.acceptsProfiles("debug")).thenReturn(true);

        ModelAndView mav = errorControllerAdvice.createErrorModelAndViewWithUrlTitleAndMessage(exception, httpServletRequest, arguments, INTERNAL_SERVER_ERROR, "sample.title.key", "sample.message.key");

        ModelMap modelMap = mav.getModelMap();

        assertEquals("title-and-message-error", mav.getViewName());
        assertEquals("sample title", modelMap.get("title"));
        assertEquals("sample user message", modelMap.get("messageForUser"));
        assertEquals("internal_server_error", modelMap.get("errorMessageClass"));
        assertEquals("http://site/test.html", modelMap.get("url"));
        assertEquals("/", modelMap.get("userDashboardLink"));
        assertEquals("http://site/logout", modelMap.get("logoutUrl"));
        assertEquals(exception, modelMap.get("exception"));
        assertEquals(ExceptionUtils.getStackTrace(exception), modelMap.get("stacktrace"));
        assertEquals("sample error message", modelMap.get("message"));
    }

    @Test
    public void createErrorModelAndViewWithUrlTitleMessageAndView() {
        Exception exception = new IFSRuntimeException();
        List<Object> arguments = asList("arg 1", "arg 2");
        httpServletRequest.setRequestURI("/test.html");

        when(messageSource.getMessage("sample.title.key", null, Locale.ENGLISH)).thenReturn("sample title");
        when(messageSource.getMessage("sample.message.key", arguments.toArray(), Locale.ENGLISH)).thenReturn("sample user message");
        when(messageSource.getMessage("org.innovateuk.ifs.commons.exception.IFSRuntimeException", arguments.toArray(), Locale.ENGLISH)).thenReturn("sample error message");
        when(env.acceptsProfiles("debug")).thenReturn(true);

        ModelAndView mav = errorControllerAdvice.createErrorModelAndViewWithUrlTitleMessageAndView(exception, httpServletRequest, arguments, INTERNAL_SERVER_ERROR, "sample.title.key", "sample.message.key", "other-error-view");

        ModelMap modelMap = mav.getModelMap();

        assertEquals("other-error-view", mav.getViewName());
        assertEquals("sample title", modelMap.get("title"));
        assertEquals("sample user message", modelMap.get("messageForUser"));
        assertEquals("internal_server_error", modelMap.get("errorMessageClass"));
        assertEquals("http://site/test.html", modelMap.get("url"));
        assertEquals("/", modelMap.get("userDashboardLink"));
        assertEquals("http://site/logout", modelMap.get("logoutUrl"));
        assertEquals(exception, modelMap.get("exception"));
        assertEquals(ExceptionUtils.getStackTrace(exception), modelMap.get("stacktrace"));
        assertEquals("sample error message", modelMap.get("message"));
    }

    @Test
    public void shouldNotIncludeGoogleAnalyticsTagIfVariableNotPresent() {

        Exception exception = new IFSRuntimeException();
        List<Object> arguments = asList("arg 1", "arg 2");

        ModelAndView mav = errorControllerAdvice.createErrorModelAndViewWithUrlTitleMessageAndView(exception, httpServletRequest, arguments, INTERNAL_SERVER_ERROR, "sample.title.key", "sample.message.key", "other-error-view");

        ModelMap modelMap = mav.getModelMap();

        assertNull(modelMap.get("GoogleAnalyticsTrackingID"));
    }

    @Test
    public void shouldIncludeGoogleAnalyticsTagIfVariablePresent() {
        ReflectionTestUtils.setField(errorControllerAdvice, "googleAnalyticsKeys", "testkey");
        Exception exception = new IFSRuntimeException();
        List<Object> arguments = asList("arg 1", "arg 2");

        ModelAndView mav = errorControllerAdvice.createErrorModelAndViewWithUrlTitleMessageAndView(exception, httpServletRequest, arguments, INTERNAL_SERVER_ERROR, "sample.title.key", "sample.message.key", "other-error-view");

        ModelMap modelMap = mav.getModelMap();

        assertEquals("testkey", modelMap.get("GoogleAnalyticsTrackingID"));
    }
}