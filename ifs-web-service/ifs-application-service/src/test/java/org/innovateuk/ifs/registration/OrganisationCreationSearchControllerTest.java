package org.innovateuk.ifs.registration;

import org.innovateuk.ifs.BaseUnitTest;
import org.innovateuk.ifs.address.resource.AddressResource;
import org.innovateuk.ifs.address.service.AddressRestService;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.form.AddressForm;
import org.innovateuk.ifs.organisation.resource.OrganisationSearchResult;
import org.innovateuk.ifs.registration.controller.OrganisationCreationSearchController;
import org.innovateuk.ifs.registration.form.OrganisationCreationForm;
import org.innovateuk.ifs.registration.form.OrganisationTypeForm;
import org.innovateuk.ifs.registration.populator.OrganisationCreationSelectTypePopulator;
import org.innovateuk.ifs.registration.service.RegistrationCookieService;
import org.innovateuk.ifs.user.resource.OrganisationResource;
import org.innovateuk.ifs.user.service.OrganisationSearchRestService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import javax.servlet.http.Cookie;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;

import static java.lang.String.format;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasProperty;
import static org.innovateuk.ifs.BaseControllerMockMVCTest.setupMockMvc;
import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.user.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(MockitoJUnitRunner.class)
@TestPropertySource(locations = "classpath:application.properties")
public class OrganisationCreationSearchControllerTest extends BaseUnitTest {
    @InjectMocks
    private OrganisationCreationSearchController organisationCreationController;
    private ApplicationResource applicationResource;

    @Mock
    private OrganisationSearchRestService organisationSearchRestService;

    @Mock
    private AddressRestService addressRestService;

    @Mock
    private RegistrationCookieService registrationCookieService;

    @Spy
    @InjectMocks
    private OrganisationCreationSelectTypePopulator organisationCreationSelectTypePopulator;

    private String COMPANY_ID = "08241216";
    private String COMPANY_NAME = "NETWORTHNET LTD";
    private String POSTCODE_LOOKUP = "CH64 3RU";
    private String POSTCODE_LOOKUP_URL_ENCODED = "CH64%203RU";
    private OrganisationResource organisationResource;
    private Long competitionId;

    private OrganisationCreationForm organisationForm;
    private OrganisationTypeForm organisationTypeForm;
    private OrganisationCreationForm organisationFormUseSearchResult;

    @Before
    public void setUp() {

        // Process mock annotations
        initMocks(this);

        mockMvc = setupMockMvc(organisationCreationController, () -> loggedInUser, env, messageSource);

        super.setup();

        this.setupInvites();
        this.setupCookieUtil();

        applicationResource = newApplicationResource().withId(6L).withName("some application").build();
        organisationResource = newOrganisationResource().withId(5L).withName(COMPANY_NAME).build();
        OrganisationSearchResult organisationSearchResult = new OrganisationSearchResult(COMPANY_ID, COMPANY_NAME);
        when(organisationService.getCompanyHouseOrganisation(COMPANY_ID)).thenReturn(organisationSearchResult);
        when(organisationService.saveForAnonymousUserFlow(any(OrganisationResource.class))).thenReturn(organisationResource);
        when(applicationService.createApplication(anyLong(), anyLong(), anyString())).thenReturn(applicationResource);
        when(organisationSearchRestService.getOrganisation(businessOrganisationTypeResource.getId(), COMPANY_ID)).thenReturn(restSuccess(organisationSearchResult));
        when(organisationSearchRestService.searchOrganisation(anyLong(), anyString())).thenReturn(restSuccess(new ArrayList<>()));
        when(addressRestService.validatePostcode("CH64 3RU")).thenReturn(restSuccess(true));

        AddressForm addressForm = new AddressForm();
        addressForm.setPostcodeInput("");
        addressForm.setSelectedPostcodeIndex(null);
        addressForm.setPostcodeOptions(Collections.emptyList());

        organisationTypeForm = new OrganisationTypeForm();
        organisationTypeForm.setOrganisationType(1L);

        organisationForm = new OrganisationCreationForm();
        organisationForm.setAddressForm(addressForm);
        organisationForm.setTriedToSave(true);
        organisationForm.setOrganisationSearchName("company name");
        organisationForm.setOrganisationTypeId(1L);
        organisationForm.setSearchOrganisationId(COMPANY_ID);
        organisationForm.setOrganisationSearching(false);
        organisationForm.setManualEntry(false);
        organisationForm.setUseSearchResultAddress(false);
        organisationForm.setOrganisationSearchResults(Collections.emptyList());
        organisationForm.setOrganisationName("NOMENSA LTD");


        organisationFormUseSearchResult = new OrganisationCreationForm();
        organisationFormUseSearchResult.setOrganisationSearchName("searchname");
        organisationFormUseSearchResult.setOrganisationName("actualname");
        organisationFormUseSearchResult.setUseSearchResultAddress(true);

        competitionId = 2L;

        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();
        ReflectionTestUtils.setField(organisationCreationController, "validator", validator);
    }

