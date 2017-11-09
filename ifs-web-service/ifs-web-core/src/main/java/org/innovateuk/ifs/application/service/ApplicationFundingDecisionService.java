package org.innovateuk.ifs.application.service;

import org.innovateuk.ifs.application.resource.FundingDecision;
import org.innovateuk.ifs.application.resource.FundingNotificationResource;
import org.innovateuk.ifs.commons.security.NotSecured;
import org.innovateuk.ifs.commons.service.ServiceResult;

import java.util.List;
import java.util.Optional;

/**
 * Service for making the decision of which applications will receive funding and which will not for a given competition.
 */
public interface ApplicationFundingDecisionService {

	@NotSecured("Not currently secured")
	ServiceResult<Void> saveApplicationFundingDecisionData(Long competitionId, FundingDecision fundingDecision, List<Long> applicationIds);

	@NotSecured("Not currently secured")
	Optional<FundingDecision> getFundingDecisionForString(String fundingDecisionName);

	@NotSecured("Not currently secured")
	ServiceResult<Void> sendFundingNotifications(FundingNotificationResource fundingNotificationResource);
}
