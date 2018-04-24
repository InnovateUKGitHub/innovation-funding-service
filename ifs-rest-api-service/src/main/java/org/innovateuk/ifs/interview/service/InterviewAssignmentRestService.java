package org.innovateuk.ifs.interview.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.interview.resource.InterviewAssignmentKeyStatisticsResource;
import org.innovateuk.ifs.invite.resource.*;
import org.springframework.core.io.ByteArrayResource;

import java.util.List;

/**
 * REST service for managing to interview panels
 */
public interface InterviewAssignmentRestService {

    RestResult<AvailableApplicationPageResource> getAvailableApplications(long competitionId, int page);

    RestResult<List<Long>> getAvailableApplicationIds(long competitionId);

    RestResult<Void> assignApplications(StagedApplicationListResource stagedApplicationListResource);

    RestResult<InterviewAssignmentStagedApplicationPageResource> getStagedApplications(long competitionId, int page);

    RestResult<Void> unstageApplication(long applicationId);

    RestResult<Void> unstageApplications(long competitionId);

    RestResult<InterviewAssignmentApplicationPageResource> getAssignedApplications(long competitionId, int page);

    RestResult<ApplicantInterviewInviteResource> getEmailTemplate();

    RestResult<Void> sendAllInvites(long competitionId, AssessorInviteSendResource assessorInviteSendResource);

    RestResult<Boolean> isAssignedToInterview(long applicationId);

    RestResult<InterviewAssignmentKeyStatisticsResource> getKeyStatistics(long competitionId);

    RestResult<Void> uploadFeedback(long applicationId, String contentType, long size, String originalFilename, byte[] multipartFileBytes);

    RestResult<Void> deleteFeedback(long applicationId);

    RestResult<ByteArrayResource> downloadFeedback(long applicationId);

    RestResult<FileEntryResource> findFeedback(long applicationId);
}