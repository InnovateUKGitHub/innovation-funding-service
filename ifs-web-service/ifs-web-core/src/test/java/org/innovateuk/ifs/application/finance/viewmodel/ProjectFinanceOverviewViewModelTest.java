package org.innovateuk.ifs.application.finance.viewmodel;

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
