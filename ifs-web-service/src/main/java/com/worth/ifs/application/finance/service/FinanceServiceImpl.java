package com.worth.ifs.application.finance.service;

import com.worth.ifs.finance.resource.ApplicationFinanceResource;
import com.worth.ifs.finance.resource.cost.CostItem;
import com.worth.ifs.finance.service.ApplicationFinanceRestService;
import com.worth.ifs.finance.service.CostRestService;
import com.worth.ifs.user.domain.ProcessRole;
import com.worth.ifs.user.service.UserRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    ApplicationFinanceRestService applicationFinanceRestService;

    @Override
    public ApplicationFinanceResource addApplicationFinance(Long applicationId, Long userId) {
        ProcessRole processRole = userRestService.findProcessRole(userId, applicationId).getSuccessObjectOrNull();

        if(processRole.getOrganisation()!=null) {
            return applicationFinanceRestService.addApplicationFinanceForOrganisation(applicationId, processRole.getOrganisation().getId());
        }
        return null;
    }

    @Override
    public ApplicationFinanceResource getApplicationFinance(Long applicationId, Long userId) {
        ProcessRole userApplicationRole = userRestService.findProcessRole(userId, applicationId).getSuccessObjectOrNull();
        return applicationFinanceRestService.getApplicationFinance(applicationId, userApplicationRole.getOrganisation().getId());
    }

    @Override
    public ApplicationFinanceResource getApplicationFinanceDetails(Long applicationId, Long userId) {
        ProcessRole userApplicationRole = userRestService.findProcessRole(userId, applicationId).getSuccessObjectOrNull();
        return applicationFinanceRestService.getFinanceDetails(applicationId, userApplicationRole.getOrganisation().getId());
    }


    @Override
    public List<ApplicationFinanceResource> getApplicationFinanceTotals(Long applicationId) {
        return applicationFinanceRestService.getFinanceTotals(applicationId);
    }


    @Override
    public List<CostItem> getCosts(Long applicationFinanceId) {
       return costRestService.getCosts(applicationFinanceId).getSuccessObjectOrNull();
    }

    @Override
    public CostItem addCost(Long applicationFinanceId, Long questionId) {
        return costRestService.add(applicationFinanceId, questionId, null).getSuccessObjectOrNull();
    }
}
