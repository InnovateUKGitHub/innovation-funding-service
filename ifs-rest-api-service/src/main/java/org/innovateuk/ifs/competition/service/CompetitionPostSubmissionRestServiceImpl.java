package org.innovateuk.ifs.competition.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.innovateuk.ifs.competition.resource.CompetitionOpenQueryResource;
import org.innovateuk.ifs.competition.resource.SpendProfileStatusResource;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.innovateuk.ifs.commons.service.ParameterizedTypeReferences.competitionOpenQueryResourceListType;
import static org.innovateuk.ifs.commons.service.ParameterizedTypeReferences.spendProfileStatusResourceListType;

/**
 * Implements {@link CompetitionPostSubmissionRestService}
 */
@Service
public class CompetitionPostSubmissionRestServiceImpl extends BaseRestService implements CompetitionPostSubmissionRestService {

    private String competitionsRestURL = "/competition/post-submission";

    @Override
    public RestResult<Void> releaseFeedback(long competitionId) {
        return putWithRestResult(String.format("%s/%s/release-feedback", competitionsRestURL, competitionId), Void.class);
    }

    @Override
    public RestResult<List<CompetitionOpenQueryResource>> getCompetitionOpenQueries(long competitionId) {
        return getWithRestResult(competitionsRestURL + "/" + competitionId + "/queries/open", competitionOpenQueryResourceListType());
    }

    @Override
    public RestResult<Long> getCompetitionOpenQueriesCount(long competitionId) {
        return getWithRestResult(competitionsRestURL + "/" + competitionId + "/queries/open/count", Long.class);
    }

    @Override
    public RestResult<List<SpendProfileStatusResource>> getPendingSpendProfiles(long competitionId) {
        return getWithRestResult(competitionsRestURL + "/" + competitionId + "/pending-spend-profiles", spendProfileStatusResourceListType());
    }

    @Override
    public RestResult<Long> countPendingSpendProfiles(long competitionId) {
        return getWithRestResult(competitionsRestURL + "/" + competitionId + "/count-pending-spend-profiles", Long.class);
    }

    @Override
    public RestResult<Void> closeAssessment(long competitionId) {
        return putWithRestResult(String.format("%s/%s/close-assessment", competitionsRestURL, competitionId), Void.class);
    }

    @Override
    public RestResult<Void> reopenAssessmentPeriod(long competitionId) {
        return putWithRestResult(String.format("%s/%s/reopen-assessment-period", competitionsRestURL, competitionId), Void.class);
    }
}
