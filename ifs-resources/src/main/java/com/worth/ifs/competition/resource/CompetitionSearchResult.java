package com.worth.ifs.competition.resource;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.worth.ifs.application.resource.PageResource;

import java.util.List;
import java.util.Map;

public class CompetitionSearchResult extends PageResource<CompetitionSearchResultItem> {

    private Map<CompetitionStatus, List<CompetitionSearchResultItem>> mappedCompetitions;

    @JsonIgnore
    public Map<CompetitionStatus, List<CompetitionSearchResultItem>> getMappedCompetitions() {
        return mappedCompetitions;
    }

    public void setMappedCompetitions(Map<CompetitionStatus, List<CompetitionSearchResultItem>> mappedCompetitions) { this.mappedCompetitions = mappedCompetitions; }
}