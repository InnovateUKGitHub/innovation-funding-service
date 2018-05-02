package org.innovateuk.ifs.interview.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.innovateuk.ifs.interview.resource.InterviewAssessorAllocateApplicationsPageResource;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import static java.lang.String.format;

/**
 * REST service for allocating application to assessors in interview panels
 */
@Service
public class InterviewAllocateRestServiceImpl extends BaseRestService implements InterviewAllocateRestService {

    private static final String interviewPanelRestUrl = "/interview-panel";

    @Override
    public RestResult<InterviewAssessorAllocateApplicationsPageResource> getAllocateApplicationsOverview(long competitionId, int page) {

        String baseUrl = format("%s/%s/%s", interviewPanelRestUrl, "allocate-overview", competitionId);

        UriComponentsBuilder builder = UriComponentsBuilder.fromPath(baseUrl)
                .queryParam("page", page);

        return getWithRestResult(builder.toUriString(), InterviewAssessorAllocateApplicationsPageResource.class);
    }
}
