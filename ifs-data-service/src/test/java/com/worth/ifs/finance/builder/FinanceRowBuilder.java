package com.worth.ifs.finance.builder;

import com.worth.ifs.BaseBuilder;
import com.worth.ifs.finance.domain.ApplicationFinance;
import com.worth.ifs.finance.domain.FinanceRow;

import java.util.List;
import java.util.function.BiConsumer;

import static com.worth.ifs.base.amend.BaseBuilderAmendFunctions.idBasedDescriptions;
import static com.worth.ifs.base.amend.BaseBuilderAmendFunctions.setField;
import static com.worth.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;
import static java.util.Collections.emptyList;

/**
 * Builder for FinanceRow entities.
 */
public class FinanceRowBuilder extends BaseBuilder<FinanceRow, FinanceRowBuilder> {

    private FinanceRowBuilder(List<BiConsumer<Integer, FinanceRow>> newMultiActions) {
        super(newMultiActions);
    }

    public static FinanceRowBuilder newFinanceRow() {
        return new FinanceRowBuilder(emptyList()).
                with(uniqueIds()).
                with(idBasedDescriptions("Description "));
    }

    @Override
    protected FinanceRowBuilder createNewBuilderWithActions(List<BiConsumer<Integer, FinanceRow>> actions) {
        return new FinanceRowBuilder(actions);
    }

    @Override
    protected FinanceRow createInitial() {
        return new FinanceRow();
    }

    public FinanceRowBuilder withItem(String item){
        return with(cost -> setField("item", item, cost));
    }

    public FinanceRowBuilder withApplicationFinance(final ApplicationFinance applicationFinance) {
        return with(cost -> cost.setApplicationFinance(applicationFinance));
    }
}
