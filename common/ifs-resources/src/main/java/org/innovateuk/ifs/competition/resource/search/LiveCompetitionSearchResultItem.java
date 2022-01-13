package org.innovateuk.ifs.competition.resource.search;

import org.innovateuk.ifs.competition.resource.CompetitionStatus;

import java.util.Set;

/**
 * A summary of competition information displayed during competition search
 */
public class LiveCompetitionSearchResultItem extends AbstractCompetitionSearchResultItem {

    private int numberOfApplications;

    private LiveCompetitionSearchResultItem() {} //for jackson

    public LiveCompetitionSearchResultItem(long id, String name, CompetitionStatus competitionStatus, String competitionTypeName, Set<String> innovationAreaNames, int numberOfApplications) {
        super(id, name, competitionStatus, competitionTypeName, innovationAreaNames);
        this.numberOfApplications = numberOfApplications;
    }

    public int getNumberOfApplications() {
        return numberOfApplications;
    }

}