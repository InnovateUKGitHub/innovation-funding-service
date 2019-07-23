package org.innovateuk.ifs.management.dashboard.service;

import org.innovateuk.ifs.application.resource.ApplicationPageResource;
import org.innovateuk.ifs.competition.resource.CompetitionCountResource;
import org.innovateuk.ifs.competition.resource.search.*;
import org.innovateuk.ifs.competition.resource.CompetitionStatus;

import java.util.List;
import java.util.Map;

/**
 * Service for the competition dashboard search and tabs.
 */
public interface CompetitionDashboardSearchService {

    Map<CompetitionStatus, List<CompetitionSearchResultItem>> getLiveCompetitions();

    CompetitionSearchResult getProjectSetupCompetitions(int page);

    Map<CompetitionStatus, List<CompetitionSearchResultItem>> getUpcomingCompetitions();

    CompetitionSearchResult getNonIfsCompetitions(int page);

    CompetitionSearchResult getPreviousCompetitions(int page);

    CompetitionSearchResult searchCompetitions(String searchQuery, int page);

    ApplicationPageResource wildcardSearchByApplicationId(String searchString, int pageNumber, int pageSize);

    CompetitionCountResource getCompetitionCounts();


}
