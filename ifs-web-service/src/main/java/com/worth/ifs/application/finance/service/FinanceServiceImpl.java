package com.worth.ifs.application.finance.service;

import com.worth.ifs.finance.domain.ApplicationFinance;
import com.worth.ifs.finance.domain.Cost;
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

    public ApplicationFinance addApplicationFinance(Long applicationId, Long userId) {
        ProcessRole processRole = userRestService.findProcessRole(userId, applicationId);

        if(processRole.getOrganisation()!=null) {
            return applicationFinanceRestService.addApplicationFinanceForOrganisation(applicationId, processRole.getOrganisation().getId());
        }
        return null;
    }

    public ApplicationFinance getApplicationFinance(Long applicationId, Long userId) {
        ProcessRole userApplicationRole = userRestService.findProcessRole(userId, applicationId);
        return applicationFinanceRestService.getApplicationFinance(applicationId, userApplicationRole.getOrganisation().getId());
    }

    public List<ApplicationFinance> getApplicationFinances(Long applicationId) {
        return applicationFinanceRestService.getApplicationFinances(applicationId);
    }

    public List<Cost> getCosts(Long applicationFinanceId) {
       return costRestService.getCosts(applicationFinanceId);
    }

    public void addCost(Long applicationFinanceId, Long questionId) {
        costRestService.add(applicationFinanceId, questionId);
    }
}
