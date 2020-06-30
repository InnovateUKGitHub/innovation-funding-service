package org.innovateuk.ifs.application.readonly.populator;

import org.innovateuk.ifs.address.resource.AddressResource;
import org.innovateuk.ifs.address.resource.OrganisationAddressType;
import org.innovateuk.ifs.application.readonly.ApplicationReadOnlyData;
import org.innovateuk.ifs.application.readonly.viewmodel.ApplicationTeamOrganisationReadOnlyViewModel;
import org.innovateuk.ifs.application.readonly.viewmodel.ApplicationTeamReadOnlyViewModel;
import org.innovateuk.ifs.application.readonly.viewmodel.ApplicationTeamUserReadOnlyViewModel;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.ApplicationOrganisationAddressRestService;
import org.innovateuk.ifs.competition.resource.CollaborationLevel;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.form.resource.QuestionResource;
import org.innovateuk.ifs.invite.constant.InviteStatus;
import org.innovateuk.ifs.invite.resource.ApplicationInviteResource;
import org.innovateuk.ifs.invite.resource.InviteOrganisationResource;
import org.innovateuk.ifs.invite.service.InviteRestService;
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
import static java.util.Optional.empty;
import static org.innovateuk.ifs.address.builder.AddressResourceBuilder.newAddressResource;
import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.application.readonly.ApplicationReadOnlySettings.defaultSettings;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.form.builder.QuestionResourceBuilder.newQuestionResource;
import static org.innovateuk.ifs.invite.builder.ApplicationInviteResourceBuilder.newApplicationInviteResource;
import static org.innovateuk.ifs.invite.builder.InviteOrganisationResourceBuilder.newInviteOrganisationResource;
import static org.innovateuk.ifs.organisation.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.innovateuk.ifs.user.builder.ProcessRoleResourceBuilder.newProcessRoleResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ApplicationTeamReadOnlyViewModelPopulatorTest {

    @InjectMocks
    private ApplicationTeamReadOnlyViewModelPopulator populator;

    @Mock
    private InviteRestService inviteRestService;

    @Mock
    private UserRestService userRestService;

    @Mock
    private OrganisationRestService organisationRestService;

    @Mock
    private ApplicationOrganisationAddressRestService applicationOrganisationAddressRestService;

    @Test
    public void populate() {
        UserResource user = newUserResource().withRoleGlobal(Role.SUPPORT).build();
        UserResource collaborator = newUserResource().build();
        OrganisationResource leadOrganisation = newOrganisationResource()
                .withName("Lead")
                .build();
        OrganisationResource collaboratorOrganisation = newOrganisationResource()
                .withName("Collaborator")
                .withIsInternational(true)
                .build();

        ProcessRoleResource leadRole = newProcessRoleResource()
                .withRole(Role.LEADAPPLICANT)
                .withOrganisation(leadOrganisation.getId())
                .withUser(user)
                .build();

        ProcessRoleResource collaboratorRole = newProcessRoleResource()
                .withRole(Role.COLLABORATOR)
                .withOrganisation(collaboratorOrganisation.getId())
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
                .withOrganisationNameConfirmed(collaboratorOrganisation.getName())
                .withOrganisation(collaboratorOrganisation.getId())
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
        QuestionResource question = newQuestionResource().build();

        AddressResource address = newAddressResource().build();

        when(userRestService.findProcessRole(application.getId())).thenReturn(restSuccess(asList(leadRole, collaboratorRole)));
        when(inviteRestService.getInvitesByApplication(application.getId())).thenReturn(restSuccess(asList(collaboratorOrganisationInvite, invitedOrganisation)));
        when(organisationRestService.getOrganisationsByApplicationId(application.getId())).thenReturn(restSuccess(asList(leadOrganisation, collaboratorOrganisation)));
        when(userRestService.findUserByEmail(any())).thenReturn(restSuccess(newUserResource().withPhoneNumber("999").build()));
        when(applicationOrganisationAddressRestService.getAddress(application.getId(), collaboratorOrganisation.getId(), OrganisationAddressType.INTERNATIONAL)).thenReturn(restSuccess(address));
        ApplicationReadOnlyData data = new ApplicationReadOnlyData(application, competition, user, empty(), emptyList(), emptyList(), emptyList(), emptyList(), emptyList());

        ApplicationTeamReadOnlyViewModel viewModel = populator.populate(competition, question, data, defaultSettings());

        assertEquals((Long) application.getId(), viewModel.getApplicationId());
        assertEquals((long) question.getId(), viewModel.getQuestionId());
        assertEquals(3, viewModel.getOrganisations().size());

        ApplicationTeamOrganisationReadOnlyViewModel leadOrganisationViewModel = viewModel.getOrganisations().get(0);
        assertEquals("Lead", leadOrganisationViewModel.getName());
        assertEquals(1, leadOrganisationViewModel.getUsers().size());
        assertTrue(leadOrganisationViewModel.isLead());
        assertTrue(leadOrganisationViewModel.isExisting());
        assertNull(leadOrganisationViewModel.getAddress());

        ApplicationTeamUserReadOnlyViewModel leadUserViewModel = leadOrganisationViewModel.getUsers().get(0);
        assertTrue(leadUserViewModel.isLead());
        assertFalse(leadUserViewModel.isInvite());
        assertEquals("999", leadUserViewModel.getPhone());

        ApplicationTeamOrganisationReadOnlyViewModel collaboratorOrganisationViewModel = viewModel.getOrganisations().get(1);
        assertEquals("Collaborator", collaboratorOrganisationViewModel.getName());
        assertEquals(1, collaboratorOrganisationViewModel.getUsers().size());
        assertFalse(collaboratorOrganisationViewModel.isLead());
        assertTrue(collaboratorOrganisationViewModel.isExisting());
        assertSame(address, collaboratorOrganisationViewModel.getAddress());

        ApplicationTeamUserReadOnlyViewModel collaboratorUserViewModel = collaboratorOrganisationViewModel.getUsers().get(0);
        assertFalse(collaboratorUserViewModel.isLead());
        assertFalse(collaboratorUserViewModel.isInvite());
        assertEquals("999", collaboratorUserViewModel.getPhone());

        ApplicationTeamOrganisationReadOnlyViewModel inviteOrganisationViewModel = viewModel.getOrganisations().get(2);
        assertEquals("New organisation", inviteOrganisationViewModel.getName());
        assertEquals(1, inviteOrganisationViewModel.getUsers().size());
        assertFalse(inviteOrganisationViewModel.isLead());
        assertFalse(inviteOrganisationViewModel.isExisting());
        assertNull(inviteOrganisationViewModel.getType());
        assertNull(inviteOrganisationViewModel.getAddress());

        ApplicationTeamUserReadOnlyViewModel inviteViewModel = inviteOrganisationViewModel.getUsers().get(0);
        assertTrue(inviteViewModel.isInvite());
        assertEquals("New user (pending for 10 days)", inviteViewModel.getName());
    }
}