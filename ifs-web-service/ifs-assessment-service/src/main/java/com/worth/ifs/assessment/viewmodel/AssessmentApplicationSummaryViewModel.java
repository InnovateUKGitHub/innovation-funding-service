package com.worth.ifs.assessment.viewmodel;

import com.worth.ifs.application.resource.ApplicationResource;
import com.worth.ifs.competition.resource.CompetitionResource;

import java.util.List;

/**
 * Holder of model attributes for the Application Summary displayed when a review is requested by the Assessor.
 */
public class AssessmentApplicationSummaryViewModel {

    private Long assessmentId;
    private long daysLeft;
    private long daysLeftPercentage;
    private CompetitionResource competition;
    private ApplicationResource application;
    private List<AssessmentApplicationSummaryQuestionViewModel> questions;
    private Integer totalScoreGiven;
    private Integer totalScorePossible;
    private Integer totalScorePercentage;

    public AssessmentApplicationSummaryViewModel(Long assessmentId, long daysLeft, long daysLeftPercentage, CompetitionResource competition, ApplicationResource application, List<AssessmentApplicationSummaryQuestionViewModel> questions, Integer totalScoreGiven, Integer totalScorePossible, Integer totalScorePercentage) {
        this.assessmentId = assessmentId;
        this.daysLeft = daysLeft;
        this.daysLeftPercentage = daysLeftPercentage;
        this.competition = competition;
        this.application = application;
        this.questions = questions;
        this.totalScoreGiven = totalScoreGiven;
        this.totalScorePossible = totalScorePossible;
        this.totalScorePercentage = totalScorePercentage;
    }

    public Long getAssessmentId() {
        return assessmentId;
    }

    public void setAssessmentId(Long assessmentId) {
        this.assessmentId = assessmentId;
    }

    public long getDaysLeft() {
        return daysLeft;
    }

    public void setDaysLeft(long daysLeft) {
        this.daysLeft = daysLeft;
    }

    public long getDaysLeftPercentage() {
        return daysLeftPercentage;
    }

    public void setDaysLeftPercentage(long daysLeftPercentage) {
        this.daysLeftPercentage = daysLeftPercentage;
    }

    public CompetitionResource getCompetition() {
        return competition;
    }

    public void setCompetition(CompetitionResource competition) {
        this.competition = competition;
    }

    public ApplicationResource getApplication() {
        return application;
    }

    public void setApplication(ApplicationResource application) {
        this.application = application;
    }

    public List<AssessmentApplicationSummaryQuestionViewModel> getQuestions() {
        return questions;
    }

    public void setQuestions(List<AssessmentApplicationSummaryQuestionViewModel> questions) {
        this.questions = questions;
    }

    public Integer getTotalScoreGiven() {
        return totalScoreGiven;
    }

    public void setTotalScoreGiven(Integer totalScoreGiven) {
        this.totalScoreGiven = totalScoreGiven;
    }

    public Integer getTotalScorePossible() {
        return totalScorePossible;
    }

    public void setTotalScorePossible(Integer totalScorePossible) {
        this.totalScorePossible = totalScorePossible;
    }

    public Integer getTotalScorePercentage() {
        return totalScorePercentage;
    }

    public void setTotalScorePercentage(Integer totalScorePercentage) {
        this.totalScorePercentage = totalScorePercentage;
    }
}
