package org.innovateuk.ifs.finance.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.finance.resource.cost.LabourCost;

import java.math.BigDecimal;
import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.idBasedNames;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;

public class LabourCostBuilder extends BaseBuilder<LabourCost, LabourCostBuilder> {

    public static LabourCostBuilder newLabourCost() {
        return new LabourCostBuilder(emptyList()).with(uniqueIds()).with(idBasedNames("LabourCost "));
    }

    public LabourCostBuilder withId(Long... value) {
        return withArraySetFieldByReflection("id", value);
    }

    public LabourCostBuilder withTargetId(Long... value) {
        return withArraySetFieldByReflection("targetId", value);
    }

    public LabourCostBuilder withName(String... value) {
        return withArraySetFieldByReflection("name", value);
    }

    public LabourCostBuilder withDescription(String... value) {
        return withArraySetFieldByReflection("description", value);
    }

    public LabourCostBuilder withRole(String... value) {
        return withArraySetFieldByReflection("role", value);
    }

    public LabourCostBuilder withGrossEmployeeCost(BigDecimal... value) {
        return withArraySetFieldByReflection("grossEmployeeCost", value);
    }

    public LabourCostBuilder withLabourDays(Integer... value) {
        return withArraySetFieldByReflection("labourDays", value);
    }

    public LabourCostBuilder withRate(BigDecimal... value) {
        return withArraySetFieldByReflection("rate", value);
    }

    public LabourCostBuilder withTotal(BigDecimal... value) {
        return withArraySetFieldByReflection("total", value);
    }

    private LabourCostBuilder(List<BiConsumer<Integer, LabourCost>> multiActions) {
        super(multiActions);
    }

    @Override
    protected LabourCostBuilder createNewBuilderWithActions(List<BiConsumer<Integer, LabourCost>> actions) {
        return new LabourCostBuilder(actions);
    }

    @Override
    protected LabourCost createInitial() {
        return newInstance(LabourCost.class);
    }
}
