package org.innovateuk.ifs.finance.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.finance.resource.cost.VAT;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.idBasedNames;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;

public class VATCostBuilder extends BaseBuilder<VAT, VATCostBuilder> {

    public VATCostBuilder withId(Long... id) {
        return withArraySetFieldByReflection("id", id);
    }

    public VATCostBuilder withRegistered(Boolean... value) {
        return withArraySetFieldByReflection("registered", value);
    }

    public VATCostBuilder withName(String... value) {
        return withArraySetFieldByReflection("name", value);
    }

    public static VATCostBuilder newVATCost() {
        return new VATCostBuilder(emptyList()).with(uniqueIds()).with(idBasedNames("VAT "));
    }

    private VATCostBuilder(List<BiConsumer<Integer, VAT>> multiActions) {
        super(multiActions);
    }

    @Override
    protected VATCostBuilder createNewBuilderWithActions(List<BiConsumer<Integer, VAT>> actions) {
        return new VATCostBuilder(actions);
    }

    @Override
    protected VAT createInitial() {
        return newInstance(VAT.class);
    }
}