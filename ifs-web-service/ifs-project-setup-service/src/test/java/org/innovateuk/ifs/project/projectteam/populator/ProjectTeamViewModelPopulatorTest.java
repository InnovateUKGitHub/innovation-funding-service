package org.innovateuk.ifs.project.projectteam.populator;

import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.invite.constant.InviteStatus;
import org.innovateuk.ifs.invite.resource.ProjectUserInviteResource;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.project.ProjectService;
import org.innovateuk.ifs.project.constant.ProjectActivityStates;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.resource.ProjectUserResource;
import org.innovateuk.ifs.project.status.resource.ProjectTeamStatusResource;
import org.innovateuk.ifs.projectdetails.ProjectDetailsService;
import org.innovateuk.ifs.projectteam.viewmodel.ProjectOrganisationUserRowViewModel;
import org.innovateuk.ifs.projectteam.viewmodel.ProjectOrganisationViewModel;
import org.innovateuk.ifs.projectteam.viewmodel.ProjectTeamViewModel;
import org.innovateuk.ifs.status.StatusService;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;
import java.util.Optional;

import static java.util.Arrays.asList;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.invite.builder.ProjectUserInviteResourceBuilder.newProjectUserInviteResource;
import static org.innovateuk.ifs.organisation.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.innovateuk.ifs.project.builder.ProjectPartnerStatusResourceBuilder.newProjectPartnerStatusResource;
import static org.innovateuk.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static org.innovateuk.ifs.project.builder.ProjectTeamStatusResourceBuilder.newProjectTeamStatusResource;
import static org.innovateuk.ifs.project.builder.ProjectUserResourceBuilder.newProjectUserResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ProjectTeamViewModelPopulatorTest {

    @InjectMocks
    private ProjectTeamViewModelPopulator service;

    @Mock
    private ProjectService projectService;

    @Mock
    private CompetitionRestService competitionRestService;

    @Mock
    private StatusService statusService;

    @Mock
    private ProjectDetailsService projectDetailsService;

    @Test
    public void populate() {
        UserResource loggedInUser = newUserResource().withId(123L).build();
        CompetitionResource competition = newCompetitionResource()
                .withName("Imaginative competition name")
                .build();
        ProjectResource project = newProjectResource()
                .withCompetition(competition.getId())
                .withName("Imaginative project name")
                .withMonitoringOfficerUser(789L)
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

        List<ProjectUserInviteResource> invites = newProjectUserInviteResource()
                .withName("Mr Invite")
                .withOrganisation(partnerOne.getId())
                .withStatus(InviteStatus.SENT)
                .build(1);

        when(projectService.getById(project.getId())).thenReturn(project);
        when(projectService.getProjectUsersForProject(project.getId())).thenReturn(projectUsers);
        when(projectService.getPartnerOrganisationsForProject(project.getId())).thenReturn(projectOrgs);
        when(projectService.getLeadOrganisation(project.getId())).thenReturn(leadOrg);
        when(statusService.getProjectTeamStatus(project.getId(), Optional.empty())).thenReturn(teamStatus);
        when(projectDetailsService.getInvitesByProject(project.getId())).thenReturn(serviceSuccess(invites));

        ProjectTeamViewModel model = service.populate(project.getId(), loggedInUser);

        assertEquals(project.getCompetitionName(), model.getCompetitionName());
        assertEquals(project.getCompetition(), model.getCompetitionId());
        assertEquals(project.getName(), model.getProjectName());
        assertEquals((long) project.getId(), model.getProjectId());
        assertFalse(model.isUserLeadPartner());
        assertEquals((long) loggedInUser.getId(), model.getLoggedInUserId());
        assertFalse(model.isInternalUserView());
        assertEquals(2, model.getPartnerOrgs().size());

        ProjectOrganisationViewModel partnerOneViewModel = model.getPartnerOrgs().stream().filter(view -> view.getOrgId() == partnerOne.getId()).findAny().get();

        assertEquals(2, partnerOneViewModel.getUsers().size());
        assertNotNull(partnerOneViewModel.getProjectManager());

        ProjectOrganisationUserRowViewModel partnerOneUser = partnerOneViewModel.getUsers().get(0);
        assertEquals(partnerOneUser.getId(), 123L);

        ProjectOrganisationUserRowViewModel partnerOneInvitee = partnerOneViewModel.getUsers().get(1);
        assertEquals((long) invites.get(0).getId(), partnerOneInvitee.getId());
        assertEquals("Mr Invite (Pending)", partnerOneInvitee.getDisplayName());

        assertFalse(partnerOneViewModel.isOpenAddTeamMemberForm());
        model.openAddTeamMemberForm(partnerOne.getId());
        assertTrue(partnerOneViewModel.isOpenAddTeamMemberForm());
        assertFalse(model.isReadOnly());
    }

}
