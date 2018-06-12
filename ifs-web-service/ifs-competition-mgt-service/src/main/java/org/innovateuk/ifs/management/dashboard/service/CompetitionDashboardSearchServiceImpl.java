package org.innovateuk.ifs.management.dashboard.service;


import org.innovateuk.ifs.application.resource.ApplicationPageResource;
import org.innovateuk.ifs.application.service.ApplicationRestService;
import org.innovateuk.ifs.competition.resource.CompetitionCountResource;
import org.innovateuk.ifs.competition.resource.CompetitionSearchResult;
import org.innovateuk.ifs.competition.resource.CompetitionSearchResultItem;
import org.innovateuk.ifs.competition.resource.CompetitionStatus;
import org.innovateuk.ifs.competition.service.CompetitionPostSubmissionRestService;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.util.CollectionFunctions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.apache.commons.lang3.tuple.Pair;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.innovateuk.ifs.application.service.CompetitionServiceImpl.COMPETITION_PAGE_SIZE;

/**
 * Implementation of the competition dashboard searches.
 */
@Service
public class CompetitionDashboardSearchServiceImpl implements CompetitionDashboardSearchService {

    @Autowired
    private CompetitionRestService competitionRestService;

    @Autowired
    private ApplicationRestService applicationRestService;

    @Autowired
    private CompetitionPostSubmissionRestService competitionPostSubmissionRestService;


    @Override
    public Map<CompetitionStatus, List<CompetitionSearchResultItem>> getLiveCompetitions() {
        return mapToStatus(competitionRestService.findLiveCompetitions().getSuccess());
    }

    @Override
    public Map<CompetitionStatus, List<CompetitionSearchResultItem>> getProjectSetupCompetitions() {
        List<CompetitionSearchResultItem> projectSetupCompetitions = competitionRestService.findProjectSetupCompetitions().getSuccess();

        return mapToStatus(CollectionFunctions.reverse(projectSetupCompetitions.stream()
                .map(competition -> Pair.of(applicationRestService.getLatestEmailFundingDate(competition.getId()).getSuccess(), competition))
                .sorted(Comparator.comparing(Pair::getKey))
                .map(Pair::getValue)
                .collect(Collectors.toList())));
    }
    @Override
    public Map<CompetitionStatus, List<CompetitionSearchResultItem>> getUpcomingCompetitions() {
        return mapToStatus(competitionRestService.findUpcomingCompetitions().getSuccess());
    }

    @Override
    public Map<CompetitionStatus, List<CompetitionSearchResultItem>> getNonIfsCompetitions() {
        return mapToStatus(competitionRestService.findNonIfsCompetitions().getSuccess());
    }

    @Override
    public Map<CompetitionStatus, List<CompetitionSearchResultItem>> getPreviousCompetitions() {
        return mapToStatus(competitionPostSubmissionRestService.findFeedbackReleasedCompetitions().getSuccess());
    }

    @Override
    public CompetitionSearchResult searchCompetitions(String searchQuery, int page) {
        CompetitionSearchResult searchResult = competitionRestService.searchCompetitions(searchQuery, page, COMPETITION_PAGE_SIZE).getSuccess();
        searchResult.setMappedCompetitions(mapToStatus(searchResult.getContent()));
        return searchResult;
    }

    @Override
    public ApplicationPageResource wildcardSearchByApplicationId(String searchString, int pageNumber, int pageSize) {
        return applicationRestService.wildcardSearchById(searchString, pageNumber, pageSize).getSuccess();
    }

    @Override
    public CompetitionCountResource getCompetitionCounts() {
        return competitionRestService.countCompetitions().getSuccess();
    }

    private Map<CompetitionStatus, List<CompetitionSearchResultItem>> mapToStatus(List<CompetitionSearchResultItem> resources) {
        return resources.stream().collect(Collectors.groupingBy(CompetitionSearchResultItem::getCompetitionStatus));
    }
}
