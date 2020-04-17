package org.innovateuk.ifs.competition.domain;

import javax.persistence.*;

@Entity
@Table(name = "competition_average_assessor_score_config")
public class CompetitionAverageAssessorScoreConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(mappedBy = "competitionAverageAssessorScoreConfig", fetch = FetchType.LAZY)
    private Competition competition;

    @Column(name = "average_assessor_score")
    private Boolean averageAssessorScore;

    public CompetitionAverageAssessorScoreConfig() {
    }

    public CompetitionAverageAssessorScoreConfig(Competition competition, Boolean averageAssessorScore) {
        this.competition = competition;
        this.averageAssessorScore = averageAssessorScore;
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

    public Boolean getAverageAssessorScore() {
        return averageAssessorScore;
    }

    public void setAverageAssessorScore(Boolean averageAssessorScore) {
        this.averageAssessorScore = averageAssessorScore;
    }
}
