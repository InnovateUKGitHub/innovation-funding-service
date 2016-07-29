package com.worth.ifs.competition.security;

import com.worth.ifs.competition.resource.CompetitionCountResource;
import com.worth.ifs.security.PermissionRule;
import com.worth.ifs.security.PermissionRules;
import com.worth.ifs.user.resource.UserResource;
import org.springframework.stereotype.Component;

/**
 * Provides the permissions around retrieving the counts for total number of competitions.
 */
@Component
@PermissionRules
public class CompetitionCountPermissionRules {

    @PermissionRule(value = "READ", description = "Anyone can view competition counts")
    public boolean anyoneCanViewCompetitionCounts(CompetitionCountResource competitionCountResource, UserResource user) {
        return true;
    }

}
