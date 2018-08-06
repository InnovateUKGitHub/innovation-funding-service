package org.innovateuk.ifs.application.finance.viewmodel;

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
