package com.worth.ifs.assessment.domain;

public class AssessmentTotalScore {

    private int totalScoreGiven;
    private int totalScorePossible;

    public AssessmentTotalScore(int totalScoreGiven, int totalScorePossible) {
        this.totalScoreGiven = totalScoreGiven;
        this.totalScorePossible = totalScorePossible;
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
}