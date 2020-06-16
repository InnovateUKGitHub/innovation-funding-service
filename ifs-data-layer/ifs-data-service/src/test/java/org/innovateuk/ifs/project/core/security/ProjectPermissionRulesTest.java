package org.innovateuk.ifs.project.core.security;

import org.innovateuk.ifs.BasePermissionRulesTest;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.project.builder.ProjectResourceBuilder;
import org.innovateuk.ifs.project.builder.ProjectUserResourceBuilder;
import org.innovateuk.ifs.project.core.domain.ProjectParticipantRole;
import org.innovateuk.ifs.project.core.domain.ProjectProcess;
import org.innovateuk.ifs.project.core.domain.ProjectUser;
import org.innovateuk.ifs.project.core.repository.ProjectProcessRepository;
import org.innovateuk.ifs.project.resource.ProjectCompositeId;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.resource.ProjectState;
import org.innovateuk.ifs.project.resource.ProjectUserResource;
import org.innovateuk.ifs.user.builder.UserResourceBuilder;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.resource.UserCompositeId;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Test;
import org.mockito.Mock;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Collections.*;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static org.innovateuk.ifs.project.core.builder.ProjectProcessBuilder.newProjectProcess;
import static org.innovateuk.ifs.project.core.domain.ProjectParticipantRole.PROJECT_PARTNER;
import static org.innovateuk.ifs.project.core.domain.ProjectParticipantRole.PROJECT_USER_ROLES;
import static org.innovateuk.ifs.user.builder.UserBuilder.newUser;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.user.resource.Role.EXTERNAL_FINANCE;
import static org.innovateuk.ifs.user.resource.Role.STAKEHOLDER;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
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

    @Test
    public void projectUsersCanReadPostAwardServiceForCompetition() {

        UserResource user = newUserResource().withId(5L).build();
        ProjectResource project = newProjectResource().withProjectUsers(Collections.singletonList(5L)).build();

        List<ProjectUser> projectUsers = project.getProjectUsers().stream().map(pu -> {
            ProjectUser projectUser = new ProjectUser();
            projectUser.setUser(newUser().withId(pu).build());
            return projectUser;
        }).collect(Collectors.toList());

        List<ProjectParticipantRole> projectRoles = PROJECT_USER_ROLES.stream().collect(Collectors.toList());
        //ProjectUser projectUser = new ProjectUser();
        //projectUser.setUser(newUser().withId(user.getId()).build());
        when(projectUserRepository.findByProjectIdAndRoleIsIn(project.getId(), projectRoles)).thenReturn(projectUsers);

        assertTrue(rules.projectUsersCanReadPostAwardServiceForCompetition(project, user));
        verify(projectUserRepository).findByProjectIdAndRoleIsIn(project.getId(), projectRoles);
    }

    @Test
    public void nonProjectUsersCannotReadPostAwardServiceForCompetition() {

        UserResource user = newUserResource().withId(5L).build();
        ProjectResource project = newProjectResource().withProjectUsers(Collections.singletonList(15L)).build();

        List<ProjectUser> projectUsers = project.getProjectUsers().stream().map(pu -> {
            ProjectUser projectUser = new ProjectUser();
            projectUser.setUser(newUser().withId(pu).build());
            return projectUser;
        }).collect(Collectors.toList());

        List<ProjectParticipantRole> projectRoles = PROJECT_USER_ROLES.stream().collect(Collectors.toList());
        when(projectUserRepository.findByProjectIdAndRoleIsIn(project.getId(), projectRoles)).thenReturn(projectUsers);

        assertFalse(rules.projectUsersCanReadPostAwardServiceForCompetition(project, user));
        verify(projectUserRepository).findByProjectIdAndRoleIsIn(project.getId(), projectRoles);
    }
}