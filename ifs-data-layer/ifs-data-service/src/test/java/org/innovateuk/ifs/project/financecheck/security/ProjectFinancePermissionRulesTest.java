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
import org.innovateuk.ifs.project.monitoring.domain.MonitoringOfficer;
import org.innovateuk.ifs.project.monitoring.repository.MonitoringOfficerRepository;
import org.innovateuk.ifs.project.resource.ProjectCompositeId;
import org.innovateuk.ifs.project.resource.ProjectOrganisationCompositeId;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.user.resource.Authority;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.util.Collections.singletonList;
import static junit.framework.TestCase.assertFalse;
import static org.hibernate.validator.internal.util.CollectionHelper.asSet;
import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.finance.builder.ProjectFinanceResourceBuilder.newProjectFinanceResource;
import static org.innovateuk.ifs.organisation.builder.OrganisationBuilder.newOrganisation;
import static org.innovateuk.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static org.innovateuk.ifs.project.core.ProjectParticipantRole.PROJECT_FINANCE_CONTACT;
import static org.innovateuk.ifs.project.core.ProjectParticipantRole.PROJECT_USER_ROLES;
import static org.innovateuk.ifs.project.core.builder.ProjectBuilder.newProject;
import static org.innovateuk.ifs.project.core.builder.ProjectProcessBuilder.newProjectProcess;
import static org.innovateuk.ifs.project.core.builder.ProjectUserBuilder.newProjectUser;
import static org.innovateuk.ifs.project.finance.builder.FinanceCheckPartnerStatusResourceBuilder.FinanceCheckEligibilityResourceBuilder.newFinanceCheckEligibilityResource;
import static org.innovateuk.ifs.project.monitoring.builder.MonitoringOfficerBuilder.newMonitoringOfficer;
import static org.innovateuk.ifs.project.resource.ProjectState.*;
import static org.innovateuk.ifs.user.builder.UserBuilder.newUser;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.user.resource.Role.*;
import static org.innovateuk.ifs.util.SecurityRuleUtil.*;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class ProjectFinancePermissionRulesTest extends BasePermissionRulesTest<ProjectFinancePermissionRules> {

    private ProjectProcess projectProcess;
    private ProjectResource project;
    private long organisationId;

    @Mock
    private ProjectProcessRepository projectProcessRepository;

    @Mock
    private MonitoringOfficerRepository monitoringOfficerRepository;

    @Before
    public void setUp() throws Exception {
        projectProcess = newProjectProcess().withActivityState(SETUP).build();
        project = newProjectResource().withId(1L).withProjectState(SETUP).build();
        organisationId = 1L;
    }

    @Test
    public void projectFinanceUserCanViewViability() {

        ProjectOrganisationCompositeId projectOrganisationCompositeId = new ProjectOrganisationCompositeId(project.getId(), organisationId);

        allGlobalRoleUsers.forEach(user -> {
            if (user.hasAuthority(Authority.PROJECT_FINANCE)) {
                assertTrue(rules.projectFinanceUserCanViewViability(projectOrganisationCompositeId, user));
            } else {
                assertFalse(rules.projectFinanceUserCanViewViability(projectOrganisationCompositeId, user));
            }
        });
    }

    @Test
    public void externalFinanceUserCanViewViability() {

        UserResource userResource = newUserResource().withRoleGlobal(EXTERNAL_FINANCE).build();
        UserResource userResourceNotInCompetition = newUserResource().withRoleGlobal(EXTERNAL_FINANCE).build();
        Competition competition = newCompetition().build();
        Project competitionFinanceProject = newProject().withId(project.getId()).withApplication(newApplication().withCompetition(competition).build()).build();
        ProjectOrganisationCompositeId projectOrganisationCompositeId = new ProjectOrganisationCompositeId(project.getId(), organisationId);

        when(projectRepository.findById(competitionFinanceProject.getId())).thenReturn(Optional.of(competitionFinanceProject));
        when(externalFinanceRepository.existsByCompetitionIdAndUserId(competition.getId(), userResource.getId())).thenReturn(true);

        assertTrue(rules.competitionFinanceUserCanViewViability(projectOrganisationCompositeId, userResource));
        assertFalse(rules.competitionFinanceUserCanViewViability(projectOrganisationCompositeId, userResourceNotInCompetition));
    }

    @Test
    public void auditorUserCanViewViability() {

        UserResource userResource = newUserResource().withRoleGlobal(AUDITOR).build();
        Competition competition = newCompetition().build();
        Project competitionFinanceProject = newProject().withId(project.getId()).withApplication(newApplication().withCompetition(competition).build()).build();
        ProjectOrganisationCompositeId projectOrganisationCompositeId = new ProjectOrganisationCompositeId(project.getId(), organisationId);

        when(projectRepository.findById(competitionFinanceProject.getId())).thenReturn(Optional.of(competitionFinanceProject));

        assertTrue(rules.auditorUserCanViewViability(projectOrganisationCompositeId, userResource));
    }

    @Test
    public void competitionFinanceUserCanSaveViability() {

        UserResource userResource = newUserResource().withRoleGlobal(EXTERNAL_FINANCE).build();
        UserResource userResourceNotInCompetition = newUserResource().withRoleGlobal(EXTERNAL_FINANCE).build();
        Competition competition = newCompetition().build();
        Project competitionFinanceProject = newProject().withId(project.getId()).withApplication(newApplication().withCompetition(competition).build()).build();
        ProjectOrganisationCompositeId projectOrganisationCompositeId = new ProjectOrganisationCompositeId(project.getId(), organisationId);

        when(projectRepository.findById(competitionFinanceProject.getId())).thenReturn(Optional.of(competitionFinanceProject));
        when(externalFinanceRepository.existsByCompetitionIdAndUserId(competition.getId(), userResource.getId())).thenReturn(true);
        when(projectProcessRepository.findOneByTargetId(competitionFinanceProject.getId())).thenReturn(projectProcess);

        assertTrue(rules.competitionFinanceUserCanSaveViability(projectOrganisationCompositeId, userResource));
        assertFalse(rules.competitionFinanceUserCanSaveViability(projectOrganisationCompositeId, userResourceNotInCompetition));
    }

    @Test
    public void projectFinanceUserCanSaveViability() {

        ProjectOrganisationCompositeId projectOrganisationCompositeId = new ProjectOrganisationCompositeId(project.getId(), organisationId);

        when(projectProcessRepository.findOneByTargetId(project.getId())).thenReturn(projectProcess);

        allGlobalRoleUsers.forEach(user -> {
            if (hasProjectFinanceAuthority(user)) {
                assertTrue(rules.projectFinanceUserCanSaveViability(projectOrganisationCompositeId, user));
            } else {
                assertFalse(rules.projectFinanceUserCanSaveViability(projectOrganisationCompositeId, user));
            }
        });
    }

    @Test
    public void projectFinanceUserCanViewEligibility() {

        ProjectOrganisationCompositeId projectOrganisationCompositeId = new ProjectOrganisationCompositeId(project.getId(), organisationId);

        allGlobalRoleUsers.forEach(user -> {
            if (hasProjectFinanceAuthority(user)) {
                assertTrue(rules.projectFinanceUserCanViewEligibility(projectOrganisationCompositeId, user));
            } else {
                assertFalse(rules.projectFinanceUserCanViewEligibility(projectOrganisationCompositeId, user));
            }
        });
    }

    @Test
    public void auditorUserCanViewEligibility() {

        UserResource userResource = newUserResource().withRoleGlobal(AUDITOR).build();
        Competition competition = newCompetition().build();
        Project competitionFinanceProject = newProject().withId(project.getId()).withApplication(newApplication().withCompetition(competition).build()).build();
        ProjectOrganisationCompositeId projectOrganisationCompositeId = new ProjectOrganisationCompositeId(project.getId(), organisationId);

        when(projectRepository.findById(competitionFinanceProject.getId())).thenReturn(Optional.of(competitionFinanceProject));

        assertTrue(rules.auditorUserCanViewEligibility(projectOrganisationCompositeId, userResource));
    }

    @Test
    public void competitionFinanceUserCanViewEligibility() {

        UserResource userResource = newUserResource().withRoleGlobal(EXTERNAL_FINANCE).build();
        UserResource userResourceNotInCompetition = newUserResource().withRoleGlobal(EXTERNAL_FINANCE).build();
        Competition competition = newCompetition().build();
        Project competitionFinanceProject = newProject().withId(project.getId()).withApplication(newApplication().withCompetition(competition).build()).build();
        ProjectOrganisationCompositeId projectOrganisationCompositeId = new ProjectOrganisationCompositeId(project.getId(), organisationId);

        when(projectRepository.findById(competitionFinanceProject.getId())).thenReturn(Optional.of(competitionFinanceProject));
        when(externalFinanceRepository.existsByCompetitionIdAndUserId(competition.getId(), userResource.getId())).thenReturn(true);

        assertTrue(rules.competitionFinanceUserCanViewEligibility(projectOrganisationCompositeId, userResource));
        assertFalse(rules.competitionFinanceUserCanViewEligibility(projectOrganisationCompositeId, userResourceNotInCompetition));
    }

    @Test
    public void competitionFinanceUserCanSaveEligibility() {

        UserResource userResource = newUserResource().withRoleGlobal(EXTERNAL_FINANCE).build();
        UserResource userResourceNotInCompetition = newUserResource().withRoleGlobal(EXTERNAL_FINANCE).build();
        Competition competition = newCompetition().build();
        Project competitionFinanceProject = newProject().withId(project.getId()).withApplication(newApplication().withCompetition(competition).build()).build();
        ProjectOrganisationCompositeId projectOrganisationCompositeId = new ProjectOrganisationCompositeId(project.getId(), organisationId);

        when(projectRepository.findById(competitionFinanceProject.getId())).thenReturn(Optional.of(competitionFinanceProject));
        when(externalFinanceRepository.existsByCompetitionIdAndUserId(competition.getId(), userResource.getId())).thenReturn(true);
        when(projectProcessRepository.findOneByTargetId(competitionFinanceProject.getId())).thenReturn(projectProcess);

        assertTrue(rules.competitionFinanceUserCanSaveEligibility(projectOrganisationCompositeId, userResource));
        assertFalse(rules.competitionFinanceUserCanSaveEligibility(projectOrganisationCompositeId, userResourceNotInCompetition));
    }

    @Test
    public void projectFinanceUserCanSaveEligibility() {

        ProjectOrganisationCompositeId projectOrganisationCompositeId = new ProjectOrganisationCompositeId(project.getId(), organisationId);

        when(projectProcessRepository.findOneByTargetId(project.getId())).thenReturn(projectProcess);

        allGlobalRoleUsers.forEach(user -> {
            if (user.hasAuthority(Authority.PROJECT_FINANCE)) {
                assertTrue(rules.projectFinanceUserCanSaveEligibility(projectOrganisationCompositeId, user));
            } else {
                assertFalse(rules.projectFinanceUserCanSaveEligibility(projectOrganisationCompositeId, user));
            }
        });
    }

    @Test
    public void competitionFinanceUserCanSaveFundingRules() {

        UserResource userResource = newUserResource().withRoleGlobal(EXTERNAL_FINANCE).build();
        UserResource userResourceNotInCompetition = newUserResource().withRoleGlobal(EXTERNAL_FINANCE).build();
        Competition competition = newCompetition().build();
        Project competitionFinanceProject = newProject().withId(project.getId()).withApplication(newApplication().withCompetition(competition).build()).build();
        ProjectOrganisationCompositeId projectOrganisationCompositeId = new ProjectOrganisationCompositeId(project.getId(), organisationId);

        when(projectRepository.findById(competitionFinanceProject.getId())).thenReturn(Optional.of(competitionFinanceProject));
        when(externalFinanceRepository.existsByCompetitionIdAndUserId(competition.getId(), userResource.getId())).thenReturn(true);
        when(projectProcessRepository.findOneByTargetId(competitionFinanceProject.getId())).thenReturn(projectProcess);

        assertTrue(rules.competitionFinanceUserCanSaveFundingRules(projectOrganisationCompositeId, userResource));
        assertFalse(rules.competitionFinanceUserCanSaveFundingRules(projectOrganisationCompositeId, userResourceNotInCompetition));
    }

    @Test
    public void projectFinanceUserCanSaveFundingRules() {

        ProjectOrganisationCompositeId projectOrganisationCompositeId = new ProjectOrganisationCompositeId(project.getId(), organisationId);

        when(projectProcessRepository.findOneByTargetId(project.getId())).thenReturn(projectProcess);

        allGlobalRoleUsers.forEach(user -> {
            if (user.hasAuthority(Authority.PROJECT_FINANCE)) {
                assertTrue(rules.projectFinanceUserCanSaveFundingRules(projectOrganisationCompositeId, user));
            } else {
                assertFalse(rules.projectFinanceUserCanSaveFundingRules(projectOrganisationCompositeId, user));
            }
        });
    }

    @Test
    public void projectFinanceUserCanSaveCreditReport() {

        ProjectCompositeId projectId = ProjectCompositeId.id(1L);

        when(projectProcessRepository.findOneByTargetId(project.getId())).thenReturn(projectProcess);

        allGlobalRoleUsers.forEach(user -> {
            if (user.hasAuthority(Authority.PROJECT_FINANCE)) {
                assertTrue(rules.projectFinanceUserCanSaveCreditReport(projectId, user));
            } else {
                assertFalse(rules.projectFinanceUserCanSaveCreditReport(projectId, user));
            }
        });
    }

    @Test
    public void externalFinanceCanSaveCreditReport() {

        Competition competition = newCompetition().build();
        Project competitionFinanceProject = newProject().withId(project.getId()).withApplication(newApplication().withCompetition(competition).build()).build();
        ProjectCompositeId projectId = ProjectCompositeId.id(1L);
        UserResource userResource = newUserResource().withRoleGlobal(EXTERNAL_FINANCE).build();
        UserResource userResourceNotInCompetition = newUserResource().withRoleGlobal(EXTERNAL_FINANCE).build();

        when(projectRepository.findById(projectId.id())).thenReturn(Optional.of(competitionFinanceProject));
        when(externalFinanceRepository.existsByCompetitionIdAndUserId(competition.getId(), userResource.getId())).thenReturn(true);
        when(projectProcessRepository.findOneByTargetId(competitionFinanceProject.getId())).thenReturn(projectProcess);

        assertTrue(rules.competitionFinanceUserCanSaveCreditReport(projectId, userResource));
        assertFalse(rules.competitionFinanceUserCanSaveCreditReport(projectId, userResourceNotInCompetition));
    }

    @Test
    public void competitionFinanceCanViewCreditReport() {

        Competition competition = newCompetition().build();
        Project competitionFinanceProject = newProject().withId(project.getId()).withApplication(newApplication().withCompetition(competition).build()).build();
        ProjectCompositeId projectId = ProjectCompositeId.id(1L);
        UserResource userResource = newUserResource().withRoleGlobal(EXTERNAL_FINANCE).build();
        UserResource userResourceNotInCompetition = newUserResource().withRoleGlobal(EXTERNAL_FINANCE).build();

        when(projectRepository.findById(projectId.id())).thenReturn(Optional.of(competitionFinanceProject));
        when(externalFinanceRepository.existsByCompetitionIdAndUserId(competition.getId(), userResource.getId())).thenReturn(true);

        assertTrue(rules.competitionFinanceUserCanViewCreditReport(projectId, userResource));
        assertFalse(rules.competitionFinanceUserCanViewCreditReport(projectId, userResourceNotInCompetition));
    }

    @Test
    public void projectFinanceUserCanViewCreditReport() {

        ProjectCompositeId projectId = ProjectCompositeId.id(1L);

        allGlobalRoleUsers.forEach(user -> {
            if (user.hasAuthority(Authority.PROJECT_FINANCE)) {
                assertTrue(rules.projectFinanceUserCanViewCreditReport(projectId, user));
            } else {
                assertFalse(rules.projectFinanceUserCanViewCreditReport(projectId, user));
            }
        });
    }

    @Test
    public void auditorUserCanViewCreditReport() {

        ProjectCompositeId projectId = ProjectCompositeId.id(1L);

        allGlobalRoleUsers.forEach(user -> {
            if (user.hasAuthority(Authority.AUDITOR)) {
                assertTrue(rules.auditorUserCanViewCreditReport(projectId, user));
            } else {
                assertFalse(rules.auditorUserCanViewCreditReport(projectId, user));
            }
        });
    }

    @Test
    public void internalUserCanViewFinanceChecks() {
        ProjectCompositeId projectId = ProjectCompositeId.id(1L);
        allInternalUsers.forEach(user -> assertTrue(rules.internalUsersCanSeeTheProjectFinanceOverviewsForAllProjects(projectId, user)));
    }

    @Test
    public void auditorUserCanViewFinanceChecks() {
        ProjectCompositeId projectId = ProjectCompositeId.id(1L);

        allGlobalRoleUsers.forEach(user -> {
            if (user.hasAuthority(Authority.AUDITOR)) {
                assertTrue(rules.auditorUsersCanSeeTheProjectFinanceOverviewsForAllProjects(projectId, user));
            } else {
                assertFalse(rules.auditorUsersCanSeeTheProjectFinanceOverviewsForAllProjects(projectId, user));
            }
        });
    }

    @Test
    public void competitionFinanceUserCanViewFinanceChecks() {
        ProjectCompositeId projectId = ProjectCompositeId.id(1L);
        Competition competition = newCompetition().build();
        Project competitionFinanceProject = newProject().withId(project.getId()).withApplication(newApplication().withCompetition(competition).build()).build();
        UserResource userResource = newUserResource().withRoleGlobal(EXTERNAL_FINANCE).build();
        UserResource userResourceNotInCompetition = newUserResource().withRoleGlobal(EXTERNAL_FINANCE).build();

        when(projectRepository.findById(projectId.id())).thenReturn(Optional.of(competitionFinanceProject));
        when(externalFinanceRepository.existsByCompetitionIdAndUserId(competition.getId(), userResource.getId())).thenReturn(true);

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
        List<Role> financeContact = singletonList(Role.APPLICANT);
        user.setRoles(financeContact);

        assertTrue(rules.partnersCanSeeTheProjectFinanceOverviewsForTheirProject(ProjectCompositeId.id(project.getId()), user));
    }

    private void setupFinanceContactExpectations(ProjectResource project, UserResource user) {
        List<ProjectUser> partnerProjectUser = newProjectUser().build(1);

        when(projectUserRepository.findByProjectIdAndUserIdAndRoleIsIn(project.getId(), user.getId(), new ArrayList<>(PROJECT_USER_ROLES))).thenReturn(partnerProjectUser);

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
    public void externalFinanceUserCanSeeProjectFinancesForOrganisations() {

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
            if (hasProjectFinanceAuthority(user)) {
                assertTrue(rules.internalUsersCanUpdateProjectFinance(projectFinanceResource, user));
            } else {
                assertFalse(rules.internalUsersCanUpdateProjectFinance(projectFinanceResource, user));
            }
        });
    }

    @Test
    public void projectFinanceUsersCanResetFinanceChecks() {
        ProjectCompositeId projectId = ProjectCompositeId.id(1L);

        when(projectProcessRepository.findOneByTargetId(project.getId())).thenReturn(projectProcess);

        allGlobalRoleUsers.forEach(user -> {
            if (hasProjectFinanceAuthority(user)) {
                assertTrue(rules.projectFinanceUserCanResetFinanceChecks(projectId, user));
            } else {
                assertFalse(rules.projectFinanceUserCanResetFinanceChecks(projectId, user));
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
    public void auditorUserCanSeeTheProjectFinancesForTheirOrganisation() {
        FinanceCheckEligibilityResource financeCheckEligibilityResource = newFinanceCheckEligibilityResource().withProjectId(project.getId()).build();

        allGlobalRoleUsers.forEach(user -> {
            if (isAuditor(user)) {
                assertTrue(rules.auditorCanSeeTheProjectFinancesForTheirOrganisation(financeCheckEligibilityResource, user));
            } else {
                assertFalse(rules.auditorCanSeeTheProjectFinancesForTheirOrganisation(financeCheckEligibilityResource, user));
            }
        });
    }

    @Test
    public void projectFinanceUserCanSaveMilestoneCheck() {
        ProjectOrganisationCompositeId projectOrganisationCompositeId =
                new ProjectOrganisationCompositeId(project.getId(), newOrganisation().build().getId());

        when(projectProcessRepository.findOneByTargetId(project.getId())).thenReturn(projectProcess);

        allGlobalRoleUsers.forEach(user -> {
            if (hasProjectFinanceAuthority(user)) {
                assertTrue(rules.projectFinanceUserCanSaveMilestoneCheck(projectOrganisationCompositeId, user));
            } else {
                assertFalse(rules.projectFinanceUserCanSaveMilestoneCheck(projectOrganisationCompositeId, user));
            }
        });
    }

    @Test
    public void projectFinanceUserCannotSaveMilestoneCheck() {
        ProjectOrganisationCompositeId projectOrganisationCompositeId =
                new ProjectOrganisationCompositeId(project.getId(), newOrganisation().build().getId());

        when(projectProcessRepository.findOneByTargetId(project.getId())).thenReturn(projectProcess);

        projectProcess.setProcessState(WITHDRAWN);

        allGlobalRoleUsers.forEach(user -> assertFalse(rules.projectFinanceUserCanSaveMilestoneCheck(projectOrganisationCompositeId, user)));
    }

    @Test
    public void projectFinanceUserCanResetMilestoneCheck() {
        ProjectOrganisationCompositeId projectOrganisationCompositeId =
                new ProjectOrganisationCompositeId(project.getId(), newOrganisation().build().getId());

        when(projectProcessRepository.findOneByTargetId(project.getId())).thenReturn(projectProcess);

        allGlobalRoleUsers.forEach(user -> {
            if (hasProjectFinanceAuthority(user)) {
                assertTrue(rules.projectFinanceUserCanResetMilestoneCheck(projectOrganisationCompositeId, user));
            } else {
                assertFalse(rules.projectFinanceUserCanResetMilestoneCheck(projectOrganisationCompositeId, user));
            }
        });
    }

    @Test
    public void projectFinanceUserCannotResetMilestoneCheck() {
        ProjectOrganisationCompositeId projectOrganisationCompositeId =
                new ProjectOrganisationCompositeId(project.getId(), newOrganisation().build().getId());

        when(projectProcessRepository.findOneByTargetId(project.getId())).thenReturn(projectProcess);

        projectProcess.setProcessState(WITHDRAWN);

        allGlobalRoleUsers.forEach(user -> assertFalse(rules.projectFinanceUserCanResetMilestoneCheck(projectOrganisationCompositeId, user)));
    }

    @Test
    public void projectFinanceUserCanViewMilestoneCheck() {
        ProjectOrganisationCompositeId projectOrganisationCompositeId =
                new ProjectOrganisationCompositeId(project.getId(), newOrganisation().build().getId());

        allGlobalRoleUsers.forEach(user -> {
            if (hasProjectFinanceAuthority(user)) {
                assertTrue(rules.projectFinanceUserCanViewMilestoneCheck(projectOrganisationCompositeId, user));
            } else {
                assertFalse(rules.projectFinanceUserCanViewMilestoneCheck(projectOrganisationCompositeId, user));
            }
        });
    }

    @Test
    public void auditorUserCanViewMilestoneCheck() {
        ProjectOrganisationCompositeId projectOrganisationCompositeId =
                new ProjectOrganisationCompositeId(project.getId(), newOrganisation().build().getId());

        allGlobalRoleUsers.forEach(user -> {
            if (hasAuditorAuthority(user)) {
                assertTrue(rules.auditorUserCanViewMilestoneCheck(projectOrganisationCompositeId, user));
            } else {
                assertFalse(rules.auditorUserCanViewMilestoneCheck(projectOrganisationCompositeId, user));
            }
        });
    }

    @Test
    public void userCanViewTheirOwnMilestoneStatus() {
        UserResource user = newUserResource().build();

        ProjectOrganisationCompositeId projectOrganisationCompositeId =
                new ProjectOrganisationCompositeId(project.getId(), newOrganisation().build().getId());

        setupUserAsPartner(project, user);
        assertTrue(rules.userCanViewTheirOwnMilestoneStatus(projectOrganisationCompositeId, user));

        setupUserNotAsPartner(project, user);
        assertFalse(rules.userCanViewTheirOwnMilestoneStatus(projectOrganisationCompositeId, user));
    }

    @Test
    public void competitionFinanceUsersCanSeeTheProjectFinancesForTheirOrganisation() {

        UserResource user = newUserResource().withRoleGlobal(EXTERNAL_FINANCE).build();
        UserResource userNotInCompetition = newUserResource().withRoleGlobal(EXTERNAL_FINANCE).build();
        FinanceCheckEligibilityResource financeCheckEligibilityResource = newFinanceCheckEligibilityResource().withProjectId(project.getId()).build();
        Competition competition = newCompetition().build();
        Project competitionFinanceProject = newProject().withId(project.getId()).withApplication(newApplication().withCompetition(competition).build()).build();

        when(projectRepository.findById(competitionFinanceProject.getId())).thenReturn(Optional.of(competitionFinanceProject));
        when(externalFinanceRepository.existsByCompetitionIdAndUserId(competition.getId(), user.getId())).thenReturn(true);

        setupUserAsPartner(project, user);
        assertTrue(rules.competitionFinanceUsersCanSeeTheProjectFinancesForTheirOrganisation(financeCheckEligibilityResource, user));
        assertFalse(rules.competitionFinanceUsersCanSeeTheProjectFinancesForTheirOrganisation(financeCheckEligibilityResource, userNotInCompetition));
    }

    @Test
    public void moUserCanSeeTheProjectFinancesForTheOrganisationsBelongsToTheirProject() {

        UserResource user = newUserResource()
                .withRoleGlobal(MONITORING_OFFICER)
                .build();
        MonitoringOfficer mo = newMonitoringOfficer()
                .withUser(newUser()
                        .withId(user.getId())
                        .withRoles(asSet(MONITORING_OFFICER))
                        .build())
                .build();
        UserResource userNotInCompetition = newUserResource()
                .withRoleGlobal(MONITORING_OFFICER)
                .build();
        MonitoringOfficer moNotInCompetition  = newMonitoringOfficer()
                .withUser(newUser()
                        .withId(userNotInCompetition.getId())
                        .withRoles(asSet(MONITORING_OFFICER))
                        .build())
                .build();
        FinanceCheckEligibilityResource financeCheckEligibilityResource = newFinanceCheckEligibilityResource()
                .withProjectId(project.getId())
                .build();
        Competition competition = newCompetition().build();
        Project competitionFinanceProject = newProject()
                .withId(project.getId())
                .withProjectMonitoringOfficer(mo)
                .withApplication(newApplication()
                        .withCompetition(competition)
                        .build())
                .build();

        when(projectRepository.findById(competitionFinanceProject.getId())).thenReturn(Optional.of(competitionFinanceProject));
        when(monitoringOfficerRepository.existsByProjectIdAndUserId(project.getId(), mo.getUser().getId())).thenReturn(true);
        when(monitoringOfficerRepository.existsByProjectIdAndUserId(project.getId(), moNotInCompetition.getUser().getId())).thenReturn(false);

        assertTrue(rules.moUserCanSeeTheProjectFinancesForTheOrganisationsBelongsToTheirProject(financeCheckEligibilityResource, user));
        assertFalse(rules.moUserCanSeeTheProjectFinancesForTheOrganisationsBelongsToTheirProject(financeCheckEligibilityResource, userNotInCompetition));
    }

    @Test
    public void auditorCanSeeProjectFinancesForOrganisations() {
        UserResource user = newUserResource().withRoleGlobal(AUDITOR).build();
        ProjectFinanceResource projectFinanceResource = newProjectFinanceResource().withProject(project.getId()).build();
        assertTrue(rules.stakeholderUserCanSeeProjectFinancesForOrganisations(projectFinanceResource, user));
    }

    @Test
    public void projectMoCanViewMilestoneCheck() {
        Long organisationId = 1L;
        UserResource userResource = newUserResource().withRoleGlobal(MONITORING_OFFICER).build();
        UserResource userResourceNotInProject = newUserResource().withRoleGlobal(MONITORING_OFFICER).build();
        ProjectOrganisationCompositeId projectOrganisationCompositeId = new ProjectOrganisationCompositeId(project.getId(), organisationId);

        when(monitoringOfficerRepository.existsByProjectIdAndUserId(project.getId(), userResource.getId())).thenReturn(true);

        assertTrue(rules.projectMoCanViewMilestoneCheck(projectOrganisationCompositeId, userResource));
        assertFalse(rules.projectMoCanViewMilestoneCheck(projectOrganisationCompositeId, userResourceNotInProject));
    }

    @Test
    public void auditorCanViewFundingRules() {

        long organisationId = 1L;

        ProjectOrganisationCompositeId projectOrganisationCompositeId = new ProjectOrganisationCompositeId(project.getId(), organisationId);

        allGlobalRoleUsers.forEach(user -> {
            if (user.hasAuthority(Authority.AUDITOR)) {
                assertTrue(rules.auditorCanViewFundingRules(projectOrganisationCompositeId, user));
            }
        });
    }

    @Override
    protected ProjectFinancePermissionRules supplyPermissionRulesUnderTest() {
        return new ProjectFinancePermissionRules();
    }
}