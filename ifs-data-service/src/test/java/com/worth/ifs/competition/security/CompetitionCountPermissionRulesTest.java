package com.worth.ifs.competition.security;

import com.worth.ifs.BasePermissionRulesTest;
import com.worth.ifs.competition.resource.CompetitionCountResource;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Tests the logic within the individual CompetitionPermissionRules methods that secures basic competition count details
 */
public class CompetitionCountPermissionRulesTest extends BasePermissionRulesTest<CompetitionCountPermissionRules> {

	@Override
	protected CompetitionCountPermissionRules supplyPermissionRulesUnderTest() { return new CompetitionCountPermissionRules(); }
	
    @Test
    public void testAnyoneCanViewACompetition() {
        assertTrue(rules.anyoneCanViewCompetitionCounts(new CompetitionCountResource(), null));
    }
    
}
