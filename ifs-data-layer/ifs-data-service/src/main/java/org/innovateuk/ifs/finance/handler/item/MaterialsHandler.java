package org.innovateuk.ifs.finance.handler.item;

import org.innovateuk.ifs.finance.domain.ApplicationFinanceRow;
import org.innovateuk.ifs.finance.domain.FinanceRow;
import org.innovateuk.ifs.finance.domain.ProjectFinanceRow;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.innovateuk.ifs.finance.resource.cost.Materials;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;

import javax.validation.constraints.NotNull;

import java.util.Optional;

import static org.innovateuk.ifs.finance.resource.cost.FinanceRowType.MATERIALS;

/**
 * Handles the material costs, i.e. converts the costs to be stored into the database
 * or for sending it over.
 */
@Component
public class MaterialsHandler extends FinanceRowHandler<Materials> {
    public static final String COST_KEY = "materials";

    @Override
    public void validate(@NotNull Materials materials, @NotNull BindingResult bindingResult) {
        super.validate(materials, bindingResult);
    }

    @Override
    public ApplicationFinanceRow toApplicationDomain(Materials materials) {
        return new ApplicationFinanceRow(materials.getId(), COST_KEY, materials.getItem(), "", materials.getQuantity(), materials.getCost(),null, materials.getCostType());
    }

    @Override
    public ProjectFinanceRow toProjectDomain(Materials materials) {
        return new ProjectFinanceRow(materials.getId(), COST_KEY, materials.getItem(), "", materials.getQuantity(), materials.getCost(),null, materials.getCostType());
    }

    @Override
    public Materials toResource(FinanceRow cost) {
        return new Materials(cost.getId(),cost.getItem(),cost.getCost(),cost.getQuantity(), cost.getTarget().getId());
    }

    @Override
    public Optional<FinanceRowType> getFinanceRowType() {
        return Optional.of(MATERIALS);
    }
}
