package org.innovateuk.ifs.finance.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.finance.resource.cost.AdditionalCompanyCost;

import java.math.BigInteger;
import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;

import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;

public class AdditionalCompanyCostBuilder extends BaseBuilder<AdditionalCompanyCost, AdditionalCompanyCostBuilder> {

    public static AdditionalCompanyCostBuilder newAdditionalCompanyCost() {
        return new AdditionalCompanyCostBuilder(Collections.emptyList()).with(uniqueIds());
    }

    public AdditionalCompanyCostBuilder withId(Long... value) {
        return withArraySetFieldByReflection("id", value);
    }

    public AdditionalCompanyCostBuilder withType(AdditionalCompanyCost.AdditionalCompanyCostType... value) {
        return withArraySetFieldByReflection("type", value);
    }

    public AdditionalCompanyCostBuilder withDescription(String... value) {
        return withArraySetFieldByReflection("description", value);
    }

    public AdditionalCompanyCostBuilder withCost(BigInteger... value) {
        return withArraySetFieldByReflection("cost", value);
    }

    private AdditionalCompanyCostBuilder(List<BiConsumer<Integer, AdditionalCompanyCost>> multiActions) {
        super(multiActions);
    }

    @Override
    protected AdditionalCompanyCostBuilder createNewBuilderWithActions(List<BiConsumer<Integer, AdditionalCompanyCost>> actions) {
        return new AdditionalCompanyCostBuilder(actions);
    }

    @Override
    protected AdditionalCompanyCost createInitial() {
        return newInstance(AdditionalCompanyCost.class);
    }
}
