package org.innovateuk.ifs.competition.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.competition.resource.CompetitionCountResource;
import org.innovateuk.ifs.competition.resource.search.*;

import java.util.List;

public interface CompetitionSearchRestService {

    RestResult<List<LiveCompetitionSearchResultItem>> findLiveCompetitions();

    RestResult<CompetitionSearchResult> findProjectSetupCompetitions(int page);

    RestResult<List<UpcomingCompetitionSearchResultItem>> findUpcomingCompetitions();

    RestResult<CompetitionSearchResult> findNonIfsCompetitions(int page);

    RestResult<CompetitionSearchResult> findFeedbackReleasedCompetitions(int page);

    RestResult<CompetitionSearchResult> searchCompetitions(String searchQuery, int page);

    RestResult<CompetitionCountResource> countCompetitions();
}
