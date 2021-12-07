package org.innovateuk.ifs.management.competition.setup.closecompetition.viewmodel;

import org.innovateuk.ifs.application.resource.ApplicationResource;

import java.time.ZonedDateTime;
import java.util.List;

/**
 * Holder of model attributes for the Always Open Competition - Close competition page
 */
public class AlwaysOpenCloseCompetitionViewModel {

    private Long competitionId;
    private String competitionName;
    private ZonedDateTime submissionDate;
    private List<ApplicationResource> submittedApplications;

    public AlwaysOpenCloseCompetitionViewModel(Long competitionId, String competitionName, ZonedDateTime submissionDate, List<ApplicationResource> submittedApplications) {
        this.competitionId = competitionId;
        this.competitionName = competitionName;
        this.submissionDate = submissionDate;
        this.submittedApplications = submittedApplications;
    }

    public Long getCompetitionId() {
        return competitionId;
    }

    public String getCompetitionName() {
        return competitionName;
    }

    public ZonedDateTime getSubmissionDate() {
        return submissionDate;
    }

    public List<ApplicationResource> getSubmittedApplications() {
        return submittedApplications;
    }

    public boolean submissionDateIsPresentAndHadPassed() {
        return (submissionDate != null) && (submissionDate.isBefore(ZonedDateTime.now()));
    }

    public boolean feedbackReleasedForAllApplications() {
        return submittedApplications.stream().allMatch(ApplicationResource::isFeedbackReleased);
    }

    // need to see if the manage_email_funding_date can be used after the bug fix tickets/ web test data fixes
    public boolean closeCompetitionButtonEnabled() {
        return submissionDateIsPresentAndHadPassed() && feedbackReleasedForAllApplications();
    }

}
