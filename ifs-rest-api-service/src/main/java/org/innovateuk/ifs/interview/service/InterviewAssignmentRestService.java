package org.innovateuk.ifs.interview.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.interview.resource.InterviewAssignmentKeyStatisticsResource;
import org.innovateuk.ifs.invite.resource.AvailableApplicationPageResource;
import org.innovateuk.ifs.invite.resource.InterviewAssignmentStagedApplicationPageResource;
import org.innovateuk.ifs.invite.resource.StagedApplicationListResource;

import java.util.List;

/**
 * REST service for managing to interview panels
 */
public interface InterviewAssignmentRestService {

    RestResult<AvailableApplicationPageResource> getAvailableApplications(long competitionId, int page);

    RestResult<List<Long>> getAvailableApplicationIds(long competitionId);

    RestResult<Void> assignApplications(StagedApplicationListResource stagedApplicationListResource);

    RestResult<InterviewAssignmentStagedApplicationPageResource> getStagedApplications(long competitionId, int page);

    RestResult<InterviewAssignmentKeyStatisticsResource> getKeyStatistics(long competitionId);
}