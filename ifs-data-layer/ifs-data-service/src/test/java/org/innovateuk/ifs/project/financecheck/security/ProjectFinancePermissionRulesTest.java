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
import org.innovateuk.ifs.project.resource.ProjectState;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.List;

import static java.util.Arrays.asList;
import static junit.framework.TestCase.assertFalse;
import static org.innovateuk.ifs.finance.builder.ProjectFinanceResourceBuilder.newProjectFinanceResource;
import static org.innovateuk.ifs.invite.domain.ProjectParticipantRole.PROJECT_FINANCE_CONTACT;
import static org.innovateuk.ifs.invite.domain.ProjectParticipantRole.PROJECT_PARTNER;
import static org.innovateuk.ifs.project.core.builder.ProjectProcessBuilder.newProjectProcess;
import static org.innovateuk.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static org.innovateuk.ifs.project.core.builder.ProjectUserBuilder.newProjectUser;
import static org.innovateuk.ifs.project.finance.builder.FinanceCheckPartnerStatusResourceBuilder.FinanceCheckEligibilityResourceBuilder.newFinanceCheckEligibilityResource;
import static org.innovateuk.ifs.organisation.builder.OrganisationBuilder.newOrganisation;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.util.SecurityRuleUtil.isInternal;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class ProjectFinancePermissionRulesTest extends BasePermissionRulesTest<ProjectFinancePermissionRules> {
    private ProjectProcess projectProcess;
    private ProjectResource project;

    @Mock
    private ProjectProcessRepository projectProcessRepositoryMock;


    @Before
    public void setUp() throws Exception {
        projectProcess = newProjectProcess().withActivityState(ProjectState.SETUP).build();
        project = newProjectResource().withId(1L).withProjectState(ProjectState.SETUP).build();
    }

    @Test
    public void testProjectFinanceUserCanViewViability() {

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
    public void testProjectFinanceUserCanSaveViability() {

        Long organisationId = 1L;

        ProjectOrganisationCompositeId projectOrganisationCompositeId = new ProjectOrganisationCompositeId(project.getId(), organisationId);

        when(projectProcessRepositoryMock.findOneByTargetId(project.getId())).thenReturn(projectProcess);

        allGlobalRoleUsers.forEach(user -> {
            if (user.equals(projectFinanceUser())) {
                assertTrue(rules.projectFinanceUserCanSaveViability(projectOrganisationCompositeId, user));
            } else {
                assertFalse(rules.projectFinanceUserCanSaveViability(projectOrganisationCompositeId, user));
            }
        });
    }

    @Test
    public void testProjectFinanceUserCanViewEligibility() {

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
    public void testProjectFinanceUserCanSaveEligibility() {

        Long organisationId = 1L;

        ProjectOrganisationCompositeId projectOrganisationCompositeId = new ProjectOrganisationCompositeId(project.getId(), organisationId);

        when(projectProcessRepositoryMock.findOneByTargetId(project.getId())).thenReturn(projectProcess);

        allGlobalRoleUsers.forEach(user -> {
            if (user.equals(projectFinanceUser())) {
                assertTrue(rules.projectFinanceUserCanSaveEligibility(projectOrganisationCompositeId, user));
            } else {
                assertFalse(rules.projectFinanceUserCanSaveEligibility(projectOrganisationCompositeId, user));
            }
        });
    }

    @Test
    public void testProjectFinanceUserCanSaveCreditReport() {

        ProjectCompositeId projectId = ProjectCompositeId.id(1L);

        when(projectProcessRepositoryMock.findOneByTargetId(project.getId())).thenReturn(projectProcess);

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
    public void testInternalUserCanViewFinanceChecks() {
        ProjectCompositeId projectId = ProjectCompositeId.id(1L);
        allInternalUsers.forEach(user -> assertTrue(rules.internalUsersCanSeeTheProjectFinanceOverviewsForAllProjects(projectId, user)));
    }

    @Test
    public void testProjectUsersCanViewFinanceChecks() {
        UserResource user = newUserResource().build();

        setupUserAsPartner(project, user);

        assertTrue(rules.partnersCanSeeTheProjectFinanceOverviewsForTheirProject(ProjectCompositeId.id(project.getId()), user));
    }

    @Test
    public void testProjectFinanceContactCanViewFinanceChecks() {
        UserResource user = newUserResource().build();

        setupFinanceContactExpectations(project, user);
        List<Role> financeContact = asList(Role.FINANCE_CONTACT, Role.PARTNER);
        user.setRoles(financeContact);

        assertTrue(rules.partnersCanSeeTheProjectFinanceOverviewsForTheirProject(ProjectCompositeId.id(project.getId()), user));
    }


    private void setupFinanceContactExpectations(ProjectResource project, UserResource user) {
        List<ProjectUser> partnerProjectUser = newProjectUser().build(1);

        when(projectUserRepositoryMock.findByProjectIdAndUserIdAndRole(project.getId(), user.getId(), PROJECT_PARTNER)).thenReturn(partnerProjectUser);

        when(projectUserRepositoryMock.findByProjectIdAndUserIdAndRole(project.getId(), user.getId(), PROJECT_FINANCE_CONTACT)).thenReturn(partnerProjectUser);
    }

    @Test
    public void testProjectPartnersCanViewEligibility(){
        UserResource user = newUserResource().build();
        ProjectOrganisationCompositeId projectOrganisationCompositeId =
                new ProjectOrganisationCompositeId(project.getId(), newOrganisation().build().getId());

        setupUserAsPartner(project, user);
        assertTrue(rules.projectPartnersCanViewEligibility(projectOrganisationCompositeId, user));

        setupUserNotAsPartner(project, user);
        assertFalse(rules.projectPartnersCanViewEligibility(projectOrganisationCompositeId, user));
    }

    @Test
    public void testPartnersCanSeeTheProjectFinancesForTheirOrganisationProjectFinanceResource(){
        UserResource user = newUserResource().build();
        ProjectFinanceResource projectFinanceResource = newProjectFinanceResource().withProject(project.getId()).build();

        setupUserAsPartner(project, user);
        assertTrue(rules.partnersCanSeeTheProjectFinancesForTheirOrganisation(projectFinanceResource, user));

        setupUserNotAsPartner(project, user);
        assertFalse(rules.partnersCanSeeTheProjectFinancesForTheirOrganisation(projectFinanceResource, user));
    }

    @Test
    public void testInternalUserCanSeeProjectFinancesForOrganisations(){
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
    public void testPartnersCanAddEmptyRowWhenReadingProjectCosts(){

        UserResource user = newUserResource().build();
        ProjectFinanceResource projectFinanceResource = newProjectFinanceResource().withProject(project.getId()).build();

        when(projectProcessRepositoryMock.findOneByTargetId(project.getId())).thenReturn(projectProcess);

        setupUserAsPartner(project, user);
        assertTrue(rules.partnersCanAddEmptyRowWhenReadingProjectCosts(projectFinanceResource, user));

        setupUserNotAsPartner(project, user);
        assertFalse(rules.partnersCanAddEmptyRowWhenReadingProjectCosts(projectFinanceResource, user));
    }

    @Test
    public void testInternalUsersCanAddEmptyRowWhenReadingProjectCosts(){
        ProjectFinanceResource projectFinanceResource = newProjectFinanceResource().withProject(project.getId()).build();

        when(projectProcessRepositoryMock.findOneByTargetId(project.getId())).thenReturn(projectProcess);

        allGlobalRoleUsers.forEach(user -> {
            if (isInternal(user)) {
                assertTrue(rules.internalUsersCanAddEmptyRowWhenReadingProjectCosts(projectFinanceResource, user));
            } else {
                assertFalse(rules.internalUsersCanAddEmptyRowWhenReadingProjectCosts(projectFinanceResource, user));
            }
        });
    }

    @Test
    public void testPartnersCanSeeTheProjectFinancesForTheirOrganisation(){
        UserResource user = newUserResource().build();
        FinanceCheckEligibilityResource financeCheckEligibilityResource = newFinanceCheckEligibilityResource().withProjectId(project.getId()).build();

        setupUserAsPartner(project, user);
        assertTrue(rules.partnersCanSeeTheProjectFinancesForTheirOrganisation(financeCheckEligibilityResource, user));

        setupUserNotAsPartner(project, user);
        assertFalse(rules.partnersCanSeeTheProjectFinancesForTheirOrganisation(financeCheckEligibilityResource, user));
    }

    @Test
    public void testInternalUsersCanSeeTheProjectFinancesForTheirOrganisation(){
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