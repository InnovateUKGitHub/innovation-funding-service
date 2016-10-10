package com.worth.ifs.finance.resource;

import com.worth.ifs.finance.resource.category.FinanceRowCostCategory;
import com.worth.ifs.finance.resource.cost.FinanceRowType;
import com.worth.ifs.finance.resource.cost.GrantClaim;
import com.worth.ifs.user.resource.OrganisationSize;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * Application finance resource holds the organisation's finance resources for an application
 */
public class ApplicationFinanceResource {

    private static final Log LOG = LogFactory.getLog(ApplicationFinanceResource.class);

    Long id;
    private Long organisation;
    private Long application;
    private Long financeFileEntry;
    private OrganisationSize organisationSize;
    private Map<FinanceRowType, FinanceRowCostCategory> financeOrganisationDetails = new HashMap<>();

    public ApplicationFinanceResource(ApplicationFinanceResource applicationFinance) {
        if (applicationFinance != null) {
            this.id = applicationFinance.getId();
            this.organisation = applicationFinance.getOrganisation();
            this.application = applicationFinance.getApplication();
            this.organisationSize = applicationFinance.getOrganisationSize();
            if(applicationFinance.getFinanceFileEntry()!=null) {
                this.financeFileEntry = applicationFinance.getFinanceFileEntry();
            }
        }
    }

    public ApplicationFinanceResource() {
    	// no-arg constructor
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

    public void setOrganisation(Long organisation) {
        this.organisation = organisation;
    }

    public Long getApplication() {
        return application;
    }

    public void setApplication(Long application) {
        this.application = application;
    }

    public Long getFinanceFileEntry() {
        return financeFileEntry;
    }

    public void setFinanceFileEntry(Long financeFileEntry) {
        this.financeFileEntry = financeFileEntry;
    }

    public OrganisationSize getOrganisationSize() {
        return organisationSize;
    }

    public void setOrganisationSize(OrganisationSize organisationSize) {
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


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        ApplicationFinanceResource that = (ApplicationFinanceResource) o;

        return new EqualsBuilder()
                .append(id, that.id)
                .append(organisation, that.organisation)
                .append(application, that.application)
                .append(financeFileEntry, that.financeFileEntry)
                .append(organisationSize, that.organisationSize)
                .append(financeOrganisationDetails, that.financeOrganisationDetails)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .append(organisation)
                .append(application)
                .append(financeFileEntry)
                .append(organisationSize)
                .append(financeOrganisationDetails)
                .toHashCode();
    }
}