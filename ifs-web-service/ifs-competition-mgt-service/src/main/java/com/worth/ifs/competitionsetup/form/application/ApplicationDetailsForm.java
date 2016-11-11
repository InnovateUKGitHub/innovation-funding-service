package com.worth.ifs.competitionsetup.form.application;

import com.worth.ifs.competitionsetup.form.CompetitionSetupForm;

public class ApplicationDetailsForm extends CompetitionSetupForm {

    private boolean useProjectTitleQuestion;
    private boolean useResubmissionQuestion;
    private boolean useEstimatedStartDateQuestion;
    private boolean useDurationQuestion;


    public boolean isUseProjectTitleQuestion() {
        return useProjectTitleQuestion;
    }

    public void setUseProjectTitleQuestion(boolean useProjectTitleQuestion) {
        this.useProjectTitleQuestion = useProjectTitleQuestion;
    }

    public boolean isUseResubmissionQuestion() {
        return useResubmissionQuestion;
    }

    public void setUseResubmissionQuestion(boolean useResubmissionQuestion) {
        this.useResubmissionQuestion = useResubmissionQuestion;
    }

    public boolean isUseEstimatedStartDateQuestion() {
        return useEstimatedStartDateQuestion;
    }

    public void setUseEstimatedStartDateQuestion(boolean useEstimatedStartDateQuestion) {
        this.useEstimatedStartDateQuestion = useEstimatedStartDateQuestion;
    }

    public boolean isUseDurationQuestion() {
        return useDurationQuestion;
    }

    public void setUseDurationQuestion(boolean useDurationQuestion) {
        this.useDurationQuestion = useDurationQuestion;
    }
}
