package com.worth.ifs.competition.service;

import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.commons.service.BaseRestService;
import com.worth.ifs.competition.domain.Competition;
import com.worth.ifs.competition.resource.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

import static com.worth.ifs.commons.service.ParameterizedTypeReferences.competitionResourceListType;
import static com.worth.ifs.commons.service.ParameterizedTypeReferences.competitionSearchResultItemListType;
import static com.worth.ifs.commons.service.ParameterizedTypeReferences.competitionTypeResourceListType;

/**
 * CompetitionsRestServiceImpl is a utility for CRUD operations on {@link Competition}.
 * This class connects to the {@link com.worth.ifs.competition.controller.CompetitionController}
 * through a REST call.
 */
@Service
public class CompetitionsRestServiceImpl extends BaseRestService implements CompetitionsRestService {

    @SuppressWarnings("unused")
    private static final Log LOG = LogFactory.getLog(CompetitionsRestServiceImpl.class);
    private String competitionsRestURL = "/competition";
    private String competitionsTypesRestURL = "/competition-type";

    @Override
    public RestResult<List<CompetitionResource>> getAll() {
        return getWithRestResult(competitionsRestURL + "/findAll", competitionResourceListType());
    }

    @Override
    public RestResult<List<CompetitionSearchResultItem>> findLiveCompetitions() {
        return getWithRestResult(competitionsRestURL + "/live", competitionSearchResultItemListType());
    }

    @Override
    public RestResult<List<CompetitionSearchResultItem>> findProjectSetupCompetitions() {
        return getWithRestResult(competitionsRestURL + "/projectSetup", competitionSearchResultItemListType());
    }

    @Override
    public RestResult<List<CompetitionSearchResultItem>> findUpcomingCompetitions() {
        return getWithRestResult(competitionsRestURL + "/upcoming", competitionSearchResultItemListType());
    }

    @Override
    public RestResult<CompetitionSearchResult> searchCompetitions(String searchQuery, int page, int size) {
        return getWithRestResult(competitionsRestURL + "/search/" + page + "/" + size + "?searchQuery=" + searchQuery, CompetitionSearchResult.class);
    }

    @Override
    public RestResult<CompetitionCountResource> countCompetitions() {
        return getWithRestResult(competitionsRestURL + "/count", CompetitionCountResource.class);
    }

    @Override
    public RestResult<CompetitionResource> getCompetitionById(Long competitionId) {
        return getWithRestResult(competitionsRestURL + "/" + competitionId, CompetitionResource.class);
    }

    @Override
    public RestResult<List<CompetitionTypeResource>> getCompetitionTypes() {
        return getWithRestResult(competitionsTypesRestURL + "/findAll", competitionTypeResourceListType());
    }

    @Override
    public RestResult<CompetitionResource> create() {
        return postWithRestResult(competitionsRestURL + "", CompetitionResource.class);
    }


    @Override
    public RestResult<Void> update(CompetitionResource competition) {
        return putWithRestResult(competitionsRestURL + "/" + competition.getId(), competition, Void.class);
    }

    @Override
    public RestResult<Void> markSectionComplete(Long competitionId, CompetitionSetupSection section) {
        return getWithRestResult(String.format("%s/sectionStatus/complete/%s/%s", competitionsRestURL, competitionId, section), Void.class);
    }

    @Override
    public RestResult<Void> markSectionInComplete(Long competitionId, CompetitionSetupSection section) {
        return getWithRestResult(String.format("%s/sectionStatus/incomplete/%s/%s", competitionsRestURL, competitionId, section), Void.class);
    }

    @Override
    public RestResult<String> generateCompetitionCode(Long competitionId, LocalDateTime openingDate) {
        return postWithRestResult(String.format("%s/generateCompetitionCode/%s", competitionsRestURL, competitionId), openingDate, String.class);
    }

    @Override
    public RestResult<Void> initApplicationForm(Long competitionId, Long competitionTypeId) {
        return postWithRestResult(String.format("%s/%s/initialise-form/%s", competitionsRestURL, competitionId, competitionTypeId), Void.class);
    }

    @Override
    public RestResult<Void> markAsSetup(Long competitionId) {
        return postWithRestResult(String.format("%s/%s/mark-as-setup", competitionsRestURL, competitionId), Void.class);
    }

    @Override
    public RestResult<Void> returnToSetup(Long competitionId) {
        return postWithRestResult(String.format("%s/%s/return-to-setup", competitionsRestURL, competitionId), Void.class);
    }

}