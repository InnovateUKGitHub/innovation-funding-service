package org.innovateuk.ifs.assessment.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.innovateuk.ifs.commons.service.ParameterizedTypeReferences;
import org.innovateuk.ifs.invite.resource.*;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

import static java.lang.String.format;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleJoiner;

/**
 * REST service for managing
 */
@Service
public class InterviewPanelInviteRestServiceImpl extends BaseRestService implements InterviewPanelInviteRestService {

    private static final String REST_URL = "/interview-panel-invite";

    @Override
    public RestResult<AvailableApplicationPageResource> getAvailableApplications(long competitionId, int page) {
        String baseUrl = format("%s/%s/%s", REST_URL, "get-available-applications", competitionId);

        UriComponentsBuilder builder = UriComponentsBuilder.fromPath(baseUrl)
                .queryParam("page", page);

        return getWithRestResult(builder.toUriString(), AvailableApplicationPageResource.class);
    }
}