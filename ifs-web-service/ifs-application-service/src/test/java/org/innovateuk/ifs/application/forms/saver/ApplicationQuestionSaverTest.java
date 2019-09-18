package org.innovateuk.ifs.application.forms.saver;

import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.ApplicationService;
import org.innovateuk.ifs.application.service.QuestionRestService;
import org.innovateuk.ifs.application.service.QuestionService;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.error.ValidationMessages;
import org.innovateuk.ifs.filter.CookieFlashMessageFilter;
import org.innovateuk.ifs.form.ApplicationForm;
import org.innovateuk.ifs.user.service.UserRestService;
import org.innovateuk.ifs.user.service.UserService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.util.Optional;

import static java.util.Arrays.asList;
import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.application.forms.ApplicationFormUtil.MARK_AS_COMPLETE;
import static org.innovateuk.ifs.application.forms.ApplicationFormUtil.MARK_AS_INCOMPLETE;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.form.builder.QuestionResourceBuilder.newQuestionResource;
import static org.innovateuk.ifs.user.builder.ProcessRoleResourceBuilder.newProcessRoleResource;
import static org.innovateuk.ifs.util.MapFunctions.asMap;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

/**
 * Tests {@link ApplicationQuestionSaver}
 */
@RunWith(MockitoJUnitRunner.Silent.class)
public class ApplicationQuestionSaverTest {

    @InjectMocks
    private ApplicationQuestionSaver questionSaver;

    @Mock
    private UserRestService userRestService;

    @Mock
    private ApplicationService applicationService;

    @Mock
    private UserService userService;

    @Mock
    private QuestionService questionService;

    @Mock
    private QuestionRestService questionRestService;

    @Mock
    private CookieFlashMessageFilter cookieFlashMessageFilter;

    @Mock
    private ApplicationQuestionFileSaver fileSaver;

    @Mock
    private ApplicationQuestionNonFileSaver nonFileSaver;

    private final Long applicationId = 125123L;
    private final Long userId = 94993L;
    private final Long questionId = 12942L;
    private final Long processRoleId = 34123L;
    private final ApplicationResource application = newApplicationResource().withId(applicationId).build();

    private ApplicationForm form = new ApplicationForm();
    private final HttpServletRequest request = mock(HttpServletRequest.class);
    private final HttpServletResponse response = mock(HttpServletResponse.class);

    @Before
    public void setup() {
        application.setResubmission(false);
        form.setApplication(application);

        when(userRestService.findProcessRole(userId, applicationId)).thenReturn(restSuccess(newProcessRoleResource().withId(processRoleId).build()));
        when(applicationService.getById(applicationId)).thenReturn(application);
        when(questionRestService.findById(questionId)).thenReturn(restSuccess(newQuestionResource().withId(questionId).build()));
        when(userService.isLeadApplicant(userId, application)).thenReturn(Boolean.FALSE);
    }

    @Test
    public void saveApplicationForm_MarkQuestionAsIncomplete() {
        when(request.getParameterMap()).thenReturn(asMap(MARK_AS_INCOMPLETE, new String[]{"1"}));
        when(request.getParameter(MARK_AS_INCOMPLETE)).thenReturn("1");

        ValidationMessages result = questionSaver.saveApplicationForm(applicationId, form, questionId, userId, request, response, Optional.empty());

        assertFalse(result.hasErrors());
        verify(fileSaver, never()).saveFileUploadQuestionsIfAny(anyList(), anyMap(), any(HttpServletRequest.class), anyLong(), anyLong());
        verify(nonFileSaver, never()).saveNonFileUploadQuestions(anyList(), any(HttpServletRequest.class), anyLong(), anyLong(), anyBoolean());
    }

