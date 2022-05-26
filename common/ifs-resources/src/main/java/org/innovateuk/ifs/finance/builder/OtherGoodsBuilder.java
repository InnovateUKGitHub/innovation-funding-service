package org.innovateuk.ifs.finance.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.finance.resource.cost.OtherGoods;

import java.math.BigDecimal;
import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;

public class OtherGoodsBuilder extends BaseBuilder<OtherGoods, OtherGoodsBuilder> {

    public OtherGoodsBuilder withId(Long... id) {
        return withArraySetFieldByReflection("id", id);
    }

    public OtherGoodsBuilder withDescription(String... value) {
        return withArraySetFieldByReflection("description", value);
    }

    public OtherGoodsBuilder withNpv(BigDecimal... value) {
        return withArraySetFieldByReflection("npv", value);
    }

    public OtherGoodsBuilder withDeprecation(Integer... value) {
        return withArraySetFieldByReflection("deprecation", value);
    }

    public OtherGoodsBuilder withExisting(String... value) {
        return withArraySetFieldByReflection("existing", value);
    }

    public OtherGoodsBuilder withResidualValue(BigDecimal... value) {
        return withArraySetFieldByReflection("residualValue", value);
    }

    public OtherGoodsBuilder withUtilisation(Integer... value) {
        return withArraySetFieldByReflection("utilisation", value);
    }

    public OtherGoodsBuilder withTargetId(Long... value) {
        return withArraySetFieldByReflection("targetId", value);
    }

    public static OtherGoodsBuilder newOtherGoods() {
        return new OtherGoodsBuilder(emptyList()).with(uniqueIds());
    }

    private OtherGoodsBuilder(List<BiConsumer<Integer, OtherGoods>> multiActions) {
        super(multiActions);
    }

    @Override
    protected OtherGoodsBuilder createNewBuilderWithActions(List<BiConsumer<Integer, OtherGoods>> actions) {
        return new OtherGoodsBuilder(actions);
    }

    @Override
    protected OtherGoods createInitial() {
        return newInstance(OtherGoods.class);
    }
}
