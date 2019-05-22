package org.innovateuk.ifs.competition.resource.search;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.innovateuk.ifs.commons.resource.PageResource;
import org.innovateuk.ifs.competition.resource.CompetitionStatus;

import java.util.List;
import java.util.Map;

public class CompetitionSearchResult extends PageResource<AbstractCompetitionSearchResultItem> {

    private Map<CompetitionStatus, List<CompetitionSearchResultItem>> mappedCompetitions;

    @JsonIgnore
    public Map<CompetitionStatus, List<CompetitionSearchResultItem>> getMappedCompetitions() {
        return mappedCompetitions;
    }

    public void setMappedCompetitions(Map<CompetitionStatus, List<CompetitionSearchResultItem>> mappedCompetitions) { this.mappedCompetitions = mappedCompetitions; }
}
