package org.innovateuk.ifs.dashboard.viewmodel;

import org.innovateuk.ifs.application.resource.ApplicationState;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class PreviousDashboardRowViewModelTest {

    @Test
    public void testConstruct() {
        PreviousDashboardRowViewModel viewModel = new PreviousDashboardRowViewModel(null, 1L,
                "Competition", ApplicationState.APPROVED, false);

        assertThat(viewModel.getLinkUrl(), equalTo("/application/1/summary"));
        assertThat(viewModel.getTitle(), equalTo("Untitled application"));
        assertThat(viewModel.isApproved(), equalTo(true));
        assertThat(viewModel.isCreatedOrOpen(), equalTo(false));
        assertThat(viewModel.isInformedIneligible(), equalTo(false));
        assertThat(viewModel.isRejected(), equalTo(false));
        assertThat(viewModel.isWithdrawn(), equalTo(false));
    }
}
