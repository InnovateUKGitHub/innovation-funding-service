package org.innovateuk.ifs.finance.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.finance.resource.cost.AcademicCost;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;

import java.math.BigDecimal;
import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.idBasedNames;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;

public class AcademicCostBuilder extends BaseBuilder<AcademicCost, AcademicCostBuilder> {

    public static AcademicCostBuilder newAcademicCost() {
        return new AcademicCostBuilder(emptyList()).with(uniqueIds()).with(idBasedNames("AcademicCost "));
    }

    public AcademicCostBuilder withId(Long... value) {
        return withArraySetFieldByReflection("id", value);
    }

    public AcademicCostBuilder withName(String... value) {
        return withArraySetFieldByReflection("name", value);
    }

    public AcademicCostBuilder withItem(String... value) {
        return withArray((v, cost) -> cost.setItem(v), value);
    }

    public AcademicCostBuilder withCostType(FinanceRowType... value) {
        return withArraySetFieldByReflection("costType", value);
    }

    public AcademicCostBuilder withCost(BigDecimal... value) {
        return withArray((v, cost) -> cost.setCost(v), value);
    }

    private AcademicCostBuilder(List<BiConsumer<Integer, AcademicCost>> multiActions) {
        super(multiActions);
    }

    @Override
    protected AcademicCostBuilder createNewBuilderWithActions(List<BiConsumer<Integer, AcademicCost>> actions) {
        return new AcademicCostBuilder(actions);
    }

    @Override
    protected AcademicCost createInitial() {
        return new AcademicCost();
    }
}
