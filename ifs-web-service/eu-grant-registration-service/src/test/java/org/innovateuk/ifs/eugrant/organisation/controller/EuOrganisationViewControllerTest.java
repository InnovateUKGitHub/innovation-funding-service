package org.innovateuk.ifs.eugrant.organisation.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.eugrant.EuGrantResource;
import org.innovateuk.ifs.eugrant.organisation.populator.EuOrganisationViewModelPopulator;
import org.innovateuk.ifs.eugrant.organisation.viewmodel.EuOrganisationViewModel;
import org.innovateuk.ifs.eugrant.overview.service.EuGrantCookieService;
import org.junit.Test;
import org.mockito.Mock;

import static org.innovateuk.ifs.eugrant.builder.EuGrantResourceBuilder.newEuGrantResource;
import static org.innovateuk.ifs.eugrant.builder.EuOrganisationResourceBuilder.newEuOrganisationResource;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class EuOrganisationViewControllerTest extends BaseControllerMockMVCTest<EuOrganisationViewController> {

    @Mock
    private EuOrganisationViewModelPopulator organisationViewModelPopulator;

    @Mock
    private EuGrantCookieService euGrantCookieService;

    @Override
    protected EuOrganisationViewController supplyControllerUnderTest() {
        return new EuOrganisationViewController();
    }

    @Test
    public void viewOrganisation() throws Exception {
        EuOrganisationViewModel viewModel = mock(EuOrganisationViewModel.class);
        EuGrantResource euGrant = newEuGrantResource()
                .withOrganisation(newEuOrganisationResource()
                        .build())
                .build();
        when(euGrantCookieService.get()).thenReturn(euGrant);
        when(organisationViewModelPopulator.populate(euGrant.getOrganisation())).thenReturn(viewModel);
        mockMvc.perform(get("/organisation/view"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("organisation/view"))
                .andExpect(model().attribute("model", viewModel));
    }

    @Test
    public void viewOrganisation_notComplete() throws Exception {
        EuGrantResource euGrant = newEuGrantResource()
                .build();
        when(euGrantCookieService.get()).thenReturn(euGrant);
        mockMvc.perform(get("/organisation/view"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/organisation/type"));
        verifyZeroInteractions(organisationViewModelPopulator);
    }

}
