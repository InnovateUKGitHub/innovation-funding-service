package org.innovateuk.ifs.interview.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.innovateuk.ifs.interview.resource.*;
import org.innovateuk.ifs.invite.resource.AssessorInvitesToSendResource;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

import static java.lang.String.format;
import static org.apache.commons.lang3.StringUtils.join;
import static org.innovateuk.ifs.commons.service.ParameterizedTypeReferences.*;

/**
 * REST service for allocating application to assessors in interview panels
 */
@Service
public class InterviewAllocationRestServiceImpl extends BaseRestService implements InterviewAllocationRestService {

    private static final String INTERVIEW_PANEL_REST_URL = "/interview-panel";

    @Override
    public RestResult<InterviewAcceptedAssessorsPageResource> getInterviewAcceptedAssessors(long competitionId, int page) {

        String baseUrl = format("%s/%s/%s", INTERVIEW_PANEL_REST_URL, "allocate-assessors", competitionId);

        UriComponentsBuilder builder = UriComponentsBuilder.fromPath(baseUrl)
                .queryParam("page", page);

        return getWithRestResult(builder.toUriString(), InterviewAcceptedAssessorsPageResource.class);
    }

    @Override
    public RestResult<InterviewApplicationPageResource> getAllocatedApplications(long competitionId, long assessorId, int page) {
        String baseUrl = format("%s/%s/%s/%s", INTERVIEW_PANEL_REST_URL, competitionId, "allocated-applications", assessorId);

        UriComponentsBuilder builder = UriComponentsBuilder.fromPath(baseUrl)
                .queryParam("page", page);

        return getWithRestResult(builder.toUriString(), InterviewApplicationPageResource.class);
    }

    @Override
    public RestResult<List<InterviewResource>> getAllocatedApplicationsByAssessorId(long competitionId, long assessorId) {
        String baseUrl = format("%s/%s/%s/%s", INTERVIEW_PANEL_REST_URL, competitionId, "allocated-applications-assessor-id", assessorId);

        return getWithRestResult(baseUrl,  interviewResourceListType());
    }

    @Override
    public RestResult<List<InterviewApplicationResource>> getUnallocatedApplicationsById(long competitionId, List<Long> applicationIds) {
        String baseUrl = format("%s/%d/%s/%s/%s", INTERVIEW_PANEL_REST_URL, competitionId, "unallocated-applications", "all", join(applicationIds, ','));

        return getWithRestResult(baseUrl, interviewApplicationsResourceListType());
    }

    @Override
    public RestResult<AssessorInvitesToSendResource> getInviteToSend(long competitionId, long assessorId) {
        String baseUrl = format("%s/%d/%s/%s/%s", INTERVIEW_PANEL_REST_URL, competitionId, "allocated-applications", assessorId, "invite-to-send");
        return getWithRestResult(baseUrl, AssessorInvitesToSendResource.class);
    }


    @Override
    public RestResult<Void> notifyAllocations(InterviewNotifyAllocationResource interviewNotifyAllocationResource) {
        String baseUrl = format("%s/%d/%s/%s/%s", INTERVIEW_PANEL_REST_URL,
                interviewNotifyAllocationResource.getCompetitionId(), "allocated-applications",
                interviewNotifyAllocationResource.getAssessorId(), "send-invite");

        return postWithRestResult(baseUrl, interviewNotifyAllocationResource, Void.class);
    }

    @Override
    public RestResult<Void> unallocateApplication(long assessorId, long applicationId) {
        String baseUrl = format("%s/%s/%d/%s/%d", INTERVIEW_PANEL_REST_URL,
                "allocated-applications",
                assessorId, "unallocate",
                applicationId);

        return postWithRestResult(baseUrl, Void.class);
    }

    @Override
    public RestResult<InterviewApplicationPageResource> getUnallocatedApplications(long competitionId, long assessorId, int page) {
        String baseUrl = format("%s/%s/%s/%s", INTERVIEW_PANEL_REST_URL, competitionId, "unallocated-applications", assessorId);

        UriComponentsBuilder builder = UriComponentsBuilder.fromPath(baseUrl)
                .queryParam("page", page);

        return getWithRestResult(builder.toUriString(), InterviewApplicationPageResource.class);
    }

    @Override
    public RestResult<List<Long>> getUnallocatedApplicationIds(long competitionId, long assessorId) {
        String baseUrl = format("%s/%s/%s/%s", INTERVIEW_PANEL_REST_URL, competitionId, "unallocated-application-ids", assessorId);

        UriComponentsBuilder builder = UriComponentsBuilder.fromPath(baseUrl);

        return getWithRestResult(builder.toUriString(), longsListType());
    }
}