package org.innovateuk.ifs.application.forms.security;

import org.innovateuk.ifs.application.resource.ApplicationCompositeId;
import org.innovateuk.ifs.commons.security.PermissionRule;
import org.innovateuk.ifs.commons.security.PermissionRules;
import org.innovateuk.ifs.interview.service.InterviewAssignmentRestService;
import org.innovateuk.ifs.security.BasePermissionRules;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@PermissionRules
@Component
public class ApplicationFeedbackPermissionRules extends BasePermissionRules {

    @Autowired
    private InterviewAssignmentRestService interviewAssignmentRestService;

    @PermissionRule(value = "APPLICATION_FEEDBACK", description = "Only the applicant can see the feedback for their application" +
            "if the application has been assigned to an interview panel.")
    public boolean interviewFeedback(ApplicationCompositeId applicationCompositeId, UserResource loggedInUser) {
        return isApplicationAssignedToInterviewPanel(applicationCompositeId) && isUserOnApplication(applicationCompositeId, loggedInUser);
    }

    private boolean isApplicationAssignedToInterviewPanel(ApplicationCompositeId applicationCompositeId) {
        return interviewAssignmentRestService.isAssignedToInterview(applicationCompositeId.id()).getSuccess();
    }

    private boolean isUserOnApplication(ApplicationCompositeId applicationCompositeId, UserResource loggedInUser){
        return isUserOnApplication(applicationCompositeId.id(), loggedInUser);
    }
}
