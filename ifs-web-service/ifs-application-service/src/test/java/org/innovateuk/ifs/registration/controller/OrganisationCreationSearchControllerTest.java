package org.innovateuk.ifs.registration.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.address.service.AddressRestService;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.ApplicationRestService;
import org.innovateuk.ifs.organisation.resource.OrganisationSearchResult;
import org.innovateuk.ifs.organisation.resource.OrganisationTypeResource;
import org.innovateuk.ifs.organisation.service.CompaniesHouseRestService;
import org.innovateuk.ifs.registration.form.OrganisationCreationForm;
import org.innovateuk.ifs.registration.form.OrganisationTypeForm;
import org.innovateuk.ifs.registration.populator.OrganisationCreationSelectTypePopulator;
import org.innovateuk.ifs.registration.service.RegistrationCookieService;
import org.innovateuk.ifs.user.service.OrganisationSearchRestService;
import org.innovateuk.ifs.user.service.OrganisationTypeRestService;
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

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasProperty;
import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.id;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.name;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.organisation.builder.OrganisationTypeResourceBuilder.newOrganisationTypeResource;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(MockitoJUnitRunner.class)
@TestPropertySource(locations = "classpath:application.properties")
public class OrganisationCreationSearchControllerTest extends BaseControllerMockMVCTest<OrganisationCreationSearchController> {
    private ApplicationResource applicationResource;

    @Mock
    private OrganisationSearchRestService organisationSearchRestService;

    @Mock
    private AddressRestService addressRestService;

    @Mock
    private RegistrationCookieService registrationCookieService;

    @Mock
    private CompaniesHouseRestService companiesHouseRestService;

    @Mock
    private ApplicationRestService applicationRestService;

    @Mock
    private OrganisationTypeRestService organisationTypeRestService;

    @Spy
    @InjectMocks
    private OrganisationCreationSelectTypePopulator organisationCreationSelectTypePopulator;

    private String COMPANY_ID = "08241216";
    private String COMPANY_NAME = "NETWORTHNET LTD";

    private OrganisationCreationForm organisationForm;
    private OrganisationTypeForm organisationTypeForm;
    private OrganisationCreationForm organisationFormUseSearchResult;

    OrganisationTypeResource businessOrganisationTypeResource = newOrganisationTypeResource().with(id(1L)).with(name("Business")).build();

    @Override
    protected OrganisationCreationSearchController supplyControllerUnderTest() {
        return new OrganisationCreationSearchController();
    }

    @Before
    public void setUpForms() {

        applicationResource = newApplicationResource().withId(6L).withName("some application").build();
        OrganisationSearchResult organisationSearchResult = new OrganisationSearchResult(COMPANY_ID, COMPANY_NAME);
        when(companiesHouseRestService.getOrganisationById(COMPANY_ID)).thenReturn(restSuccess(organisationSearchResult));
        when(applicationRestService.createApplication(anyLong(), anyLong(), anyLong(), anyString())).thenReturn(restSuccess(applicationResource));
        when(organisationSearchRestService.getOrganisation(businessOrganisationTypeResource.getId(), COMPANY_ID)).thenReturn(restSuccess(organisationSearchResult));
        when(organisationSearchRestService.searchOrganisation(anyLong(), anyString())).thenReturn(restSuccess(new ArrayList<>()));
        when(addressRestService.validatePostcode("CH64 3RU")).thenReturn(restSuccess(true));
        when(organisationTypeRestService.findOne(anyLong())).thenReturn(restSuccess(new OrganisationTypeResource()));

        organisationTypeForm = new OrganisationTypeForm();
        organisationTypeForm.setOrganisationType(1L);

        organisationForm = new OrganisationCreationForm();
        organisationForm.setTriedToSave(true);
        organisationForm.setOrganisationSearchName("company name");
        organisationForm.setOrganisationTypeId(1L);
        organisationForm.setSearchOrganisationId(COMPANY_ID);
        organisationForm.setOrganisationSearching(false);
        organisationForm.setManualEntry(false);
        organisationForm.setOrganisationSearchResults(Collections.emptyList());
        organisationForm.setOrganisationName("NOMENSA LTD");


        organisationFormUseSearchResult = new OrganisationCreationForm();
        organisationFormUseSearchResult.setOrganisationSearchName("searchname");
        organisationFormUseSearchResult.setOrganisationName("actualname");

        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();
        ReflectionTestUtils.setField(controller, "validator", validator);
    }

    @Test
    public void testManualOrganisationEntry() throws Exception {
        OrganisationCreationForm organisationFormCookieValue = new OrganisationCreationForm();
        organisationFormCookieValue.setManualEntry(true);

        when(registrationCookieService.getOrganisationTypeCookieValue(any())).thenReturn(Optional.of(organisationTypeForm));
        when(registrationCookieService.getOrganisationCreationCookieValue(any())).thenReturn(Optional.of(organisationFormCookieValue));

        Cookie[] cookies = mockMvc.perform(post("/organisation/create/find-organisation")
                .param("organisationSearchName", "BusinessName")
                .param("not-in-companies-house", ""))
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
    public void testSearchOrganisation_findBusinessSearchCompaniesHouse() throws Exception {
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
    public void testFindBusinessSearchCompaniesHouseInvalid() throws Exception {
        when(registrationCookieService.getOrganisationTypeCookieValue(any())).thenReturn(Optional.of(organisationTypeForm));

        mockMvc.perform(post("/organisation/create/find-organisation")
                .param("organisationSearchName", "")
                .param("search-organisation", ""))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/organisation/create/find-organisation?searchTerm="));
    }

    @Test
    public void testSaveOrganisationtest_findBusinessConfirmCompaniesDetailsInvalid() throws Exception {
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
    public void testSaveOrganisation_successfulSaveShouldRedirectToConfirmCompaniesDetailsPage() throws Exception {
        when(registrationCookieService.getOrganisationTypeCookieValue(any())).thenReturn(Optional.of(organisationTypeForm));
        when(registrationCookieService.getOrganisationCreationCookieValue(any())).thenReturn(Optional.of(organisationForm));

        mockMvc.perform(post("/organisation/create/find-organisation")
                .param("organisationName", "BusinessName")
                .param("manualEntry", "true")
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
    public void testSelectedBusinessSaveBusiness() throws Exception {

        when(registrationCookieService.getOrganisationTypeCookieValue(any())).thenReturn(Optional.of(organisationTypeForm));
        when(registrationCookieService.getOrganisationCreationCookieValue(any())).thenReturn(Optional.of(organisationForm));

        mockMvc.perform(post("/organisation/create/selected-organisation/" + COMPANY_ID)
                .param("save-organisation-details", "true")
                .param("searchOrganisationId", COMPANY_ID)
                .header("referer", "/organisation/create/selected-organisation/"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/organisation/create/confirm-organisation"));
    }
}