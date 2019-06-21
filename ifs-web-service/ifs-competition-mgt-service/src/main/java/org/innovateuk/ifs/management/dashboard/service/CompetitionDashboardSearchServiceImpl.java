package org.innovateuk.ifs.management.dashboard.service;


import org.innovateuk.ifs.application.resource.ApplicationPageResource;
import org.innovateuk.ifs.application.service.ApplicationRestService;
import org.innovateuk.ifs.competition.resource.CompetitionCountResource;
import org.innovateuk.ifs.competition.resource.CompetitionStatus;
import org.innovateuk.ifs.competition.resource.search.CompetitionSearchResult;
import org.innovateuk.ifs.competition.resource.search.CompetitionSearchResultItem;
import org.innovateuk.ifs.competition.service.CompetitionSearchRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Implementation of the competition dashboard searches.
 */
@Service
public class CompetitionDashboardSearchServiceImpl implements CompetitionDashboardSearchService {

    @Autowired
    private CompetitionSearchRestService competitionSearchRestService;

    @Autowired
    private ApplicationRestService applicationRestService;

    @Override
    public Map<CompetitionStatus, List<CompetitionSearchResultItem>> getLiveCompetitions() {
        return mapToStatus(competitionSearchRestService.findLiveCompetitions().getSuccess());
    }

    @Override
    public CompetitionSearchResult getProjectSetupCompetitions(int page) {
        return competitionSearchRestService.findProjectSetupCompetitions(page).getSuccess();
    }

    @Override
    public Map<CompetitionStatus, List<CompetitionSearchResultItem>> getUpcomingCompetitions() {
        return mapToStatus(competitionSearchRestService.findUpcomingCompetitions().getSuccess());
    }

    @Override
    public CompetitionSearchResult getNonIfsCompetitions(int page) {
        return competitionSearchRestService.findNonIfsCompetitions(page).getSuccess();
    }

    @Override
    public CompetitionSearchResult getPreviousCompetitions(int page) {
        return competitionSearchRestService.findFeedbackReleasedCompetitions(page).getSuccess();
    }

    @Override
    public CompetitionSearchResult searchCompetitions(String searchQuery, int page) {
        return competitionSearchRestService.searchCompetitions(searchQuery, page).getSuccess();
    }

    @Override
    public ApplicationPageResource wildcardSearchByApplicationId(String searchString, int pageNumber, int pageSize) {
        return applicationRestService.wildcardSearchById(searchString, pageNumber, pageSize).getSuccess();
    }

    @Override
    public CompetitionCountResource getCompetitionCounts() {
        return competitionSearchRestService.countCompetitions().getSuccess();
    }

    private <T extends CompetitionSearchResultItem> Map<CompetitionStatus, List<CompetitionSearchResultItem>> mapToStatus(List<T> resources) {
        return resources.stream().collect(Collectors.groupingBy(CompetitionSearchResultItem::getCompetitionStatus));
    }
}