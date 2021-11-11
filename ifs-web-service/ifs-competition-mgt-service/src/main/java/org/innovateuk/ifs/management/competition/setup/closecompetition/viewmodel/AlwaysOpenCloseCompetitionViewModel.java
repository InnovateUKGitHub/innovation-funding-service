package org.innovateuk.ifs.management.competition.setup.closecompetition.viewmodel;

/**
 * Holder of model attributes for the Always Open Competition - Close competition page
 */
public class AlwaysOpenCloseCompetitionViewModel {

    private Long competitionId;
    private String competitionName;
    private boolean hasSubmissionDatePassed;

    public AlwaysOpenCloseCompetitionViewModel(Long competitionId, String competitionName, boolean hasSubmissionDatePassed) {
        this.competitionId = competitionId;
        this.competitionName = competitionName;
        this.hasSubmissionDatePassed = hasSubmissionDatePassed;
    }

    public Long getCompetitionId() {
        return competitionId;
    }

    public void setCompetitionId(Long competitionId) {
        this.competitionId = competitionId;
    }

    public String getCompetitionName() {
        return competitionName;
    }

    public void setCompetitionName(String competitionName) {
        this.competitionName = competitionName;
    }

    public boolean isHasSubmissionDatePassed() {
        return hasSubmissionDatePassed;
    }

    public void setHasSubmissionDatePassed(boolean hasSubmissionDatePassed) {
        this.hasSubmissionDatePassed = hasSubmissionDatePassed;
    }
}
