package org.innovateuk.ifs.finance.sync.service;

import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.resource.ApplicationState;
import org.innovateuk.ifs.application.transactional.ApplicationService;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.finance.handler.ApplicationFinanceHandler;
import org.innovateuk.ifs.finance.resource.ApplicationFinanceResource;
import org.innovateuk.ifs.finance.resource.sync.FinanceCostTotalResource;
import org.innovateuk.ifs.finance.sync.mapper.FinanceCostTotalResourceMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.EnumSet;
import java.util.List;

/**
 * This service is will send finance totals towards the finance-data-service when notified of a change.
 */
@Service
public class FinanceTotalsSenderImpl implements FinanceTotalsSender {

    private EnumSet<ApplicationState> submitted = EnumSet.of(ApplicationState.SUBMITTED,
            ApplicationState.INELIGIBLE,
            ApplicationState.APPROVED,
            ApplicationState.REJECTED,
            ApplicationState.INELIGIBLE_INFORMED);

    @Autowired
    private ApplicationFinanceHandler applicationFinanceHandler;

    @Autowired
    private FinanceCostTotalResourceMapper financeCostTotalResourceMapper;

    @Autowired
    private MessageQueueServiceStub messageQueueServiceStub;

    @Autowired
    private ApplicationService applicationService;

    public ServiceResult<Void> sendFinanceTotalsForApplication(Long applicationId) {
        List<ApplicationFinanceResource> applicationFinanceResources = applicationFinanceHandler.getApplicationFinances(applicationId);
        List<FinanceCostTotalResource> financeCostTotalResources = financeCostTotalResourceMapper.mapFromApplicationFinanceResourceListToList(applicationFinanceResources);

        return messageQueueServiceStub.sendFinanceTotals(financeCostTotalResources);
    }

    @Override
    public ServiceResult<Void> sendFinanceTotalsForCompetition(Long competitionId) {
        List<Application> applications = applicationService.getApplicationsByCompetitionIdAndState(competitionId, submitted).getSuccessObjectOrThrowException();
        applications.forEach(app -> applicationFinanceHandler.getApplicationFinances(app.getId()));

        return ServiceResult.serviceSuccess();
    }

    @Override
    public ServiceResult<Void> sendAllFinanceTotals() {
        List<Application> applications = applicationService.getApplicationsByState(submitted).getSuccessObjectOrThrowException();
        applications.forEach(app -> applicationFinanceHandler.getApplicationFinances(app.getId()));

        return ServiceResult.serviceSuccess();
    }
}
