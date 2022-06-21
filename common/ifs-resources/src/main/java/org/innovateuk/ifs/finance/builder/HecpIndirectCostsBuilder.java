package org.innovateuk.ifs.finance.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.finance.resource.cost.HecpIndirectCosts;
import org.innovateuk.ifs.finance.resource.cost.OverheadRateType;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;

public class HecpIndirectCostsBuilder extends BaseBuilder<HecpIndirectCosts, HecpIndirectCostsBuilder> {

    public HecpIndirectCostsBuilder withId(Long... id) {
        return withArraySetFieldByReflection("id", id);
    }

    public HecpIndirectCostsBuilder withRateType(OverheadRateType... value) {
        return withArraySetFieldByReflection("rateType", value);
    }

    public HecpIndirectCostsBuilder withName(String... value) {
        return withArraySetFieldByReflection("name", value);
    }

    public HecpIndirectCostsBuilder withRate(Integer... value) {
        return withArraySetFieldByReflection("rate", value);
    }

    public static HecpIndirectCostsBuilder newHecpIndirectCosts() {
        return new HecpIndirectCostsBuilder(emptyList()).with(uniqueIds());
    }

    private HecpIndirectCostsBuilder(List<BiConsumer<Integer, HecpIndirectCosts>> multiActions) {
        super(multiActions);
    }

    @Override
    protected HecpIndirectCostsBuilder createNewBuilderWithActions(List<BiConsumer<Integer, HecpIndirectCosts>> actions) {
        return new HecpIndirectCostsBuilder(actions);
    }

    @Override
    protected HecpIndirectCosts createInitial() {
        return newInstance(HecpIndirectCosts.class);
    }
}
