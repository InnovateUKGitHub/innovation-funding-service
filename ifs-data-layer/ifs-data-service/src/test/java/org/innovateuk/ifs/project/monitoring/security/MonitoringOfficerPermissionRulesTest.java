package org.innovateuk.ifs.project.monitoring.security;

import org.innovateuk.ifs.BasePermissionRulesTest;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Test;

import static java.util.Collections.*;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static org.innovateuk.ifs.project.core.domain.ProjectParticipantRole.PROJECT_PARTNER;
import static org.innovateuk.ifs.user.builder.UserBuilder.newUser;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.user.resource.Role.*;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class MonitoringOfficerPermissionRulesTest extends BasePermissionRulesTest<MonitoringOfficerPermissionRules> {

    @Override
    protected MonitoringOfficerPermissionRules supplyPermissionRulesUnderTest() {
        return new MonitoringOfficerPermissionRules();
    }

    @Test
    public void monitoringOfficerCanSeeTheirOwnProjects() {
        UserResource monitoringOfficer = newUserResource().withRoleGlobal(MONITORING_OFFICER).build();
        UserResource otherMonitoringOfficer = newUserResource().withRoleGlobal(APPLICANT).build();
        UserResource nonMonitoringOfficer = newUserResource().withRoleGlobal(APPLICANT).build();

        assertTrue(rules.monitoringOfficerCanSeeTheirOwnProjects(monitoringOfficer, monitoringOfficer));

        assertFalse(rules.monitoringOfficerCanSeeTheirOwnProjects(monitoringOfficer, otherMonitoringOfficer));
        assertFalse(rules.monitoringOfficerCanSeeTheirOwnProjects(monitoringOfficer, nonMonitoringOfficer));
    }


    @Test
    public void internalUsersCanSeeMonitoringOfficerProjects() {
        UserResource monitoringOfficer = newUserResource().withRoleGlobal(APPLICANT).build();
        UserResource internal = newUserResource().withRoleGlobal(COMP_ADMIN).build();
        UserResource nonInternal = newUserResource().withRoleGlobal(APPLICANT).build();

        assertTrue(rules.internalUsersCanSeeMonitoringOfficerProjects(monitoringOfficer, internal));

        assertFalse(rules.internalUsersCanSeeMonitoringOfficerProjects(monitoringOfficer, nonInternal));
    }

    @Test
    public void internalUsersCanViewMonitoringOfficersOnProjects() {

        ProjectResource project = newProjectResource().build();

        allGlobalRoleUsers.forEach(user -> {
            if (allInternalUsers.contains(user)) {
                assertTrue(rules.internalUsersCanViewMonitoringOfficersForAnyProject(project, user));
            } else {
                assertFalse(rules.internalUsersCanViewMonitoringOfficersForAnyProject(project, user));
            }
        });
    }

    @Test
    public void stakeholdersCanViewMonitoringOfficersForAProjectOnTheirCompetitions() {

        User stakeholderUserOnCompetition = newUser().withRoles(singleton(STAKEHOLDER)).build();
        UserResource stakeholderUserResourceOnCompetition = newUserResource().withId(stakeholderUserOnCompetition.getId()).withRolesGlobal(singletonList(STAKEHOLDER)).build();
        Competition competition = newCompetition().build();

        ProjectResource project = newProjectResource()
                .withCompetition(competition.getId())
                .build();

        when(stakeholderRepository.existsByCompetitionIdAndUserId(competition.getId(), stakeholderUserResourceOnCompetition.getId())).thenReturn(true);

        assertTrue(rules.stakeholdersCanViewMonitoringOfficersForAProjectOnTheirCompetitions(project, stakeholderUserResourceOnCompetition));

        allInternalUsers.forEach(user -> assertFalse(rules.stakeholdersCanViewMonitoringOfficersForAProjectOnTheirCompetitions(newProjectResource().build(), user)));
    }

    @Test
    public void partnersCanViewMonitoringOfficersOnTheirOwnProjects() {

        UserResource user = newUserResource().build();
        ProjectResource project = newProjectResource().build();

        setupUserAsPartner(project, user);

        assertTrue(rules.partnersCanViewMonitoringOfficersOnTheirProjects(project, user));
    }

    @Test
    public void monitoringOfficersCanViewThemselves() {

        UserResource user = newUserResource().build();
        ProjectResource project = newProjectResource().build();

        setupUserAsMonitoringOfficer(project, user);

        when(projectMonitoringOfficerRepository.existsByProjectIdAndUserId(project.getId(), user.getId())).thenReturn(true);

        assertTrue(rules.monitoringOfficersCanViewThemselves(project, user));
    }

    @Test
    public void partnersCanViewMonitoringOfficersOnTheirOwnProjectsButUserNotPartner() {

        UserResource user = newUserResource().build();

        ProjectResource project = newProjectResource().build();

        when(projectUserRepository.findByProjectIdAndUserIdAndRole(project.getId(), user.getId(), PROJECT_PARTNER)).thenReturn(emptyList());

        assertFalse(rules.partnersCanViewMonitoringOfficersOnTheirProjects(project, user));
    }
}
