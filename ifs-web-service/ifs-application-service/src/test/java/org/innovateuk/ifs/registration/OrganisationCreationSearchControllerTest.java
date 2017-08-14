package org.innovateuk.ifs.registration;

import org.apache.commons.lang3.CharEncoding;
import org.hamcrest.Matchers;
import org.innovateuk.ifs.BaseUnitTest;
import org.innovateuk.ifs.address.resource.AddressResource;
import org.innovateuk.ifs.address.service.AddressRestService;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.organisation.resource.OrganisationSearchResult;
import org.innovateuk.ifs.registration.form.OrganisationCreationForm;
import org.innovateuk.ifs.registration.populator.OrganisationCreationSelectTypePopulator;
import org.innovateuk.ifs.registration.service.RegistrationCookieService;
import org.innovateuk.ifs.user.resource.OrganisationResource;
import org.innovateuk.ifs.user.resource.OrganisationTypeEnum;
import org.innovateuk.ifs.user.resource.OrganisationTypeResource;
import org.innovateuk.ifs.user.service.OrganisationSearchRestService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.validation.Validator;

import javax.servlet.http.Cookie;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.lang.String.format;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasProperty;
import static org.innovateuk.ifs.BaseControllerMockMVCTest.setupMockMvc;
import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.user.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.innovateuk.ifs.user.builder.OrganisationTypeResourceBuilder.newOrganisationTypeResource;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.web.util.UriUtils.encodeQueryParam;

@RunWith(MockitoJUnitRunner.class)
@TestPropertySource(locations = "classpath:application.properties")
public class OrganisationCreationSearchControllerTest extends BaseUnitTest {
    @InjectMocks
    private OrganisationCreationSearchController organisationCreationController;
    private ApplicationResource applicationResource;

    @Mock
    private Validator validator;
    @Mock
    private OrganisationSearchRestService organisationSearchRestService;

    @Mock
    private AddressRestService addressRestService;

    @Spy
    @InjectMocks
    private OrganisationCreationSelectTypePopulator organisationCreationSelectTypePopulator;

    private String COMPANY_ID = "08241216";
    private String COMPANY_NAME = "NETWORTHNET LTD";
    private String POSTCODE_LOOKUP = "CH64 3RU";
    private String POSTCODE_LOOKUP_URL_ENCODED = "CH64%203RU";
    private OrganisationResource organisationResource;
    private Cookie organisationTypeBusiness;
    private Cookie organisationForm;
    private Cookie organisationFormWithPostcodeInput;
    private Cookie organisationFormWithSelectedPostcode;
    private Cookie organisationFormWithEmptyPostcodeInput;
    private Cookie competitionIdCookie;
    private Long competitionId;
    private Cookie inviteHash;

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

