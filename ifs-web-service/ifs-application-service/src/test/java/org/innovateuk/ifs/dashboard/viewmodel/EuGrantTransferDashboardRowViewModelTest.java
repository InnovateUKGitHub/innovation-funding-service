package org.innovateuk.ifs.dashboard.viewmodel;

import org.junit.Test;

import java.time.LocalDate;

import static org.innovateuk.ifs.application.resource.ApplicationState.APPROVED;
import static org.junit.Assert.assertEquals;

public class EuGrantTransferDashboardRowViewModelTest {

    private static final LocalDate YESTERDAY = LocalDate.now().minusDays(1);

    @Test
    public void construct() {
        EuGrantTransferDashboardRowViewModel viewModel = new EuGrantTransferDashboardRowViewModel("title",
                1L,
                "Competition",
                APPROVED,
                50,
                2L,
                YESTERDAY);

        assertEquals("/project-setup/project/2", viewModel.getLinkUrl());
        assertEquals("title", viewModel.getTitle());
        assertEquals(50, viewModel.getApplicationProgress());
        assertEquals(YESTERDAY, viewModel.getStartDate());
    }
}
