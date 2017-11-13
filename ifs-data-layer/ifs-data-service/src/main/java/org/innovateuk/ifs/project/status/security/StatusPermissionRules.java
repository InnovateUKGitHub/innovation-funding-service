package org.innovateuk.ifs.project.status.security;

import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.commons.security.PermissionRule;
import org.innovateuk.ifs.commons.security.PermissionRules;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.security.BasePermissionRules;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.stereotype.Component;

import static org.innovateuk.ifs.util.SecurityRuleUtil.*;

/**
 * Permissions for access to Status section (Setup Status, Team Status and Competition Status)
 */
@PermissionRules
@Component
public class StatusPermissionRules extends BasePermissionRules {

    @PermissionRule(
            value = "VIEW_TEAM_STATUS",
            description = "All partners can view team status")
    public boolean partnersCanViewTeamStatus(ProjectResource project, UserResource user) {
        return isPartner(project.getId(), user.getId());
    }

    @PermissionRule(
            value = "VIEW_TEAM_STATUS",
            description = "Internal users can see a team's status")
    public boolean internalUsersCanViewTeamStatus(ProjectResource project, UserResource user) {
        return isInternal(user);
    }

    @PermissionRule(
            value = "VIEW_STATUS",
            description = "All partners can view the project status")
    public boolean partnersCanViewStatus(ProjectResource project, UserResource user) {
        return isPartner(project.getId(), user.getId());
    }

    @PermissionRule(
            value = "VIEW_STATUS",
            description = "Internal users can see the project status")
    public boolean internalUsersCanViewStatus(ProjectResource project, UserResource user) {
        return isInternal(user);
    }

    @PermissionRule(value = "VIEW_PROJECT_SETUP_COMPETITION_STATUS", description = "Internal admin team (comp admin and project finance) users should be able to access the current status of the competition")
    public boolean internalAdminTeamCanViewCompetitionStatus(CompetitionResource competition, UserResource user){
        return isInternalAdmin(user);
    }

    @PermissionRule(value = "VIEW_PROJECT_SETUP_COMPETITION_STATUS", description = "Support users should be able to access the current status of the competition")
    public boolean supportCanViewCompetitionStatus(CompetitionResource competition, UserResource user){
        return isSupport(user);
    }

    @PermissionRule(value = "VIEW_PROJECT_SETUP_COMPETITION_STATUS", description = "Innovation lead users should be able to access the current status of the competition that are assigned to them")
    public boolean assignedInnovationLeadCanViewCompetitionStatus(CompetitionResource competition, UserResource user){
        return userIsInnovationLeadOnCompetition(competition.getId(), user.getId());
    }

    @PermissionRule(value = "VIEW_PROJECT_STATUS", description = "Internal admin team (comp admin and project finance) users should be able to access the current status of project")
    public boolean internalAdminTeamCanViewProjectStatus(ProjectResource project, UserResource user){
        return isInternalAdmin(user);
    }

    @PermissionRule(value = "VIEW_PROJECT_STATUS", description = "Support users should be able to view current status of project")
    public boolean supportCanViewProjectStatus(ProjectResource project, UserResource user){
        return isSupport(user);
    }

    @PermissionRule(value = "VIEW_PROJECT_STATUS", description = "Innovation lead users should be able to view current status of project from competition assigned to them")
    public boolean assignedInnovationLeadCanViewProjectStatus(ProjectResource project, UserResource user){
        Application application = applicationRepository.findOne(project.getApplication());
        return userIsInnovationLeadOnCompetition(application.getCompetition().getId(), user.getId());
    }
}
