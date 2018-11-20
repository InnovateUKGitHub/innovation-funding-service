package org.innovateuk.ifs.project.documents.security;

import org.innovateuk.ifs.BasePermissionRulesTest;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.util.SecurityRuleUtil;
import org.junit.Test;

import static org.innovateuk.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.util.SecurityRuleUtil.isInternal;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class DocumentPermissionRulesTest extends BasePermissionRulesTest<DocumentPermissionRules> {

    @Override
    protected DocumentPermissionRules supplyPermissionRulesUnderTest() {
        return new DocumentPermissionRules();
    }

    @Test
    public void projectManagerCanUploadDocument() {
        ProjectResource project = newProjectResource().build();

        UserResource user = newUserResource().build();

        setUpUserAsProjectManager(project, user);
        assertTrue(rules.projectManagerCanUploadDocument(project, user));
    }

    @Test
    public void nonProjectManagerCannotUploadDocument() {
        ProjectResource project = newProjectResource().build();

        UserResource user = newUserResource().build();

        setUpUserNotAsProjectManager(user);
        assertFalse(rules.projectManagerCanUploadDocument(project, user));
    }

    @Test
    public void partnersCanDownloadDocument() {

        ProjectResource project = newProjectResource().build();
        UserResource user = newUserResource().build();

        setupUserAsPartner(project, user);

        assertTrue(rules.partnersCanDownloadDocument(project, user));
    }

    @Test
    public void nonPartnersCannotDownloadDocument() {

        ProjectResource project = newProjectResource().build();
        UserResource user = newUserResource().build();

        setupUserNotAsPartner(project, user);

        assertFalse(rules.partnersCanDownloadDocument(project, user));
    }

    @Test
    public void internalUserCanDownloadDocument() {
        ProjectResource project = newProjectResource().build();

        allGlobalRoleUsers.forEach(user -> {
            if (isInternal(user)) {
                assertTrue(rules.internalUserCanDownloadDocument(project, user));
            } else {
                assertFalse(rules.internalUserCanDownloadDocument(project, user));
            }
        });
    }

    @Test
    public void projectManagerCanDeleteDocument() {
        ProjectResource project = newProjectResource().build();

        UserResource user = newUserResource().build();

        setUpUserAsProjectManager(project, user);
        assertTrue(rules.projectManagerCanDeleteDocument(project, user));
    }

    @Test
    public void nonProjectManagerCannotDeleteDocument() {
        ProjectResource project = newProjectResource().build();

        UserResource user = newUserResource().build();

        setUpUserNotAsProjectManager(user);
        assertFalse(rules.projectManagerCanDeleteDocument(project, user));
    }

    @Test
    public void projectManagerCanSubmitDocument() {
        ProjectResource project = newProjectResource().build();

        UserResource user = newUserResource().build();

        setUpUserAsProjectManager(project, user);
        assertTrue(rules.projectManagerCanSubmitDocument(project, user));
    }

    @Test
    public void nonProjectManagerCannotSubmitDocument() {
        ProjectResource project = newProjectResource().build();

        UserResource user = newUserResource().build();

        setUpUserNotAsProjectManager(user);
        assertFalse(rules.projectManagerCanSubmitDocument(project, user));
    }

    @Test
    public void internalAdminCanApproveDocument() {
        ProjectResource project = newProjectResource().build();

        allGlobalRoleUsers.forEach(user -> {
            if (SecurityRuleUtil.isInternalAdmin(user)) {
                assertTrue(rules.internalAdminCanApproveDocument(project, user));
            } else {
                assertFalse(rules.internalAdminCanApproveDocument(project, user));
            }
        });
    }
}

