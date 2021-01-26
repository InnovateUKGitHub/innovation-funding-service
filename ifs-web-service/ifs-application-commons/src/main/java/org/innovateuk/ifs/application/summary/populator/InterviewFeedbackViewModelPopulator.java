package org.innovateuk.ifs.application.summary.populator;

import org.innovateuk.ifs.application.summary.viewmodel.InterviewFeedbackViewModel;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.interview.service.InterviewAssignmentRestService;
import org.innovateuk.ifs.interview.service.InterviewResponseRestService;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.resource.ProcessRoleType;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.ProcessRoleRestService;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static java.util.Optional.ofNullable;

@Component
public class InterviewFeedbackViewModelPopulator {

    private InterviewResponseRestService interviewResponseRestService;
    private InterviewAssignmentRestService interviewAssignmentRestService;
    private ProcessRoleRestService processRoleRestService;

    public InterviewFeedbackViewModelPopulator(InterviewResponseRestService interviewResponseRestService,
                                               InterviewAssignmentRestService interviewAssignmentRestService,
                                               ProcessRoleRestService processRoleRestService) {
        this.interviewResponseRestService = interviewResponseRestService;
        this.interviewAssignmentRestService = interviewAssignmentRestService;
        this.processRoleRestService = processRoleRestService;
    }

    public InterviewFeedbackViewModel populate(long applicationId, String competitionName, UserResource userResource, boolean isFeedbackReleased) {
        String responseFilename = ofNullable(interviewResponseRestService.findResponse(applicationId).getSuccess())
                .map(FileEntryResource::getName)
                .orElse(null);

        Optional<ProcessRoleResource> role = processRoleRestService.findProcessRole(userResource.getId(), applicationId).getOptionalSuccessObject();

        String feedbackFilename = ofNullable(interviewAssignmentRestService.findFeedback(applicationId).getSuccess())
                .map(FileEntryResource::getName)
                .orElse(null);

        return new InterviewFeedbackViewModel(applicationId,
                competitionName,
                responseFilename,
                feedbackFilename,
                role.map(ProcessRoleResource::getRole).map(ProcessRoleType::isLeadApplicant).orElse(false),
                isFeedbackReleased,
                userResource.hasRole(Role.ASSESSOR) || userResource.hasAnyRoles(Role.internalRoles())
        );
    }
}
