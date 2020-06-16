package org.innovateuk.ifs.competition.security;

import org.innovateuk.ifs.commons.security.PermissionRule;
import org.innovateuk.ifs.commons.security.PermissionRules;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.search.CompetitionSearchResultItem;
import org.innovateuk.ifs.project.monitoring.repository.MonitoringOfficerRepository;
import org.innovateuk.ifs.security.BasePermissionRules;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.EnumSet;

import static org.innovateuk.ifs.competition.resource.CompetitionStatus.COMPETITION_SETUP;
import static org.innovateuk.ifs.competition.resource.CompetitionStatus.READY_TO_OPEN;
import static org.innovateuk.ifs.util.SecurityRuleUtil.*;

/**
 * Provides the permissions around CRUD for Competitions
 */
@Component
@PermissionRules
public class CompetitionPermissionRules extends BasePermissionRules {

    @Autowired
    private MonitoringOfficerRepository projectMonitoringOfficerRepository;

    @PermissionRule(value = "READ", description = "External users cannot view competitions in setup")
    public boolean externalUsersCannotViewCompetitionsInSetup(CompetitionResource competition, UserResource user) {
        return !COMPETITION_SETUP.equals(competition.getCompetitionStatus());
    }

    @PermissionRule(value = "READ", description = "Internal users other than innovation lead users and stakeholders can see all competitions")
    public boolean internalUserCanViewAllCompetitions(CompetitionResource competition, UserResource user) {
        return isInternal(user) && !isInnovationLead(user) && !isStakeholder(user);
    }

    @PermissionRule(value = "READ", description = "Innovation leads can view competitions that are assigned to them")
    public boolean innovationLeadCanViewCompetitionAssignedToThem(CompetitionResource competition, UserResource user) {
        return userIsInnovationLeadOnCompetition(competition.getId(), user.getId());
    }

    @PermissionRule(value = "READ", description = "Stakeholders can view competitions that are assigned to them")
    public boolean stakeholderCanViewCompetitionAssignedToThem(CompetitionResource competition, UserResource user) {
        return userIsStakeholderInCompetition(competition.getId(), user.getId());
    }

    @PermissionRule(value = "READ", description = "Competition finance users can view competitions that are assigned to them")
    public boolean compFinanceCanViewCompetitionAssignedToThem(CompetitionSearchResultItem competition, UserResource user) {
        return userIsExternalFinanceInCompetition(competition.getId(), user.getId());
    }

    @PermissionRule(value = "READ", description = "Monitoring officers can view competitions that are assigned to them")
    public boolean monitoringOfficersCanViewCompetitionAssignedToThem(CompetitionResource competition, UserResource user) {
        return projectMonitoringOfficerRepository.existsByProjectApplicationCompetitionIdAndUserId(competition.getId(), user.getId());
    }

    @PermissionRule(value = "READ", description = "Internal users other than innovation leads and stakeholders can see all competition search results")
    public boolean internalUserCanViewAllCompetitionSearchResults(CompetitionSearchResultItem competition, UserResource user) {
        return isInternal(user) && !isInnovationLead(user) && !isStakeholder(user);
    }

    @PermissionRule(value = "READ", description = "Innovation lead users can see competitions that are assigned to them")
    public boolean innovationLeadCanViewCompetitionAssignedToThem(CompetitionSearchResultItem competition, UserResource user) {
        return userIsInnovationLeadOnCompetition(competition.getId(), user.getId());
    }

    @PermissionRule(value = "READ", description = "Stakeholders can see competitions that are assigned to them")
    public boolean stakeholderCanViewCompetitionAssignedToThem(CompetitionSearchResultItem competition, UserResource user) {
        return userIsStakeholderInCompetition(competition.getId(), user.getId());
    }

    @PermissionRule(value = "MANAGE_INNOVATION_LEADS", description = "Competition Admin and Project Finance can add, remove and view innovation leads for a competition")
    public boolean internalAdminCanManageInnovationLeadsForCompetition(CompetitionResource competition, UserResource user) {
        return isInternalAdmin(user);
    }

    @PermissionRule(value = "VIEW_PREVIOUS_APPLICATIONS", description = "Internal users (barring innovation leads and stakeholders), and IFS Admin can view previous applications")
    public boolean internalUsersAndIFSAdminCanViewPreviousApplications(CompetitionResource competition, UserResource user) {
        return (isInternal(user) && !isInnovationLead(user) && !isStakeholder(user)) || isIFSAdmin(user);
    }
    @PermissionRule(value = "VIEW_PREVIOUS_APPLICATIONS", description = "Innovation leads for the competition can view previous applications")
    public boolean innovationLeadForCompetitionCanViewPreviousApplications(CompetitionResource competition, UserResource user) {
        return userIsInnovationLeadOnCompetition(competition.getId(), user.getId());
    }

    @PermissionRule(value = "VIEW_PREVIOUS_APPLICATIONS", description = "Stakeholders for the competition can view previous applications")
    public boolean stakeholderForCompetitionCanViewPreviousApplications(CompetitionResource competition, UserResource user) {
        return userIsStakeholderInCompetition(competition.getId(), user.getId());
    }

    @PermissionRule(value = "DELETE",
            description = "Comp admins are able to delete competitions in preparation prior to them being in the Open state",
            particularBusinessState = "Competition is in preparation")
    public boolean internalAdminAndIFSAdminCanDeleteCompetitionInPreparation(CompetitionResource competition, UserResource user) {
        return (isInternalAdmin(user) || isIFSAdmin(user)) &&
                EnumSet.of(COMPETITION_SETUP, READY_TO_OPEN).contains(competition.getCompetitionStatus());
    }

    @PermissionRule(value = "CHOOSE_POST_AWARD_SERVICE", description = "Competition Admin can set post award service on a competition")
    public boolean internalAdminCanSetPostAwardServiceForCompetition(CompetitionResource competition, UserResource user) {
        return isInternalAdmin(user);
    }

    @PermissionRule(value = "READ_POST_AWARD_SERVICE", description = "Competition Admin can read post award service on a competition")
    public boolean internalAdminCanReadPostAwardServiceForCompetition(CompetitionResource competition, UserResource user) {
        return isInternalAdmin(user);
    }

    @PermissionRule(value = "READ_POST_AWARD_SERVICE", description = "Allowed for users part of project on competition to read post award service during project setup")
    public boolean projectUsersCanReadPostAwardServiceForCompetition(CompetitionResource competition, UserResource user) {
        return projectUserRepository.existsByProjectApplicationCompetitionIdAndUserId(competition.getId(), user.getId());
    }
}