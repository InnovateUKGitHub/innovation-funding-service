package com.worth.ifs.invite.security;


import com.worth.ifs.BasePermissionRulesTest;
import com.worth.ifs.application.domain.Application;
import com.worth.ifs.competition.domain.Competition;
import com.worth.ifs.invite.domain.ApplicationInvite;
import com.worth.ifs.invite.domain.InviteOrganisation;
import com.worth.ifs.invite.resource.ApplicationInviteResource;
import com.worth.ifs.user.builder.OrganisationBuilder;
import com.worth.ifs.user.domain.Organisation;
import com.worth.ifs.user.resource.UserResource;
import org.junit.Before;
import org.junit.Test;

import static com.worth.ifs.application.builder.ApplicationBuilder.newApplication;
import static com.worth.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static com.worth.ifs.invite.builder.ApplicationInviteBuilder.newInvite;
import static com.worth.ifs.invite.builder.ApplicationInviteResourceBuilder.newApplicationInviteResource;
import static com.worth.ifs.invite.builder.InviteOrganisationBuilder.newInviteOrganisation;
import static com.worth.ifs.user.builder.ProcessRoleBuilder.newProcessRole;
import static com.worth.ifs.user.builder.UserResourceBuilder.newUserResource;
import static com.worth.ifs.user.resource.UserRoleType.COLLABORATOR;
import static com.worth.ifs.user.resource.UserRoleType.LEADAPPLICANT;
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
            final Application application = newApplication().withCompetition(competition).build();
            final InviteOrganisation inviteOrganisation = newInviteOrganisation().withOrganisation(organisation).build();
            invite = newInvite().withApplication(application).withInviteOrganisation(inviteOrganisation).build();
            inviteResource = new ApplicationInviteResource();
            inviteResource.setApplication(application.getId());
            inviteResource.setInviteOrganisation(inviteOrganisation.getId());
            inviteResourceLead = newApplicationInviteResource().withApplication(application.getId()).withUsers(leadApplicant.getId()).build();
            when(inviteOrganisationRepositoryMock.findOne(inviteOrganisation.getId())).thenReturn(inviteOrganisation);
            when(applicationInviteRepositoryMock.findOne(invite.getId())).thenReturn(invite);
            when(processRoleRepositoryMock.findByUserIdAndApplicationId(leadApplicant.getId(), application.getId())).thenReturn(newProcessRole().withRole(getRole(LEADAPPLICANT)).build());
            when(processRoleRepositoryMock.findByUserIdAndApplicationId(collaborator.getId(), application.getId())).thenReturn(newProcessRole().withRole(getRole(COLLABORATOR)).build());
            when(processRoleRepositoryMock.findByUserIdAndRoleIdAndApplicationIdAndOrganisationId(collaborator.getId(), getRole(COLLABORATOR).getId(), application.getId(), organisation.getId())).thenReturn(newProcessRole().withRole(getRole(COLLABORATOR)).build());

        }
        otherLeadApplicant = newUserResource().build();
        otherCollaborator = newUserResource().build();
        {
            final Application otherApplication = newApplication().build();
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
}
