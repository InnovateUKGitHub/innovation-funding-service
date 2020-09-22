package org.innovateuk.ifs.finance.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.finance.resource.cost.Consumable;

import java.math.BigInteger;
import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;

public class ConsumablesBuilder extends BaseBuilder<Consumable, ConsumablesBuilder> {

    public ConsumablesBuilder withId(Long... id) {
        return withArraySetFieldByReflection("id", id);
    }

    public ConsumablesBuilder withItem(String... value) {
        return withArraySetFieldByReflection("item", value);
    }

    public ConsumablesBuilder withCost(BigInteger... value) {
        return withArraySetFieldByReflection("cost", value);
    }

    public ConsumablesBuilder withQuantity(Integer... value) {
        return withArraySetFieldByReflection("quantity", value);
    }

    public static ConsumablesBuilder newConsumable() {
        return new ConsumablesBuilder(emptyList()).with(uniqueIds());
    }

    private ConsumablesBuilder(List<BiConsumer<Integer, Consumable>> multiActions) {
        super(multiActions);
    }

    @Override
    protected ConsumablesBuilder createNewBuilderWithActions(List<BiConsumer<Integer, Consumable>> actions) {
        return new ConsumablesBuilder(actions);
    }

    @Override
    protected Consumable createInitial() {
        return newInstance(Consumable.class);
    }
}
