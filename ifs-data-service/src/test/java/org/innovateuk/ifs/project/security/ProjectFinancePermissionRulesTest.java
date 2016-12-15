package org.innovateuk.ifs.project.security;

import org.innovateuk.ifs.BasePermissionRulesTest;
import org.innovateuk.ifs.project.resource.ProjectOrganisationCompositeId;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Test;

import static junit.framework.TestCase.assertFalse;
import static org.innovateuk.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static org.innovateuk.ifs.user.builder.OrganisationBuilder.newOrganisation;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.junit.Assert.assertTrue;

/**
 * Module: innovation-funding-service
 **/
public class ProjectFinancePermissionRulesTest extends BasePermissionRulesTest<ProjectFinancePermissionRules> {

    @Test
    public void testProjectManagerCanViewAnySpendProfileData() throws Exception {

        ProjectResource project = newProjectResource().build();
        UserResource user = newUserResource().build();
        ProjectOrganisationCompositeId projectOrganisationCompositeId =
                new ProjectOrganisationCompositeId(project.getId(), newOrganisation().build().getId());

        setUpUserAsProjectManager(project, user);

        assertTrue(rules.projectManagerCanViewAnySpendProfileData(projectOrganisationCompositeId, user));
    }
    @Test
    public void testUserNotProjectManagerCannotViewSpendProfile() throws Exception {
        ProjectResource project = newProjectResource().build();
        UserResource user = newUserResource().build();
        ProjectOrganisationCompositeId projectOrganisationCompositeId =
                new ProjectOrganisationCompositeId(project.getId(), newOrganisation().build().getId());

        setUpUserNotAsProjectManager(user);

        assertFalse(rules.projectManagerCanViewAnySpendProfileData(projectOrganisationCompositeId, user));
    }

    @Test
    public void testProjectManagerCanCompleteSpendProfile() throws Exception {
        ProjectResource project = newProjectResource().build();
        UserResource user = newUserResource().build();

        setUpUserAsProjectManager(project, user);

        assertTrue(rules.projectManagerCanCompleteSpendProfile(project.getId(), user));
    }

    @Test
    public void testProjectFinanceUserCanViewViability() {

        Long projectId = 1L;
        Long organisationId = 1L;

        ProjectOrganisationCompositeId projectOrganisationCompositeId = new ProjectOrganisationCompositeId(projectId, organisationId);

        allGlobalRoleUsers.forEach(user -> {
            if (user.equals(projectFinanceUser())) {
                assertTrue(rules.projectFinanceUserCanViewViability(projectOrganisationCompositeId, user));
            } else {
                assertFalse(rules.projectFinanceUserCanViewViability(projectOrganisationCompositeId, user));
            }
        });
    }

    @Test
    public void testProjectFinanceUserCanSaveViability() {

        Long projectId = 1L;
        Long organisationId = 1L;

        ProjectOrganisationCompositeId projectOrganisationCompositeId = new ProjectOrganisationCompositeId(projectId, organisationId);

        allGlobalRoleUsers.forEach(user -> {
            if (user.equals(projectFinanceUser())) {
                assertTrue(rules.projectFinanceUserCanSaveViability(projectOrganisationCompositeId, user));
            } else {
                assertFalse(rules.projectFinanceUserCanSaveViability(projectOrganisationCompositeId, user));
            }
        });
    }

    @Test
    public void testProjectManagerCanIncompleteAnySpendProfile() throws Exception {
        ProjectResource project = newProjectResource().build();
        UserResource user = newUserResource().build();
        ProjectOrganisationCompositeId projectOrganisationCompositeId =
                new ProjectOrganisationCompositeId(project.getId(), newOrganisation().build().getId());

        setUpUserAsProjectManager(project, user);

        assertTrue(rules.projectManagerCanMarkSpendProfileIncomplete(projectOrganisationCompositeId, user));
    }

    @Test
    public void testPartnerCannotIncompleteSpendProfile() throws Exception {
        ProjectResource project = newProjectResource().build();
        UserResource user = newUserResource().build();
        ProjectOrganisationCompositeId projectOrganisationCompositeId =
                new ProjectOrganisationCompositeId(project.getId(), newOrganisation().build().getId());

        setUpUserNotAsProjectManager(user);

        assertFalse(rules.projectManagerCanMarkSpendProfileIncomplete(projectOrganisationCompositeId, user));
    }

    @Test
    public void testProjectFinanceUserCanSaveCreditReport() {

        Long projectId = 1L;

        allGlobalRoleUsers.forEach(user -> {
            if (user.equals(projectFinanceUser())) {
                assertTrue(rules.projectFinanceUserCanSaveCreditReport(projectId, user));
            } else {
                assertFalse(rules.projectFinanceUserCanSaveCreditReport(projectId, user));
            }
        });
    }

    @Test
    public void testProjectFinanceUserCanViewCreditReport() {

        Long projectId = 1L;

        allGlobalRoleUsers.forEach(user -> {
            if (user.equals(projectFinanceUser())) {
                assertTrue(rules.projectFinanceUserCanViewCreditReport(projectId, user));
            } else {
                assertFalse(rules.projectFinanceUserCanViewCreditReport(projectId, user));
            }
        });
    }

    @Override
    protected ProjectFinancePermissionRules supplyPermissionRulesUnderTest() {
        return new ProjectFinancePermissionRules();
    }
}