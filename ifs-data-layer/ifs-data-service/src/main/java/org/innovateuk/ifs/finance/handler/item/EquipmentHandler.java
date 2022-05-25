package org.innovateuk.ifs.finance.handler.item;

import org.innovateuk.ifs.finance.domain.ApplicationFinanceRow;
import org.innovateuk.ifs.finance.domain.FinanceRow;
import org.innovateuk.ifs.finance.domain.ProjectFinanceRow;
import org.innovateuk.ifs.finance.resource.cost.Equipment;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;

import javax.validation.constraints.NotNull;
import java.util.Optional;

import static org.innovateuk.ifs.finance.resource.cost.FinanceRowType.EQUIPMENT;

/**
 * Handles the Equipment costs, i.e. converts the costs to be stored into the database
 * or for sending it over.
 */
@Component
public class EquipmentHandler extends FinanceRowHandler<Equipment> {
    public static final String COST_KEY = "equipment";

    @Override
    public void validate(@NotNull Equipment equipment, @NotNull BindingResult bindingResult) {
        super.validate(equipment, bindingResult);
    }

    @Override
    public ApplicationFinanceRow toApplicationDomain(Equipment equipment) {
        return new ApplicationFinanceRow(equipment.getId(), COST_KEY, equipment.getItem(), "", equipment.getQuantity(), equipment.getCost(),null, equipment.getCostType());
    }

    @Override
    public ProjectFinanceRow toProjectDomain(Equipment equipment) {
        return new ProjectFinanceRow(equipment.getId(), COST_KEY, equipment.getItem(), "", equipment.getQuantity(), equipment.getCost(),null, equipment.getCostType());
    }

    @Override
    public Equipment toResource(FinanceRow cost) {
        return new Equipment(cost.getId(),cost.getItem(),cost.getCost(),cost.getQuantity(), cost.getTarget().getId());
    }

    @Override
    public Optional<FinanceRowType> getFinanceRowType() {
        return Optional.of(EQUIPMENT);
    }
}
