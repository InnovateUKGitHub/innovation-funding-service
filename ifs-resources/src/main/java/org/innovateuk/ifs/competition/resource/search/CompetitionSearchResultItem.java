package org.innovateuk.ifs.competition.resource.search;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.innovateuk.ifs.competition.resource.CompetitionStatus;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = UpcomingCompetitionSearchResultItem.class, name = "upcoming"),
        @JsonSubTypes.Type(value = LiveCompetitionSearchResultItem.class, name = "live"),
        @JsonSubTypes.Type(value = NonIfsCompetitionSearchResultItem.class, name = "nonifs"),
        @JsonSubTypes.Type(value = ProjectSetupCompetitionSearchResultItem.class, name = "project"),
        @JsonSubTypes.Type(value = PreviousCompetitionSearchResultItem.class, name = "previous"),
})
public interface CompetitionSearchResultItem {

    long getId();
    CompetitionStatus getCompetitionStatus();
}
