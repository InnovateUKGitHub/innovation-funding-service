package org.innovateuk.ifs.finance.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.finance.resource.cost.AssociateDevelopmentCost;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;

public class AssociateDevelopmentCostBuilder extends BaseBuilder<AssociateDevelopmentCost, AssociateDevelopmentCostBuilder> {

    public AssociateDevelopmentCostBuilder withId(Long... id) {
        return withArraySetFieldByReflection("id", id);
    }

    public AssociateDevelopmentCostBuilder withDuration(Integer... value) {
        return withArraySetFieldByReflection("duration", value);
    }

    public AssociateDevelopmentCostBuilder withCost(BigInteger... value) {
        return withArraySetFieldByReflection("cost", value);
    }

    public AssociateDevelopmentCostBuilder withRole(String... value) {
        return withArraySetFieldByReflection("role", value);
    }

    public AssociateDevelopmentCostBuilder withName(String... value) {
        return withArraySetFieldByReflection("name", value);
    }

    public AssociateDevelopmentCostBuilder withTargetId(Long... value) {
        return withArraySetFieldByReflection("targetId", value);
    }

    public static AssociateDevelopmentCostBuilder newAssociateDevelopmentCost() {
        return new AssociateDevelopmentCostBuilder(emptyList()).with(uniqueIds());
    }

    private AssociateDevelopmentCostBuilder(List<BiConsumer<Integer, AssociateDevelopmentCost>> multiActions) {
        super(multiActions);
    }

    @Override
    protected AssociateDevelopmentCostBuilder createNewBuilderWithActions(List<BiConsumer<Integer, AssociateDevelopmentCost>> actions) {
        return new AssociateDevelopmentCostBuilder(actions);
    }

    @Override
    protected AssociateDevelopmentCost createInitial() {
        return newInstance(AssociateDevelopmentCost.class);
    }
}