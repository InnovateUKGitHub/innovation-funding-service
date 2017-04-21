package org.innovateuk.ifs.project.security;

import org.innovateuk.ifs.BasePermissionRulesTest;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Test;

import static org.innovateuk.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


public class ProjectGrantOfferPermissionRulesTest extends BasePermissionRulesTest<ProjectGrantOfferPermissionRules> {

    @Test
    public void testLeadPartnersCanCreateSignedGrantOfferLetter() {

        ProjectResource project = newProjectResource().build();
        UserResource user = newUserResource().build();

        setupUserAsLeadPartner(project, user);

        assertTrue(rules.leadPartnerCanUploadGrantOfferLetter(project, user));
    }

    @Test
    public void testNonLeadPartnersCannotCreateSignedGrantOfferLetter() {

        ProjectResource project = newProjectResource().build();
        UserResource user = newUserResource().build();

        setupUserNotAsLeadPartner(project, user);

        assertFalse(rules.leadPartnerCanUploadGrantOfferLetter(project, user));
    }

    @Test
    public void testProjectManagerCanCreateSignedGrantOfferLetter() {
        ProjectResource project = newProjectResource().build();
        UserResource user = newUserResource().build();

        setUpUserAsProjectManager(project, user);

        assertTrue(rules.projectManagerCanUploadGrantOfferLetter(project, user));

    }

    @Test
    public void testNonProjectManagerCannotCreateSignedGrantOfferLetter() {
        ProjectResource project = newProjectResource().build();
        UserResource user = newUserResource().build();

        setUpUserNotAsProjectManager(user);

        assertFalse(rules.projectManagerCanUploadGrantOfferLetter(project, user));

    }


    @Test
    public void testPartnersCanViewGrantOfferLetterDetails() {

        ProjectResource project = newProjectResource().build();
        UserResource user = newUserResource().build();

        setupUserAsPartner(project, user);

        assertTrue(rules.partnersCanViewGrantOfferLetter(project, user));
    }

    @Test
    public void testNonPartnersCannotViewGrantOfferLetterDetails() {

        ProjectResource project = newProjectResource().build();
        UserResource user = newUserResource().build();

        setupUserNotAsPartner(project, user);

        assertFalse(rules.partnersCanViewGrantOfferLetter(project, user));
    }

    @Test
    public void testPartnersCanDownloadGrantOfferLetterDocuments() {

        ProjectResource project = newProjectResource().build();
        UserResource user = newUserResource().build();

        setupUserAsPartner(project, user);

        assertTrue(rules.partnersCanDownloadGrantOfferLetter(project, user));
    }

    @Test
    public void testNonPartnersCannotDownloadGrantOfferLetterDocuments() {

        ProjectResource project = newProjectResource().build();
        UserResource user = newUserResource().build();

        setupUserNotAsPartner(project, user);

        assertFalse(rules.partnersCanDownloadGrantOfferLetter(project, user));
    }

    @Test
    public void testCompAdminsCanDownloadGrantOfferLetterDocuments() {

        ProjectResource project = newProjectResource().build();
        UserResource user = newUserResource().build();

        setUpUserAsCompAdmin(project, user);

        assertTrue(rules.internalUsersCanDownloadGrantOfferLetter(project, user));
    }

    @Test
    public void testNonCompAdminsCannotDownloadGrantOfferLetterDocuments() {

        ProjectResource project = newProjectResource().build();
        UserResource user = newUserResource().build();

        setUpUserNotAsCompAdmin(project, user);

        assertFalse(rules.internalUsersCanDownloadGrantOfferLetter(project, user));
    }

    @Test
    public void testCompAdminsCanViewGrantOfferLetterDocuments() {

        ProjectResource project = newProjectResource().build();
        UserResource user = newUserResource().build();

        setUpUserAsCompAdmin(project, user);

        assertTrue(rules.internalUsersCanViewGrantOfferLetter(project, user));
    }

    @Test
    public void testNonCompAdminsCannotViewGrantOfferLetterDocuments() {

        ProjectResource project = newProjectResource().build();
        UserResource user = newUserResource().build();

        setUpUserNotAsCompAdmin(project, user);

        assertFalse(rules.internalUsersCanViewGrantOfferLetter(project, user));
    }

