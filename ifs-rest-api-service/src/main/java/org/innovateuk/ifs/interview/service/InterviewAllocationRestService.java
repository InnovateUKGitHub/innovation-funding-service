package org.innovateuk.ifs.interview.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.interview.resource.InterviewAcceptedAssessorsPageResource;

/**
 * REST service for allocating application to assessors in interview panels
 */
public interface InterviewAllocationRestService {

    RestResult<InterviewAcceptedAssessorsPageResource> getInterviewAcceptedAssessors(long competitionId, int page);

}
