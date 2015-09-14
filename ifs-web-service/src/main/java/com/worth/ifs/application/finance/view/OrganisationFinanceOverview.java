package com.worth.ifs.application.finance.view;

import com.worth.ifs.application.finance.CostType;
import com.worth.ifs.application.finance.model.OrganisationFinance;
import com.worth.ifs.application.finance.service.CostService;
import com.worth.ifs.application.finance.service.FinanceService;
import com.worth.ifs.finance.domain.ApplicationFinance;
import com.worth.ifs.finance.domain.Cost;
import com.worth.ifs.finance.service.ApplicationFinanceRestService;
import com.worth.ifs.finance.service.CostRestService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.SpringApplicationContextLoader;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

@Configurable
public class OrganisationFinanceOverview {
    private final Log log = LogFactory.getLog(getClass());

    Long applicationId;
    List<OrganisationFinance> organisationFinances = new ArrayList<>();

    @Autowired
    private FinanceService financeService;

    public OrganisationFinanceOverview() {

    }

    public OrganisationFinanceOverview(FinanceService financeService, Long applicationId) {
        this.applicationId = applicationId;
        this.financeService = financeService;
        initializeOrganisationFinances();
    }

    private void initializeOrganisationFinances() {
        List<ApplicationFinance> applicationFinances = financeService.getApplicationFinances(applicationId);
        for(ApplicationFinance applicationFinance : applicationFinances) {
            List<Cost> costs = financeService.getCosts(applicationFinance.getId());
            OrganisationFinance organisationFinance = new OrganisationFinance(applicationFinance.getId(), applicationFinance.getOrganisation(), costs);
            organisationFinances.add(organisationFinance);

        }
    }

    public List<OrganisationFinance> getOrganisationFinances() {
        return organisationFinances;
    }

    public EnumMap<CostType, Double> getTotalPerType() {
        EnumMap<CostType, Double> totalPerType = new EnumMap<CostType, Double>(CostType.class);
        for(CostType costType : CostType.values()) {
            Double typeTotal = organisationFinances.stream().mapToDouble(o -> o.getCostCategory(costType).getTotal()).sum();
            totalPerType.put(costType, typeTotal);
        }

        return totalPerType;
    }


    public Double getTotal() {
        return organisationFinances.stream().mapToDouble(of -> of.getTotal()).sum();
    }
}
