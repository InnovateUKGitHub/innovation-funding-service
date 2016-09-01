package com.worth.ifs.assessment.viewmodel;

import com.worth.ifs.application.resource.ApplicationResource;
import com.worth.ifs.competition.resource.CompetitionResource;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.List;

/**
 * Holder of model attributes for the Assessment Summary displayed when a review is requested by the Assessor.
 */
public class AssessmentSummaryViewModel {

    private Long assessmentId;
    private long daysLeft;
    private long daysLeftPercentage;
    private CompetitionResource competition;
    private ApplicationResource application;
    private List<AssessmentSummaryQuestionViewModel> questionsForScoreOverview;
    private List<AssessmentSummaryQuestionViewModel> questionsForReview;
    private int totalScoreGiven;
    private int totalScorePossible;
    private int totalScorePercentage;

    public AssessmentSummaryViewModel(Long assessmentId, long daysLeft, long daysLeftPercentage, CompetitionResource competition, ApplicationResource application, List<AssessmentSummaryQuestionViewModel> questionsForScoreOverview, List<AssessmentSummaryQuestionViewModel> questionsForReview, int totalScoreGiven, int totalScorePossible, int totalScorePercentage) {
        this.assessmentId = assessmentId;
        this.daysLeft = daysLeft;
        this.daysLeftPercentage = daysLeftPercentage;
        this.competition = competition;
        this.application = application;
        this.questionsForScoreOverview = questionsForScoreOverview;
        this.questionsForReview = questionsForReview;
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

    public List<AssessmentSummaryQuestionViewModel> getQuestionsForScoreOverview() {
        return questionsForScoreOverview;
    }

    public void setQuestionsForScoreOverview(List<AssessmentSummaryQuestionViewModel> questionsForScoreOverview) {
        this.questionsForScoreOverview = questionsForScoreOverview;
    }

    public List<AssessmentSummaryQuestionViewModel> getQuestionsForReview() {
        return questionsForReview;
    }

    public void setQuestionsForReview(List<AssessmentSummaryQuestionViewModel> questionsForReview) {
        this.questionsForReview = questionsForReview;
    }

    public int getTotalScoreGiven() {
        return totalScoreGiven;
    }

    public void setTotalScoreGiven(int totalScoreGiven) {
        this.totalScoreGiven = totalScoreGiven;
    }

    public int getTotalScorePossible() {
        return totalScorePossible;
    }

    public void setTotalScorePossible(int totalScorePossible) {
        this.totalScorePossible = totalScorePossible;
    }

    public int getTotalScorePercentage() {
        return totalScorePercentage;
    }

    public void setTotalScorePercentage(int totalScorePercentage) {
        this.totalScorePercentage = totalScorePercentage;
    }

    public Integer getWordsRemaining(Integer maxWordCount, String response) {
        return response.equals("null") ? maxWordCount : maxWordCount - getResponseWords(response);
    }

    private int getResponseWords(String responseValue) {
        // clean any HTML markup from the value
        Document doc = Jsoup.parse(responseValue);
        String cleaned = doc.text();
        return cleaned.split("\\s+").length;
    }
}