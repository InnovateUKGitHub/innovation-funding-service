package org.innovateuk.ifs.project.otherdocuments.security;

import org.innovateuk.ifs.BasePermissionRulesTest;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Test;

import static org.innovateuk.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class OtherDocumentsPermissionRulesTest extends BasePermissionRulesTest<OtherDocumentsPermissionRules> {

    @Override
    protected OtherDocumentsPermissionRules supplyPermissionRulesUnderTest() {
        return new OtherDocumentsPermissionRules();
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
}
