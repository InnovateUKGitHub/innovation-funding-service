package org.innovateuk.ifs.project.status.documentation;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.application.resource.ApplicationState;
import org.innovateuk.ifs.project.constant.ProjectActivityStates;
import org.innovateuk.ifs.project.resource.ProjectPartnerStatusResource;
import org.innovateuk.ifs.project.status.controller.StatusController;
import org.innovateuk.ifs.project.status.resource.ProjectStatusPageResource;
import org.innovateuk.ifs.project.status.resource.ProjectStatusResource;
import org.innovateuk.ifs.project.status.resource.ProjectTeamStatusResource;
import org.innovateuk.ifs.project.status.transactional.InternalUserProjectStatusService;
import org.innovateuk.ifs.project.status.transactional.StatusService;
import org.junit.Test;
import org.mockito.Mock;

import java.util.List;
import java.util.Optional;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.project.builder.ProjectPartnerStatusResourceBuilder.newProjectPartnerStatusResource;
import static org.innovateuk.ifs.project.builder.ProjectStatusResourceBuilder.newProjectStatusResource;
import static org.innovateuk.ifs.project.builder.ProjectTeamStatusResourceBuilder.newProjectTeamStatusResource;
import static org.innovateuk.ifs.project.constant.ProjectActivityStates.*;
import static org.innovateuk.ifs.project.resource.ProjectState.LIVE;
import static org.innovateuk.ifs.util.JsonMappingUtil.toJson;
import static org.mockito.Mockito.when;
org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class StatusControllerDocumentation extends BaseControllerMockMVCTest<StatusController> {

    @Mock
    private StatusService statusServiceMock;

    @Mock
    private InternalUserProjectStatusService internalUserProjectStatusService;

    @Test
    public void getCompetitionStatus() throws Exception {
        Long competitionId = 1L;
        String applicationSearchString = "12";

        List<ProjectStatusResource> projectStatusResources = newProjectStatusResource().
                        withProjectNumber(1L, 2L, 3L).
                        withProjectTitles("Project ABC", "Project PMQ", "Project XYZ").
                        withProjectLeadOrganisationName("Hive IT").
                        withNumberOfPartners(3, 3, 3).
                        withProjectDetailStatus(COMPLETE, PENDING, COMPLETE).
                        withProjectTeamStatus(COMPLETE, PENDING, COMPLETE).
                        withMonitoringOfficerStatus(PENDING, PENDING, COMPLETE).
                        withBankDetailsStatus(PENDING, NOT_REQUIRED, COMPLETE).
                        withFinanceChecksStatus(PENDING, NOT_STARTED, COMPLETE).
                        withSpendProfileStatus(PENDING, ACTION_REQUIRED, COMPLETE).
                        withGrantOfferLetterStatus(PENDING, PENDING, PENDING).
                        withProjectSetupCompleteStatus(PENDING, PENDING, PENDING).
                        withProjectState(LIVE).
                        withApplicationState(ApplicationState.APPROVED).
                        build(3);
        ProjectStatusPageResource page = new ProjectStatusPageResource(10, 2, projectStatusResources, 0, 5);

        when(internalUserProjectStatusService.getCompetitionStatus(competitionId, applicationSearchString, 0, 5)).thenReturn(serviceSuccess(page));

        mockMvc.perform(get("/project/competition/{id}?applicationSearchString={applicationSearchString}&page={page}&size={size}", competitionId, applicationSearchString, 0, 5)
                .header("IFS_AUTH_TOKEN", "123abc"));
    }

    @Test
    public void getPreviousCompetitionStatus() throws Exception {
        Long competitionId = 1L;

        List<ProjectStatusResource> projectStatusResources = newProjectStatusResource().
                        withProjectNumber(1L, 2L, 3L).
                        withProjectTitles("Project ABC", "Project PMQ", "Project XYZ").
                        withProjectLeadOrganisationName("Hive IT").
                        withNumberOfPartners(3, 3, 3).
                        withProjectDetailStatus(COMPLETE, PENDING, COMPLETE).
                        withProjectTeamStatus(COMPLETE, PENDING, COMPLETE).
                        withMonitoringOfficerStatus(PENDING, PENDING, COMPLETE).
                        withBankDetailsStatus(PENDING, NOT_REQUIRED, COMPLETE).
                        withFinanceChecksStatus(PENDING, NOT_STARTED, COMPLETE).
                        withSpendProfileStatus(PENDING, ACTION_REQUIRED, COMPLETE).
                        withGrantOfferLetterStatus(PENDING, PENDING, PENDING).
                        withProjectState(LIVE).
                        withApplicationState(ApplicationState.APPROVED).
                        build(3);

        when(internalUserProjectStatusService.getPreviousCompetitionStatus(competitionId)).thenReturn(serviceSuccess(projectStatusResources));

        mockMvc.perform(get("/project/previous/competition/{id}", competitionId)
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andExpect(content().json(toJson(projectStatusResources)));
    }

    @Test
    public void getTeamStatus() throws Exception {
        ProjectTeamStatusResource projectTeamStatusResource = buildTeamStatus();
        when(statusServiceMock.getProjectTeamStatus(123L, Optional.empty())).thenReturn(serviceSuccess(projectTeamStatusResource));
        mockMvc.perform(get("/project/{projectId}/team-status", 123L)
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(projectTeamStatusResource)));
    }

    @Test
    public void getTeamStatusWithFilterByUserId() throws Exception {
        ProjectTeamStatusResource projectTeamStatusResource = buildTeamStatus();
        when(statusServiceMock.getProjectTeamStatus(123L, Optional.of(456L))).thenReturn(serviceSuccess(projectTeamStatusResource));
        mockMvc.perform(get("/project/{projectId}/team-status", 123L)
                .param("filterByUserId", "456")
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(projectTeamStatusResource)));
    }

    @Test
    public void getStatus() throws Exception {
        Long projectId = 1L;
        ProjectStatusResource projectStatusResource = newProjectStatusResource().build();

        when(internalUserProjectStatusService.getProjectStatusByProjectId(projectId)).thenReturn(serviceSuccess(projectStatusResource));

        mockMvc.perform(get("/project/{id}/status", projectId)
                .header("IFS_AUTH_TOKEN", "123abc"));
    }

    private ProjectTeamStatusResource buildTeamStatus() {
        ProjectPartnerStatusResource projectLeadStatusResource = newProjectPartnerStatusResource().withIsLeadPartner(true).build();
        List<ProjectPartnerStatusResource> partnerStatuses = newProjectPartnerStatusResource().build(3);

        projectLeadStatusResource.setName("Nomensa");
        partnerStatuses.get(0).setName("Acme Corp");
        partnerStatuses.get(1).setName("Hive IT");
        partnerStatuses.get(2).setName("Worth IT Systems");

        projectLeadStatusResource.setSpendProfileStatus(ProjectActivityStates.COMPLETE);
        partnerStatuses.get(0).setSpendProfileStatus(ProjectActivityStates.COMPLETE);
        partnerStatuses.get(1).setSpendProfileStatus(ProjectActivityStates.NOT_STARTED);
        partnerStatuses.get(2).setSpendProfileStatus(PENDING);

        projectLeadStatusResource.setBankDetailsStatus(ProjectActivityStates.COMPLETE);
        partnerStatuses.get(0).setBankDetailsStatus(PENDING);
        partnerStatuses.get(1).setBankDetailsStatus(ProjectActivityStates.NOT_REQUIRED);
        partnerStatuses.get(2).setBankDetailsStatus(ProjectActivityStates.NOT_STARTED);

        projectLeadStatusResource.setProjectDetailsStatus(ProjectActivityStates.COMPLETE);
        partnerStatuses.get(0).setProjectDetailsStatus(ProjectActivityStates.COMPLETE);
        partnerStatuses.get(1).setProjectDetailsStatus(ProjectActivityStates.COMPLETE);
        partnerStatuses.get(2).setProjectDetailsStatus(ProjectActivityStates.COMPLETE);

        projectLeadStatusResource.setProjectTeamStatus(ProjectActivityStates.COMPLETE);
        partnerStatuses.get(0).setProjectTeamStatus(ProjectActivityStates.COMPLETE);
        partnerStatuses.get(1).setProjectTeamStatus(ProjectActivityStates.COMPLETE);
        partnerStatuses.get(2).setProjectTeamStatus(ProjectActivityStates.COMPLETE);

        projectLeadStatusResource.setFinanceChecksStatus(PENDING);
        partnerStatuses.get(0).setFinanceChecksStatus(PENDING);
        partnerStatuses.get(1).setFinanceChecksStatus(PENDING);
        partnerStatuses.get(2).setFinanceChecksStatus(PENDING);

        projectLeadStatusResource.setMonitoringOfficerStatus(PENDING);
        partnerStatuses.get(0).setMonitoringOfficerStatus(ProjectActivityStates.COMPLETE);
        partnerStatuses.get(1).setMonitoringOfficerStatus(PENDING);
        partnerStatuses.get(2).setMonitoringOfficerStatus(ProjectActivityStates.COMPLETE);

        projectLeadStatusResource.setGrantOfferLetterStatus(PENDING);
        partnerStatuses.get(0).setGrantOfferLetterStatus(PENDING);
        partnerStatuses.get(1).setGrantOfferLetterStatus(PENDING);
        partnerStatuses.get(2).setGrantOfferLetterStatus(PENDING);

        return newProjectTeamStatusResource().withPartnerStatuses(partnerStatuses).build();
    }

    @Override
    protected StatusController supplyControllerUnderTest() {
        return new StatusController();
    }
}
