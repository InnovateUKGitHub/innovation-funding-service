package org.innovateuk.ifs.application.summary.populator;

import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.ApplicationService;
import org.innovateuk.ifs.application.service.CompetitionService;
import org.innovateuk.ifs.application.summary.viewmodel.ApplicationInterviewSummaryViewModel;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.interview.service.InterviewAssignmentRestService;
import org.innovateuk.ifs.interview.service.InterviewResponseRestService;
import org.springframework.stereotype.Component;

import static java.util.Optional.ofNullable;

@Component
public class ApplicationInterviewSummaryViewModelPopulator {

    private InterviewResponseRestService interviewResponseRestService;
    private InterviewAssignmentRestService interviewAssignmentRestService;
    private ApplicationService applicationService;
    private CompetitionService competitionService;

    public ApplicationInterviewSummaryViewModelPopulator(InterviewResponseRestService interviewResponseRestService,
                                                         InterviewAssignmentRestService interviewAssignmentRestService,
                                                         ApplicationService applicationService,
                                                         CompetitionService competitionService
    ) {
        this.interviewResponseRestService = interviewResponseRestService;
        this.interviewAssignmentRestService = interviewAssignmentRestService;
        this.applicationService = applicationService;
        this.competitionService = competitionService;
    }

    public ApplicationInterviewSummaryViewModel populate(long applicationId) {

        ApplicationResource application = applicationService.getById(applicationId);
        CompetitionResource competition = competitionService.getById(application.getCompetition());

        String responseFilename = ofNullable(interviewResponseRestService.findResponse(applicationId).getSuccess())
                .map(FileEntryResource::getName)
                .orElse(null);

        String feedbackFilename = ofNullable(interviewAssignmentRestService.findFeedback(applicationId).getSuccess())
                .map(FileEntryResource::getName)
                .orElse(null);

        return new ApplicationInterviewSummaryViewModel(
                application,
                competition,
                responseFilename,
                feedbackFilename,
                null,
                null
        );
    }
}
