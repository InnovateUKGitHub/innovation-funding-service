package org.innovateuk.ifs.competition.resource;

import java.math.BigDecimal;

public class CompetitionAssessmentConfigResource {

    private Boolean averageAssessorScore;

    private Integer assessorCount;
    private BigDecimal assessorPay;
    private Boolean hasAssessmentPanel;
    private Boolean hasInterviewStage;
    private AssessorFinanceView assessorFinanceView = AssessorFinanceView.OVERVIEW;

    public CompetitionAssessmentConfigResource() {
    }

    public CompetitionAssessmentConfigResource(Boolean averageAssessorScore,
                                               Integer assessorCount,
                                               BigDecimal assessorPay,
                                               Boolean hasAssessmentPanel,
                                               Boolean hasInterviewStage,
                                               AssessorFinanceView assessorFinanceView) {
        this.averageAssessorScore = averageAssessorScore;
        this.assessorCount = assessorCount;
        this.assessorPay = assessorPay;
        this.hasAssessmentPanel = hasAssessmentPanel;
        this.hasInterviewStage = hasInterviewStage;
        this.assessorFinanceView = assessorFinanceView;
    }

    public Boolean getAverageAssessorScore() {
        return averageAssessorScore;
    }

    public void setAverageAssessorScore(Boolean averageAssessorScore) {
        this.averageAssessorScore = averageAssessorScore;
    }

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

    public AssessorFinanceView getAssessorFinanceView() {
        return assessorFinanceView;
    }

    public void setAssessorFinanceView(AssessorFinanceView assessorFinanceView) {
        this.assessorFinanceView = assessorFinanceView;
    }
}
