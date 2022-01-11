package org.innovateuk.ifs.competition.resource.search;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.innovateuk.ifs.competition.resource.CompetitionStatus;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "clazz")
@JsonSubTypes({
        @JsonSubTypes.Type(value = UpcomingCompetitionSearchResultItem.class, name = "UpcomingCompetitionSearchResultItem"),
        @JsonSubTypes.Type(value = LiveCompetitionSearchResultItem.class, name = "LiveCompetitionSearchResultItem"),
        @JsonSubTypes.Type(value = NonIfsCompetitionSearchResultItem.class, name = "NonIfsCompetitionSearchResultItem"),
        @JsonSubTypes.Type(value = ProjectSetupCompetitionSearchResultItem.class, name = "ProjectSetupCompetitionSearchResultItem"),
        @JsonSubTypes.Type(value = PreviousCompetitionSearchResultItem.class, name = "PreviousCompetitionSearchResultItem"),
})
public interface CompetitionSearchResultItem {

    long getId();
    CompetitionStatus getCompetitionStatus();

    default String getClazz() {
        return this.getClass().getSimpleName();
    }
}
