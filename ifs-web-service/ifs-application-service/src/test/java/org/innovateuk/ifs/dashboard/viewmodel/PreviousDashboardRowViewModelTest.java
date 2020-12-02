package org.innovateuk.ifs.dashboard.viewmodel;

import org.innovateuk.ifs.competition.resource.CompetitionCompletionStage;
import org.junit.Test;

import java.time.LocalDate;

import static junit.framework.TestCase.assertTrue;
import static org.innovateuk.ifs.application.resource.ApplicationState.APPROVED;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class PreviousDashboardRowViewModelTest {

    private static final LocalDate YESTERDAY = LocalDate.now().minusDays(1);

    @Test
    public void testConstruct() {
        PreviousDashboardRowViewModel viewModel = new PreviousDashboardRowViewModel(null,
                                                                                    1L,
                                                                                    1L,
                                                                                    "Competition",
                                                                                    APPROVED,
                                                                                    null,
                                                                                    YESTERDAY,
                                                                        false,
                                                                false,
                CompetitionCompletionStage.PROJECT_SETUP);
        assertEquals("/application/1/summary", viewModel.getLinkUrl());
        assertEquals("Untitled application", viewModel.getTitle());
        assertTrue(viewModel.isApproved());
        assertFalse(viewModel.isCreatedOrOpen());
        assertFalse(viewModel.isInformedIneligible());
        assertFalse(viewModel.isRejected());
    }
}
