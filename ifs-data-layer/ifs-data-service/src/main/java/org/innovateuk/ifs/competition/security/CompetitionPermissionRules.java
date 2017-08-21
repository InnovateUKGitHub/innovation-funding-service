package org.innovateuk.ifs.competition.security;

import org.innovateuk.ifs.commons.security.PermissionRule;
import org.innovateuk.ifs.commons.security.PermissionRules;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSearchResultItem;
import org.innovateuk.ifs.competition.resource.CompetitionStatus;
import org.innovateuk.ifs.competition.transactional.CompetitionService;
import org.innovateuk.ifs.security.BasePermissionRules;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.innovateuk.ifs.security.SecurityRuleUtil.*;

/**
 * Provides the permissions around CRUD for Competitions
 */
@Component
@PermissionRules
public class CompetitionPermissionRules extends BasePermissionRules {

    @Autowired
    private CompetitionService competitionService;

    @PermissionRule(value = "READ", description = "External users cannot view competitions in setup")
    public boolean externalUsersCannotViewCompetitionsInSetup(CompetitionResource competition, UserResource user) {
        return !CompetitionStatus.COMPETITION_SETUP.equals(competition.getCompetitionStatus());
    }

    @PermissionRule(value = "READ", description = "Internal users can see all competitions")
    public boolean internalUserCanViewAllCompetitions(CompetitionResource competition, UserResource user) {
        return isInternal(user) && !isInnovationLead(user);
    }

    @PermissionRule(value = "READ", description = "Internal users can see all competition search results")
    public boolean internalUserCanViewAllCompetitionSearchResults(CompetitionSearchResultItem competition, UserResource user) {
        return isInternal(user) && !isInnovationLead(user);
    }

    @PermissionRule(value = "READ", description = "Innovation lead users can only see competitions they are assigned in search results")
    public boolean innovationLeadCanViewCompetitionAssignedToThemInSearchResults(CompetitionSearchResultItem competition, UserResource user) {
        return (isInternal(user) && !isInnovationLead(user)) || userIsInnovationLeadOnCompetition(competition.getId(), user.getId());
    }

    private boolean userIsInnovationLeadOnCompetition(long competitionId, long loggedInUserId){
        return competitionService.findInnovationLeads(competitionId).
                handleSuccessOrFailure(
                        failure -> false,
                        success -> success.stream().anyMatch(u -> u.getId().equals(loggedInUserId)));
    }

    @PermissionRule(value = "MANAGE_INNOVATION_LEADS", description = "Competition Admin and Project Finance can add, remove and view innovation leads for a competition")
    public boolean internalAdminCanManageInnovationLeadsForCompetition(CompetitionResource competition, UserResource user) {
        return isInternalAdmin(user);
    }
}