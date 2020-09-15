package org.innovateuk.ifs.management.competition.setup.assessor.form;

import org.innovateuk.ifs.competition.resource.AssessorFinanceView;
import org.innovateuk.ifs.management.competition.setup.core.form.CompetitionSetupForm;

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
    private Boolean hasAssessmentPanel;

    @NotNull(message = "{validation.assessorsform.interviewStage.required}")
    private Boolean hasInterviewStage;

    @NotNull(message = "{validation.assessorsform.averageAssessorScore.required}")
    private Boolean averageAssessorScore;

    @NotNull(message = "{validation.assessorsform.assessorFinanceView.required}")
    private AssessorFinanceView assessorFinanceView;

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

    public Boolean getHasAssessmentPanel() {
        return hasAssessmentPanel;
    }

    public void setHasAssessmentPanel(Boolean hasAssessmentPanel) {
        this.hasAssessmentPanel = hasAssessmentPanel;
    }

    public Boolean getHasInterviewStage() {
        return hasInterviewStage;
    }

    public void setHasInterviewStage(Boolean hasInterviewStage) {
        this.hasInterviewStage = hasInterviewStage;
    }

    public Boolean getAverageAssessorScore() {
        return averageAssessorScore;
    }

    public void setAverageAssessorScore(Boolean averageAssessorScore) {
        this.averageAssessorScore = averageAssessorScore;
    }

    public AssessorFinanceView getAssessorFinanceView() {
        return assessorFinanceView;
    }

    public void setAssessorFinanceView(AssessorFinanceView assessorFinanceView) {
        this.assessorFinanceView = assessorFinanceView;
    }
}
