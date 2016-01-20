package com.worth.ifs.application.finance.service;

import com.worth.ifs.finance.domain.ApplicationFinance;
import com.worth.ifs.finance.domain.Cost;
import com.worth.ifs.finance.resource.ApplicationFinanceResource;
import com.worth.ifs.finance.service.ApplicationFinanceRestService;
import com.worth.ifs.finance.service.CostRestService;
import com.worth.ifs.user.domain.ProcessRole;
import com.worth.ifs.user.service.UserRestService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * {@code FinanceServiceImpl} implements {@link FinanceService} handles the finances for each of the organisations.
 */
@Service
public class FinanceServiceImpl implements FinanceService {
    private final Log log = LogFactory.getLog(getClass());

    @Autowired
    UserRestService userRestService;

    @Autowired
    CostRestService costRestService;

    @Autowired
    ApplicationFinanceRestService applicationFinanceRestService;

    public ApplicationFinanceResource addApplicationFinance(Long applicationId, Long userId) {
        ProcessRole processRole = userRestService.findProcessRole(userId, applicationId);

        if(processRole.getOrganisation()!=null) {
            return applicationFinanceRestService.addApplicationFinanceForOrganisation(applicationId, processRole.getOrganisation().getId());
        }
        return null;
    }

    public ApplicationFinanceResource getApplicationFinance(Long applicationId, Long userId) {
        ProcessRole userApplicationRole = userRestService.findProcessRole(userId, applicationId);
        return applicationFinanceRestService.getApplicationFinance(applicationId, userApplicationRole.getOrganisation().getId());
    }

    public ApplicationFinanceResource getApplicationFinanceDetails(Long applicationId, Long userId) {
        ProcessRole userApplicationRole = userRestService.findProcessRole(userId, applicationId);
        return applicationFinanceRestService.getFinanceDetails(applicationId, userApplicationRole.getOrganisation().getId());
    }


    public List<ApplicationFinanceResource> getApplicationFinanceTotals(Long applicationId) {
        return applicationFinanceRestService.getFinanceTotals(applicationId);
    }


    public List<Cost> getCosts(Long applicationFinanceId) {
       return costRestService.getCosts(applicationFinanceId);
    }

    public void addCost(Long applicationFinanceId, Long questionId) {
        costRestService.add(applicationFinanceId, questionId);
    }
}
