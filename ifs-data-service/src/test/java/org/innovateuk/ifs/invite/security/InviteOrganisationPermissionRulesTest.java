package org.innovateuk.ifs.invite.security;

import org.innovateuk.ifs.BasePermissionRulesTest;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.invite.builder.InviteResourceBuilder;
import org.innovateuk.ifs.invite.resource.ApplicationInviteResource;
import org.innovateuk.ifs.invite.resource.InviteOrganisationResource;
import org.innovateuk.ifs.user.domain.Role;
import org.innovateuk.ifs.user.resource.OrganisationResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.invite.builder.InviteOrganisationResourceBuilder.newInviteOrganisationResource;
import static org.innovateuk.ifs.user.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.innovateuk.ifs.user.builder.ProcessRoleBuilder.newProcessRole;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.user.resource.UserRoleType.COLLABORATOR;
import static org.innovateuk.ifs.user.resource.UserRoleType.LEADAPPLICANT;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class InviteOrganisationPermissionRulesTest extends BasePermissionRulesTest<InviteOrganisationPermissionRules> {

    private ApplicationResource applicationResource;
    private OrganisationResource organisationResource;
    private UserResource leadApplicant;
    private UserResource collaborator;
    private UserResource otherApplicant;

    @Override
    protected InviteOrganisationPermissionRules supplyPermissionRulesUnderTest() {
        return new InviteOrganisationPermissionRules();
    }

    @Before
    public void setUp() throws Exception {
        applicationResource = newApplicationResource().build();
        organisationResource = newOrganisationResource().build();

        leadApplicant = newUserResource().build();
        collaborator = newUserResource().build();
        otherApplicant = newUserResource().build();

        Role collaboratorRole = getRole(COLLABORATOR);

        when(processRoleRepositoryMock.findByUserIdAndApplicationId(leadApplicant.getId(), applicationResource.getId()))
                .thenReturn(newProcessRole().withRole(getRole(LEADAPPLICANT)).build());
        when(processRoleRepositoryMock.findByUserIdAndApplicationId(collaborator.getId(), applicationResource.getId()))
                .thenReturn(newProcessRole().withRole(getRole(COLLABORATOR)).build());
        when(processRoleRepositoryMock.findByUserIdAndRoleIdAndApplicationIdAndOrganisationId(collaborator.getId(),
                collaboratorRole.getId(), applicationResource.getId(), organisationResource.getId())).thenReturn(newProcessRole().withRole(getRole(COLLABORATOR)).build());
    }

    @Test
    public void testLeadApplicantCanInviteAnOrganisationToTheApplication() throws Exception {
        List<ApplicationInviteResource> inviteResource = InviteResourceBuilder.newInviteResource().withApplication(applicationResource.getId()).build(5);
        InviteOrganisationResource inviteOrganisationResource = newInviteOrganisationResource().withInviteResources(inviteResource).build();

        assertTrue(rules.leadApplicantCanInviteAnOrganisationToTheApplication(inviteOrganisationResource, leadApplicant));
        assertFalse(rules.leadApplicantCanInviteAnOrganisationToTheApplication(inviteOrganisationResource, collaborator));
        assertFalse(rules.leadApplicantCanInviteAnOrganisationToTheApplication(inviteOrganisationResource, otherApplicant));
    }

    @Test
    public void testConsortiumCanViewAnyInviteOrganisation() throws Exception {
        List<ApplicationInviteResource> inviteResource = InviteResourceBuilder.newInviteResource().withApplication(applicationResource.getId()).build(5);
        InviteOrganisationResource inviteOrganisationResource = newInviteOrganisationResource().withInviteResources(inviteResource).build();

        assertTrue(rules.consortiumCanViewAnyInviteOrganisation(inviteOrganisationResource, leadApplicant));
        assertTrue(rules.consortiumCanViewAnyInviteOrganisation(inviteOrganisationResource, collaborator));
        assertFalse(rules.consortiumCanViewAnyInviteOrganisation(inviteOrganisationResource, otherApplicant));
    }

    @Test
    public void testConsortiumCanViewAnInviteOrganisationToTheApplication() throws Exception {
        List<ApplicationInviteResource> inviteResource = InviteResourceBuilder.newInviteResource().withApplication(applicationResource.getId()).build(5);
        InviteOrganisationResource inviteOrganisationResource = newInviteOrganisationResource().withInviteResources(inviteResource).build();

        assertTrue(rules.consortiumCanViewAnInviteOrganisation(inviteOrganisationResource, leadApplicant));
        assertFalse(rules.consortiumCanViewAnInviteOrganisation(inviteOrganisationResource, collaborator));
        assertFalse(rules.consortiumCanViewAnInviteOrganisation(inviteOrganisationResource, otherApplicant));
    }

    @Test
    public void testConsortiumCanViewAnInviteOrganisationToTheApplicationForAConfirmedOrganisation() throws Exception {
        List<ApplicationInviteResource> inviteResource = InviteResourceBuilder.newInviteResource().withApplication(applicationResource.getId()).build(5);
        InviteOrganisationResource inviteOrganisationResource = newInviteOrganisationResource()
                .withOrganisation(organisationResource.getId())
                .withInviteResources(inviteResource).build();

        assertTrue(rules.consortiumCanViewAnInviteOrganisation(inviteOrganisationResource, leadApplicant));
        assertTrue(rules.consortiumCanViewAnInviteOrganisation(inviteOrganisationResource, collaborator));
        assertFalse(rules.consortiumCanViewAnInviteOrganisation(inviteOrganisationResource, otherApplicant));
    }

    @Test
    public void testLeadApplicantCanSaveInviteAnOrganisationToTheApplication() throws Exception {
        List<ApplicationInviteResource> inviteResource = InviteResourceBuilder.newInviteResource().withApplication(applicationResource.getId()).build(5);
        InviteOrganisationResource inviteOrganisationResource = newInviteOrganisationResource().withInviteResources(inviteResource).build();

        assertTrue(rules.leadApplicantCanSaveInviteAnOrganisationToTheApplication(inviteOrganisationResource, leadApplicant));
        assertFalse(rules.leadApplicantCanSaveInviteAnOrganisationToTheApplication(inviteOrganisationResource, collaborator));
        assertFalse(rules.leadApplicantCanSaveInviteAnOrganisationToTheApplication(inviteOrganisationResource, otherApplicant));
    }

}
