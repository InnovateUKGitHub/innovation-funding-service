package org.innovateuk.ifs.application.service;

import org.innovateuk.ifs.application.resource.Decision;
import org.innovateuk.ifs.application.resource.ApplicationDecisionToSendApplicationResource;
import org.innovateuk.ifs.application.resource.FundingNotificationResource;
import org.innovateuk.ifs.commons.rest.RestResult;

import java.util.List;
import java.util.Map;

/**
 * Interface for the action for the funding decisions.
 */
public interface ApplicationDecisionRestService {

    RestResult<Void> saveApplicationDecisionData(Long competitionId, Map<Long, Decision> applicationIdToDecision);

    RestResult<Void> sendApplicationDecisions(FundingNotificationResource fundingNotificationResource);

    RestResult<List<ApplicationDecisionToSendApplicationResource>> getNotificationResourceForApplications(List<Long> applicationIds);

}
