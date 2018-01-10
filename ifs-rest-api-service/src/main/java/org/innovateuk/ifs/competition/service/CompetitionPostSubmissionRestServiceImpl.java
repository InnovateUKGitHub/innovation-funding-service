package org.innovateuk.ifs.competition.service;

import org.innovateuk.ifs.application.resource.ApplicationPageResource;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.innovateuk.ifs.competition.resource.CompetitionOpenQueryResource;
import org.innovateuk.ifs.competition.resource.SpendProfileStatusResource;
import org.innovateuk.ifs.competition.resource.CompetitionSearchResultItem;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.List;

import static org.innovateuk.ifs.commons.service.ParameterizedTypeReferences.competitionOpenQueryResourceListType;
import static org.innovateuk.ifs.commons.service.ParameterizedTypeReferences.competitionSearchResultItemListType;
import static org.innovateuk.ifs.commons.service.ParameterizedTypeReferences.spendProfileStatusResourceListType;

/**
 * Implements {@link CompetitionPostSubmissionRestService}
 */
@Service
public class CompetitionPostSubmissionRestServiceImpl extends BaseRestService implements CompetitionPostSubmissionRestService {

    private String competitionsRestURL = "/competition/postSubmission";

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
    public RestResult<ApplicationPageResource> findUnsuccessfulApplications(Long competitionId, int pageNumber, int pageSize, String sortField) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        String uriWithParams = buildPaginationUri(competitionsRestURL +  "/" + competitionId + "/unsuccessful-applications", pageNumber, pageSize, sortField, params);
        return getWithRestResult(uriWithParams, ApplicationPageResource.class);
    }
}
