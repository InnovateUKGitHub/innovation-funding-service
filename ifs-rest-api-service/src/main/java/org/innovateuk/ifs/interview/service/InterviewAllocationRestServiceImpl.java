package org.innovateuk.ifs.interview.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.innovateuk.ifs.interview.resource.InterviewApplicationPageResource;
import org.innovateuk.ifs.interview.resource.InterviewAcceptedAssessorsPageResource;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

import static java.lang.String.format;
import static org.innovateuk.ifs.commons.service.ParameterizedTypeReferences.longsListType;

/**
 * REST service for allocating application to assessors in interview panels
 */
@Service
public class InterviewAllocationRestServiceImpl extends BaseRestService implements InterviewAllocationRestService {

    private static final String interviewPanelRestUrl = "/interview-panel";

    @Override
    public RestResult<InterviewAcceptedAssessorsPageResource> getInterviewAcceptedAssessors(long competitionId, int page) {

        String baseUrl = format("%s/%s/%s", interviewPanelRestUrl, "allocate-assessors", competitionId);

        UriComponentsBuilder builder = UriComponentsBuilder.fromPath(baseUrl)
                .queryParam("page", page);

        return getWithRestResult(builder.toUriString(), InterviewAcceptedAssessorsPageResource.class);
    }

    @Override
    public RestResult<InterviewApplicationPageResource> getAllocatedApplications(long competitionId, long assessorId, int page) {
        String baseUrl = format("%s/%s/%s/%s", interviewPanelRestUrl, competitionId, "allocated-applications", assessorId);

        UriComponentsBuilder builder = UriComponentsBuilder.fromPath(baseUrl)
                .queryParam("page", page);

        return getWithRestResult(builder.toUriString(), InterviewApplicationPageResource.class);
    }

    @Override
    public RestResult<InterviewApplicationPageResource> getUnallocatedApplications(long competitionId, long assessorId, int page) {
        String baseUrl = format("%s/%s/%s/%s", interviewPanelRestUrl, competitionId, "unallocated-applications", assessorId);

        UriComponentsBuilder builder = UriComponentsBuilder.fromPath(baseUrl)
                .queryParam("page", page);

        return getWithRestResult(builder.toUriString(), InterviewApplicationPageResource.class);
    }

    @Override
    public RestResult<List<Long>> getUnallocatedApplicationIds(long competitionId, long assessorId) {
        String baseUrl = format("%s/%s/%s/%s", interviewPanelRestUrl, competitionId, "unallocated-application-ids", assessorId);

        UriComponentsBuilder builder = UriComponentsBuilder.fromPath(baseUrl);

        return getWithRestResult(builder.toUriString(), longsListType());
    }
}
