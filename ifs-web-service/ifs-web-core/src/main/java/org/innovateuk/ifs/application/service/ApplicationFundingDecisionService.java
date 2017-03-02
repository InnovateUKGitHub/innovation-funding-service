package org.innovateuk.ifs.application.service;

import org.innovateuk.ifs.application.resource.FundingDecision;
import org.innovateuk.ifs.application.resource.NotificationResource;
import org.innovateuk.ifs.commons.service.ServiceResult;

import java.util.List;
import java.util.Map;

/**
 * Service for making the decision of which applications will receive funding and which will not for a given competition.
 */
public interface ApplicationFundingDecisionService {

	void makeApplicationFundingDecision(Long competitionId, Map<Long, FundingDecision> applicationIdToFundingDecision);

	ServiceResult<Void> sendFundingNotifications(NotificationResource notificationResource);

	boolean verifyAllApplicationsRepresented(Map<String, String[]> parameterMap, List<Long> applicationIds);
	
	Map<Long, FundingDecision> applicationIdToFundingDecisionFromRequestParams(Map<String, String[]> parameterMap, List<Long> applicationIds);

	void saveApplicationFundingDecisionData(Long competitionId, Map<Long, FundingDecision> applicationIdToFundingDecision);
	
	FundingDecision fundingDecisionForString(String val);
}
