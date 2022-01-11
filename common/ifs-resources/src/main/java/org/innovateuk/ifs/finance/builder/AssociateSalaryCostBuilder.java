package org.innovateuk.ifs.finance.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.finance.resource.cost.AssociateSalaryCost;

import java.math.BigInteger;
import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;

public class AssociateSalaryCostBuilder extends BaseBuilder<AssociateSalaryCost, AssociateSalaryCostBuilder> {

    public AssociateSalaryCostBuilder withId(Long... id) {
        return withArraySetFieldByReflection("id", id);
    }

    public AssociateSalaryCostBuilder withDuration(Integer... value) {
        return withArraySetFieldByReflection("duration", value);
    }

    public AssociateSalaryCostBuilder withCost(BigInteger... value) {
        return withArraySetFieldByReflection("cost", value);
    }

    public AssociateSalaryCostBuilder withRole(String... value) {
        return withArraySetFieldByReflection("role", value);
    }

    public AssociateSalaryCostBuilder withName(String... value) {
        return withArraySetFieldByReflection("name", value);
    }

    public AssociateSalaryCostBuilder withTargetId(Long... value) {
        return withArraySetFieldByReflection("targetId", value);
    }

    public static AssociateSalaryCostBuilder newAssociateSalaryCost() {
        return new AssociateSalaryCostBuilder(emptyList()).with(uniqueIds());
    }

    private AssociateSalaryCostBuilder(List<BiConsumer<Integer, AssociateSalaryCost>> multiActions) {
        super(multiActions);
    }

    @Override
    protected AssociateSalaryCostBuilder createNewBuilderWithActions(List<BiConsumer<Integer, AssociateSalaryCost>> actions) {
        return new AssociateSalaryCostBuilder(actions);
    }

    @Override
    protected AssociateSalaryCost createInitial() {
        return newInstance(AssociateSalaryCost.class);
    }
}