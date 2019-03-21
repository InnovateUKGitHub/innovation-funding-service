package org.innovateuk.ifs.application.forms.questions.grantagreement.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.application.forms.questions.grantagreement.model.GrantAgreementViewModel;
import org.innovateuk.ifs.application.forms.questions.grantagreement.populator.GrantAgreementViewModelPopulator;
import org.innovateuk.ifs.application.service.QuestionStatusRestService;
import org.innovateuk.ifs.commons.error.CommonErrors;
import org.innovateuk.ifs.granttransfer.service.EuGrantTransferRestService;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.service.UserRestService;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockMultipartFile;

import java.util.Collections;

import static org.innovateuk.ifs.commons.rest.RestResult.restFailure;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.file.builder.FileEntryResourceBuilder.newFileEntryResource;
import static org.innovateuk.ifs.user.builder.ProcessRoleResourceBuilder.newProcessRoleResource;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class GrantAgreementControllerTest extends BaseControllerMockMVCTest<GrantAgreementController> {

    @Mock
    private GrantAgreementViewModelPopulator grantAgreementViewModelPopulator;

    @Mock
    private EuGrantTransferRestService euGrantTransferRestService;

    @Mock
    private QuestionStatusRestService questionStatusRestService;

    @Mock
    private UserRestService userRestService;

    @Override
    protected GrantAgreementController supplyControllerUnderTest() {
        return new GrantAgreementController(grantAgreementViewModelPopulator, euGrantTransferRestService, questionStatusRestService, userRestService);
    }

    @Test
    public void viewGrantAgreement() throws Exception {
        long questionId = 1L;
        long applicationId = 2L;

        GrantAgreementViewModel viewModel = mock(GrantAgreementViewModel.class);
        when(grantAgreementViewModelPopulator.populate(applicationId, questionId, loggedInUser.getId())).thenReturn(viewModel);
        mockMvc.perform(
                get("/application/{applicationId}/form/question/{questionId}/grant-agreement", applicationId, questionId))
                .andExpect(status().isOk())
                .andExpect(view().name("application/questions/grant-agreement"))
                .andReturn();
    }

    @Test
    public void saveAndReturn() throws Exception {
        long questionId = 1L;
        long applicationId = 2L;

        GrantAgreementViewModel viewModel = mock(GrantAgreementViewModel.class);
        when(grantAgreementViewModelPopulator.populate(applicationId, questionId, loggedInUser.getId())).thenReturn(viewModel);
        mockMvc.perform(
                post("/application/{applicationId}/form/question/{questionId}/grant-agreement", applicationId, questionId))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(String.format("/application/%d", applicationId)))
                .andReturn();
    }

    @Test
    public void markAsComplete() throws Exception {
        long questionId = 1L;
        long applicationId = 2L;

        ProcessRoleResource role = newProcessRoleResource().build();
        when(euGrantTransferRestService.findGrantAgreement(applicationId)).thenReturn(restSuccess(newFileEntryResource().build()));
        when(userRestService.findProcessRole(loggedInUser.getId(), applicationId)).thenReturn(restSuccess(role));
        when(questionStatusRestService.markAsComplete(questionId, applicationId, role.getId())).thenReturn(restSuccess(Collections.emptyList()));

        mockMvc.perform(
                post("/application/{applicationId}/form/question/{questionId}/grant-agreement", applicationId, questionId)
                .param("complete", "true"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(String.format("/application/%d/form/question/%d/grant-agreement", applicationId, questionId)))
                .andReturn();

        verify(questionStatusRestService).markAsComplete(questionId, applicationId, role.getId());
    }

    @Test
    public void markAsComplete_missingFile() throws Exception {
        long questionId = 1L;
        long applicationId = 2L;

        GrantAgreementViewModel viewModel = mock(GrantAgreementViewModel.class);
        when(grantAgreementViewModelPopulator.populate(applicationId, questionId, loggedInUser.getId())).thenReturn(viewModel);
        when(euGrantTransferRestService.findGrantAgreement(applicationId)).thenReturn(restFailure(Collections.emptyList(), HttpStatus.NOT_FOUND));

        mockMvc.perform(
                post("/application/{applicationId}/form/question/{questionId}/grant-agreement", applicationId, questionId)
                        .param("complete", "true"))
                .andExpect(status().isOk())
                .andExpect(view().name("application/questions/grant-agreement"))
                .andReturn();

        verifyZeroInteractions(questionStatusRestService);
    }

    @Test
    public void edit() throws Exception {
        long questionId = 1L;
        long applicationId = 2L;

        ProcessRoleResource role = newProcessRoleResource().build();
        GrantAgreementViewModel viewModel = mock(GrantAgreementViewModel.class);
        when(grantAgreementViewModelPopulator.populate(applicationId, questionId, loggedInUser.getId())).thenReturn(viewModel);
        when(userRestService.findProcessRole(loggedInUser.getId(), applicationId)).thenReturn(restSuccess(role));
        when(questionStatusRestService.markAsInComplete(questionId, applicationId, role.getId())).thenReturn(restSuccess());

        mockMvc.perform(
                post("/application/{applicationId}/form/question/{questionId}/grant-agreement", applicationId, questionId)
                        .param("edit", "true"))
                .andExpect(status().isOk())
                .andExpect(view().name("application/questions/grant-agreement"))
                .andReturn();

        verify(questionStatusRestService).markAsInComplete(questionId, applicationId, role.getId());
    }

    @Test
    public void uploadFeedback() throws Exception {
        long questionId = 1L;
        long applicationId = 2L;

        when(euGrantTransferRestService.uploadGrantAgreement(applicationId, "application/pdf", 11, "testFile.pdf", "My content!".getBytes()))
                .thenReturn(restFailure(CommonErrors.payloadTooLargeError(1)));

        MockMultipartFile file = new MockMultipartFile("grantAgreement", "testFile.pdf", "application/pdf", "My content!".getBytes());

        mockMvc.perform(
                multipart("/application/{applicationId}/form/question/{questionId}/grant-agreement", applicationId, questionId)
                        .file(file)
                        .param("uploadGrantAgreement", "true"))
                .andExpect(status().isOk())
                .andExpect(view().name("application/questions/grant-agreement"))
                .andReturn();

        verify(euGrantTransferRestService).uploadGrantAgreement(applicationId, "application/pdf", 11, "testFile.pdf", "My content!".getBytes());
    }

    @Test
    public void removeGrantAgreement() throws Exception {
        long questionId = 1L;
        long applicationId = 2L;

        GrantAgreementViewModel viewModel = mock(GrantAgreementViewModel.class);
        when(grantAgreementViewModelPopulator.populate(applicationId, questionId, loggedInUser.getId())).thenReturn(viewModel);
        when(euGrantTransferRestService.deleteGrantAgreement(applicationId)).thenReturn(restSuccess());

        mockMvc.perform(
                post("/application/{applicationId}/form/question/{questionId}/grant-agreement", applicationId, questionId)
                        .param("removeGrantAgreement", "true"))
                .andExpect(status().isOk())
                .andExpect(view().name("application/questions/grant-agreement"))
                .andReturn();

        verify(euGrantTransferRestService).deleteGrantAgreement(applicationId);
    }

}
