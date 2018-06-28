package org.innovateuk.ifs.application.feedback.populator;

import org.innovateuk.ifs.application.feedback.viewmodel.InterviewFeedbackViewModel;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.interview.service.InterviewAssignmentRestService;
import org.innovateuk.ifs.interview.service.InterviewResponseRestService;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.UserRestService;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static java.util.Optional.ofNullable;

@Component
public class InterviewFeedbackViewModelPopulator {

    private InterviewResponseRestService interviewResponseRestService;
    private InterviewAssignmentRestService interviewAssignmentRestService;
    private UserRestService userRestService;

    public InterviewFeedbackViewModelPopulator(InterviewResponseRestService interviewResponseRestService, InterviewAssignmentRestService interviewAssignmentRestService, UserRestService userRestService) {
        this.interviewResponseRestService = interviewResponseRestService;
        this.interviewAssignmentRestService = interviewAssignmentRestService;
        this.userRestService = userRestService;
    }

    public InterviewFeedbackViewModel populate(long applicationId, UserResource userResource, boolean isFeedbackReleased) {
        String responseFilename = ofNullable(interviewResponseRestService.findResponse(applicationId).getSuccess())
                .map(FileEntryResource::getName)
                .orElse(null);

        Optional<ProcessRoleResource> role = userRestService.findProcessRole(userResource.getId(), applicationId).getOptionalSuccessObject();

        String feedbackFilename = ofNullable(interviewAssignmentRestService.findFeedback(applicationId).getSuccess())
                .map(FileEntryResource::getName)
                .orElse(null);

        return new InterviewFeedbackViewModel(responseFilename,
                feedbackFilename,
                role.map(ProcessRoleResource::getRole).map(Role::isLeadApplicant).orElse(false),
                isFeedbackReleased,
                userResource.hasRole(Role.ASSESSOR) || userResource.hasAnyRoles(Role.internalRoles())
        );
    }
}
