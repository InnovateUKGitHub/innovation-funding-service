package com.worth.ifs.notifications.service;

import com.worth.ifs.BaseServiceUnitTest;
import com.worth.ifs.notifications.resource.ExternalUserNotificationTarget;
import com.worth.ifs.notifications.resource.NotificationSource;
import com.worth.ifs.notifications.resource.NotificationTarget;
import com.worth.ifs.notifications.resource.SystemNotificationSource;
import com.worth.ifs.transactional.ServiceResult;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.junit.Test;
import org.mockito.Mock;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;

import static com.worth.ifs.notifications.service.FreemarkerNotificationTemplateRenderer.ServiceErrors.UNABLE_TO_RENDER_TEMPLATE;
import static com.worth.ifs.util.MapFunctions.asMap;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 *
 */
public class FreemarkerNotificationTemplateRendererTest extends BaseServiceUnitTest<FreemarkerNotificationTemplateRenderer> {

    @Mock
    private Configuration freemarkerConfigurationMock;

    @Mock
    private Template freemarkerTemplateMock;

    @Mock
    private SystemNotificationSource systemNotificationSource;

    @Override
    protected FreemarkerNotificationTemplateRenderer supplyServiceUnderTest() {
        return new FreemarkerNotificationTemplateRenderer();
    }

    @Test
    public void testRenderTemplate() throws IOException, TemplateException {

        NotificationSource from = this.systemNotificationSource;
        NotificationTarget to = new ExternalUserNotificationTarget("A User", "user@example.com");

        when(freemarkerConfigurationMock.getTemplate("/path/to/template")).thenReturn(freemarkerTemplateMock);

        ServiceResult<String> renderResult = service.renderTemplate(from, to, "/path/to/template", asMap("arg1", "1", "arg2", 2L));
        assertTrue(renderResult.isRight());
        assertEquals("", renderResult.getRight());

        Map<String, Object> expectedTemplateArguments = asMap("notificationSource", from, "notificationTarget", to, "arg1", "1", "arg2", 2L);
        verify(freemarkerTemplateMock).process(eq(expectedTemplateArguments), isA(Writer.class));
    }

    @Test
    public void testRenderTemplateButGetTemplateFails() throws IOException {

        NotificationSource from = this.systemNotificationSource;
        NotificationTarget to = new ExternalUserNotificationTarget("A User", "user@example.com");

        when(freemarkerConfigurationMock.getTemplate("/path/to/template")).thenThrow(new IllegalArgumentException("no templates!"));

        ServiceResult<String> renderResult = service.renderTemplate(from, to, "/path/to/template", asMap("arg1", "1", "arg2", 2L));
        assertTrue(renderResult.isLeft());
        assertTrue(renderResult.getLeft().is(UNABLE_TO_RENDER_TEMPLATE));
    }

    @Test
    public void testRenderTemplateButProcessTemplateThrowsUnhandledException() throws IOException, TemplateException {

        NotificationSource from = this.systemNotificationSource;
        NotificationTarget to = new ExternalUserNotificationTarget("A User", "user@example.com");

        when(freemarkerConfigurationMock.getTemplate("/path/to/template")).thenReturn(freemarkerTemplateMock);
        doThrow(new IllegalArgumentException("No processing!")).when(freemarkerTemplateMock).process(isA(Map.class), isA(Writer.class));

        ServiceResult<String> renderResult = service.renderTemplate(from, to, "/path/to/template", asMap("arg1", "1", "arg2", 2L));
        assertTrue(renderResult.isLeft());
        assertTrue(renderResult.getLeft().is(UNABLE_TO_RENDER_TEMPLATE));
    }

    @Test
    public void testRenderTemplateButProcessTemplateThrowsHandledException() throws IOException, TemplateException {

        NotificationSource from = this.systemNotificationSource;
        NotificationTarget to = new ExternalUserNotificationTarget("A User", "user@example.com");

        when(freemarkerConfigurationMock.getTemplate("/path/to/template")).thenReturn(freemarkerTemplateMock);
        doThrow(new TemplateException("No processing!", null)).when(freemarkerTemplateMock).process(isA(Map.class), isA(Writer.class));

        ServiceResult<String> renderResult = service.renderTemplate(from, to, "/path/to/template", asMap("arg1", "1", "arg2", 2L));
        assertTrue(renderResult.isLeft());
        assertTrue(renderResult.getLeft().is(UNABLE_TO_RENDER_TEMPLATE));
    }
}
