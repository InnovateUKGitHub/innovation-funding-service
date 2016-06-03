package com.worth.ifs.competition.resource;

public class CompetitionSetupSectionStatusResource {
    private Long id;
    private Long competitionSetupSection;
    private Long competition;
    private Boolean finished = Boolean.FALSE;

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

    public Boolean getFinished() {
        return finished;
    }

    public void setFinished(Boolean finished) {
        this.finished = finished;
    }
}

