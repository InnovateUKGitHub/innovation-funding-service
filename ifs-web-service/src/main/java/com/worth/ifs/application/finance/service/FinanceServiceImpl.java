package com.worth.ifs.application.finance.service;

import com.worth.ifs.finance.resource.ApplicationFinanceResource;
import com.worth.ifs.finance.resource.cost.CostItem;
import com.worth.ifs.finance.service.ApplicationFinanceRestService;
import com.worth.ifs.finance.service.CostRestService;
import com.worth.ifs.user.domain.ProcessRole;
import com.worth.ifs.user.service.UserRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * {@code FinanceServiceImpl} implements {@link FinanceService} handles the finances for each of the organisations.
 */
// TODO DW - INFUND-1555 - get the service calls below to use RestResults
@Service
public class FinanceServiceImpl implements FinanceService {

    @Autowired
    private UserRestService userRestService;

    @Autowired
    private CostRestService costRestService;

    @Autowired
    private ApplicationFinanceRestService applicationFinanceRestService;

    @Override
    public ApplicationFinanceResource addApplicationFinance(Long userId, Long applicationId) {
        ProcessRole processRole = userRestService.findProcessRole(userId, applicationId).getSuccessObjectOrThrowException();

        if(processRole.getOrganisation()!=null) {
            return applicationFinanceRestService.addApplicationFinanceForOrganisation(applicationId, processRole.getOrganisation().getId()).getSuccessObjectOrThrowException();
        }
        return null;
    }

    @Override
    public ApplicationFinanceResource getApplicationFinance(Long userId, Long applicationId) {
        ProcessRole userApplicationRole = userRestService.findProcessRole(userId, applicationId).getSuccessObjectOrThrowException();
        return applicationFinanceRestService.getApplicationFinance(applicationId, userApplicationRole.getOrganisation().getId()).getSuccessObjectOrThrowException();
    }

    @Override
    public ApplicationFinanceResource getApplicationFinanceDetails(Long userId, Long applicationId) {
        ProcessRole userApplicationRole = userRestService.findProcessRole(userId, applicationId).getSuccessObjectOrThrowException();
        return applicationFinanceRestService.getFinanceDetails(applicationId, userApplicationRole.getOrganisation().getId()).getSuccessObjectOrThrowException();
    }


    @Override
    public List<ApplicationFinanceResource> getApplicationFinanceTotals(Long applicationId) {
        return applicationFinanceRestService.getFinanceTotals(applicationId).handleSuccessOrFailure(
                failure -> Collections.<ApplicationFinanceResource> emptyList(),
                success -> success
        );
    }


    @Override
    public List<CostItem> getCosts(Long applicationFinanceId) {
       return costRestService.getCosts(applicationFinanceId).getSuccessObjectOrThrowException();
    }

    @Override
    public CostItem addCost(Long applicationFinanceId, Long questionId) {
        return costRestService.add(applicationFinanceId, questionId, null).getSuccessObjectOrThrowException();
    }
}
