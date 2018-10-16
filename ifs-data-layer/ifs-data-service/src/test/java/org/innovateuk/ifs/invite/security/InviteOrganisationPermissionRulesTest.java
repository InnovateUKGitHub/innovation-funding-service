package org.innovateuk.ifs.invite.security;

import org.innovateuk.ifs.BasePermissionRulesTest;
import org.innovateuk.ifs.application.builder.ApplicationBuilder;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.repository.ApplicationRepository;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.ApplicationState;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.invite.builder.ApplicationInviteResourceBuilder;
import org.innovateuk.ifs.invite.resource.ApplicationInviteResource;
import org.innovateuk.ifs.invite.resource.InviteOrganisationResource;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.competition.resource.CollaborationLevel.COLLABORATIVE;
import static org.innovateuk.ifs.competition.resource.CollaborationLevel.SINGLE;
import static org.innovateuk.ifs.competition.resource.CollaborationLevel.SINGLE_OR_COLLABORATIVE;
import static org.innovateuk.ifs.invite.builder.ApplicationInviteResourceBuilder.newApplicationInviteResource;
import static org.innovateuk.ifs.invite.builder.InviteOrganisationResourceBuilder.newInviteOrganisationResource;
import static org.innovateuk.ifs.organisation.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.innovateuk.ifs.user.builder.ProcessRoleBuilder.newProcessRole;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.user.resource.Role.SUPPORT;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class InviteOrganisationPermissionRulesTest extends BasePermissionRulesTest<InviteOrganisationPermissionRules> {

    private ApplicationResource applicationResource;
    private OrganisationResource organisationResource;
    private UserResource leadApplicant;
    private UserResource collaborator;
    private UserResource support;
    private UserResource otherApplicant;

    @Mock
    private ApplicationRepository applicationRepositoryMock;

    @Override
    protected InviteOrganisationPermissionRules supplyPermissionRulesUnderTest() {
        return new InviteOrganisationPermissionRules();
    }

    @Before
    public void setUp() {
        applicationResource = newApplicationResource().build();
        organisationResource = newOrganisationResource().build();

        leadApplicant = newUserResource().build();
        collaborator = newUserResource().build();
        support = newUserResource().withRolesGlobal(Collections.singletonList(SUPPORT)).build();
        otherApplicant = newUserResource().build();

        when(processRoleRepositoryMock.existsByUserIdAndApplicationIdAndRole(leadApplicant.getId(), applicationResource.getId(), Role.LEADAPPLICANT))
                .thenReturn(true);
        when(processRoleRepositoryMock.existsByUserIdAndApplicationIdAndRole(collaborator.getId(), applicationResource.getId(), Role.COLLABORATOR))
                .thenReturn(true);
        when(processRoleRepositoryMock.findByUserIdAndRoleAndApplicationIdAndOrganisationId(collaborator.getId(),
                Role.COLLABORATOR, applicationResource.getId(), organisationResource.getId())).thenReturn(newProcessRole().withRole(Role.COLLABORATOR).build());
    }

    @Test
    public void leadApplicantCanInviteAnOrganisationToTheApplication() {
        List<ApplicationInviteResource> inviteResource = newApplicationInviteResource().withApplication(applicationResource.getId()).build(5);
        InviteOrganisationResource inviteOrganisationResource = newInviteOrganisationResource().withInviteResources(inviteResource).build();

        assertTrue(rules.leadApplicantCanInviteAnOrganisationToTheApplication(inviteOrganisationResource, leadApplicant));
        assertFalse(rules.leadApplicantCanInviteAnOrganisationToTheApplication(inviteOrganisationResource, collaborator));
        assertFalse(rules.leadApplicantCanInviteAnOrganisationToTheApplication(inviteOrganisationResource, otherApplicant));
    }

    @Test
    public void consortiumCanViewAnyInviteOrganisation() {
        List<ApplicationInviteResource> inviteResource = newApplicationInviteResource().withApplication(applicationResource.getId()).build(5);
        InviteOrganisationResource inviteOrganisationResource = newInviteOrganisationResource().withInviteResources(inviteResource).build();

        assertTrue(rules.consortiumCanViewAnyInviteOrganisation(inviteOrganisationResource, leadApplicant));
        assertTrue(rules.consortiumCanViewAnyInviteOrganisation(inviteOrganisationResource, collaborator));
        assertFalse(rules.consortiumCanViewAnyInviteOrganisation(inviteOrganisationResource, support));
        assertFalse(rules.consortiumCanViewAnyInviteOrganisation(inviteOrganisationResource, otherApplicant));
    }

    @Test
    public void supportCanViewAnyInviteOrganisation() {
        List<ApplicationInviteResource> inviteResource = newApplicationInviteResource().withApplication(applicationResource.getId()).build(5);
        InviteOrganisationResource inviteOrganisationResource = newInviteOrganisationResource().withInviteResources(inviteResource).build();

        assertFalse(rules.supportCanViewAnyInviteOrganisation(inviteOrganisationResource, leadApplicant));
        assertFalse(rules.supportCanViewAnyInviteOrganisation(inviteOrganisationResource, collaborator));
        assertTrue(rules.supportCanViewAnyInviteOrganisation(inviteOrganisationResource, support));
        assertFalse(rules.supportCanViewAnyInviteOrganisation(inviteOrganisationResource, otherApplicant));
    }

    @Test
    public void consortiumCanViewAnInviteOrganisationToTheApplication() {
        List<ApplicationInviteResource> inviteResource = newApplicationInviteResource().withApplication(applicationResource.getId()).build(5);
        InviteOrganisationResource inviteOrganisationResource = newInviteOrganisationResource().withInviteResources(inviteResource).build();

        assertTrue(rules.consortiumCanViewAnInviteOrganisation(inviteOrganisationResource, leadApplicant));
        assertFalse(rules.consortiumCanViewAnInviteOrganisation(inviteOrganisationResource, collaborator));
        assertFalse(rules.consortiumCanViewAnInviteOrganisation(inviteOrganisationResource, otherApplicant));
    }

    @Test
    public void consortiumCanViewAnInviteOrganisationToTheApplicationForAConfirmedOrganisation() {
        List<ApplicationInviteResource> inviteResource = newApplicationInviteResource().withApplication(applicationResource.getId()).build(5);
        InviteOrganisationResource inviteOrganisationResource = newInviteOrganisationResource()
                .withOrganisation(organisationResource.getId())
                .withInviteResources(inviteResource)
                .build();

        assertTrue(rules.consortiumCanViewAnInviteOrganisation(inviteOrganisationResource, leadApplicant));
        assertTrue(rules.consortiumCanViewAnInviteOrganisation(inviteOrganisationResource, collaborator));
        assertFalse(rules.consortiumCanViewAnInviteOrganisation(inviteOrganisationResource, otherApplicant));
    }

    @Test
    public void leadApplicantCanSaveInviteAnOrganisationToTheApplication() {
        List<ApplicationInviteResource> inviteResource = newApplicationInviteResource().withApplication(applicationResource.getId()).build(5);
        InviteOrganisationResource inviteOrganisationResource = newInviteOrganisationResource()
                .withInviteResources(inviteResource)
                .build();

        assertTrue(rules.leadApplicantCanSaveInviteAnOrganisationToTheApplication(inviteOrganisationResource, leadApplicant));
        assertFalse(rules.leadApplicantCanSaveInviteAnOrganisationToTheApplication(inviteOrganisationResource, collaborator));
        assertFalse(rules.leadApplicantCanSaveInviteAnOrganisationToTheApplication(inviteOrganisationResource, otherApplicant));
    }

    @Test
    public void leadApplicantCanCreateApplicationInvitesIfApplicationEditableWhenApplicationCreated() {
        List<ApplicationInviteResource> inviteResource = newApplicationInviteResource().withApplication(applicationResource.getId()).build(5);
        InviteOrganisationResource inviteOrganisationResource = newInviteOrganisationResource()
                .withInviteResources(inviteResource)
                .build();

        Competition competition = newCompetition().withCollaborationLevel(SINGLE_OR_COLLABORATIVE).build();
        Application application = ApplicationBuilder.newApplication()
                .withCompetition(competition)
                .withApplicationState(ApplicationState.CREATED).build();
        when(applicationRepositoryMock.findById(applicationResource.getId()))
                .thenReturn(Optional.of(application));

        assertTrue(rules.leadApplicantCanCreateApplicationInvitesIfApplicationEditable(inviteOrganisationResource, leadApplicant));
        assertTrue(rules.leadApplicantCanCreateApplicationInvitesIfApplicationEditable(inviteOrganisationResource, collaborator));
        assertFalse(rules.leadApplicantCanCreateApplicationInvitesIfApplicationEditable(inviteOrganisationResource, otherApplicant));
    }

    @Test
    public void leadApplicantCanCreateApplicationInvitesIfApplicationEditableWhenApplicationOpen() {
        List<ApplicationInviteResource> inviteResource = newApplicationInviteResource().withApplication(applicationResource.getId()).build(5);
        InviteOrganisationResource inviteOrganisationResource = newInviteOrganisationResource()
                .withInviteResources(inviteResource)
                .build();

        Competition competition = newCompetition().withCollaborationLevel(SINGLE_OR_COLLABORATIVE).build();
        Application application = ApplicationBuilder.newApplication()
                .withCompetition(competition)
                .withApplicationState(ApplicationState.OPEN).build();
        when(applicationRepositoryMock.findById(applicationResource.getId()))
                .thenReturn(Optional.of(application));

        assertTrue(rules.leadApplicantCanCreateApplicationInvitesIfApplicationEditable(inviteOrganisationResource, leadApplicant));
        assertTrue(rules.leadApplicantCanCreateApplicationInvitesIfApplicationEditable(inviteOrganisationResource, collaborator));
        assertFalse(rules.leadApplicantCanCreateApplicationInvitesIfApplicationEditable(inviteOrganisationResource, otherApplicant));
    }

    @Test
    public void leadApplicantAndCollaboratorCannotCreateApplicationInvitesIfApplicationEditableWhenApplicationCreated() {
        List<ApplicationInviteResource> inviteResource = ApplicationInviteResourceBuilder.newApplicationInviteResource().withApplication(applicationResource.getId()).build(5);
        InviteOrganisationResource inviteOrganisationResource = newInviteOrganisationResource()
                .withInviteResources(inviteResource)
                .build();

        when(processRoleRepositoryMock.existsByUserIdAndApplicationIdAndRole(leadApplicant.getId(), applicationResource.getId(), Role.LEADAPPLICANT))
                .thenReturn(false);
        when(processRoleRepositoryMock.existsByUserIdAndApplicationIdAndRole(collaborator.getId(), applicationResource.getId(), Role.COLLABORATOR))
                .thenReturn(false);

        Application application = ApplicationBuilder.newApplication()
                .withApplicationState(ApplicationState.CREATED).build();
        when(applicationRepositoryMock.findById(applicationResource.getId()))
                .thenReturn(Optional.of(application));

        assertFalse(rules.leadApplicantCanCreateApplicationInvitesIfApplicationEditable(inviteOrganisationResource, leadApplicant));
        assertFalse(rules.leadApplicantCanCreateApplicationInvitesIfApplicationEditable(inviteOrganisationResource, collaborator));
        assertFalse(rules.leadApplicantCanCreateApplicationInvitesIfApplicationEditable(inviteOrganisationResource, otherApplicant));
    }

    @Test
    public void leadApplicantAndCollaboratorCannotCreateApplicationInvitesIfApplicationEditableWhenApplicationOpen() {
        List<ApplicationInviteResource> inviteResource = ApplicationInviteResourceBuilder.newApplicationInviteResource().withApplication(applicationResource.getId()).build(5);
        InviteOrganisationResource inviteOrganisationResource = newInviteOrganisationResource()
                .withInviteResources(inviteResource)
                .build();

        when(processRoleRepositoryMock.existsByUserIdAndApplicationIdAndRole(leadApplicant.getId(), applicationResource.getId(), Role.LEADAPPLICANT))
                .thenReturn(false);
        when(processRoleRepositoryMock.existsByUserIdAndApplicationIdAndRole(collaborator.getId(), applicationResource.getId(), Role.COLLABORATOR))
                .thenReturn(false);

        Competition competition = newCompetition().withCollaborationLevel(SINGLE_OR_COLLABORATIVE).build();
        Application application = ApplicationBuilder.newApplication()
                .withCompetition(competition)
                .withApplicationState(ApplicationState.OPEN).build();
        when(applicationRepositoryMock.findById(applicationResource.getId()))
                .thenReturn(Optional.of(application));

        assertFalse(rules.leadApplicantCanCreateApplicationInvitesIfApplicationEditable(inviteOrganisationResource, leadApplicant));
        assertFalse(rules.leadApplicantCanCreateApplicationInvitesIfApplicationEditable(inviteOrganisationResource, collaborator));
        assertFalse(rules.leadApplicantCanCreateApplicationInvitesIfApplicationEditable(inviteOrganisationResource, otherApplicant));
    }

    @Test
    public void leadApplicantCanCreateApplicationInvitesIfApplicationEditableWhenApplicationSubmitted() {
        List<ApplicationInviteResource> inviteResource = newApplicationInviteResource().withApplication(applicationResource.getId()).build(5);
        InviteOrganisationResource inviteOrganisationResource = newInviteOrganisationResource()
                .withInviteResources(inviteResource)
                .build();

        Competition competition = newCompetition().withCollaborationLevel(SINGLE_OR_COLLABORATIVE).build();
        Application application = ApplicationBuilder.newApplication()
                .withCompetition(competition)
                .withApplicationState(ApplicationState.SUBMITTED).build();
        when(applicationRepositoryMock.findById(applicationResource.getId()))
                .thenReturn(Optional.of(application));

        assertFalse(rules.leadApplicantCanCreateApplicationInvitesIfApplicationEditable(inviteOrganisationResource, leadApplicant));
        assertFalse(rules.leadApplicantCanCreateApplicationInvitesIfApplicationEditable(inviteOrganisationResource, collaborator));
        assertFalse(rules.leadApplicantCanCreateApplicationInvitesIfApplicationEditable(inviteOrganisationResource, otherApplicant));
    }

    @Test
    public void leadApplicantCanCreateApplicationInvitesIfApplicationEditable_newCollaboratingOrganisationAndCollaborationLevelIsSingle() {
        List<ApplicationInviteResource> inviteResource = newApplicationInviteResource().withApplication(applicationResource.getId()).build(5);
        InviteOrganisationResource inviteOrganisationResource = newInviteOrganisationResource()
                .withInviteResources(inviteResource)
                .build();

        Competition competition = newCompetition().withCollaborationLevel(SINGLE).build();
        Application application = ApplicationBuilder.newApplication()
                .withCompetition(competition)
                .withApplicationState(ApplicationState.OPEN).build();
        when(applicationRepositoryMock.findOne(applicationResource.getId()))
                .thenReturn(application);

        assertFalse(rules.leadApplicantCanCreateApplicationInvitesIfApplicationEditable(inviteOrganisationResource,
                leadApplicant));
    }

    @Test
    public void leadApplicantCanCreateApplicationInvitesIfApplicationEditable_newCollaboratingOrganisationAndCollaborationLevelIsSingleOrCollaborative() {
        List<ApplicationInviteResource> inviteResource = newApplicationInviteResource().withApplication(applicationResource.getId()).build(5);
        InviteOrganisationResource inviteOrganisationResource = newInviteOrganisationResource()
                .withInviteResources(inviteResource)
                .build();

        Competition competition = newCompetition().withCollaborationLevel(SINGLE_OR_COLLABORATIVE).build();
        Application application = ApplicationBuilder.newApplication()
                .withCompetition(competition)
                .withApplicationState(ApplicationState.OPEN).build();
        when(applicationRepositoryMock.findOne(applicationResource.getId()))
                .thenReturn(application);

        assertTrue(rules.leadApplicantCanCreateApplicationInvitesIfApplicationEditable(inviteOrganisationResource, leadApplicant));
    }

    @Test
    public void leadApplicantCanCreateApplicationInvitesIfApplicationEditable_existingOrganisationAndCollaborationLevelIsSingleOrCollaborative() {
        long existingOrganisationId = 1L;

        List<ApplicationInviteResource> inviteResource = newApplicationInviteResource().withApplication(applicationResource.getId()).build(5);
        InviteOrganisationResource inviteOrganisationResource = newInviteOrganisationResource()
                .withOrganisation(existingOrganisationId)
                .withInviteResources(inviteResource)
                .build();

        Competition competition = newCompetition().withCollaborationLevel(SINGLE_OR_COLLABORATIVE).build();
        Application application = ApplicationBuilder.newApplication()
                .withCompetition(competition)
                .withApplicationState(ApplicationState.OPEN).build();
        when(applicationRepositoryMock.findOne(applicationResource.getId()))
                .thenReturn(application);

        assertTrue(rules.leadApplicantCanCreateApplicationInvitesIfApplicationEditable(inviteOrganisationResource, leadApplicant));
    }

    @Test
    public void leadApplicantCanCreateApplicationInvitesIfApplicationEditable_newCollaboratingOrganisationAndCollaborationLevelIsCollaborative() {
        List<ApplicationInviteResource> inviteResource = newApplicationInviteResource().withApplication(applicationResource.getId()).build(5);
        InviteOrganisationResource inviteOrganisationResource = newInviteOrganisationResource()
                .withInviteResources(inviteResource)
                .build();

        Competition competition = newCompetition().withCollaborationLevel(COLLABORATIVE).build();
        Application application = ApplicationBuilder.newApplication()
                .withCompetition(competition)
                .withApplicationState(ApplicationState.OPEN).build();
        when(applicationRepositoryMock.findOne(applicationResource.getId()))
                .thenReturn(application);

        assertTrue(rules.leadApplicantCanCreateApplicationInvitesIfApplicationEditable(inviteOrganisationResource, leadApplicant));
    }

    @Test
    public void leadApplicantCanCreateApplicationInvitesIfApplicationEditable_existingOrganisationAndCollaborationLevelIsCollaborative() {
        long existingOrganisationId = 1L;

        List<ApplicationInviteResource> inviteResource = newApplicationInviteResource().withApplication(applicationResource.getId()).build(5);
        InviteOrganisationResource inviteOrganisationResource = newInviteOrganisationResource()
                .withOrganisation(existingOrganisationId)
                .withInviteResources(inviteResource)
                .build();

        Competition competition = newCompetition().withCollaborationLevel(COLLABORATIVE).build();
        Application application = ApplicationBuilder.newApplication()
                .withCompetition(competition)
                .withApplicationState(ApplicationState.OPEN).build();
        when(applicationRepositoryMock.findOne(applicationResource.getId()))
                .thenReturn(application);

        assertTrue(rules.leadApplicantCanCreateApplicationInvitesIfApplicationEditable(inviteOrganisationResource, leadApplicant));
    }
}
