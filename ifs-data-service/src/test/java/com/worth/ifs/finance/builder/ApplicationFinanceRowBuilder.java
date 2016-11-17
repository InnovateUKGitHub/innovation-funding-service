package com.worth.ifs.finance.builder;

import com.worth.ifs.BaseBuilder;
import com.worth.ifs.finance.domain.ApplicationFinance;
import com.worth.ifs.finance.domain.ApplicationFinanceRow;

import java.util.List;
import java.util.function.BiConsumer;

import static com.worth.ifs.BuilderAmendFunctions.*;
import static java.util.Collections.emptyList;

/**
 * Builder for FinanceRow entities.
 */
public class ApplicationFinanceRowBuilder extends BaseBuilder<ApplicationFinanceRow, ApplicationFinanceRowBuilder> {

    private ApplicationFinanceRowBuilder(List<BiConsumer<Integer, ApplicationFinanceRow>> newMultiActions) {
        super(newMultiActions);
    }

    public static ApplicationFinanceRowBuilder newFinanceRow() {
        return new ApplicationFinanceRowBuilder(emptyList()).
                with(uniqueIds()).
                with(idBasedDescriptions("Description "));
    }

    @Override
    protected ApplicationFinanceRowBuilder createNewBuilderWithActions(List<BiConsumer<Integer, ApplicationFinanceRow>> actions) {
        return new ApplicationFinanceRowBuilder(actions);
    }

    @Override
    protected ApplicationFinanceRow createInitial() {
        return new ApplicationFinanceRow();
    }

    public ApplicationFinanceRowBuilder withItem(String item){
        return with(cost -> setField("item", item, cost));
    }

    public ApplicationFinanceRowBuilder withApplicationFinance(final ApplicationFinance applicationFinance) {
        return with(cost -> cost.setTarget(applicationFinance));
    }
}
