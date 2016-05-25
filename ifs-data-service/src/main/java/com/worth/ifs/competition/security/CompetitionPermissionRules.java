package com.worth.ifs.competition.security;

import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.security.PermissionRule;
import com.worth.ifs.security.PermissionRules;
import com.worth.ifs.user.resource.UserResource;

import static com.worth.ifs.security.SecurityRuleUtil.isCompAdmin;

import org.springframework.stereotype.Component;

/**
 * Provides the permissions around CRUD for Competitions
 */
@Component
@PermissionRules
public class CompetitionPermissionRules {

    @PermissionRule(value = "READ", description = "Anyone can view Competitions",
            additionalComments =
                    "This seems too powerful a permission that would allow an anonymous user of the API to view the entire " +
                    "CompetitionResource.  CompetitionResource should be broken into public- and non-public-facing parts")
    public boolean anyoneCanViewCompetitions(CompetitionResource competition, UserResource user) {
        return true;
    }
    
    @PermissionRule(value = "CHECK_ASSESSOR_FEEDBACK_UPLOADED", description = "Comp admin can check if assessor feedback uploaded for Competitions")
    public boolean compAdminCanCheckForAssessorFeedbackUploaded(CompetitionResource competition, UserResource user) {
    	return isCompAdmin(user);
    }
    
    @PermissionRule(value = "SUBMIT_ASSESSOR_FEEDBACK", description = "Comp admin can submit assessor feedback for Competitions")
    public boolean compAdminCanSubmitAssessorFeedback(CompetitionResource competition, UserResource user) {
    	return isCompAdmin(user);
    }
}
