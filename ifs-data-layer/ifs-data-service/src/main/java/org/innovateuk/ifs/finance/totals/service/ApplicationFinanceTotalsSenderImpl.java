package org.innovateuk.ifs.finance.totals.service;

import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.finance.handler.ApplicationFinanceHandler;
import org.innovateuk.ifs.finance.resource.ApplicationFinanceResource;
import org.innovateuk.ifs.finance.resource.totals.FinanceCostTotalResource;
import org.innovateuk.ifs.finance.totals.filter.SpendProfileCostFilter;
import org.innovateuk.ifs.finance.totals.mapper.FinanceCostTotalResourceMapper;
import org.innovateuk.ifs.finance.totals.queue.CostTotalMessageQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service sends cost totals for an {@link Application}.
 */
@Service
public class ApplicationFinanceTotalsSenderImpl implements ApplicationFinanceTotalsSender {

    private static final Logger LOG = LoggerFactory.getLogger(ApplicationFinanceTotalsSenderImpl.class);

    private ApplicationFinanceHandler applicationFinanceHandler;
    private FinanceCostTotalResourceMapper financeCostTotalResourceMapper;
    private SpendProfileCostFilter spendProfileCostFilter;
    private CostTotalMessageQueue messageQueueService;

    @Autowired
    public ApplicationFinanceTotalsSenderImpl(
            ApplicationFinanceHandler applicationFinanceHandler,
            FinanceCostTotalResourceMapper financeCostTotalResourceMapper,
            SpendProfileCostFilter spendProfileCostFilter,
            CostTotalMessageQueue messageQueueService
    ) {
        this.applicationFinanceHandler = applicationFinanceHandler;
        this.financeCostTotalResourceMapper = financeCostTotalResourceMapper;
        this.spendProfileCostFilter = spendProfileCostFilter;
        this.messageQueueService = messageQueueService;
    }

    @Override
    public ServiceResult<Void> sendFinanceTotalsForApplication(Long applicationId) {
        LOG.debug("Initiating sendFinanceTotalsForApplication for applicationId: {}", applicationId);

        List<ApplicationFinanceResource> applicationFinanceResources =
                applicationFinanceHandler.getApplicationFinances(applicationId);

        List<FinanceCostTotalResource> financeCostTotalResourceList = financeCostTotalResourceMapper
                .mapFromApplicationFinanceResourceListToList(applicationFinanceResources);

        return messageQueueService
                .sendCostTotals(spendProfileCostFilter.filterBySpendProfile(financeCostTotalResourceList))
                .andOnFailure(() -> LOG.error(
                        "Failed sending financeCostTotalResources for applicationId: {}",
                        applicationId
                ));
    }
}
