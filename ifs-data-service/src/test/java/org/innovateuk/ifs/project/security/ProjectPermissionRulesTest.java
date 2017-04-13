package org.innovateuk.ifs.project.security;

import org.innovateuk.ifs.BasePermissionRulesTest;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.project.domain.Project;
import org.innovateuk.ifs.project.domain.ProjectUser;
import org.innovateuk.ifs.project.resource.ProjectOrganisationCompositeId;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.user.domain.Organisation;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.user.domain.Role;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Test;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.invite.domain.ProjectParticipantRole.PROJECT_PARTNER;
import static org.innovateuk.ifs.project.builder.ProjectBuilder.newProject;
import static org.innovateuk.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static org.innovateuk.ifs.user.builder.OrganisationBuilder.newOrganisation;
import static org.innovateuk.ifs.user.builder.ProcessRoleBuilder.newProcessRole;
import static org.innovateuk.ifs.user.builder.RoleBuilder.newRole;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.user.resource.UserRoleType.LEADAPPLICANT;
import static org.innovateuk.ifs.user.resource.UserRoleType.PARTNER;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class ProjectPermissionRulesTest extends BasePermissionRulesTest<ProjectPermissionRules> {

    @Override
    protected ProjectPermissionRules supplyPermissionRulesUnderTest() {
        return new ProjectPermissionRules();
    }

    @Test
    public void testPartnersOnProjectCanView() {

        UserResource user = newUserResource().build();

        ProjectResource project = newProjectResource().build();
        setupUserAsPartner(project, user);

        assertTrue(rules.partnersOnProjectCanView(project, user));
    }

    @Test
    public void testPartnersOnProjectCanViewButUserNotPartner() {

        UserResource user = newUserResource().build();

        ProjectResource project = newProjectResource().build();
        Role partnerRole = newRole().build();

        when(roleRepositoryMock.findOneByName(PARTNER.getName())).thenReturn(partnerRole);
        when(projectUserRepositoryMock.findByProjectIdAndUserIdAndRole(project.getId(), user.getId(), PROJECT_PARTNER)).thenReturn(emptyList());

        assertFalse(rules.partnersOnProjectCanView(project, user));
    }

    @Test
    public void testInternalUsersCanViewProjects() {

        ProjectResource project = newProjectResource().build();

        allGlobalRoleUsers.forEach(user -> {
            if (allInternalUsers.contains(user)) {
                assertTrue(rules.internalUsersCanViewProjects(project, user));
            } else {
                assertFalse(rules.internalUsersCanViewProjects(project, user));
            }
        });
    }

    @Test
    public void testInternalUsersCanAcceptOrRejectDocuments() {

        ProjectResource project = newProjectResource().build();

        allGlobalRoleUsers.forEach(user -> {
            if (allInternalUsers.contains(user)) {
                assertTrue(rules.internalUserCanAcceptOrRejectOtherDocuments(project, user));
            } else {
                assertFalse(rules.internalUserCanAcceptOrRejectOtherDocuments(project, user));
            }
        });
    }

    @Test
    public void testLeadPartnersCanUpdateTheBasicProjectDetails() {

        ProjectResource project = newProjectResource().build();
        UserResource user = newUserResource().build();

        setupUserAsLeadPartner(project, user);

        assertTrue(rules.leadPartnersCanUpdateTheBasicProjectDetails(project, user));
    }

    @Test
    public void testLeadPartnersCanUpdateTheBasicProjectDetailsButUserNotLeadPartner() {

        Application originalApplication = newApplication().build();
        ProjectResource project = newProjectResource().build();
        Project projectEntity = newProject().withApplication(originalApplication).build();
        UserResource user = newUserResource().build();
        Role leadApplicantRole = newRole().build();
        Organisation leadOrganisation = newOrganisation().build();
        ProcessRole leadApplicantProcessRole = newProcessRole().withOrganisationId(leadOrganisation.getId()).build();

        // find the lead organisation
        when(projectRepositoryMock.findOne(project.getId())).thenReturn(projectEntity);
        when(roleRepositoryMock.findOneByName(LEADAPPLICANT.getName())).thenReturn(leadApplicantRole);
        when(processRoleRepositoryMock.findOneByApplicationIdAndRoleId(projectEntity.getApplication().getId(), leadApplicantRole.getId())).thenReturn(leadApplicantProcessRole);

        // see if the user is a partner on the lead organisation
        when(organisationRepositoryMock.findOne(leadOrganisation.getId())).thenReturn(leadOrganisation);
        when(projectUserRepositoryMock.findOneByProjectIdAndUserIdAndOrganisationIdAndRole(
                project.getId(), user.getId(), leadOrganisation.getId(), PROJECT_PARTNER)).thenReturn(null);

        assertFalse(rules.leadPartnersCanUpdateTheBasicProjectDetails(project, user));
    }

    @Test
    public void testPartnersCanUpdateTheirOwnOrganisationsFinanceContacts() {

        ProjectResource project = newProjectResource().build();
        Organisation organisation = newOrganisation().build();
        ProjectOrganisationCompositeId composite = new ProjectOrganisationCompositeId(project.getId(), organisation.getId());
        UserResource user = newUserResource().build();
        setupUserAsPartner(project, user);
        when(projectUserRepositoryMock.findOneByProjectIdAndUserIdAndOrganisationIdAndRole(project.getId(), user.getId(), organisation.getId(), PROJECT_PARTNER)).thenReturn(new ProjectUser());

        assertTrue(rules.partnersCanUpdateTheirOwnOrganisationsFinanceContacts(composite, user));
    }

    @Test
    public void testPartnersCanUpdateTheirOwnOrganisationsFinanceContactsButUserNotPartner() {

        ProjectResource project = newProjectResource().build();
        Organisation organisation = newOrganisation().build();
        ProjectOrganisationCompositeId composite = new ProjectOrganisationCompositeId(project.getId(), organisation.getId());
        UserResource user = newUserResource().build();
        Role partnerRole = newRole().build();

        when(roleRepositoryMock.findOneByName(PARTNER.getName())).thenReturn(partnerRole);
        when(projectUserRepositoryMock.findByProjectIdAndUserIdAndRole(project.getId(), user.getId(), PROJECT_PARTNER)).thenReturn(emptyList());

        assertFalse(rules.partnersCanUpdateTheirOwnOrganisationsFinanceContacts(composite, user));
    }

    @Test
    public void testPartnersCanUpdateTheirOwnOrganisationsFinanceContactsButUserNotMemberOfOrganisation() {

        ProjectResource project = newProjectResource().build();
        Organisation organisation = newOrganisation().build();
        ProjectOrganisationCompositeId composite = new ProjectOrganisationCompositeId(project.getId(), organisation.getId());
        UserResource user = newUserResource().build();
        setupUserAsPartner(project, user);

        when(projectUserRepositoryMock.findOneByProjectIdAndUserIdAndOrganisationIdAndRole(project.getId(), user.getId(), organisation.getId(), PROJECT_PARTNER)).thenReturn(null);

        assertFalse(rules.partnersCanUpdateTheirOwnOrganisationsFinanceContacts(composite, user));
    }

    @Test
    public void testSubmitIsAllowed() {
        ProjectResource project = newProjectResource().build();
        UserResource user = newUserResource().build();
        setupUserAsPartner(project, user);

        assertTrue(rules.submitIsAllowed(project.getId(), user));
    }

    @Test
    public void testInternalUsersCanViewMonitoringOfficersOnProjects() {

        ProjectResource project = newProjectResource().build();

        allGlobalRoleUsers.forEach(user -> {
            if (allInternalUsers.contains(user)) {
                assertTrue(rules.internalUsersCanViewMonitoringOfficersForAnyProject(project, user));
            } else {
                assertFalse(rules.internalUsersCanViewMonitoringOfficersForAnyProject(project, user));
            }
        });
    }

    @Test
    public void testPartnersCanViewMonitoringOfficersOnTheirOwnProjects() {

        UserResource user = newUserResource().build();
        ProjectResource project = newProjectResource().build();

        setupUserAsPartner(project, user);

        assertTrue(rules.partnersCanViewMonitoringOfficersOnTheirProjects(project, user));
    }

    @Test
    public void testPartnersCanViewMonitoringOfficersOnTheirOwnProjectsButUserNotPartner() {

        UserResource user = newUserResource().build();

        ProjectResource project = newProjectResource().build();
        Role partnerRole = newRole().build();

        when(roleRepositoryMock.findOneByName(PARTNER.getName())).thenReturn(partnerRole);
        when(projectUserRepositoryMock.findByProjectIdAndUserIdAndRole(project.getId(), user.getId(), PROJECT_PARTNER)).thenReturn(emptyList());

        assertFalse(rules.partnersCanViewMonitoringOfficersOnTheirProjects(project, user));
    }

    @Test
    public void testInternalUsersCanEditMonitoringOfficersOnProjects() {

        ProjectResource project = newProjectResource().build();

        allGlobalRoleUsers.forEach(user -> {
            if (allInternalUsers.contains(user)) {
                assertTrue(rules.internalUsersCanAssignMonitoringOfficersForAnyProject(project, user));
            } else {
                assertFalse(rules.internalUsersCanAssignMonitoringOfficersForAnyProject(project, user));
            }
        });
    }

    @Test
    public void testLeadPartnersCanCreateOtherDocuments() {

        ProjectResource project = newProjectResource().build();
        UserResource user = newUserResource().build();

        setupUserAsLeadPartner(project, user);

        assertTrue(rules.leadPartnersCanUploadOtherDocuments(project, user));
    }

    @Test
    public void testNonLeadPartnersCannotCreateOtherDocuments() {

        ProjectResource project = newProjectResource().build();
        UserResource user = newUserResource().build();

        setupUserNotAsLeadPartner(project, user);

        assertFalse(rules.leadPartnersCanUploadOtherDocuments(project, user));
    }

    @Test
    public void testPartnersCanViewOtherDocumentsDetails() {

        ProjectResource project = newProjectResource().build();
        UserResource user = newUserResource().build();

        setupUserAsPartner(project, user);

        assertTrue(rules.partnersCanViewOtherDocumentsDetails(project, user));
    }

    @Test
    public void testNonPartnersCannotViewOtherDocumentsDetails() {

        ProjectResource project = newProjectResource().build();
        UserResource user = newUserResource().build();

        setupUserNotAsPartner(project, user);

        assertFalse(rules.partnersCanViewOtherDocumentsDetails(project, user));
    }

    @Test
    public void testInternalUserCanViewOtherDocumentsDetails() {
        ProjectResource project = newProjectResource().build();

        allGlobalRoleUsers.forEach(user -> {
            if (allInternalUsers.contains(user)) {
                assertTrue(rules.internalUserCanViewOtherDocumentsDetails(project, user));
            } else {
                assertFalse(rules.internalUserCanViewOtherDocumentsDetails(project, user));
            }
        });
    }

    @Test
    public void testPartnersCanDownloadOtherDocuments() {

        ProjectResource project = newProjectResource().build();
        UserResource user = newUserResource().build();

        setupUserAsPartner(project, user);

        assertTrue(rules.partnersCanDownloadOtherDocuments(project, user));
    }

    @Test
    public void testNonPartnersCannotDownloadOtherDocuments() {

        ProjectResource project = newProjectResource().build();
        UserResource user = newUserResource().build();

        setupUserNotAsPartner(project, user);

        assertFalse(rules.partnersCanDownloadOtherDocuments(project, user));
    }

    @Test
    public void testInternalUserCanDownloadOtherDocuments() {
        ProjectResource project = newProjectResource().build();

        allGlobalRoleUsers.forEach(user -> {
            if (allInternalUsers.contains(user)) {
                assertTrue(rules.internalUserCanDownloadOtherDocuments(project, user));
            } else {
                assertFalse(rules.internalUserCanDownloadOtherDocuments(project, user));
            }
        });
    }

    @Test
    public void testLeadPartnersCanDeleteOtherDocuments() {

        ProjectResource project = newProjectResource().build();
        UserResource user = newUserResource().build();

        setupUserAsLeadPartner(project, user);

        assertTrue(rules.leadPartnersCanDeleteOtherDocuments(project, user));
    }

    @Test
    public void testNonLeadPartnersCannotDeleteOtherDocuments() {

        ProjectResource project = newProjectResource().build();
        UserResource user = newUserResource().build();

        setupUserNotAsLeadPartner(project, user);

        assertFalse(rules.leadPartnersCanDeleteOtherDocuments(project, user));
    }

    @Test
    public void testOnlyProjectManagerCanSubmitDocuments() {
        ProjectResource project = newProjectResource().build();
        UserResource user = newUserResource().build();

        setUpUserAsProjectManager(project, user);

        assertTrue(rules.onlyProjectManagerCanMarkDocumentsAsSubmit(project, user));

    }

    @Test
    public void testPartnersCanViewTeamStatus() {
        ProjectResource project = newProjectResource().build();
        UserResource user = newUserResource().build();
        setupUserAsPartner(project, user);
        assertTrue(rules.partnersCanViewTeamStatus(project, user));
    }

    @Test
    public void testNonPartnersCannotViewTeamStatus() {
        ProjectResource project = newProjectResource().build();
        UserResource user = newUserResource().build();
        setupUserNotAsPartner(project, user);
        assertFalse(rules.partnersCanViewTeamStatus(project, user));
    }

    @Test
    public void testInternalUsersCanViewTeamStatus() {
        ProjectResource project = newProjectResource().build();
        assertTrue(rules.internalUsersCanViewTeamStatus(project, compAdminUser()));
        assertTrue(rules.internalUsersCanViewTeamStatus(project, projectFinanceUser()));
    }

    @Test
    public void testProjectFinanceUserCanSendGrantOfferLetter() {
        ProjectResource project = newProjectResource().build();
        assertTrue(rules.internalUserCanSendGrantOfferLetter(project, projectFinanceUser()));
    }

    @Test
    public void testCompAdminsUserCanSendGrantOfferLetter() {
        ProjectResource project = newProjectResource().build();
        assertTrue(rules.internalUserCanSendGrantOfferLetter(project, compAdminUser()));
    }

    @Test
    public void testPartnerUserCannotSendGrantOfferLetter() {
        ProjectResource project = newProjectResource().build();
        UserResource user = newUserResource().build();
        setupUserAsPartner(project, user);
        assertFalse(rules.internalUserCanSendGrantOfferLetter(project, user));
    }

    @Test
    public void testInternalUserCanViewSendGrantOfferLetterStatus() {

        ProjectResource project = newProjectResource().build();

        allGlobalRoleUsers.forEach(user -> {
            if (user.equals(projectFinanceUser()) || user.equals(compAdminUser())) {
                assertTrue(rules.internalUserCanViewSendGrantOfferLetterStatus(project, user));
            } else {
                assertFalse(rules.internalUserCanViewSendGrantOfferLetterStatus(project, user));
            }
        });
    }

    @Test
    public void testPartnersCanViewSendGrantOfferLetterStatus() {

        ProjectResource project = newProjectResource().build();

        // Ensure partners can access
        UserResource user = newUserResource().build();
        setupUserAsPartner(project, user);
        assertTrue(rules.externalUserCanViewSendGrantOfferLetterStatus(project, user));
    }

    @Test
    public void testNonPartnersCannotViewSendGrantOfferLetterStatus() {

        ProjectResource project = newProjectResource().build();

        // Ensure non-partners cannot access
        allGlobalRoleUsers.forEach(user2 -> assertFalse(rules.externalUserCanViewSendGrantOfferLetterStatus(project, user2)));
    }
}
