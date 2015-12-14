package com.worth.ifs.application.security;

import com.worth.ifs.file.resource.FileEntryResource;
import com.worth.ifs.security.PermissionRule;
import com.worth.ifs.security.PermissionRules;
import com.worth.ifs.user.domain.User;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Rules defining who is allowed to upload files as part of an Application Form response to a Question
 */
@PermissionRules
public class FormInputResponseFileUploadRules {

    private static final Log LOG = LogFactory.getLog(FormInputResponseFileUploadRules.class);

    @PermissionRule(value = "UPDATE", description = "An Applicant can upload a file for an answer to one of their own Applications")
    public boolean applicantUploadForOwnApplication(FileEntryResource fileEntry, User user) {

        // TODO DW - implement!
        LOG.warn("Need to implement FormInputResponseFileUploadRules.applicantUploadForOwnApplication()");
        return true;
    }
}
