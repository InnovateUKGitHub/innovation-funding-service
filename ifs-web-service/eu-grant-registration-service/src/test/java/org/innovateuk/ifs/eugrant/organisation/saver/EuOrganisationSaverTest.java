package org.innovateuk.ifs.eugrant.organisation.saver;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.eugrant.EuGrantResource;
import org.innovateuk.ifs.eugrant.EuOrganisationType;
import org.innovateuk.ifs.eugrant.organisation.form.EuOrganisationForm;
import org.innovateuk.ifs.eugrant.organisation.service.EuOrganisationCookieService;
import org.innovateuk.ifs.eugrant.overview.service.EuGrantCookieService;
import org.innovateuk.ifs.organisation.resource.OrganisationSearchResult;
import org.innovateuk.ifs.user.service.OrganisationSearchRestService;
import org.junit.Test;
import org.mockito.Mock;

import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.eugrant.builder.EuGrantResourceBuilder.newEuGrantResource;
import static org.innovateuk.ifs.eugrant.builder.EuOrganisationResourceBuilder.newEuOrganisationResource;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class EuOrganisationSaverTest extends BaseServiceUnitTest<EuOrganisationSaver> {

    @Mock
    private EuGrantCookieService euGrantCookieService;

    @Mock
    private EuOrganisationCookieService organisationCookieService;

    @Mock
    private OrganisationSearchRestService organisationSearchRestService;

    @Override
    protected EuOrganisationSaver supplyServiceUnderTest() {
        return new EuOrganisationSaver();
    }

    @Test
    public void testSave() {
        EuGrantResource euGrantResource = newEuGrantResource().build();
        when(euGrantCookieService.get()).thenReturn(euGrantResource);

        EuOrganisationForm form = new EuOrganisationForm();
        form.setSelectedOrganisationId("SomeId");
        OrganisationSearchResult searchResult = new OrganisationSearchResult("SomeId", "Company1");
        when(organisationSearchRestService.getOrganisation(EuOrganisationType.BUSINESS, "SomeId")).thenReturn(restSuccess(searchResult));
        when(euGrantCookieService.save(euGrantResource)).thenReturn(serviceSuccess(euGrantResource));

        ServiceResult<Void> result = service.save(form, EuOrganisationType.BUSINESS);

        assertTrue(result.isSuccess());

        verify(euGrantCookieService).save(newEuGrantResource()
                .withOrganisation(newEuOrganisationResource()
                        .withCompaniesHouseNumber("SomeId")
                        .withName("Company1")
                        .withOrganisationType(EuOrganisationType.BUSINESS)
                        .build())
                .build());
        verify(organisationCookieService).clear();
    }

    @Test
    public void testSave_manualEntry() {
        EuGrantResource euGrantResource = newEuGrantResource().build();
        when(euGrantCookieService.get()).thenReturn(euGrantResource);

        EuOrganisationForm form = new EuOrganisationForm();
        form.setManualEntry(true);
        form.setOrganisationName("SomeName");
        when(euGrantCookieService.save(euGrantResource)).thenReturn(serviceSuccess(euGrantResource));

        ServiceResult<Void> result = service.save(form, EuOrganisationType.BUSINESS);

        assertTrue(result.isSuccess());

        verify(euGrantCookieService).save(newEuGrantResource()
                .withOrganisation(newEuOrganisationResource()
                        .withName("SomeName")
                        .withOrganisationType(EuOrganisationType.BUSINESS)
                        .build())
                .build());
        verify(organisationCookieService).clear();
    }
}
