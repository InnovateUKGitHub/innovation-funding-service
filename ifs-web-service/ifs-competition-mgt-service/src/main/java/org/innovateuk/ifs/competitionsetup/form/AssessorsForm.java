package org.innovateuk.ifs.competitionsetup.form;

import javax.validation.constraints.Digits;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * Form for the assessors competition setup section.
 */
public class AssessorsForm extends CompetitionSetupForm {

    @NotNull(message = "{validation.assessorsform.assessorCount.required}")
    private Integer assessorCount;

    @Min(value=0, message = "{validation.assessorsform.assessorPay.min}")
    @NotNull(message = "{validation.assessorsform.assessorPay.required}")
    @Digits(integer = 8, fraction = 0, message = "{validation.assessorsform.assessorPay.max.amount.invalid}")
    private BigDecimal assessorPay;

    @NotNull(message = "{validation.assessorsform.assessmentPanel.required}")
    private Boolean useAssessmentPanel;

    @NotNull(message = "{validation.assessorsform.interviewStage.required}")
    private Boolean addInterviewStage;

    public Integer getAssessorCount() {
        return assessorCount;
    }

    public void setAssessorCount(Integer assessorCount) {
        this.assessorCount = assessorCount;
    }

    public BigDecimal getAssessorPay() {
        return assessorPay;
    }

    public void setAssessorPay(BigDecimal assessorPay) {
        this.assessorPay = assessorPay;
    }

    public Boolean getUseAssessmentPanel() {
        return useAssessmentPanel;
    }

    public void setUseAssessmentPanel(Boolean useAssessmentPanel) {
        this.useAssessmentPanel = useAssessmentPanel;
    }

    public Boolean getAddInterviewStage() {
        return addInterviewStage;
    }

    public void setAddInterviewStage(Boolean addInterviewStage) {
        this.addInterviewStage = addInterviewStage;
    }
}
