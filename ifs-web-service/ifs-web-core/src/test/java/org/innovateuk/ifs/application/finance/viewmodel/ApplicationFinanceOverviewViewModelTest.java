package org.innovateuk.ifs.application.finance.viewmodel;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import static org.innovateuk.ifs.util.MapFunctions.asMap;
import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class ApplicationFinanceOverviewViewModelTest {

    @InjectMocks
    private ApplicationFinanceOverviewViewModel viewModel;

    @Before
    public void setup() {
        viewModel = new ApplicationFinanceOverviewViewModel();
    }

    @Test
    public void getHasAcademicFileEntriesTest() {
        assertEquals(Boolean.FALSE, viewModel.getHasAcademicFileEntries());

        viewModel.setAcademicFileEntries(asMap());

        assertEquals(Boolean.TRUE, viewModel.getHasAcademicFileEntries());
    }

    @Test
    public void hasTooHighResearchRatioTest() {
        assertEquals(Boolean.FALSE, viewModel.hasTooHighResearchRatio(23.6));

        viewModel.setResearchParticipationPercentage(20.0);

        assertEquals(Boolean.FALSE, viewModel.hasTooHighResearchRatio(23.6));

        viewModel.setResearchParticipationPercentage(23.7);

        assertEquals(Boolean.TRUE, viewModel.hasTooHighResearchRatio(23.6));

        viewModel.setResearchParticipationPercentage(50.0);

        assertEquals(Boolean.TRUE, viewModel.hasTooHighResearchRatio(23.6));
    }
}
