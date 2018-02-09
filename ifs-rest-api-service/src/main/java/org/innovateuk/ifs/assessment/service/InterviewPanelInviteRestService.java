package org.innovateuk.ifs.assessment.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.invite.resource.*;

import java.util.List;

/**
 * REST service for managing invites to interview panels
 */
public interface InterviewPanelInviteRestService {

    RestResult<AvailableApplicationPageResource> getAvailableApplications(long competitionId, int page);
}
