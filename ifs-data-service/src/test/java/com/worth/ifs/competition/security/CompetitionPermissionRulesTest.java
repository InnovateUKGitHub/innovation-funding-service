package com.worth.ifs.competition.security;

import static com.worth.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static com.worth.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.junit.Assert.assertFalse;
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
    
    @Test
    public void compAdminCanCheckForAssessorFeedbackUploaded() {
        assertTrue(rules.compAdminCanCheckForAssessorFeedbackUploaded(newCompetitionResource().build(), compAdminUser()));
        assertFalse(rules.compAdminCanCheckForAssessorFeedbackUploaded(newCompetitionResource().build(), newUserResource().build()));
    }
    
    @Test
    public void compAdminCanSubmitAssessorFeedback() {
        assertTrue(rules.compAdminCanSubmitAssessorFeedback(newCompetitionResource().build(), compAdminUser()));
        assertFalse(rules.compAdminCanSubmitAssessorFeedback(newCompetitionResource().build(), newUserResource().build()));
    }

}
