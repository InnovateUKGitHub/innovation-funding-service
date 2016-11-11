package com.worth.ifs.registration;

import com.worth.ifs.BaseUnitTest;
import com.worth.ifs.address.resource.AddressResource;
import com.worth.ifs.address.service.AddressRestService;
import com.worth.ifs.application.resource.ApplicationResource;
import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.organisation.resource.OrganisationSearchResult;
import com.worth.ifs.registration.form.OrganisationCreationForm;
import com.worth.ifs.user.resource.OrganisationResource;
import com.worth.ifs.user.service.OrganisationSearchRestService;
import org.apache.commons.lang3.CharEncoding;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.validation.Validator;

import javax.servlet.http.Cookie;
import java.net.URLEncoder;
import java.util.ArrayList;

import static com.worth.ifs.BaseControllerMockMVCTest.setupMockMvc;
import static com.worth.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static com.worth.ifs.user.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(MockitoJUnitRunner.class)
@TestPropertySource(locations = "classpath:application.properties")
public class OrganisationCreationControllerTest extends BaseUnitTest {
    @InjectMocks
    private OrganisationCreationController organisationCreationController;
    private ApplicationResource applicationResource;

    @Mock
    private Validator validator;
    @Mock
    private OrganisationSearchRestService organisationSearchRestService;

    @Mock
    private AddressRestService addressRestService;

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
    private Cookie inviteHash;

    @Before
    public void setUp() {

        // Process mock annotations
        MockitoAnnotations.initMocks(this);

        mockMvc = setupMockMvc(organisationCreationController, () -> loggedInUser, env, messageSource);

        super.setup();

        this.setupInvites();

        applicationResource = newApplicationResource().withId(6L).withName("some application").build();
        organisationResource = newOrganisationResource().withId(5L).withName(COMPANY_NAME).build();
        OrganisationSearchResult organisationSearchResult = new OrganisationSearchResult(COMPANY_ID, COMPANY_NAME);
        when(organisationService.getCompanyHouseOrganisation(COMPANY_ID)).thenReturn(organisationSearchResult);
        when(organisationService.saveForAnonymousUserFlow(any(OrganisationResource.class))).thenReturn(organisationResource);
        when(applicationService.createApplication(anyLong(), anyLong(), anyString())).thenReturn(applicationResource);
        when(organisationSearchRestService.getOrganisation(businessOrganisationTypeResource.getId(), COMPANY_ID)).thenReturn(RestResult.restSuccess(organisationSearchResult));
        when(organisationSearchRestService.searchOrganisation(anyLong(), anyString())).thenReturn(RestResult.restSuccess(new ArrayList<>()));
        when(addressRestService.validatePostcode("CH64 3RU")).thenReturn(RestResult.restSuccess(true));

        organisationTypeBusiness = new Cookie("organisationType", "{\"organisationType\":1}");
        organisationForm = new Cookie("organisationForm", "{\"addressForm\":{\"triedToSave\":false,\"postcodeInput\":\"\",\"selectedPostcodeIndex\":null,\"selectedPostcode\":null,\"postcodeOptions\":[],\"manualAddress\":false},\"triedToSave\":false,\"organisationType\":{\"id\":1,\"name\":\"Business\",\"parentOrganisationType\":null},\"organisationSearchName\":null,\"searchOrganisationId\":\""+COMPANY_ID+"\",\"organisationSearching\":false,\"manualEntry\":false,\"useSearchResultAddress\":false,\"organisationSearchResults\":[],\"organisationName\":\"NOMENSA LTD\"}");
        organisationFormWithPostcodeInput = new Cookie("organisationForm", "{\"addressForm\":{\"triedToSave\":false,\"postcodeInput\":\""+POSTCODE_LOOKUP+"\",\"selectedPostcodeIndex\":null,\"selectedPostcode\":null,\"postcodeOptions\":[],\"manualAddress\":false},\"triedToSave\":false,\"organisationType\":{\"id\":1,\"name\":\"Business\",\"parentOrganisationType\":null},\"organisationSearchName\":null,\"searchOrganisationId\":\""+COMPANY_ID+"\",\"organisationSearching\":false,\"manualEntry\":false,\"useSearchResultAddress\":false,\"organisationSearchResults\":[],\"organisationName\":\"NOMENSA LTD\"}");
        organisationFormWithSelectedPostcode = new Cookie("organisationForm", "{\"addressForm\":{\"triedToSave\":false,\"postcodeInput\":\""+POSTCODE_LOOKUP+"\",\"selectedPostcodeIndex\":\"0\",\"selectedPostcode\":null,\"postcodeOptions\":[],\"manualAddress\":false},\"triedToSave\":false,\"organisationType\":{\"id\":1,\"name\":\"Business\",\"parentOrganisationType\":null},\"organisationSearchName\":null,\"searchOrganisationId\":\""+COMPANY_ID+"\",\"organisationSearching\":false,\"manualEntry\":false,\"useSearchResultAddress\":false,\"organisationSearchResults\":[],\"organisationName\":\"NOMENSA LTD\"}");
        organisationFormWithEmptyPostcodeInput = new Cookie("organisationForm", "{\"addressForm\":{\"triedToSearch\":true,\"triedToSave\":false,\"postcodeInput\":\"\",\"selectedPostcodeIndex\":null,\"selectedPostcode\":null,\"postcodeOptions\":[],\"manualAddress\":false},\"triedToSave\":false,\"organisationType\":{\"id\":1,\"name\":\"Business\",\"parentOrganisationType\":null},\"organisationSearchName\":null,\"searchOrganisationId\":\""+COMPANY_ID+"\",\"organisationSearching\":false,\"manualEntry\":false,\"useSearchResultAddress\":false,\"organisationSearchResults\":[],\"organisationName\":\"NOMENSA LTD\"}");
        inviteHash = new Cookie(AcceptInviteController.INVITE_HASH, INVITE_HASH);
    }

