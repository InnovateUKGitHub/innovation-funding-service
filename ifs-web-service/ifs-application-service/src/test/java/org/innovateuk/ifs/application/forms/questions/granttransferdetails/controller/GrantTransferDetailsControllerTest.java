package org.innovateuk.ifs.application.forms.questions.granttransferdetails.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.application.forms.questions.granttransferdetails.form.GrantTransferDetailsForm;
import org.innovateuk.ifs.application.forms.questions.granttransferdetails.populator.GrantTransferDetailsFormPopulator;
import org.innovateuk.ifs.application.forms.questions.granttransferdetails.populator.GrantTransferDetailsViewModelPopulator;
import org.innovateuk.ifs.application.forms.questions.granttransferdetails.saver.GrantTransferDetailsSaver;
import org.innovateuk.ifs.application.forms.questions.granttransferdetails.viewmodel.GrantTransferDetailsViewModel;
import org.innovateuk.ifs.application.service.QuestionStatusRestService;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.service.UserRestService;
import org.junit.Test;
import org.mockito.Mock;

import java.math.BigDecimal;
import java.util.Collections;

import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.user.builder.ProcessRoleResourceBuilder.newProcessRoleResource;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class GrantTransferDetailsControllerTest extends BaseControllerMockMVCTest<GrantTransferDetailsController> {

    @Mock
    private GrantTransferDetailsFormPopulator grantTransferDetailsFormPopulator;

    @Mock
    private GrantTransferDetailsViewModelPopulator grantTransferDetailsViewModelPopulator;

    @Mock
    private GrantTransferDetailsSaver grantTransferDetailsSaver;

    @Mock
    private UserRestService userRestService;

    @Mock
    private QuestionStatusRestService questionStatusRestService;

    @Override
    protected GrantTransferDetailsController supplyControllerUnderTest() {
        return new GrantTransferDetailsController(grantTransferDetailsFormPopulator, grantTransferDetailsViewModelPopulator, grantTransferDetailsSaver, userRestService, questionStatusRestService);
    }

    @Test
    public void viewGrantTransferDetails() throws Exception {
        long questionId = 1L;
        long applicationId = 2L;

        GrantTransferDetailsViewModel viewModel = mock(GrantTransferDetailsViewModel.class);
        when(grantTransferDetailsViewModelPopulator.populate(applicationId, questionId, loggedInUser.getId())).thenReturn(viewModel);
        mockMvc.perform(
                get("/application/{applicationId}/form/question/{questionId}/grant-transfer-details", applicationId, questionId))
                .andExpect(status().isOk())
                .andExpect(view().name("application/questions/grant-transfer-details"))
                .andReturn();

        verify(grantTransferDetailsFormPopulator).populate(any(), eq(applicationId));
    }

    @Test
    public void saveAndReturn() throws Exception {
        long questionId = 1L;
        long applicationId = 2L;

        mockMvc.perform(
                post("/application/{applicationId}/form/question/{questionId}/grant-transfer-details", applicationId, questionId))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(String.format("/application/%d", applicationId)))
                .andReturn();
    }

    @Test
    public void markAsComplete() throws Exception {
        long questionId = 1L;
        long applicationId = 2L;

        GrantTransferDetailsForm grantTransferDetailsForm = new GrantTransferDetailsForm();
        grantTransferDetailsForm.setActionType(1L);
        grantTransferDetailsForm.setFundingContribution(BigDecimal.valueOf(100000L));
        grantTransferDetailsForm.setGrantAgreementNumber("123456");
        grantTransferDetailsForm.setProjectCoordinator(true);
        grantTransferDetailsForm.setStartDateMonth(10);
        grantTransferDetailsForm.setStartDateYear(2000);
        grantTransferDetailsForm.setEndDateMonth(10);
        grantTransferDetailsForm.setEndDateYear(2020);
        grantTransferDetailsForm.setParticipantId("123456789");
        grantTransferDetailsForm.setProjectName("Project Name");

        ProcessRoleResource role = newProcessRoleResource().build();

        when(grantTransferDetailsSaver.save(grantTransferDetailsForm, applicationId)).thenReturn(restSuccess());
        when(userRestService.findProcessRole(loggedInUser.getId(), applicationId)).thenReturn(restSuccess(role));
        when(questionStatusRestService.markAsComplete(questionId, applicationId, role.getId())).thenReturn(restSuccess(Collections.emptyList()));

        mockMvc.perform(
                post("/application/{applicationId}/form/question/{questionId}/grant-transfer-details", applicationId, questionId)
                .param("complete", "true")
                .param("grantAgreementNumber", grantTransferDetailsForm.getGrantAgreementNumber())
                .param("participantId", grantTransferDetailsForm.getParticipantId())
                .param("projectName", grantTransferDetailsForm.getProjectName())
                .param("startDateMonth", String.valueOf(grantTransferDetailsForm.getStartDateMonth()))
                .param("startDateYear", String.valueOf(grantTransferDetailsForm.getStartDateYear()))
                .param("endDateMonth", String.valueOf(grantTransferDetailsForm.getEndDateMonth()))
                .param("endDateYear", String.valueOf(grantTransferDetailsForm.getEndDateYear()))
                .param("fundingContribution", String.valueOf(grantTransferDetailsForm.getFundingContribution()))
                .param("projectCoordinator", String.valueOf(grantTransferDetailsForm.getProjectCoordinator()))
                .param("actionType", String.valueOf(grantTransferDetailsForm.getActionType())))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(String.format("/application/%d", applicationId)))
                .andReturn();

        verify(grantTransferDetailsSaver).save(grantTransferDetailsForm, applicationId);
        verify(questionStatusRestService).markAsComplete(questionId, applicationId, role.getId());
    }

    @Test
    public void edit() throws Exception {
        long questionId = 1L;
        long applicationId = 2L;

        ProcessRoleResource role = newProcessRoleResource().build();
        when(userRestService.findProcessRole(loggedInUser.getId(), applicationId)).thenReturn(restSuccess(role));
        when(questionStatusRestService.markAsInComplete(questionId, applicationId, role.getId())).thenReturn(restSuccess());
        GrantTransferDetailsViewModel viewModel = mock(GrantTransferDetailsViewModel.class);
        when(grantTransferDetailsViewModelPopulator.populate(applicationId, questionId, loggedInUser.getId())).thenReturn(viewModel);

        mockMvc.perform(
                post("/application/{applicationId}/form/question/{questionId}/grant-transfer-details", applicationId, questionId)
                        .param("edit", "true"))
                .andExpect(status().isOk())
                .andExpect(view().name("application/questions/grant-transfer-details"))
                .andReturn();

        verify(questionStatusRestService).markAsInComplete(questionId, applicationId, role.getId());
    }
}
