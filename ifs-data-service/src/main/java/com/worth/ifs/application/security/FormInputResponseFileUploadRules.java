package com.worth.ifs.application.security;

import com.worth.ifs.application.domain.Application;
import com.worth.ifs.application.repository.ApplicationRepository;
import com.worth.ifs.application.resource.FormInputResponseFileEntryResource;
import com.worth.ifs.commons.security.PermissionRule;
import com.worth.ifs.commons.security.PermissionRules;
import com.worth.ifs.security.SecurityRuleUtil;
import com.worth.ifs.user.domain.ProcessRole;
import com.worth.ifs.user.domain.Role;
import com.worth.ifs.user.repository.ProcessRoleRepository;
import com.worth.ifs.user.repository.RoleRepository;
import com.worth.ifs.user.resource.UserResource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

import static com.worth.ifs.user.resource.UserRoleType.*;

/**
 * Rules defining who is allowed to upload files as part of an com.worth.ifs.Application Form response to a Question
 */
@Component
@PermissionRules
public class FormInputResponseFileUploadRules {

    @SuppressWarnings("unused")
    private static final Log LOG = LogFactory.getLog(FormInputResponseFileUploadRules.class);

    @Autowired
    private ProcessRoleRepository processRoleRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

    @PermissionRule(value = "UPDATE", description = "An Applicant can upload a file for an answer to one of their own Applications")
    public boolean applicantCanUploadFilesInResponsesForOwnApplication(FormInputResponseFileEntryResource fileEntry, UserResource user) {
        Application application = applicationRepository.findOne(fileEntry.getCompoundId().getApplicationId());
        return userIsApplicantOnThisApplication(fileEntry, user) && application.isOpen();
    }

    @PermissionRule(value = "READ", description = "An Applicant can download a file for an answer to one of their own Applications")
    public boolean applicantCanDownloadFilesInResponsesForOwnApplication(FormInputResponseFileEntryResource fileEntry, UserResource user) {
        return userIsApplicantOnThisApplication(fileEntry, user);
    }

    @PermissionRule(value = "READ", description = "A comp admin can download a file for an answer")
    public boolean compAdminCanDownloadFilesInResponses(FormInputResponseFileEntryResource fileEntry, UserResource user) {
        return SecurityRuleUtil.isCompAdmin(user);
    }

    @PermissionRule(value = "READ", description = "A comp admin can download a file for an answer")
    public boolean projectFinanceUserCanDownloadFilesInResponses(FormInputResponseFileEntryResource fileEntry, UserResource user) {
        return SecurityRuleUtil.isProjectFinanceUser(user);
    }

    private boolean userIsApplicantOnThisApplication(FormInputResponseFileEntryResource fileEntry, UserResource user) {
        return userIsApplicantOnThisApplication(fileEntry.getCompoundId().getApplicationId(), user);
    }

    private boolean userIsApplicantOnThisApplication(long applicationId, UserResource user) {
        List<Role> allApplicantRoles = roleRepository.findByNameIn(Arrays.asList(APPLICANT.getName(), LEADAPPLICANT.getName(), COLLABORATOR.getName()));
        List<ProcessRole> applicantProcessRoles = processRoleRepository.findByUserIdAndRoleInAndApplicationId(user.getId(), allApplicantRoles, applicationId);
        return !applicantProcessRoles.isEmpty();
    }
}