    @Test
    public void testManualOrganisationEntry() throws Exception {
        OrganisationCreationForm organisationFormCookieValue = new OrganisationCreationForm();
        organisationFormCookieValue.setManualEntry(true);

        when(registrationCookieService.getOrganisationTypeCookieValue(any())).thenReturn(Optional.of(organisationTypeForm));
        when(registrationCookieService.getOrganisationCreationCookieValue(any())).thenReturn(Optional.of(organisationFormCookieValue));

        Cookie[] cookies = mockMvc.perform(post("/organisation/create/find-organisation")
                .param("organisationSearchName", "BusinessName")
                .param("not-in-company-house", ""))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/organisation/create/find-organisation"))
                .andReturn().getResponse().getCookies();

        mockMvc.perform(get("/organisation/create/find-organisation")
                .cookie(cookies))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("registration/organisation/find-organisation"))
                .andExpect(model().attribute("organisationForm", hasProperty("manualEntry", equalTo(true))));
    }

    @Test
    public void testFindBusinessManualAddress() throws Exception {
        when(registrationCookieService.getOrganisationTypeCookieValue(any())).thenReturn(Optional.of(organisationTypeForm));
        when(registrationCookieService.getOrganisationCreationCookieValue(any())).thenReturn(Optional.of(organisationForm));

        mockMvc.perform(post("/organisation/create/find-organisation")
                .param("organisationSearchName", "BusinessName")
                .param("manual-address", ""))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/organisation/create/find-organisation"));

        verify(registrationCookieService, times(1)).saveToOrganisationCreationCookie(any(), any());
    }

    @Test
    public void testFindBusinessSearchAddress() throws Exception {
        when(registrationCookieService.getOrganisationTypeCookieValue(any())).thenReturn(Optional.of(organisationTypeForm));
        when(registrationCookieService.getOrganisationCreationCookieValue(any())).thenReturn(Optional.of(organisationForm));

        mockMvc.perform(post("/organisation/create/find-organisation")
                .param("addressForm.postcodeInput", POSTCODE_LOOKUP)
                .param("search-address", "")
                .header("referer", "/organisation/create/find-organisation/"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name(format("redirect:/organisation/create/find-organisation?searchTerm=%s", POSTCODE_LOOKUP_URL_ENCODED)));

        mockMvc.perform(get(format("/organisation/create/find-organisation/%s", POSTCODE_LOOKUP)))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("registration/organisation/find-organisation"))
                .andExpect(model().attribute("organisationForm", hasProperty("manualEntry", equalTo(false))));
    }

    @Test
    public void testFindBusinessSearchAddressInvalid() throws Exception {
        when(registrationCookieService.getOrganisationTypeCookieValue(any())).thenReturn(Optional.of(organisationTypeForm));
        when(registrationCookieService.getOrganisationCreationCookieValue(any())).thenReturn(Optional.of(organisationForm));

        mockMvc.perform(post("/organisation/create/find-organisation")
                .param("postcodeInput", "")
                .param("search-address", "")
                .header("referer", "/organisation/create/find-organisation/"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/organisation/create/find-organisation"));

        mockMvc.perform(get("/organisation/create/find-organisation"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("registration/organisation/find-organisation"))
                .andExpect(model().attribute("organisationForm", hasProperty("manualEntry", equalTo(false))));
    }

    @Test
    public void testCreateOrganisation_addressSearchShowsEmptyPostCodeValidationError() throws Exception {
        OrganisationCreationForm organisationFormCookieValue = new OrganisationCreationForm();
        organisationFormCookieValue.setTriedToSave(true);
        organisationFormCookieValue.getAddressForm().setSelectedPostcode(new AddressResource());

        when(registrationCookieService.getOrganisationTypeCookieValue(any())).thenReturn(Optional.of(organisationTypeForm));
        when(registrationCookieService.getOrganisationCreationCookieValue(any())).thenReturn(Optional.of(organisationFormCookieValue));

        mockMvc.perform(get("/organisation/create/find-organisation"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("registration/organisation/find-organisation"))
                .andExpect(model().attribute("organisationForm", hasProperty("manualEntry", equalTo(false))))
                .andExpect(model().attributeHasFieldErrors("organisationForm", "addressForm.selectedPostcode.addressLine1"));
    }

    @Test
    public void testCreateOrganisation_noAddressAndNoUseSearchResultAddressShouldResultInError() throws Exception {
        OrganisationCreationForm organisationFormCookieValue = new OrganisationCreationForm();
        organisationFormCookieValue.setTriedToSave(true);

        when(registrationCookieService.getOrganisationTypeCookieValue(any())).thenReturn(Optional.of(organisationTypeForm));
        when(registrationCookieService.getOrganisationCreationCookieValue(any())).thenReturn(Optional.of(organisationFormCookieValue));

        mockMvc.perform(get("/organisation/create/find-organisation"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("registration/organisation/find-organisation"))
                .andExpect(model().attribute("organisationForm", hasProperty("manualEntry", equalTo(false))))
                .andExpect(model().attributeHasFieldErrors("organisationForm", "useSearchResultAddress"));
    }

    @Test
    public void testFindBusinessSelectAddress() throws Exception {
        when(registrationCookieService.getOrganisationTypeCookieValue(any())).thenReturn(Optional.of(organisationTypeForm));
        when(registrationCookieService.getOrganisationCreationCookieValue(any())).thenReturn(Optional.of(organisationForm));

        mockMvc.perform(post("/organisation/create/find-organisation")
                .param("manualEntry", "true")
                .param("addressForm.postcodeInput", POSTCODE_LOOKUP)
                .param("addressForm.selectedPostcodeIndex", String.valueOf(0))
                .param("select-address", "")
                .header("referer", "/organisation/create/find-organisation/"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name(format("redirect:/organisation/create/find-organisation/%s/0", POSTCODE_LOOKUP_URL_ENCODED)));

        mockMvc.perform(get(format("/organisation/create/find-organisation/%s/0", POSTCODE_LOOKUP)))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("registration/organisation/find-organisation"))
                .andExpect(model().attribute("organisationForm", hasProperty("manualEntry", equalTo(false))));
    }

    @Test
    public void testSearchOrganisation_findBusinessSearchCompanyHouse() throws Exception {
        OrganisationCreationForm organisationFormCookieValue = new OrganisationCreationForm();
        organisationFormCookieValue.setOrganisationSearching(true);

        when(registrationCookieService.getOrganisationTypeCookieValue(any())).thenReturn(Optional.of(organisationTypeForm));
        when(registrationCookieService.getOrganisationCreationCookieValue(any())).thenReturn(Optional.of(organisationFormCookieValue));

        String companyName = "Business Name";

        mockMvc.perform(get("/organisation/create/find-organisation/" + companyName))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("registration/organisation/find-organisation"))
                .andExpect(model().attribute("organisationForm", hasProperty("organisationSearching", equalTo(true))));
        verify(registrationCookieService, times(1)).saveToOrganisationCreationCookie(any(), any());
    }

    @Test
    public void testFindBusinessSearchCompanyHouseInvalid() throws Exception {
        when(registrationCookieService.getOrganisationTypeCookieValue(any())).thenReturn(Optional.of(organisationTypeForm));

        mockMvc.perform(post("/organisation/create/find-organisation")
                .param("organisationSearchName", "")
                .param("search-organisation", ""))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/organisation/create/find-organisation?searchTerm="));
    }

    @Test
    public void testSaveOrganisationtest_findBusinessConfirmCompanyDetailsInvalid() throws Exception {
        when(registrationCookieService.getOrganisationTypeCookieValue(any())).thenReturn(Optional.of(organisationTypeForm));
        when(registrationCookieService.getOrganisationCreationCookieValue(any())).thenReturn(Optional.of(organisationForm));

        mockMvc.perform(post("/organisation/create/find-organisation")
                .param("organisationName", "")
                .param("save-organisation-details", "")
                .header("referer", "/organisation/create/find-organisation/"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/organisation/create/find-organisation"))
                .andReturn();

        verify(registrationCookieService, times(1)).saveToOrganisationCreationCookie(any(), any());
    }

    @Test
    public void testSaveOrganisation_successfulSaveShouldRedirectToConfirmCompanyDetailsPage() throws Exception {
        when(registrationCookieService.getOrganisationTypeCookieValue(any())).thenReturn(Optional.of(organisationTypeForm));
        when(registrationCookieService.getOrganisationCreationCookieValue(any())).thenReturn(Optional.of(organisationForm));

        mockMvc.perform(post("/organisation/create/find-organisation")
                .param("organisationName", "BusinessName")
                .param("manualEntry", "true")
                .param("addressForm.selectedPostcode.addressLine1", "a")
                .param("addressForm.selectedPostcode.locality", "abc")
                .param("addressForm.selectedPostcode.region", "def")
                .param("addressForm.selectedPostcode.postcode", "abc post")
                .param("addressForm.selectedPostcode.town", "abc town")
                .param("save-organisation-details", "")
                .header("referer", "/organisation/create/find-organisation/"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/organisation/create/confirm-organisation"));
    }

    @Test
    public void testCreateOrganisation() throws Exception {
        when(registrationCookieService.getOrganisationTypeCookieValue(any())).thenReturn(Optional.of(organisationTypeForm));
        when(registrationCookieService.getOrganisationCreationCookieValue(any())).thenReturn(Optional.of(organisationForm));

        mockMvc.perform(get("/organisation/create/find-organisation"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("registration/organisation/find-organisation"))
                .andExpect(model().attributeExists("organisationForm"));
    }

    @Test
    public void testSelectedBusinessGet() throws Exception {
        when(registrationCookieService.getOrganisationTypeCookieValue(any())).thenReturn(Optional.of(organisationTypeForm));
        when(registrationCookieService.getOrganisationCreationCookieValue(any())).thenReturn(Optional.of(organisationForm));

        mockMvc.perform(get("/organisation/create/selected-organisation/" + COMPANY_ID)
                .header("referer", "/organisation/create/selected-organisation/"))
                .andExpect(status().isOk());
    }

    @Test
    public void testSelectedBusinessSubmit() throws Exception {
        when(registrationCookieService.getOrganisationTypeCookieValue(any())).thenReturn(Optional.of(organisationTypeForm));
        when(registrationCookieService.getOrganisationCreationCookieValue(any())).thenReturn(Optional.of(organisationForm));

        mockMvc.perform(post("/organisation/create/selected-organisation/" + COMPANY_ID)
                .param("addressForm.postcodeInput", POSTCODE_LOOKUP)
                .param("searchOrganisationId", COMPANY_ID)
                .param("search-address", "")
                .header("referer", "/organisation/create/selected-organisation/"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name(format("redirect:/organisation/create/selected-organisation/%s/search-postcode?searchTerm=%s", COMPANY_ID, POSTCODE_LOOKUP_URL_ENCODED)));
    }

    @Test
    public void testSearchAddress_setUseSearchResultAddressInCookieShouldResultInSuccessfulSaveOfOrganisationToCookie() throws Exception {
        when(registrationCookieService.getOrganisationTypeCookieValue(any())).thenReturn(Optional.of(organisationTypeForm));
        when(registrationCookieService.getOrganisationCreationCookieValue(any())).thenReturn(Optional.of(organisationFormUseSearchResult));

        mockMvc.perform(post("/organisation/create/selected-organisation/" + COMPANY_ID)
                .param("addressForm.postcodeInput", POSTCODE_LOOKUP)
                .param("search-address", "")
                .param("searchOrganisationId", COMPANY_ID)
                .header("referer", "/organisation/create/selected-organisation/"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name(format("redirect:/organisation/create/selected-organisation/%s/search-postcode?searchTerm=%s", COMPANY_ID, POSTCODE_LOOKUP_URL_ENCODED)));

        mockMvc.perform(get(format("/organisation/create/selected-organisation/%s/search-postcode?searchTerm=%s", COMPANY_ID, POSTCODE_LOOKUP)))
        .andExpect(status().is2xxSuccessful())
        .andExpect(view().name("registration/organisation/confirm-selected-organisation"))
        .andExpect(model().attributeHasNoErrors("organisationForm"));

        verify(registrationCookieService, atLeastOnce()).saveToOrganisationCreationCookie(any(), any());
    }

    @Test
    public void testAmendOrganisationAddressPostCode() throws Exception {
        when(registrationCookieService.getOrganisationTypeCookieValue(any())).thenReturn(Optional.of(organisationTypeForm));
        when(registrationCookieService.getOrganisationCreationCookieValue(any())).thenReturn(Optional.of(organisationFormUseSearchResult));
        when(addressRestService.doLookup(anyString())).thenReturn(restSuccess(new ArrayList<>()));
        mockMvc.perform(get(format("/organisation/create/selected-organisation/%s/%s", COMPANY_ID, POSTCODE_LOOKUP)))
        .andExpect(status().is2xxSuccessful())
        .andExpect(view().name("registration/organisation/confirm-selected-organisation"))
        .andExpect(model().attributeHasNoErrors("organisationForm"))
        .andExpect(model().attributeExists("model"));
    }

    @Test
    public void testAmendOrganisationAddressPostCode_selectedOrganisationSelectedPostcode() throws Exception {
        when(registrationCookieService.getOrganisationTypeCookieValue(any())).thenReturn(Optional.of(organisationTypeForm));
        when(registrationCookieService.getOrganisationCreationCookieValue(any())).thenReturn(Optional.of(organisationFormUseSearchResult));

        ArrayList<AddressResource> addresses = new ArrayList<>();
        addresses.add(new AddressResource());
        addresses.add(new AddressResource());
        addresses.add(new AddressResource());
        when(addressRestService.doLookup(anyString())).thenReturn(restSuccess(addresses));
        mockMvc.perform(get(format("/organisation/create/selected-organisation/%s/0", COMPANY_ID)))
        .andExpect(status().is2xxSuccessful())
        .andExpect(view().name("registration/organisation/confirm-selected-organisation"))
        .andExpect(model().attributeHasNoErrors("organisationForm"))
        .andExpect(model().attributeExists("model"));
    }

    @Test
    public void testSelectAddress_selectedBusinessSubmitSelectAddress() throws Exception {
        when(registrationCookieService.getOrganisationTypeCookieValue(any())).thenReturn(Optional.of(organisationTypeForm));
        when(registrationCookieService.getOrganisationCreationCookieValue(any())).thenReturn(Optional.of(organisationFormUseSearchResult));

        mockMvc.perform(post("/organisation/create/selected-organisation/" + COMPANY_ID)
                .param("addressForm.postcodeInput", POSTCODE_LOOKUP)
                .param("searchOrganisationId", COMPANY_ID)
                .param("addressForm.selectedPostcodeIndex", "0")
                .param("select-address", "")
                .header("referer", "/organisation/create/selected-organisation/"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name(format("redirect:/organisation/create/selected-organisation/%s/%s", COMPANY_ID, "0")));

        mockMvc.perform(get(format("/organisation/create/selected-organisation/%s/%s", COMPANY_ID, "0")))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("registration/organisation/confirm-selected-organisation"))
                .andExpect(model().attributeHasNoErrors("organisationForm"));
    }

    @Test
    public void testManualAddress_selectedBusinessManualAddress() throws Exception {
        OrganisationCreationForm organisationFormCookieValue = new OrganisationCreationForm();
        organisationFormCookieValue.setUseSearchResultAddress(true);

        when(registrationCookieService.getOrganisationTypeCookieValue(any())).thenReturn(Optional.of(organisationTypeForm));
        when(registrationCookieService.getOrganisationCreationCookieValue(any())).thenReturn(Optional.of(organisationFormCookieValue));

        mockMvc.perform(post("/organisation/create/selected-organisation/" + COMPANY_ID)
                .param("searchOrganisationId", COMPANY_ID)
                .param("manual-address", "true"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/organisation/create/selected-organisation/"+COMPANY_ID));
    }

    @Test
    public void testSelectedBusinessSaveBusiness() throws Exception {

        when(registrationCookieService.getOrganisationTypeCookieValue(any())).thenReturn(Optional.of(organisationTypeForm));
        when(registrationCookieService.getOrganisationCreationCookieValue(any())).thenReturn(Optional.of(organisationForm));

        mockMvc.perform(post("/organisation/create/selected-organisation/" + COMPANY_ID)
                .param("useSearchResultAddress", "true")
                .param("_useSearchResultAddress", "on")
                .param("save-organisation-details", "true")
                .param("searchOrganisationId", COMPANY_ID)
                .header("referer", "/organisation/create/selected-organisation/"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/organisation/create/confirm-organisation"));
    }



    /**
     * Check if request is redirected back to the form, when submit is invalid.
     */
    @Test
    public void testSearchAddress_selectedBusinessInvalidSubmit() throws Exception {
        when(registrationCookieService.getOrganisationTypeCookieValue(any())).thenReturn(Optional.of(organisationTypeForm));
        when(registrationCookieService.getOrganisationCreationCookieValue(any())).thenReturn(Optional.of(organisationForm));

        mockMvc.perform(post("/organisation/create/selected-organisation/" + COMPANY_ID)
                .param("manualEntry", "true")
                .param("addressForm.postcodeInput", "")
                .param("search-address", "")
                .param("searchOrganisationId", COMPANY_ID)
                .header("referer", "/organisation/create/selected-organisation/"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name(format("redirect:/organisation/create/selected-organisation/%s", COMPANY_ID)));
    }
}
