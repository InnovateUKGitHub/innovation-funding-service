package org.innovateuk.ifs.application.forms.saver;

import org.innovateuk.ifs.application.resource.QuestionResource;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.rest.ValidationMessages;
import org.innovateuk.ifs.form.resource.FormInputResource;
import org.innovateuk.ifs.form.resource.FormInputScope;
import org.innovateuk.ifs.form.resource.FormInputType;
import org.innovateuk.ifs.form.service.FormInputResponseRestService;
import org.innovateuk.ifs.form.service.FormInputRestService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.List;

import static java.util.Arrays.asList;
import static org.innovateuk.ifs.application.builder.QuestionResourceBuilder.newQuestionResource;
import static org.innovateuk.ifs.form.builder.FormInputResourceBuilder.newFormInputResource;
import static org.innovateuk.ifs.util.MapFunctions.asMap;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.*;

/**
 * Tests {@link ApplicationQuestionNonFileSaver}
 */
@RunWith(MockitoJUnitRunner.class)
public class ApplicationQuestionNonFileSaverTest {

    @InjectMocks
    private ApplicationQuestionNonFileSaver nonFileSaver;

    @Mock
    private FormInputResponseRestService formInputResponseRestService;

    @Mock
    private FormInputRestService formInputRestService;

    private final Long userId = 5L;
    private final Long applicationId = 2434L;

    @Test
    public void saveNonFileUploadQuestions_filterOneInput() {
        final HttpServletRequest request = mock(HttpServletRequest.class);
        final String value = "01-02-2017";
        when(request.getParameterMap()).thenReturn(asMap("formInput[1000]", new String[]{value}));
        final List<QuestionResource> questions = newQuestionResource().withId(1L).build(1);
        final List<FormInputResource> formInputList = asList(
                newFormInputResource().withType(FormInputType.FILEUPLOAD).build(),
                newFormInputResource().withId(1000L)
                        .withType(FormInputType.DATE).build());
        when(formInputRestService.getByQuestionIdAndScope(anyLong(), eq(FormInputScope.APPLICATION))).thenReturn(RestResult.restSuccess(formInputList));
        when(formInputResponseRestService.saveQuestionResponse(userId, applicationId, 1000L, value, true)).thenReturn(RestResult.restSuccess(new ValidationMessages()));

        ValidationMessages result = nonFileSaver.saveNonFileUploadQuestions(questions, request, userId, applicationId, true);

        assertTrue(!result.hasErrors());
        verify(formInputResponseRestService, times(1)).saveQuestionResponse(userId, applicationId, 1000L, value, true);
    }

    @Test
    public void saveNonFileUploadQuestions_NoInputs() {
        final HttpServletRequest request = mock(HttpServletRequest.class);
        final List<QuestionResource> questions = newQuestionResource().withId(1L).build(1);
        final List<FormInputResource> formInputList = Collections.emptyList();
        when(formInputRestService.getByQuestionIdAndScope(anyLong(), eq(FormInputScope.APPLICATION))).thenReturn(RestResult.restSuccess(formInputList));

        ValidationMessages result = nonFileSaver.saveNonFileUploadQuestions(questions, request, userId, applicationId, true);

        assertTrue(!result.hasErrors());
        verify(formInputResponseRestService, never()).saveQuestionResponse(anyLong(), anyLong(), anyLong(), anyString(), anyBoolean());
    }

    @Test
    public void saveNonFileUploadQuestions_AllInputsFiltered() {
        final HttpServletRequest request = mock(HttpServletRequest.class);
        final String value = "01-02-2017";
        when(request.getParameterMap()).thenReturn(asMap("formInput[1000]", new String[]{value}));
        final List<QuestionResource> questions = newQuestionResource().withId(1L).build(1);
        final List<FormInputResource> formInputList = asList(
                newFormInputResource().withType(FormInputType.FILEUPLOAD).build(),
                newFormInputResource().withId(1000L)
                        .withType(FormInputType.FILEUPLOAD).build());
        when(formInputRestService.getByQuestionIdAndScope(anyLong(), eq(FormInputScope.APPLICATION))).thenReturn(RestResult.restSuccess(formInputList));
        when(formInputResponseRestService.saveQuestionResponse(userId, applicationId, 1000L, value, true)).thenReturn(RestResult.restSuccess(new ValidationMessages()));

        ValidationMessages result = nonFileSaver.saveNonFileUploadQuestions(questions, request, userId, applicationId, true);

        assertTrue(!result.hasErrors());
        verify(formInputResponseRestService, never()).saveQuestionResponse(anyLong(), anyLong(), anyLong(), anyString(), anyBoolean());
    }

    @Test
    public void saveNonFileUploadQuestions_NoParamValue() {
        final HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getParameterMap()).thenReturn(asMap());
        final List<QuestionResource> questions = newQuestionResource().withId(1L).build(1);
        final List<FormInputResource> formInputList = asList(
                newFormInputResource().withType(FormInputType.FILEUPLOAD).build(),
                newFormInputResource().withId(1000L)
                        .withType(FormInputType.FILEUPLOAD).build());
        when(formInputRestService.getByQuestionIdAndScope(anyLong(), eq(FormInputScope.APPLICATION))).thenReturn(RestResult.restSuccess(formInputList));

        ValidationMessages result = nonFileSaver.saveNonFileUploadQuestions(questions, request, userId, applicationId, true);

        assertTrue(!result.hasErrors());
        verify(formInputResponseRestService, never()).saveQuestionResponse(anyLong(), anyLong(), anyLong(), anyString(), anyBoolean());
    }
}
