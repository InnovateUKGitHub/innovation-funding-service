package com.worth.ifs.assessment.dto;


public class Score {
    private final int totalScore;
    private final int possibleScore;
    private final int scorePercentage;

    public Score(int possibleScore, int totalScore) {
        this.possibleScore = possibleScore;
        this.totalScore = totalScore;
        this.scorePercentage = possibleScore == 0 ? 0 : (totalScore * 100) / possibleScore;
    }

    public int getTotalScore() {
        return totalScore;
    }

    public int getPossibleScore() {
        return possibleScore;
    }

    public int getScorePercentage() {
        return scorePercentage;
    }
}
