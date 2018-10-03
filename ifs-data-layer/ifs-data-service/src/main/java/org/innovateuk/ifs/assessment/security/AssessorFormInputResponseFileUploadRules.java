package org.innovateuk.ifs.assessment.security;

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

import static org.innovateuk.ifs.assessment.resource.AssessmentState.acceptedAssessmentStates;

@Component
@PermissionRules
public class AssessorFormInputResponseFileUploadRules {

    @Autowired
    private ProcessRoleRepository processRoleRepository;

    @Autowired
    private AssessmentRepository assessmentRepository;


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


    private boolean assessmentIsAccepted(ProcessRole processRole) {
        Assessment assessment = assessmentRepository.findOneByParticipantId(processRole.getId());
        return acceptedAssessmentStates.contains(assessment.getProcessState());
    }
}
