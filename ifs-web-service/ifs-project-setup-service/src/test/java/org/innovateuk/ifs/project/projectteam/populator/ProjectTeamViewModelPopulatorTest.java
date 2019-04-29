package org.innovateuk.ifs.project.projectteam.populator;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.project.ProjectService;
import org.innovateuk.ifs.project.constant.ProjectActivityStates;
import org.innovateuk.ifs.project.projectteam.viewmodel.ProjectTeamViewModel;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.resource.ProjectUserResource;
import org.innovateuk.ifs.project.status.resource.ProjectTeamStatusResource;
import org.innovateuk.ifs.status.StatusService;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;
import java.util.Optional;

import static java.util.Arrays.asList;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.organisation.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.innovateuk.ifs.project.builder.ProjectPartnerStatusResourceBuilder.newProjectPartnerStatusResource;
import static org.innovateuk.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static org.innovateuk.ifs.project.builder.ProjectTeamStatusResourceBuilder.newProjectTeamStatusResource;
import static org.innovateuk.ifs.project.builder.ProjectUserResourceBuilder.newProjectUserResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class ProjectTeamViewModelPopulatorTest extends BaseServiceUnitTest<ProjectTeamViewModelPopulator> {

    @Mock
    private ProjectService projectService;

    @Mock
    private CompetitionRestService competitionRestService;

    @Mock
    private StatusService statusService;

    @Override
    protected ProjectTeamViewModelPopulator supplyServiceUnderTest() {
        return new ProjectTeamViewModelPopulator();
    }

    @Test
    public void populate() {

        UserResource loggedInUser = newUserResource().withId(123L).build();
        CompetitionResource competition = newCompetitionResource()
                .withName("Imaginative competition name")
                .build();
        ProjectResource project = newProjectResource()
                .withCompetition(competition.getId())
                .withName("Imaginative project name")
                .build();
        OrganisationResource leadOrg = newOrganisationResource()
                .withName("Imaginative organisation name")
                .build();
        OrganisationResource partnerOne = newOrganisationResource().build();
        OrganisationResource partnerTwo = newOrganisationResource().build();
        List<ProjectUserResource> projectUsers = newProjectUserResource()
                .withUser(123L, 456L, 123L, 456L)
                .withOrganisation(partnerOne.getId(), partnerTwo.getId(), partnerOne.getId(), partnerTwo.getId())
                .withRole(11L, 10L, 10L, 9L)
                .build(4);
        List<OrganisationResource> projectOrgs = asList(partnerOne, partnerTwo);
        ProjectTeamStatusResource teamStatus = newProjectTeamStatusResource().
                withProjectLeadStatus(newProjectPartnerStatusResource()
                                              .withIsLeadPartner(true)
                                              .withMonitoringOfficerStatus(ProjectActivityStates.NOT_STARTED)
                                              .withSpendProfileStatus(ProjectActivityStates.PENDING)
                                              .withGrantOfferStatus(ProjectActivityStates.NOT_REQUIRED)
                                              .build())
                .build();


        when(projectService.getById(project.getId())).thenReturn(project);
        when(competitionRestService.getCompetitionById(competition.getId())).thenReturn(restSuccess(competition));
        when(projectService.getProjectUsersForProject(project.getId())).thenReturn(projectUsers);
        when(projectService.getPartnerOrganisationsForProject(project.getId())).thenReturn(projectOrgs);
        when(projectService.getLeadOrganisation(project.getId())).thenReturn(leadOrg);
        when(statusService.getProjectTeamStatus(project.getId(), Optional.empty())).thenReturn(teamStatus);

        ProjectTeamViewModel model = service.populate(project.getId(), loggedInUser);
        assertEquals(competition.getName(), model.getCompetitionName());
        assertEquals(project.getName(), model.getProjectName());
        assertEquals((long) project.getId(), model.getProjectId());
        assertEquals(false, model.isUserLeadPartner());
        assertEquals(true, model.isSpendProfileGenerated());
        assertEquals(leadOrg.getName(), model.getLeadOrg().getOrgName());
        assertEquals((long) loggedInUser.getId(), model.getLoggedInUserId());
        assertEquals(false, model.isReadOnly());
    }

}
