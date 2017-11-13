package org.innovateuk.ifs.project.spendprofile.security;

import org.innovateuk.ifs.BasePermissionRulesTest;
import org.innovateuk.ifs.project.resource.ProjectOrganisationCompositeId;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.user.resource.OrganisationResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Test;

import static junit.framework.TestCase.assertFalse;
import static org.innovateuk.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static org.innovateuk.ifs.user.builder.OrganisationBuilder.newOrganisation;
import static org.innovateuk.ifs.user.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.util.SecurityRuleUtil.isInternalAdmin;
import static org.innovateuk.ifs.util.SecurityRuleUtil.isSupport;
import static org.junit.Assert.assertTrue;

public class SpendProfilePermissionRulesTest extends BasePermissionRulesTest<SpendProfilePermissionRules> {

    @Test
    public void testProjectManagerCanCompleteSpendProfile() throws Exception {
        ProjectResource project = newProjectResource().build();
        UserResource user = newUserResource().build();

        setUpUserAsProjectManager(project, user);

        assertTrue(rules.projectManagerCanCompleteSpendProfile(project.getId(), user));
    }

    @Test
    public void testLeadPartnerCanIncompleteAnySpendProfile() throws Exception {
        ProjectResource project = newProjectResource().build();
        UserResource user = newUserResource().build();
        ProjectOrganisationCompositeId projectOrganisationCompositeId =
                new ProjectOrganisationCompositeId(project.getId(), newOrganisation().build().getId());

        setupUserAsLeadPartner(project, user);

        assertTrue(rules.leadPartnerCanMarkSpendProfileIncomplete(projectOrganisationCompositeId, user));
    }

    @Test
    public void testNonLeadPartnerCannotIncompleteSpendProfile() throws Exception {
        ProjectResource project = newProjectResource().build();
        UserResource user = newUserResource().build();
        ProjectOrganisationCompositeId projectOrganisationCompositeId =
                new ProjectOrganisationCompositeId(project.getId(), newOrganisation().build().getId());

        setupUserNotAsLeadPartner(project, user);

        assertFalse(rules.leadPartnerCanMarkSpendProfileIncomplete(projectOrganisationCompositeId, user));
    }

    @Test
    public void testLeadPartnerCanViewAnySpendProfileData() throws Exception {

        ProjectResource project = newProjectResource().build();
        UserResource user = newUserResource().build();
        ProjectOrganisationCompositeId projectOrganisationCompositeId =
                new ProjectOrganisationCompositeId(project.getId(), newOrganisation().build().getId());

        setupUserAsLeadPartner(project, user);

        assertTrue(rules.leadPartnerCanViewAnySpendProfileData(projectOrganisationCompositeId, user));
    }
    @Test
    public void testUserNotLeadPartnerCannotViewSpendProfile() throws Exception {
        ProjectResource project = newProjectResource().build();
        UserResource user = newUserResource().build();
        ProjectOrganisationCompositeId projectOrganisationCompositeId =
                new ProjectOrganisationCompositeId(project.getId(), newOrganisation().build().getId());

        setupUserNotAsLeadPartner(project, user);

        assertFalse(rules.leadPartnerCanViewAnySpendProfileData(projectOrganisationCompositeId, user));
    }

    @Test
    public void testPartnersCanViewTheirOwnSpendProfileData(){
        ProjectResource project = newProjectResource().build();
        UserResource user = newUserResource().build();
        OrganisationResource org = newOrganisationResource().build();
        ProjectOrganisationCompositeId projectOrganisationCompositeId =
                new ProjectOrganisationCompositeId(project.getId(), org.getId());

        setupUserAsPartner(project, user, org);
        assertTrue(rules.partnersCanViewTheirOwnSpendProfileData(projectOrganisationCompositeId, user));

        setupUserNotAsPartner(project, user, org);
        assertFalse(rules.partnersCanViewTheirOwnSpendProfileData(projectOrganisationCompositeId, user));
    }

    @Test
    public void testProjectFinanceUserCanViewAnySpendProfileData(){
        ProjectOrganisationCompositeId projectOrganisationCompositeId =
                new ProjectOrganisationCompositeId(1L, newOrganisation().build().getId());

        allGlobalRoleUsers.forEach(user -> {
            if (user.equals(projectFinanceUser())) {
                assertTrue(rules.projectFinanceUserCanViewAnySpendProfileData(projectOrganisationCompositeId, user));
            } else {
                assertFalse(rules.projectFinanceUserCanViewAnySpendProfileData(projectOrganisationCompositeId, user));
            }
        });
    }

