package org.innovateuk.ifs.invite.security;

import org.innovateuk.ifs.BasePermissionRulesTest;
import org.innovateuk.ifs.application.builder.ApplicationBuilder;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.ApplicationState;
import org.innovateuk.ifs.invite.builder.ApplicationInviteResourceBuilder;
import org.innovateuk.ifs.invite.resource.ApplicationInviteResource;
import org.innovateuk.ifs.invite.resource.InviteOrganisationResource;
import org.innovateuk.ifs.user.resource.OrganisationResource;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.invite.builder.ApplicationInviteResourceBuilder.newApplicationInviteResource;
import static org.innovateuk.ifs.invite.builder.InviteOrganisationResourceBuilder.newInviteOrganisationResource;
import static org.innovateuk.ifs.user.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.innovateuk.ifs.user.builder.ProcessRoleBuilder.newProcessRole;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
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

        when(processRoleRepositoryMock.existsByUserIdAndApplicationIdAndRole(leadApplicant.getId(), applicationResource.getId(), Role.LEADAPPLICANT))
                .thenReturn(true);
        when(processRoleRepositoryMock.existsByUserIdAndApplicationIdAndRole(collaborator.getId(), applicationResource.getId(), Role.COLLABORATOR))
                .thenReturn(true);
        when(processRoleRepositoryMock.findByUserIdAndRoleAndApplicationIdAndOrganisationId(collaborator.getId(),
                Role.COLLABORATOR, applicationResource.getId(), organisationResource.getId())).thenReturn(newProcessRole().withRole(Role.COLLABORATOR).build());
    }

    @Test
    public void testLeadApplicantCanInviteAnOrganisationToTheApplication() throws Exception {
        List<ApplicationInviteResource> inviteResource = newApplicationInviteResource().withApplication(applicationResource.getId()).build(5);
        InviteOrganisationResource inviteOrganisationResource = newInviteOrganisationResource().withInviteResources(inviteResource).build();

        assertTrue(rules.leadApplicantCanInviteAnOrganisationToTheApplication(inviteOrganisationResource, leadApplicant));
        assertFalse(rules.leadApplicantCanInviteAnOrganisationToTheApplication(inviteOrganisationResource, collaborator));
        assertFalse(rules.leadApplicantCanInviteAnOrganisationToTheApplication(inviteOrganisationResource, otherApplicant));
    }

    @Test
    public void testConsortiumCanViewAnyInviteOrganisation() throws Exception {
        List<ApplicationInviteResource> inviteResource = newApplicationInviteResource().withApplication(applicationResource.getId()).build(5);
        InviteOrganisationResource inviteOrganisationResource = newInviteOrganisationResource().withInviteResources(inviteResource).build();

        assertTrue(rules.consortiumCanViewAnyInviteOrganisation(inviteOrganisationResource, leadApplicant));
        assertTrue(rules.consortiumCanViewAnyInviteOrganisation(inviteOrganisationResource, collaborator));
        assertFalse(rules.consortiumCanViewAnyInviteOrganisation(inviteOrganisationResource, otherApplicant));
    }

    @Test
    public void testConsortiumCanViewAnInviteOrganisationToTheApplication() throws Exception {
        List<ApplicationInviteResource> inviteResource = newApplicationInviteResource().withApplication(applicationResource.getId()).build(5);
        InviteOrganisationResource inviteOrganisationResource = newInviteOrganisationResource().withInviteResources(inviteResource).build();

        assertTrue(rules.consortiumCanViewAnInviteOrganisation(inviteOrganisationResource, leadApplicant));
        assertFalse(rules.consortiumCanViewAnInviteOrganisation(inviteOrganisationResource, collaborator));
        assertFalse(rules.consortiumCanViewAnInviteOrganisation(inviteOrganisationResource, otherApplicant));
    }

    @Test
    public void testConsortiumCanViewAnInviteOrganisationToTheApplicationForAConfirmedOrganisation() throws Exception {
        List<ApplicationInviteResource> inviteResource = newApplicationInviteResource().withApplication(applicationResource.getId()).build(5);
        InviteOrganisationResource inviteOrganisationResource = newInviteOrganisationResource()
                .withOrganisation(organisationResource.getId())
                .withInviteResources(inviteResource).build();

        assertTrue(rules.consortiumCanViewAnInviteOrganisation(inviteOrganisationResource, leadApplicant));
        assertTrue(rules.consortiumCanViewAnInviteOrganisation(inviteOrganisationResource, collaborator));
        assertFalse(rules.consortiumCanViewAnInviteOrganisation(inviteOrganisationResource, otherApplicant));
    }

    @Test
    public void testLeadApplicantCanSaveInviteAnOrganisationToTheApplication() throws Exception {
        List<ApplicationInviteResource> inviteResource = newApplicationInviteResource().withApplication(applicationResource.getId()).build(5);
        InviteOrganisationResource inviteOrganisationResource = newInviteOrganisationResource().withInviteResources(inviteResource).build();

        assertTrue(rules.leadApplicantCanSaveInviteAnOrganisationToTheApplication(inviteOrganisationResource, leadApplicant));
        assertFalse(rules.leadApplicantCanSaveInviteAnOrganisationToTheApplication(inviteOrganisationResource, collaborator));
        assertFalse(rules.leadApplicantCanSaveInviteAnOrganisationToTheApplication(inviteOrganisationResource, otherApplicant));
    }

    @Test
    public void leadApplicantCanCreateApplicationInvitesIfApplicationEditableWhenApplicationCreated() throws Exception {
        List<ApplicationInviteResource> inviteResource = newApplicationInviteResource().withApplication(applicationResource.getId()).build(5);
        InviteOrganisationResource inviteOrganisationResource = newInviteOrganisationResource().withInviteResources(inviteResource).build();

        Application application = ApplicationBuilder.newApplication()
                .withApplicationState(ApplicationState.CREATED).build();
        when(applicationRepositoryMock.findOne(applicationResource.getId()))
                .thenReturn(application);

        assertTrue(rules.leadApplicantCanCreateApplicationInvitesIfApplicationEditable(inviteOrganisationResource, leadApplicant));
        assertTrue(rules.leadApplicantCanCreateApplicationInvitesIfApplicationEditable(inviteOrganisationResource, collaborator));
        assertFalse(rules.leadApplicantCanCreateApplicationInvitesIfApplicationEditable(inviteOrganisationResource, otherApplicant));
    }

    @Test
    public void leadApplicantCanCreateApplicationInvitesIfApplicationEditableWhenApplicationOpen() throws Exception {
        List<ApplicationInviteResource> inviteResource = newApplicationInviteResource().withApplication(applicationResource.getId()).build(5);
        InviteOrganisationResource inviteOrganisationResource = newInviteOrganisationResource().withInviteResources(inviteResource).build();

        Application application = ApplicationBuilder.newApplication()
                .withApplicationState(ApplicationState.OPEN).build();
        when(applicationRepositoryMock.findOne(applicationResource.getId()))
                .thenReturn(application);

        assertTrue(rules.leadApplicantCanCreateApplicationInvitesIfApplicationEditable(inviteOrganisationResource, leadApplicant));
        assertTrue(rules.leadApplicantCanCreateApplicationInvitesIfApplicationEditable(inviteOrganisationResource, collaborator));
        assertFalse(rules.leadApplicantCanCreateApplicationInvitesIfApplicationEditable(inviteOrganisationResource, otherApplicant));
    }

    @Test
    public void leadApplicantAndCollaboratorCannotCreateApplicationInvitesIfApplicationEditableWhenApplicationCreated() throws Exception {
        List<ApplicationInviteResource> inviteResource = ApplicationInviteResourceBuilder.newApplicationInviteResource().withApplication(applicationResource.getId()).build(5);
        InviteOrganisationResource inviteOrganisationResource = newInviteOrganisationResource().withInviteResources(inviteResource).build();

        when(processRoleRepositoryMock.existsByUserIdAndApplicationIdAndRole(leadApplicant.getId(), applicationResource.getId(), Role.LEADAPPLICANT))
                .thenReturn(false);
        when(processRoleRepositoryMock.existsByUserIdAndApplicationIdAndRole(collaborator.getId(), applicationResource.getId(), Role.COLLABORATOR))
                .thenReturn(false);

        Application application = ApplicationBuilder.newApplication()
                .withApplicationState(ApplicationState.CREATED).build();
        when(applicationRepositoryMock.findOne(applicationResource.getId()))
                .thenReturn(application);

        assertFalse(rules.leadApplicantCanCreateApplicationInvitesIfApplicationEditable(inviteOrganisationResource, leadApplicant));
        assertFalse(rules.leadApplicantCanCreateApplicationInvitesIfApplicationEditable(inviteOrganisationResource, collaborator));
        assertFalse(rules.leadApplicantCanCreateApplicationInvitesIfApplicationEditable(inviteOrganisationResource, otherApplicant));
    }

    @Test
    public void leadApplicantAndCollaboratorCannotCreateApplicationInvitesIfApplicationEditableWhenApplicationOpen() throws Exception {
        List<ApplicationInviteResource> inviteResource = ApplicationInviteResourceBuilder.newApplicationInviteResource().withApplication(applicationResource.getId()).build(5);
        InviteOrganisationResource inviteOrganisationResource = newInviteOrganisationResource().withInviteResources(inviteResource).build();

        when(processRoleRepositoryMock.existsByUserIdAndApplicationIdAndRole(leadApplicant.getId(), applicationResource.getId(), Role.LEADAPPLICANT))
                .thenReturn(false);
        when(processRoleRepositoryMock.existsByUserIdAndApplicationIdAndRole(collaborator.getId(), applicationResource.getId(), Role.COLLABORATOR))
                .thenReturn(false);

        Application application = ApplicationBuilder.newApplication()
                .withApplicationState(ApplicationState.OPEN).build();
        when(applicationRepositoryMock.findOne(applicationResource.getId()))
                .thenReturn(application);

        assertFalse(rules.leadApplicantCanCreateApplicationInvitesIfApplicationEditable(inviteOrganisationResource, leadApplicant));
        assertFalse(rules.leadApplicantCanCreateApplicationInvitesIfApplicationEditable(inviteOrganisationResource, collaborator));
        assertFalse(rules.leadApplicantCanCreateApplicationInvitesIfApplicationEditable(inviteOrganisationResource, otherApplicant));
    }

    @Test
    public void leadApplicantCanCreateApplicationInvitesIfApplicationEditableWhenApplicationSubmitted() throws Exception {
        List<ApplicationInviteResource> inviteResource = newApplicationInviteResource().withApplication(applicationResource.getId()).build(5);
        InviteOrganisationResource inviteOrganisationResource = newInviteOrganisationResource().withInviteResources(inviteResource).build();

        Application application = ApplicationBuilder.newApplication()
                .withApplicationState(ApplicationState.SUBMITTED).build();
        when(applicationRepositoryMock.findOne(applicationResource.getId()))
                .thenReturn(application);

        assertFalse(rules.leadApplicantCanCreateApplicationInvitesIfApplicationEditable(inviteOrganisationResource, leadApplicant));
        assertFalse(rules.leadApplicantCanCreateApplicationInvitesIfApplicationEditable(inviteOrganisationResource, collaborator));
        assertFalse(rules.leadApplicantCanCreateApplicationInvitesIfApplicationEditable(inviteOrganisationResource, otherApplicant));
    }
}