    @Test
    public void testCreateAccountOrganisationType() throws Exception {
        mockMvc.perform(get("/organisation/create/create-organisation-type"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("registration/organisation/create-organisation-type"));
    }

    @Test
    public void testFindBusiness() throws Exception {
        Cookie[] cookies = mockMvc.perform(MockMvcRequestBuilders.get("/organisation/create/find-business"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.view().name("redirect:/organisation/create/find-organisation"))
                .andReturn().getResponse().getCookies();

        assertEquals(2, cookies.length);
        assertNotNull(cookies[0]);
        assertNotNull(cookies[1]);
        assertEquals("flashMessage", cookies[0].getName());
        assertEquals("", cookies[0].getValue());
        assertEquals("organisationType", cookies[1].getName());
        assertEquals(URLEncoder.encode("{\"organisationType\":1}", CharEncoding.UTF_8), cookies[1].getValue());
    }

    @Test
    public void testFindOrganisation() throws Exception {
        Cookie[] cookies = mockMvc.perform(MockMvcRequestBuilders.post("/organisation/create/find-organisation")
                .param("organisationSearchName", "BusinessName")
                .cookie(organisationTypeBusiness)
                .cookie(organisationForm)
                .param("not-in-company-house", "")
        )
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.view().name("redirect:/organisation/create/find-organisation"))
                .andReturn().getResponse().getCookies();

        mockMvc.perform(MockMvcRequestBuilders.get("/organisation/create/find-organisation")
                .cookie(organisationTypeBusiness)
                .cookie(cookies))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andExpect(MockMvcResultMatchers.view().name("registration/organisation/find-organisation"))
                .andExpect(MockMvcResultMatchers.model().attribute("organisationForm", Matchers.hasProperty("manualEntry", Matchers.equalTo(true))));
    }

    @Test
    public void testFindBusinessManualAddress() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/organisation/create/find-organisation")
                .param("organisationSearchName", "BusinessName")
                .param("manual-address", "")
                .cookie(organisationTypeBusiness)
                .cookie(organisationForm)
        )
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.view().name("redirect:/organisation/create/find-organisation"))
                .andExpect(MockMvcResultMatchers.model().attribute("organisationForm", Matchers.isA(OrganisationCreationForm.class)))
                .andExpect(MockMvcResultMatchers.model().attribute("organisationForm", Matchers.hasProperty("manualEntry", Matchers.equalTo(true))));
