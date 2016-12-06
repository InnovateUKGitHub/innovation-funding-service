package com.worth.ifs.finance.builder;

import com.worth.ifs.finance.domain.ApplicationFinanceRow;
import com.worth.ifs.finance.domain.ProjectFinance;
import com.worth.ifs.finance.domain.ProjectFinanceRow;

import java.util.List;
import java.util.function.BiConsumer;

import static com.worth.ifs.BuilderAmendFunctions.idBasedDescriptions;
import static com.worth.ifs.BuilderAmendFunctions.uniqueIds;
import static java.util.Collections.emptyList;

/**
 * Builder for ProjectFinanceRow entities.
 */
public class ProjectFinanceRowBuilder extends BaseFinanceRowBuilder<ProjectFinance, ProjectFinanceRow, ProjectFinanceRowBuilder> {

    public ProjectFinanceRowBuilder withOriginalApplicationFinanceRow(ApplicationFinanceRow... value) {
        return withArray((v, financeRow) -> financeRow.setApplicationRowId(v.getId()), value);
    }

    private ProjectFinanceRowBuilder(List<BiConsumer<Integer, ProjectFinanceRow>> newMultiActions) {
        super(newMultiActions);
    }

    public static ProjectFinanceRowBuilder newProjectFinanceRow() {
        return new ProjectFinanceRowBuilder(emptyList()).
                with(uniqueIds()).
                with(idBasedDescriptions("Description "));
    }

    @Override
    protected ProjectFinanceRowBuilder createNewBuilderWithActions(List<BiConsumer<Integer, ProjectFinanceRow>> actions) {
        return new ProjectFinanceRowBuilder(actions);
    }

    @Override
    protected ProjectFinanceRow createInitial() {
        return new ProjectFinanceRow();
    }
}
