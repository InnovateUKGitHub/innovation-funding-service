package com.worth.ifs.finance.handler.item;

import com.worth.ifs.finance.domain.Cost;
import com.worth.ifs.finance.resource.cost.CostItem;
import com.worth.ifs.finance.resource.cost.Materials;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Handles the material costs, i.e. converts the costs to be stored into the database
 * or for sending it over.
 */
public class MaterialsHandler extends CostHandler {
    private static final Log LOG = LogFactory.getLog(MaterialsHandler.class);
    public static final String COST_KEY = "materials";

    @Override
    public Cost toCost(CostItem costItem) {
        Cost cost = null;
        if (costItem instanceof Materials) {
            Materials materials = (Materials) costItem;
            cost = new Cost(materials.getId(), COST_KEY, materials.getItem(), "", materials.getQuantity(), materials.getCost(),null, null);
        }
        return cost;
    }

    @Override
    public CostItem toCostItem(Cost cost) {
        return new Materials(cost.getId(),cost.getItem(),cost.getCost(),cost.getQuantity());
    }
}
