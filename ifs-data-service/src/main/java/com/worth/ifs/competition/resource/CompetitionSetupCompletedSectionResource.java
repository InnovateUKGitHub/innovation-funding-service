package com.worth.ifs.competition.resource;

public class CompetitionSetupCompletedSectionResource {
    private Long id;
    private Long competitionSetupSection;
    private Long competition;

    public CompetitionSetupCompletedSectionResource() {
    }

    public CompetitionSetupCompletedSectionResource(Long id, Long competitionSetupSection, Long competition) {
        this.id = id;
        this.competitionSetupSection = competitionSetupSection;
        this.competition = competition;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCompetitionSetupSection() {
        return competitionSetupSection;
    }

    public void setCompetitionSetupSection(Long competitionSetupSection) {
        this.competitionSetupSection = competitionSetupSection;
    }

    public Long getCompetition() {
        return competition;
    }

    public void setCompetition(Long competition) {
        this.competition = competition;
    }
}

