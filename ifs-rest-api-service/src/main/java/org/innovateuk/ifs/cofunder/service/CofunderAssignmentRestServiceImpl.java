package org.innovateuk.ifs.cofunder.service;

import org.innovateuk.ifs.cofunder.resource.*;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

import static java.lang.String.format;
import static org.innovateuk.ifs.commons.service.ParameterizedTypeReferences.longsListType;
import static org.innovateuk.ifs.commons.service.ParameterizedTypeReferences.cofunderAssignmentResourceListType;

@Service
public class CofunderAssignmentRestServiceImpl extends BaseRestService implements CofunderAssignmentRestService {

    private String cofunderRestUrl = "/cofunder";

    @Override
    public RestResult<CofunderAssignmentResource> getAssignment(long userId, long applicationId) {
        return getWithRestResult(format("%s/assignment/user/%d/application/%d", cofunderRestUrl, userId, applicationId), CofunderAssignmentResource.class);
    }

    @Override
    public RestResult<List<CofunderAssignmentResource>> getAssignmentsByApplicationId(long applicationId) {
        return getWithRestResult(format("%s/assignment/application/%d", cofunderRestUrl, applicationId), cofunderAssignmentResourceListType());
    }

    @Override
    public RestResult<CofunderAssignmentResource> assign(long userId, long applicationId) {
        return postWithRestResult(format("%s/user/%d/application/%d", cofunderRestUrl, userId, applicationId), CofunderAssignmentResource.class);
    }

    @Override
    public RestResult<Void> assign(AssignCofundersResource assignCofundersResource) {
        return postWithRestResult(format("%s/assignment", cofunderRestUrl), assignCofundersResource, Void.class);
    }

    @Override
    public RestResult<Void> removeAssignment(long userId, long applicationId) {
        return deleteWithRestResult(format("%s/user/%d/application/%d", cofunderRestUrl, userId, applicationId));
    }

    @Override
    public RestResult<Void> decision(long assignmentId, CofunderDecisionResource decision) {
        return postWithRestResult(format("%s/assignment/%d/decision", cofunderRestUrl, assignmentId), decision, Void.class);
    }

    @Override
    public RestResult<Void> edit(long assignmentId) {
        return postWithRestResult(format("%s/assignment/%d/edit", cofunderRestUrl, assignmentId), Void.class);
    }

    @Override
    public RestResult<ApplicationsForCofundingPageResource> findApplicationsNeedingCofunders(long competitionId, String filter, int page) {
        String baseUrl = format("%s/competition/%d", cofunderRestUrl, competitionId);

        UriComponentsBuilder builder = UriComponentsBuilder.fromPath(baseUrl)
                .queryParam("page", page)
                .queryParam("filter", filter);
        return getWithRestResult(builder.toUriString(), ApplicationsForCofundingPageResource.class);
    }

    @Override
    public RestResult<CofundersAvailableForApplicationPageResource> findAvailableCofundersForApplication(long applicationId, String filter, int page) {
        String baseUrl = format("%s/application/%d", cofunderRestUrl, applicationId);

        UriComponentsBuilder builder = UriComponentsBuilder.fromPath(baseUrl)
                .queryParam("page", page)
                .queryParam("filter", filter);
        return getWithRestResult(builder.toUriString(), CofundersAvailableForApplicationPageResource.class);
    }

    @Override
    public RestResult<List<Long>> findAllAvailableCofunderUserIdsForApplication(long applicationId, String filter) {
        String baseUrl = format("%s/application/%d/userIds", cofunderRestUrl, applicationId);

        UriComponentsBuilder builder = UriComponentsBuilder.fromPath(baseUrl)
                .queryParam("filter", filter);
        return getWithRestResult(builder.toUriString(), longsListType());
    }
}
