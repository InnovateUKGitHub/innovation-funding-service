package org.innovateuk.ifs.finance.totals.service;

import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.finance.handler.ApplicationFinanceHandler;
import org.innovateuk.ifs.finance.resource.ApplicationFinanceResource;
import org.innovateuk.ifs.finance.resource.totals.FinanceCostTotalResource;
import org.innovateuk.ifs.finance.totals.filter.SpendProfileCostFilter;
import org.innovateuk.ifs.finance.totals.mapper.FinanceCostTotalResourceMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;

/**
 * Service sends cost totals for an {@link Application}.
 */
@Service
public class ApplicationFinanceTotalsSenderImpl implements ApplicationFinanceTotalsSender {

    private static final Logger LOG = LoggerFactory.getLogger(ApplicationFinanceTotalsSenderImpl.class);

    private ApplicationFinanceHandler applicationFinanceHandler;
    private FinanceCostTotalResourceMapper financeCostTotalResourceMapper;
    private SpendProfileCostFilter spendProfileCostFilter;
    private AsyncRestCostTotalEndpoint costTotalEndpoint;

    @Autowired
    public ApplicationFinanceTotalsSenderImpl(
            ApplicationFinanceHandler applicationFinanceHandler,
            FinanceCostTotalResourceMapper financeCostTotalResourceMapper,
            SpendProfileCostFilter spendProfileCostFilter,
            AsyncRestCostTotalEndpoint costTotalEndpoint
    ) {
        this.applicationFinanceHandler = applicationFinanceHandler;
        this.financeCostTotalResourceMapper = financeCostTotalResourceMapper;
        this.spendProfileCostFilter = spendProfileCostFilter;
        this.costTotalEndpoint = costTotalEndpoint;
    }

    @Override
    @Transactional(readOnly = true, propagation = Propagation.REQUIRES_NEW)
    public ServiceResult<Void> sendFinanceTotalsForApplication(Long applicationId) {
        LOG.debug("Initiating sendFinanceTotalsForApplication for applicationId: {}", applicationId);

        List<ApplicationFinanceResource> applicationFinanceResources =
                applicationFinanceHandler.getApplicationFinances(applicationId);

        List<FinanceCostTotalResource> financeCostTotalResourceList = financeCostTotalResourceMapper
                .mapFromApplicationFinanceResourceListToList(applicationFinanceResources);

        if (financeCostTotalResourceList.isEmpty()) {
            LOG.debug("Ignoring empty financeCostTotalResources for applicationId: {}", applicationId);
            return serviceSuccess();
        }

        return costTotalEndpoint.sendCostTotals(applicationId,
                spendProfileCostFilter.filterBySpendProfile(financeCostTotalResourceList));
    }
}
