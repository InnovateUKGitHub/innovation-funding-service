package com.worth.ifs.competition.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class CompetitionSetupSectionStatus {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    CompetitionSetupSection competitionSetupSection;
    @ManyToOne
    Competition competition;
    Boolean finished = Boolean.FALSE;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CompetitionSetupSection getCompetitionSetupSection() {
        return competitionSetupSection;
    }

    public void setCompetitionSetupSection(CompetitionSetupSection competitionSetupSection) {
        this.competitionSetupSection = competitionSetupSection;
    }

    public Competition getCompetition() {
        return competition;
    }

    public void setCompetition(Competition competition) {
        this.competition = competition;
    }

    public boolean isFinished() {
        return finished;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }
}

