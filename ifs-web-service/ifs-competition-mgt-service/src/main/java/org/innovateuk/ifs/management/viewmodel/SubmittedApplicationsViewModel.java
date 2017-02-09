package org.innovateuk.ifs.management.viewmodel;

import java.time.LocalDateTime;
import java.util.List;

/**
 * View model for the Competition Management Submitted Applications page.
 */
public class SubmittedApplicationsViewModel extends BaseApplicationsViewModel<SubmittedApplicationsRowViewModel> {

    private LocalDateTime assessmentDeadline;
    private int applicationsSubmitted;

    public SubmittedApplicationsViewModel(long competitionId,
                                          String competitionName,
                                          LocalDateTime assessmentDeadline,
                                          int applicationsSubmitted,
                                          List<SubmittedApplicationsRowViewModel> applications) {
        super(competitionId, competitionName, applications);
        this.assessmentDeadline = assessmentDeadline;
        this.applicationsSubmitted = applicationsSubmitted;
    }

    public LocalDateTime getAssessmentDeadline() {
        return assessmentDeadline;
    }

    public int getApplicationsSubmitted() {
        return applicationsSubmitted;
    }
}
