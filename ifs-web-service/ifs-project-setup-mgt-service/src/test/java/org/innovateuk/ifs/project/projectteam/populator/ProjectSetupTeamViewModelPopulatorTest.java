package org.innovateuk.ifs.project.projectteam.populator;

import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.invite.constant.InviteStatus;
import org.innovateuk.ifs.invite.resource.ProjectUserInviteResource;
import org.innovateuk.ifs.invite.service.ProjectInviteRestService;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.project.ProjectService;
import org.innovateuk.ifs.project.constant.ProjectActivityStates;
import org.innovateuk.ifs.project.invite.resource.SentProjectPartnerInviteResource;
import org.innovateuk.ifs.project.invite.service.ProjectPartnerInviteRestService;
import org.innovateuk.ifs.project.monitoring.service.MonitoringOfficerRestService;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.resource.ProjectUserResource;
import org.innovateuk.ifs.project.status.resource.ProjectTeamStatusResource;
import org.innovateuk.ifs.projectteam.viewmodel.AbstractProjectTeamRowViewModel;
import org.innovateuk.ifs.projectteam.viewmodel.ProjectTeamOrganisationViewModel;
import org.innovateuk.ifs.projectteam.viewmodel.ProjectTeamViewModel;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.ZonedDateTime;
import java.util.List;

import static java.util.Arrays.asList;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.invite.builder.ProjectUserInviteResourceBuilder.newProjectUserInviteResource;
import static org.innovateuk.ifs.organisation.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.innovateuk.ifs.project.builder.ProjectPartnerStatusResourceBuilder.newProjectPartnerStatusResource;
import static org.innovateuk.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static org.innovateuk.ifs.project.builder.ProjectTeamStatusResourceBuilder.newProjectTeamStatusResource;
import static org.innovateuk.ifs.project.builder.ProjectUserResourceBuilder.newProjectUserResource;
import static org.innovateuk.ifs.project.invite.builder.SentProjectPartnerInviteResourceBuilder.newSentProjectPartnerInviteResource;
import static org.innovateuk.ifs.project.resource.ProjectState.SETUP;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.user.resource.Role.IFS_ADMINISTRATOR;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ProjectSetupTeamViewModelPopulatorTest {

    @InjectMocks
    private ProjectTeamViewModelPopulator service;

    @Mock
    private ProjectService projectService;

    @Mock
    private ProjectInviteRestService projectInviteRestService;

    @Mock
    private ProjectPartnerInviteRestService projectPartnerInviteRestService;

    @Mock
    private MonitoringOfficerRestService monitoringOfficerRestService;

    @Test
    public void populate() {
        UserResource loggedInUser = newUserResource().withRoleGlobal(IFS_ADMINISTRATOR).withId(123L).build();
        CompetitionResource competition = newCompetitionResource()
                .withName("Imaginative competition name")
                .build();
        ProjectResource project = newProjectResource()
                .withCompetition(competition.getId())
                .withName("Imaginative project name")
                .withProjectState(SETUP)
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
                .withSentOn(ZonedDateTime.now().minusHours(2).minusDays(10))
                .build(1);

        List<SentProjectPartnerInviteResource> partnerInvites = newSentProjectPartnerInviteResource()
                .withEmail("partner@partners.com")
                .withUserName("Partner")
                .withOrganisationName("Partner Ltd.")
                .withSentOn(ZonedDateTime.now().minusHours(2).minusDays(10))
                .build(1);

        when(projectService.getById(project.getId())).thenReturn(project);
        when(projectService.getProjectUsersForProject(project.getId())).thenReturn(projectUsers);
        when(projectService.getPartnerOrganisationsForProject(project.getId())).thenReturn(projectOrgs);
        when(projectService.getLeadOrganisation(project.getId())).thenReturn(leadOrg);
        when(projectInviteRestService.getInvitesByProject(project.getId())).thenReturn(restSuccess(invites));
        when(projectPartnerInviteRestService.getPartnerInvites(project.getId())).thenReturn(restSuccess(partnerInvites));
        when(monitoringOfficerRestService.isMonitoringOfficerOnProject(project.getId(), loggedInUser.getId())).thenReturn(restSuccess(false));

        ProjectTeamViewModel model = service.populate(project.getId(), loggedInUser);

        assertEquals(project.getCompetitionName(), model.getCompetitionName());
        assertEquals(project.getCompetition(), model.getCompetitionId());
        assertEquals(project.getName(), model.getProjectName());
        assertEquals((long) project.getId(), model.getProjectId());
        assertFalse(model.isUserLeadPartner());
        assertEquals((long) loggedInUser.getId(), model.getLoggedInUserId());
        assertTrue(model.isInternalUserView());
        assertFalse(model.isReadOnly());
        assertEquals(3, model.getPartners().size());

        ProjectTeamOrganisationViewModel partnerOneViewModel =
                model.getPartners()
                        .stream()
                        .filter(view -> view.getId() == partnerOne.getId())
                        .findAny()
                        .get();

        assertEquals(1, partnerOneViewModel.getUsers().size());

        AbstractProjectTeamRowViewModel partnerOneInvitee = partnerOneViewModel.getUsers().get(0);
        assertEquals((long) invites.get(0).getId(), partnerOneInvitee.getId());
        assertEquals("Mr Invite (pending for 10 days)", partnerOneInvitee.getName());

        assertFalse(partnerOneViewModel.isOpenAddTeamMemberForm());
        model.openAddTeamMemberForm(partnerOne.getId());
        assertTrue(partnerOneViewModel.isOpenAddTeamMemberForm());

        ProjectTeamOrganisationViewModel partnerInviteOrganisation =
                model.getPartners()
                        .stream()
                        .filter(view -> view.getId() == partnerInvites.get(0).getId())
                        .findAny()
                        .get();

        assertTrue(partnerInviteOrganisation.isPartnerInvite());
        assertEquals("Partner Ltd.", partnerInviteOrganisation.getName());

        AbstractProjectTeamRowViewModel inviteOrganisationUser = partnerInviteOrganisation.getUsers().get(0);
        assertEquals("Partner (pending for 10 days)", inviteOrganisationUser.getName());
        assertEquals("partner@partners.com", inviteOrganisationUser.getEmail());
    }
}

