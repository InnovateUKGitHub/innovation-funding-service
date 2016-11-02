package com.worth.ifs.competition.resource;

public class CompetitionProjectsCountResource {

    private Long competitionId;
    private Integer numProjects;

    public CompetitionProjectsCountResource() {
    }

    public CompetitionProjectsCountResource(Long competitionId, Integer numProjects) {
        this.competitionId = competitionId;
        this.numProjects = numProjects;
    }

    public Long getCompetitionId() {
        return competitionId;
    }

    public void setCompetitionId(Long competitionId) {
        this.competitionId = competitionId;
    }

    public Integer getNumProjects() {
        return numProjects;
    }

    public void setNumProjects(Integer numProjects) {
        this.numProjects = numProjects;
    }
}