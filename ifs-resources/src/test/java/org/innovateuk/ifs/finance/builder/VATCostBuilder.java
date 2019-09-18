package org.innovateuk.ifs.finance.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.finance.resource.cost.Vat;

import java.math.BigDecimal;
import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;

public class VATCostBuilder extends BaseBuilder<Vat, VATCostBuilder> {

    public VATCostBuilder withId(Long... id) {
        return withArraySetFieldByReflection("id", id);
    }

    public VATCostBuilder withRegistered(Boolean... value) {
        return withArraySetFieldByReflection("registered", value);
    }

    public VATCostBuilder withRate(BigDecimal... value) {
        return withArraySetFieldByReflection("rate", value);
    }

    public VATCostBuilder withName(String... value) {
        return withArraySetFieldByReflection("name", value);
    }

    public static VATCostBuilder newVATCost() {
        return new VATCostBuilder(emptyList()).with(uniqueIds());
    }

    private VATCostBuilder(List<BiConsumer<Integer, Vat>> multiActions) {
        super(multiActions);
    }

    @Override
    protected VATCostBuilder createNewBuilderWithActions(List<BiConsumer<Integer, Vat>> actions) {
        return new VATCostBuilder(actions);
    }

    @Override
    protected Vat createInitial() {
        return newInstance(Vat.class);
    }
}