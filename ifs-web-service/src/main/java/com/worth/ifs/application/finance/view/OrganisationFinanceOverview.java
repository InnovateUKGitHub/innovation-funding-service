package com.worth.ifs.application.finance.view;

import com.worth.ifs.application.finance.service.FinanceService;
import com.worth.ifs.finance.resource.ApplicationFinanceResource;
import com.worth.ifs.finance.resource.cost.CostType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Configurable
public class OrganisationFinanceOverview {

    Long applicationId;
    List<ApplicationFinanceResource> applicationFinances = new ArrayList<>();

    @Autowired
    private FinanceService financeService;

    public OrganisationFinanceOverview() {
    	// no-arg constructor
    }

    public OrganisationFinanceOverview(FinanceService financeService, Long applicationId) {
        this.applicationId = applicationId;
        this.financeService = financeService;
        initializeOrganisationFinances();
    }

    private void initializeOrganisationFinances() {
        applicationFinances = financeService.getApplicationFinanceTotals(applicationId);
    }

    public List<ApplicationFinanceResource> getApplicationFinances() {
        return applicationFinances;
    }
    public Map<Long, ApplicationFinanceResource> getApplicationFinancesByOrganisation(){
        return applicationFinances
                .stream()
                .collect(Collectors.toMap(ApplicationFinanceResource::getOrganisation, f -> f));
    }


    public Map<CostType, BigDecimal> getTotalPerType() {
        Map<CostType, BigDecimal> totalPerType = new EnumMap<>(CostType.class);
        for(CostType costType : CostType.values()) {
            BigDecimal typeTotal = applicationFinances.stream()
                    .filter(o -> o.getFinanceOrganisationDetails(costType) != null)
                    .map(o -> o.getFinanceOrganisationDetails(costType).getTotal())
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            totalPerType.put(costType, typeTotal);
        }

        return totalPerType;
    }

    public BigDecimal getTotal() {
        return applicationFinances.stream()
                .map(of -> of.getTotal())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getTotalFundingSought() {
        BigDecimal totalFundingSought = applicationFinances.stream()
                .filter(of -> of != null && of.getGrantClaimPercentage() != null)
                .map(of -> of.getTotalFundingSought())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return totalFundingSought;
    }

    public BigDecimal getTotalContribution() {
        return applicationFinances.stream()
                .filter(of -> of != null)
                .map(of -> of.getTotalContribution())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getTotalOtherFunding() {
        return applicationFinances.stream()
                .filter(of -> of != null)
                .map(of -> of.getTotalOtherFunding())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
