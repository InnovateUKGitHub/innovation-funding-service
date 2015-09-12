package com.worth.ifs.application.finance;

import com.worth.ifs.finance.domain.ApplicationFinance;
import com.worth.ifs.finance.domain.Cost;
import com.worth.ifs.finance.service.ApplicationFinanceService;
import com.worth.ifs.finance.service.CostService;
import com.worth.ifs.user.domain.UserApplicationRole;
import com.worth.ifs.user.service.OrganisationService;
import com.worth.ifs.user.service.UserService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Optional;

@Service
public class FinanceService {
    private final Log log = LogFactory.getLog(getClass());

    @Autowired
    UserService userService;

    @Autowired
    CostService costService;

    @Autowired
    OrganisationService organisationService;

    @Autowired
    ApplicationFinanceService applicationFinanceService;

    List<OrganisationFinance> organisationFinances = new ArrayList<>();

    public EnumMap<CostType, CostCategory> getFinances(Long applicationFinanceId) {
        List<Cost> costs = costService.getCosts(applicationFinanceId);
        OrganisationFinance organisationFinance = new OrganisationFinance(applicationFinanceId, costs);
        EnumMap<CostType, CostCategory> organisationFinanceDetails = organisationFinance.getOrganisationFinances();
        organisationFinances.add(organisationFinance);
        return organisationFinanceDetails;
    }

    public Double getTotal(Long applicationFinanceId) {
        Optional<OrganisationFinance> organisationFinance = organisationFinances.stream().
                filter(oa -> oa.getApplicationFinanceId().equals(applicationFinanceId)).findFirst();
        if(organisationFinance.isPresent()) {
            return organisationFinance.get().getTotal();
        } else {
            return 0D;
        }
    }

    public ApplicationFinance addApplicationFinance(Long applicationId, Long userId) {
        UserApplicationRole userApplicationRole = userService.findUserApplicationRole(userId, applicationId);

        if(userApplicationRole.getOrganisation()!=null) {
            return applicationFinanceService.addApplicationFinanceForOrganisation(applicationId, userApplicationRole.getOrganisation().getId());
        }
        return null;
    }

    public ApplicationFinance getApplicationFinance(Long applicationId, Long userId) {
        UserApplicationRole userApplicationRole = userService.findUserApplicationRole(userId, applicationId);
        return applicationFinanceService.getApplicationFinance(applicationId, userApplicationRole.getOrganisation().getId());
    }

    public List<ApplicationFinance> getApplicationFinances(Long applicationId) {
        return applicationFinanceService.getApplicationFinances(applicationId);
    }

    public void addCost(Long applicationFinanceId , Long questionId) {
        costService.add(applicationFinanceId, questionId);
    }
}
