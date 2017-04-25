package org.innovateuk.ifs.invite.security;


import org.innovateuk.ifs.BasePermissionRulesTest;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.repository.ApplicationRepository;
import org.innovateuk.ifs.application.resource.ApplicationState;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.invite.domain.ApplicationInvite;
import org.innovateuk.ifs.invite.domain.InviteOrganisation;
import org.innovateuk.ifs.invite.resource.ApplicationInviteResource;
import org.innovateuk.ifs.user.builder.OrganisationBuilder;
import org.innovateuk.ifs.user.domain.Organisation;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.invite.builder.ApplicationInviteBuilder.newInvite;
import static org.innovateuk.ifs.invite.builder.ApplicationInviteResourceBuilder.newApplicationInviteResource;
import static org.innovateuk.ifs.invite.builder.InviteOrganisationBuilder.newInviteOrganisation;
import static org.innovateuk.ifs.user.builder.ProcessRoleBuilder.newProcessRole;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.user.resource.UserRoleType.COLLABORATOR;
import static org.innovateuk.ifs.user.resource.UserRoleType.LEADAPPLICANT;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class ApplicationInvitePermissionRulesTest extends BasePermissionRulesTest<ApplicationInvitePermissionRules> {

    private UserResource leadApplicant;
    private UserResource collaborator;
    private ApplicationInvite invite;
    private ApplicationInviteResource inviteResource;
    private ApplicationInviteResource inviteResourceLead;

    private UserResource otherLeadApplicant;
    private UserResource otherCollaborator;
    private ApplicationInvite otherInvite;

    @Mock
    private ApplicationRepository applicationRepository;

    @Override
    protected ApplicationInvitePermissionRules supplyPermissionRulesUnderTest() {
        return new ApplicationInvitePermissionRules();
    }

    @Before
    public void setup() throws Exception {

        leadApplicant = newUserResource().build();
        collaborator = newUserResource().build();
        {
            final Competition competition = newCompetition().build();
            final Organisation organisation = OrganisationBuilder.newOrganisation().build();
            final Application application = newApplication().withApplicationState(ApplicationState.OPEN).withCompetition(competition).build();
            final InviteOrganisation inviteOrganisation = newInviteOrganisation().withOrganisation(organisation).build();
            invite = newInvite().withApplication(application).withInviteOrganisation(inviteOrganisation).build();
            inviteResource = new ApplicationInviteResource();
            inviteResource.setApplication(application.getId());
            inviteResource.setInviteOrganisation(inviteOrganisation.getId());
            inviteResourceLead = newApplicationInviteResource().withApplication(application.getId()).withUsers(leadApplicant.getId()).build();
            when(inviteOrganisationRepositoryMock.findOne(inviteOrganisation.getId())).thenReturn(inviteOrganisation);
            when(processRoleRepositoryMock.findByUserIdAndApplicationId(leadApplicant.getId(), application.getId())).thenReturn(newProcessRole().withRole(getRole(LEADAPPLICANT)).build());
            when(processRoleRepositoryMock.findByUserIdAndApplicationId(collaborator.getId(), application.getId())).thenReturn(newProcessRole().withRole(getRole(COLLABORATOR)).build());
            when(processRoleRepositoryMock.findByUserIdAndRoleIdAndApplicationIdAndOrganisationId(collaborator.getId(), getRole(COLLABORATOR).getId(), application.getId(), organisation.getId())).thenReturn(newProcessRole().withRole(getRole(COLLABORATOR)).build());
            when(applicationRepository.findOne(invite.getTarget().getId())).thenReturn(application);
        }

        otherLeadApplicant = newUserResource().build();
        otherCollaborator = newUserResource().build();
        {
            final Application otherApplication = newApplication().withApplicationState(ApplicationState.OPEN).build();
            final Organisation otherOrganisation = OrganisationBuilder.newOrganisation().build();
            final InviteOrganisation otherInviteOrganisation = newInviteOrganisation().withOrganisation(otherOrganisation).build();
            otherInvite = newInvite().withApplication(otherApplication).withInviteOrganisation(otherInviteOrganisation).build();
            when(processRoleRepositoryMock.findByUserIdAndApplicationId(otherApplication.getId(), otherApplication.getId())).thenReturn(newProcessRole().withRole(getRole(LEADAPPLICANT)).build());
            when(processRoleRepositoryMock.findByUserIdAndApplicationId(otherCollaborator.getId(), otherApplication.getId())).thenReturn(newProcessRole().withRole(getRole(COLLABORATOR)).build());
            when(processRoleRepositoryMock.findByUserIdAndRoleIdAndApplicationIdAndOrganisationId(otherCollaborator.getId(), getRole(COLLABORATOR).getId(), otherApplication.getId(), otherOrganisation.getId())).thenReturn(newProcessRole().withRole(getRole(COLLABORATOR)).build());
        }
    }

    @Test
    public void testLeadApplicantCanInviteToTheApplication() {
        assertTrue(rules.leadApplicantCanInviteToTheApplication(invite, leadApplicant));
        assertFalse(rules.leadApplicantCanInviteToTheApplication(invite, collaborator));
        assertFalse(rules.leadApplicantCanInviteToTheApplication(invite, otherLeadApplicant));
    }

    @Test
    public void testCollaboratorCanInviteToApplicantForTheirOrganisation() {
        assertTrue(rules.collaboratorCanInviteToApplicationForTheirOrganisation(invite, collaborator));
        assertFalse(rules.collaboratorCanInviteToApplicationForTheirOrganisation(invite, leadApplicant));
        assertFalse(rules.collaboratorCanInviteToApplicationForTheirOrganisation(invite, otherCollaborator));
    }

    @Test
    public void testLeadApplicantCanSaveInviteToTheApplication() {
        assertTrue(rules.leadApplicantCanSaveInviteToTheApplication(inviteResource, leadApplicant));
        assertFalse(rules.leadApplicantCanSaveInviteToTheApplication(inviteResource, collaborator));
        assertFalse(rules.leadApplicantCanSaveInviteToTheApplication(inviteResource, otherLeadApplicant));
    }

    @Test
    public void testCollaboratorCanSaveInviteToApplicantForTheirOrganisation() {
        assertTrue(rules.collaboratorCanSaveInviteToApplicationForTheirOrganisation(inviteResource, collaborator));
        assertFalse(rules.collaboratorCanSaveInviteToApplicationForTheirOrganisation(inviteResource, leadApplicant));
        assertFalse(rules.collaboratorCanSaveInviteToApplicationForTheirOrganisation(inviteResource, otherCollaborator));
    }

    @Test
    public void testCollaboratorCanReadInviteForTheirApplicationForTheirOrganisation() {
        assertTrue(rules.collaboratorCanReadInviteForTheirApplicationForTheirOrganisation(invite, collaborator));
        assertFalse(rules.collaboratorCanReadInviteForTheirApplicationForTheirOrganisation(invite, leadApplicant));
        assertFalse(rules.collaboratorCanReadInviteForTheirApplicationForTheirOrganisation(invite, otherCollaborator));
    }

    @Test
    public void testLeadApplicantReadInviteToTheApplication() {
        assertTrue(rules.leadApplicantReadInviteToTheApplication(invite, leadApplicant));
        assertFalse(rules.leadApplicantReadInviteToTheApplication(invite, collaborator));
        assertFalse(rules.leadApplicantReadInviteToTheApplication(invite, otherLeadApplicant));
    }

    @Test
    public void testLeadApplicantAndNotDeleteOwnInviteToTheApplication() {
        assertTrue(rules.leadApplicantAndNotDeleteOwnInviteToTheApplication(inviteResource, leadApplicant));
        assertFalse(rules.leadApplicantAndNotDeleteOwnInviteToTheApplication(inviteResource, collaborator));
        assertFalse(rules.leadApplicantAndNotDeleteOwnInviteToTheApplication(inviteResourceLead, leadApplicant));
    }

    @Test
    public void testNotDeleteOrSaveWhenApplicationIsNotEditable() {
        Competition competition = newCompetition().build();
        Organisation organisation = OrganisationBuilder.newOrganisation().build();
        Application application = newApplication().withApplicationState(ApplicationState.SUBMITTED).withCompetition(competition).build();
        InviteOrganisation inviteOrganisation = newInviteOrganisation().withOrganisation(organisation).build();
        invite = newInvite().withApplication(application).withInviteOrganisation(inviteOrganisation).build();
        inviteResource.setApplication(application.getId());
        inviteResource.setInviteOrganisation(inviteOrganisation.getId());
        when(applicationRepository.findOne(invite.getTarget().getId())).thenReturn(application);

        assertFalse(rules.leadApplicantAndNotDeleteOwnInviteToTheApplication(inviteResource, leadApplicant));
        assertFalse(rules.leadApplicantAndNotDeleteOwnInviteToTheApplication(inviteResource, collaborator));
        assertFalse(rules.leadApplicantAndNotDeleteOwnInviteToTheApplication(inviteResourceLead, leadApplicant));

        assertFalse(rules.collaboratorCanSaveInviteToApplicationForTheirOrganisation(inviteResource, collaborator));
        assertFalse(rules.collaboratorCanSaveInviteToApplicationForTheirOrganisation(inviteResource, leadApplicant));
        assertFalse(rules.collaboratorCanSaveInviteToApplicationForTheirOrganisation(inviteResource, otherCollaborator));

        assertFalse(rules.leadApplicantCanInviteToTheApplication(invite, leadApplicant));
        assertFalse(rules.leadApplicantCanInviteToTheApplication(invite, collaborator));
        assertFalse(rules.leadApplicantCanInviteToTheApplication(invite, otherLeadApplicant));
    }
}
