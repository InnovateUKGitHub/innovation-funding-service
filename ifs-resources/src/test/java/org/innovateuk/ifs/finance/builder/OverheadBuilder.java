package org.innovateuk.ifs.finance.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.finance.resource.cost.Overhead;
import org.innovateuk.ifs.finance.resource.cost.OverheadRateType;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;

public class OverheadBuilder extends BaseBuilder<Overhead, OverheadBuilder> {

    public OverheadBuilder withId(Long... id) {
        return withArraySetFieldByReflection("id", id);
    }

    public OverheadBuilder withRateType(OverheadRateType... value) {
        return withArraySetFieldByReflection("rateType", value);
    }

    public OverheadBuilder withName(String... value) {
        return withArraySetFieldByReflection("name", value);
    }

    public OverheadBuilder withRate(Integer... value) {
        return withArraySetFieldByReflection("rate", value);
    }

    public static OverheadBuilder newOverhead() {
        return new OverheadBuilder(emptyList()).with(uniqueIds());
    }

    private OverheadBuilder(List<BiConsumer<Integer, Overhead>> multiActions) {
        super(multiActions);
    }

    @Override
    protected OverheadBuilder createNewBuilderWithActions(List<BiConsumer<Integer, Overhead>> actions) {
        return new OverheadBuilder(actions);
    }

    @Override
    protected Overhead createInitial() {
        return new Overhead();
    }
}
