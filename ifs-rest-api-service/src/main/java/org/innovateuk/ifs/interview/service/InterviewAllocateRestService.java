package org.innovateuk.ifs.interview.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.interview.resource.InterviewApplicationPageResource;
import org.innovateuk.ifs.interview.resource.InterviewAllocateOverviewPageResource;

/**
 * REST service for allocating application to assessors in interview panels
 */
public interface InterviewAllocateRestService {

    RestResult<InterviewAllocateOverviewPageResource> getAllocateApplicationsOverview(long competitionId, int page);

    RestResult<InterviewApplicationPageResource> getAllocatedApplications(long competitionId, long assessorId, int page);

    RestResult<InterviewApplicationPageResource> getUnallocatedApplications(long competitionId, long assessorId, int page);
}
