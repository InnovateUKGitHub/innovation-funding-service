package org.innovateuk.ifs.competitionsetup.form.application;

import org.innovateuk.ifs.competitionsetup.form.CompetitionSetupForm;
import org.innovateuk.ifs.commons.validation.constraints.FieldLarger;

import javax.validation.constraints.NotNull;

@FieldLarger(firstField = "maxProjectDuration", secondField = "minProjectDuration", message = "{competition.setup.applicationdetails.min.projectduration.larger}")
public class ApplicationDetailsForm extends CompetitionSetupForm {

    @NotNull(message = "{competition.setup.applicationdetails.min.projectduration}")
    private Integer minProjectDuration;

    @NotNull(message = "{competition.setup.applicationdetails.max.projectduration}")
    private Integer maxProjectDuration;

    private boolean useResubmissionQuestion;

    public Integer getMinProjectDuration() {
        return minProjectDuration;
    }

    public void setMinProjectDuration(Integer minProjectDuration) {
        this.minProjectDuration = minProjectDuration;
    }

    public Integer getMaxProjectDuration() {
        return maxProjectDuration;
    }

    public void setMaxProjectDuration(Integer maxProjectDuration) {
        this.maxProjectDuration = maxProjectDuration;
    }

    public boolean isUseResubmissionQuestion() {
        return useResubmissionQuestion;
    }

    public void setUseResubmissionQuestion(boolean useResubmissionQuestion) {
        this.useResubmissionQuestion = useResubmissionQuestion;
    }
}
