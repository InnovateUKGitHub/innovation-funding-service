package com.worth.ifs.application.finance;

import com.worth.ifs.domain.Cost;
import com.worth.ifs.service.ApplicationFinanceService;
import com.worth.ifs.service.CostService;
import com.worth.ifs.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ImportResource;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

@Service
public class FinanceService {

    @Autowired
    UserService userService;

    @Autowired
    CostService costService;

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
}
