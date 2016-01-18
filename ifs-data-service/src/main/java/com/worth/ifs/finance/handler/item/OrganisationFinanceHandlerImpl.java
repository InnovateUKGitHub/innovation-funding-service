package com.worth.ifs.finance.handler.item;

import com.worth.ifs.finance.domain.Cost;
import com.worth.ifs.finance.repository.CostRepository;
import com.worth.ifs.finance.resource.category.CostCategory;
import com.worth.ifs.finance.resource.category.DefaultCostCategory;
import com.worth.ifs.finance.resource.category.LabourCostCategory;
import com.worth.ifs.finance.resource.category.OtherFundingCostCategory;
import com.worth.ifs.finance.resource.cost.CostItem;
import com.worth.ifs.finance.resource.cost.CostType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

@Service
public class OrganisationFinanceHandlerImpl implements OrganisationFinanceHandler {
    private final Log log = LogFactory.getLog(getClass());
    EnumMap<CostType, CostCategory> costCategories = new EnumMap<>(CostType.class);

    @Autowired
    CostRepository costRepository;

    @Override
    public EnumMap<CostType, CostCategory> getOrganisationFinances(Long applicationFinanceId) {
        List<Cost> costs = costRepository.findByApplicationFinanceId(applicationFinanceId);
        createCostCategories();
        addCostsToCategories(costs);
        calculateTotals();
        return costCategories;
    }

    @Override
    public EnumMap<CostType, CostCategory> getOrganisationFinanceTotals(Long applicationFinanceId) {
        getOrganisationFinances(applicationFinanceId);
        resetCosts();
        return costCategories;
    }

    private void createCostCategories() {
        for(CostType costType : CostType.values()) {
            CostCategory costCategory = createCostCategoryByType(costType);
            costCategories.put(costType, costCategory);
        }
    }

    private void addCostsToCategories(List<Cost> costs) {
        costs.stream().forEach(c -> addCostToCategory(c));
    }

    /**
     * The costs are converted to a representation based on its type that can be used in the view and
     * are added to a specific category (e.g. labour).
     * @param cost Cost to be added
     */
    private void addCostToCategory(Cost cost) {
        CostType costType = CostType.fromString(cost.getQuestion().getFormInputs().get(0).getFormInputType().getTitle());
        CostHandler costHandler = getCostHandler(costType);
        CostItem costItem = costHandler.toCostItem(cost);
        CostCategory costCategory = costCategories.get(costType);
        costCategory.addCost(costItem);
    }

    private void calculateTotals() {
        for(CostCategory costCategory : costCategories.values()) {
            costCategory.calculateTotal();
        }
    }

    private void resetCosts() {
        for(CostCategory costCategory : costCategories.values()) {
            costCategory.setCosts(new ArrayList<>());
        }
    }

    public List<Cost> costItemsToCost(CostType costType, List<CostItem> costItems) {
        List<Cost> costs = new ArrayList<>();
        for(CostItem costItem : costItems) {
            CostHandler costHandler = getCostHandler(costType);
            costs.add(costHandler.toCost(costItem));
        }
        return costs;
    }

    private CostHandler getCostHandler(CostType costType) {
        switch(costType) {
            case LABOUR:
                return new LabourCostHandler();
            case CAPITAL_USAGE:
                return new CapitalUsageHandler();
            case MATERIALS:
                return new MaterialsHandler();
            case OTHER_COSTS:
                return new OtherCostHandler();
            case OVERHEADS:
                return new OverheadsHandler();
            case SUBCONTRACTING_COSTS:
                return new SubContractingCostHandler();
            case TRAVEL:
                return new TravelCostHandler();
            case FINANCE:
                return new GrantClaimHandler();
            case OTHER_FUNDING:
                return new OtherFundingHandler();
        }
        log.error("Not a valid CostType: " + costType);
        throw new IllegalArgumentException("Not a valid CostType: " + costType);
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
}
