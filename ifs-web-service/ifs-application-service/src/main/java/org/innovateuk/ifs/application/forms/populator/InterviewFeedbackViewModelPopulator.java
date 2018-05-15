package org.innovateuk.ifs.application.forms.populator;

import org.innovateuk.ifs.application.forms.viewmodel.InterviewFeedbackViewModel;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.interview.service.InterviewResponseRestService;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.resource.Role;
import org.springframework.stereotype.Component;

import static java.util.Optional.ofNullable;

@Component
public class InterviewFeedbackViewModelPopulator {

    private InterviewResponseRestService interviewResponseRestService;

    public InterviewFeedbackViewModelPopulator(InterviewResponseRestService interviewResponseRestService) {
        this.interviewResponseRestService = interviewResponseRestService;
    }

    public InterviewFeedbackViewModel populate(long applicationId, ProcessRoleResource userApplicationRole) {
        String filename = ofNullable(interviewResponseRestService.findResponse(applicationId).getSuccess())
                .map(FileEntryResource::getName)
                .orElse(null);
        return new InterviewFeedbackViewModel(filename, Role.getById(userApplicationRole.getRole()).isLeadApplicant());
    }
}
