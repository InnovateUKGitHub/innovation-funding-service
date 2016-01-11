package com.worth.ifs.application.finance.model;

import com.worth.ifs.application.finance.*;
import com.worth.ifs.application.finance.cost.CostItem;
import com.worth.ifs.application.finance.cost.OtherFunding;
import com.worth.ifs.finance.domain.Cost;
import com.worth.ifs.user.domain.Organisation;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Optional;

/**
 * {@code OrganisationFinance} keeps track of all the organisation's / application's specific
 * finance data.
 */
public class OrganisationFinance {
    private final Log log = LogFactory.getLog(getClass());

    public static final String GRANT_CLAIM = "Grant Claim";
    Long applicationFinanceId = 0L;
    EnumMap<CostType, CostCategory> costCategories = new EnumMap<>(CostType.class);
    Organisation organisation;
    Long grantClaimPercentageId;
    Integer grantClaimPercentage;
    List<Cost> costs = new ArrayList<>();

    CostItemFactory costItemFactory = new CostItemFactory();

    public OrganisationFinance(Long applicationFinanceId, Organisation organisation, List<Cost> costs) {
        this.applicationFinanceId = applicationFinanceId;
        this.organisation = organisation;
        this.costs = costs;
        initializeOrganisationFinances();
    }

    public void initializeOrganisationFinances() {
        createCostCategories();
        addCostsToCategories();
        setGrantClaimPercentage();
    }

    private void createCostCategories() {
        for(CostType costType : CostType.values()) {
            CostCategory costCategory = createCostCategoryByType(costType);
            costCategories.put(costType, costCategory);
        }
    }

    private CostCategory createCostCategoryByType(CostType costType) {
        switch(costType) {
            case LABOUR:
                return new LabourCostCategory();
            case OTHER_FUNDING:
                return new OtherFundingCostCategory();
            default:
                return new DefaultCostCategory();
        }
    }

    private void addCostsToCategories() {
        costs.stream().forEach(c -> addCostToCategory(c));
    }

    private void setGrantClaimPercentage() {

        Optional<Cost> grantClaim = costs
                .stream()
                .filter(c -> c.getDescription().equals(GRANT_CLAIM) && c.getQuestion().getFormInputs().stream().anyMatch(input -> input.getFormInputType().getTitle().equals("finance")))
                .findFirst();

        if(grantClaim.isPresent()) {
            this.grantClaimPercentage = grantClaim.get().getQuantity();
            this.grantClaimPercentageId = grantClaim.get().getId();
        }
    }

    /**
     * The costs are converted to a representation based on its type that can be used in the view and
     * are added to a specific category (e.g. labour).
     * @param cost Cost to be added
     */
    private void addCostToCategory(Cost cost) {
        CostType costType = CostType.fromString(cost.getQuestion().getFormInputs().get(0).getFormInputType().getTitle());
        CostItem costItem = costItemFactory.createCostItem(costType, cost);
        CostCategory costCategory = costCategories.get(costType);
        costCategory.addCost(costItem);
    }

    public BigDecimal getTotal() {
        BigDecimal total = costCategories.entrySet().stream()
                .filter(cat -> cat != null)
                .filter(cat -> cat.getValue() != null)
                .filter(cat -> cat.getValue().getTotal() != null)
                .filter(cat -> !cat.getValue().excludeFromTotalCost())
                .map(cat -> cat.getValue().getTotal())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if(total == null) {
            return new BigDecimal(0);
        }

        return total;
    }

    public EnumMap<CostType, CostCategory> getCostCategories() {
        return costCategories;
    }

    public CostCategory getCostCategory(CostType costType) {
        return costCategories.get(costType);
    }

    public Organisation getOrganisation() {
        return organisation;
    }

    public Long getApplicationFinanceId() {
        return applicationFinanceId;
    }

    public Integer getGrantClaimPercentage() {
        if(grantClaimPercentage == null) {
            return 0;
        }
        return grantClaimPercentage;
    }

    public Long getGrantClaimPercentageId() {

        return grantClaimPercentageId;
    }

    public BigDecimal getTotalFundingSought() {
        return getTotal().multiply(new BigDecimal(getGrantClaimPercentage())).divide(new BigDecimal(100)).subtract(getTotalOtherFunding());
    }

    public BigDecimal getTotalContribution() {
        return getTotal().multiply(new BigDecimal(100 - getGrantClaimPercentage())).divide(new BigDecimal(100));
    }

    public BigDecimal getTotalOtherFunding() {
        CostCategory otherFundingCategory = getCostCategory(CostType.OTHER_FUNDING);
        return otherFundingCategory.getTotal();
    }
}
