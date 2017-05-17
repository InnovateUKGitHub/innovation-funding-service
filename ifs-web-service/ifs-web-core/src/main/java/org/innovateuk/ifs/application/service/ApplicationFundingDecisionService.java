package org.innovateuk.ifs.application.service;

import org.innovateuk.ifs.application.resource.FundingDecision;
import org.innovateuk.ifs.application.resource.FundingNotificationResource;
import org.innovateuk.ifs.commons.service.ServiceResult;

import java.util.List;
import java.util.Optional;

/**
 * Service for making the decision of which applications will receive funding and which will not for a given competition.
 */
public interface ApplicationFundingDecisionService {

	ServiceResult<Void> saveApplicationFundingDecisionData(Long competitionId, FundingDecision fundingDecision, List<Long> applicationIds);

	Optional<FundingDecision> getFundingDecisionForString(String fundingDecisionName);

	ServiceResult<Void> sendFundingNotifications(FundingNotificationResource fundingNotificationResource);
}
