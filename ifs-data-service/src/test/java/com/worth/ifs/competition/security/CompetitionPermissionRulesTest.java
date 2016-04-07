package com.worth.ifs.competition.security;

import org.junit.Test;
import org.mockito.InjectMocks;

import static com.worth.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.junit.Assert.assertTrue;

/**
 * Tests the logic within the individual OrganisationRules methods that secures basic Organisation details
 */
public class CompetitionPermissionRulesTest {

    @InjectMocks
    private CompetitionPermissionRules rules = new CompetitionPermissionRules();

    @Test
    public void testAnyoneCanViewACompetition() {
        assertTrue(rules.anyoneCanViewCompetitions(newCompetitionResource().build(), null));
    }
}
