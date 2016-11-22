package com.worth.ifs.finance.builder;

import com.worth.ifs.BaseBuilder;
import com.worth.ifs.finance.domain.FinanceRowMetaField;

import java.util.List;
import java.util.function.BiConsumer;

import static com.worth.ifs.base.amend.BaseBuilderAmendFunctions.*;
import static java.util.Collections.emptyList;

/**
 * Builder for FinanceRowMetaField entities.
 */
public class FinanceRowMetaFieldBuilder extends BaseBuilder<FinanceRowMetaField, FinanceRowMetaFieldBuilder> {

    private FinanceRowMetaFieldBuilder(List<BiConsumer<Integer, FinanceRowMetaField>> newMultiActions) {
        super(newMultiActions);
    }

    public static FinanceRowMetaFieldBuilder newFinanceRowMetaField() {
        return new FinanceRowMetaFieldBuilder(emptyList()).
                with(uniqueIds()).
                with(idBasedTitles("Title ")).
                with(idBasedTypes("Type "));
    }

    @Override
    protected FinanceRowMetaFieldBuilder createNewBuilderWithActions(List<BiConsumer<Integer, FinanceRowMetaField>> actions) {
        return new FinanceRowMetaFieldBuilder(actions);
    }

    @Override
    protected FinanceRowMetaField createInitial() {
        return new FinanceRowMetaField();
    }
}
