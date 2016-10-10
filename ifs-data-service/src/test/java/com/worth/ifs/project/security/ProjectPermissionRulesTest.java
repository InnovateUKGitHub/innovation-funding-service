package com.worth.ifs.project.security;

import com.worth.ifs.BasePermissionRulesTest;
import com.worth.ifs.application.domain.Application;
import com.worth.ifs.project.domain.Project;
import com.worth.ifs.project.resource.ProjectResource;
import com.worth.ifs.user.domain.Organisation;
import com.worth.ifs.user.domain.ProcessRole;
import com.worth.ifs.user.domain.Role;
import com.worth.ifs.user.resource.UserResource;
import org.junit.Test;

import static com.worth.ifs.application.builder.ApplicationBuilder.newApplication;
import static com.worth.ifs.invite.domain.ProjectParticipantRole.PROJECT_PARTNER;
import static com.worth.ifs.project.builder.ProjectBuilder.newProject;
import static com.worth.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static com.worth.ifs.user.builder.OrganisationBuilder.newOrganisation;
import static com.worth.ifs.user.builder.ProcessRoleBuilder.newProcessRole;
import static com.worth.ifs.user.builder.RoleBuilder.newRole;
import static com.worth.ifs.user.builder.UserResourceBuilder.newUserResource;
import static com.worth.ifs.user.resource.UserRoleType.LEADAPPLICANT;
import static com.worth.ifs.user.resource.UserRoleType.PARTNER;
import static java.util.Collections.emptyList;
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
    public void testCompAdminsCanViewProjects() {

        ProjectResource project = newProjectResource().build();

        allGlobalRoleUsers.forEach(user -> {
            if (user.equals(compAdminUser())) {
                assertTrue(rules.compAdminsCanViewProjects(project, user));
            } else {
                assertFalse(rules.compAdminsCanViewProjects(project, user));
            }
        });
    }

    @Test
    public void testCompAdminsCanAcceptOrRejectDocuments() {

        ProjectResource project = newProjectResource().build();

        allGlobalRoleUsers.forEach(user -> {
            if (user.equals(compAdminUser())) {
                assertTrue(rules.competitionAdminCanAcceptOrRejectOtherDocuments(project, user));
            } else {
                assertFalse(rules.competitionAdminCanAcceptOrRejectOtherDocuments(project, user));
            }
        });
    }

    @Test
    public void testProjectFinanceUserCanAcceptOrRejectOtherDocuments() {

        ProjectResource project = newProjectResource().build();

        allGlobalRoleUsers.forEach(user -> {
            if (user.equals(projectFinanceUser())) {
                assertTrue(rules.projectFinanceUserCanAcceptOrRejectOtherDocuments(project, user));
            } else {
                assertFalse(rules.projectFinanceUserCanAcceptOrRejectOtherDocuments(project, user));
            }
        });
    }

    @Test
    public void testProjectFinanceUsersCanViewProjects() {

        ProjectResource project = newProjectResource().build();

        allGlobalRoleUsers.forEach(user -> {
            if (user.equals(projectFinanceUser())) {
                assertTrue(rules.projectFinanceUsersCanViewProjects(project, user));
            } else {
                assertFalse(rules.projectFinanceUsersCanViewProjects(project, user));
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
        Role partnerRole = newRole().build();
        Organisation leadOrganisation = newOrganisation().build();
        ProcessRole leadApplicantProcessRole = newProcessRole().withOrganisation(leadOrganisation).build();

        // find the lead organisation
        when(projectRepositoryMock.findOne(project.getId())).thenReturn(projectEntity);
        when(roleRepositoryMock.findOneByName(LEADAPPLICANT.getName())).thenReturn(leadApplicantRole);
        when(processRoleRepositoryMock.findOneByApplicationIdAndRoleId(projectEntity.getApplication().getId(), leadApplicantRole.getId())).thenReturn(leadApplicantProcessRole);

        // see if the user is a partner on the lead organisation
        when(roleRepositoryMock.findOneByName(PARTNER.getName())).thenReturn(partnerRole);
        when(projectUserRepositoryMock.findOneByProjectIdAndUserIdAndOrganisationIdAndRole(
                project.getId(), user.getId(), leadOrganisation.getId(), PROJECT_PARTNER)).thenReturn(null);

        assertFalse(rules.leadPartnersCanUpdateTheBasicProjectDetails(project, user));
    }

    @Test
    public void testPartnersCanUpdateTheirOwnOrganisationsFinanceContacts() {

        ProjectResource project = newProjectResource().build();
        UserResource user = newUserResource().build();
        setupUserAsPartner(project, user);

        assertTrue(rules.partnersCanUpdateTheirOwnOrganisationsFinanceContacts(project, user));
    }

    @Test
    public void testPartnersCanUpdateTheirOwnOrganisationsFinanceContactsButUserNotPartner() {

        ProjectResource project = newProjectResource().build();
        UserResource user = newUserResource().build();
        Role partnerRole = newRole().build();

        when(roleRepositoryMock.findOneByName(PARTNER.getName())).thenReturn(partnerRole);
        when(projectUserRepositoryMock.findByProjectIdAndUserIdAndRole(project.getId(), user.getId(), PROJECT_PARTNER)).thenReturn(emptyList());

        assertFalse(rules.partnersCanUpdateTheirOwnOrganisationsFinanceContacts(project, user));
    }

    @Test
    public void testCompAdminsCanViewMonitoringOfficersOnProjects() {

        ProjectResource project = newProjectResource().build();

        allGlobalRoleUsers.forEach(user -> {
            if (user.equals(compAdminUser())) {
                assertTrue(rules.compAdminsCanViewMonitoringOfficersForAnyProject(project, user));
            } else {
                assertFalse(rules.compAdminsCanViewMonitoringOfficersForAnyProject(project, user));
            }
        });
    }

    @Test
    public void testProjectFinanceUsersCanViewMonitoringOfficersOnProjects() {

        ProjectResource project = newProjectResource().build();

        allGlobalRoleUsers.forEach(user -> {
            if (user.equals(projectFinanceUser())) {
                assertTrue(rules.projectFinanceUsersCanViewMonitoringOfficersForAnyProject(project, user));
            } else {
                assertFalse(rules.projectFinanceUsersCanViewMonitoringOfficersForAnyProject(project, user));
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
    public void testCompAdminsCanEditMonitoringOfficersOnProjects() {

        ProjectResource project = newProjectResource().build();

        allGlobalRoleUsers.forEach(user -> {
            if (user.equals(compAdminUser())) {
                assertTrue(rules.compAdminsCanAssignMonitoringOfficersForAnyProject(project, user));
            } else {
                assertFalse(rules.compAdminsCanAssignMonitoringOfficersForAnyProject(project, user));
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
    public void testCompAdminCanViewOtherDocumentsDetails() {

        ProjectResource project = newProjectResource().build();
        UserResource user = newUserResource().build();

        setUpUserAsCompAdmin(project, user);

        assertTrue(rules.competitionAdminCanViewOtherDocumentsDetails(project, user));
    }

    @Test
    public void testNonCompAdminCannotViewOtherDocumentsDetails() {

        ProjectResource project = newProjectResource().build();
        UserResource user = newUserResource().build();

        setUpUserNotAsCompAdmin(project, user);

        assertFalse(rules.competitionAdminCanViewOtherDocumentsDetails(project, user));
    }

    @Test
    public void testProjectFinanceUserCanViewOtherDocumentsDetails() {

        ProjectResource project = newProjectResource().build();
        UserResource user = newUserResource().build();

        setUpUserAsProjectFinanceUser(project, user);

        assertFalse(rules.competitionAdminCanViewOtherDocumentsDetails(project, user));
    }

    @Test
    public void testNonProjectFinanceUserCannotViewOtherDocumentsDetails() {

        ProjectResource project = newProjectResource().build();
        UserResource user = newUserResource().build();

        setUpUserNotAsProjectFinanceUser(project, user);

        assertFalse(rules.competitionAdminCanViewOtherDocumentsDetails(project, user));
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
    public void testCompAdminCanDownloadOtherDocuments() {

        ProjectResource project = newProjectResource().build();
        UserResource user = newUserResource().build();

        setUpUserAsCompAdmin(project, user);

        assertTrue(rules.competitionAdminCanDownloadOtherDocuments(project, user));
    }

    @Test
    public void testNonCompAdminCannotDownloadOtherDocuments() {

        ProjectResource project = newProjectResource().build();
        UserResource user = newUserResource().build();

        setUpUserNotAsCompAdmin(project, user);

        assertFalse(rules.competitionAdminCanDownloadOtherDocuments(project, user));
    }

    @Test
    public void testProjectFinanceUserCanDownloadOtherDocuments() {

        ProjectResource project = newProjectResource().build();
        UserResource user = newUserResource().build();

        setUpUserAsProjectFinanceUser(project, user);

        assertTrue(rules.projectFinanceUserCanDownloadOtherDocuments(project, user));
    }

    @Test
    public void testNonProjectFinanceUserCanDownloadOtherDocuments() {

        ProjectResource project = newProjectResource().build();
        UserResource user = newUserResource().build();

        setUpUserNotAsProjectFinanceUser(project, user);

        assertFalse(rules.projectFinanceUserCanDownloadOtherDocuments(project, user));
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
    public void testPartnersCanViewTeamStatus(){
        ProjectResource project = newProjectResource().build();
        UserResource user = newUserResource().build();
        setupUserAsPartner(project, user);
        assertTrue(rules.partnersCanViewTeamStatus(project, user));
    }

    @Test
    public void testNonPartnersCannotViewTeamStatus(){
        ProjectResource project = newProjectResource().build();
        UserResource user = newUserResource().build();
        setupUserNotAsPartner(project, user);
        assertFalse(rules.partnersCanViewTeamStatus(project, user));
    }
    @Test
    public void testCompAdminsCanViewTeamStatus(){
        ProjectResource project = newProjectResource().build();
        assertTrue(rules.compAdminsCanViewTeamStatus(project, compAdminUser()));
    }

    @Test
    public void testProjectFinanceUserCanViewTeamStatus(){
        ProjectResource project = newProjectResource().build();
        assertTrue(rules.projectFinanceUserCanViewTeamStatus(project, projectFinanceUser()));
    }
}
