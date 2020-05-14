package org.innovateuk.ifs.competition.domain;

import org.innovateuk.ifs.competition.resource.AssessorFinanceView;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "competition_assessment_config")
public class CompetitionAssessmentConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(mappedBy = "competitionAssessmentConfig", fetch = FetchType.LAZY)
    private Competition competition;

    private Boolean includeAverageAssessorScoreInNotifications;
    private Integer assessorCount;
    private BigDecimal assessorPay;
    private Boolean hasAssessmentPanel;
    private Boolean hasInterviewStage;

    @Enumerated(EnumType.STRING)
    private AssessorFinanceView assessorFinanceView = AssessorFinanceView.OVERVIEW;

    public CompetitionAssessmentConfig() {
    }

    public CompetitionAssessmentConfig(Competition competition,
                                       Boolean includeAverageAssessorScoreInNotifications,
                                       Integer assessorCount,
                                       BigDecimal assessorPay,
                                       Boolean hasAssessmentPanel,
                                       Boolean hasInterviewStage,
                                       AssessorFinanceView assessorFinanceView) {
        this.competition = competition;
        this.includeAverageAssessorScoreInNotifications = includeAverageAssessorScoreInNotifications;
        this.assessorCount = assessorCount;
        this.assessorPay = assessorPay;
        this.hasAssessmentPanel = hasAssessmentPanel;
        this.hasInterviewStage = hasInterviewStage;
        this.assessorFinanceView = assessorFinanceView;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Competition getCompetition() {
        return competition;
    }

    public void setCompetition(Competition competition) {
        this.competition = competition;
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
