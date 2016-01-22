package com.worth.ifs.application.finance.view;

import com.worth.ifs.application.finance.service.FinanceService;
import com.worth.ifs.finance.domain.ApplicationFinance;
import com.worth.ifs.finance.domain.Cost;
import com.worth.ifs.finance.resource.ApplicationFinanceResource;
import com.worth.ifs.finance.resource.cost.CostType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

@Configurable
public class OrganisationFinanceOverview {
    private final Log log = LogFactory.getLog(getClass());

    Long applicationId;
    List<ApplicationFinanceResource> applicationFinances = new ArrayList<>();

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
        applicationFinances = financeService.getApplicationFinanceTotals(applicationId);
    }

    public List<ApplicationFinanceResource> getApplicationFinances() {
        return applicationFinances;
    }

    public EnumMap<CostType, BigDecimal> getTotalPerType() {
        EnumMap<CostType, BigDecimal> totalPerType = new EnumMap<>(CostType.class);
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
