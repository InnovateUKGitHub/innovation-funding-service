package org.innovateuk.ifs.project.grantofferletter.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.CompetitionSummaryResource;
import org.innovateuk.ifs.commons.ZeroDowntime;
import org.innovateuk.ifs.competition.resource.CompetitionStatus;
import org.innovateuk.ifs.project.grantofferletter.form.GrantOfferLetterLetterForm;
import org.innovateuk.ifs.project.grantofferletter.resource.GrantOfferLetterState;
import org.innovateuk.ifs.project.grantofferletter.viewmodel.GrantOfferLetterModel;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.junit.Test;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Optional;

import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.application.builder.CompetitionSummaryResourceBuilder.newCompetitionSummaryResource;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@ZeroDowntime(reference = "IFS-2579", description = "Remove in Sprint 19 - replaced with GrantOfferLetterControllerTest")
public class GrantOfferLetterControllerOldTest extends BaseControllerMockMVCTest<GrantOfferLetterController> {

    @Test
    public void testView() throws Exception {
        Long competitionId = 1L;
        Long projectId = 123L;
        Long applicationId = 789L;

        ApplicationResource applicationResource = newApplicationResource().withId(applicationId).build();
        ProjectResource projectResource = newProjectResource().withId(projectId).withApplication(applicationResource).build();

        when(projectService.getById(projectId)).thenReturn(projectResource);

        when(applicationService.getById(applicationId)).thenReturn(newApplicationResource().withId(applicationId).withCompetition(competitionId).build());

        CompetitionSummaryResource competitionSummaryResource = newCompetitionSummaryResource().withId(competitionId).withCompetitionStatus(CompetitionStatus.OPEN).build();
        when(applicationSummaryRestService.getCompetitionSummary(competitionId)).thenReturn(restSuccess(competitionSummaryResource));

        when(grantOfferLetterService.getGrantOfferFileDetails(projectId)).thenReturn(Optional.empty());
        when(grantOfferLetterService.getAdditionalContractFileDetails(projectId)).thenReturn(Optional.empty());

        when(grantOfferLetterService.getGrantOfferLetterWorkflowState(projectId)).thenReturn(serviceSuccess(GrantOfferLetterState.PENDING));
        when(grantOfferLetterService.getSignedGrantOfferLetterFileDetails(projectId)).thenReturn(Optional.empty());

        when(grantOfferLetterService.isSignedGrantOfferLetterRejected(projectId)).thenReturn(serviceSuccess(false));
        
        MvcResult result = mockMvc.perform(get("/project/" + projectId + "/grant-offer-letter/send")).
                andExpect(view().name("project/grant-offer-letter-send")).
                andReturn();

        GrantOfferLetterModel golViewModel = (GrantOfferLetterModel) result.getModelAndView().getModel().get("model");

        assertFalse(golViewModel.isSentToProjectTeam());
        assertEquals(null, golViewModel.getGrantOfferLetterFile());
        assertEquals(null, golViewModel.getAdditionalContractFile());
        assertEquals(null, golViewModel.getSignedGrantOfferLetterFile());
        assertFalse(golViewModel.getAdditionalContractFileContentAvailable());
        assertFalse(golViewModel.getGrantOfferLetterFileContentAvailable());

        GrantOfferLetterLetterForm form = (GrantOfferLetterLetterForm) result.getModelAndView().getModel().get("form");
        assertEquals(form.getAnnex(), null);
    }

    @Test
    public void testSendGOLSuccess() throws Exception {
        Long competitionId = 1L;
        Long projectId = 123L;
        Long applicationId = 789L;

        ApplicationResource applicationResource = newApplicationResource().withId(applicationId).build();
        ProjectResource projectResource = newProjectResource().withId(projectId).withApplication(applicationResource).build();

        when(projectService.getById(projectId)).thenReturn(projectResource);

        when(applicationService.getById(applicationId)).thenReturn(newApplicationResource().withId(applicationId).withCompetition(competitionId).build());

        CompetitionSummaryResource competitionSummaryResource = newCompetitionSummaryResource().withId(competitionId).withCompetitionStatus(CompetitionStatus.OPEN).build();
        when(applicationSummaryRestService.getCompetitionSummary(competitionId)).thenReturn(restSuccess(competitionSummaryResource));

        when(grantOfferLetterService.getGrantOfferFileDetails(projectId)).thenReturn(Optional.empty());
        when(grantOfferLetterService.getAdditionalContractFileDetails(projectId)).thenReturn(Optional.empty());
        when(grantOfferLetterService.getGrantOfferLetterWorkflowState(projectId)).thenReturn(serviceSuccess(GrantOfferLetterState.PENDING));
        when(grantOfferLetterService.getSignedGrantOfferLetterFileDetails(projectId)).thenReturn(Optional.empty());

        when(grantOfferLetterService.sendGrantOfferLetter(projectId)).thenReturn(serviceSuccess());

        // re-load model after sending GOL
        when(projectService.getById(projectId)).thenReturn(projectResource);

        when(applicationService.getById(applicationId)).thenReturn(newApplicationResource().withId(applicationId).withCompetition(competitionId).build());

        competitionSummaryResource = newCompetitionSummaryResource().withId(competitionId).withCompetitionStatus(CompetitionStatus.OPEN).build();
        when(applicationSummaryRestService.getCompetitionSummary(competitionId)).thenReturn(restSuccess(competitionSummaryResource));

        when(grantOfferLetterService.getGrantOfferFileDetails(projectId)).thenReturn(Optional.empty());
        when(grantOfferLetterService.getAdditionalContractFileDetails(projectId)).thenReturn(Optional.empty());
        when(grantOfferLetterService.getGrantOfferLetterWorkflowState(projectId)).thenReturn(serviceSuccess(GrantOfferLetterState.SENT));
        when(grantOfferLetterService.getSignedGrantOfferLetterFileDetails(projectId)).thenReturn(Optional.empty());

        when(grantOfferLetterService.isSignedGrantOfferLetterRejected(projectId)).thenReturn(serviceSuccess(false));

        MvcResult result = mockMvc.perform(post("/project/" + projectId + "/grant-offer-letter/send")).
                andExpect(view().name("project/grant-offer-letter-send")).
                andReturn();

        GrantOfferLetterModel golViewModel = (GrantOfferLetterModel) result.getModelAndView().getModel().get("model");

        assertTrue(golViewModel.isSentToProjectTeam());
        assertEquals(null, golViewModel.getGrantOfferLetterFile());
        assertEquals(null, golViewModel.getAdditionalContractFile());
        assertEquals(null, golViewModel.getSignedGrantOfferLetterFile());
        assertFalse(golViewModel.getAdditionalContractFileContentAvailable());
        assertFalse(golViewModel.getGrantOfferLetterFileContentAvailable());
        assertEquals(Boolean.TRUE, golViewModel.isSentToProjectTeam());

        GrantOfferLetterLetterForm form = (GrantOfferLetterLetterForm) result.getModelAndView().getModel().get("form");
        assertEquals(form.getAnnex(), null);
    }

