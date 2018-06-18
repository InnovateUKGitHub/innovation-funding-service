package org.innovateuk.ifs.management.dashboard.service;

import org.innovateuk.ifs.application.resource.ApplicationPageResource;
import org.innovateuk.ifs.competition.resource.CompetitionCountResource;
import org.innovateuk.ifs.competition.resource.CompetitionSearchResult;
import org.innovateuk.ifs.competition.resource.CompetitionSearchResultItem;
import org.innovateuk.ifs.competition.resource.CompetitionStatus;

import java.util.List;
import java.util.Map;

/**
 * Service for the competition dashboard search and tabs.
 */
public interface CompetitionDashboardSearchService {

    Map<CompetitionStatus, List<CompetitionSearchResultItem>> getLiveCompetitions();

    Map<CompetitionStatus, List<CompetitionSearchResultItem>> getProjectSetupCompetitions();

    Map<CompetitionStatus, List<CompetitionSearchResultItem>> getUpcomingCompetitions();

    Map<CompetitionStatus, List<CompetitionSearchResultItem>> getNonIfsCompetitions();

    CompetitionSearchResult searchCompetitions(String searchQuery, int page);

    ApplicationPageResource wildcardSearchByApplicationId(String searchString, int pageNumber, int pageSize);

    CompetitionCountResource getCompetitionCounts();

    Map<CompetitionStatus, List<CompetitionSearchResultItem>> getPreviousCompetitions();

}
