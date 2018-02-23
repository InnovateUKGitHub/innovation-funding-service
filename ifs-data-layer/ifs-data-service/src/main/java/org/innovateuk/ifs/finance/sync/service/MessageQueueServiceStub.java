package org.innovateuk.ifs.finance.sync.service;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.finance.resource.sync.FinanceCostTotalResource;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Stub to receive Queue Rest jobs. Always returns success for now. Will be implemented fully as part of IFS-xxxx.
 */
@Component
public class MessageQueueServiceStub {
    public ServiceResult<Void> sendFinanceTotals(List<FinanceCostTotalResource> financeCostTotalResourceList) {
        return ServiceResult.serviceSuccess();
    }
}
