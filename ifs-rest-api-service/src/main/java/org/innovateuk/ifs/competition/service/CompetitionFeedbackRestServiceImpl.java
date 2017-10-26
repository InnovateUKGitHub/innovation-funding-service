package org.innovateuk.ifs.competition.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.innovateuk.ifs.competition.resource.CompetitionOpenQueryResource;
import org.innovateuk.ifs.competition.resource.CompetitionSearchResultItem;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.innovateuk.ifs.commons.service.ParameterizedTypeReferences.competitionOpenQueryResourceListType;
import static org.innovateuk.ifs.commons.service.ParameterizedTypeReferences.competitionSearchResultItemListType;

/**
 * CompetitionsFeedbackRestService TODO
 */
@Service
public class CompetitionFeedbackRestServiceImpl extends BaseRestService implements CompetitionFeedbackRestService {

    private String competitionsRestURL = "/competition/feedback";

    @Override
    public RestResult<Void> notifyAssessors(long competitionId) {
        return putWithRestResult(String.format("%s/%s/notify-assessors", competitionsRestURL, competitionId), Void.class);
    }

    @Override
    public RestResult<Void> releaseFeedback(long competitionId) {
        return putWithRestResult(String.format("%s/%s/release-feedback", competitionsRestURL, competitionId), Void.class);
    }

    @Override
    public RestResult<List<CompetitionSearchResultItem>> findFeedbackReleasedCompetitions() {
        return getWithRestResult(competitionsRestURL +  "/feedback-released", competitionSearchResultItemListType());
    }

    @Override
    public RestResult<List<CompetitionOpenQueryResource>> getCompetitionOpenQueries(long competitionId) {
        return getWithRestResult(competitionsRestURL + "/" + competitionId + "/queries/open", competitionOpenQueryResourceListType());
    }

    @Override
    public RestResult<Long> getCompetitionOpenQueriesCount(long competitionId) {
        return getWithRestResult(competitionsRestURL + "/" + competitionId + "/queries/open/count", Long.class);
    }

}
