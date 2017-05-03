package org.innovateuk.ifs.util;

import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.commons.error.exception.ForbiddenActionException;
import org.innovateuk.ifs.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Util for common functions in the Application section
 */
@Component
public class ApplicationUtil {

    @Autowired
    private UserService userService;

    public void checkIfApplicationAlreadySubmitted(ApplicationResource applicationResource) {
        if (applicationResource.hasBeenSubmitted()){
            throw new ForbiddenActionException("Application has already been submitted");
        }
    }

    public void checkUserIsLeadApplicant(ApplicationResource applicationResource, long loggedInUserId) {
        if (loggedInUserId != getLeadApplicantId(applicationResource)) {
            throw new ForbiddenActionException("User must be Lead Applicant");
        }
    }

    private long getLeadApplicantId(ApplicationResource applicationResource) {
        return userService.getLeadApplicantProcessRoleOrNull(applicationResource).getUser();
    }
}
