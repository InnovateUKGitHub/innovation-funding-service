package org.innovateuk.ifs.finance.handler.item;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.finance.domain.ApplicationFinanceRow;
import org.innovateuk.ifs.finance.domain.FinanceRow;
import org.innovateuk.ifs.finance.domain.ProjectFinanceRow;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowItem;
import org.innovateuk.ifs.finance.resource.cost.Materials;
import org.springframework.validation.BindingResult;

import javax.validation.constraints.NotNull;

/**
 * Handles the material costs, i.e. converts the costs to be stored into the database
 * or for sending it over.
 */
public class MaterialsHandler extends FinanceRowHandler<Materials> {
    private static final Log LOG = LogFactory.getLog(MaterialsHandler.class);
    public static final String COST_KEY = "materials";

    @Override
    public void validate(@NotNull Materials materials, @NotNull BindingResult bindingResult) {
        super.validate(materials, bindingResult);
    }

    @Override
    public ApplicationFinanceRow toCost(Materials materials) {
        return new ApplicationFinanceRow(materials.getId(), COST_KEY, materials.getItem(), "", materials.getQuantity(), materials.getCost(),null, null);
    }

    @Override
    public ProjectFinanceRow toProjectCost(Materials materials) {
        return new ProjectFinanceRow(materials.getId(), COST_KEY, materials.getItem(), "", materials.getQuantity(), materials.getCost(),null, null);
    }

    @Override
    public FinanceRowItem toCostItem(ApplicationFinanceRow cost) {
        return buildRowItem(cost);
    }

    @Override
    public FinanceRowItem toCostItem(ProjectFinanceRow cost) {
        return buildRowItem(cost);
    }

    private FinanceRowItem buildRowItem(FinanceRow cost){
        return new Materials(cost.getId(),cost.getItem(),cost.getCost(),cost.getQuantity());
    }
}
