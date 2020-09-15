package org.innovateuk.ifs.competition.resource;

import java.math.BigDecimal;

public class CompetitionAssessmentConfigResource {

    private Boolean includeAverageAssessorScoreInNotifications;
    private Integer assessorCount;
    private BigDecimal assessorPay;
    private Boolean hasAssessmentPanel;
    private Boolean hasInterviewStage;
    private AssessorFinanceView assessorFinanceView = AssessorFinanceView.OVERVIEW;

    public CompetitionAssessmentConfigResource() {
    }

    public CompetitionAssessmentConfigResource(Boolean includeAverageAssessorScoreInNotifications,
                                               Integer assessorCount,
                                               BigDecimal assessorPay,
                                               Boolean hasAssessmentPanel,
                                               Boolean hasInterviewStage,
                                               AssessorFinanceView assessorFinanceView) {
        this.includeAverageAssessorScoreInNotifications = includeAverageAssessorScoreInNotifications;
        this.assessorCount = assessorCount;
        this.assessorPay = assessorPay;
        this.hasAssessmentPanel = hasAssessmentPanel;
        this.hasInterviewStage = hasInterviewStage;
        this.assessorFinanceView = assessorFinanceView;
    }

    public Boolean getIncludeAverageAssessorScoreInNotifications() {
        return includeAverageAssessorScoreInNotifications;
    }

    public void setIncludeAverageAssessorScoreInNotifications(Boolean includeAverageAssessorScoreInNotifications) {
        this.includeAverageAssessorScoreInNotifications = includeAverageAssessorScoreInNotifications;
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
