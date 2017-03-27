package org.innovateuk.ifs.finance.resource;

import org.innovateuk.ifs.finance.resource.category.FinanceRowCostCategory;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.innovateuk.ifs.finance.resource.cost.GrantClaim;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * Application finance resource holds the organisation's finance resources for an target
 */
public abstract class BaseFinanceResource {

    private static final Log LOG = LogFactory.getLog(BaseFinanceResource.class);

    protected Long id;
    protected Long organisation;
    protected Long target;
    protected Long organisationSize;
    protected Map<FinanceRowType, FinanceRowCostCategory> financeOrganisationDetails = new HashMap<>();

    public BaseFinanceResource(BaseFinanceResource originalFinance) {
        if (originalFinance != null) {
            this.id = originalFinance.getId();
            this.organisation = originalFinance.getOrganisation();
            this.target = originalFinance.getTarget();
            this.organisationSize = originalFinance.getOrganisationSize();
        }
    }

    public BaseFinanceResource() {
    	// no-arg constructor
    }

    public BaseFinanceResource(Long id, Long organisation, Long target, Long organisationSize) {
        this.id = id;
        this.organisation = organisation;
        this.target = target;
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

    public void setOrganisation(Long organisation) {
        this.organisation = organisation;
    }

    public Long getTarget() {
        return target;
    }

    public void setTarget(Long target) {
        this.target = target;
    }

    public Long getOrganisationSize() {
        return organisationSize;
    }

    public void setOrganisationSize(Long organisationSize) {
        this.organisationSize = organisationSize;
    }

    public Map<FinanceRowType, FinanceRowCostCategory> getFinanceOrganisationDetails() {
        return financeOrganisationDetails;
    }

    public void setFinanceOrganisationDetails(Map<FinanceRowType, FinanceRowCostCategory> financeOrganisationDetails) {
        this.financeOrganisationDetails = financeOrganisationDetails;
    }

    public FinanceRowCostCategory getFinanceOrganisationDetails(FinanceRowType costType) {
        if (financeOrganisationDetails != null) {
            return financeOrganisationDetails.get(costType);
        } else {
            return null;
        }
    }

    public BigDecimal getTotal() {
        if (financeOrganisationDetails == null) {
            return BigDecimal.ZERO;
        }

        BigDecimal total = financeOrganisationDetails.entrySet().stream()
                .filter(cat -> cat != null &&
                        cat.getValue() != null &&
                        cat.getValue().getTotal() != null)
                .filter(cat -> !cat.getValue().excludeFromTotalCost())
                .map(cat -> cat.getValue().getTotal())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (total == null) {
            return BigDecimal.ZERO;
        }

        return total;
    }

    public GrantClaim getGrantClaim() {
        if (financeOrganisationDetails != null && financeOrganisationDetails.containsKey(FinanceRowType.FINANCE)) {
            FinanceRowCostCategory financeRowCostCategory = financeOrganisationDetails.get(FinanceRowType.FINANCE);
            return financeRowCostCategory.getCosts().stream()
                    .findAny()
                    .filter(c -> c instanceof GrantClaim)
                    .map(c -> (GrantClaim) c)
                    .orElse(null);
        } else {
            return null;
        }
    }

    public Integer getGrantClaimPercentage() {
        FinanceRowCostCategory financeRowCostCategory = getFinanceOrganisationDetails(FinanceRowType.FINANCE);
        return (financeRowCostCategory != null && financeRowCostCategory.getTotal() != null) ? financeRowCostCategory.getTotal().intValueExact() : null;
    }

    public BigDecimal getTotalFundingSought() {
        if (getGrantClaimPercentage() == null) {
            return new BigDecimal(0);
        }
        BigDecimal totalFundingSought = getTotal()
                .multiply(new BigDecimal(getGrantClaimPercentage()))
                .divide(new BigDecimal(100))
                .subtract(getTotalOtherFunding());

        return totalFundingSought.max(BigDecimal.ZERO);
    }

    public BigDecimal getTotalContribution() {
        return getTotal()
                .subtract(getTotalOtherFunding())
                .subtract(getTotalFundingSought())
                .max(BigDecimal.ZERO);
    }

    public BigDecimal getTotalOtherFunding() {
        FinanceRowCostCategory otherFundingCategory = getFinanceOrganisationDetails(FinanceRowType.OTHER_FUNDING);
        return otherFundingCategory != null ? otherFundingCategory.getTotal() : BigDecimal.ZERO;
    }
}
