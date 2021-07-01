package org.innovateuk.ifs.finance.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.finance.resource.cost.AdditionalCompanyCost;
import org.innovateuk.ifs.finance.resource.cost.AdditionalCompanyCost.AdditionalCompanyCostType;

import java.math.BigInteger;
import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;

public class AdditionalCompanyCostBuilder extends BaseBuilder<AdditionalCompanyCost, AdditionalCompanyCostBuilder> {

    public AdditionalCompanyCostBuilder withId(Long... id) {
        return withArraySetFieldByReflection("id", id);
    }

    public AdditionalCompanyCostBuilder withType(AdditionalCompanyCostType... value) {
        return withArraySetFieldByReflection("type", value);
    }

    public AdditionalCompanyCostBuilder withDescription(String... value) {
        return withArraySetFieldByReflection("description", value);
    }

    public AdditionalCompanyCostBuilder withCost(BigInteger... value) {
        return withArraySetFieldByReflection("cost", value);
    }

    public AdditionalCompanyCostBuilder withName(String... value) {
        return withArraySetFieldByReflection("name", value);
    }

    public AdditionalCompanyCostBuilder withTargetId(Long... value) {
        return withArraySetFieldByReflection("targetId", value);
    }

    public static AdditionalCompanyCostBuilder newAdditionalCompanyCost() {
        return new AdditionalCompanyCostBuilder(emptyList()).with(uniqueIds());
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