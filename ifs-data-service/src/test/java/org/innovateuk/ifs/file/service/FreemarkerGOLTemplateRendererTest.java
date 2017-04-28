package org.innovateuk.ifs.file.service;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.commons.service.ServiceResult;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.junit.Test;
import org.mockito.Mock;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;

import static org.innovateuk.ifs.commons.error.CommonFailureKeys.GRANT_OFFER_LETTER_GENERATION_UNABLE_TO_RENDER_TEMPLATE;
import static org.innovateuk.ifs.util.MapFunctions.asMap;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.*;

/**
 *
 **/
public class FreemarkerGOLTemplateRendererTest extends BaseServiceUnitTest<FileTemplateRenderer> {

    @Mock
    private Configuration freemarkerConfigurationMock;

    @Mock
    private Template freemarkerTemplateMock;

    @Test
    public void testRenderTemplate() throws IOException, TemplateException {

        when(freemarkerConfigurationMock.getTemplate("/path/to/template")).thenReturn(freemarkerTemplateMock);

        ServiceResult<String> renderResult = service.renderTemplate("/path/to/template", asMap("arg1", "1&", "arg2", 2L, "arg3", "3\"", "arg4", "4<", "arg5", "5>", "arg6", "6'"));
        assertTrue(renderResult.isSuccess());
        assertEquals("", renderResult.getSuccessObject());

        Map<String, Object> expectedTemplateArguments = asMap("arg1", "1&", "arg2", 2L, "arg3", "3\"", "arg4", "4<", "arg5", "5>", "arg6", "6'");
        verify(freemarkerTemplateMock).process(eq(expectedTemplateArguments), isA(Writer.class));
    }

    @Test
    public void testRenderTemplateButGetTemplateFails() throws IOException {

        when(freemarkerConfigurationMock.getTemplate("/path/to/template")).thenThrow(new IllegalArgumentException("no templates!"));

        ServiceResult<String> renderResult = service.renderTemplate("/path/to/template", asMap("arg1", "1", "arg2", 2L));
        assertTrue(renderResult.isFailure());
        assertTrue(renderResult.getFailure().is(GRANT_OFFER_LETTER_GENERATION_UNABLE_TO_RENDER_TEMPLATE));
    }

    @Test
    public void testRenderTemplateButProcessTemplateThrowsUnhandledException() throws IOException, TemplateException {


        when(freemarkerConfigurationMock.getTemplate("/path/to/template")).thenReturn(freemarkerTemplateMock);
        doThrow(new IllegalArgumentException("No processing!")).when(freemarkerTemplateMock).process(isA(Map.class), isA(Writer.class));

        ServiceResult<String> renderResult = service.renderTemplate("/path/to/template", asMap("arg1", "1", "arg2", 2L));
        assertTrue(renderResult.isFailure());
        assertTrue(renderResult.getFailure().is(GRANT_OFFER_LETTER_GENERATION_UNABLE_TO_RENDER_TEMPLATE));
    }

    @Test
    public void testRenderTemplateButProcessTemplateThrowsHandledException() throws IOException, TemplateException {

        when(freemarkerConfigurationMock.getTemplate("/path/to/template")).thenReturn(freemarkerTemplateMock);
        doThrow(new TemplateException("No processing!", null)).when(freemarkerTemplateMock).process(isA(Map.class), isA(Writer.class));

        ServiceResult<String> renderResult = service.renderTemplate("/path/to/template", asMap("arg1", "1", "arg2", 2L));
        assertTrue(renderResult.isFailure());
        assertTrue(renderResult.getFailure().is(GRANT_OFFER_LETTER_GENERATION_UNABLE_TO_RENDER_TEMPLATE));
    }

    @Override
    protected FileTemplateRenderer supplyServiceUnderTest() {
        return new FreemarkerGOLTemplateRenderer();
    }
}
