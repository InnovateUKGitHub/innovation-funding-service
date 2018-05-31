package org.innovateuk.ifs.application.forms.populator;

import org.innovateuk.ifs.application.forms.viewmodel.InterviewFeedbackViewModel;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.interview.service.InterviewAssignmentRestService;
import org.innovateuk.ifs.interview.service.InterviewResponseRestService;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.stereotype.Component;

import static java.util.Optional.ofNullable;

@Component
public class InterviewFeedbackViewModelPopulator {

    private InterviewResponseRestService interviewResponseRestService;
    private InterviewAssignmentRestService interviewAssignmentRestService;

    public InterviewFeedbackViewModelPopulator(InterviewResponseRestService interviewResponseRestService, InterviewAssignmentRestService interviewAssignmentRestService) {
        this.interviewResponseRestService = interviewResponseRestService;
        this.interviewAssignmentRestService = interviewAssignmentRestService;
    }

    public InterviewFeedbackViewModel populate(long applicationId, ProcessRoleResource userApplicationRole, boolean isAssessor) {
        String responseFilename = ofNullable(interviewResponseRestService.findResponse(applicationId).getSuccess())
                .map(FileEntryResource::getName)
                .orElse(null);

        String feedbackFilename = ofNullable(interviewAssignmentRestService.findFeedback(applicationId).getSuccess())
                .map(FileEntryResource::getName)
                .orElse(null);

        return new InterviewFeedbackViewModel(responseFilename,
                feedbackFilename,
                userApplicationRole.getRole().isLeadApplicant(),
                isAssessor
        );
    }
}
