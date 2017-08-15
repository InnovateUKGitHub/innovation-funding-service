package org.innovateuk.ifs.registration;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.registration.form.OrganisationCreationForm;
import org.innovateuk.ifs.registration.form.OrganisationTypeForm;
import org.innovateuk.ifs.registration.service.RegistrationCookieService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Optional;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasProperty;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

//TODO: Fix tests
public class OrganisationCreationSaveControllerTest extends BaseControllerMockMVCTest<OrganisationCreationSaveController> {

    private static String COMPANY_NAME = "organisation name";
    private static String COMPANY_ID = "1";
    private static String INVITE_HASH = "123abc";

    protected OrganisationCreationSaveController supplyControllerUnderTest() {
        return new OrganisationCreationSaveController();
    }

    @Mock
    private RegistrationCookieService registrationCookieService;

    private OrganisationTypeForm organisationTypeForm;
    private OrganisationCreationForm organisationForm;

    @Before
    public void setup(){
        super.setup();
    }
    @Test
    public void testSaveOrganisation() throws Exception {
        when(registrationCookieService.getOrganisationTypeCookieValue(any())).thenReturn(Optional.of(organisationTypeForm));
        when(registrationCookieService.getOrganisationCreationCookieValue(any())).thenReturn(Optional.of(organisationForm));

        MvcResult result = mockMvc.perform(get("/organisation/create/save-organisation")
                .param("searchOrganisationId", "123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/registration/register"))
                .andExpect(cookie().exists("organisationId"))
                .andReturn();

        assertEquals("5", getDecryptedCookieValue(result.getResponse().getCookies(), "organisationId"));
    }

    @Test
    public void testConfirmBusiness() throws Exception {
        when(registrationCookieService.getOrganisationTypeCookieValue(any())).thenReturn(Optional.of(organisationTypeForm));
        when(registrationCookieService.getOrganisationCreationCookieValue(any())).thenReturn(Optional.of(organisationForm));

        mockMvc.perform(get("/organisation/create/confirm-organisation"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("registration/organisation/confirm-organisation"))
                .andExpect(model().attributeExists("organisationFormCookie"));
    }

    @Test
    public void testConfirmCompany() throws Exception {
        mockMvc.perform(get("/organisation/create/confirm-organisation"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(model().attributeExists("selectedOrganisation"))
                .andExpect(model().attribute("selectedOrganisation", hasProperty("name", equalTo(COMPANY_NAME))))
                .andExpect(view().name("registration/organisation/confirm-organisation"));
    }

    @Test
    public void testSaveOrganisationWithInvite() throws Exception {
        when(registrationCookieService.getOrganisationTypeCookieValue(any())).thenReturn(Optional.of(organisationTypeForm));
        when(registrationCookieService.getOrganisationCreationCookieValue(any())).thenReturn(Optional.of(organisationForm));

        MvcResult result = mockMvc.perform(get("/organisation/create/save-organisation")
                .param("searchOrganisationId", COMPANY_ID))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/registration/register"))
                .andExpect(cookie().exists("organisationId"))
                .andReturn();

        assertEquals("5", getDecryptedCookieValue(result.getResponse().getCookies(), "organisationId"));
    }
}