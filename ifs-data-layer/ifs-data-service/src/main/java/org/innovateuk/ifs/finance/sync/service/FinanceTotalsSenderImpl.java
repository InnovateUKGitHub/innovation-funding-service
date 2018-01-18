package org.innovateuk.ifs.finance.sync.service;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.finance.handler.ApplicationFinanceHandler;
import org.innovateuk.ifs.finance.resource.ApplicationFinanceResource;
import org.innovateuk.ifs.finance.resource.sync.FinanceCostTotalResource;
import org.innovateuk.ifs.finance.sync.mapper.FinanceCostTotalResourceMapper;
import org.innovateuk.ifs.finance.sync.rest.QueueRestStub;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * This service is will send finance totals towards the finance-data-service when notified of a change.
 */
@Service
public class FinanceTotalsSenderImpl implements FinanceTotalsSender {
    @Autowired
    private ApplicationFinanceHandler applicationFinanceHandler;

    @Autowired
    private FinanceCostTotalResourceMapper financeCostTotalResourceMapper;

    @Autowired
    private QueueRestStub queueRestStub;

    public ServiceResult<Void> syncFinanceTotalsForApplication(Long applicationId) {
        List<ApplicationFinanceResource> applicationFinanceResources = applicationFinanceHandler.getApplicationFinances(applicationId);
        List<FinanceCostTotalResource> financeCostTotalResources = financeCostTotalResourceMapper.mapFromApplicationFinanceResourceListToList(applicationFinanceResources);

        return queueRestStub.sendFinanceTotals(financeCostTotalResources).toServiceResult();
    }
}
