package org.innovateuk.ifs.competition.security;

import org.innovateuk.ifs.commons.security.PermissionRule;
import org.innovateuk.ifs.commons.security.PermissionRules;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSearchResultItem;
import org.innovateuk.ifs.competition.resource.CompetitionStatus;
import org.innovateuk.ifs.invite.domain.CompetitionParticipant;
import org.innovateuk.ifs.invite.domain.CompetitionParticipantRole;
import org.innovateuk.ifs.invite.repository.CompetitionParticipantRepository;
import org.innovateuk.ifs.security.BasePermissionRules;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.innovateuk.ifs.security.SecurityRuleUtil.*;

/**
 * Provides the permissions around CRUD for Competitions
 */
@Component
@PermissionRules
public class CompetitionPermissionRules extends BasePermissionRules {

    @Autowired
    private CompetitionParticipantRepository competitionParticipantRepository;

    @PermissionRule(value = "READ", description = "External users cannot view competitions in setup")
    public boolean externalUsersCannotViewCompetitionsInSetup(CompetitionResource competition, UserResource user) {
        return !CompetitionStatus.COMPETITION_SETUP.equals(competition.getCompetitionStatus());
    }

    @PermissionRule(value = "READ", description = "Internal users can see all competitions")
    public boolean internalUserCanViewAllCompetitions(CompetitionResource competition, UserResource user) {
        return isInternal(user) && !isInnovationLead(user);
    }

    @PermissionRule(value = "READ", description = "Internal users can see all competitions")
    public boolean innovationLeadCanViewCompetitionAssignedToThem(CompetitionResource competition, UserResource user) {
        return userIsInnovationLeadOnCompetition(competition.getId(), user.getId());
    }

    @PermissionRule(value = "READ", description = "Internal users can see all competition search results")
    public boolean internalUserCanViewAllCompetitionSearchResults(CompetitionSearchResultItem competition, UserResource user) {
        return isInternal(user) && !isInnovationLead(user);
    }

    @PermissionRule(value = "READ", description = "Innovation lead users can only see competitions they are assigned in search results")
    public boolean innovationLeadCanViewCompetitionAssignedToThemInSearchResults(CompetitionSearchResultItem competition, UserResource user) {
        return userIsInnovationLeadOnCompetition(competition.getId(), user.getId());
    }

    private boolean userIsInnovationLeadOnCompetition(long competitionId, long loggedInUserId){
        List<CompetitionParticipant> competitionParticipants = competitionParticipantRepository.getByCompetitionIdAndRole(competitionId, CompetitionParticipantRole.INNOVATION_LEAD);
        return competitionParticipants.stream().anyMatch(cp -> cp.getUser().getId().equals(loggedInUserId));
    }

    @PermissionRule(value = "MANAGE_INNOVATION_LEADS", description = "Competition Admin and Project Finance can add, remove and view innovation leads for a competition")
    public boolean internalAdminCanManageInnovationLeadsForCompetition(CompetitionResource competition, UserResource user) {
        return isInternalAdmin(user);
    }
}