package com.worth.ifs.finance.handler.item;

import com.worth.ifs.finance.domain.ApplicationFinanceRow;
import com.worth.ifs.finance.resource.cost.FinanceRowItem;
import com.worth.ifs.finance.resource.cost.Materials;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.validation.BindingResult;

import javax.validation.constraints.NotNull;

/**
 * Handles the material costs, i.e. converts the costs to be stored into the database
 * or for sending it over.
 */
public class MaterialsHandler extends FinanceRowHandler {
    private static final Log LOG = LogFactory.getLog(MaterialsHandler.class);
    public static final String COST_KEY = "materials";

    @Override
    public void validate(@NotNull FinanceRowItem costItem, @NotNull BindingResult bindingResult) {
        super.validate(costItem, bindingResult);
    }

    @Override
    public ApplicationFinanceRow toCost(FinanceRowItem costItem) {
        ApplicationFinanceRow cost = null;
        if (costItem instanceof Materials) {
            Materials materials = (Materials) costItem;
            cost = new ApplicationFinanceRow(materials.getId(), COST_KEY, materials.getItem(), "", materials.getQuantity(), materials.getCost(),null, null);
        }
        return cost;
    }

    @Override
    public FinanceRowItem toCostItem(ApplicationFinanceRow cost) {
        return new Materials(cost.getId(),cost.getItem(),cost.getCost(),cost.getQuantity());
    }
}
