package org.innovateuk.ifs.assessment.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.invite.resource.AvailableApplicationPageResource;
import org.innovateuk.ifs.invite.resource.InterviewPanelStagedApplicationPageResource;
import org.innovateuk.ifs.invite.resource.StagedApplicationListResource;

import java.util.List;

/**
 * REST service for managing to interview panels
 */
public interface InterviewPanelRestService {

    RestResult<AvailableApplicationPageResource> getAvailableApplications(long competitionId, int page);

    RestResult<List<Long>> getAvailableApplicationIds(long competitionId);

    RestResult<Void> assignApplications(StagedApplicationListResource stagedApplicationListResource);

    RestResult<InterviewPanelStagedApplicationPageResource> getStagedApplications(long competitionId, int page);
}