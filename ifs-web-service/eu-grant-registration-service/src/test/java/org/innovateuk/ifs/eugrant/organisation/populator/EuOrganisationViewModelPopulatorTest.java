package org.innovateuk.ifs.eugrant.organisation.populator;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.eugrant.EuOrganisationResource;
import org.innovateuk.ifs.eugrant.EuOrganisationType;
import org.innovateuk.ifs.eugrant.organisation.viewmodel.EuOrganisationViewModel;
import org.innovateuk.ifs.organisation.resource.OrganisationSearchResult;
import org.innovateuk.ifs.user.service.OrganisationSearchRestService;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import static org.innovateuk.ifs.address.builder.AddressResourceBuilder.newAddressResource;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.eugrant.builder.EuOrganisationResourceBuilder.newEuOrganisationResource;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verifyZeroInteractions;

public class EuOrganisationViewModelPopulatorTest extends BaseServiceUnitTest<EuOrganisationViewModelPopulator> {

    @Mock
    private OrganisationSearchRestService searchRestService;

    @Override
    protected EuOrganisationViewModelPopulator supplyServiceUnderTest() {
        return new EuOrganisationViewModelPopulator();
    }

    @Test
    public void populate() {
        EuOrganisationResource organisation = newEuOrganisationResource()
                .withCompaniesHouseNumber("1234")
                .withOrganisationType(EuOrganisationType.BUSINESS)
                .withName("Company1")
                .build();

        OrganisationSearchResult result = new OrganisationSearchResult("1234", "Company1");
        result.setOrganisationAddress(newAddressResource().build());
        Mockito.when(searchRestService.getOrganisation(EuOrganisationType.BUSINESS, "1234")).thenReturn(restSuccess(result));

        EuOrganisationViewModel viewModel = service.populate(organisation);

        assertEquals(viewModel.getType(), EuOrganisationType.BUSINESS);
        assertEquals(viewModel.getName(), "Company1");
        assertEquals(viewModel.getRegistrationNumber(), "1234");
        assertEquals(viewModel.getAddressLine1(), result.getOrganisationAddress().getAddressLine1());
        assertEquals(viewModel.getAddressLine2(), result.getOrganisationAddress().getAddressLine2());
        assertEquals(viewModel.getAddressLine3(), result.getOrganisationAddress().getAddressLine3());
        assertEquals(viewModel.getTown(), result.getOrganisationAddress().getTown());
        assertEquals(viewModel.getCounty(), result.getOrganisationAddress().getCounty());
        assertEquals(viewModel.getPostcode(), result.getOrganisationAddress().getPostcode());
        assertTrue(viewModel.hasCompaniesHouseFields());
    }

    @Test
    public void populate_research() {
        EuOrganisationResource organisation = newEuOrganisationResource()
                .withOrganisationType(EuOrganisationType.RESEARCH)
                .withName("Research1")
                .build();

        EuOrganisationViewModel viewModel = service.populate(organisation);

        assertEquals(viewModel.getType(), EuOrganisationType.RESEARCH);
        assertEquals(viewModel.getName(), "Research1");
        assertFalse(viewModel.hasCompaniesHouseFields());
        verifyZeroInteractions(searchRestService);
    }
}
