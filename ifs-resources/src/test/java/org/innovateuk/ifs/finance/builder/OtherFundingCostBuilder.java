package org.innovateuk.ifs.finance.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.finance.resource.cost.OtherFunding;

import java.math.BigDecimal;
import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.idBasedNames;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;

public class OtherFundingCostBuilder extends BaseBuilder<OtherFunding, OtherFundingCostBuilder> {

    public OtherFundingCostBuilder withId(Long... id) {
        return withArraySetFieldByReflection("id", id);
    }

    public OtherFundingCostBuilder withName(String... value) {
        return withArray((v, cost) -> cost.setName(v), value);
    }

    public OtherFundingCostBuilder withOtherPublicFunding(String... value) {
        return withArray((v, cost) -> cost.setOtherPublicFunding(v), value);
    }

    public OtherFundingCostBuilder withSecuredDate(String... value) {
        return withArray((v, cost) -> cost.setSecuredDate(v), value);
    }

    public OtherFundingCostBuilder withFundingSource(String... value) {
        return withArray((v, cost) -> cost.setFundingSource(v), value);
    }


    public OtherFundingCostBuilder withFundingAmount(BigDecimal... value) {
        return withArray((v, cost) -> cost.setFundingAmount(v), value);
    }

    public static OtherFundingCostBuilder newOtherFunding() {
        return new OtherFundingCostBuilder(emptyList()).with(uniqueIds()).with(idBasedNames("Other Funding "));
    }

    private OtherFundingCostBuilder(List<BiConsumer<Integer, OtherFunding>> multiActions) {
        super(multiActions);
    }

    @Override
    protected OtherFundingCostBuilder createNewBuilderWithActions(List<BiConsumer<Integer, OtherFunding>> actions) {
        return new OtherFundingCostBuilder(actions);
    }

    @Override
    protected OtherFunding createInitial() {
        return new OtherFunding();
    }
}
