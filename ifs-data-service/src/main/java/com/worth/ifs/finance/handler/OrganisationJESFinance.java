package com.worth.ifs.finance.handler;

import com.worth.ifs.finance.domain.ApplicationFinance;
import com.worth.ifs.finance.domain.Cost;
import com.worth.ifs.finance.resource.category.CostCategory;
import com.worth.ifs.finance.resource.cost.CostItem;
import com.worth.ifs.finance.resource.cost.CostType;
import org.springframework.stereotype.Component;

import java.util.EnumMap;

@Component
public class OrganisationJESFinance implements OrganisationFinanceHandler {
    @Override
    public Iterable<Cost> initialiseCostType(ApplicationFinance applicationFinance, CostType costType) {
        return null;
    }

    @Override
    public EnumMap<CostType, CostCategory> getOrganisationFinances(Long applicationFinanceId) {
        return null;
    }

    @Override
    public EnumMap<CostType, CostCategory> getOrganisationFinanceTotals(Long id) {
        return new EnumMap<CostType,CostCategory>(CostType.class);
    }

    @Override
    public Cost costItemToCost(CostItem costItem) {
        return null;
    }

    @Override
    public CostItem costToCostItem(Cost cost) {
        return null;
    }
}
