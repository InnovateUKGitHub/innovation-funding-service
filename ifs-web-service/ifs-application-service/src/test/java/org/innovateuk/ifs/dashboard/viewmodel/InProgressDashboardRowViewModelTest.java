package org.innovateuk.ifs.dashboard.viewmodel;

import org.innovateuk.ifs.application.resource.ApplicationState;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.time.ZonedDateTime;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class InProgressDashboardRowViewModelTest {

    @Test
    public void testConstructOpen() {
        ZonedDateTime end = ZonedDateTime.now().plusHours(2).minusMinutes(1);
        InProgressDashboardRowViewModel viewModel = new InProgressDashboardRowViewModel("Application", 1L,
                "Competition", true, ApplicationState.OPEN, true,
                end, 0, 50 , false);

        assertThat(viewModel.getLinkUrl(), equalTo("/application/1"));
        assertThat(viewModel.getTitle(), equalTo("Application"));
        assertThat(viewModel.getHoursLeftBeforeSubmit(), equalTo(1L));
        if (end.getDayOfMonth() == ZonedDateTime.now().getDayOfMonth()) {
            assertThat(viewModel.isClosingToday(), equalTo(true));
        } else {
            assertThat(viewModel.isClosingToday(), equalTo(false));
        }
        assertThat(viewModel.isWithin24Hours(), equalTo(true));
    }

    @Test
    public void testConstructCreated() {
        InProgressDashboardRowViewModel viewModel = new InProgressDashboardRowViewModel(null, 1L,
                "Competition", true, ApplicationState.CREATED, true,
                ZonedDateTime.now().plusDays(12), 12, 0 , false);

        assertThat(viewModel.getLinkUrl(), equalTo("/application/1/team"));
        assertThat(viewModel.getTitle(), equalTo( "Untitled application (start here)"));
    }

    @Test
    public void testConstructSubmitted() {
        InProgressDashboardRowViewModel viewModel = new InProgressDashboardRowViewModel(null, 1L,
                "Competition", true, ApplicationState.SUBMITTED, true,
                ZonedDateTime.now().plusDays(12), 12, 100 , false);

        assertThat(viewModel.getLinkUrl(), equalTo("/application/1/track"));
        assertThat(viewModel.getTitle(), equalTo( "Untitled application"));
    }

    @Test
    public void testConstructInterview() {
        InProgressDashboardRowViewModel viewModel = new InProgressDashboardRowViewModel(null, 1L,
                "Competition", true, ApplicationState.SUBMITTED, true,
                ZonedDateTime.now().plusDays(12), 12, 100 , true);

        assertThat(viewModel.getLinkUrl(), is(nullValue()));
    }
}
