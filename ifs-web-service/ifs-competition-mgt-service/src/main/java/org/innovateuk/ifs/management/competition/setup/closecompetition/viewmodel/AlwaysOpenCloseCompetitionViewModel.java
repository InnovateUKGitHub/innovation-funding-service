package org.innovateuk.ifs.management.competition.setup.closecompetition.viewmodel;

/**
 * Holder of model attributes for the Always Open Competition - Close competition page
 */
public class AlwaysOpenCloseCompetitionViewModel {

    private boolean hasSubmissionDatePassed;

    public AlwaysOpenCloseCompetitionViewModel(boolean hasSubmissionDatePassed) {
        this.hasSubmissionDatePassed = hasSubmissionDatePassed;
    }

    public boolean isHasSubmissionDatePassed() {
        return hasSubmissionDatePassed;
    }

    public void setHasSubmissionDatePassed(boolean hasSubmissionDatePassed) {
        this.hasSubmissionDatePassed = hasSubmissionDatePassed;
    }
}
