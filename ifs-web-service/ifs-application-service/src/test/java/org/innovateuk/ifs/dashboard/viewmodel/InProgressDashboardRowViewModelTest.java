package org.innovateuk.ifs.dashboard.viewmodel;

import org.innovateuk.ifs.application.resource.ApplicationState;
import org.innovateuk.ifs.util.TimeZoneUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.ZonedDateTime;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.Silent.class)
public class InProgressDashboardRowViewModelTest {

    @Test
    public void constructOpen() {
        ZonedDateTime end = ZonedDateTime.now().plusHours(2).minusMinutes(1);
        InProgressDashboardRowViewModel viewModel = new InProgressDashboardRowViewModel("Application", 1L,
                "Competition", true, ApplicationState.OPEN, true,
                end, 0, 50 , false);

        assertEquals(viewModel.getLinkUrl(), "/application/1");
        assertEquals(viewModel.getTitle(), "Application");
        assertEquals(viewModel.getHoursLeftBeforeSubmit(), 1L);
        if (TimeZoneUtil.toUkTimeZone(end).getDayOfMonth() == TimeZoneUtil.toUkTimeZone(ZonedDateTime.now()).getDayOfMonth()) {
            assertTrue(viewModel.isClosingToday());
        } else {
            assertFalse(viewModel.isClosingToday());
        }
        assertTrue(viewModel.isWithin24Hours());
        assertFalse(viewModel.isApplicationComplete());
        assertEquals(viewModel.getProgressMessage(), "50% complete");
    }

    @Test
    public void constructSubmitted() {
        InProgressDashboardRowViewModel viewModel = new InProgressDashboardRowViewModel(null, 1L,
                "Competition", true, ApplicationState.SUBMITTED, true,
                ZonedDateTime.now().plusDays(12), 12, 100 , false);

        assertEquals(viewModel.getLinkUrl(), "/application/1/track");
        assertEquals(viewModel.getTitle(),  "Untitled application");
        assertTrue(viewModel.isApplicationComplete());
        assertEquals(viewModel.getProgressMessage(), "Ready to review and submit");
    }

    @Test
    public void constructInterview() {
        InProgressDashboardRowViewModel viewModel = new InProgressDashboardRowViewModel(null, 1L,
                "Competition", true, ApplicationState.SUBMITTED, true,
                ZonedDateTime.now().plusDays(12), 12, 100 , true);

        assertEquals(viewModel.getLinkUrl(), "/application/1/summary");
        assertTrue(viewModel.isApplicationComplete());
        assertEquals(viewModel.getProgressMessage(), "Ready to review and submit");
    }
}