package org.innovateuk.ifs.application.security;

import org.innovateuk.ifs.commons.security.PermissionRule;
import org.innovateuk.ifs.commons.security.PermissionRules;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.security.BasePermissionRules;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.stereotype.Component;

import static org.innovateuk.ifs.util.SecurityRuleUtil.isInnovationLead;
import static org.innovateuk.ifs.util.SecurityRuleUtil.isInternal;

@Component
@PermissionRules
public class CompetitionSummaryPermissionRules extends BasePermissionRules {
    @PermissionRule(value = "VIEW_COMPETITION_SUMMARY", description = "Innovation lead users can view competition summary of competitions assigned to them.")
    public boolean innovationLeadsCanViewCompetitionSummaryOnAssginedComps(CompetitionResource competition, UserResource user) {
        return userIsInnovationLeadOnCompetition(competition.getId(), user.getId());
    }

    @PermissionRule(value = "VIEW_COMPETITION_SUMMARY", description = "Internal users (except innovation leads) can view competition summary of any competition.")
    public boolean allInternalUsersCanViewCompetitionSummaryOtherThanInnovationLeads(CompetitionResource competition, UserResource user) {
        return isInternal(user) && !isInnovationLead(user);
    }
}