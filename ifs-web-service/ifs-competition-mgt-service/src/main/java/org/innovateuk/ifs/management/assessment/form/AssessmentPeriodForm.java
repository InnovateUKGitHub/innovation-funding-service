package org.innovateuk.ifs.management.assessment.form;

import javax.validation.constraints.NotNull;

public class AssessmentPeriodForm {

    @NotNull(message = "{validation.assessmentPeriodForm.assessmentPeriodId.required}")
    private Long assessmentPeriodId;

    public Long getAssessmentPeriodId() {
        return assessmentPeriodId;
    }

    public void setAssessmentPeriodId(Long assessmentPeriodId) {
        this.assessmentPeriodId = assessmentPeriodId;
    }
}
