package org.innovateuk.ifs.competition.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.innovateuk.ifs.competition.resource.CompetitionCountResource;
import org.innovateuk.ifs.competition.resource.search.*;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.innovateuk.ifs.commons.service.ParameterizedTypeReferences.*;
import static org.innovateuk.ifs.commons.service.ParameterizedTypeReferences.previousCompetitionSearchResultItemListType;

@Service
public class CompetitionSearchRestServiceImpl extends BaseRestService implements CompetitionSearchRestService {

    private String competitionsRestURL = "/competition";

    @Override
    public RestResult<List<LiveCompetitionSearchResultItem>> findLiveCompetitions() {
        return getWithRestResult(competitionsRestURL + "/live", liveCompetitionSearchResultItemListType());
    }

    @Override
    public RestResult<List<ProjectSetupCompetitionSearchResultItem>> findProjectSetupCompetitions() {
        return getWithRestResult(competitionsRestURL + "/project-setup", projectSetupCompetitionSearchResultItemListType());
    }

    @Override
    public RestResult<List<UpcomingCompetitionSearchResultItem>> findUpcomingCompetitions() {
        return getWithRestResult(competitionsRestURL + "/upcoming", upcomingCompetitionSearchResultItemListType());
    }

    @Override
    public RestResult<List<NonIfsCompetitionSearchResultItem>> findNonIfsCompetitions() {
        return getWithRestResult(competitionsRestURL + "/non-ifs", nonIfsCompetitionSearchReultItemListType());
    }

    @Override
    public RestResult<List<PreviousCompetitionSearchResultItem>> findFeedbackReleasedCompetitions() {
        return getWithRestResult(competitionsRestURL +  "/post-submission/feedback-released", previousCompetitionSearchResultItemListType());
    }


    @Override
    public RestResult<CompetitionSearchResult> searchCompetitions(String searchQuery, int page, int size) {
        return getWithRestResult(competitionsRestURL + "/search/" + page + "/" + size + "?searchQuery=" + searchQuery, CompetitionSearchResult.class);
    }

    @Override
    public RestResult<CompetitionCountResource> countCompetitions() {
        return getWithRestResult(competitionsRestURL + "/count", CompetitionCountResource.class);
    }
}
