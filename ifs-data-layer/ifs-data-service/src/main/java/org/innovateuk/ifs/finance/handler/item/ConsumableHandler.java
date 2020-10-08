package org.innovateuk.ifs.finance.handler.item;

import org.innovateuk.ifs.finance.domain.ApplicationFinanceRow;
import org.innovateuk.ifs.finance.domain.FinanceRow;
import org.innovateuk.ifs.finance.domain.ProjectFinanceRow;
import org.innovateuk.ifs.finance.resource.cost.Consumable;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Optional;

import static java.util.Optional.ofNullable;
import static org.innovateuk.ifs.finance.resource.cost.FinanceRowType.CONSUMABLES;

/**
 * Handles the material costs, i.e. converts the costs to be stored into the database
 * or for sending it over.
 */
@Component
public class ConsumableHandler extends FinanceRowHandler<Consumable> {
    public static final String COST_KEY = "consumable";

    @Override
    public ApplicationFinanceRow toApplicationDomain(Consumable materials) {
        return new ApplicationFinanceRow(materials.getId(), COST_KEY, materials.getItem(), "", materials.getQuantity(), ofNullable(materials.getCost()).map(BigDecimal::new).orElse(null),null, materials.getCostType());
    }

    @Override
    public ProjectFinanceRow toProjectDomain(Consumable materials) {
        return new ProjectFinanceRow(materials.getId(), COST_KEY, materials.getItem(), "", materials.getQuantity(), ofNullable(materials.getCost()).map(BigDecimal::new).orElse(null),null, materials.getCostType());
    }

    @Override
    public Consumable toResource(FinanceRow cost) {
        return new Consumable(cost.getId(), cost.getItem(), bigIntegerOrNull(cost.getCost()), cost.getQuantity(), cost.getTarget().getId());
    }

    @Override
    public Optional<FinanceRowType> getFinanceRowType() {
        return Optional.of(CONSUMABLES);
    }
}
