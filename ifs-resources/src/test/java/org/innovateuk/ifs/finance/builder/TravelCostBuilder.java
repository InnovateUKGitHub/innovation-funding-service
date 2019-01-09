package org.innovateuk.ifs.finance.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.finance.resource.cost.TravelCost;

import java.math.BigDecimal;
import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;

public class TravelCostBuilder extends BaseBuilder<TravelCost, TravelCostBuilder> {

    public TravelCostBuilder withId(Long... id) {
        return withArraySetFieldByReflection("id", id);
    }

    public TravelCostBuilder withItem(String... value) {
        return withArraySetFieldByReflection("item", value);
    }

    public TravelCostBuilder withCost(BigDecimal... value) {
        return withArraySetFieldByReflection("cost", value);
    }

    public TravelCostBuilder withQuantity(Integer... value) {
        return withArraySetFieldByReflection("quantity", value);
    }

    public static TravelCostBuilder newTravelCost() {
        return new TravelCostBuilder(emptyList()).with(uniqueIds());
    }

    private TravelCostBuilder(List<BiConsumer<Integer, TravelCost>> multiActions) {
        super(multiActions);
    }

    @Override
    protected TravelCostBuilder createNewBuilderWithActions(List<BiConsumer<Integer, TravelCost>> actions) {
        return new TravelCostBuilder(actions);
    }

    @Override
    protected TravelCost createInitial() {
        return new TravelCost();
    }
}
