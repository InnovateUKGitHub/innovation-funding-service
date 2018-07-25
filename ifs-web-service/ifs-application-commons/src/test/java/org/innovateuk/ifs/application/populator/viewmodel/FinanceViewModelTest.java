package org.innovateuk.ifs.application.populator.viewmodel;

import org.innovateuk.ifs.application.finance.viewmodel.FinanceViewModel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import static org.innovateuk.ifs.util.MapFunctions.asMap;
import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class FinanceViewModelTest {

    @InjectMocks
    private FinanceViewModel viewModel;

    @Before
    public void setup() {
        viewModel = new FinanceViewModel();
    }

    @Test
    public void getHasOrganisationFinanceTest(){
        assertEquals(Boolean.FALSE, viewModel.getHasOrganisationFinance());

        viewModel.setOrganisationFinance(asMap());

        assertEquals(Boolean.TRUE, viewModel.getHasOrganisationFinance());
    }
}