        organisationTypeBusiness = new Cookie("organisationType", encryptor.encrypt("{\"organisationType\":1}"));
        organisationForm = new Cookie("organisationForm", encryptor.encrypt("{\"addressForm\":{\"triedToSave\":false,\"postcodeInput\":\"\",\"selectedPostcodeIndex\":null,\"selectedPostcode\":null,\"postcodeOptions\":[],\"manualAddress\":false},\"triedToSave\":false,\"organisationType\":{\"id\":1,\"name\":\"Business\",\"parentOrganisationType\":null},\"organisationSearchName\":null,\"searchOrganisationId\":\""+COMPANY_ID+"\",\"organisationSearching\":false,\"manualEntry\":false,\"useSearchResultAddress\":false,\"organisationSearchResults\":[],\"organisationName\":\"NOMENSA LTD\"}"));
        organisationFormWithPostcodeInput = new Cookie("organisationForm", encryptor.encrypt("{\"addressForm\":{\"triedToSave\":false,\"postcodeInput\":\""+POSTCODE_LOOKUP+"\",\"selectedPostcodeIndex\":null,\"selectedPostcode\":null,\"postcodeOptions\":[],\"manualAddress\":false},\"triedToSave\":false,\"organisationType\":{\"id\":1,\"name\":\"Business\",\"parentOrganisationType\":null},\"organisationSearchName\":null,\"searchOrganisationId\":\""+COMPANY_ID+"\",\"organisationSearching\":false,\"manualEntry\":false,\"useSearchResultAddress\":false,\"organisationSearchResults\":[],\"organisationName\":\"NOMENSA LTD\"}"));
        organisationFormWithSelectedPostcode = new Cookie("organisationForm", encryptor.encrypt("{\"addressForm\":{\"triedToSave\":false,\"postcodeInput\":\""+POSTCODE_LOOKUP+"\",\"selectedPostcodeIndex\":\"0\",\"selectedPostcode\":null,\"postcodeOptions\":[],\"manualAddress\":false},\"triedToSave\":false,\"organisationType\":{\"id\":1,\"name\":\"Business\",\"parentOrganisationType\":null},\"organisationSearchName\":null,\"searchOrganisationId\":\""+COMPANY_ID+"\",\"organisationSearching\":false,\"manualEntry\":false,\"useSearchResultAddress\":false,\"organisationSearchResults\":[],\"organisationName\":\"NOMENSA LTD\"}"));
        organisationFormWithEmptyPostcodeInput = new Cookie("organisationForm", encryptor.encrypt("{\"addressForm\":{\"triedToSearch\":true,\"triedToSave\":false,\"postcodeInput\":\"\",\"selectedPostcodeIndex\":null,\"selectedPostcode\":null,\"postcodeOptions\":[],\"manualAddress\":false},\"triedToSave\":false,\"organisationType\":{\"id\":1,\"name\":\"Business\",\"parentOrganisationType\":null},\"organisationSearchName\":null,\"searchOrganisationId\":\""+COMPANY_ID+"\",\"organisationSearching\":false,\"manualEntry\":false,\"useSearchResultAddress\":false,\"organisationSearchResults\":[],\"organisationName\":\"NOMENSA LTD\"}"));

        competitionId = 2L;
        competitionIdCookie = new Cookie("competitionId", encryptor.encrypt(competitionId.toString()));

