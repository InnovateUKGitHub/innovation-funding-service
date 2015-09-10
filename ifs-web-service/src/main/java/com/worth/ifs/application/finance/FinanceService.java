package com.worth.ifs.application.finance;

import com.worth.ifs.domain.Application;
import com.worth.ifs.domain.ApplicationFinance;
import com.worth.ifs.domain.Cost;
import com.worth.ifs.domain.UserApplicationRole;
import com.worth.ifs.service.ApplicationFinanceService;
import com.worth.ifs.service.CostService;
import com.worth.ifs.service.OrganisationService;
import com.worth.ifs.service.UserService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ImportResource;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

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
