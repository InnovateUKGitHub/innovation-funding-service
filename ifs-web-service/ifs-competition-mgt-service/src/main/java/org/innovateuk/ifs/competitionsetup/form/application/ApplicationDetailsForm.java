package org.innovateuk.ifs.competitionsetup.form.application;

import org.innovateuk.ifs.competitionsetup.form.CompetitionSetupForm;

public class ApplicationDetailsForm extends CompetitionSetupForm {

    private boolean useResubmissionQuestion;

    public boolean isUseResubmissionQuestion() {
        return useResubmissionQuestion;
    }

    public void setUseResubmissionQuestion(boolean useResubmissionQuestion) {
        this.useResubmissionQuestion = useResubmissionQuestion;
    }
}
