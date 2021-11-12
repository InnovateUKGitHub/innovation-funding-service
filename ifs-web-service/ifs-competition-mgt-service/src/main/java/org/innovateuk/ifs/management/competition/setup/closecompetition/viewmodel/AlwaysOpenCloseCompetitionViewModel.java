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
    private boolean allApplicationsHadBeenNotified;

    public AlwaysOpenCloseCompetitionViewModel(Long competitionId, String competitionName, ZonedDateTime submissionDate, List<ApplicationResource> submittedApplications, boolean allApplicationsHadBeenNotified) {
        this.competitionId = competitionId;
        this.competitionName = competitionName;
        this.submissionDate = submissionDate;
        this.submittedApplications = submittedApplications;
        this.allApplicationsHadBeenNotified = allApplicationsHadBeenNotified;
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

    public boolean isAllApplicationsHadBeenNotified() {
        return allApplicationsHadBeenNotified;
    }

    public boolean submissionDateIsPresentAndHadPassed() {
        return (submissionDate != null) && (submissionDate.isBefore(ZonedDateTime.now()));
    }

    public boolean feedbackReleasedForAllApplications() {
        return submittedApplications.stream().allMatch(ApplicationResource::isFeedbackReleased);
    }

    public boolean showCloseCompetitionButton() {
        return submissionDateIsPresentAndHadPassed() && feedbackReleasedForAllApplications() && allApplicationsHadBeenNotified;
    }

}
