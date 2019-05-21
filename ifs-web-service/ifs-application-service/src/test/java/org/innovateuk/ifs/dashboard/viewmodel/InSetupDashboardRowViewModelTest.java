package org.innovateuk.ifs.dashboard.viewmodel;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

@RunWith(MockitoJUnitRunner.Silent.class)
public class InSetupDashboardRowViewModelTest {

    @Test
    public void testConstruct() {
        InSetupDashboardRowViewModel viewModel = new InSetupDashboardRowViewModel("Application", 1L,
                "Competition", 2L, "Project");

        assertThat(viewModel.getLinkUrl(), equalTo("/project-setup/project/2"));
        assertThat(viewModel.getTitle(), equalTo("Project"));
    }

    @Test
    public void testNullTitle() {
        InSetupDashboardRowViewModel viewModel = new InSetupDashboardRowViewModel("Application", 1L,
                "Competition", 2L, "");

        assertThat(viewModel.getTitle(), equalTo("Competition"));
    }
}
