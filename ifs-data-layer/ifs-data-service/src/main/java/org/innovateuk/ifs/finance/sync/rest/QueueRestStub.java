package org.innovateuk.ifs.finance.sync.rest;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.finance.resource.sync.FinanceCostTotalResource;
import org.springframework.stereotype.Component;
import java.util.List;

/**
 * Stub to receive Queue Rest jobs. Always returns success for now. Will be implemented fully as part of IFS-xxxx.
 */
@Component
public class QueueRestStub {
    public RestResult<Void> sendFinanceTotals(List<FinanceCostTotalResource> financeCostTotalResourceList) {
        return RestResult.restSuccess();
    }
}
