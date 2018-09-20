package org.innovateuk.ifs.project.status.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.application.builder.ApplicationResourceBuilder;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.ApplicationService;
import org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.project.ProjectService;
import org.innovateuk.ifs.project.builder.ProjectResourceBuilder;
import org.innovateuk.ifs.project.consortiumoverview.viewmodel.ProjectConsortiumStatusViewModel;
import org.innovateuk.ifs.project.constant.ProjectActivityStates;
import org.innovateuk.ifs.project.resource.ProjectPartnerStatusResource;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.status.resource.ProjectTeamStatusResource;
import org.innovateuk.ifs.status.StatusService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;
import java.util.Optional;

import static junit.framework.TestCase.assertEquals;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.project.builder.ProjectPartnerStatusResourceBuilder.newProjectPartnerStatusResource;
import static org.innovateuk.ifs.project.builder.ProjectTeamStatusResourceBuilder.newProjectTeamStatusResource;
import static org.innovateuk.ifs.project.constant.ProjectActivityStates.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@RunWith(MockitoJUnitRunner.class)
public class TeamStatusControllerTest extends BaseControllerMockMVCTest<TeamStatusController> {

    @Mock
    private StatusService statusService;

    @Mock
    private ProjectService projectService;

    @Mock
    private ApplicationService applicationService;

    @Mock
    private CompetitionRestService competitionRestService;

    @Override
    protected TeamStatusController supplyControllerUnderTest() {
        return new TeamStatusController();
    }

    @Test
    public void testViewProjectTeamStatusWhenFinanceContactsAndPartnerProjectLocationsBothNotYetSubmitted() throws Exception {
        Long projectId = 1L;

        setUpMockingViewProjectTeamStatus(projectId,
                COMPLETE,   //leadProjectDetailsStatus
                ACTION_REQUIRED,   //leadFinanceContactStatus
                ACTION_REQUIRED,    //leadProjectLocationStatus
                PENDING,    //leadMonitoringOfficerStatus
                ACTION_REQUIRED,   //otherPartnersFinanceContactStatus
                ACTION_REQUIRED,   //firstPartnerProjectLocationStatus
                ACTION_REQUIRED     //secondPartnerProjectLocationStatus
        );

        MvcResult mvcResult = mockMvc.perform(get("/project/{id}/team-status", projectId))
                .andExpect(view().name("project/consortium-status"))
                .andReturn();

        ProjectConsortiumStatusViewModel model = (ProjectConsortiumStatusViewModel) mvcResult.getModelAndView().getModel().get("model");

        assertionsViewProjectTeamStatus(model,
                ACTION_REQUIRED, // leadPartnerProjectDetailsTeamStatus
                ACTION_REQUIRED, // firstPartnerProjectDetailsTeamStatus
                ACTION_REQUIRED, // secondPartnerProjectDetailsTeamStatus
                NOT_STARTED // monitoringOfficerStatus
        );

    }

    @Test
    public void testViewProjectTeamStatusWhenAllPartnerProjectLocationsNotYetSubmitted() throws Exception {
        Long projectId = 1L;

        setUpMockingViewProjectTeamStatus(projectId,
                COMPLETE,   //leadProjectDetailsStatus
                COMPLETE,   //leadFinanceContactStatus
                ACTION_REQUIRED,    //leadProjectLocationStatus
                PENDING,    //leadMonitoringOfficerStatus
                COMPLETE,   //otherPartnersFinanceContactStatus
                COMPLETE,   //firstPartnerProjectLocationStatus
                ACTION_REQUIRED     //secondPartnerProjectLocationStatus
                );

        MvcResult mvcResult = mockMvc.perform(get("/project/{id}/team-status", projectId))
            .andExpect(view().name("project/consortium-status"))
            .andReturn();

        ProjectConsortiumStatusViewModel model = (ProjectConsortiumStatusViewModel) mvcResult.getModelAndView().getModel().get("model");

        assertionsViewProjectTeamStatus(model,
            ACTION_REQUIRED, // leadPartnerProjectDetailsTeamStatus
            COMPLETE, // firstPartnerProjectDetailsTeamStatus
            ACTION_REQUIRED, // secondPartnerProjectDetailsTeamStatus
            NOT_STARTED // monitoringOfficerStatus
            );

    }

    @Test
    public void testViewProjectTeamStatusWhenAllFinanceContactsNotYetSubmitted() throws Exception {
        Long projectId = 1L;

        setUpMockingViewProjectTeamStatus(projectId,
                COMPLETE,   //leadProjectDetailsStatus
                COMPLETE,   //leadFinanceContactStatus
                COMPLETE,    //leadProjectLocationStatus
                PENDING,    //leadMonitoringOfficerStatus
                ACTION_REQUIRED,   //otherPartnersFinanceContactStatus
                COMPLETE,   //firstPartnerProjectLocationStatus
                COMPLETE     //secondPartnerProjectLocationStatus
        );

        MvcResult mvcResult = mockMvc.perform(get("/project/{id}/team-status", projectId))
                .andExpect(view().name("project/consortium-status"))
                .andReturn();

        ProjectConsortiumStatusViewModel model = (ProjectConsortiumStatusViewModel) mvcResult.getModelAndView().getModel().get("model");

        assertionsViewProjectTeamStatus(model,
                ACTION_REQUIRED, // leadPartnerProjectDetailsTeamStatus
                ACTION_REQUIRED, // firstPartnerProjectDetailsTeamStatus
                ACTION_REQUIRED, // secondPartnerProjectDetailsTeamStatus
                PENDING // monitoringOfficerStatus
        );

    }

