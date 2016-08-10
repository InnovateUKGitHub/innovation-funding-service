package com.worth.ifs.competition.security;

import com.worth.ifs.BasePermissionRulesTest;
import com.worth.ifs.competition.resource.CompetitionCountResource;
import com.worth.ifs.competition.resource.CompetitionSearchResult;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Tests the logic within the individual CompetitionSearchResultPermissionRules methods that secures basic competition count details
 */
public class CompetitionSearchResultRulesTest extends BasePermissionRulesTest<CompetitionSearchResultPermissionRules> {

	@Override
	protected CompetitionSearchResultPermissionRules supplyPermissionRulesUnderTest() { return new CompetitionSearchResultPermissionRules(); }
	
    @Test
    public void testAnyoneCanViewACompetition() {
        assertTrue(rules.anyoneCanSearchCompetitions(new CompetitionSearchResult(), null));
    }
    
}
