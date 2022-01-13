package org.innovateuk.ifs.finance.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.finance.resource.FinanceRowMetaFieldResource;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.*;

/**
 * Builder for FinanceRowMetaField entities.
 */
public class FinanceRowMetaFieldResourceBuilder extends BaseBuilder<FinanceRowMetaFieldResource, FinanceRowMetaFieldResourceBuilder> {

    private FinanceRowMetaFieldResourceBuilder(List<BiConsumer<Integer, FinanceRowMetaFieldResource>> newMultiActions) {
        super(newMultiActions);
    }

    public static FinanceRowMetaFieldResourceBuilder newFinanceRowMetaFieldResource() {
        return new FinanceRowMetaFieldResourceBuilder(emptyList()).
                with(uniqueIds()).
                with(idBasedTitles("Title ")).
                with(idBasedTypes("Type "));
    }

    @Override
    protected FinanceRowMetaFieldResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, FinanceRowMetaFieldResource>> actions) {
        return new FinanceRowMetaFieldResourceBuilder(actions);
    }

    @Override
    protected FinanceRowMetaFieldResource createInitial() {
        return new FinanceRowMetaFieldResource();
    }
}
