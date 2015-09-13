package com.worth.ifs.application.finance.service;

import com.worth.ifs.application.finance.CostCategory;
import com.worth.ifs.application.finance.CostType;
import com.worth.ifs.application.finance.OrganisationFinance;
import com.worth.ifs.finance.domain.ApplicationFinance;
import com.worth.ifs.finance.domain.Cost;
import com.worth.ifs.finance.service.ApplicationFinanceRestService;
import com.worth.ifs.finance.service.CostRestService;
import com.worth.ifs.user.domain.UserApplicationRole;
import com.worth.ifs.user.service.OrganisationRestService;
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
public class FinanceServiceImpl {
    private final Log log = LogFactory.getLog(getClass());

    @Autowired
    UserRestService userRestService;

    @Autowired
    CostRestService costRestService;

    @Autowired
    OrganisationRestService organisationRestService;

    @Autowired
    ApplicationFinanceRestService applicationFinanceRestService;

    List<OrganisationFinance> organisationFinances = new ArrayList<>();

    public EnumMap<CostType, CostCategory> getFinances(Long applicationFinanceId) {
        List<Cost> costs = costRestService.getCosts(applicationFinanceId);
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
        UserApplicationRole userApplicationRole = userRestService.findUserApplicationRole(userId, applicationId);

        if(userApplicationRole.getOrganisation()!=null) {
            return applicationFinanceRestService.addApplicationFinanceForOrganisation(applicationId, userApplicationRole.getOrganisation().getId());
        }
        return null;
    }

    public ApplicationFinance getApplicationFinance(Long applicationId, Long userId) {
        UserApplicationRole userApplicationRole = userRestService.findUserApplicationRole(userId, applicationId);
        return applicationFinanceRestService.getApplicationFinance(applicationId, userApplicationRole.getOrganisation().getId());
    }

    public List<ApplicationFinance> getApplicationFinances(Long applicationId) {
        return applicationFinanceRestService.getApplicationFinances(applicationId);
    }

    public void addCost(Long applicationFinanceId , Long questionId) {
        costRestService.add(applicationFinanceId, questionId);
    }
}
