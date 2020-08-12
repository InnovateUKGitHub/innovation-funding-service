package org.innovateuk.ifs.application.forms.questions.team.populator;

import org.innovateuk.ifs.application.forms.questions.team.viewmodel.ApplicationTeamOrganisationViewModel;
import org.innovateuk.ifs.application.forms.questions.team.viewmodel.ApplicationTeamRowViewModel;
import org.innovateuk.ifs.application.forms.questions.team.viewmodel.ApplicationTeamViewModel;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.QuestionStatusResource;
import org.innovateuk.ifs.application.service.ApplicationService;
import org.innovateuk.ifs.application.service.QuestionStatusRestService;
import org.innovateuk.ifs.competition.resource.CollaborationLevel;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.invite.constant.InviteStatus;
import org.innovateuk.ifs.invite.resource.ApplicationInviteResource;
import org.innovateuk.ifs.invite.resource.InviteOrganisationResource;
import org.innovateuk.ifs.invite.service.InviteRestService;
import org.innovateuk.ifs.invite.service.KtaInviteRestService;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.OrganisationRestService;
import org.innovateuk.ifs.user.service.UserRestService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.ZonedDateTime;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.application.builder.QuestionStatusResourceBuilder.newQuestionStatusResource;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.invite.builder.ApplicationInviteResourceBuilder.newApplicationInviteResource;
import static org.innovateuk.ifs.invite.builder.InviteOrganisationResourceBuilder.newInviteOrganisationResource;
import static org.innovateuk.ifs.organisation.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.innovateuk.ifs.user.builder.ProcessRoleResourceBuilder.newProcessRoleResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ApplicationTeamPopulatorTest {

    @InjectMocks
    private ApplicationTeamPopulator populator;

    @Mock
    private InviteRestService inviteRestService;

    @Mock
    private KtaInviteRestService ktaInviteRestService;

    @Mock
    private ApplicationService applicationService;

    @Mock
    private UserRestService userRestService;

    @Mock
    private OrganisationRestService organisationRestService;

    @Mock
    private QuestionStatusRestService questionStatusRestService;

    @Mock
    private CompetitionRestService competitionRestService;

    @Test
    public void populate() {
        long questionId = 3L;
        UserResource user = newUserResource().build();
        UserResource collaborator = newUserResource().build();
        OrganisationResource leadOrganisation = newOrganisationResource()
                .withName("Lead")
                .build();
        OrganisationResource collboratorOrganisation = newOrganisationResource()
                .withName("Collaborator")
                .build();

        ProcessRoleResource leadRole = newProcessRoleResource()
                .withRole(Role.LEADAPPLICANT)
                .withOrganisation(leadOrganisation.getId())
                .withUser(user)
                .build();

        ProcessRoleResource collaboratorRole = newProcessRoleResource()
                .withRole(Role.COLLABORATOR)
                .withOrganisation(collboratorOrganisation.getId())
                .withUser(collaborator)
                .build();

        ApplicationInviteResource collaboratorInvite = newApplicationInviteResource()
                .withName("Collaborator Invite")
                .withStatus(InviteStatus.OPENED)
                .withUsers(collaborator.getId())
                .build();

        ApplicationInviteResource newUserInvite = newApplicationInviteResource()
                .withName("New user")
                .withStatus(InviteStatus.SENT)
                .withSentOn(ZonedDateTime.now().minusDays(10).minusHours(1))
                .build();

        InviteOrganisationResource collaboratorOrganisationInvite = newInviteOrganisationResource()
                .withOrganisationNameConfirmed(collboratorOrganisation.getName())
                .withOrganisation(collboratorOrganisation.getId())
                .withInviteResources(singletonList(collaboratorInvite))
                .build();

        InviteOrganisationResource invitedOrganisation = newInviteOrganisationResource()
                .withOrganisationName("New organisation")
                .withInviteResources(singletonList(newUserInvite))
                .build();

        CompetitionResource competition = newCompetitionResource()
                .withCollaborationLevel(CollaborationLevel.SINGLE)
                .build();
        ApplicationResource application = newApplicationResource()
                .withCompetition(competition.getId())
                .build();
        QuestionStatusResource status = newQuestionStatusResource()
                .withQuestion(questionId)
                .withMarkedAsComplete(true)
                .build();

        when(applicationService.getById(application.getId())).thenReturn(application);
        when(competitionRestService.getCompetitionById(application.getCompetition())).thenReturn(restSuccess(competition));
        when(userRestService.findProcessRole(application.getId())).thenReturn(restSuccess(asList(leadRole, collaboratorRole)));
        when(inviteRestService.getInvitesByApplication(application.getId())).thenReturn(restSuccess(asList(collaboratorOrganisationInvite, invitedOrganisation)));
        when(ktaInviteRestService.getKtaInvitesByApplication(application.getId())).thenReturn(restSuccess(emptyList()));
        when(organisationRestService.getOrganisationsByApplicationId(application.getId())).thenReturn(restSuccess(asList(collboratorOrganisation, leadOrganisation)));
        when(questionStatusRestService.findQuestionStatusesByQuestionAndApplicationId(questionId, application.getId())).thenReturn(restSuccess(singletonList(status)));

        ApplicationTeamViewModel viewModel = populator.populate(application.getId(), questionId, user);

        assertEquals((Long) application.getId(), viewModel.getApplicationId());
        assertEquals(application.getName(), viewModel.getApplicationName());
        assertEquals((long) user.getId(), viewModel.getLoggedInUserId());
        assertEquals(questionId, viewModel.getQuestionId());
        assertTrue(viewModel.isAnyPendingInvites());
        assertTrue(viewModel.isReadOnly());
        assertTrue(viewModel.isCollaborationLevelSingle());
        assertTrue(viewModel.isComplete());
        assertTrue(viewModel.isLeadApplicant());

        assertEquals(3, viewModel.getOrganisations().size());

        ApplicationTeamOrganisationViewModel leadOrganisationViewModel = viewModel.getOrganisations().get(0);
        assertEquals("Lead", leadOrganisationViewModel.getName());
        assertEquals((long) leadOrganisation.getId(), leadOrganisationViewModel.getId());
        assertEquals(1, leadOrganisationViewModel.getRows().size());
        assertTrue(leadOrganisationViewModel.isLead());
        assertTrue(leadOrganisationViewModel.isEditable());
        assertTrue(leadOrganisationViewModel.isExisting());

        ApplicationTeamRowViewModel leadUserViewModel = leadOrganisationViewModel.getRows().get(0);
        assertEquals(user.getId(), leadUserViewModel.getId());
        assertTrue(leadUserViewModel.isLead());
        assertFalse(leadUserViewModel.isInvite());
        assertNull(leadUserViewModel.getInviteId());

        ApplicationTeamOrganisationViewModel collaboratorOrganisationViewModel = viewModel.getOrganisations().get(1);
        assertEquals("Collaborator", collaboratorOrganisationViewModel.getName());
        assertEquals((long) collboratorOrganisation.getId(), collaboratorOrganisationViewModel.getId());
        assertEquals(1, collaboratorOrganisationViewModel.getRows().size());
        assertFalse(collaboratorOrganisationViewModel.isLead());
        assertTrue(collaboratorOrganisationViewModel.isEditable());
        assertTrue(collaboratorOrganisationViewModel.isExisting());

        ApplicationTeamRowViewModel collaboratorUserViewModel = collaboratorOrganisationViewModel.getRows().get(0);
        assertEquals(collaborator.getId(), collaboratorUserViewModel.getId());
        assertFalse(collaboratorUserViewModel.isLead());
        assertFalse(collaboratorUserViewModel.isInvite());
        assertEquals(collaboratorInvite.getId(), collaboratorUserViewModel.getInviteId());

        ApplicationTeamOrganisationViewModel inviteOrganisationViewModel = viewModel.getOrganisations().get(2);
        assertEquals("New organisation", inviteOrganisationViewModel.getName());
        assertEquals((long) invitedOrganisation.getId(), inviteOrganisationViewModel.getId());
        assertEquals(1, inviteOrganisationViewModel.getRows().size());
        assertFalse(inviteOrganisationViewModel.isLead());
        assertTrue(inviteOrganisationViewModel.isEditable());
        assertFalse(inviteOrganisationViewModel.isExisting());

        ApplicationTeamRowViewModel inviteViewModel = inviteOrganisationViewModel.getRows().get(0);
        assertEquals(newUserInvite.getId(), inviteViewModel.getId());
        assertTrue(inviteViewModel.isInvite());
        assertEquals(newUserInvite.getId(), inviteViewModel.getInviteId());
        assertEquals("New user (pending for 10 days)", inviteViewModel.getName());
    }
}
