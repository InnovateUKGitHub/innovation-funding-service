package org.innovateuk.ifs.competition.service;

import org.innovateuk.ifs.application.resource.ApplicationPageResource;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.innovateuk.ifs.competition.resource.*;
import org.innovateuk.ifs.user.resource.OrganisationTypeResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.List;

import static org.innovateuk.ifs.commons.service.ParameterizedTypeReferences.*;

/**
 * CompetitionsRestServiceImpl is a utility for CRUD operations on {@link CompetitionResource}.
 * This class connects to the { org.innovateuk.ifs.competition.controller.CompetitionController}
 * through a REST call.
 */
@Service
public class CompetitionRestServiceImpl extends BaseRestService implements CompetitionRestService {

    private String competitionsRestURL = "/competition";
    private String competitionsTypesRestURL = "/competition-type";

    @Override
    public RestResult<List<CompetitionResource>> getAll() {
        return getWithRestResult(competitionsRestURL + "/findAll", competitionResourceListType());
    }

    @Override
    public RestResult<List<CompetitionResource>> getCompetitionsByUserId(Long userId) {
        return getWithRestResult(competitionsRestURL + "/getCompetitionsByUserId/" + userId, competitionResourceListType());
    }

    @Override
    public RestResult<List<CompetitionSearchResultItem>> findLiveCompetitions() {
        return getWithRestResult(competitionsRestURL + "/live", competitionSearchResultItemListType());
    }

    @Override
    public RestResult<List<CompetitionSearchResultItem>> findProjectSetupCompetitions() {
        return getWithRestResult(competitionsRestURL + "/project-setup", competitionSearchResultItemListType());
    }

    @Override
    public RestResult<List<CompetitionSearchResultItem>> findUpcomingCompetitions() {
        return getWithRestResult(competitionsRestURL + "/upcoming", competitionSearchResultItemListType());
    }

    @Override
    public RestResult<List<CompetitionSearchResultItem>> findNonIfsCompetitions() {
        return getWithRestResult(competitionsRestURL + "/non-ifs", competitionSearchResultItemListType());
    }

    @Override
    public RestResult<ApplicationPageResource> findUnsuccessfulApplications(Long competitionId, int pageNumber, int pageSize, String sortField) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        String uriWithParams = buildPaginationUri(competitionsRestURL +  "/" + competitionId + "/unsuccessful-applications", pageNumber, pageSize, sortField, params);
        return getWithRestResult(uriWithParams, ApplicationPageResource.class);
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
    public RestResult<CompetitionResource> getCompetitionById(long competitionId) {
        return getWithRestResult(competitionsRestURL + "/" + competitionId, CompetitionResource.class);
    }

    @Override
    public RestResult<List<UserResource>> findInnovationLeads(long competitionId) {
        return getWithRestResult(competitionsRestURL + "/" + competitionId + "/innovation-leads", userListType());
    }

    @Override
    public RestResult<Void> addInnovationLead(long competitionId, long innovationLeadUserId) {
        return postWithRestResult(competitionsRestURL + "/" + competitionId + "/add-innovation-lead/" + innovationLeadUserId, Void.class);
    }

    @Override
    public RestResult<Void> removeInnovationLead(long competitionId, long innovationLeadUserId) {
        return postWithRestResult(competitionsRestURL + "/" + competitionId + "/remove-innovation-lead/" + innovationLeadUserId, Void.class);
    }

    @Override
    public RestResult<List<OrganisationTypeResource>> getCompetitionOrganisationType(long competitionId) {
        return getWithRestResultAnonymous(competitionsRestURL + "/" + competitionId + "/getOrganisationTypes", organisationTypeResourceListType());
    }

    @Override
    public RestResult<CompetitionResource> getPublishedCompetitionById(long competitionId) {
        return getWithRestResultAnonymous(competitionsRestURL + "/" + competitionId, CompetitionResource.class);
    }

    @Override
    public RestResult<List<CompetitionTypeResource>> getCompetitionTypes() {
        return getWithRestResult(competitionsTypesRestURL + "/findAll", competitionTypeResourceListType());
    }

    @Override
    public RestResult<Void> closeAssessment(long competitionId) {
        return putWithRestResult(String.format("%s/%s/close-assessment", competitionsRestURL, competitionId), Void.class);
    }
}
