package org.innovateuk.ifs.interview.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.interview.resource.*;
import org.innovateuk.ifs.invite.resource.AssessorInvitesToSendResource;

import java.util.List;

/**
 * REST service for allocating applications to assessors in interview panels
 */
public interface InterviewAllocationRestService {

    RestResult<InterviewAcceptedAssessorsPageResource> getInterviewAcceptedAssessors(long competitionId, int page);

    RestResult<InterviewApplicationPageResource> getAllocatedApplications(long competitionId, long assessorId, int page);

    RestResult<List<InterviewResource>> getAllocatedApplicationsByAssessorId(long competitionId, long assessorId);

    RestResult<AssessorInvitesToSendResource> getInviteToSend(long competitionId, long assessorId);

    RestResult<InterviewApplicationPageResource> getUnallocatedApplications(long competitionId, long assessorId, int page);

    RestResult<List<InterviewApplicationResource>> getUnallocatedApplicationsById(long competitionId, List<Long> applicationIds);

    RestResult<List<Long>> getUnallocatedApplicationIds(long competitionId, long assessorId);

    RestResult<Void> notifyAllocations(InterviewNotifyAllocationResource interviewNotifyAllocationResource);

    RestResult<Void> unallocateApplication(long assessorId, long applicationId);
}