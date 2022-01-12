package org.innovateuk.ifs.finance.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.finance.resource.cost.KtpTravelCost;
import org.innovateuk.ifs.finance.resource.cost.KtpTravelCost.KtpTravelCostType;

import java.math.BigDecimal;
import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;

public class KtpTravelCostBuilder extends BaseBuilder<KtpTravelCost, KtpTravelCostBuilder> {

    public KtpTravelCostBuilder withId(Long... id) {
        return withArraySetFieldByReflection("id", id);
    }

    public KtpTravelCostBuilder withDescription(String... value) {
        return withArraySetFieldByReflection("description", value);
    }

    public KtpTravelCostBuilder withCost(BigDecimal... value) {
        return withArraySetFieldByReflection("cost", value);
    }

    public KtpTravelCostBuilder withType(KtpTravelCostType... value) {
        return withArraySetFieldByReflection("type", value);
    }

    public KtpTravelCostBuilder withQuantity(Integer... value) {
        return withArraySetFieldByReflection("quantity", value);
    }

    public static KtpTravelCostBuilder newKtpTravelCost() {
        return new KtpTravelCostBuilder(emptyList()).with(uniqueIds());
    }

    private KtpTravelCostBuilder(List<BiConsumer<Integer, KtpTravelCost>> multiActions) {
        super(multiActions);
    }

    @Override
    protected KtpTravelCostBuilder createNewBuilderWithActions(List<BiConsumer<Integer, KtpTravelCost>> actions) {
        return new KtpTravelCostBuilder(actions);
    }

    @Override
    protected KtpTravelCost createInitial() {
        return newInstance(KtpTravelCost.class);
    }
}
