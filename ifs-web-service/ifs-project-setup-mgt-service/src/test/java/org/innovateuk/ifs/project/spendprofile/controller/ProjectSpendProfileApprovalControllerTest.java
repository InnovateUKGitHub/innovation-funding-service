package org.innovateuk.ifs.project.spendprofile.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.application.resource.CompetitionSummaryResource;
import org.innovateuk.ifs.application.service.ApplicationSummaryRestService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.project.ProjectService;
import org.innovateuk.ifs.project.resource.ApprovalType;
import org.innovateuk.ifs.project.resource.PartnerOrganisationResource;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.spendprofile.viewmodel.ProjectSpendProfileApprovalViewModel;
import org.innovateuk.ifs.spendprofile.OrganisationReviewDetails;
import org.innovateuk.ifs.spendprofile.SpendProfileService;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.UserRestService;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.innovateuk.ifs.application.builder.CompetitionSummaryResourceBuilder.newCompetitionSummaryResource;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.organisation.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.innovateuk.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static org.innovateuk.ifs.project.resource.ProjectState.SETUP;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class ProjectSpendProfileApprovalControllerTest extends BaseControllerMockMVCTest<ProjectSpendProfileApprovalController> {

    @Mock
    private ProjectService projectService;

    @Mock
    private ApplicationSummaryRestService applicationSummaryRestService;

    @Mock
    private CompetitionRestService competitionRestService;

    @Mock
    private UserRestService userRestService;

    @Mock
    private SpendProfileService spendProfileService;

    private ProjectSpendProfileApprovalViewModel buildExpectedProjectSpendProfileApprovalViewModelViewModel(CompetitionSummaryResource competitionSummary,
                                                                                                            String leadTechnologist,
                                                                                                            ApprovalType approvalType,
                                                                                                            List<OrganisationResource> organisations,
                                                                                                            ProjectResource project,
                                                                                                            List<OrganisationResource> partnerOrganisations) {
        Map<Long, OrganisationReviewDetails> editablePartners = new HashMap<>();
        final OrganisationResource leadOrganisation = partnerOrganisations.get(0);
        editablePartners.put(1L,
                new OrganisationReviewDetails(leadOrganisation.getId(),
                        leadOrganisation.getName(),
                        false,
                        false,
                        false,
                        null,
                        null));

        return new ProjectSpendProfileApprovalViewModel(competitionSummary, leadTechnologist, approvalType, Collections.emptyList(), project, editablePartners, leadOrganisation, false,false);
    }

    @Test
    public void viewSpendProfileApprovalSuccess() throws Exception {
        long projectId = 123L;
        long applicationId = 20L;
        long competitionId = 2319L;
        long userId = 239L;
        long organisationId = 1L;

        ReflectionTestUtils.setField(controller, "isMOSpendProfileUpdateEnabled", false);
        UserResource user = newUserResource().withId(userId).build();
        CompetitionSummaryResource competitionSummary = newCompetitionSummaryResource().withId(competitionId).build();
        CompetitionResource competition = newCompetitionResource().withId(competitionId).withLeadTechnologist(userId).build();
        ProjectResource project = newProjectResource().withId(projectId).withApplication(applicationId).withCompetition(competitionId).withProjectState(SETUP).build();

        List<OrganisationResource> partnerOrganisations = newOrganisationResource()
                .withId(organisationId)
                .withName("abc")
                .build(1);

        PartnerOrganisationResource partnerOrganisationResource = new PartnerOrganisationResource();
        partnerOrganisationResource.setOrganisation(organisationId);
        partnerOrganisationResource.setLeadOrganisation(true);
        partnerOrganisationResource.setOrganisationName(partnerOrganisations.get(0).getName());

        when(projectService.getById(projectId)).thenReturn(project);
        when(projectService.getLeadOrganisation(organisationId)).thenReturn(partnerOrganisations.get(0));
        when(applicationSummaryRestService.getCompetitionSummary(competitionId)).thenReturn(restSuccess(competitionSummary));
        when(competitionRestService.getCompetitionById(competitionId)).thenReturn(restSuccess(competition));
        when(userRestService.retrieveUserById(userId)).thenReturn(restSuccess(user));
        when(spendProfileService.getSpendProfileStatusByProjectId(projectId)).thenReturn(ApprovalType.APPROVED);
        when(projectService.getPartnerOrganisationsForProject(projectId)).thenReturn(Collections.emptyList());

        ProjectSpendProfileApprovalViewModel expectedProjectSpendProfileApprovalViewModel = buildExpectedProjectSpendProfileApprovalViewModelViewModel(competitionSummary, user.getName(), ApprovalType.APPROVED, Collections.emptyList(), project, partnerOrganisations);

        mockMvc.perform(get("/project/{projectId}/spend-profile/approval", project.getId()))
                .andExpect(status().isOk())
                .andExpect(model().attribute("model", expectedProjectSpendProfileApprovalViewModel))
                .andExpect(view().name("project/finance/spend-profile/approval"));
    }

    @Test
    public void viewImprovedSpendProfileApprovalSuccess() throws Exception {
        long projectId = 123L;
        long applicationId = 20L;
        long competitionId = 2319L;
        long userId = 239L;
        long organisationId = 1L;

        ReflectionTestUtils.setField(controller, "isMOSpendProfileUpdateEnabled", true);
        UserResource user = newUserResource().withId(userId).build();
        CompetitionSummaryResource competitionSummary = newCompetitionSummaryResource().withId(competitionId).build();
        CompetitionResource competition = newCompetitionResource().withId(competitionId).withLeadTechnologist(userId).build();
        ProjectResource project = newProjectResource().withId(projectId).withApplication(applicationId).withCompetition(competitionId).withProjectState(SETUP).build();

        List<OrganisationResource> partnerOrganisations = newOrganisationResource()
                .withId(organisationId)
                .withName("abc")
                .build(1);

        PartnerOrganisationResource partnerOrganisationResource = new PartnerOrganisationResource();
        partnerOrganisationResource.setOrganisation(organisationId);
        partnerOrganisationResource.setLeadOrganisation(true);
        partnerOrganisationResource.setOrganisationName(partnerOrganisations.get(0).getName());

        when(projectService.getById(projectId)).thenReturn(project);
        when(projectService.getLeadOrganisation(organisationId)).thenReturn(partnerOrganisations.get(0));
        when(applicationSummaryRestService.getCompetitionSummary(competitionId)).thenReturn(restSuccess(competitionSummary));
        when(competitionRestService.getCompetitionById(competitionId)).thenReturn(restSuccess(competition));
        when(userRestService.retrieveUserById(userId)).thenReturn(restSuccess(user));
        when(spendProfileService.getSpendProfileStatusByProjectId(projectId)).thenReturn(ApprovalType.APPROVED);
        when(projectService.getPartnerOrganisationsForProject(projectId)).thenReturn(Collections.emptyList());

        ProjectSpendProfileApprovalViewModel expectedProjectSpendProfileApprovalViewModel = buildExpectedProjectSpendProfileApprovalViewModelViewModel(competitionSummary, user.getName(), ApprovalType.APPROVED, Collections.emptyList(), project, partnerOrganisations);

        mockMvc.perform(get("/project/{projectId}/spend-profile/approval", project.getId()))
                .andExpect(status().isOk())
                .andExpect(model().attribute("model", expectedProjectSpendProfileApprovalViewModel))
                .andExpect(view().name("project/finance/spend-profile/approval"));
    }
     @Override
    protected ProjectSpendProfileApprovalController supplyControllerUnderTest() {
        return new ProjectSpendProfileApprovalController();
    }
}
