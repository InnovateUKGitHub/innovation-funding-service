package org.innovateuk.ifs.invite.security;


import org.innovateuk.ifs.BasePermissionRulesTest;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.repository.ApplicationRepository;
import org.innovateuk.ifs.application.resource.ApplicationState;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.invite.domain.ApplicationInvite;
import org.innovateuk.ifs.invite.domain.InviteOrganisation;
import org.innovateuk.ifs.invite.repository.InviteOrganisationRepository;
import org.innovateuk.ifs.invite.resource.ApplicationInviteResource;
import org.innovateuk.ifs.invite.resource.ApplicationKtaInviteResource;
import org.innovateuk.ifs.organisation.builder.OrganisationBuilder;
import org.innovateuk.ifs.organisation.domain.Organisation;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.Optional;

import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.invite.builder.ApplicationInviteBuilder.newApplicationInvite;
import static org.innovateuk.ifs.invite.builder.ApplicationInviteResourceBuilder.newApplicationInviteResource;
import static org.innovateuk.ifs.invite.builder.InviteOrganisationBuilder.newInviteOrganisation;
import static org.innovateuk.ifs.user.builder.ProcessRoleBuilder.newProcessRole;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.user.resource.Role.*;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class ApplicationInvitePermissionRulesTest extends BasePermissionRulesTest<ApplicationInvitePermissionRules> {

    private UserResource leadApplicant;
    private UserResource collaborator;
    private ApplicationInvite invite;
    private ApplicationKtaInviteResource inviteKtaResource;
    private ApplicationInviteResource inviteResource;
    private ApplicationInviteResource inviteResourceCollab;
    private ApplicationInviteResource inviteResourceLead;

    private UserResource otherLeadApplicant;
    private UserResource otherCollaborator;

    @Mock
    private ApplicationRepository applicationRepository;

    @Mock
    private InviteOrganisationRepository inviteOrganisationRepository;

    @Override
    protected ApplicationInvitePermissionRules supplyPermissionRulesUnderTest() {
        return new ApplicationInvitePermissionRules();
    }

    @Before
    public void setup() {

        leadApplicant = newUserResource().build();
        collaborator = newUserResource().build();
        {
            final Competition competition = newCompetition().build();
            final Organisation organisation = OrganisationBuilder.newOrganisation().build();
            final Application application = newApplication().withApplicationState(ApplicationState.OPENED).withCompetition(competition).build();
            final InviteOrganisation inviteOrganisation = newInviteOrganisation().withOrganisation(organisation).build();
            invite = newApplicationInvite().withApplication(application).withInviteOrganisation(inviteOrganisation).build();
            inviteResource = new ApplicationInviteResource();
            inviteResource.setApplication(application.getId());
            inviteResource.setInviteOrganisation(inviteOrganisation.getId());
            inviteKtaResource = new ApplicationKtaInviteResource();
            inviteKtaResource.setApplication(application.getId());
            inviteResourceLead = newApplicationInviteResource().withApplication(application.getId()).withUsers(leadApplicant.getId()).build();
            inviteResourceCollab = newApplicationInviteResource().withApplication(application.getId()).withUsers(collaborator.getId()).build();
            when(inviteOrganisationRepository.findById(inviteOrganisation.getId())).thenReturn(Optional.of(inviteOrganisation));
            when(processRoleRepository.existsByUserIdAndApplicationIdAndRole(leadApplicant.getId(), application.getId(), Role.LEADAPPLICANT)).thenReturn(true);
            when(processRoleRepository.findOneByUserIdAndRoleInAndApplicationId(collaborator.getId(), applicantProcessRoles(), application.getId())).thenReturn(newProcessRole().withRole(COLLABORATOR).build());
            when(processRoleRepository.findByUserIdAndRoleAndApplicationIdAndOrganisationId(collaborator.getId(), Role.COLLABORATOR, application.getId(), organisation.getId())).thenReturn(newProcessRole().withRole(COLLABORATOR).build());
            when(applicationRepository.findById(invite.getTarget().getId())).thenReturn(Optional.of(application));
        }

        otherLeadApplicant = newUserResource().build();
        otherCollaborator = newUserResource().build();
        {
            final Application otherApplication = newApplication().withApplicationState(ApplicationState.OPENED).build();
            final Organisation otherOrganisation = OrganisationBuilder.newOrganisation().build();
            when(processRoleRepository.findOneByUserIdAndRoleInAndApplicationId(otherApplication.getId(), applicantProcessRoles(), otherApplication.getId())).thenReturn(newProcessRole().withRole(LEADAPPLICANT).build());
            when(processRoleRepository.findOneByUserIdAndRoleInAndApplicationId(otherCollaborator.getId(), applicantProcessRoles(), otherApplication.getId())).thenReturn(newProcessRole().withRole(COLLABORATOR).build());
            when(processRoleRepository.findByUserIdAndRoleAndApplicationIdAndOrganisationId(otherCollaborator.getId(), Role.COLLABORATOR, otherApplication.getId(), otherOrganisation.getId())).thenReturn(newProcessRole().withRole(COLLABORATOR).build());
        }
    }

    @Test
    public void leadApplicantCanInviteToTheApplication() {
        assertTrue(rules.leadApplicantCanInviteToTheApplication(invite, leadApplicant));
        assertFalse(rules.leadApplicantCanInviteToTheApplication(invite, collaborator));
        assertFalse(rules.leadApplicantCanInviteToTheApplication(invite, otherLeadApplicant));
    }

    @Test
    public void collaboratorCanInviteToApplicantForTheirOrganisation() {
        assertTrue(rules.collaboratorCanInviteToApplicationForTheirOrganisation(invite, collaborator));
        assertFalse(rules.collaboratorCanInviteToApplicationForTheirOrganisation(invite, leadApplicant));
        assertFalse(rules.collaboratorCanInviteToApplicationForTheirOrganisation(invite, otherCollaborator));
    }

    @Test
    public void leadApplicantCanSaveInviteToTheApplication() {
        assertTrue(rules.leadApplicantCanSaveInviteToTheApplication(inviteResource, leadApplicant));
        assertFalse(rules.leadApplicantCanSaveInviteToTheApplication(inviteResource, collaborator));
        assertFalse(rules.leadApplicantCanSaveInviteToTheApplication(inviteResource, otherLeadApplicant));
    }

    @Test
    public void leadApplicantCanSaveKtaInviteToTheApplication() {
        assertTrue(rules.leadApplicantCanSaveKtaInviteToTheApplication(inviteKtaResource, leadApplicant));
        assertFalse(rules.leadApplicantCanSaveKtaInviteToTheApplication(inviteKtaResource, collaborator));
        assertFalse(rules.leadApplicantCanSaveKtaInviteToTheApplication(inviteKtaResource, otherLeadApplicant));
    }

    @Test
    public void collaboratorCanSaveInviteToApplicantForTheirOrganisation() {
        assertTrue(rules.collaboratorCanSaveInviteToApplicationForTheirOrganisation(inviteResource, collaborator));
        assertFalse(rules.collaboratorCanSaveInviteToApplicationForTheirOrganisation(inviteResource, leadApplicant));
        assertFalse(rules.collaboratorCanSaveInviteToApplicationForTheirOrganisation(inviteResource, otherCollaborator));
    }

    @Test
    public void collaboratorCanReadInviteForTheirApplicationForTheirOrganisation() {
        assertTrue(rules.collaboratorCanReadInviteForTheirApplicationForTheirOrganisation(invite, collaborator));
        assertFalse(rules.collaboratorCanReadInviteForTheirApplicationForTheirOrganisation(invite, leadApplicant));
        assertFalse(rules.collaboratorCanReadInviteForTheirApplicationForTheirOrganisation(invite, otherCollaborator));
    }

    @Test
    public void leadApplicantReadInviteToTheApplication() {
        assertTrue(rules.leadApplicantReadInviteToTheApplication(invite, leadApplicant));
        assertFalse(rules.leadApplicantReadInviteToTheApplication(invite, collaborator));
        assertFalse(rules.leadApplicantReadInviteToTheApplication(invite, otherLeadApplicant));
    }

    @Test
    public void leadCanDeleteNotOwnInvite() {
        assertTrue(rules.leadCanDeleteNotOwnInvite(inviteResource, leadApplicant));
        assertFalse(rules.leadCanDeleteNotOwnInvite(inviteResource, collaborator));
        assertFalse(rules.leadCanDeleteNotOwnInvite(inviteResourceLead, leadApplicant));
    }

    @Test
    public void collaboratorCanDeleteNotOwnInvite() {
        assertFalse(rules.collaboratorCanDeleteNotOwnInvite(inviteResource, leadApplicant));
        assertTrue(rules.collaboratorCanDeleteNotOwnInvite(inviteResource, collaborator));
        assertFalse(rules.collaboratorCanDeleteNotOwnInvite(inviteResourceCollab, collaborator));
    }

    @Test
    public void notDeleteOrSaveWhenApplicationIsNotEditable() {
        Competition competition = newCompetition().build();
        Organisation organisation = OrganisationBuilder.newOrganisation().build();
        Application application = newApplication().withApplicationState(ApplicationState.SUBMITTED).withCompetition(competition).build();
        InviteOrganisation inviteOrganisation = newInviteOrganisation().withOrganisation(organisation).build();
        invite = newApplicationInvite().withApplication(application).withInviteOrganisation(inviteOrganisation).build();
        inviteResource.setApplication(application.getId());
        inviteResource.setInviteOrganisation(inviteOrganisation.getId());
        when(applicationRepository.findById(invite.getTarget().getId())).thenReturn(Optional.of(application));

        assertFalse(rules.leadCanDeleteNotOwnInvite(inviteResource, leadApplicant));
        assertFalse(rules.leadCanDeleteNotOwnInvite(inviteResource, collaborator));
        assertFalse(rules.leadCanDeleteNotOwnInvite(inviteResourceLead, leadApplicant));

        assertFalse(rules.collaboratorCanDeleteNotOwnInvite(inviteResource, leadApplicant));
        assertFalse(rules.collaboratorCanDeleteNotOwnInvite(inviteResource, collaborator));
        assertFalse(rules.collaboratorCanDeleteNotOwnInvite(inviteResourceLead, leadApplicant));

        assertFalse(rules.collaboratorCanSaveInviteToApplicationForTheirOrganisation(inviteResource, collaborator));
        assertFalse(rules.collaboratorCanSaveInviteToApplicationForTheirOrganisation(inviteResource, leadApplicant));
        assertFalse(rules.collaboratorCanSaveInviteToApplicationForTheirOrganisation(inviteResource, otherCollaborator));

        assertFalse(rules.leadApplicantCanInviteToTheApplication(invite, leadApplicant));
        assertFalse(rules.leadApplicantCanInviteToTheApplication(invite, collaborator));
        assertFalse(rules.leadApplicantCanInviteToTheApplication(invite, otherLeadApplicant));
    }
}