    @Test
    public void saveApplicationForm_MarkQuestionAsCompleteAndErrors() {
        when(request.getParameterMap()).thenReturn(asMap(MARK_AS_COMPLETE, new String[]{}));
        when(questionRestService.findById(anyLong())).thenReturn(restSuccess(newQuestionResource().build()));

        ValidationMessages messages = new ValidationMessages();
        messages.addError(new Error("Random One", HttpStatus.BAD_REQUEST));

        when(fileSaver.saveFileUploadQuestionsIfAny(anyList(), anyMap(), any(HttpServletRequest.class), anyLong(), anyLong())).thenReturn(messages);
        when(nonFileSaver.saveNonFileUploadQuestions(anyList(), any(HttpServletRequest.class), anyLong(), anyLong(), anyBoolean())).thenReturn(new ValidationMessages());

        ValidationMessages result = questionSaver.saveApplicationForm(applicationId, form, questionId, userId, request, response, Optional.empty());

        assertTrue(result.hasErrors());
        verify(fileSaver, times(1)).saveFileUploadQuestionsIfAny(anyList(), anyMap(), any(HttpServletRequest.class), anyLong(), anyLong());
        verify(nonFileSaver, times(1)).saveNonFileUploadQuestions(anyList(), any(HttpServletRequest.class), anyLong(), anyLong(), anyBoolean());
    }

    @Test
    public void saveApplicationForm_MarkQuestionAsCompleteWithOverridingAndErrors() {
        when(request.getParameterMap()).thenReturn(asMap());
        when(questionRestService.findById(anyLong())).thenReturn(restSuccess(newQuestionResource().build()));

        ValidationMessages messages = new ValidationMessages();
        messages.addError(new Error("Random One", HttpStatus.BAD_REQUEST));

        when(fileSaver.saveFileUploadQuestionsIfAny(anyList(), anyMap(), any(HttpServletRequest.class), anyLong(), anyLong())).thenReturn(messages);
        when(nonFileSaver.saveNonFileUploadQuestions(anyList(), any(HttpServletRequest.class), anyLong(), anyLong(), anyBoolean())).thenReturn(new ValidationMessages());

        ValidationMessages result = questionSaver.saveApplicationForm(applicationId, form, questionId, userId, request, response, Optional.of(Boolean.TRUE));

        assertTrue(result.hasErrors());
        verify(fileSaver, times(1)).saveFileUploadQuestionsIfAny(anyList(), anyMap(), any(HttpServletRequest.class), anyLong(), anyLong());
        verify(nonFileSaver, times(1)).saveNonFileUploadQuestions(anyList(), any(HttpServletRequest.class), anyLong(), anyLong(), anyBoolean());
    }

    @Test
    public void saveApplicationForm_UserIsLead() {
        when(userService.isLeadApplicant(userId, application)).thenReturn(true);

        ValidationMessages result = questionSaver.saveApplicationForm(applicationId, form, questionId, userId, request, response, Optional.empty());

        assertFalse(result.hasErrors());
        verify(applicationService, times(1)).save(any(ApplicationResource.class));
    }

    @Test
    public void saveApplicationForm_MarkQuestionComplete() {
        when(request.getParameterMap()).thenReturn(asMap(MARK_AS_COMPLETE, new String[]{}));
        when(request.getParameter(MARK_AS_COMPLETE)).thenReturn(String.valueOf(questionId));
        when(questionService.markAsComplete(questionId, applicationId, processRoleId)).thenReturn(Collections.emptyList());

        ValidationMessages result = questionSaver.saveApplicationForm(applicationId, form, questionId, userId, request, response, Optional.empty());

        assertFalse(result.hasErrors());
        verify(questionService, times(1)).markAsComplete(questionId, applicationId, processRoleId);
        verify(questionService, never()).markAsIncomplete(questionId, applicationId, processRoleId);
    }

    @Test
    public void saveApplicationForm_MarkQuestionCompleteWithErrors() {
        when(request.getParameterMap()).thenReturn(asMap(MARK_AS_COMPLETE, new String[]{}));
        when(request.getParameter(MARK_AS_COMPLETE)).thenReturn(String.valueOf(questionId));
        ValidationMessages messages = new ValidationMessages();
        messages.addError(new Error("RandomKey", HttpStatus.BAD_REQUEST));
        when(questionService.markAsComplete(questionId, applicationId, processRoleId)).thenReturn(asList(messages));

        ValidationMessages result = questionSaver.saveApplicationForm(applicationId, form, questionId, userId, request, response, Optional.empty());

        assertTrue(result.hasErrors());
        verify(questionService, times(1)).markAsComplete(questionId, applicationId, processRoleId);
        verify(questionService, times(1)).markAsIncomplete(questionId, applicationId, processRoleId);

    }


}
