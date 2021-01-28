package org.innovateuk.ifs.application.security;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.repository.ApplicationRepository;
import org.innovateuk.ifs.application.resource.FormInputResponseFileEntryResource;
import org.innovateuk.ifs.commons.security.PermissionRule;
import org.innovateuk.ifs.commons.security.PermissionRules;
import org.innovateuk.ifs.security.BasePermissionRules;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Rules defining who is allowed to upload files as part of an Application Form response to a Question
 */
@Component
@PermissionRules
public class FormInputResponseFileUploadRules extends BasePermissionRules {

    @SuppressWarnings("unused")
    private static final Log LOG = LogFactory.getLog(FormInputResponseFileUploadRules.class);

    @Autowired
    private ApplicationRepository applicationRepository;
    @Autowired
    private ApplicationSecurityHelper applicationSecurityHelper;

    @PermissionRule(value = "READ", description = "A user can see the response if they can view the application")
    public boolean userCanSeeResponseIfTheyCanViewApplication(FormInputResponseFileEntryResource fileEntry, UserResource user) {
        return applicationSecurityHelper.canViewApplication(fileEntry.getCompoundId().getApplicationId(), user);
    }

    @PermissionRule(value = "UPDATE", description = "An Applicant can upload a file for an answer to one of their own Applications")
    public boolean applicantCanUploadFilesInResponsesForOwnApplication(FormInputResponseFileEntryResource fileEntry, UserResource user) {
        Application application = applicationRepository.findById(fileEntry.getCompoundId().getApplicationId()).orElse(null);
        return isMemberOfProjectTeam(application.getId(), user) && application.isOpen();
    }
}