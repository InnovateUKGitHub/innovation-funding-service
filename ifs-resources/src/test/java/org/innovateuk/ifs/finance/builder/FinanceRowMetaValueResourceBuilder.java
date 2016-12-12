package org.innovateuk.ifs.finance.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.finance.resource.FinanceRowMetaValueResource;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;

/**
 * Builder for {@link FinanceRowMetaValueResource} entities.
 */
public class FinanceRowMetaValueResourceBuilder extends BaseBuilder<FinanceRowMetaValueResource, FinanceRowMetaValueResourceBuilder> {

    public FinanceRowMetaValueResourceBuilder withFinanceRowMetaField(final Long... costFieldId){
        return withArray((v, metaValue) -> metaValue.setFinanceRowMetaField(v), costFieldId);
    }

    public FinanceRowMetaValueResourceBuilder withFinanceRow(final Long... costId) {
        return withArray((v, metaValue) -> metaValue.setFinanceRowId(v), costId);
    }

    public FinanceRowMetaValueResourceBuilder withValue(final String... value) {
        return withArray((v, metaValue) -> metaValue.setValue(v), value);
    }

    private FinanceRowMetaValueResourceBuilder(List<BiConsumer<Integer, FinanceRowMetaValueResource>> newMultiActions) {
        super(newMultiActions);
    }

    public static FinanceRowMetaValueResourceBuilder newFinanceRowMetaValueResource() {
        return new FinanceRowMetaValueResourceBuilder(emptyList());
    }

    @Override
    protected FinanceRowMetaValueResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, FinanceRowMetaValueResource>> actions) {
        return new FinanceRowMetaValueResourceBuilder(actions);
    }

    @Override
    protected FinanceRowMetaValueResource createInitial() {
        return new FinanceRowMetaValueResource();
    }
}
