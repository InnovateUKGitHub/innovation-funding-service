package com.worth.ifs.application.finance.view;

import com.worth.ifs.application.finance.CostType;
import com.worth.ifs.application.finance.model.OrganisationFinance;
import com.worth.ifs.application.finance.service.FinanceService;
import com.worth.ifs.finance.domain.ApplicationFinance;
import com.worth.ifs.finance.domain.Cost;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
            OrganisationFinance organisationFinance = new OrganisationFinance(applicationFinance, costs);
            organisationFinances.add(organisationFinance);

        }
    }

    public List<OrganisationFinance> getOrganisationFinances() {
        return organisationFinances;
    }

    public EnumMap<CostType, BigDecimal> getTotalPerType() {
        EnumMap<CostType, BigDecimal> totalPerType = new EnumMap<>(CostType.class);
        for(CostType costType : CostType.values()) {
            BigDecimal typeTotal = organisationFinances.stream()
                    .map(o -> o.getCostCategory(costType).getTotal())
                    .reduce(BigDecimal.ZERO, BigDecimal::add);;
            totalPerType.put(costType, typeTotal);
        }

        return totalPerType;
    }


    public BigDecimal getTotal() {
        return organisationFinances.stream()
                .map(of -> of.getTotal())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public Double getTotalGrantPercentage() {
        Double totalFundingSought = 0D;

        if(totalIsNotZero()) {
            totalFundingSought = getTotalFundingSought().divide(getTotal(), 2, RoundingMode.HALF_UP).multiply(new BigDecimal(100)).doubleValue();
        }

        return totalFundingSought;
    }

    private boolean totalIsNotZero() {
        return !getTotal().equals(new BigDecimal(0));
    }

    public BigDecimal getTotalFundingSought() {
        BigDecimal totalFundingSought = new BigDecimal(0);

        if(totalIsNotZero()) {
            totalFundingSought = organisationFinances.stream()
                    .filter(of -> of != null && of.getGrantClaimPercentage() != null)
                    .map(of -> of.getTotalFundingSought())
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        }

        return totalFundingSought;
    }

    public BigDecimal getTotalContribution() {
        return getTotal().subtract(getTotalFundingSought());
    }

    public BigDecimal getTotalOtherFunding() {
        return organisationFinances.stream()
                .filter(of -> of != null)
                .map(of -> of.getTotalOtherFunding())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
