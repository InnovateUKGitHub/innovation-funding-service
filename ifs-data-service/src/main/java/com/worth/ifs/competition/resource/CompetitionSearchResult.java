package com.worth.ifs.competition.resource;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.worth.ifs.application.resource.PageResource;
import com.worth.ifs.competition.domain.Competition;

import java.util.List;
import java.util.Map;

public class CompetitionSearchResult extends PageResource<CompetitionResource> {

    private Map<CompetitionResource.Status, List<CompetitionResource>> mappedCompetitions;

    @JsonIgnore
    public Map<CompetitionResource.Status, List<CompetitionResource>> getMappedCompetitions() {
        return mappedCompetitions;
    }
    public void setMappedCompetitions(Map<CompetitionResource.Status, List<CompetitionResource>> mappedCompetitions) { this.mappedCompetitions = mappedCompetitions; }
}