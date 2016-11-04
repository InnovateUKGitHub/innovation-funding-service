package com.worth.ifs.finance.builder;

import com.worth.ifs.BaseBuilder;
import com.worth.ifs.finance.resource.FinanceRowMetaFieldResource;

import java.util.List;
import java.util.function.BiConsumer;

import static com.worth.ifs.base.amend.BaseBuilderAmendFunctions.*;
import static java.util.Collections.emptyList;

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
