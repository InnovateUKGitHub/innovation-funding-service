package org.innovateuk.ifs.application.security;

import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.ApplicationService;
import org.innovateuk.ifs.commons.security.PermissionRule;
import org.innovateuk.ifs.commons.security.PermissionRules;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Permission checker around the access to Application
 */
@PermissionRules
@Component
public class ApplicationPermissionRules {

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private UserService userService;

    @PermissionRule(value = "VIEW_ADD_ORGANISATION_PAGE", description = "Allowed to view the add organisation page")
    public boolean viewAddOrganisationPage(Long applicationId, UserResource loggedInUser) {
        return isLeadApplicant(applicationId, loggedInUser) && applicationNotYetSubmitted(applicationId);
    }

    @PermissionRule(value = "ADD_NEW_ORGANISATION", description = "Allowed to add a new organisation")
    public boolean addNewOrganisation(Long applicationId, UserResource loggedInUser) {
        return isLeadApplicant(applicationId, loggedInUser) && applicationNotYetSubmitted(applicationId);
    }

    @PermissionRule(value = "ADD_APPLICANT", description = "Allowed to add a new applicant")
    public boolean addApplicant(Long applicationId, UserResource loggedInUser) {
        return isLeadApplicant(applicationId, loggedInUser) && applicationNotYetSubmitted(applicationId);
    }

    @PermissionRule(value = "REMOVE_APPLICANT", description = "Allowed to remove an existing applicant")
    public boolean removeApplicant(Long applicationId, UserResource loggedInUser) {
        return isLeadApplicant(applicationId, loggedInUser) && applicationNotYetSubmitted(applicationId);
    }

    @PermissionRule(value = "VIEW_APPLICATION_TEAM_PAGE", description = "Allowed to view the application team page")
    public boolean viewApplicationTeamPage(Long applicationId, UserResource loggedInUser) {
        return applicationNotYetSubmitted(applicationId);
    }

    @PermissionRule(value = "BEGIN_APPLICATION", description = "Allowed to begin an application")
    public boolean beginApplication(Long applicationId, UserResource loggedInUser) {
        return isLeadApplicant(applicationId, loggedInUser);
    }

    private boolean isLeadApplicant(Long applicationId, UserResource loggedInUser) {
        ApplicationResource applicationResource = getApplication(applicationId);
        return loggedInUser.getId() == getLeadApplicantId(applicationResource);
    }

    private long getLeadApplicantId(ApplicationResource applicationResource) {
        return userService.getLeadApplicantProcessRoleOrNull(applicationResource).getUser();
    }

    private boolean applicationNotYetSubmitted(Long applicationId) {
        ApplicationResource applicationResource = getApplication(applicationId);
        return !applicationResource.hasBeenSubmitted();
    }

    private ApplicationResource getApplication(Long applicationId){
        return applicationService.getById(applicationId);
    }
}
