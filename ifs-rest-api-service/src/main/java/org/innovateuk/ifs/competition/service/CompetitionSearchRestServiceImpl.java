package org.innovateuk.ifs.competition.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.innovateuk.ifs.competition.resource.CompetitionCountResource;
import org.innovateuk.ifs.competition.resource.search.CompetitionSearchResult;
import org.innovateuk.ifs.competition.resource.search.LiveCompetitionSearchResultItem;
import org.innovateuk.ifs.competition.resource.search.UpcomingCompetitionSearchResultItem;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.innovateuk.ifs.commons.service.ParameterizedTypeReferences.liveCompetitionSearchResultItemListType;
import static org.innovateuk.ifs.commons.service.ParameterizedTypeReferences.upcomingCompetitionSearchResultItemListType;

@Service
public class CompetitionSearchRestServiceImpl extends BaseRestService implements CompetitionSearchRestService {

    private String competitionsRestURL = "/competition";

    @Override
    public RestResult<List<LiveCompetitionSearchResultItem>> findLiveCompetitions() {
        return getWithRestResult(competitionsRestURL + "/live", liveCompetitionSearchResultItemListType());
    }

    @Override
    public RestResult<CompetitionSearchResult> findProjectSetupCompetitions(int page) {
        return getWithRestResult(competitionsRestURL + "/project-setup?page="+ page, CompetitionSearchResult.class);
    }

    @Override
    public RestResult<List<UpcomingCompetitionSearchResultItem>> findUpcomingCompetitions() {
        return getWithRestResult(competitionsRestURL + "/upcoming", upcomingCompetitionSearchResultItemListType());
    }

    @Override
    public RestResult<CompetitionSearchResult> findNonIfsCompetitions(int page) {
        return getWithRestResult(competitionsRestURL + "/non-ifs?page="+ page, CompetitionSearchResult.class);
    }

    @Override
    public RestResult<CompetitionSearchResult> findFeedbackReleasedCompetitions(int page) {
        return getWithRestResult(competitionsRestURL +  "/post-submission/feedback-released?page="+ page, CompetitionSearchResult.class);
    }


    @Override
    public RestResult<CompetitionSearchResult> searchCompetitions(String searchQuery, int page) {
        return getWithRestResult(competitionsRestURL + "/search?page=" + page + "&searchQuery=" + searchQuery, CompetitionSearchResult.class);
    }

    @Override
    public RestResult<CompetitionCountResource> countCompetitions() {
        return getWithRestResult(competitionsRestURL + "/count", CompetitionCountResource.class);
    }
}
