package org.innovateuk.ifs.eugrant.overview.populator;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.eugrant.EuGrantResource;
import org.innovateuk.ifs.eugrant.contact.populator.EuContactFormPopulator;
import org.innovateuk.ifs.eugrant.overview.service.EuGrantCookieService;
import org.innovateuk.ifs.eugrant.overview.viewmodel.EuGrantOverviewViewModel;
import org.junit.Test;
import org.mockito.Mock;

import static org.innovateuk.ifs.eugrant.builder.EuGrantResourceBuilder.newEuGrantResource;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

public class EuGrantOverviewViewModelPopulatorTest extends BaseServiceUnitTest<EuGrantOverviewViewModelPopulator> {

    @Mock
    private EuGrantCookieService euGrantCookieService;

    @Override
    protected EuGrantOverviewViewModelPopulator supplyServiceUnderTest() {
        return new EuGrantOverviewViewModelPopulator();
    }

    @Test
    public void populate() throws Exception {

        EuGrantResource euGrantResource = newEuGrantResource().build();

        euGrantResource.setContactComplete(true);
        euGrantResource.setOrganisationComplete(true);
        euGrantResource.setFundingComplete(true);

        when(euGrantCookieService.get()).thenReturn(euGrantResource);

        EuGrantOverviewViewModel viewModel = service.populate();

        assertEquals(euGrantResource.isContactComplete(), viewModel.isContactComplete());
        assertEquals(euGrantResource.isFundingComplete(), viewModel.isFundingComplete());
        assertEquals(euGrantResource.isOrganisationComplete(), viewModel.isOrganisationComplete());
    }
}
