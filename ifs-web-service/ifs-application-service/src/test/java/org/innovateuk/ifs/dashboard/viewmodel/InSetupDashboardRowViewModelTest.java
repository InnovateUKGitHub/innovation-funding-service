package org.innovateuk.ifs.dashboard.viewmodel;

import org.junit.Test;

import java.time.LocalDate;

import static org.junit.Assert.assertEquals;

public class InSetupDashboardRowViewModelTest {

    private static final LocalDate YESTERDAY = LocalDate.now().minusDays(1);

    @Test
    public void testConstruct() {
        InSetupDashboardRowViewModel viewModel = new InSetupDashboardRowViewModel("Application", 1L,
                "Competition", 2L, "Project", YESTERDAY);

        assertEquals("/project-setup/project/2", viewModel.getLinkUrl());
        assertEquals("Project", viewModel.getTitle());
    }

    @Test
    public void testNullTitle() {
        InSetupDashboardRowViewModel viewModel = new InSetupDashboardRowViewModel("Application", 1L,
                "Competition", 2L, "", YESTERDAY);

        assertEquals("Competition", viewModel.getTitle());
    }
}
