package org.innovateuk.ifs.application.populator.viewmodel;

import org.innovateuk.ifs.application.populator.finance.viewmodel.ProjectFinanceViewModel;
import org.junit.Before;
import org.mockito.InjectMocks;

//@RunWith(MockitoJUnitRunner.class)
public class ProjectFinanceViewModelTest {

    @InjectMocks
    private ProjectFinanceViewModel viewModel;

    @Before
    public void setup() {
        viewModel = new ProjectFinanceViewModel();
    }

}
