package com.worth.ifs.competition.security;

import com.worth.ifs.competition.resource.CompetitionCountResource;
import com.worth.ifs.competition.resource.CompetitionSearchResult;
import com.worth.ifs.security.PermissionRule;
import com.worth.ifs.security.PermissionRules;
import com.worth.ifs.user.resource.UserResource;
import org.springframework.stereotype.Component;

/**
 * Provides the permissions around retrieving the search results for competitions.
 */
@Component
@PermissionRules
public class CompetitionSearchResultPermissionRules {

    @PermissionRule(value = "READ", description = "Anyone can search competitions")
    public boolean anyoneCanSearchCompetitions(CompetitionSearchResult competitionSearchResult, UserResource user) {
        return true;
    }

}
