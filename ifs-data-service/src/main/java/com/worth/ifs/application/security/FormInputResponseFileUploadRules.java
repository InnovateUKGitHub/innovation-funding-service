package com.worth.ifs.application.security;

import com.worth.ifs.application.resource.FormInputResponseFileEntryResource;
import com.worth.ifs.form.domain.FormInputResponse;
import com.worth.ifs.form.repository.FormInputResponseRepository;
import com.worth.ifs.security.PermissionRule;
import com.worth.ifs.security.PermissionRules;
import com.worth.ifs.user.domain.ProcessRole;
import com.worth.ifs.user.domain.User;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.worth.ifs.user.domain.UserRoleType.APPLICANT;

/**
 * Rules defining who is allowed to upload files as part of an Application Form response to a Question
 */
@Component
@PermissionRules
public class FormInputResponseFileUploadRules {

    private static final Log LOG = LogFactory.getLog(FormInputResponseFileUploadRules.class);

    @Autowired
    private FormInputResponseRepository formInputResponseRepository;

    @PermissionRule(value = "UPDATE", description = "An Applicant can upload a file for an answer to one of their own Applications")
    public boolean applicantCanUploadFilesInResponsesForOwnApplication(FormInputResponseFileEntryResource fileEntry, User user) {

        FormInputResponse response = formInputResponseRepository.findOne(fileEntry.getFormInputResponseId());

        if (response == null) {
            LOG.warn("Unable to locate FormInputResponse with id " + fileEntry.getFormInputResponseId());
            return false;
        }

        Long applicationId = response.getApplication().getId();
        List<ProcessRole> applicantProcessRoles = user.getProcessRolesForRole(APPLICANT);

        boolean userIsApplicantOnThisApplication =
                applicantProcessRoles.stream().anyMatch(processRole -> processRole.getApplication().getId().equals(applicationId));

        return userIsApplicantOnThisApplication;
    }
}
