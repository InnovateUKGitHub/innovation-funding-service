package com.worth.ifs.competition.security;

import static com.worth.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.worth.ifs.BasePermissionRulesTest;

/**
 * Tests the logic within the individual CompetitionPermissionRules methods that secures basic Competition details
 */
public class CompetitionPermissionRulesTest extends BasePermissionRulesTest<CompetitionPermissionRules> {

	@Override
	protected CompetitionPermissionRules supplyPermissionRulesUnderTest() {
		return new CompetitionPermissionRules();
	}
	
    @Test
    public void testAnyoneCanViewACompetition() {
        assertTrue(rules.anyoneCanViewCompetitions(newCompetitionResource().build(), null));
    }
    
}