    @Test
    public void testSendGOLFailure() throws Exception {
        Long competitionId = 1L;
        Long projectId = 123L;
        Long applicationId = 789L;

        ApplicationResource applicationResource = newApplicationResource().withId(applicationId).build();
        ProjectResource projectResource = newProjectResource().withId(projectId).withApplication(applicationResource).build();

        when(projectService.getById(projectId)).thenReturn(projectResource);

        when(applicationService.getById(applicationId)).thenReturn(newApplicationResource().withId(applicationId).withCompetition(competitionId).build());

        CompetitionSummaryResource competitionSummaryResource = newCompetitionSummaryResource().withId(competitionId).withCompetitionStatus(CompetitionStatus.OPEN).build();
        when(applicationSummaryRestService.getCompetitionSummary(competitionId)).thenReturn(restSuccess(competitionSummaryResource));

        when(grantOfferLetterService.getGrantOfferFileDetails(projectId)).thenReturn(Optional.empty());
        when(grantOfferLetterService.getAdditionalContractFileDetails(projectId)).thenReturn(Optional.empty());
        when(grantOfferLetterService.getGrantOfferLetterWorkflowState(projectId)).thenReturn(serviceSuccess(GrantOfferLetterState.PENDING));
        when(grantOfferLetterService.getSignedGrantOfferLetterFileDetails(projectId)).thenReturn(Optional.empty());
        when(grantOfferLetterService.isSignedGrantOfferLetterRejected(projectId)).thenReturn(serviceSuccess(false));

        when(grantOfferLetterService.sendGrantOfferLetter(projectId)).thenReturn(serviceSuccess());

        // re-load model after sending GOL
        when(projectService.getById(projectId)).thenReturn(projectResource);

        when(applicationService.getById(applicationId)).thenReturn(newApplicationResource().withId(applicationId).withCompetition(competitionId).build());

        competitionSummaryResource = newCompetitionSummaryResource().withId(competitionId).withCompetitionStatus(CompetitionStatus.OPEN).build();
        when(applicationSummaryRestService.getCompetitionSummary(competitionId)).thenReturn(restSuccess(competitionSummaryResource));

        when(grantOfferLetterService.getGrantOfferFileDetails(projectId)).thenReturn(Optional.empty());
        when(grantOfferLetterService.getAdditionalContractFileDetails(projectId)).thenReturn(Optional.empty());
        when(grantOfferLetterService.getGrantOfferLetterWorkflowState(projectId)).thenReturn(serviceSuccess(GrantOfferLetterState.PENDING));
        when(grantOfferLetterService.getSignedGrantOfferLetterFileDetails(projectId)).thenReturn(Optional.empty());


        MvcResult result = mockMvc.perform(post("/project/" + projectId + "/grant-offer-letter/send")).
                andExpect(view().name("project/grant-offer-letter-send")).
                andReturn();

        GrantOfferLetterModel golViewModel = (GrantOfferLetterModel) result.getModelAndView().getModel().get("model");

        assertFalse(golViewModel.isSentToProjectTeam());
        assertEquals(null, golViewModel.getGrantOfferLetterFile());
        assertEquals(null, golViewModel.getAdditionalContractFile());
        assertEquals(null, golViewModel.getSignedGrantOfferLetterFile());
        assertFalse(golViewModel.getAdditionalContractFileContentAvailable());
        assertFalse(golViewModel.getGrantOfferLetterFileContentAvailable());
        assertEquals(Boolean.FALSE, golViewModel.isSentToProjectTeam());

        GrantOfferLetterLetterForm form = (GrantOfferLetterLetterForm) result.getModelAndView().getModel().get("form");
        assertEquals(form.getAnnex(), null);
    }

    @Override
    protected GrantOfferLetterController supplyControllerUnderTest() {
        return new GrantOfferLetterController();
    }
}
