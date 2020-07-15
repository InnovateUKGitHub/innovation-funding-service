package org.innovateuk.ifs.application.forms.questions.generic.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.applicant.resource.ApplicantQuestionResource;
import org.innovateuk.ifs.applicant.service.ApplicantRestService;
import org.innovateuk.ifs.application.forms.questions.generic.form.GenericQuestionApplicationForm;
import org.innovateuk.ifs.application.forms.questions.generic.populator.GenericQuestionApplicationFormPopulator;
import org.innovateuk.ifs.application.forms.questions.generic.populator.GenericQuestionApplicationModelPopulator;
import org.innovateuk.ifs.application.forms.questions.generic.validator.GenericQuestionApplicationFormValidator;
import org.innovateuk.ifs.application.forms.questions.generic.viewmodel.GenericQuestionApplicationViewModel;
import org.innovateuk.ifs.application.resource.FormInputResponseResource;
import org.innovateuk.ifs.application.service.QuestionStatusRestService;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.filter.CookieFlashMessageFilter;
import org.innovateuk.ifs.form.resource.FormInputResource;
import org.innovateuk.ifs.form.service.FormInputResponseRestService;
import org.innovateuk.ifs.form.service.FormInputRestService;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.service.UserRestService;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;

import javax.servlet.http.HttpServletResponse;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.application.builder.FormInputResponseResourceBuilder.newFormInputResponseResource;
import static org.innovateuk.ifs.commons.error.ValidationMessages.noErrors;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.form.builder.FormInputResourceBuilder.newFormInputResource;
import static org.innovateuk.ifs.form.resource.FormInputScope.APPLICATION;
import static org.innovateuk.ifs.form.resource.FormInputType.*;
import static org.innovateuk.ifs.user.builder.ProcessRoleResourceBuilder.newProcessRoleResource;
import static org.innovateuk.ifs.user.resource.Role.COLLABORATOR;
import static org.innovateuk.ifs.user.resource.Role.LEADAPPLICANT;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class GenericQuestionApplicationControllerTest extends BaseControllerMockMVCTest<GenericQuestionApplicationController> {
    @Mock
    private ApplicantRestService applicantRestService;

    @Mock
    private GenericQuestionApplicationModelPopulator modelPopulator;

    @Mock
    private GenericQuestionApplicationFormPopulator formPopulator;

    @Mock
    private FormInputRestService formInputRestService;

    @Mock
    private FormInputResponseRestService formInputResponseRestService;

    @Mock
    private UserRestService userRestService;

    @Mock
    private QuestionStatusRestService questionStatusRestService;

    @Mock
    private CookieFlashMessageFilter cookieFlashMessageFilter;

    @Mock
    private GenericQuestionApplicationFormValidator validator;

    private long applicationId = 1L;
    private long questionId = 2L;

    @Override
    protected GenericQuestionApplicationController supplyControllerUnderTest() {
        return new GenericQuestionApplicationController();
    }

    @Test
    public void viewPage() throws Exception {

        GenericQuestionApplicationViewModel viewModel = mock(GenericQuestionApplicationViewModel.class);
        ApplicantQuestionResource applicantQuestion = mock(ApplicantQuestionResource.class);
        when(applicantRestService.getQuestion(loggedInUser.getId(), applicationId, questionId)).thenReturn(applicantQuestion);
        when(modelPopulator.populate(applicantQuestion)).thenReturn(viewModel);

        mockMvc.perform(get("/application/{applicationId}/form/question/{questionId}/generic", applicationId, questionId))
                .andExpect(view().name("application/questions/generic"))
                .andExpect(model().attribute("model", viewModel));

        verify(formPopulator).populate(any(), eq(applicantQuestion));
    }

    @Test
    public void showErrors() throws Exception {

        FormInputResource formInput = newFormInputResource()
                .withType(TEMPLATE_DOCUMENT)
                .build();

        FormInputResponseResource response = newFormInputResponseResource()
                .build();

        when(formInputRestService.getByQuestionIdAndScope(questionId, APPLICATION)).thenReturn(restSuccess(singletonList(formInput)));
        when(formInputResponseRestService.getByFormInputIdAndApplication(formInput.getId(), applicationId)).thenReturn(restSuccess(singletonList(response)));
        GenericQuestionApplicationViewModel viewModel = mock(GenericQuestionApplicationViewModel.class);
        ApplicantQuestionResource applicantQuestion = mock(ApplicantQuestionResource.class);
        when(applicantRestService.getQuestion(loggedInUser.getId(), applicationId, questionId)).thenReturn(applicantQuestion);
        when(modelPopulator.populate(applicantQuestion)).thenReturn(viewModel);

        mockMvc.perform(get("/application/{applicationId}/form/question/{questionId}/generic?show-errors=true", applicationId, questionId))
                .andExpect(view().name("application/questions/generic"))
                .andExpect(model().attribute("model", viewModel))
                .andExpect(model().attributeHasFieldErrorCode("form", "templateDocument", "validation.file.required"));

        verify(formPopulator).populate(any(), eq(applicantQuestion));
        verify(validator).validate(any(), any());

    }

    @Test
    public void assignToLeadForReview() throws Exception {

        ProcessRoleResource userProcessRole = newProcessRoleResource()
                .withRole(COLLABORATOR)
                .withUser(loggedInUser)
                .build();
        ProcessRoleResource leadProcessRole = newProcessRoleResource()
                .withRole(LEADAPPLICANT)
                .build();

        when(userRestService.findProcessRole(applicationId)).thenReturn(restSuccess(asList(leadProcessRole, userProcessRole)));
        when(userRestService.findProcessRole(loggedInUser.getId(), applicationId)).thenReturn(restSuccess(userProcessRole));
        when(questionStatusRestService.assign(questionId, applicationId, leadProcessRole.getId(), userProcessRole.getId())).thenReturn(restSuccess());

        mockMvc.perform(post("/application/{applicationId}/form/question/{questionId}/generic", applicationId, questionId)
                .param("assign", "true"))
                .andExpect(redirectedUrl(String.format("/application/%d/form/question/%d/generic", applicationId, questionId)));

        verify(questionStatusRestService).assign(questionId, applicationId, leadProcessRole.getId(), userProcessRole.getId());
        verify(cookieFlashMessageFilter).setFlashMessage(any(HttpServletResponse.class), eq("assignedQuestion"));
        verifyNoMoreInteractions(questionStatusRestService);
    }

    @Test
    public void complete() throws Exception {

        ProcessRoleResource userProcessRole = newProcessRoleResource()
                .withRole(COLLABORATOR)
                .withUser(loggedInUser)
                .build();
        FormInputResource formInput = newFormInputResource()
                .withType(TEXTAREA)
                .build();

        when(formInputRestService.getByQuestionIdAndScope(questionId, APPLICATION)).thenReturn(restSuccess(singletonList(formInput)));
        when(userRestService.findProcessRole(loggedInUser.getId(), applicationId)).thenReturn(restSuccess(userProcessRole));
        when(formInputResponseRestService.saveQuestionResponse(loggedInUser.getId(), applicationId, formInput.getId(), "answer", null, false)).thenReturn(restSuccess(noErrors()));
        when(questionStatusRestService.markAsComplete(questionId, applicationId, userProcessRole.getId())).thenReturn(restSuccess(emptyList()));

        mockMvc.perform(post("/application/{applicationId}/form/question/{questionId}/generic", applicationId, questionId)
                .param("complete", "true")
                .param("answer", "answer")
                .param("textAreaActive", "true")
                .param("MultipleChoiceOptionsActive", "false"))
                .andExpect(redirectedUrl(String.format("/application/%d/form/question/%d/generic", applicationId, questionId)));

        verify(formInputResponseRestService).saveQuestionResponse(loggedInUser.getId(), applicationId, formInput.getId(), "answer", null, false);
        verify(questionStatusRestService).markAsComplete(questionId, applicationId, userProcessRole.getId());
        verify(validator).validate(any(), any());
        verifyNoMoreInteractions(questionStatusRestService);
    }

    @Test
    public void completeMultipleChoiceOptions() throws Exception {

        ProcessRoleResource userProcessRole = newProcessRoleResource()
                .withRole(COLLABORATOR)
                .withUser(loggedInUser)
                .build();
        FormInputResource formInput = newFormInputResource()
                .withType(MULTIPLE_CHOICE)
                .build();

        when(formInputRestService.getByQuestionIdAndScope(questionId, APPLICATION)).thenReturn(restSuccess(singletonList(formInput)));
        when(userRestService.findProcessRole(loggedInUser.getId(), applicationId)).thenReturn(restSuccess(userProcessRole));
        when(formInputResponseRestService.saveQuestionResponse(loggedInUser.getId(), applicationId, formInput.getId(), "Yes", 1L, false)).thenReturn(restSuccess(noErrors()));
        when(questionStatusRestService.markAsComplete(questionId, applicationId, userProcessRole.getId())).thenReturn(restSuccess(emptyList()));

        mockMvc.perform(post("/application/{applicationId}/form/question/{questionId}/generic", applicationId, questionId)
                .param("complete", "true")
                .param("answer", "Yes")
                .param("textAreaActive", "false")
                .param("multipleChoiceOptionId", "1")
                .param("multipleChoiceOptionsActive", "true"))
                .andExpect(redirectedUrl(String.format("/application/%d/form/question/%d/generic", applicationId, questionId)));

        verify(formInputResponseRestService).saveQuestionResponse(loggedInUser.getId(), applicationId, formInput.getId(), "Yes", 1L, false);
        verify(questionStatusRestService).markAsComplete(questionId, applicationId, userProcessRole.getId());
        verify(validator).validate(any(), any());
        verifyNoMoreInteractions(questionStatusRestService);
    }

    @Test
    public void edit() throws Exception {

        ProcessRoleResource userProcessRole = newProcessRoleResource()
                .withRole(COLLABORATOR)
                .withUser(loggedInUser)
                .build();

        when(userRestService.findProcessRole(loggedInUser.getId(), applicationId)).thenReturn(restSuccess(userProcessRole));
        when(questionStatusRestService.markAsInComplete(questionId, applicationId, userProcessRole.getId())).thenReturn(restSuccess());

        mockMvc.perform(post("/application/{applicationId}/form/question/{questionId}/generic", applicationId, questionId)
                .param("edit", "true"))
                .andExpect(redirectedUrl(String.format("/application/%d/form/question/%d/generic", applicationId, questionId)));

        verify(questionStatusRestService).markAsInComplete(questionId, applicationId, userProcessRole.getId());
        verifyNoMoreInteractions(questionStatusRestService);
    }

    @Test
    public void uploadTemplateDocument() throws Exception {

        GenericQuestionApplicationViewModel viewModel = mock(GenericQuestionApplicationViewModel.class);
        ApplicantQuestionResource applicantQuestion = mock(ApplicantQuestionResource.class);
        when(applicantRestService.getQuestion(loggedInUser.getId(), applicationId, questionId)).thenReturn(applicantQuestion);
        when(modelPopulator.populate(applicantQuestion)).thenReturn(viewModel);

        ProcessRoleResource userProcessRole = newProcessRoleResource()
                .withRole(COLLABORATOR)
                .withUser(loggedInUser)
                .build();

        FormInputResource formInput = newFormInputResource()
                .withType(TEMPLATE_DOCUMENT)
                .build();

        when(formInputRestService.getByQuestionIdAndScope(questionId, APPLICATION)).thenReturn(restSuccess(singletonList(formInput)));
        when(userRestService.findProcessRole(loggedInUser.getId(), applicationId)).thenReturn(restSuccess(userProcessRole));

        MockMultipartFile file = new MockMultipartFile("templateDocument", "testFile.pdf", "application/pdf", "My content!".getBytes());

        when(formInputResponseRestService.createFileEntry(formInput.getId(),
                applicationId,
                userProcessRole.getId(),
                "application/pdf",11, "testFile.pdf", "My content!".getBytes()))
                .thenReturn(restSuccess(mock(FileEntryResource.class)));

        mockMvc.perform(multipart("/application/{applicationId}/form/question/{questionId}/generic", applicationId, questionId)
                .file(file)
                .param("uploadTemplateDocument", "true"))
                .andExpect(view().name("application/questions/generic"));

        verify(formInputResponseRestService).createFileEntry(formInput.getId(),
                applicationId,
                userProcessRole.getId(),
                "application/pdf",11, "testFile.pdf", "My content!".getBytes());
        verifyNoMoreInteractions(formInputResponseRestService);
    }

    @Test
    public void removeTemplateDocument() throws Exception {
        long fileEntryId = 10L;

        GenericQuestionApplicationViewModel viewModel = mock(GenericQuestionApplicationViewModel.class);
        ApplicantQuestionResource applicantQuestion = mock(ApplicantQuestionResource.class);
        when(applicantRestService.getQuestion(loggedInUser.getId(), applicationId, questionId)).thenReturn(applicantQuestion);
        when(modelPopulator.populate(applicantQuestion)).thenReturn(viewModel);

        ProcessRoleResource userProcessRole = newProcessRoleResource()
                .withRole(COLLABORATOR)
                .withUser(loggedInUser)
                .build();

        FormInputResource formInput = newFormInputResource()
                .withType(TEMPLATE_DOCUMENT)
                .build();

        when(formInputRestService.getByQuestionIdAndScope(questionId, APPLICATION)).thenReturn(restSuccess(singletonList(formInput)));
        when(userRestService.findProcessRole(loggedInUser.getId(), applicationId)).thenReturn(restSuccess(userProcessRole));

        when(formInputResponseRestService.removeFileEntry(formInput.getId(),
                applicationId,
                userProcessRole.getId(),
                fileEntryId)).thenReturn(restSuccess());

        mockMvc.perform(post("/application/{applicationId}/form/question/{questionId}/generic", applicationId, questionId)
                .param("removeTemplateDocument", String.valueOf(fileEntryId)))
                .andExpect(view().name("application/questions/generic"));

        verify(formInputResponseRestService).removeFileEntry(formInput.getId(),
                applicationId,
                userProcessRole.getId(),
                fileEntryId);
        verifyNoMoreInteractions(formInputResponseRestService);
    }

    @Test
    public void uploadAppendix() throws Exception {

        GenericQuestionApplicationViewModel viewModel = mock(GenericQuestionApplicationViewModel.class);
        ApplicantQuestionResource applicantQuestion = mock(ApplicantQuestionResource.class);
        when(applicantRestService.getQuestion(loggedInUser.getId(), applicationId, questionId)).thenReturn(applicantQuestion);
        when(modelPopulator.populate(applicantQuestion)).thenReturn(viewModel);

        ProcessRoleResource userProcessRole = newProcessRoleResource()
                .withRole(COLLABORATOR)
                .withUser(loggedInUser)
                .build();

        FormInputResource formInput = newFormInputResource()
                .withType(FILEUPLOAD)
                .build();

        when(formInputRestService.getByQuestionIdAndScope(questionId, APPLICATION)).thenReturn(restSuccess(singletonList(formInput)));
        when(userRestService.findProcessRole(loggedInUser.getId(), applicationId)).thenReturn(restSuccess(userProcessRole));

        MockMultipartFile file = new MockMultipartFile("appendix", "testFile.pdf", "application/pdf", "My content!".getBytes());

        when(formInputResponseRestService.createFileEntry(formInput.getId(),
                applicationId,
                userProcessRole.getId(),
                "application/pdf",11, "testFile.pdf", "My content!".getBytes()))
                .thenReturn(restSuccess(mock(FileEntryResource.class)));

        mockMvc.perform(multipart("/application/{applicationId}/form/question/{questionId}/generic", applicationId, questionId)
                .file(file)
                .param("uploadAppendix", "true"))
                .andExpect(view().name("application/questions/generic"));

        verify(formInputResponseRestService).createFileEntry(formInput.getId(),
                applicationId,
                userProcessRole.getId(),
                "application/pdf",11, "testFile.pdf", "My content!".getBytes());
        verifyNoMoreInteractions(formInputResponseRestService);
    }

    @Test
    public void removeAppendix() throws Exception {
        long fileEntryId = 10L;

        GenericQuestionApplicationViewModel viewModel = mock(GenericQuestionApplicationViewModel.class);
        ApplicantQuestionResource applicantQuestion = mock(ApplicantQuestionResource.class);
        when(applicantRestService.getQuestion(loggedInUser.getId(), applicationId, questionId)).thenReturn(applicantQuestion);
        when(modelPopulator.populate(applicantQuestion)).thenReturn(viewModel);

        ProcessRoleResource userProcessRole = newProcessRoleResource()
                .withRole(COLLABORATOR)
                .withUser(loggedInUser)
                .build();

        FormInputResource formInput = newFormInputResource()
                .withType(FILEUPLOAD)
                .build();

        when(formInputRestService.getByQuestionIdAndScope(questionId, APPLICATION)).thenReturn(restSuccess(singletonList(formInput)));
        when(userRestService.findProcessRole(loggedInUser.getId(), applicationId)).thenReturn(restSuccess(userProcessRole));

        when(formInputResponseRestService.removeFileEntry(formInput.getId(),
                applicationId,
                userProcessRole.getId(),
                fileEntryId)).thenReturn(restSuccess());

        mockMvc.perform(post("/application/{applicationId}/form/question/{questionId}/generic", applicationId, questionId)
                .param("removeAppendix", String.valueOf(fileEntryId)))
                .andExpect(view().name("application/questions/generic"));

        verify(formInputResponseRestService).removeFileEntry(formInput.getId(),
                applicationId,
                userProcessRole.getId(),
                fileEntryId);
        verifyNoMoreInteractions(formInputResponseRestService);
    }
}
