package org.innovateuk.ifs.finance.builder;

import org.innovateuk.ifs.finance.resource.cost.GrantClaim;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.idBasedNames;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;

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