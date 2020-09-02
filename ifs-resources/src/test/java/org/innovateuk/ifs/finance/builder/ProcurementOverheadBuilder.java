package org.innovateuk.ifs.finance.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.finance.resource.cost.ProcurementOverhead;

import java.math.BigDecimal;
import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;

public class ProcurementOverheadBuilder extends BaseBuilder<ProcurementOverhead, ProcurementOverheadBuilder> {

    public ProcurementOverheadBuilder withId(Long... id) {
        return withArraySetFieldByReflection("id", id);
    }

    public ProcurementOverheadBuilder withCompanyCost(Integer... value) {
        return withArraySetFieldByReflection("companyCost", value);
    }

    public ProcurementOverheadBuilder withProjectCost(BigDecimal... value) {
        return withArraySetFieldByReflection("projectCost", value);
    }

    public ProcurementOverheadBuilder withItem(String... value) {
        return withArraySetFieldByReflection("item", value);
    }

    public ProcurementOverheadBuilder withName(String... value) {
        return withArraySetFieldByReflection("name", value);
    }

    public ProcurementOverheadBuilder withTargetId(Long... value) {
        return withArraySetFieldByReflection("targetId", value);
    }

    public static ProcurementOverheadBuilder newProcurementOverhead() {
        return new ProcurementOverheadBuilder(emptyList()).with(uniqueIds());
    }

    private ProcurementOverheadBuilder(List<BiConsumer<Integer, ProcurementOverhead>> multiActions) {
        super(multiActions);
    }

    @Override
    protected ProcurementOverheadBuilder createNewBuilderWithActions(List<BiConsumer<Integer, ProcurementOverhead>> actions) {
        return new ProcurementOverheadBuilder(actions);
    }

    @Override
    protected ProcurementOverhead createInitial() {
        return newInstance(ProcurementOverhead.class);
    }
}