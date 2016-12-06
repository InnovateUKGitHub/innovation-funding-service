package com.worth.ifs.finance.builder;

import com.worth.ifs.finance.domain.ApplicationFinance;
import com.worth.ifs.finance.domain.ApplicationFinanceRow;

import java.util.List;
import java.util.function.BiConsumer;

import static com.worth.ifs.BuilderAmendFunctions.idBasedDescriptions;
import static com.worth.ifs.BuilderAmendFunctions.uniqueIds;
import static java.util.Collections.emptyList;

/**
 * Builder for ApplicationFinanceRow entities.
 */
public class ApplicationFinanceRowBuilder extends BaseFinanceRowBuilder<ApplicationFinance, ApplicationFinanceRow, ApplicationFinanceRowBuilder> {

    private ApplicationFinanceRowBuilder(List<BiConsumer<Integer, ApplicationFinanceRow>> newMultiActions) {
        super(newMultiActions);
    }

    public static ApplicationFinanceRowBuilder newApplicationFinanceRow() {
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
}
