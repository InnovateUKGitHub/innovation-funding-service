package org.innovateuk.ifs.fundingdecision.transactional;

import org.innovateuk.ifs.application.resource.FundingNotificationResource;
import org.innovateuk.ifs.commons.service.ServiceResult;

/**
 * Transactional and secure service for making the decision of what applications to fund for a given competition.
 */
public interface ApplicationFundingNotificationBulkService {

	ServiceResult<Void> doit(FundingNotificationResource fundingNotificationResource);
}
