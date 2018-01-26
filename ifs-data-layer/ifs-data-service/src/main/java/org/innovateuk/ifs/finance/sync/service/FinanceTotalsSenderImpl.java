package org.innovateuk.ifs.finance.sync.service;

import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.resource.ApplicationState;
import org.innovateuk.ifs.application.transactional.ApplicationService;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.finance.handler.ApplicationFinanceHandler;
import org.innovateuk.ifs.finance.resource.ApplicationFinanceResource;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.innovateuk.ifs.finance.resource.sync.FinanceCostTotalResource;
import org.innovateuk.ifs.finance.sync.mapper.FinanceCostTotalResourceMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
    private MessageQueueServiceStub messageQueueServiceStub;

    @Autowired
    private ApplicationService applicationService;

    public ServiceResult<Void> sendFinanceTotalsForApplication(Long applicationId) {
        List<ApplicationFinanceResource> applicationFinanceResources = applicationFinanceHandler.getApplicationFinances(applicationId);
        List<FinanceCostTotalResource> financeCostTotalResourceList = financeCostTotalResourceMapper.mapFromApplicationFinanceResourceListToList(applicationFinanceResources);

        return messageQueueServiceStub.sendFinanceTotals(filterBySpendProfile(financeCostTotalResourceList));
    }

    @Override
    public ServiceResult<Void> sendFinanceTotalsForCompetition(Long competitionId) {
        List<Application> applications = applicationService.getApplicationsByCompetitionIdAndState(competitionId, ApplicationState.wasSubmittedStates()).getSuccessObjectOrThrowException();
        applications.forEach(app -> sendFinanceTotalsForApplication(app.getId()));

        return ServiceResult.serviceSuccess();
    }

    @Override
    public ServiceResult<Void> sendAllFinanceTotals() {
        List<Application> applications = applicationService.getApplicationsByState(ApplicationState.wasSubmittedStates()).getSuccessObjectOrThrowException();
        applications.forEach(app -> sendFinanceTotalsForApplication(app.getId()));

        return ServiceResult.serviceSuccess();
    }

    private static List<FinanceCostTotalResource> filterBySpendProfile(List<FinanceCostTotalResource> financeCostTotalResources) {
        return financeCostTotalResources.stream().filter(financeResource ->
                isSpendProfile(financeResource.getName())).collect(Collectors.toList());
    }

    private static boolean isSpendProfile(String typeName) {
        Optional<FinanceRowType> financeRowTypeOptional = FinanceRowType.getByTypeName(typeName);
        return financeRowTypeOptional.isPresent() && financeRowTypeOptional.get().isIncludedInSpendProfile();
    }
}
