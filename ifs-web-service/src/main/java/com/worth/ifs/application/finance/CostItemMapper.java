package com.worth.ifs.application.finance;

import com.worth.ifs.application.finance.cost.*;
import com.worth.ifs.application.finance.model.OrganisationFinance;
import com.worth.ifs.finance.domain.Cost;
import com.worth.ifs.finance.domain.CostField;
import com.worth.ifs.finance.domain.CostValue;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * {@code CostItemMapper} maps cost items which are based on the representation for view to
 * costs which can be handled by the rest service, which is modeled generically. Each of the
 * different types of costs are mapped.
 */
public class CostItemMapper {
    private final Log log = LogFactory.getLog(getClass());
    Map<String, CostField> costFields = new HashMap<>();

    public CostItemMapper(List<CostField> costFields) {
        this.costFields = costFields.stream().collect(Collectors.toMap(CostField::getTitle, Function.<CostField>identity()));
    }

    public List<Cost> costItemsToCost(CostType costType, List<CostItem> costItems) {
        List<Cost> costs = new ArrayList<>();
        for(CostItem costItem : costItems) {
            costs.add(costItemToCost(costType, costItem));
        }
        return costs;
    }

    public Cost costItemToCost(CostType costType, CostItem costItem) {
        switch(costType) {
            case LABOUR:
                LabourCost labourCostItem = (LabourCost) costItem;
                return new Cost(labourCostItem.getId(),labourCostItem.getRole(), labourCostItem.getDescription(), labourCostItem.getLabourDays(), labourCostItem.getGrossAnnualSalary(), null, null);
            case CAPITAL_USAGE:
               return mapCapitalUsage(costItem);
            case MATERIALS:
                Materials materials = (Materials) costItem;
                return new Cost(costItem.getId(), materials.getItem(), "", materials.getQuantity(), materials.getCost(),null, null);
            case OTHER_COSTS:
                OtherCost otherCost = (OtherCost) costItem;
                return new Cost(otherCost.getId(), "", otherCost.getDescription(), 0, otherCost.getCost(), null, null);
            case OVERHEADS:
                Overhead overhead = (Overhead) costItem;
                return new Cost(overhead.getId(), overhead.getAcceptRate(), "", overhead.getCustomRate(), 0D, null, null);
            case SUBCONTRACTING_COSTS:
                return mapSubContractingCost(costItem);
            case TRAVEL:
                TravelCost travel = (TravelCost) costItem;
                return new Cost(travel.getId(), travel.getItem(), "", travel.getQuantity(), travel.getCostPerItem(), null, null);
            case FINANCE:
                GrantClaim grantClaim = (GrantClaim) costItem;
                return new Cost(grantClaim.getId(), "", OrganisationFinance.GRANT_CLAIM, grantClaim.getGrantClaimPercentage(), 0D, null,null);
        }

        throw new IllegalArgumentException("Not a valid CostType: " + costType);
    }

    public Cost mapCapitalUsage(CostItem costItem) {
        CapitalUsage capitalUsage = (CapitalUsage) costItem;
        Cost capitalUsageCost = new Cost(capitalUsage.getId(), "",  capitalUsage.getDescription(), capitalUsage.getDeprecation(),
                capitalUsage.getNpv(), null, null);
        capitalUsageCost.getCostValues().add(
                new CostValue(capitalUsageCost, costFields.get(CostItemFactory.COST_FIELD_EXISTING), capitalUsage.getExisting()));
        capitalUsageCost.getCostValues().add(
                new CostValue(capitalUsageCost, costFields.get(CostItemFactory.COST_FIELD_RESIDUAL_VALUE), String.valueOf(capitalUsage.getResidualValue())));
        capitalUsageCost.getCostValues().add(
                new CostValue(capitalUsageCost, costFields.get(CostItemFactory.COST_FIELD_UTILISATION), String.valueOf(capitalUsage.getUtilisation())));

        return capitalUsageCost;
    }

    public Cost mapSubContractingCost(CostItem costItem) {
        SubContractingCost subContractingCost = (SubContractingCost) costItem;
        Cost cost =  new Cost(subContractingCost.getId(), subContractingCost.getName(), subContractingCost.getRole(),
               0, subContractingCost.getCost(),null,null);
        cost.getCostValues().add(
                new CostValue(cost, costFields.get(CostItemFactory.COST_FIELD_COUNTRY), subContractingCost.getCountry()));
        return cost;
    }

}
