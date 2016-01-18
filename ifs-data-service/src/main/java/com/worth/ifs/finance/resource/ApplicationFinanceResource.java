package com.worth.ifs.finance.resource;

import com.worth.ifs.finance.domain.ApplicationFinance;
import com.worth.ifs.finance.resource.category.CostCategory;
import com.worth.ifs.finance.resource.cost.CostType;
import com.worth.ifs.finance.resource.cost.GrantClaim;
import com.worth.ifs.user.domain.OrganisationSize;

import java.math.BigDecimal;
import java.util.EnumMap;

public class ApplicationFinanceResource {
    Long id;
    private Long organisation;
    private Long application;
    private OrganisationSize organisationSize;
    private EnumMap<CostType, CostCategory> financeOrganisationDetails;

    public ApplicationFinanceResource(ApplicationFinance applicationFinance) {
        this.id = applicationFinance.getId();
        this.organisation = applicationFinance.getOrganisation().getId();
        this.application = applicationFinance.getApplication().getId();
        this.organisationSize = applicationFinance.getOrganisationSize();
    }

    public ApplicationFinanceResource() {
    }

    public ApplicationFinanceResource(Long id, Long organisation, Long application, OrganisationSize organisationSize) {
        this.id = id;
        this.organisation = organisation;
        this.application = application;
        this.organisationSize = organisationSize;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getOrganisation() {
        return organisation;
    }

    public Long getApplication() {
        return application;
    }

    public OrganisationSize getOrganisationSize() {
        return organisationSize;
    }

    public void setOrganisationSize(OrganisationSize organisationSize) {
        this.organisationSize = organisationSize;
    }

    public void setOrganisation(Long organisation) {
        this.organisation = organisation;
    }

    public void setApplication(Long application) {
        this.application = application;
    }

    public EnumMap<CostType, CostCategory> getFinanceOrganisationDetails() {
        return financeOrganisationDetails;
    }

    public CostCategory getFinanceOrganisationDetails(CostType costType) {
        return financeOrganisationDetails.get(costType);
    }

    public void setFinanceOrganisationDetails(EnumMap<CostType, CostCategory> financeOrganisationDetails) {
        this.financeOrganisationDetails = financeOrganisationDetails;
    }

    public BigDecimal getTotal() {
        BigDecimal total = financeOrganisationDetails.entrySet().stream()
                .filter(cat -> cat != null &&
                        cat.getValue() != null &&
                        cat.getValue().getTotal() != null)
                .filter(cat -> !cat.getValue().excludeFromTotalCost())
                .map(cat -> cat.getValue().getTotal())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if(total == null) {
            return new BigDecimal(0);
        }

        return total;
    }

    public Integer getGrantClaimPercentage() {
        if(financeOrganisationDetails.containsKey(CostType.FINANCE)) {
            GrantClaim grantClaim = (GrantClaim) financeOrganisationDetails.get(CostType.FINANCE);
            return grantClaim.getGrantClaimPercentage();
        } else {
            return 0;
        }
    }

    public BigDecimal getTotalFundingSought() {
        BigDecimal totalFundingSought  = getTotal()
                .multiply(new BigDecimal(getGrantClaimPercentage()))
                .divide(new BigDecimal(100))
                .subtract(getTotalOtherFunding());

        return totalFundingSought.max(new BigDecimal(0));
    }

    public BigDecimal getTotalContribution() {
        return getTotal()
                .subtract(getTotalOtherFunding())
                .subtract(getTotalFundingSought())
                .max(new BigDecimal(0));
    }

    public BigDecimal getTotalOtherFunding() {
        CostCategory otherFundingCategory = getFinanceOrganisationDetails(CostType.OTHER_FUNDING);
        return (otherFundingCategory != null ? otherFundingCategory.getTotal() : BigDecimal.ZERO);
    }
}