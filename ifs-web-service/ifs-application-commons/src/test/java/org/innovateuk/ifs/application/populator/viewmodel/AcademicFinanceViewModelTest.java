package org.innovateuk.ifs.application.populator.viewmodel;

import org.innovateuk.ifs.application.populator.finance.viewmodel.AcademicFinanceViewModel;
import org.junit.Before;
import org.mockito.InjectMocks;

//@RunWith(MockitoJUnitRunner.class)
public class AcademicFinanceViewModelTest {
    @InjectMocks
    private AcademicFinanceViewModel viewModel;

    @Before
    public void setup() {
        viewModel = new AcademicFinanceViewModel();
    }
}
