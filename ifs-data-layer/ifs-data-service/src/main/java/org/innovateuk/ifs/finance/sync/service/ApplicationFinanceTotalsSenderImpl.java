package org.innovateuk.ifs.finance.sync.service;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.finance.handler.ApplicationFinanceHandler;
import org.innovateuk.ifs.finance.resource.ApplicationFinanceResource;
import org.innovateuk.ifs.finance.resource.sync.FinanceCostTotalResource;
import org.innovateuk.ifs.finance.sync.filter.SpendProfileCostFilter;
import org.innovateuk.ifs.finance.sync.mapper.FinanceCostTotalResourceMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service sends cost totals for an {@Application}.
 */
@Service
public class ApplicationFinanceTotalsSenderImpl implements ApplicationFinanceTotalsSender {
    private static final Logger LOG = LoggerFactory.getLogger(ApplicationFinanceTotalsSenderImpl.class);

    @Autowired
    private ApplicationFinanceHandler applicationFinanceHandler;

    @Autowired
    private FinanceCostTotalResourceMapper financeCostTotalResourceMapper;

    @Autowired
    private SpendProfileCostFilter spendProfileCostFilter;

    @Autowired
    private MessageQueueServiceStub messageQueueServiceStub;

    @Override
    public ServiceResult<Void> sendFinanceTotalsForApplication(Long applicationId) {
        LOG.info("Initiating sendFinanceTotalsForApplication for applicationId: {}", applicationId);

        List<ApplicationFinanceResource> applicationFinanceResources = applicationFinanceHandler.getApplicationFinances(applicationId);
        List<FinanceCostTotalResource> financeCostTotalResourceList = financeCostTotalResourceMapper
                .mapFromApplicationFinanceResourceListToList(applicationFinanceResources);

        ServiceResult<Void> result = messageQueueServiceStub.sendFinanceTotals(spendProfileCostFilter
                .filterBySpendProfile(financeCostTotalResourceList));
        result.andOnFailure(logErrorOnSend(applicationId));

        return result;
    }

    private Runnable logErrorOnSend(long applicationId) {
        return () -> LOG.error("Failed sending financeTotals for applicationId: {}", applicationId);
    }
}
