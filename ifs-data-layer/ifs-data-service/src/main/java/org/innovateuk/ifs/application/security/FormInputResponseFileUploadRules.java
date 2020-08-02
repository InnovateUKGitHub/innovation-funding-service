package org.innovateuk.ifs.application.security;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.repository.ApplicationRepository;
import org.innovateuk.ifs.application.resource.FormInputResponseFileEntryResource;
import org.innovateuk.ifs.commons.security.PermissionRule;
import org.innovateuk.ifs.commons.security.PermissionRules;
import org.innovateuk.ifs.security.BasePermissionRules;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.user.repository.ProcessRoleRepository;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import static org.innovateuk.ifs.util.SecurityRuleUtil.isInternal;

/**
 * Rules defining who is allowed to upload files as part of an Application Form response to a Question
 */
@Component
@PermissionRules
public class FormInputResponseFileUploadRules extends BasePermissionRules {

    @SuppressWarnings("unused")
    private static final Log LOG = LogFactory.getLog(FormInputResponseFileUploadRules.class);

    @Autowired
    private ProcessRoleRepository processRoleRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

    @PermissionRule(value = "UPDATE", description = "An Applicant can upload a file for an answer to one of their own Applications")
    public boolean applicantCanUploadFilesInResponsesForOwnApplication(FormInputResponseFileEntryResource fileEntry, UserResource user) {
        Application application = applicationRepository.findById(fileEntry.getCompoundId().getApplicationId()).orElse(null);
        return userIsApplicantOnThisApplication(fileEntry, user) && application.isOpen();
    }

    @PermissionRule(value = "READ", description = "An Applicant can download a file for an answer to one of their own Applications")
    public boolean applicantCanDownloadFilesInResponsesForOwnApplication(FormInputResponseFileEntryResource fileEntry, UserResource user) {
        return userIsApplicantOnThisApplication(fileEntry, user);
    }

    @PermissionRule(value = "READ", description = "An internal user can download a file for an answer")
    public boolean internalUserCanDownloadFilesInResponses(FormInputResponseFileEntryResource fileEntry, UserResource user) {
        return isInternal(user);
    }

    @PermissionRule(value = "READ", description = "A monitoring officer can download a file for an answer")
    public boolean monitoringOfficerCanDownloadFilesInResponses(FormInputResponseFileEntryResource fileEntry, UserResource user) {
        return monitoringOfficerCanViewApplication(fileEntry.getCompoundId().getApplicationId(), user.getId());
    }

    @PermissionRule(value = "READ", description = "Stakeholders can can download a file for an answer for applications theyre assigned to")
    public boolean stakeholdersCanDownloadFilesInResponse(FormInputResponseFileEntryResource fileEntry, UserResource user) {
        Application application = applicationRepository.findById(fileEntry.getCompoundId().getApplicationId()).get();
        return userIsStakeholderInCompetition(application.getCompetition().getId(), user.getId());
    }

    @PermissionRule(value = "READ", description = "A external Finance can can download a file for an answer for applications they're assigned to")
    public boolean externalFinanceCanDownloadFilesInResponse(FormInputResponseFileEntryResource fileEntry, UserResource user) {
        Application application = applicationRepository.findById(fileEntry.getCompoundId().getApplicationId()).get();
        return userIsExternalFinanceInCompetition(application.getCompetition().getId(), user.getId());
    }

    private boolean userIsApplicantOnThisApplication(FormInputResponseFileEntryResource fileEntry, UserResource user) {
        return userIsApplicantOnThisApplication(fileEntry.getCompoundId().getApplicationId(), user);
    }

    private boolean userIsApplicantOnThisApplication(long applicationId, UserResource user) {
        Set<Role> allApplicantRoles = EnumSet.of(Role.LEADAPPLICANT, Role.COLLABORATOR);
        List<ProcessRole> applicantProcessRoles = processRoleRepository.findByUserIdAndRoleInAndApplicationId(user.getId(), allApplicantRoles, applicationId);
        return !applicantProcessRoles.isEmpty();
    }
}