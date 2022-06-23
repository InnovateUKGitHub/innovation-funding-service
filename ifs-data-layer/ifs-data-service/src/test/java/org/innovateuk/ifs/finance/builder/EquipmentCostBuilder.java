package org.innovateuk.ifs.finance.builder;

import org.innovateuk.ifs.finance.resource.cost.Equipment;

import java.math.BigDecimal;
import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;

public class EquipmentCostBuilder extends AbstractCostBuilder<Equipment, EquipmentCostBuilder> {

    public EquipmentCostBuilder withId(Long... id) {
        return withArraySetFieldByReflection("id", id);
    }

    public EquipmentCostBuilder withItem(String... value) {
        return withArraySetFieldByReflection("item", value);
    }

    public EquipmentCostBuilder withCost(BigDecimal... value) {
        return withArraySetFieldByReflection("cost", value);
    }

    public EquipmentCostBuilder withQuantity(Integer... value) {
        return withArraySetFieldByReflection("quantity", value);
    }

    public static EquipmentCostBuilder newEquipment() {
        return new EquipmentCostBuilder(emptyList()).with(uniqueIds());
    }

    private EquipmentCostBuilder(List<BiConsumer<Integer, Equipment>> multiActions) {
        super(multiActions);
    }

    @Override
    protected EquipmentCostBuilder createNewBuilderWithActions(List<BiConsumer<Integer, Equipment>> actions) {
        return new EquipmentCostBuilder(actions);
    }

    @Override
    protected Equipment createInitial() {
        return newInstance(Equipment.class);
    }
}
