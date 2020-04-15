package org.innovateuk.ifs.project.financecheck.security;

import org.innovateuk.ifs.BasePermissionRulesTest;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.finance.resource.ProjectFinanceResource;
import org.innovateuk.ifs.project.core.domain.Project;
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
import java.util.Optional;

import static java.util.Arrays.asList;
import static junit.framework.TestCase.assertFalse;
import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.finance.builder.ProjectFinanceResourceBuilder.newProjectFinanceResource;
import static org.innovateuk.ifs.organisation.builder.OrganisationBuilder.newOrganisation;
import static org.innovateuk.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static org.innovateuk.ifs.project.core.builder.ProjectBuilder.newProject;
import static org.innovateuk.ifs.project.core.builder.ProjectProcessBuilder.newProjectProcess;
import static org.innovateuk.ifs.project.core.builder.ProjectUserBuilder.newProjectUser;
import static org.innovateuk.ifs.project.core.domain.ProjectParticipantRole.PROJECT_FINANCE_CONTACT;
import static org.innovateuk.ifs.project.core.domain.ProjectParticipantRole.PROJECT_PARTNER;
import static org.innovateuk.ifs.project.finance.builder.FinanceCheckPartnerStatusResourceBuilder.FinanceCheckEligibilityResourceBuilder.newFinanceCheckEligibilityResource;
import static org.innovateuk.ifs.project.resource.ProjectState.SETUP;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.user.resource.Role.COMPETITION_FINANCE;
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
    public void competitionFinanceUserCanViewViability() {

        Long organisationId = 1L;
        UserResource userResource = newUserResource().withRoleGlobal(COMPETITION_FINANCE).build();
        UserResource userResourceNotInCompetition = newUserResource().withRoleGlobal(COMPETITION_FINANCE).build();
        Competition competition = newCompetition().build();
        Project competitionFinanceProject = newProject().withId(project.getId()).withApplication(newApplication().withCompetition(competition).build()).build();
        ProjectOrganisationCompositeId projectOrganisationCompositeId = new ProjectOrganisationCompositeId(project.getId(), organisationId);

        when(projectRepository.findById(competitionFinanceProject.getId())).thenReturn(Optional.of(competitionFinanceProject));
        when(competitionFinanceRepository.existsByCompetitionIdAndUserId(competition.getId(), userResource.getId())).thenReturn(true);

        assertTrue(rules.competitionFinanceUserCanViewViability(projectOrganisationCompositeId, userResource));
        assertFalse(rules.competitionFinanceUserCanViewViability(projectOrganisationCompositeId, userResourceNotInCompetition));
    }

    @Test
    public void competitionFinanceUserCanSaveViability() {

        Long organisationId = 1L;
        UserResource userResource = newUserResource().withRoleGlobal(COMPETITION_FINANCE).build();
        UserResource userResourceNotInCompetition = newUserResource().withRoleGlobal(COMPETITION_FINANCE).build();
        Competition competition = newCompetition().build();
        Project competitionFinanceProject = newProject().withId(project.getId()).withApplication(newApplication().withCompetition(competition).build()).build();
        ProjectOrganisationCompositeId projectOrganisationCompositeId = new ProjectOrganisationCompositeId(project.getId(), organisationId);

        when(projectRepository.findById(competitionFinanceProject.getId())).thenReturn(Optional.of(competitionFinanceProject));
        when(competitionFinanceRepository.existsByCompetitionIdAndUserId(competition.getId(), userResource.getId())).thenReturn(true);
        when(projectProcessRepository.findOneByTargetId(competitionFinanceProject.getId())).thenReturn(projectProcess);

        assertTrue(rules.competitionFinanceUserCanSaveViability(projectOrganisationCompositeId, userResource));
        assertFalse(rules.competitionFinanceUserCanSaveViability(projectOrganisationCompositeId, userResourceNotInCompetition));
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
    public void competitionFinanceUserCanViewEligibility() {

        Long organisationId = 1L;
        UserResource userResource = newUserResource().withRoleGlobal(COMPETITION_FINANCE).build();
        UserResource userResourceNotInCompetition = newUserResource().withRoleGlobal(COMPETITION_FINANCE).build();
        Competition competition = newCompetition().build();
        Project competitionFinanceProject = newProject().withId(project.getId()).withApplication(newApplication().withCompetition(competition).build()).build();
        ProjectOrganisationCompositeId projectOrganisationCompositeId = new ProjectOrganisationCompositeId(project.getId(), organisationId);

        when(projectRepository.findById(competitionFinanceProject.getId())).thenReturn(Optional.of(competitionFinanceProject));
        when(competitionFinanceRepository.existsByCompetitionIdAndUserId(competition.getId(), userResource.getId())).thenReturn(true);

        assertTrue(rules.competitionFinanceUserCanViewEligibility(projectOrganisationCompositeId, userResource));
        assertFalse(rules.competitionFinanceUserCanViewEligibility(projectOrganisationCompositeId, userResourceNotInCompetition));
    }

    @Test
    public void competitionFinanceUserCanSaveEligibility() {

        Long organisationId = 1L;
        UserResource userResource = newUserResource().withRoleGlobal(COMPETITION_FINANCE).build();
        UserResource userResourceNotInCompetition = newUserResource().withRoleGlobal(COMPETITION_FINANCE).build();
        Competition competition = newCompetition().build();
        Project competitionFinanceProject = newProject().withId(project.getId()).withApplication(newApplication().withCompetition(competition).build()).build();
        ProjectOrganisationCompositeId projectOrganisationCompositeId = new ProjectOrganisationCompositeId(project.getId(), organisationId);

        when(projectRepository.findById(competitionFinanceProject.getId())).thenReturn(Optional.of(competitionFinanceProject));
        when(competitionFinanceRepository.existsByCompetitionIdAndUserId(competition.getId(), userResource.getId())).thenReturn(true);
        when(projectProcessRepository.findOneByTargetId(competitionFinanceProject.getId())).thenReturn(projectProcess);

        assertTrue(rules.competitionFinanceUserCanSaveEligibility(projectOrganisationCompositeId, userResource));
        assertFalse(rules.competitionFinanceUserCanSaveEligibility(projectOrganisationCompositeId, userResourceNotInCompetition));
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
    public void competitionFinanceCanSaveCreditReport() {

        Competition competition = newCompetition().build();
        Project competitionFinanceProject = newProject().withId(project.getId()).withApplication(newApplication().withCompetition(competition).build()).build();
        ProjectCompositeId projectId = ProjectCompositeId.id(1L);
        UserResource userResource = newUserResource().withRoleGlobal(COMPETITION_FINANCE).build();
        UserResource userResourceNotInCompetition = newUserResource().withRoleGlobal(COMPETITION_FINANCE).build();

        when(projectRepository.findById(projectId.id())).thenReturn(Optional.of(competitionFinanceProject));
        when(competitionFinanceRepository.existsByCompetitionIdAndUserId(competition.getId(), userResource.getId())).thenReturn(true);
        when(projectProcessRepository.findOneByTargetId(competitionFinanceProject.getId())).thenReturn(projectProcess);

        assertTrue(rules.competitionFinanceUserCanSaveCreditReport(projectId, userResource));
        assertFalse(rules.competitionFinanceUserCanSaveCreditReport(projectId, userResourceNotInCompetition));
    }

    @Test
    public void competitionFinanceCanViewCreditReport() {

        Competition competition = newCompetition().build();
        Project competitionFinanceProject = newProject().withId(project.getId()).withApplication(newApplication().withCompetition(competition).build()).build();
        ProjectCompositeId projectId = ProjectCompositeId.id(1L);
        UserResource userResource = newUserResource().withRoleGlobal(COMPETITION_FINANCE).build();
        UserResource userResourceNotInCompetition = newUserResource().withRoleGlobal(COMPETITION_FINANCE).build();

        when(projectRepository.findById(projectId.id())).thenReturn(Optional.of(competitionFinanceProject));
        when(competitionFinanceRepository.existsByCompetitionIdAndUserId(competition.getId(), userResource.getId())).thenReturn(true);

        assertTrue(rules.competitionFinanceUserCanViewCreditReport(projectId, userResource));
        assertFalse(rules.competitionFinanceUserCanViewCreditReport(projectId, userResourceNotInCompetition));
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
    public void competitionFinanceUserCanViewFinanceChecks() {
        ProjectCompositeId projectId = ProjectCompositeId.id(1L);
        Competition competition = newCompetition().build();
        Project competitionFinanceProject = newProject().withId(project.getId()).withApplication(newApplication().withCompetition(competition).build()).build();
        UserResource userResource = newUserResource().withRoleGlobal(COMPETITION_FINANCE).build();
        UserResource userResourceNotInCompetition = newUserResource().withRoleGlobal(COMPETITION_FINANCE).build();

        when(projectRepository.findById(projectId.id())).thenReturn(Optional.of(competitionFinanceProject));
        when(competitionFinanceRepository.existsByCompetitionIdAndUserId(competition.getId(), userResource.getId())).thenReturn(true);

        assertTrue(rules.competitionFinanceUsersCanSeeTheProjectFinanceOverviewsForAllProjects(projectId, userResource));
        assertFalse(rules.competitionFinanceUsersCanSeeTheProjectFinanceOverviewsForAllProjects(projectId, userResourceNotInCompetition));
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
    public void competitionFinanceUserCanSeeProjectFinancesForOrganisations() {

        ProjectFinanceResource projectFinanceResource = newProjectFinanceResource().withProject(project.getId()).build();

        allGlobalRoleUsers.forEach(user -> {
            if (user.equals(competitionFinanceUser())) {
                assertTrue(rules.competitionFinanceUserCanSeeProjectFinancesForOrganisations(projectFinanceResource, user));
            } else {
                assertFalse(rules.competitionFinanceUserCanSeeProjectFinancesForOrganisations(projectFinanceResource, user));
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

    @Test
    public void competitionFinanceUsersCanSeeTheProjectFinancesForTheirOrganisation() {
        UserResource user = newUserResource().withRoleGlobal(COMPETITION_FINANCE).build();
        UserResource userNotInCompetition = newUserResource().withRoleGlobal(COMPETITION_FINANCE).build();
        FinanceCheckEligibilityResource financeCheckEligibilityResource = newFinanceCheckEligibilityResource().withProjectId(project.getId()).build();
        Competition competition = newCompetition().build();
        Project competitionFinanceProject = newProject().withId(project.getId()).withApplication(newApplication().withCompetition(competition).build()).build();

        when(projectRepository.findById(competitionFinanceProject.getId())).thenReturn(Optional.of(competitionFinanceProject));
        when(competitionFinanceRepository.existsByCompetitionIdAndUserId(competition.getId(), user.getId())).thenReturn(true);

        setupUserAsPartner(project, user);
        assertTrue(rules.competitionFinanceUsersCanSeeTheProjectFinancesForTheirOrganisation(financeCheckEligibilityResource, user));
        assertFalse(rules.competitionFinanceUsersCanSeeTheProjectFinancesForTheirOrganisation(financeCheckEligibilityResource, userNotInCompetition));
    }

    @Override
    protected ProjectFinancePermissionRules supplyPermissionRulesUnderTest() {
        return new ProjectFinancePermissionRules();
    }
}