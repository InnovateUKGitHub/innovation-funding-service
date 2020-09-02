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

    /*

    private Integer companyCost;
    private BigDecimal projectCost;
    private String item;
    private String name;
     */
    public ProcurementOverheadBuilder withRegistered(Boolean... value) {
        return withArraySetFieldByReflection("registered", value);
    }

    public ProcurementOverheadBuilder withRate(BigDecimal... value) {
        return withArraySetFieldByReflection("rate", value);
    }

    public ProcurementOverheadBuilder withName(String... value) {
        return withArraySetFieldByReflection("name", value);
    }

    public ProcurementOverheadBuilder withTargetId(Long... value) {
        return withArraySetFieldByReflection("targetId", value);
    }

    public static ProcurementOverheadBuilder newVATCost() {
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