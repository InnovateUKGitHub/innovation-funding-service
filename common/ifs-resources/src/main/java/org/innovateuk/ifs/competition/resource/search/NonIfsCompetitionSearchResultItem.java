package org.innovateuk.ifs.competition.resource.search;

import org.innovateuk.ifs.competition.resource.CompetitionStatus;

import java.time.ZonedDateTime;
import java.util.Set;

public class NonIfsCompetitionSearchResultItem extends AbstractCompetitionSearchResultItem {

    private ZonedDateTime publishDate;

    private NonIfsCompetitionSearchResultItem() {} //for jackson

    public NonIfsCompetitionSearchResultItem(long id, String name, CompetitionStatus competitionStatus, Set<String> innovationAreaNames, ZonedDateTime publishDate) {
        super(id, name, competitionStatus, null, innovationAreaNames);
        this.publishDate = publishDate;
    }

    public ZonedDateTime getPublishDate() {
        return publishDate;
    }
}
