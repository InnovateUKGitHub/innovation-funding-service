package org.innovateuk.ifs.application.populator.viewmodel;

import org.innovateuk.ifs.application.populator.finance.viewmodel.ProjectFinanceOverviewViewModel;
import org.junit.Before;
import org.mockito.InjectMocks;

//@RunWith(MockitoJUnitRunner.class)
public class ProjectFinanceOverviewViewModelTest {

    @InjectMocks
    private ProjectFinanceOverviewViewModel viewModel;

    @Before
    public void setup() {
        viewModel = new ProjectFinanceOverviewViewModel();
    }
}