    @Test
    public void testViewProjectTeamStatusWhenAllFinanceContactsAndProjectLocationsSubmitted() throws Exception {
        Long projectId = 1L;

        setUpMockingViewProjectTeamStatus(projectId,
                COMPLETE,   //leadProjectDetailsStatus
                COMPLETE,   //leadFinanceContactStatus
                COMPLETE,    //leadProjectLocationStatus
                PENDING,    //leadMonitoringOfficerStatus
                COMPLETE,   //otherPartnersFinanceContactStatus
                COMPLETE,   //firstPartnerProjectLocationStatus
                COMPLETE     //secondPartnerProjectLocationStatus
        );

        MvcResult mvcResult = mockMvc.perform(get("/project/{id}/team-status", projectId))
                .andExpect(view().name("project/consortium-status"))
                .andReturn();

        ProjectConsortiumStatusViewModel model = (ProjectConsortiumStatusViewModel) mvcResult.getModelAndView().getModel().get("model");

        assertionsViewProjectTeamStatus(model,
                COMPLETE, // leadPartnerProjectDetailsTeamStatus
                COMPLETE, // firstPartnerProjectDetailsTeamStatus
                COMPLETE, // secondPartnerProjectDetailsTeamStatus
                PENDING // monitoringOfficerStatus
        );

    }

    private void setUpMockingViewProjectTeamStatus(Long projectId,
                                                   ProjectActivityStates leadProjectDetailsStatus,
                                                   ProjectActivityStates leadFinanceContactStatus,
                                                   ProjectActivityStates leadProjectLocationStatus,
                                                   ProjectActivityStates leadMonitoringOfficerStatus,
                                                   ProjectActivityStates otherPartnersFinanceContactStatus,
                                                   ProjectActivityStates firstPartnerProjectLocationStatus,
                                                   ProjectActivityStates secondPartnerProjectLocationStatus) {
        Long applicationId = 1L;
        Long competitionId = 12L;

        ApplicationResource application = ApplicationResourceBuilder.newApplicationResource()
                .withId(applicationId)
                .withCompetition(competitionId)
                .build();

        ProjectResource project = ProjectResourceBuilder.newProjectResource()
                .withId(projectId)
                .withApplication(applicationId)
                .build();

        CompetitionResource competition = CompetitionResourceBuilder.newCompetitionResource()
                .withId(competitionId)
                .withLocationPerPartner(true)
                .build();

        ProjectTeamStatusResource teamStatus = buildTeamStatus(leadProjectDetailsStatus,
                                                            leadFinanceContactStatus,
                                                            leadProjectLocationStatus,
                                                            leadMonitoringOfficerStatus,
                                                            otherPartnersFinanceContactStatus,
                                                            firstPartnerProjectLocationStatus,
                                                            secondPartnerProjectLocationStatus);

        when(statusService.getProjectTeamStatus(projectId, Optional.empty())).thenReturn(teamStatus);
        when(projectService.getById(projectId)).thenReturn(project);
        when(applicationService.getById(applicationId)).thenReturn(application);
        when(competitionRestService.getCompetitionById(competitionId)).thenReturn(restSuccess(competition));
    }

    private void assertionsViewProjectTeamStatus(ProjectConsortiumStatusViewModel model,
                                                 ProjectActivityStates leadPartnerProjectDetailsTeamStatus,
                                                 ProjectActivityStates firstPartnerProjectDetailsTeamStatus,
                                                 ProjectActivityStates secondPartnerProjectDetailsTeamStatus,
                                                 ProjectActivityStates monitoringOfficerStatus) {

        ProjectTeamStatusResource teamStatus = model.getProjectTeamStatusResource();
        assertEquals(leadPartnerProjectDetailsTeamStatus, teamStatus.getLeadPartnerStatus().getProjectDetailsStatus());
        assertEquals(firstPartnerProjectDetailsTeamStatus, teamStatus.getOtherPartnersStatuses().get(0).getProjectDetailsStatus());
        assertEquals(secondPartnerProjectDetailsTeamStatus, teamStatus.getOtherPartnersStatuses().get(1).getProjectDetailsStatus());
        assertEquals(monitoringOfficerStatus, teamStatus.getLeadPartnerStatus().getMonitoringOfficerStatus());

    }

    private ProjectTeamStatusResource buildTeamStatus(ProjectActivityStates leadProjectDetailsStatus,
                                                      ProjectActivityStates leadFinanceContactStatus,
                                                      ProjectActivityStates leadProjectLocationStatus,
                                                      ProjectActivityStates leadMonitoringOfficerStatus,
                                                      ProjectActivityStates otherPartnersFinanceContactStatus,
                                                      ProjectActivityStates firstPartnerProjectLocationStatus,
                                                      ProjectActivityStates secondPartnerProjectLocationStatus
                                                      ){
        List<ProjectPartnerStatusResource> partnerStatuses = newProjectPartnerStatusResource()
                .withFinanceContactStatus(otherPartnersFinanceContactStatus)
                .withPartnerProjectLocationStatus(firstPartnerProjectLocationStatus, secondPartnerProjectLocationStatus)
                .build(2);

        ProjectPartnerStatusResource leadProjectPartnerStatusResource = newProjectPartnerStatusResource()
                .withIsLeadPartner(true)
                .withProjectDetailsStatus(leadProjectDetailsStatus)
                .withFinanceContactStatus(leadFinanceContactStatus)
                .withPartnerProjectLocationStatus(leadProjectLocationStatus)
                .withMonitoringOfficerStatus(leadMonitoringOfficerStatus)
                .build();

        partnerStatuses.add(leadProjectPartnerStatusResource);

        return newProjectTeamStatusResource().withPartnerStatuses(partnerStatuses).build();
    }
}
