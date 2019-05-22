package org.innovateuk.ifs.competition.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.competition.resource.CompetitionCountResource;
import org.innovateuk.ifs.competition.resource.search.*;

import java.util.List;

public interface CompetitionSearchRestService {

    RestResult<List<LiveCompetitionSearchResultItem>> findLiveCompetitions();

    RestResult<List<ProjectSetupCompetitionSearchResultItem>> findProjectSetupCompetitions();

    RestResult<List<UpcomingCompetitionSearchResultItem>> findUpcomingCompetitions();

    RestResult<List<NonIfsCompetitionSearchResultItem>> findNonIfsCompetitions();

    RestResult<List<PreviousCompetitionSearchResultItem>> findFeedbackReleasedCompetitions();

    RestResult<CompetitionSearchResult> searchCompetitions(String searchQuery, int page, int size);

    RestResult<CompetitionCountResource> countCompetitions();
}
