package org.innovateuk.ifs.management.competition.setup.applicationassessment.form;

import org.innovateuk.ifs.management.competition.setup.core.form.CompetitionSetupForm;

import javax.validation.constraints.NotNull;

/**
 * Form to capture the selection of an Application assessment in Competition Setup.
 */
public class ApplicationAssessmentForm extends CompetitionSetupForm {

    @NotNull(message = "{validation.applicationassessmentform.application.assessment.required}")
    private Boolean assessmentStage;

    public ApplicationAssessmentForm() {
    }

    public ApplicationAssessmentForm(Boolean assessmentStage) {
        this.assessmentStage = assessmentStage;
    }

    public Boolean getAssessmentStage() {
        return assessmentStage;
    }

    public void setAssessmentStage(Boolean assessmentStage) {
        this.assessmentStage = assessmentStage;
    }
}