//                .andExpect(MockMvcResultMatchers.model().attribute("organisationForm", Matchers.hasProperty("manualAddress", Matchers.equalTo(true))));
    }

    @Test
    public void testFindBusinessSearchAddress() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/organisation/create/find-organisation")
                .cookie(organisationTypeBusiness)
                .cookie(organisationForm)
                .param("addressForm.postcodeInput", POSTCODE_LOOKUP)
                .param("search-address", "")
                .header("referer", "/organisation/create/find-organisation/")
        )
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.view().name(String.format("redirect:/organisation/create/find-organisation?searchTerm=%s", POSTCODE_LOOKUP_URL_ENCODED)));

        mockMvc.perform(MockMvcRequestBuilders.get(String.format("/organisation/create/find-organisation/%s", POSTCODE_LOOKUP))
                .cookie(organisationTypeBusiness)
                .cookie(organisationForm))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andExpect(MockMvcResultMatchers.view().name("registration/organisation/find-organisation"))
                .andExpect(MockMvcResultMatchers.model().attribute("organisationForm", Matchers.hasProperty("manualEntry", Matchers.equalTo(false))));
    }

    @Test
    public void testFindBusinessSearchAddressInvalid() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/organisation/create/find-organisation")
                .param("postcodeInput", "")
                .param("search-address", "")
                .cookie(organisationTypeBusiness)
                .header("referer", "/organisation/create/find-organisation/")
        )
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.view().name(String.format("redirect:/organisation/create/find-organisation")));

        mockMvc.perform(MockMvcRequestBuilders.get(String.format("/organisation/create/find-organisation"))
                .cookie(organisationTypeBusiness)
        )
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andExpect(MockMvcResultMatchers.view().name("registration/organisation/find-organisation"))
                .andExpect(MockMvcResultMatchers.model().attribute("organisationForm", Matchers.hasProperty("manualEntry", Matchers.equalTo(false))));
    }

    @Test
    public void testAddressSearchShowsEmptyPostCodeValidationError() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(String.format("/organisation/create/find-organisation"))
                .cookie(organisationFormWithEmptyPostcodeInput)
        )
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andExpect(MockMvcResultMatchers.view().name("registration/organisation/find-organisation"))
                .andExpect(MockMvcResultMatchers.model().attribute("organisationForm", Matchers.hasProperty("manualEntry", Matchers.equalTo(false))))
                .andExpect(model().attributeHasFieldErrors("organisationForm", "addressForm.postcodeInput"));;
    }

    @Test
    public void testFindBusinessSelectAddress() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/organisation/create/find-organisation")
                .param("manualEntry", "true")
                .param("addressForm.postcodeInput", POSTCODE_LOOKUP)
                .param("addressForm.selectedPostcodeIndex", String.valueOf(0))
                .param("select-address", "")
                .cookie(organisationTypeBusiness)
                .cookie(organisationForm)
                .header("referer", "/organisation/create/find-organisation/")
        )
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.view().name(String.format("redirect:/organisation/create/find-organisation/%s/0", POSTCODE_LOOKUP_URL_ENCODED)));

        mockMvc.perform(MockMvcRequestBuilders.get(String.format("/organisation/create/find-organisation/%s/0", POSTCODE_LOOKUP))
                .cookie(organisationTypeBusiness)
                .cookie(organisationForm)
        )
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andExpect(MockMvcResultMatchers.view().name("registration/organisation/find-organisation"))
                .andExpect(MockMvcResultMatchers.model().attribute("organisationForm", Matchers.hasProperty("manualEntry", Matchers.equalTo(false))));
    }

    @Test
    public void testFindBusinessSearchCompanyHouse() throws Exception {
        String companyName = "BusinessName";
        Cookie cookie = mockMvc.perform(MockMvcRequestBuilders.post("/organisation/create/find-organisation")
                .cookie(organisationTypeBusiness)
                .param("organisationSearchName", companyName)
                .param("search-organisation", "")
                .header("referer", "/organisation/create/find-organisation/")
        )
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.view().name("redirect:/organisation/create/find-organisation?searchTerm=" + companyName))
                .andExpect(cookie().exists("organisationForm"))
                .andReturn().getResponse().getCookie("organisationForm");

        mockMvc.perform(MockMvcRequestBuilders.get("/organisation/create/find-organisation/" + companyName)
                .cookie(organisationTypeBusiness)
                .cookie(cookie)
        )
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andExpect(MockMvcResultMatchers.view().name("registration/organisation/find-organisation"))
                .andExpect(MockMvcResultMatchers.model().attribute("organisationForm", Matchers.hasProperty("organisationSearching", Matchers.equalTo(true))));
    }

    @Test
    public void testFindBusinessSearchCompanyHouseInvalid() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/organisation/create/find-organisation")
                .param("organisationSearchName", "")
                .param("search-organisation", "")
                .cookie(organisationTypeBusiness)
        )
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.view().name("redirect:/organisation/create/find-organisation?searchTerm="));
    }

    @Test
    public void testFindBusinessConfirmCompanyDetailsInvalid() throws Exception {
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/organisation/create/find-organisation")
                .cookie(organisationTypeBusiness)
                .param("organisationName", "")
                .param("save-organisation-details", "")
                .header("referer", "/organisation/create/find-organisation/")
        )
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.view().name("redirect:/organisation/create/find-organisation"))
                .andReturn();

        Cookie cookie = result.getResponse().getCookie("organisationForm");
        assertNotNull(cookie);

        result = mockMvc.perform(MockMvcRequestBuilders.get("/organisation/create/find-organisation")
                .cookie(organisationTypeBusiness)
                .cookie(organisationForm)
                .cookie(cookie)
        )
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andExpect(MockMvcResultMatchers.view().name("registration/organisation/find-organisation"))
                .andReturn();

        result.getModelAndView();
        log.warn("mav");
    }

    @Test
    public void testFindBusinessConfirmCompanyDetails() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/organisation/create/find-organisation")
                .cookie(organisationTypeBusiness)
                .param("organisationName", "BusinessName")
                .param("manualEntry", "true")
                .param("addressForm.selectedPostcode.addressLine1", "a")
                .param("addressForm.selectedPostcode.locality", "abc")
                .param("addressForm.selectedPostcode.region", "def")
                .param("addressForm.selectedPostcode.postalcode", "abcass")
                .param("save-organisation-details", "")
                .header("referer", "/organisation/create/find-organisation/")
        )
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.view().name("redirect:/organisation/create/confirm-organisation"));
    }

    @Test
    public void testCreateOrganisationBusiness() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/organisation/create/find-organisation")
                .cookie(organisationTypeBusiness)
        )
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful());

        mockMvc.perform(MockMvcRequestBuilders.get("/organisation/create/find-organisation")
                .cookie(organisationTypeBusiness)
        )
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andExpect(MockMvcResultMatchers.view().name("registration/organisation/find-organisation"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("organisationForm"));
    }

    @Test
    public void testConfirmBusiness() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/organisation/create/confirm-organisation")
                .param("searchOrganisationId", COMPANY_ID)
                .cookie(organisationTypeBusiness)
                .cookie(organisationForm)
        )
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andExpect(MockMvcResultMatchers.view().name("registration/organisation/confirm-organisation"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("organisationForm"));
    }

    @Test
    public void testSaveOrganisation() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/organisation/create/save-organisation")
                .param("searchOrganisationId", COMPANY_ID)
                .cookie(organisationTypeBusiness)
                .cookie(organisationForm)
        )
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.view().name("redirect:/registration/register"))
                .andExpect(cookie().exists("organisationId"))
                .andExpect(cookie().value("organisationId", "5"));
    }

    @Test
    public void testSaveOrganisationWithInvite() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/organisation/create/save-organisation")
                .param("searchOrganisationId", COMPANY_ID)
                .cookie(organisationTypeBusiness)
                .cookie(organisationForm)
                .cookie(inviteHash)
        )
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.view().name("redirect:/registration/register"))
                .andExpect(cookie().exists("organisationId"))
                .andExpect(cookie().value("organisationId", "5"));
    }

    @Test
    public void testSelectedBusinessGet() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/organisation/create/selected-organisation/" + COMPANY_ID)
                .cookie(organisationTypeBusiness)
                .cookie(organisationForm)
                .header("referer", "/organisation/create/selected-organisation/")
        )
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void testSelectedBusinessSubmit() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/organisation/create/selected-organisation/" + COMPANY_ID)
                .param("addressForm.postcodeInput", POSTCODE_LOOKUP)
                .param("searchOrganisationId", COMPANY_ID)
                .param("search-address", "")
                .cookie(organisationTypeBusiness)
                .cookie(organisationForm)
                .header("referer", "/organisation/create/selected-organisation/")
        )
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.view().name(String.format("redirect:/organisation/create/selected-organisation/%s/search-postcode?searchTerm=%s", COMPANY_ID, POSTCODE_LOOKUP_URL_ENCODED)));
    }

    @Test
    public void testSelectedBusinessSubmitSearchAddress() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/organisation/create/selected-organisation/" + COMPANY_ID)
                .param("addressForm.postcodeInput", POSTCODE_LOOKUP)
                .param("search-address", "")
                .param("searchOrganisationId", COMPANY_ID)
                .cookie(organisationTypeBusiness)
                .cookie(organisationForm)
                .header("referer", "/organisation/create/selected-organisation/")
        )
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.view().name(String.format("redirect:/organisation/create/selected-organisation/%s/search-postcode?searchTerm=%s", COMPANY_ID, POSTCODE_LOOKUP_URL_ENCODED)));

        mockMvc.perform(MockMvcRequestBuilders.get(String.format("/organisation/create/selected-organisation/%s/search-postcode?searchTerm=%s", COMPANY_ID, POSTCODE_LOOKUP))
                .cookie(organisationTypeBusiness)
                .cookie(organisationForm)
        )
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andExpect(MockMvcResultMatchers.view().name("registration/organisation/confirm-selected-organisation"))
                .andExpect(MockMvcResultMatchers.model().attributeHasNoErrors("organisationForm"));


    }
    @Test
    public void testSelectedOrganisationSearchedPostcode() throws Exception {
        when(addressRestService.doLookup(anyString())).thenReturn(RestResult.restSuccess(new ArrayList<>()));
        mockMvc.perform(MockMvcRequestBuilders.get(String.format("/organisation/create/selected-organisation/%s/%s", COMPANY_ID, POSTCODE_LOOKUP))
                .cookie(organisationTypeBusiness)
                .cookie(organisationFormWithPostcodeInput)
        )
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andExpect(MockMvcResultMatchers.view().name("registration/organisation/confirm-selected-organisation"))
                .andExpect(MockMvcResultMatchers.model().attributeHasNoErrors("organisationForm"));
    }

    @Test
    public void testSelectedOrganisationSelectedPostcode() throws Exception {
        ArrayList<AddressResource> addresses = new ArrayList<>();
        addresses.add(new AddressResource());
        addresses.add(new AddressResource());
        addresses.add(new AddressResource());
        when(addressRestService.doLookup(anyString())).thenReturn(RestResult.restSuccess(addresses));
        mockMvc.perform(MockMvcRequestBuilders.get(String.format("/organisation/create/selected-organisation/%s/0", COMPANY_ID))
                .cookie(organisationTypeBusiness)
                .cookie(organisationFormWithSelectedPostcode)
        )
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andExpect(MockMvcResultMatchers.view().name("registration/organisation/confirm-selected-organisation"))
                .andExpect(MockMvcResultMatchers.model().attributeHasNoErrors("organisationForm"));
    }

    @Test
    public void testSelectedBusinessSubmitSelectAddress() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/organisation/create/selected-organisation/" + COMPANY_ID)
                .param("addressForm.postcodeInput", POSTCODE_LOOKUP)
                .param("searchOrganisationId", COMPANY_ID)
                .cookie(organisationTypeBusiness)
                .cookie(organisationForm)
                .param("addressForm.selectedPostcodeIndex", "0")
                .param("select-address", "")
                .header("referer", "/organisation/create/selected-organisation/")
        )
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.view().name(String.format("redirect:/organisation/create/selected-organisation/%s/%s", COMPANY_ID, "0")));

        mockMvc.perform(MockMvcRequestBuilders.get(String.format("/organisation/create/selected-organisation/%s/%s", COMPANY_ID, "0"))
                .cookie(organisationTypeBusiness)
                .cookie(organisationForm)

        )
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andExpect(MockMvcResultMatchers.view().name("registration/organisation/confirm-selected-organisation"))
                .andExpect(MockMvcResultMatchers.model().attributeHasNoErrors("organisationForm"));
    }

    @Test
    public void testSelectedBusinessManualAddress() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/organisation/create/selected-organisation/" + COMPANY_ID)
                .param("searchOrganisationId", COMPANY_ID)
                .cookie(organisationTypeBusiness)
                .cookie(organisationForm)
                .param("manual-address", "true")
        )
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.view().name("redirect:/organisation/create/selected-organisation/"+COMPANY_ID));
    }

    @Test
    public void testSelectedBusinessSaveBusiness() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/organisation/create/selected-organisation/" + COMPANY_ID)
                .param("useSearchResultAddress", "true")
                .param("_useSearchResultAddress", "on")
                .param("save-organisation-details", "true")
                .param("searchOrganisationId", COMPANY_ID)
                .header("referer", "/organisation/create/selected-organisation/")
                .cookie(organisationTypeBusiness)
                .cookie(organisationForm)
        )
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.view().name("redirect:/organisation/create/confirm-organisation"));
    }

    /**
     * Check if request is redirected back to the form, when submit is invalid.
     */
    @Test
    public void testSelectedBusinessInvalidSubmit() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/organisation/create/selected-organisation/" + COMPANY_ID)
                .param("manualEntry", "true")
                .param("addressForm.postcodeInput", "")
                .param("search-address", "")
                .cookie(organisationTypeBusiness)
                .param("searchOrganisationId", COMPANY_ID)
                .cookie(organisationForm)
                .header("referer", "/organisation/create/selected-organisation/")

        )
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.view().name(String.format("redirect:/organisation/create/selected-organisation/%s", COMPANY_ID)));
    }

    @Test
    public void testConfirmCompany() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.get("/organisation/create/confirm-organisation")
                .cookie(organisationTypeBusiness)
                .cookie(organisationForm)
        )
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andExpect(MockMvcResultMatchers.model().attributeExists("selectedOrganisation"))
                .andExpect(MockMvcResultMatchers.model().attribute("selectedOrganisation", Matchers.hasProperty("name", Matchers.equalTo(COMPANY_NAME))))
                .andExpect(MockMvcResultMatchers.view().name("registration/organisation/confirm-organisation"));
    }
}
