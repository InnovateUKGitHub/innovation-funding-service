package org.innovateuk.ifs.competition.domain;

import javax.persistence.*;

@Entity
@Table(name = "competition_external_config")
public class CompetitionExternalConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(mappedBy = "competitionExternalConfig",fetch = FetchType.LAZY)
    private Competition competition;

    private String externalCompetitionId;

    public CompetitionExternalConfig() {

    }

    public CompetitionExternalConfig(Competition competition, String externalCompetitionId) {
        this.competition = competition;
        this.externalCompetitionId = externalCompetitionId;
    }

    public Competition getCompetition() {
        return competition;
    }

    public void setCompetition(Competition competition) {
        this.competition = competition;
    }

    public String getExternalCompetitionId() {
        return externalCompetitionId;
    }

    public void setExternalCompetitionId(String externalCompetitionId) {
        this.externalCompetitionId = externalCompetitionId;
    }
}

