package org.innovateuk.ifs.project.financecheck.security;

import org.innovateuk.ifs.BasePermissionRulesTest;
import org.innovateuk.ifs.finance.resource.ProjectFinanceResource;
import org.innovateuk.ifs.project.core.domain.ProjectProcess;
import org.innovateuk.ifs.project.core.domain.ProjectUser;
import org.innovateuk.ifs.project.core.repository.ProjectProcessRepository;
import org.innovateuk.ifs.project.finance.resource.FinanceCheckEligibilityResource;
import org.innovateuk.ifs.project.financechecks.security.ProjectFinancePermissionRules;
import org.innovateuk.ifs.project.resource.ProjectCompositeId;
import org.innovateuk.ifs.project.resource.ProjectOrganisationCompositeId;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.List;

import static java.util.Arrays.asList;
import static junit.framework.TestCase.assertFalse;
import static org.innovateuk.ifs.finance.builder.ProjectFinanceResourceBuilder.newProjectFinanceResource;
import static org.innovateuk.ifs.organisation.builder.OrganisationBuilder.newOrganisation;
import static org.innovateuk.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static org.innovateuk.ifs.project.core.builder.ProjectProcessBuilder.newProjectProcess;
import static org.innovateuk.ifs.project.core.builder.ProjectUserBuilder.newProjectUser;
import static org.innovateuk.ifs.project.core.domain.ProjectParticipantRole.PROJECT_FINANCE_CONTACT;
import static org.innovateuk.ifs.project.core.domain.ProjectParticipantRole.PROJECT_PARTNER;
import static org.innovateuk.ifs.project.finance.builder.FinanceCheckPartnerStatusResourceBuilder.FinanceCheckEligibilityResourceBuilder.newFinanceCheckEligibilityResource;
import static org.innovateuk.ifs.project.resource.ProjectState.SETUP;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.util.SecurityRuleUtil.isInternal;
import static org.innovateuk.ifs.util.SecurityRuleUtil.isProjectFinanceUser;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class ProjectFinancePermissionRulesTest extends BasePermissionRulesTest<ProjectFinancePermissionRules> {
    private ProjectProcess projectProcess;
    private ProjectResource project;

    @Mock
    private ProjectProcessRepository projectProcessRepository;


    @Before
    public void setUp() throws Exception {
        projectProcess = newProjectProcess().withActivityState(SETUP).build();
        project = newProjectResource().withId(1L).withProjectState(SETUP).build();
    }

    @Test
    public void projectFinanceUserCanViewViability() {

        Long organisationId = 1L;

        ProjectOrganisationCompositeId projectOrganisationCompositeId = new ProjectOrganisationCompositeId(project.getId(), organisationId);

        allGlobalRoleUsers.forEach(user -> {
            if (user.equals(projectFinanceUser())) {
                assertTrue(rules.projectFinanceUserCanViewViability(projectOrganisationCompositeId, user));
            } else {
                assertFalse(rules.projectFinanceUserCanViewViability(projectOrganisationCompositeId, user));
            }
        });
    }

    @Test
    public void projectFinanceUserCanSaveViability() {

        Long organisationId = 1L;

        ProjectOrganisationCompositeId projectOrganisationCompositeId = new ProjectOrganisationCompositeId(project.getId(), organisationId);

        when(projectProcessRepository.findOneByTargetId(project.getId())).thenReturn(projectProcess);

        allGlobalRoleUsers.forEach(user -> {
            if (user.equals(projectFinanceUser())) {
                assertTrue(rules.projectFinanceUserCanSaveViability(projectOrganisationCompositeId, user));
            } else {
                assertFalse(rules.projectFinanceUserCanSaveViability(projectOrganisationCompositeId, user));
            }
        });
    }

    @Test
    public void projectFinanceUserCanViewEligibility() {

        Long organisationId = 1L;

        ProjectOrganisationCompositeId projectOrganisationCompositeId = new ProjectOrganisationCompositeId(project.getId(), organisationId);

        allGlobalRoleUsers.forEach(user -> {
            if (user.equals(projectFinanceUser())) {
                assertTrue(rules.projectFinanceUserCanViewEligibility(projectOrganisationCompositeId, user));
            } else {
                assertFalse(rules.projectFinanceUserCanViewEligibility(projectOrganisationCompositeId, user));
            }
        });
    }

    @Test
    public void projectFinanceUserCanSaveEligibility() {

        Long organisationId = 1L;

        ProjectOrganisationCompositeId projectOrganisationCompositeId = new ProjectOrganisationCompositeId(project.getId(), organisationId);

        when(projectProcessRepository.findOneByTargetId(project.getId())).thenReturn(projectProcess);

        allGlobalRoleUsers.forEach(user -> {
            if (user.equals(projectFinanceUser())) {
                assertTrue(rules.projectFinanceUserCanSaveEligibility(projectOrganisationCompositeId, user));
            } else {
                assertFalse(rules.projectFinanceUserCanSaveEligibility(projectOrganisationCompositeId, user));
            }
        });
    }

    @Test
    public void projectFinanceUserCanSaveCreditReport() {

        ProjectCompositeId projectId = ProjectCompositeId.id(1L);

        when(projectProcessRepository.findOneByTargetId(project.getId())).thenReturn(projectProcess);

        allGlobalRoleUsers.forEach(user -> {
            if (user.equals(projectFinanceUser())) {
                assertTrue(rules.projectFinanceUserCanSaveCreditReport(projectId, user));
            } else {
                assertFalse(rules.projectFinanceUserCanSaveCreditReport(projectId, user));
            }
        });
    }

    @Test
    public void projectFinanceUserCanViewCreditReport() {

        ProjectCompositeId projectId = ProjectCompositeId.id(1L);

        allGlobalRoleUsers.forEach(user -> {
            if (user.equals(projectFinanceUser())) {
                assertTrue(rules.projectFinanceUserCanViewCreditReport(projectId, user));
            } else {
                assertFalse(rules.projectFinanceUserCanViewCreditReport(projectId, user));
            }
        });
    }

    @Test
    public void internalUserCanViewFinanceChecks() {
        ProjectCompositeId projectId = ProjectCompositeId.id(1L);
        allInternalUsers.forEach(user -> assertTrue(rules.internalUsersCanSeeTheProjectFinanceOverviewsForAllProjects(projectId, user)));
    }

    @Test
    public void projectUsersCanViewFinanceChecks() {
        UserResource user = newUserResource().build();

        setupUserAsPartner(project, user);

        assertTrue(rules.partnersCanSeeTheProjectFinanceOverviewsForTheirProject(ProjectCompositeId.id(project.getId()), user));
    }

    @Test
    public void projectFinanceContactCanViewFinanceChecks() {
        UserResource user = newUserResource().build();

        setupFinanceContactExpectations(project, user);
        List<Role> financeContact = asList(Role.FINANCE_CONTACT, Role.PARTNER);
        user.setRoles(financeContact);

        assertTrue(rules.partnersCanSeeTheProjectFinanceOverviewsForTheirProject(ProjectCompositeId.id(project.getId()), user));
    }


    private void setupFinanceContactExpectations(ProjectResource project, UserResource user) {
        List<ProjectUser> partnerProjectUser = newProjectUser().build(1);

        when(projectUserRepository.findByProjectIdAndUserIdAndRole(project.getId(), user.getId(), PROJECT_PARTNER)).thenReturn(partnerProjectUser);

        when(projectUserRepository.findByProjectIdAndUserIdAndRole(project.getId(), user.getId(), PROJECT_FINANCE_CONTACT)).thenReturn(partnerProjectUser);
    }

    @Test
    public void projectPartnersCanViewEligibility() {
        UserResource user = newUserResource().build();
        ProjectOrganisationCompositeId projectOrganisationCompositeId =
                new ProjectOrganisationCompositeId(project.getId(), newOrganisation().build().getId());

        setupUserAsPartner(project, user);
        assertTrue(rules.projectPartnersCanViewEligibility(projectOrganisationCompositeId, user));

        setupUserNotAsPartner(project, user);
        assertFalse(rules.projectPartnersCanViewEligibility(projectOrganisationCompositeId, user));
    }

    @Test
    public void partnersCanSeeTheProjectFinancesForTheirOrganisationProjectFinanceResource() {
        UserResource user = newUserResource().build();
        ProjectFinanceResource projectFinanceResource = newProjectFinanceResource().withProject(project.getId()).build();

        setupUserAsPartner(project, user);
        assertTrue(rules.partnersCanSeeTheProjectFinancesForTheirOrganisation(projectFinanceResource, user));

        setupUserNotAsPartner(project, user);
        assertFalse(rules.partnersCanSeeTheProjectFinancesForTheirOrganisation(projectFinanceResource, user));
    }

    @Test
    public void internalUserCanSeeProjectFinancesForOrganisations() {
        ProjectFinanceResource projectFinanceResource = newProjectFinanceResource().withProject(project.getId()).build();

        allGlobalRoleUsers.forEach(user -> {
            if (isInternal(user)) {
                assertTrue(rules.internalUserCanSeeProjectFinancesForOrganisations(projectFinanceResource, user));
            } else {
                assertFalse(rules.internalUserCanSeeProjectFinancesForOrganisations(projectFinanceResource, user));
            }
        });
    }

    @Test
    public void projectPartnerCanUpdateProjectFinance() {
        UserResource user = newUserResource().build();
        ProjectFinanceResource projectFinanceResource = newProjectFinanceResource().withProject(project.getId()).build();

        setupUserAsPartner(project, user);
        assertTrue(rules.projectPartnerCanUpdateProjectFinance(projectFinanceResource, user));

        setupUserNotAsPartner(project, user);
        assertFalse(rules.projectPartnerCanUpdateProjectFinance(projectFinanceResource, user));
    }

    @Test
    public void internalUsersCanUpdateProjectFinance() {
        ProjectFinanceResource projectFinanceResource = newProjectFinanceResource().withProject(project.getId()).build();

        allGlobalRoleUsers.forEach(user -> {
            if (isProjectFinanceUser(user)) {
                assertTrue(rules.internalUsersCanUpdateProjectFinance(projectFinanceResource, user));
            } else {
                assertFalse(rules.internalUsersCanUpdateProjectFinance(projectFinanceResource, user));
            }
        });
    }

    @Test
    public void partnersCanAddEmptyRowWhenReadingProjectCosts() {

        UserResource user = newUserResource().build();
        ProjectFinanceResource projectFinanceResource = newProjectFinanceResource().withProject(project.getId()).build();

        when(projectProcessRepository.findOneByTargetId(project.getId())).thenReturn(projectProcess);

        setupUserAsPartner(project, user);
        assertTrue(rules.partnersCanAddEmptyRowWhenReadingProjectCosts(projectFinanceResource, user));

        setupUserNotAsPartner(project, user);
        assertFalse(rules.partnersCanAddEmptyRowWhenReadingProjectCosts(projectFinanceResource, user));
    }

    @Test
    public void internalUsersCanAddEmptyRowWhenReadingProjectCosts() {
        ProjectFinanceResource projectFinanceResource = newProjectFinanceResource().withProject(project.getId()).build();

        when(projectProcessRepository.findOneByTargetId(project.getId())).thenReturn(projectProcess);

        allGlobalRoleUsers.forEach(user -> {
            if (isInternal(user)) {
                assertTrue(rules.internalUsersCanAddEmptyRowWhenReadingProjectCosts(projectFinanceResource, user));
            } else {
                assertFalse(rules.internalUsersCanAddEmptyRowWhenReadingProjectCosts(projectFinanceResource, user));
            }
        });
    }

    @Test
    public void partnersCanSeeTheProjectFinancesForTheirOrganisation() {
        UserResource user = newUserResource().build();
        FinanceCheckEligibilityResource financeCheckEligibilityResource = newFinanceCheckEligibilityResource().withProjectId(project.getId()).build();

        setupUserAsPartner(project, user);
        assertTrue(rules.partnersCanSeeTheProjectFinancesForTheirOrganisation(financeCheckEligibilityResource, user));

        setupUserNotAsPartner(project, user);
        assertFalse(rules.partnersCanSeeTheProjectFinancesForTheirOrganisation(financeCheckEligibilityResource, user));
    }

    @Test
    public void internalUsersCanSeeTheProjectFinancesForTheirOrganisation() {
        FinanceCheckEligibilityResource financeCheckEligibilityResource = newFinanceCheckEligibilityResource().withProjectId(project.getId()).build();

        allGlobalRoleUsers.forEach(user -> {
            if (isInternal(user)) {
                assertTrue(rules.internalUsersCanSeeTheProjectFinancesForTheirOrganisation(financeCheckEligibilityResource, user));
            } else {
                assertFalse(rules.internalUsersCanSeeTheProjectFinancesForTheirOrganisation(financeCheckEligibilityResource, user));
            }
        });
    }

    @Override
    protected ProjectFinancePermissionRules supplyPermissionRulesUnderTest() {
        return new ProjectFinancePermissionRules();
    }
}