    @Test
    public void testPartnersCanViewTheirOwnSpendProfileCsv(){
        ProjectResource project = newProjectResource().build();
        UserResource user = newUserResource().build();
        OrganisationResource org = newOrganisationResource().build();
        ProjectOrganisationCompositeId projectOrganisationCompositeId =
                new ProjectOrganisationCompositeId(project.getId(), org.getId());

        setupUserAsPartner(project, user, org);
        assertTrue(rules.partnersCanViewTheirOwnSpendProfileCsv(projectOrganisationCompositeId, user));

        setupUserNotAsPartner(project, user, org);
        assertFalse(rules.partnersCanViewTheirOwnSpendProfileCsv(projectOrganisationCompositeId, user));
    }

    @Test
    public void testInternalAdminUsersCanSeeSpendProfileCsv(){
        ProjectOrganisationCompositeId projectOrganisationCompositeId =
                new ProjectOrganisationCompositeId(1L, newOrganisation().build().getId());

        allGlobalRoleUsers.forEach(user -> {
            if (isInternalAdmin(user)) {
                assertTrue(rules.internalAdminUsersCanSeeSpendProfileCsv(projectOrganisationCompositeId, user));
            } else {
                assertFalse(rules.internalAdminUsersCanSeeSpendProfileCsv(projectOrganisationCompositeId, user));
            }
        });
    }

    @Test
    public void testSupportUsersCanSeeSpendProfileCsv(){
        ProjectOrganisationCompositeId projectOrganisationCompositeId =
                new ProjectOrganisationCompositeId(1L, newOrganisation().build().getId());

        allGlobalRoleUsers.forEach(user -> {
            if (isSupport(user)) {
                assertTrue(rules.supportUsersCanSeeSpendProfileCsv(projectOrganisationCompositeId, user));
            } else {
                assertFalse(rules.supportUsersCanSeeSpendProfileCsv(projectOrganisationCompositeId, user));
            }
        });
    }

    @Test
    public void testLeadPartnerCanViewAnySpendProfileCsv(){
        ProjectResource project = newProjectResource().build();
        UserResource user = newUserResource().build();
        ProjectOrganisationCompositeId projectOrganisationCompositeId =
                new ProjectOrganisationCompositeId(project.getId(), newOrganisation().build().getId());

        setupUserAsLeadPartner(project, user);
        assertTrue(rules.leadPartnerCanViewAnySpendProfileCsv(projectOrganisationCompositeId, user));

        setupUserNotAsLeadPartner(project, user);
        assertFalse(rules.leadPartnerCanViewAnySpendProfileCsv(projectOrganisationCompositeId, user));
    }

    @Test
    public void testPartnersCanEditTheirOwnSpendProfileData(){
        ProjectResource project = newProjectResource().build();
        UserResource user = newUserResource().build();
        OrganisationResource org = newOrganisationResource().build();
        ProjectOrganisationCompositeId projectOrganisationCompositeId =
                new ProjectOrganisationCompositeId(project.getId(), org.getId());

        setupUserAsPartner(project, user, org);
        assertTrue(rules.partnersCanEditTheirOwnSpendProfileData(projectOrganisationCompositeId, user));

        setupUserNotAsPartner(project, user, org);
        assertFalse(rules.partnersCanEditTheirOwnSpendProfileData(projectOrganisationCompositeId, user));
    }

    @Test
    public void testPartnersCanMarkSpendProfileAsComplete(){
        ProjectResource project = newProjectResource().build();
        UserResource user = newUserResource().build();
        OrganisationResource org = newOrganisationResource().build();
        ProjectOrganisationCompositeId projectOrganisationCompositeId =
                new ProjectOrganisationCompositeId(project.getId(), org.getId());

        setupUserAsPartner(project, user, org);
        assertTrue(rules.partnersCanMarkSpendProfileAsComplete(projectOrganisationCompositeId, user));

        setupUserNotAsPartner(project, user, org);
        assertFalse(rules.partnersCanMarkSpendProfileAsComplete(projectOrganisationCompositeId, user));
    }

    @Override
    protected SpendProfilePermissionRules supplyPermissionRulesUnderTest() {
        return new SpendProfilePermissionRules();
    }
}