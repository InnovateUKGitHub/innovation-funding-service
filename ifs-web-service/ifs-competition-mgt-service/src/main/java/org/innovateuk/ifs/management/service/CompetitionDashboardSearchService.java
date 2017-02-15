package org.innovateuk.ifs.management.service;

import org.innovateuk.ifs.competition.resource.CompetitionCountResource;
import org.innovateuk.ifs.competition.resource.CompetitionSearchResult;
import org.innovateuk.ifs.competition.resource.CompetitionSearchResultItem;
import org.innovateuk.ifs.competition.resource.CompetitionStatus;

import java.util.List;
import java.util.Map;

/**
 * Created by luke.harper on 14/02/2017.
 */
public interface CompetitionDashboardSearchService {

    Map<CompetitionStatus, List<CompetitionSearchResultItem>> getLiveCompetitions();

    Map<CompetitionStatus, List<CompetitionSearchResultItem>> getProjectSetupCompetitions();

    Map<CompetitionStatus, List<CompetitionSearchResultItem>> getUpcomingCompetitions();

    List<CompetitionSearchResultItem> getNonIfsCompetitions();

    CompetitionSearchResult searchCompetitions(String searchQuery, int page);

    CompetitionCountResource getCompetitionCounts();

}
