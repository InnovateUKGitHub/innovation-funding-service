package org.innovateuk.ifs.finance.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.finance.resource.cost.AssociateSupportCost;

import java.math.BigInteger;
import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;

public class AssociateSupportCostBuilder extends BaseBuilder<AssociateSupportCost, AssociateSupportCostBuilder> {

    public AssociateSupportCostBuilder withId(Long... id) {
        return withArraySetFieldByReflection("id", id);
    }

    public AssociateSupportCostBuilder withCost(BigInteger... value) {
        return withArraySetFieldByReflection("cost", value);
    }

    public AssociateSupportCostBuilder withDescription(String... value) {
        return withArraySetFieldByReflection("description", value);
    }

    public AssociateSupportCostBuilder withName(String... value) {
        return withArraySetFieldByReflection("name", value);
    }

    public AssociateSupportCostBuilder withTargetId(Long... value) {
        return withArraySetFieldByReflection("targetId", value);
    }

    public static AssociateSupportCostBuilder newAssociateSupportCost() {
        return new AssociateSupportCostBuilder(emptyList()).with(uniqueIds());
    }

    private AssociateSupportCostBuilder(List<BiConsumer<Integer, AssociateSupportCost>> multiActions) {
        super(multiActions);
    }

    @Override
    protected AssociateSupportCostBuilder createNewBuilderWithActions(List<BiConsumer<Integer, AssociateSupportCost>> actions) {
        return new AssociateSupportCostBuilder(actions);
    }

    @Override
    protected AssociateSupportCost createInitial() {
        return newInstance(AssociateSupportCost.class);
    }
}