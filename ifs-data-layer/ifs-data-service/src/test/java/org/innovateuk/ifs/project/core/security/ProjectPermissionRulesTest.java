package org.innovateuk.ifs.project.core.security;

import org.innovateuk.ifs.BasePermissionRulesTest;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.project.core.domain.ProjectProcess;
import org.innovateuk.ifs.project.core.repository.ProjectProcessRepository;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.resource.ProjectState;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Test;
import org.mockito.Mock;

import static java.util.Collections.*;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static org.innovateuk.ifs.project.core.builder.ProjectProcessBuilder.newProjectProcess;
import static org.innovateuk.ifs.project.core.domain.ProjectParticipantRole.PROJECT_PARTNER;
import static org.innovateuk.ifs.user.builder.UserBuilder.newUser;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.user.resource.Role.EXTERNAL_FINANCE;
import static org.innovateuk.ifs.user.resource.Role.STAKEHOLDER;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class ProjectPermissionRulesTest extends BasePermissionRulesTest<ProjectPermissionRules> {

    @Mock
    private ProjectProcessRepository projectProcessRepository;

    @Override
    protected ProjectPermissionRules supplyPermissionRulesUnderTest() {
        return new ProjectPermissionRules();
    }

    @Test
    public void partnersOnProjectCanView() {

        UserResource user = newUserResource().build();

        ProjectResource project = newProjectResource().build();
        setupUserAsPartner(project, user);

        assertTrue(rules.partnersOnProjectCanView(project, user));
    }

    @Test
    public void partnersOnProjectCanViewButUserNotPartner() {

        UserResource user = newUserResource().build();

        ProjectResource project = newProjectResource().build();

        when(projectUserRepository.findByProjectIdAndUserIdAndRole(project.getId(), user.getId(), PROJECT_PARTNER)).thenReturn(emptyList());

        assertFalse(rules.partnersOnProjectCanView(project, user));
    }

    @Test
    public void internalUsersCanViewProjects() {

        ProjectResource project = newProjectResource().build();

        allGlobalRoleUsers.forEach(user -> {
            if (allInternalUsers.contains(user)) {
                assertTrue(rules.internalUsersCanViewProjects(project, user));
            } else {
                assertFalse(rules.internalUsersCanViewProjects(project, user));
            }
        });
    }

    @Test
    public void stakeholdersCanViewProjects() {

        User stakeholderUserOnCompetition = newUser().withRoles(singleton(STAKEHOLDER)).build();
        UserResource stakeholderUserResourceOnCompetition = newUserResource().withId(stakeholderUserOnCompetition.getId()).withRolesGlobal(singletonList(STAKEHOLDER)).build();
        Competition competition = newCompetition().build();

        ProjectResource project = newProjectResource()
                .withCompetition(competition.getId())
                .build();

        when(stakeholderRepository.existsByCompetitionIdAndUserId(competition.getId(), stakeholderUserResourceOnCompetition.getId())).thenReturn(true);

        assertTrue(rules.stakeholdersCanViewProjects(project, stakeholderUserResourceOnCompetition));

        allInternalUsers.forEach(user -> assertFalse(rules.stakeholdersCanViewProjects(newProjectResource().build(), user)));
    }

    @Test
    public void competitionFinanceUsersCanViewProjects() {

        User competitionFinanceUserOnCompetition = newUser().withRoles(singleton(EXTERNAL_FINANCE)).build();
        UserResource competitionFinanceUserResourceOnCompetition = newUserResource().withId(competitionFinanceUserOnCompetition.getId()).withRolesGlobal(singletonList(STAKEHOLDER)).build();
        Competition competition = newCompetition().build();

        ProjectResource project = newProjectResource()
                .withCompetition(competition.getId())
                .build();

        when(externalFinanceRepository.existsByCompetitionIdAndUserId(competition.getId(), competitionFinanceUserOnCompetition.getId())).thenReturn(true);

        assertTrue(rules.competitionFinanceUsersCanViewProjects(project, competitionFinanceUserResourceOnCompetition));

        allInternalUsers.forEach(user -> assertFalse(rules.competitionFinanceUsersCanViewProjects(newProjectResource().build(), user)));
    }

    @Test
    public void monitoringOfficerOnProjectCanView() {

        UserResource user = newUserResource().build();

        ProjectResource project = newProjectResource().build();
        setupUserAsMonitoringOfficer(project, user);

        assertTrue(rules.monitoringOfficerOnProjectCanView(project, user));
    }

    @Test
    public void monitoringOfficerOnProjectCanViewWhenNotMonitoringOfficer() {

        UserResource user = newUserResource().build();

        ProjectResource project = newProjectResource().build();
        setupUserNotAsMonitoringOfficer(project, user);

        assertFalse(rules.monitoringOfficerOnProjectCanView(project, user));
    }

    @Test
    public void systemRegistrarCanAddPartnersToProject() {

        ProjectResource project = newProjectResource().build();
        ProjectProcess projectProcess = newProjectProcess().withActivityState(ProjectState.SETUP).build();

        when(projectProcessRepository.findOneByTargetId(project.getId())).thenReturn(projectProcess);

        allGlobalRoleUsers.forEach(user -> {
            if (systemRegistrationUser().equals(user)) {
                assertTrue(rules.systemRegistrarCanAddPartnersToProject(project, user));
            } else {
                assertFalse(rules.systemRegistrarCanAddPartnersToProject(project, user));
            }
        });
    }

    @Test
    public void projectFinanceCanViewFinanceReviewer() {

        UserResource user = newUserResource().build();

        ProjectResource project = newProjectResource().build();
        setUpUserAsProjectFinanceUser(project, user);

        assertTrue(rules.projectFinanceCanViewFinanceReviewer(project, user));
    }

    @Test
    public void nonProjectFinanceCannotViewFinanceReviewer() {

        UserResource user = newUserResource().build();

        ProjectResource project = newProjectResource().build();
        setUpUserNotAsProjectFinanceUser(project, user);

        assertFalse(rules.projectFinanceCanViewFinanceReviewer(project, user));
    }

    @Test
    public void compAdminCanViewFinanceReviewer() {

        UserResource user = newUserResource().build();

        ProjectResource project = newProjectResource().build();
        setUpUserAsCompAdmin(project, user);

        assertTrue(rules.compAdminCanViewFinanceReviewer(project, user));
    }

    @Test
    public void nonCompAdminCannotViewFinanceReviewer() {

        UserResource user = newUserResource().build();

        ProjectResource project = newProjectResource().build();
        setUpUserNotAsCompAdmin(project, user);

        assertFalse(rules.compAdminCanViewFinanceReviewer(project, user));
    }

    @Test
    public void supportCanViewFinanceReviewer() {

        UserResource user = newUserResource().build();

        ProjectResource project = newProjectResource().build();
        setUpUserAsSupport(project, user);

        assertTrue(rules.supportCanViewFinanceReviewer(project, user));
    }

    @Test
    public void nonSupportCannotViewFinanceReviewer() {

        UserResource user = newUserResource().build();

        ProjectResource project = newProjectResource().build();
        setUpUserNotAsSupport(project, user);

        assertFalse(rules.supportCanViewFinanceReviewer(project, user));
    }
}