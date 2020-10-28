package org.innovateuk.ifs.supporter.service;

import org.innovateuk.ifs.supporter.resource.*;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

import static java.lang.String.format;
import static org.innovateuk.ifs.commons.service.ParameterizedTypeReferences.longsListType;
import static org.innovateuk.ifs.commons.service.ParameterizedTypeReferences.supporterAssignmentResourceListType;
import static org.innovateuk.ifs.util.EncodingUtils.urlEncode;

@Service
public class SupporterAssignmentRestServiceImpl extends BaseRestService implements SupporterAssignmentRestService {

    private String supporterRestUrl = "/supporter";

    @Override
    public RestResult<SupporterAssignmentResource> getAssignment(long userId, long applicationId) {
        return getWithRestResult(format("%s/assignment/user/%d/application/%d", supporterRestUrl, userId, applicationId), SupporterAssignmentResource.class);
    }

    @Override
    public RestResult<List<SupporterAssignmentResource>> getAssignmentsByApplicationId(long applicationId) {
        return getWithRestResult(format("%s/assignment/application/%d", supporterRestUrl, applicationId), supporterAssignmentResourceListType());
    }

    @Override
    public RestResult<SupporterAssignmentResource> assign(long userId, long applicationId) {
        return postWithRestResult(format("%s/user/%d/application/%d", supporterRestUrl, userId, applicationId), SupporterAssignmentResource.class);
    }

    @Override
    public RestResult<Void> assign(AssignSupportersResource assignSupportersResource) {
        return postWithRestResult(format("%s/assignment", supporterRestUrl), assignSupportersResource, Void.class);
    }

    @Override
    public RestResult<Void> removeAssignment(long userId, long applicationId) {
        return deleteWithRestResult(format("%s/user/%d/application/%d", supporterRestUrl, userId, applicationId));
    }

    @Override
    public RestResult<Void> decision(long assignmentId, SupporterDecisionResource decision) {
        return postWithRestResult(format("%s/assignment/%d/decision", supporterRestUrl, assignmentId), decision, Void.class);
    }

    @Override
    public RestResult<Void> edit(long assignmentId) {
        return postWithRestResult(format("%s/assignment/%d/edit", supporterRestUrl, assignmentId), Void.class);
    }

    @Override
    public RestResult<ApplicationsForCofundingPageResource> findApplicationsNeedingSupporters(long competitionId, String filter, int page) {
        String baseUrl = format("%s/competition/%d", supporterRestUrl, competitionId);

        UriComponentsBuilder builder = UriComponentsBuilder.fromPath(baseUrl)
                .queryParam("page", page);
        if (filter != null) {
            builder = builder.queryParam("filter", urlEncode(filter));
        }
        return getWithRestResult(builder.toUriString(), ApplicationsForCofundingPageResource.class);
    }

    @Override
    public RestResult<SupportersAvailableForApplicationPageResource> findAvailableSupportersForApplication(long applicationId, String filter, int page) {
        String baseUrl = format("%s/application/%d", supporterRestUrl, applicationId);

        UriComponentsBuilder builder = UriComponentsBuilder.fromPath(baseUrl)
                .queryParam("page", page);
        if (filter != null) {
            builder = builder.queryParam("filter", urlEncode(filter));
        }
        return getWithRestResult(builder.toUriString(), SupportersAvailableForApplicationPageResource.class);
    }

    @Override
    public RestResult<List<Long>> findAllAvailableSupporterUserIdsForApplication(long applicationId, String filter) {
        String baseUrl = format("%s/application/%d/userIds", supporterRestUrl, applicationId);

        UriComponentsBuilder builder = UriComponentsBuilder.fromPath(baseUrl);

        if (filter != null) {
            builder = builder.queryParam("filter", urlEncode(filter));
        }
        return getWithRestResult(builder.toUriString(), longsListType());
    }
}
