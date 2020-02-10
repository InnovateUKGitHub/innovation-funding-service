package org.innovateuk.ifs.finance.resource;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.innovateuk.ifs.finance.resource.category.FinanceRowCostCategory;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.innovateuk.ifs.finance.resource.cost.GrantClaim;
import org.innovateuk.ifs.finance.resource.cost.GrantClaimAmount;
import org.innovateuk.ifs.finance.resource.cost.GrantClaimPercentage;
import org.innovateuk.ifs.finance.resource.cost.Vat;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * Application finance resource holds the organisation's finance resources for an target
 */
public abstract class BaseFinanceResource {

    protected Long id;
    protected Long organisation;
    protected String organisationName;
    protected Long target;
    protected int maximumFundingLevel;
    protected OrganisationSize organisationSize;
    protected Map<FinanceRowType, FinanceRowCostCategory> financeOrganisationDetails = new HashMap<>();
    private FinancialYearAccountsResource financialYearAccounts;

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

    public BaseFinanceResource(long id,
                               long organisation,
                               long target,
                               OrganisationSize organisationSize) {
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

    public String getOrganisationName() {
        return organisationName;
    }

    public void setOrganisationName(String organisationName) {
        this.organisationName = organisationName;
    }

    public Long getTarget() {
        return target;
    }

    public void setTarget(Long target) {
        this.target = target;
    }

    public int getMaximumFundingLevel() {
        return maximumFundingLevel;
    }

    public void setMaximumFundingLevel(int maximumFundingLevel) {
        this.maximumFundingLevel = maximumFundingLevel;
    }

    @JsonProperty("organisationSizeValue")
    public OrganisationSize getOrganisationSize() {
        return organisationSize;
    }

    @JsonProperty("organisationSizeValue")
    public void setOrganisationSize(OrganisationSize organisationSize) {
        this.organisationSize = organisationSize;
    }

    public Map<FinanceRowType, FinanceRowCostCategory> getFinanceOrganisationDetails() {
        return financeOrganisationDetails;
    }

    public void setFinanceOrganisationDetails(Map<FinanceRowType, FinanceRowCostCategory> financeOrganisationDetails) {
        this.financeOrganisationDetails = financeOrganisationDetails;
    }

    public FinancialYearAccountsResource getFinancialYearAccounts() {
        return financialYearAccounts;
    }

    public void setFinancialYearAccounts(FinancialYearAccountsResource financialYearAccounts) {
        this.financialYearAccounts = financialYearAccounts;
    }

    public FinanceRowCostCategory getFinanceOrganisationDetails(FinanceRowType costType) {
        if (financeOrganisationDetails != null) {
            return financeOrganisationDetails.get(costType);
        } else {
            return null;
        }
    }

    @JsonIgnore
    public GrantClaim getGrantClaim() {
        if (financeOrganisationDetails != null) {
            FinanceRowCostCategory grantClaimPercentageCostCategory = financeOrganisationDetails.get(FinanceRowType.FINANCE);
            if (grantClaimPercentageCostCategory != null) {
                return grantClaimPercentageCostCategory.getCosts().stream()
                        .findAny()
                        .filter(c -> c instanceof GrantClaimPercentage)
                        .map(c -> (GrantClaimPercentage) c)
                        .orElse(null);
            }
            FinanceRowCostCategory grantClaimAmountCostCategory = financeOrganisationDetails.get(FinanceRowType.GRANT_CLAIM_AMOUNT);
            if (grantClaimAmountCostCategory != null) {
                return grantClaimAmountCostCategory.getCosts().stream()
                        .findAny()
                        .filter(c -> c instanceof GrantClaimAmount)
                        .map(c -> (GrantClaimAmount) c)
                        .orElse(null);
            }
        }
        return null;
    }

    @JsonIgnore
    public boolean isRequestingFunding() {
        GrantClaim grantClaim = getGrantClaim();
        return grantClaim.isRequestingFunding();
    }

    @JsonIgnore
    public BigDecimal getGrantClaimPercentage() {
        GrantClaim grantClaim = getGrantClaim();
        return grantClaim.calculateClaimPercentage(getTotal(), getTotalOtherFunding());
    }

    @JsonIgnore
    public BigDecimal getTotalFundingSought() {
        GrantClaim grantClaim = getGrantClaim();
        return grantClaim.calculateFundingSought(getTotal(), getTotalOtherFunding())
                .max(BigDecimal.ZERO);
    }

    @JsonIgnore
    public BigDecimal getTotalContribution() {
        BigDecimal totalFunding = getTotalOtherFunding()
                .add(getTotalFundingSought());
        return getTotal().subtract(totalFunding)
                .max(BigDecimal.ZERO);
    }

    @JsonIgnore
    public BigDecimal getTotalOtherFunding() {
        FinanceRowCostCategory otherFundingCategory = getFinanceOrganisationDetails(FinanceRowType.OTHER_FUNDING);
        return otherFundingCategory != null ? otherFundingCategory.getTotal() : BigDecimal.ZERO;
    }

    @JsonIgnore
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

    @JsonIgnore
    public boolean isVatRegistered() {
        if (financeOrganisationDetails != null && financeOrganisationDetails.containsKey(FinanceRowType.VAT)) {
            FinanceRowCostCategory financeRowCostCategory = financeOrganisationDetails.get(FinanceRowType.VAT);
            Vat vat = financeRowCostCategory.getCosts().stream()
                    .findAny()
                    .filter(c -> c instanceof Vat)
                    .map(c -> (Vat) c)
                    .orElse(null);
            return vat == null ? false : vat.getRegistered() == null ? false : vat.getRegistered();
        } else {
            return false;
        }
    }
}
