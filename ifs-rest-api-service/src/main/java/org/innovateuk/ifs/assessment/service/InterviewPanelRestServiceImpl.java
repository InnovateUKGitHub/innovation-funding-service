package org.innovateuk.ifs.assessment.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.innovateuk.ifs.invite.resource.AvailableApplicationPageResource;
import org.innovateuk.ifs.invite.resource.ExistingUserStagedInviteListResource;
import org.innovateuk.ifs.invite.resource.InterviewPanelCreatedInvitePageResource;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

import static java.lang.String.format;
import static org.innovateuk.ifs.commons.service.ParameterizedTypeReferences.longsListType;

/**
 * REST service for managing interview panel invites.
 */
@Service
public class InterviewPanelRestServiceImpl extends BaseRestService implements InterviewPanelRestService {

    private static final String REST_URL = "/interview-panel-invite"; // TODO needs changing

    @Override
    public RestResult<AvailableApplicationPageResource> getAvailableApplications(long competitionId, int page) {
        String baseUrl = format("%s/%s/%s", REST_URL, "available-applications", competitionId);

        UriComponentsBuilder builder = UriComponentsBuilder.fromPath(baseUrl).queryParam("page", page);

        return getWithRestResult(builder.toUriString(), AvailableApplicationPageResource.class);
    }

    @Override
    public RestResult<List<Long>> getAvailableApplicationIds(long competitionId) {
        String baseUrl = format("%s/%s/%s", REST_URL, "available-application-ids", competitionId);

        UriComponentsBuilder builder = UriComponentsBuilder.fromPath(baseUrl);

        return getWithRestResult(builder.toUriString(), longsListType());
    }

    @Override
    public RestResult<Void> assignApplications(ExistingUserStagedInviteListResource existingUserStagedInviteListResource) {
        return postWithRestResult(format("%s/%s", REST_URL, "assign-applications"), existingUserStagedInviteListResource, Void.class);
    }

    @Override
    public RestResult<InterviewPanelCreatedInvitePageResource> getCreatedInvites(long competitionId, int page) {
        String baseUrl = format("%s/%s/%s", REST_URL, "invited-applications", competitionId);

        UriComponentsBuilder builder = UriComponentsBuilder.fromPath(baseUrl).queryParam("page", page);

        return getWithRestResult(builder.toUriString(), InterviewPanelCreatedInvitePageResource.class);
    }
}