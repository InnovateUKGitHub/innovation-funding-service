package org.innovateuk.ifs.finance.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.finance.resource.cost.CapitalUsage;

import java.math.BigDecimal;
import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;

public class CapitalUsageBuilder extends BaseBuilder<CapitalUsage, CapitalUsageBuilder> {

    public CapitalUsageBuilder withId(Long... id) {
        return withArraySetFieldByReflection("id", id);
    }

    public CapitalUsageBuilder withDescription(String... value) {
        return withArraySetFieldByReflection("description", value);
    }

    public CapitalUsageBuilder withNpv(BigDecimal... value) {
        return withArraySetFieldByReflection("npv", value);
    }

    public CapitalUsageBuilder withDeprecation(Integer... value) {
        return withArraySetFieldByReflection("deprecation", value);
    }

    public CapitalUsageBuilder withExisting(String... value) {
        return withArraySetFieldByReflection("existing", value);
    }

    public CapitalUsageBuilder withResidualValue(BigDecimal... value) {
        return withArraySetFieldByReflection("residualValue", value);
    }

    public CapitalUsageBuilder withUtilisation(Integer... value) {
        return withArraySetFieldByReflection("utilisation", value);
    }

    public static CapitalUsageBuilder newCapitalUsage() {
        return new CapitalUsageBuilder(emptyList()).with(uniqueIds());
    }

    private CapitalUsageBuilder(List<BiConsumer<Integer, CapitalUsage>> multiActions) {
        super(multiActions);
    }

    @Override
    protected CapitalUsageBuilder createNewBuilderWithActions(List<BiConsumer<Integer, CapitalUsage>> actions) {
        return new CapitalUsageBuilder(actions);
    }

    @Override
    protected CapitalUsage createInitial() {
        return new CapitalUsage();
    }
}
