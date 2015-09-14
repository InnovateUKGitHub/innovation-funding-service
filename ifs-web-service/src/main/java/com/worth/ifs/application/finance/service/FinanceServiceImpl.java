package com.worth.ifs.application.finance.service;

import com.worth.ifs.application.finance.CostCategory;
import com.worth.ifs.application.finance.CostType;
import com.worth.ifs.application.finance.model.OrganisationFinance;
import com.worth.ifs.finance.domain.ApplicationFinance;
import com.worth.ifs.finance.domain.Cost;
import com.worth.ifs.finance.service.ApplicationFinanceRestService;
import com.worth.ifs.finance.service.CostRestService;
import com.worth.ifs.user.domain.UserApplicationRole;
import com.worth.ifs.user.service.UserRestService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Optional;

@Service
public class FinanceServiceImpl implements FinanceService {
    private final Log log = LogFactory.getLog(getClass());

    @Autowired
    UserRestService userRestService;

    @Autowired
    CostRestService costRestService;

    @Autowired
    ApplicationFinanceRestService applicationFinanceRestService;

    public ApplicationFinance addApplicationFinance(Long userId, Long applicationId) {
        UserApplicationRole userApplicationRole = userRestService.findUserApplicationRole(userId, applicationId);

        if(userApplicationRole.getOrganisation()!=null) {
            return applicationFinanceRestService.addApplicationFinanceForOrganisation(applicationId, userApplicationRole.getOrganisation().getId());
        }
        return null;
    }

    public ApplicationFinance getApplicationFinance(Long userId, Long applicationId) {
        UserApplicationRole userApplicationRole = userRestService.findUserApplicationRole(userId, applicationId);
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
