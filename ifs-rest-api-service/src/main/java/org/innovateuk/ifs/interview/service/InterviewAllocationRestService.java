package org.innovateuk.ifs.interview.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.interview.resource.InterviewAcceptedAssessorsPageResource;
import org.innovateuk.ifs.interview.resource.InterviewApplicationPageResource;

import java.util.List;

/**
 * REST service for allocating application to assessors in interview panels
 */
public interface InterviewAllocationRestService {

    RestResult<InterviewAcceptedAssessorsPageResource> getInterviewAcceptedAssessors(long competitionId, int page);

    RestResult<InterviewApplicationPageResource> getAllocatedApplications(long competitionId, long assessorId, int page);

    RestResult<InterviewApplicationPageResource> getUnallocatedApplications(long competitionId, long assessorId, int page);

    RestResult<List<Long>> getUnallocatedApplicationIds(long competitionId, long assessorId);

}
