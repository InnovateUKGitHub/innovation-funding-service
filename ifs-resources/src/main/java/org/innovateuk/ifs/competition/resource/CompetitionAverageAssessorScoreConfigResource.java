package org.innovateuk.ifs.competition.resource;

public class CompetitionAverageAssessorScoreConfigResource {

    private Long id;
    private Boolean averageAssessorScore;

    public CompetitionAverageAssessorScoreConfigResource() {
    }

    public CompetitionAverageAssessorScoreConfigResource(Boolean averageAssessorScore) {
        this.averageAssessorScore = averageAssessorScore;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getAverageAssessorScore() {
        return averageAssessorScore;
    }

    public void setAverageAssessorScore(Boolean averageAssessorScore) {
        this.averageAssessorScore = averageAssessorScore;
    }
}
