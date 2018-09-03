package org.innovateuk.ifs.application.security;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.repository.ApplicationRepository;
import org.innovateuk.ifs.application.resource.FormInputResponseFileEntryResource;
import org.innovateuk.ifs.assessment.domain.Assessment;
import org.innovateuk.ifs.assessment.repository.AssessmentRepository;
import org.innovateuk.ifs.commons.security.PermissionRule;
import org.innovateuk.ifs.commons.security.PermissionRules;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.user.repository.ProcessRoleRepository;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static java.util.Arrays.asList;
import static org.innovateuk.ifs.assessment.resource.AssessmentState.acceptedAssessmentStates;
import static org.innovateuk.ifs.util.SecurityRuleUtil.isInternal;

/**
 * Rules defining who is allowed to upload files as part of an Application Form response to a Question
 */
@Component
@PermissionRules
public class FormInputResponseFileUploadRules {

    @SuppressWarnings("unused")
    private static final Log LOG = LogFactory.getLog(FormInputResponseFileUploadRules.class);

    @Autowired
    private ProcessRoleRepository processRoleRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private AssessmentRepository assessmentRepository;

    @PermissionRule(value = "UPDATE", description = "An Applicant can upload a file for an answer to one of their own Applications")
    public boolean applicantCanUploadFilesInResponsesForOwnApplication(FormInputResponseFileEntryResource fileEntry, UserResource user) {
        Application application = applicationRepository.findOne(fileEntry.getCompoundId().getApplicationId());
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

    @PermissionRule(value = "READ", description = "An Assessor can download a file for an answer to an Application they are Assessing")
    public boolean assessorCanDownloadFileForApplicationTheyAreAssessing(FormInputResponseFileEntryResource fileEntry, UserResource user) {
        return userIsAssessorOnThisApplication(fileEntry, user);
    }

    private boolean userIsAssessorOnThisApplication(FormInputResponseFileEntryResource fileEntry, UserResource user) {
        ProcessRole assessorProcessRole = processRoleRepository.findByUserIdAndRoleAndApplicationId(user.getId(),
                Role.ASSESSOR,
                fileEntry.getCompoundId().getApplicationId());
        ProcessRole panelAssessorProcessRole = processRoleRepository.findByUserIdAndRoleAndApplicationId(user.getId(),
                Role.PANEL_ASSESSOR,
                fileEntry.getCompoundId().getApplicationId());
        ProcessRole interviewAssessorProcessRole = processRoleRepository.findByUserIdAndRoleAndApplicationId(user.getId(),
                Role.INTERVIEW_ASSESSOR,
                fileEntry.getCompoundId().getApplicationId());
        return (assessorProcessRole != null && assessmentIsAccepted(assessorProcessRole))
                || panelAssessorProcessRole != null
                || interviewAssessorProcessRole != null;
    }

    private boolean userIsApplicantOnThisApplication(FormInputResponseFileEntryResource fileEntry, UserResource user) {
        return userIsApplicantOnThisApplication(fileEntry.getCompoundId().getApplicationId(), user);
    }

    private boolean userIsApplicantOnThisApplication(long applicationId, UserResource user) {
        List<Role> allApplicantRoles = asList(Role.LEADAPPLICANT, Role.COLLABORATOR);
        List<ProcessRole> applicantProcessRoles = processRoleRepository.findByUserIdAndRoleInAndApplicationId(user.getId(), allApplicantRoles, applicationId);
        return !applicantProcessRoles.isEmpty();
    }

    private boolean assessmentIsAccepted(ProcessRole processRole) {
        Assessment assessment = assessmentRepository.findOneByParticipantId(processRole.getId());
        return acceptedAssessmentStates.contains(assessment.getProcessState());
    }
}
