package org.innovateuk.ifs.interview.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.interview.resource.InterviewApplicationSentInviteResource;
import org.innovateuk.ifs.invite.resource.*;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

import static java.lang.String.format;
import static org.innovateuk.ifs.commons.service.ParameterizedTypeReferences.longsListType;

/**
 * REST service for managing interview panel invites.
 */
@Service
public class InterviewAssignmentRestServiceImpl extends BaseRestService implements InterviewAssignmentRestService {

    private static final String REST_URL = "/interview-panel";

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
    public RestResult<Void> assignApplications(StagedApplicationListResource stagedApplicationListResource) {
        return postWithRestResult(format("%s/%s", REST_URL, "assign-applications"), stagedApplicationListResource, Void.class);
    }

    @Override
    public RestResult<InterviewAssignmentStagedApplicationPageResource> getStagedApplications(long competitionId, int page) {
        String baseUrl = format("%s/%s/%s", REST_URL, "staged-applications", competitionId);

        UriComponentsBuilder builder = UriComponentsBuilder.fromPath(baseUrl).queryParam("page", page);

        return getWithRestResult(builder.toUriString(), InterviewAssignmentStagedApplicationPageResource.class);
    }

    @Override
    public RestResult<InterviewAssignmentApplicationPageResource> getAssignedApplications(long competitionId, int page) {
        String baseUrl = format("%s/%s/%s", REST_URL, "assigned-applications", competitionId);

        UriComponentsBuilder builder = UriComponentsBuilder.fromPath(baseUrl).queryParam("page", page);

        return getWithRestResult(builder.toUriString(), InterviewAssignmentApplicationPageResource.class);
    }

    @Override
    public RestResult<Void> unstageApplication(long applicationId) {
        return postWithRestResult(format("%s/%s/%s", REST_URL, "unstage-application", applicationId), Void.class);
    }

    @Override
    public RestResult<Void> unstageApplications(long competitionId) {
        return postWithRestResult(format("%s/%s/%s", REST_URL, "unstage-applications", competitionId), Void.class);
    }

    @Override
    public RestResult<ApplicantInterviewInviteResource> getEmailTemplate() {
        String baseUrl = format("%s/%s", REST_URL, "email-template");

        UriComponentsBuilder builder = UriComponentsBuilder.fromPath(baseUrl);

        return getWithRestResult(builder.toUriString(), ApplicantInterviewInviteResource.class);
    }

    @Override
    public RestResult<Void> sendAllInvites(long competitionId, AssessorInviteSendResource assessorInviteSendResource) {
        return postWithRestResult(format("%s/%s/%s", REST_URL, "send-invites", competitionId), assessorInviteSendResource, Void.class);
    }

    @Override
    public RestResult<Boolean> isAssignedToInterview(long applicationId) {
        String baseUrl = format("%s/%s/%s", REST_URL, "is-assigned", applicationId);

        UriComponentsBuilder builder = UriComponentsBuilder.fromPath(baseUrl);

        return getWithRestResult(builder.toUriString(), Boolean.class);
    }

    @Override
    public RestResult<Void> uploadFeedback(long applicationId, String contentType, long size, String originalFilename, byte[] multipartFileBytes) {
        String url = format("%s/%s/%s?filename=%s", REST_URL, "feedback", applicationId, originalFilename);
        return postWithRestResult(url, multipartFileBytes, createFileUploadHeader(contentType, size), Void.class);
    }

    @Override
    public RestResult<Void> deleteFeedback(long applicationId) {
        String url = format("%s/%s/%s", REST_URL, "feedback", applicationId);
        return deleteWithRestResult(url);
    }

    @Override
    public RestResult<ByteArrayResource> downloadFeedback(long applicationId) {
        String url = format("%s/%s/%s", REST_URL, "feedback", applicationId);
        return getWithRestResult(url, ByteArrayResource.class);
    }

    @Override
    public RestResult<FileEntryResource> findFeedback(long applicationId) {
        String url = format("%s/%s/%s", REST_URL, "feedback-details", applicationId);
        return getWithRestResult(url, FileEntryResource.class);
    }

    @Override
    public RestResult<InterviewApplicationSentInviteResource> getSentInvite(long applicationId) {
        String url = format("%s/%s/%s", REST_URL, "sent-invite", applicationId);
        return getWithRestResult(url, InterviewApplicationSentInviteResource.class);
    }

    @Override
    public RestResult<Void> resendInvite(long applicationId, AssessorInviteSendResource assessorInviteSendResource) {
        return postWithRestResult(format("%s/%s/%s", REST_URL, "resend-invite", applicationId), assessorInviteSendResource, Void.class);
    }
}