package org.innovateuk.ifs.management.viewmodel;

import java.time.ZonedDateTime;
import java.util.List;

/**
 * View model for the Competition Management Submitted Applications page.
 */
public class SubmittedApplicationsViewModel extends BaseApplicationsViewModel<SubmittedApplicationsRowViewModel> {

    private ZonedDateTime assessmentDeadline;
    private int applicationsSubmitted;

    public SubmittedApplicationsViewModel(long competitionId,
                                          String competitionName,
                                          ZonedDateTime assessmentDeadline,
                                          int applicationsSubmitted,
                                          String sorting,
                                          String filter,
                                          List<SubmittedApplicationsRowViewModel> applications,
                                          PaginationViewModel pagination) {
        super(competitionId, competitionName, applications, pagination, sorting, filter);
        this.assessmentDeadline = assessmentDeadline;
        this.applicationsSubmitted = applicationsSubmitted;
    }

    public ZonedDateTime getAssessmentDeadline() {
        return assessmentDeadline;
    }

    public int getApplicationsSubmitted() {
        return applicationsSubmitted;
    }
}
