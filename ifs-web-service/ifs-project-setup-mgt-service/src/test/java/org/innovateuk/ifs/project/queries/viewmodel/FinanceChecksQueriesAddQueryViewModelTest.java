package org.innovateuk.ifs.project.queries.viewmodel;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.innovateuk.ifs.threads.resource.FinanceChecksSectionType.*;

public class FinanceChecksQueriesAddQueryViewModelTest {

    @Test
    public void sectionTypesForNonProcurementMilestonesCompetition() {
        FinanceChecksQueriesAddQueryViewModel viewModel = viewModel(false, false);

        assertThat(viewModel.getSectionTypes())
                .containsExactly(ELIGIBILITY, VIABILITY);
    }

    @Test
    public void sectionTypesForProcurementMilestonesCompetition() {
        FinanceChecksQueriesAddQueryViewModel viewModel = viewModel(true, false);

        assertThat(viewModel.getSectionTypes())
                .containsExactly(ELIGIBILITY, VIABILITY, PAYMENT_MILESTONES);
    }

    @Test
    public void sectionTypesForSubsidyControlCompetition() {
        FinanceChecksQueriesAddQueryViewModel viewModel = viewModel(false, true);

        assertThat(viewModel.getSectionTypes())
                .containsExactly(ELIGIBILITY, VIABILITY, FUNDING_RULES);
    }

    @Test
    public void sectionTypesForProcurementMilestonesSubsidyControlCompetition() {
        FinanceChecksQueriesAddQueryViewModel viewModel = viewModel(true, true);

        assertThat(viewModel.getSectionTypes())
                .containsExactly(ELIGIBILITY, VIABILITY, PAYMENT_MILESTONES, FUNDING_RULES);
    }

    private FinanceChecksQueriesAddQueryViewModel viewModel(boolean procurementMilestonesCompetition, boolean subsidyControlCompetition) {
        return new FinanceChecksQueriesAddQueryViewModel(null,
                false,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                0,
                0,
                0,
                null,
                null,
                null,
                procurementMilestonesCompetition,
                subsidyControlCompetition);
    }
}
