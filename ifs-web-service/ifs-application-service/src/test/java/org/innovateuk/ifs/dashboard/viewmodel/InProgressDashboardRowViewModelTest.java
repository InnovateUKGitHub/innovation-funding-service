package org.innovateuk.ifs.dashboard.viewmodel;

import org.innovateuk.ifs.util.TimeZoneUtil;
import org.junit.Test;

import java.time.LocalDate;
import java.time.ZonedDateTime;

import static org.innovateuk.ifs.application.resource.ApplicationState.OPENED;
import static org.innovateuk.ifs.application.resource.ApplicationState.SUBMITTED;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class InProgressDashboardRowViewModelTest {

    private static final LocalDate YESTERDAY = LocalDate.now().minusDays(1);

    @Test
    public void constructOpen() {
        ZonedDateTime end = ZonedDateTime.now().plusHours(2).minusMinutes(1);
        InProgressDashboardRowViewModel viewModel = new InProgressDashboardRowViewModel("Application", 1L,
                "Competition", true, OPENED, true,
                end, 0, 50 , false, YESTERDAY, true, true);

        assertEquals("/application/1", viewModel.getLinkUrl());
        assertEquals("Application", viewModel.getTitle());
        assertEquals(1L, viewModel.getHoursLeftBeforeSubmit());
        if (TimeZoneUtil.toUkTimeZone(end).getDayOfMonth() == TimeZoneUtil.toUkTimeZone(ZonedDateTime.now()).getDayOfMonth()) {
            assertTrue(viewModel.isClosingToday());
        } else {
            assertFalse(viewModel.isClosingToday());
        }
        assertTrue(viewModel.isWithin24Hours());
        assertFalse(viewModel.isApplicationComplete());
        assertEquals("50% complete", viewModel.getProgressMessage());
    }

    @Test
    public void constructSubmitted() {
        InProgressDashboardRowViewModel viewModel = new InProgressDashboardRowViewModel(null, 1L,
                "Competition", true, SUBMITTED, true,
                ZonedDateTime.now().plusDays(12), 12, 100 , false, YESTERDAY, true, true);

        assertEquals("/application/1/track", viewModel.getLinkUrl());
        assertEquals("Untitled application", viewModel.getTitle());
        assertTrue(viewModel.isApplicationComplete());
        assertEquals("Ready to review and submit", viewModel.getProgressMessage());
    }

    @Test
    public void constructInterview() {
        InProgressDashboardRowViewModel viewModel = new InProgressDashboardRowViewModel(null, 1L,
                "Competition", true, SUBMITTED, true,
                ZonedDateTime.now().plusDays(12), 12, 100 , true, YESTERDAY, true, true);

        assertEquals("/application/1/summary", viewModel.getLinkUrl());
        assertTrue(viewModel.isApplicationComplete());
        assertEquals("Ready to review and submit", viewModel.getProgressMessage());
    }
}