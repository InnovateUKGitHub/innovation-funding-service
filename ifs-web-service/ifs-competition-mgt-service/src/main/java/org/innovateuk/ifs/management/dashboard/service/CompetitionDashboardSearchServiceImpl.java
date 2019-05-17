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

import static org.innovateuk.ifs.competition.resource.CompetitionStatus.PREVIOUS;

/**
 * Implementation of the competition dashboard searches.
 */
@Service
public class CompetitionDashboardSearchServiceImpl implements CompetitionDashboardSearchService {

    public static final int COMPETITION_PAGE_SIZE = 20;

    @Autowired
    private CompetitionSearchRestService competitionSearchRestService;

    @Autowired
    private ApplicationRestService applicationRestService;

    @Override
    public Map<CompetitionStatus, List<CompetitionSearchResultItem>> getLiveCompetitions() {
        return mapToStatus(competitionSearchRestService.findLiveCompetitions().getSuccess());
    }

    @Override
    public Map<CompetitionStatus, List<CompetitionSearchResultItem>> getProjectSetupCompetitions() {
        return mapToStatus(competitionSearchRestService.findProjectSetupCompetitions().getSuccess());
    }
    @Override
    public Map<CompetitionStatus, List<CompetitionSearchResultItem>> getUpcomingCompetitions() {
        return mapToStatus(competitionSearchRestService.findUpcomingCompetitions().getSuccess());
    }

    @Override
    public Map<CompetitionStatus, List<CompetitionSearchResultItem>> getNonIfsCompetitions() {
        return mapToStatus(competitionSearchRestService.findNonIfsCompetitions().getSuccess());
    }

    @Override
    public Map<CompetitionStatus, List<CompetitionSearchResultItem>> getPreviousCompetitions() {
        return mapToPrevious(competitionSearchRestService.findFeedbackReleasedCompetitions().getSuccess());
    }

    @Override
    public CompetitionSearchResult searchCompetitions(String searchQuery, int page) {
        CompetitionSearchResult searchResult = competitionSearchRestService.searchCompetitions(searchQuery, page, COMPETITION_PAGE_SIZE).getSuccess();
        searchResult.setMappedCompetitions(mapToStatus(searchResult.getContent()));
        return searchResult;
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

    private <T extends CompetitionSearchResultItem> Map<CompetitionStatus, List<CompetitionSearchResultItem>> mapToPrevious(List<T> resources) {
        return resources.stream().collect(Collectors.groupingBy(CompetitionStatus -> PREVIOUS));
    }
}
