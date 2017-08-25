package org.innovateuk.ifs.registration;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.form.AddressForm;
import org.innovateuk.ifs.organisation.resource.OrganisationSearchResult;
import org.innovateuk.ifs.registration.controller.OrganisationCreationSaveController;
import org.innovateuk.ifs.registration.form.OrganisationCreationForm;
import org.innovateuk.ifs.registration.form.OrganisationTypeForm;
import org.innovateuk.ifs.registration.service.RegistrationCookieService;
import org.innovateuk.ifs.user.service.OrganisationSearchRestService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.validation.Validator;

import java.util.Collections;
import java.util.Optional;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasProperty;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.invite.builder.ApplicationInviteResourceBuilder.newApplicationInviteResource;
import static org.innovateuk.ifs.invite.builder.InviteOrganisationResourceBuilder.newInviteOrganisationResource;
import static org.innovateuk.ifs.user.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class OrganisationCreationSaveControllerTest extends BaseControllerMockMVCTest<OrganisationCreationSaveController> {

    private static String COMPANY_NAME = "organisation name";
    private static String COMPANY_ID = "1";
    private static String INVITE_HASH = "123abc";

    protected OrganisationCreationSaveController supplyControllerUnderTest() {
        return new OrganisationCreationSaveController();
    }

    @Mock
    private RegistrationCookieService registrationCookieService;

    @Mock
    private OrganisationSearchRestService organisationSearchRestService;

    @Mock
    private Validator validator;

    private OrganisationTypeForm organisationTypeForm;
    private OrganisationCreationForm organisationForm;

    @Before
    public void setup(){
        super.setup();

        OrganisationSearchResult organisationSearchResult = new OrganisationSearchResult(COMPANY_ID, COMPANY_NAME);

        when(organisationSearchRestService.getOrganisation(anyLong(), anyString())).thenReturn(restSuccess(organisationSearchResult));

        AddressForm addressForm = new AddressForm();
        addressForm.setPostcodeInput("ABC 12345");
        addressForm.setSelectedPostcodeIndex(null);
        addressForm.setPostcodeOptions(Collections.emptyList());

        organisationTypeForm = new OrganisationTypeForm();
        organisationTypeForm.setOrganisationType(1L);

        organisationForm = new OrganisationCreationForm();
        organisationForm.setAddressForm(addressForm);
        organisationForm.setTriedToSave(true);
        organisationForm.setOrganisationSearchName(null);
        organisationForm.setSearchOrganisationId(COMPANY_ID);
        organisationForm.setOrganisationSearching(false);
        organisationForm.setManualEntry(false);
        organisationForm.setUseSearchResultAddress(false);
        organisationForm.setOrganisationSearchResults(Collections.emptyList());
        organisationForm.setOrganisationName("NOMENSA LTD");
    }

    @Test
    public void testSaveOrganisation() throws Exception {
        when(registrationCookieService.getOrganisationTypeCookieValue(any())).thenReturn(Optional.of(organisationTypeForm));
        when(registrationCookieService.getOrganisationCreationCookieValue(any())).thenReturn(Optional.of(organisationForm));
        when(registrationCookieService.getInviteHashCookieValue(any())).thenReturn(Optional.of(INVITE_HASH));
        when(inviteRestService.getInviteByHash(any())).thenReturn(restSuccess(newApplicationInviteResource().withInviteOrganisation(1L).build()));
        when(inviteOrganisationRestService.getByIdForAnonymousUserFlow(anyLong())).thenReturn(restSuccess(newInviteOrganisationResource().build()));
        when(organisationService.saveForAnonymousUserFlow(any())).thenReturn(newOrganisationResource().withId(2L).build());
        when(inviteOrganisationRestService.put(any())).thenReturn(restSuccess());

        mockMvc.perform(post("/organisation/create/save-organisation")
                .param("searchOrganisationId", "123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/registration/register"));

        verify(registrationCookieService, times(1)).saveToOrganisationIdCookie(eq(2L), any());
    }

    @Test
    public void testConfirmBusiness() throws Exception {
        when(registrationCookieService.getOrganisationTypeCookieValue(any())).thenReturn(Optional.of(organisationTypeForm));
        when(registrationCookieService.getOrganisationCreationCookieValue(any())).thenReturn(Optional.of(organisationForm));

        mockMvc.perform(get("/organisation/create/confirm-organisation"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("registration/organisation/confirm-organisation"))
                .andExpect(model().attributeExists("organisationForm"));
    }

    @Test
    public void testConfirmCompany() throws Exception {
        when(registrationCookieService.getOrganisationTypeCookieValue(any())).thenReturn(Optional.of(organisationTypeForm));
        when(registrationCookieService.getOrganisationCreationCookieValue(any())).thenReturn(Optional.of(organisationForm));

        mockMvc.perform(get("/organisation/create/confirm-organisation"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(model().attributeExists("selectedOrganisation"))
                .andExpect(model().attribute("selectedOrganisation", hasProperty("name", equalTo(COMPANY_NAME))))
                .andExpect(view().name("registration/organisation/confirm-organisation"));
    }
}