    @Test
    public void testProjectFinanceCanDownloadGrantOfferLetterDocuments() {

        ProjectResource project = newProjectResource().build();
        UserResource user = newUserResource().build();

        setUpUserAsProjectFinanceUser(project, user);

        assertTrue(rules.internalUsersCanDownloadGrantOfferLetter(project, user));
    }

    @Test
    public void testNonProjectFinanceCannotDownloadGrantOfferLetterDocuments() {

        ProjectResource project = newProjectResource().build();
        UserResource user = newUserResource().build();

        setUpUserNotAsProjectFinanceUser(project, user);

        assertFalse(rules.internalUsersCanDownloadGrantOfferLetter(project, user));
    }

    @Test
    public void testProjectFinanceCanViewGrantOfferLetterDocuments() {

        ProjectResource project = newProjectResource().build();
        UserResource user = newUserResource().build();

        setUpUserAsProjectFinanceUser(project, user);

        assertTrue(rules.internalUsersCanViewGrantOfferLetter(project, user));
    }

    @Test
    public void testNonProjectFinanceCannotViewGrantOfferLetterDocuments() {

        ProjectResource project = newProjectResource().build();
        UserResource user = newUserResource().build();

        setUpUserNotAsProjectFinanceUser(project, user);

        assertFalse(rules.internalUsersCanViewGrantOfferLetter(project, user));
    }

    @Test
    public void testProjectManagerCanSubmitOfferLetter() {
        ProjectResource project = newProjectResource().build();
        UserResource user = newUserResource().build();

        setUpUserAsProjectManager(project, user);

        assertTrue(rules.projectManagerSubmitGrantOfferLetter(project.getId(), user));

    }

    @Test
    public void testNonProjectManagerCannotSubmitOfferLetter() {
        ProjectResource project = newProjectResource().build();
        UserResource user = newUserResource().build();

        setUpUserNotAsProjectManager(user);

        assertFalse(rules.projectManagerSubmitGrantOfferLetter(project.getId(), user));
    }

    @Test
    public void testCompAdminsCanSendGrantOfferLetterDocuments() {

        ProjectResource project = newProjectResource().build();
        UserResource user = newUserResource().build();

        setUpUserAsCompAdmin(project, user);

        assertTrue(rules.contractsTeamSendGrantOfferLetter(project.getId(), user));
    }

    @Test
    public void testNonCompAdminsCannotSendGrantOfferLetterDocuments() {

        ProjectResource project = newProjectResource().build();
        UserResource user = newUserResource().build();

        setUpUserNotAsCompAdmin(project, user);

        assertFalse(rules.contractsTeamSendGrantOfferLetter(project.getId(), user));
    }

    @Test
    public void testCompAdminsCanApproveSignedGrantOfferLetters() {
        ProjectResource project = newProjectResource().build();
        UserResource user = newUserResource().build();

        setUpUserAsCompAdmin(project, user);

        assertTrue(rules.internalUsersCanApproveSignedGrantOfferLetter(project.getId(), user));
    }

    @Test
    public void testNonCompAdminsCannotApproveSignedGrantOfferLetters() {
        ProjectResource project = newProjectResource().build();
        UserResource user = newUserResource().build();

        setUpUserNotAsCompAdmin(project, user);

        assertFalse(rules.internalUsersCanApproveSignedGrantOfferLetter(project.getId(), user));
    }

    @Test
    public void testLeadPartnerCanDeleteSignedGrantOfferLetter() {
        ProjectResource project = newProjectResource().build();
        UserResource user = newUserResource().build();

        setupUserAsLeadPartner(project, user);

        assertTrue(rules.leadPartnerCanDeleteSignedGrantOfferLetter(project, user));

    }

    @Test
    public void testNonLeadPartnerCanDeleteSignedGrantOfferLetter() {
        ProjectResource project = newProjectResource().build();
        UserResource user = newUserResource().build();

        setupUserNotAsLeadPartner(project, user);

        assertFalse(rules.leadPartnerCanDeleteSignedGrantOfferLetter(project, user));

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

    @Override
    protected ProjectGrantOfferPermissionRules supplyPermissionRulesUnderTest() {
        return new ProjectGrantOfferPermissionRules();
    }
}