        inviteHash = new Cookie(RegistrationCookieService.INVITE_HASH, encryptor.encrypt(INVITE_HASH));
    }

    @Test
    public void testFindBusiness() throws Exception {
        Cookie[] cookies = mockMvc.perform(get("/organisation/create/find-business"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/organisation/create/find-organisation"))
                .andReturn().getResponse().getCookies();

        assertEquals(2, cookies.length);
        assertNotNull(cookies[0]);
        assertNotNull(cookies[1]);
        assertEquals("", Arrays.stream(cookies).filter(cookie -> cookie.getName().equals("flashMessage")).findAny().get().getValue());
        assertEquals(URLEncoder.encode("{\"organisationType\":1,\"leadApplicant\":true}", CharEncoding.UTF_8),
                getDecryptedCookieValue(cookies, "organisationType"));
    }

    @Test
    public void testFindOrganisation() throws Exception {
        Cookie[] cookies = mockMvc.perform(post("/organisation/create/find-organisation")
                .param("organisationSearchName", "BusinessName")
                .cookie(organisationTypeBusiness)
                .cookie(organisationForm)
                .param("not-in-company-house", ""))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/organisation/create/find-organisation"))
                .andReturn().getResponse().getCookies();

        mockMvc.perform(get("/organisation/create/find-organisation")
                .cookie(organisationTypeBusiness)
                .cookie(cookies))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("registration/organisation/find-organisation"))
                .andExpect(model().attribute("organisationForm", hasProperty("manualEntry", equalTo(true))));
    }

    @Test
    public void testFindBusinessManualAddress() throws Exception {
        mockMvc.perform(post("/organisation/create/find-organisation")
                .param("organisationSearchName", "BusinessName")
                .param("manual-address", "")
                .cookie(organisationTypeBusiness)
                .cookie(organisationForm))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/organisation/create/find-organisation"))
                .andExpect(model().attribute("organisationForm", Matchers.isA(OrganisationCreationForm.class)))
                .andExpect(model().attribute("organisationForm", hasProperty("manualEntry", equalTo(true))));
    }

    @Test
    public void testFindBusinessSearchAddress() throws Exception {
        mockMvc.perform(post("/organisation/create/find-organisation")
                .cookie(organisationTypeBusiness)
                .cookie(organisationForm)
                .param("addressForm.postcodeInput", POSTCODE_LOOKUP)
                .param("search-address", "")
                .header("referer", "/organisation/create/find-organisation/"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name(format("redirect:/organisation/create/find-organisation?searchTerm=%s", POSTCODE_LOOKUP_URL_ENCODED)));

        mockMvc.perform(get(format("/organisation/create/find-organisation/%s", POSTCODE_LOOKUP))
                .cookie(organisationTypeBusiness)
                .cookie(organisationForm))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("registration/organisation/find-organisation"))
                .andExpect(model().attribute("organisationForm", hasProperty("manualEntry", equalTo(false))));
    }

    @Test
    public void testFindBusinessSearchAddressInvalid() throws Exception {
        mockMvc.perform(post("/organisation/create/find-organisation")
                .param("postcodeInput", "")
                .param("search-address", "")
                .cookie(organisationTypeBusiness)
                .header("referer", "/organisation/create/find-organisation/"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/organisation/create/find-organisation"));

        mockMvc.perform(get("/organisation/create/find-organisation")
                .cookie(organisationTypeBusiness))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("registration/organisation/find-organisation"))
                .andExpect(model().attribute("organisationForm", hasProperty("manualEntry", equalTo(false))));
    }

    @Test
    public void testAddressSearchShowsEmptyPostCodeValidationError() throws Exception {
        mockMvc.perform(get("/organisation/create/find-organisation")
                .cookie(organisationFormWithEmptyPostcodeInput))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("registration/organisation/find-organisation"))
                .andExpect(model().attribute("organisationForm", hasProperty("manualEntry", equalTo(false))))
                .andExpect(model().attributeHasFieldErrors("organisationForm", "addressForm.postcodeInput"));
    }

    @Test
    public void testFindBusinessSelectAddress() throws Exception {
        mockMvc.perform(post("/organisation/create/find-organisation")
                .param("manualEntry", "true")
                .param("addressForm.postcodeInput", POSTCODE_LOOKUP)
                .param("addressForm.selectedPostcodeIndex", String.valueOf(0))
                .param("select-address", "")
                .cookie(organisationTypeBusiness)
                .cookie(organisationForm)
                .header("referer", "/organisation/create/find-organisation/"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name(format("redirect:/organisation/create/find-organisation/%s/0", POSTCODE_LOOKUP_URL_ENCODED)));

        mockMvc.perform(get(format("/organisation/create/find-organisation/%s/0", POSTCODE_LOOKUP))
                .cookie(organisationTypeBusiness)
                .cookie(organisationForm))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("registration/organisation/find-organisation"))
                .andExpect(model().attribute("organisationForm", hasProperty("manualEntry", equalTo(false))));
    }

    @Test
    public void testFindBusinessSearchCompanyHouse() throws Exception {
        String companyName = "Business Name";
        String searchString = "  Business   Name   ";
        Cookie cookie = mockMvc.perform(post("/organisation/create/find-organisation")
                .cookie(organisationTypeBusiness)
                .param("organisationSearchName", searchString)
                .param("search-organisation", "")
                .header("referer", "/organisation/create/find-organisation/"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/organisation/create/find-organisation?searchTerm=" + encodeQueryParam(searchString,"UTF-8")))
                .andExpect(cookie().exists("organisationForm"))
                .andReturn().getResponse().getCookie("organisationForm");

        mockMvc.perform(get("/organisation/create/find-organisation/" + companyName)
                .cookie(organisationTypeBusiness)
                .cookie(cookie))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("registration/organisation/find-organisation"))
                .andExpect(model().attribute("organisationForm", hasProperty("organisationSearching", equalTo(true))));
    }

    @Test
    public void testFindBusinessSearchCompanyHouseInvalid() throws Exception {
        mockMvc.perform(post("/organisation/create/find-organisation")
                .param("organisationSearchName", "")
                .param("search-organisation", "")
                .cookie(organisationTypeBusiness))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/organisation/create/find-organisation?searchTerm="));
    }

    @Test
    public void testFindBusinessConfirmCompanyDetailsInvalid() throws Exception {
        MvcResult result = mockMvc.perform(post("/organisation/create/find-organisation")
                .cookie(organisationTypeBusiness)
                .param("organisationName", "")
                .param("save-organisation-details", "")
                .header("referer", "/organisation/create/find-organisation/"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/organisation/create/find-organisation"))
                .andReturn();

        Cookie cookie = result.getResponse().getCookie("organisationForm");
        assertNotNull(cookie);

        result = mockMvc.perform(get("/organisation/create/find-organisation")
                .cookie(organisationTypeBusiness)
                .cookie(organisationForm)
                .cookie(cookie))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("registration/organisation/find-organisation"))
                .andReturn();

        result.getModelAndView();
        log.warn("mav");
    }

    @Test
    public void testFindBusinessConfirmCompanyDetails() throws Exception {
        mockMvc.perform(post("/organisation/create/find-organisation")
                .cookie(organisationTypeBusiness)
                .param("organisationName", "BusinessName")
                .param("manualEntry", "true")
                .param("addressForm.selectedPostcode.addressLine1", "a")
                .param("addressForm.selectedPostcode.locality", "abc")
                .param("addressForm.selectedPostcode.region", "def")
                .param("addressForm.selectedPostcode.postalcode", "abcass")
                .param("save-organisation-details", "")
                .header("referer", "/organisation/create/find-organisation/"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/organisation/create/confirm-organisation"));
    }

    @Test
    public void testCreateOrganisationBusiness() throws Exception {
        mockMvc.perform(get("/organisation/create/find-organisation")
                .cookie(organisationTypeBusiness))
                .andExpect(status().is2xxSuccessful());

        mockMvc.perform(get("/organisation/create/find-organisation")
                .cookie(organisationTypeBusiness))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("registration/organisation/find-organisation"))
                .andExpect(model().attributeExists("organisationForm"));
    }

    @Test
    public void testConfirmBusiness() throws Exception {
        mockMvc.perform(get("/organisation/create/confirm-organisation")
                .param("searchOrganisationId", COMPANY_ID)
                .cookie(organisationTypeBusiness)
                .cookie(organisationForm))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("registration/organisation/confirm-organisation"))
                .andExpect(model().attributeExists("organisationForm"));
    }

    @Test
    public void testSaveOrganisation() throws Exception {
        MvcResult result = mockMvc.perform(get("/organisation/create/save-organisation")
                .param("searchOrganisationId", COMPANY_ID)
                .cookie(organisationTypeBusiness)
                .cookie(organisationForm))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/registration/register"))
                .andExpect(cookie().exists("organisationId"))
                .andReturn();

        assertEquals("5", getDecryptedCookieValue(result.getResponse().getCookies(), "organisationId"));
    }

    @Test
    public void testSaveOrganisationWithInvite() throws Exception {
        MvcResult result = mockMvc.perform(get("/organisation/create/save-organisation")
                .param("searchOrganisationId", COMPANY_ID)
                .cookie(organisationTypeBusiness)
                .cookie(organisationForm)
                .cookie(inviteHash))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/registration/register"))
                .andExpect(cookie().exists("organisationId"))
                .andReturn();

        assertEquals("5", getDecryptedCookieValue(result.getResponse().getCookies(), "organisationId"));
    }

    @Test
    public void testSelectedBusinessGet() throws Exception {
        mockMvc.perform(get("/organisation/create/selected-organisation/" + COMPANY_ID)
                .cookie(organisationTypeBusiness)
                .cookie(organisationForm)
                .header("referer", "/organisation/create/selected-organisation/"))
                .andExpect(status().isOk());
    }

    @Test
    public void testSelectedBusinessSubmit() throws Exception {
        mockMvc.perform(post("/organisation/create/selected-organisation/" + COMPANY_ID)
                .param("addressForm.postcodeInput", POSTCODE_LOOKUP)
                .param("searchOrganisationId", COMPANY_ID)
                .param("search-address", "")
                .cookie(organisationTypeBusiness)
                .cookie(organisationForm)
                .header("referer", "/organisation/create/selected-organisation/"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name(format("redirect:/organisation/create/selected-organisation/%s/search-postcode?searchTerm=%s", COMPANY_ID, POSTCODE_LOOKUP_URL_ENCODED)));
    }

    @Test
    public void testSelectedBusinessSubmitSearchAddress() throws Exception {
        mockMvc.perform(post("/organisation/create/selected-organisation/" + COMPANY_ID)
                .param("addressForm.postcodeInput", POSTCODE_LOOKUP)
                .param("search-address", "")
                .param("searchOrganisationId", COMPANY_ID)
                .cookie(organisationTypeBusiness)
                .cookie(organisationForm)
                .header("referer", "/organisation/create/selected-organisation/"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name(format("redirect:/organisation/create/selected-organisation/%s/search-postcode?searchTerm=%s", COMPANY_ID, POSTCODE_LOOKUP_URL_ENCODED)));

        mockMvc.perform(get(format("/organisation/create/selected-organisation/%s/search-postcode?searchTerm=%s", COMPANY_ID, POSTCODE_LOOKUP))
                .cookie(organisationTypeBusiness)
                .cookie(organisationForm))
        .andExpect(status().is2xxSuccessful())
        .andExpect(view().name("registration/organisation/confirm-selected-organisation"))
        .andExpect(model().attributeHasNoErrors("organisationForm"));


    }
    @Test
    public void testSelectedOrganisationSearchedPostcode() throws Exception {
        when(addressRestService.doLookup(anyString())).thenReturn(restSuccess(new ArrayList<>()));
        mockMvc.perform(get(format("/organisation/create/selected-organisation/%s/%s", COMPANY_ID, POSTCODE_LOOKUP))
                .cookie(organisationTypeBusiness)
                .cookie(organisationFormWithPostcodeInput))
        .andExpect(status().is2xxSuccessful())
        .andExpect(view().name("registration/organisation/confirm-selected-organisation"))
        .andExpect(model().attributeHasNoErrors("organisationForm"))
        .andExpect(model().attributeExists("model"));
    }

    @Test
    public void testSelectedOrganisationSelectedPostcode() throws Exception {
        ArrayList<AddressResource> addresses = new ArrayList<>();
        addresses.add(new AddressResource());
        addresses.add(new AddressResource());
        addresses.add(new AddressResource());
        when(addressRestService.doLookup(anyString())).thenReturn(restSuccess(addresses));
        mockMvc.perform(get(format("/organisation/create/selected-organisation/%s/0", COMPANY_ID))
                .cookie(organisationTypeBusiness)
                .cookie(organisationFormWithSelectedPostcode))
        .andExpect(status().is2xxSuccessful())
        .andExpect(view().name("registration/organisation/confirm-selected-organisation"))
        .andExpect(model().attributeHasNoErrors("organisationForm"))
        .andExpect(model().attributeExists("model"));
    }

    @Test
    public void testSelectedBusinessSubmitSelectAddress() throws Exception {
        mockMvc.perform(post("/organisation/create/selected-organisation/" + COMPANY_ID)
                .param("addressForm.postcodeInput", POSTCODE_LOOKUP)
                .param("searchOrganisationId", COMPANY_ID)
                .cookie(organisationTypeBusiness)
                .cookie(organisationForm)
                .param("addressForm.selectedPostcodeIndex", "0")
                .param("select-address", "")
                .header("referer", "/organisation/create/selected-organisation/"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name(format("redirect:/organisation/create/selected-organisation/%s/%s", COMPANY_ID, "0")));

        mockMvc.perform(get(format("/organisation/create/selected-organisation/%s/%s", COMPANY_ID, "0"))
                .cookie(organisationTypeBusiness)
                .cookie(organisationForm))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("registration/organisation/confirm-selected-organisation"))
                .andExpect(model().attributeHasNoErrors("organisationForm"));
    }

    @Test
    public void testSelectedBusinessManualAddress() throws Exception {
        mockMvc.perform(post("/organisation/create/selected-organisation/" + COMPANY_ID)
                .param("searchOrganisationId", COMPANY_ID)
                .cookie(organisationTypeBusiness)
                .cookie(organisationForm)
                .param("manual-address", "true"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/organisation/create/selected-organisation/"+COMPANY_ID));
    }

    @Test
    public void testSelectedBusinessSaveBusiness() throws Exception {
        mockMvc.perform(post("/organisation/create/selected-organisation/" + COMPANY_ID)
                .param("useSearchResultAddress", "true")
                .param("_useSearchResultAddress", "on")
                .param("save-organisation-details", "true")
                .param("searchOrganisationId", COMPANY_ID)
                .header("referer", "/organisation/create/selected-organisation/")
                .cookie(organisationTypeBusiness)
                .cookie(organisationForm))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/organisation/create/confirm-organisation"));
    }

    @Test
    public void testSelectedBusinessSaveLeadBusiness() throws Exception {
        mockMvc.perform(post("/organisation/create/lead-organisation-type")
                .param("organisationTypeId", OrganisationTypeEnum.RTO.getId().toString())
                .cookie(competitionIdCookie)
                .cookie(organisationForm))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/organisation/create/confirm-organisation"));
    }

    @Test
    public void testSelectedInvalidLeadOrganisationType() throws Exception {
        mockMvc.perform(post("/organisation/create/lead-organisation-type")
                .param("organisationTypeId", OrganisationTypeEnum.RESEARCH.getId().toString())
                .cookie(organisationForm)
                .cookie(competitionIdCookie))
                .andExpect(view().name("registration/organisation/lead-organisation-type"))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasFieldErrors("organisationForm", "organisationTypeId"));
    }

    @Test
    public void testLeadOrganisationTypeNotSelected() throws Exception {
        mockMvc.perform(post("/organisation/create/lead-organisation-type")
                .cookie(organisationForm)
                .cookie(competitionIdCookie))
                .andExpect(view().name("registration/organisation/lead-organisation-type"))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasFieldErrors("organisationForm", "organisationTypeId"));
    }

    /**
     * Check if request is redirected back to the form, when submit is invalid.
     */
    @Test
    public void testSelectedBusinessInvalidSubmit() throws Exception {
        mockMvc.perform(post("/organisation/create/selected-organisation/" + COMPANY_ID)
                .param("manualEntry", "true")
                .param("addressForm.postcodeInput", "")
                .param("search-address", "")
                .cookie(organisationTypeBusiness)
                .param("searchOrganisationId", COMPANY_ID)
                .cookie(organisationForm)
                .header("referer", "/organisation/create/selected-organisation/"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name(format("redirect:/organisation/create/selected-organisation/%s", COMPANY_ID)));
    }

    @Test
    public void testConfirmCompany() throws Exception {
        mockMvc.perform(get("/organisation/create/confirm-organisation")
                .cookie(organisationTypeBusiness)
                .cookie(organisationForm))
                .andExpect(status().is2xxSuccessful())
                .andExpect(model().attributeExists("selectedOrganisation"))
                .andExpect(model().attribute("selectedOrganisation", hasProperty("name", equalTo(COMPANY_NAME))))
                .andExpect(view().name("registration/organisation/confirm-organisation"));
    }

    @Test
    public void testSaveOrganisation_leadApplicantShouldBeRedirectedToConfirmOrganisationPageWhenOnlyOneTypeIsAvailable() throws Exception {
        Cookie organisationTypeBusinessAsLeadApplicant = new Cookie("organisationType", encryptor.encrypt("{\"organisationType\":1, \"leadApplicant\":true}"));
        List<OrganisationTypeResource> organisationTypeResourceList = newOrganisationTypeResource().withId(OrganisationTypeEnum.BUSINESS.getId()).build(1);

        when(competitionService.getOrganisationTypes(competitionId)).thenReturn(organisationTypeResourceList);

        mockMvc.perform(post("/organisation/create/selected-organisation/" + COMPANY_ID)
                .cookie(competitionIdCookie)
                .param("useSearchResultAddress", "true")
                .param("_useSearchResultAddress", "on")
                .param("save-organisation-details", "true")
                .param("searchOrganisationId", COMPANY_ID)
                .header("referer", "/organisation/create/selected-organisation/")
                .cookie(organisationForm)
                .cookie(organisationTypeBusinessAsLeadApplicant))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/organisation/create/confirm-organisation"));
    }

    @Test
    public void testSaveOrganisation_leadApplicantShouldBeShownOrganisationTypePageWhenMultipleTypesAreAvailable() throws Exception {
        Long competitionId = 2L;

        Cookie organisationTypeBusinessAsLeadApplicant = new Cookie("organisationType", encryptor.encrypt("{\"organisationType\":1, \"leadApplicant\":true}"));
        List<OrganisationTypeResource> organisationTypeResourceList = newOrganisationTypeResource().withId(OrganisationTypeEnum.BUSINESS.getId(), OrganisationTypeEnum.RTO.getId()).build(2);

        when(competitionService.getOrganisationTypes(competitionId)).thenReturn(organisationTypeResourceList);

        mockMvc.perform(post("/organisation/create/selected-organisation/" + COMPANY_ID)
                .cookie(competitionIdCookie)
                .param("useSearchResultAddress", "true")
                .param("_useSearchResultAddress", "on")
                .param("save-organisation-details", "true")
                .param("searchOrganisationId", COMPANY_ID)
                .header("referer", "/organisation/create/selected-organisation/")
                .cookie(organisationForm)
                .cookie(organisationTypeBusinessAsLeadApplicant))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/organisation/create/lead-organisation-type"));
    }

    @Test
    public void testSelectOrganisationType_leadApplicantShouldRedirectToConfirmPageWhenOnlyOneOrganisationTypeIsAllowed() throws Exception {
        Long competitionId = 2L;
        Cookie competitionIdCookie = new Cookie("competitionId", encryptor.encrypt(competitionId.toString()));
        List<OrganisationTypeResource> organisationTypeResourceList = newOrganisationTypeResource().withId(OrganisationTypeEnum.BUSINESS.getId()).build(1);

        when(competitionService.getOrganisationTypes(competitionId)).thenReturn(organisationTypeResourceList);

        mockMvc.perform(get("/organisation/create/lead-organisation-type")
                .cookie(competitionIdCookie))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/organisation/create/confirm-organisation"));
    }

    @Test
    public void testConfirmSelectOrganisationType_leadApplicantShouldRedirectToConfirmPageWhenOnlyOneOrganisationTypeIsAllowed() throws Exception {
        Long competitionId = 2L;
        Cookie competitionIdCookie = new Cookie("competitionId", encryptor.encrypt(competitionId.toString()));
        List<OrganisationTypeResource> organisationTypeResourceList = newOrganisationTypeResource().withId(OrganisationTypeEnum.BUSINESS.getId()).build(1);

        when(competitionService.getOrganisationTypes(competitionId)).thenReturn(organisationTypeResourceList);

        mockMvc.perform(post("/organisation/create/lead-organisation-type")
                .cookie(competitionIdCookie))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/organisation/create/confirm-organisation"));
    }
}
