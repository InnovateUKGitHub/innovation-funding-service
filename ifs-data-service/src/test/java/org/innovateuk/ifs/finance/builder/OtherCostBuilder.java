package org.innovateuk.ifs.finance.builder;

import org.innovateuk.ifs.finance.resource.cost.OtherCost;

import java.math.BigDecimal;
import java.util.List;
import java.util.function.BiConsumer;

import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.idBasedNames;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;
import static java.util.Collections.emptyList;

public class OtherCostBuilder extends AbstractCostBuilder<OtherCost, OtherCostBuilder> {

    public OtherCostBuilder withId(Long... id) {
        return withArraySetFieldByReflection("id", id);
    }

    public OtherCostBuilder withDescription(String... value) {
        return withArraySetFieldByReflection("description", value);
    }

    public OtherCostBuilder withName(String... value) {
        return withArraySetFieldByReflection("name", value);
    }

    public OtherCostBuilder withCost(BigDecimal... value) {
        return withArraySetFieldByReflection("cost", value);
    }

    public static OtherCostBuilder newOtherCost() {
        return new OtherCostBuilder(emptyList()).with(uniqueIds()).with(idBasedNames("Other cost "));
    }

    private OtherCostBuilder(List<BiConsumer<Integer, OtherCost>> multiActions) {
        super(multiActions);
    }

    @Override
    protected OtherCostBuilder createNewBuilderWithActions(List<BiConsumer<Integer, OtherCost>> actions) {
        return new OtherCostBuilder(actions);
    }

    @Override
    protected OtherCost createInitial() {
        return new OtherCost();
    }
}
