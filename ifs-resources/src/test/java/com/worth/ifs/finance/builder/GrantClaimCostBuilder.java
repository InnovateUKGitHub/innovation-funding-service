package com.worth.ifs.finance.builder;

import com.worth.ifs.finance.resource.cost.GrantClaim;

import java.util.List;
import java.util.function.BiConsumer;

import static com.worth.ifs.base.amend.BaseBuilderAmendFunctions.idBasedNames;
import static com.worth.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;
import static java.util.Collections.emptyList;

public class GrantClaimCostBuilder extends AbstractCostBuilder<GrantClaim, GrantClaimCostBuilder> {

    public GrantClaimCostBuilder withId(Long... id) {
        return withArraySetFieldByReflection("id", id);
    }

    public GrantClaimCostBuilder withGrantClaimPercentage(Integer... value) {
        return withArray((v, cost) -> cost.setGrantClaimPercentage(v), value);
    }

    public static GrantClaimCostBuilder newGrantClaim() {
        return new GrantClaimCostBuilder(emptyList()).with(uniqueIds()).with(idBasedNames("Grant Claim "));
    }

    private GrantClaimCostBuilder(List<BiConsumer<Integer, GrantClaim>> multiActions) {
        super(multiActions);
    }

    @Override
    protected GrantClaimCostBuilder createNewBuilderWithActions(List<BiConsumer<Integer, GrantClaim>> actions) {
        return new GrantClaimCostBuilder(actions);
    }

    @Override
    protected GrantClaim createInitial() {
        return new GrantClaim();
    }
}
