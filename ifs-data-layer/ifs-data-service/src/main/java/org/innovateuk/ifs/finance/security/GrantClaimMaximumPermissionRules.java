package org.innovateuk.ifs.finance.security;

import org.innovateuk.ifs.commons.security.PermissionRule;
import org.innovateuk.ifs.commons.security.PermissionRules;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionTypeResource;
import org.innovateuk.ifs.finance.domain.GrantClaimMaximum;
import org.innovateuk.ifs.security.BasePermissionRules;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.stereotype.Component;

import static org.innovateuk.ifs.user.resource.Role.*;
import static org.innovateuk.ifs.util.SecurityRuleUtil.isInternal;

/**
 * Permission rules for {@link GrantClaimMaximum} for permissioning
 */
@Component
@PermissionRules
public class GrantClaimMaximumPermissionRules extends BasePermissionRules {

    @PermissionRule(value = "READ_GRANT_CLAIM_MAXIMUM",
            description = "An Internal user can see the grant claim maximums for his competition or competition type")
    public boolean internalUserCanSeeGrantClaimMaximumsForCompetitionType(CompetitionTypeResource competitionTypeResource, UserResource user) {
        return user.getRoles().contains(LEADAPPLICANT) ||
                user.getRoles().contains(COLLABORATOR) ||
                user.getRoles().contains(APPLICANT) ||
                user.getRoles().contains(COMP_ADMIN);
    }

    @PermissionRule(value = "READ_GRANT_CLAIM_MAXIMUM",
            description = "An Internal user can see the grant claim maximums for his competition or competition type")
    public boolean internalUserCanSeeGrantClaimMaximumsForCompetition(CompetitionResource competitionResource, UserResource user) {
        return user.getRoles().contains(LEADAPPLICANT) ||
                user.getRoles().contains(COLLABORATOR) ||
                user.getRoles().contains(APPLICANT);
    }
}
