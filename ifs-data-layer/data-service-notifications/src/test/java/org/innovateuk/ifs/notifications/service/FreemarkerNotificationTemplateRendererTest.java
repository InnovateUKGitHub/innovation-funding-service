package org.innovateuk.ifs.notifications.service;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.innovateuk.ifs.BaseUnitTestMocksTest;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.notifications.resource.NotificationSource;
import org.innovateuk.ifs.notifications.resource.NotificationTarget;
import org.innovateuk.ifs.notifications.resource.SystemNotificationSource;
import org.innovateuk.ifs.notifications.resource.UserNotificationTarget;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;

import static org.innovateuk.ifs.commons.error.CommonFailureKeys.NOTIFICATIONS_UNABLE_TO_RENDER_TEMPLATE;
import static org.innovateuk.ifs.util.MapFunctions.asMap;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.Silent.class)
public class FreemarkerNotificationTemplateRendererTest extends BaseUnitTestMocksTest {

    @Mock
    private Configuration freemarkerConfigurationMock;

    @Mock
    private Template freemarkerTemplateMock;

    @Mock
    private SystemNotificationSource systemNotificationSource;

    @InjectMocks
    private FreemarkerNotificationTemplateRenderer service;

    @Test
    public void testRenderTemplate() throws IOException, TemplateException {

        NotificationSource from = this.systemNotificationSource;
        NotificationTarget to = new UserNotificationTarget("A User", "user@example.com");

        when(freemarkerConfigurationMock.getTemplate("/path/to/template")).thenReturn(freemarkerTemplateMock);

        ServiceResult<String> renderResult = service.renderTemplate(from, to, "/path/to/template", asMap("arg1", "1", "arg2", 2L));
        assertTrue(renderResult.isSuccess());
        assertEquals("", renderResult.getSuccess());

        Map<String, Object> expectedTemplateArguments = asMap("notificationSource", from, "notificationTarget", to, "arg1", "1", "arg2", 2L);
        verify(freemarkerTemplateMock).process(eq(expectedTemplateArguments), isA(Writer.class));
    }

    @Test
    public void testRenderTemplateButGetTemplateFails() throws IOException {

        NotificationSource from = this.systemNotificationSource;
        NotificationTarget to = new UserNotificationTarget("A User", "user@example.com");

        when(freemarkerConfigurationMock.getTemplate("/path/to/template")).thenThrow(new IllegalArgumentException("no templates!"));

        ServiceResult<String> renderResult = service.renderTemplate(from, to, "/path/to/template", asMap("arg1", "1", "arg2", 2L));
        assertTrue(renderResult.isFailure());
        assertTrue(renderResult.getFailure().is(NOTIFICATIONS_UNABLE_TO_RENDER_TEMPLATE));
    }

    @Test
    public void testRenderTemplateButProcessTemplateThrowsUnhandledException() throws IOException, TemplateException {

        NotificationSource from = this.systemNotificationSource;
        NotificationTarget to = new UserNotificationTarget("A User", "user@example.com");

        when(freemarkerConfigurationMock.getTemplate("/path/to/template")).thenReturn(freemarkerTemplateMock);
        doThrow(new IllegalArgumentException("No processing!")).when(freemarkerTemplateMock).process(isA(Map.class), isA(Writer.class));

        ServiceResult<String> renderResult = service.renderTemplate(from, to, "/path/to/template", asMap("arg1", "1", "arg2", 2L));
        assertTrue(renderResult.isFailure());
        assertTrue(renderResult.getFailure().is(NOTIFICATIONS_UNABLE_TO_RENDER_TEMPLATE));
    }

    @Test
    public void testRenderTemplateButProcessTemplateThrowsHandledException() throws IOException, TemplateException {

        NotificationSource from = this.systemNotificationSource;
        NotificationTarget to = new UserNotificationTarget("A User", "user@example.com");

        when(freemarkerConfigurationMock.getTemplate("/path/to/template")).thenReturn(freemarkerTemplateMock);
        doThrow(new TemplateException("No processing!", null)).when(freemarkerTemplateMock).process(isA(Map.class), isA(Writer.class));

        ServiceResult<String> renderResult = service.renderTemplate(from, to, "/path/to/template", asMap("arg1", "1", "arg2", 2L));
        assertTrue(renderResult.isFailure());
        assertTrue(renderResult.getFailure().is(NOTIFICATIONS_UNABLE_TO_RENDER_TEMPLATE));
    }
}
