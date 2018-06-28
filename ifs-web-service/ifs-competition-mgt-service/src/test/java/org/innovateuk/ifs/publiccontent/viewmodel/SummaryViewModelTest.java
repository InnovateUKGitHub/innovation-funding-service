package org.innovateuk.ifs.publiccontent.viewmodel;

import org.innovateuk.ifs.competition.publiccontent.resource.FundingType;
import org.innovateuk.ifs.publiccontent.viewmodel.section.SummaryViewModel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class SummaryViewModelTest {

    @InjectMocks
    private SummaryViewModel viewModel;

    @Test
    public void testGetFundingTypes() {

        assertThat(viewModel.getFundingTypes(), equalTo(FundingType.values()));
    }
}
