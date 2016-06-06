package com.worth.ifs.competition.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class CompetitionSetupCompletedSection {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    CompetitionSetupSection competitionSetupSection;
    @ManyToOne
    Competition competition;

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

}

