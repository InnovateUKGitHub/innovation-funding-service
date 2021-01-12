package org.innovateuk.ifs.registration.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.address.service.AddressRestService;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.ApplicationRestService;
import org.innovateuk.ifs.organisation.controller.OrganisationCreationSearchController;
import org.innovateuk.ifs.organisation.populator.OrganisationCreationSelectTypePopulator;
import org.innovateuk.ifs.organisation.resource.*;
import org.innovateuk.ifs.organisation.service.CompaniesHouseRestService;
import org.innovateuk.ifs.registration.form.OrganisationCreationForm;
import org.innovateuk.ifs.registration.form.OrganisationTypeForm;
import org.innovateuk.ifs.registration.service.RegistrationCookieService;
import org.innovateuk.ifs.user.service.OrganisationRestService;
import org.innovateuk.ifs.user.service.OrganisationSearchRestService;
import org.innovateuk.ifs.user.service.OrganisationTypeRestService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasProperty;
import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.id;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.name;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.organisation.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.innovateuk.ifs.organisation.builder.OrganisationTypeResourceBuilder.newOrganisationTypeResource;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(MockitoJUnitRunner.Silent.class)
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

    @Mock
    private OrganisationRestService organisationRestService;

    @Spy
    @InjectMocks
    private OrganisationCreationSelectTypePopulator organisationCreationSelectTypePopulator;

    private String COMPANY_ID = "08241216";
    private String COMPANY_NAME = "NETWORTHNET LTD";
    private List<OrganisationSicCodeResource> SIC_CODES = getOrgSicCodeResource();
    private List<OrganisationExecutiveOfficerResource> DIRECTORS = getOrgDirectorsResource();
    private LocalDate DATE_OF_INCORPORATION =  LocalDate.parse("2015-05-18", DateTimeFormatter.ofPattern("yyyy-MM-dd"));


    private OrganisationCreationForm organisationForm;
    private OrganisationTypeForm organisationTypeForm;

    OrganisationTypeResource businessOrganisationTypeResource = newOrganisationTypeResource().with(id(1L)).with(name("Business")).build();

    @Override
    protected OrganisationCreationSearchController supplyControllerUnderTest() {
        return new OrganisationCreationSearchController();
    }

    @Before
    public void setUpForms() {

        applicationResource = newApplicationResource().withId(6L).withName("some application").build();

        OrganisationSearchResult organisationSearchResult = new OrganisationSearchResult(COMPANY_ID, COMPANY_NAME);
        organisationSearchResult.setOrganisationExecutiveOfficers(DIRECTORS);
        organisationSearchResult.setOrganisationSicCodes(SIC_CODES);

        when(companiesHouseRestService.getOrganisationById(COMPANY_ID)).thenReturn(restSuccess(organisationSearchResult));
        when(applicationRestService.createApplication(anyLong(), anyLong(), anyLong(), anyString())).thenReturn(restSuccess(applicationResource));
        when(organisationSearchRestService.getOrganisation(businessOrganisationTypeResource.getId(), COMPANY_ID)).thenReturn(restSuccess(organisationSearchResult));
        when(organisationSearchRestService.searchOrganisation(anyLong(), anyString(), anyInt())).thenReturn(restSuccess(new ArrayList<>()));
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
        organisationForm.setDateOfIncorporation(DATE_OF_INCORPORATION);
        organisationForm.setSicCodes(SIC_CODES);
        organisationForm.setExecutiveOfficers(DIRECTORS);

        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();
        ReflectionTestUtils.setField(controller, "validator", validator);

    }

    @Test
    public void searchOrganisation_findBusinessSearchCompaniesHouse() throws Exception {
        ReflectionTestUtils.setField(controller, "isNewOrganisationSearchEnabled", false);
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
    public void findBusinessSearchCompaniesHouseInvalid() throws Exception {
        ReflectionTestUtils.setField(controller, "isNewOrganisationSearchEnabled", false);
        when(registrationCookieService.getOrganisationTypeCookieValue(any())).thenReturn(Optional.of(organisationTypeForm));

        mockMvc.perform(post("/organisation/create/find-organisation")
                .param("organisationSearchName", "")
                .param("search-organisation", ""))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/organisation/create/find-organisation?searchTerm="));
    }

    @Test
    public void saveOrganisationtest_findBusinessConfirmCompaniesDetailsInvalid() throws Exception {
        ReflectionTestUtils.setField(controller, "isNewOrganisationSearchEnabled", false);
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
    public void saveOrganisation_successfulSaveShouldRedirectToConfirmCompaniesDetailsPage() throws Exception {
        ReflectionTestUtils.setField(controller, "isNewOrganisationSearchEnabled", false);
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
    public void createOrganisation() throws Exception {
        ReflectionTestUtils.setField(controller, "isNewOrganisationSearchEnabled", false);
        when(registrationCookieService.getOrganisationTypeCookieValue(any())).thenReturn(Optional.of(organisationTypeForm));
        when(registrationCookieService.getOrganisationCreationCookieValue(any())).thenReturn(Optional.of(organisationForm));

        mockMvc.perform(get("/organisation/create/find-organisation"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("registration/organisation/find-organisation"))
                .andExpect(model().attributeExists("organisationForm"));
    }

    @Test
    public void createOrganisationUsingImprovedSearch() throws Exception {
        ReflectionTestUtils.setField(controller, "isNewOrganisationSearchEnabled", true);
        String improvedSearchLabel = "Enter your organisation name or company registration number and click the 'Search' button.";
        String improvedAdditionalLabel = "We'll look for your organisation's details and tell you what to do next.";

        when(registrationCookieService.getOrganisationTypeCookieValue(any())).thenReturn(Optional.of(organisationTypeForm));
        when(registrationCookieService.getOrganisationCreationCookieValue(any())).thenReturn(Optional.of(organisationForm));

        when(messageSource.getMessage("improved.registration.SearchLabel", null, Locale.ENGLISH)).thenReturn(improvedSearchLabel);
        when(messageSource.getMessage("improved.registration.AdditionalLabel", null, Locale.ENGLISH)).thenReturn(improvedAdditionalLabel);
        when(messageSource.getMessage("improved.registration.SearchHint", null, Locale.ENGLISH)).thenReturn("");

        mockMvc.perform(get("/organisation/create/find-organisation"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("registration/organisation/find-organisation"))
                .andExpect(model().attributeExists("organisationForm"))
                .andExpect(model().attribute("improvedSearchEnabled", equalTo(true)))
                .andExpect(model().attribute("searchLabel", equalTo(improvedSearchLabel)))
                .andExpect(model().attribute("additionalLabel", equalTo(improvedAdditionalLabel)))
                .andExpect(model().attribute("searchHint", equalTo("")))
                .andExpect(model().attribute("subtitle", equalTo("Create new application")));
    }

    @Test
    public void selectedBusinessGet() throws Exception {
        ReflectionTestUtils.setField(controller, "isNewOrganisationSearchEnabled", false);
        when(registrationCookieService.getOrganisationTypeCookieValue(any())).thenReturn(Optional.of(organisationTypeForm));
        when(registrationCookieService.getOrganisationCreationCookieValue(any())).thenReturn(Optional.of(organisationForm));

        mockMvc.perform(get("/organisation/create/selected-organisation/" + COMPANY_ID)
                .header("referer", "/organisation/create/selected-organisation/"))
                .andExpect(status().isOk());
    }

    @Test
    public void selectedBusinessGetwithSiccodeAndDirectors() throws Exception {
        ReflectionTestUtils.setField(controller, "isNewOrganisationSearchEnabled", true);

        when(registrationCookieService.getOrganisationTypeCookieValue(any())).thenReturn(Optional.of(organisationTypeForm));
        when(registrationCookieService.getOrganisationCreationCookieValue(any())).thenReturn(Optional.of(organisationForm));

        mockMvc.perform(get("/organisation/create/selected-organisation/" + COMPANY_ID))
               .andExpect(status().isOk())
               .andExpect(view().name( "registration/organisation/confirm-organisation"))
               .andExpect(model().attribute("organisationForm", hasProperty("organisationTypeId", equalTo(1L))))
                .andExpect(model().attribute("organisationForm", hasProperty("dateOfIncorporation",
                        equalTo(DATE_OF_INCORPORATION))))
               .andExpect(model().attribute("organisationForm", hasProperty("sicCodes", equalTo(SIC_CODES))))
               .andExpect(model().attribute("organisationForm", hasProperty("executiveOfficers", equalTo(DIRECTORS))));
          }

    private List<OrganisationSicCodeResource> getOrgSicCodeResource() {
        SIC_CODES = new ArrayList<>();
        List<String> sicCodes = asList("62020","63990","79909");
            sicCodes.forEach(sicCode -> {
                SIC_CODES.add(new OrganisationSicCodeResource(1L,sicCode));
            });

            return SIC_CODES;
        }

    private List<OrganisationExecutiveOfficerResource> getOrgDirectorsResource() {
        DIRECTORS = new ArrayList<>();
        List<String> directors = asList("BOUSQUET, Christophe","KRAFT ANTELYES, Diana","MAGESH, Champa Hariharan","SANCHEZ QUINONES, Arturo");
        directors.forEach(director -> {
            DIRECTORS.add(new OrganisationExecutiveOfficerResource(1L,director));
        });
        return DIRECTORS;
    }

    @Test
    public void selectedBusinessSaveBusiness() throws Exception {
        ReflectionTestUtils.setField(controller, "isNewOrganisationSearchEnabled", false);
        when(registrationCookieService.getOrganisationTypeCookieValue(any())).thenReturn(Optional.of(organisationTypeForm));
        when(registrationCookieService.getOrganisationCreationCookieValue(any())).thenReturn(Optional.of(organisationForm));

        mockMvc.perform(post("/organisation/create/selected-organisation/" + COMPANY_ID)
                .param("save-organisation-details", "true")
                .param("searchOrganisationId", COMPANY_ID)
                .header("referer", "/organisation/create/selected-organisation/"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/organisation/create/selected-organisation/" + COMPANY_ID));
    }

    @Test
    public void searchExistingOrganisation() throws Exception {
        ReflectionTestUtils.setField(controller, "isNewOrganisationSearchEnabled", true);
        OrganisationResource organisation = newOrganisationResource()
                .withId(1L)
                .withName("test")
                .withOrganisationType(2L)
                .build();
        when(organisationRestService.getOrganisationById(anyLong())).thenReturn(restSuccess(organisation));

        mockMvc.perform(get("/organisation/create/existing-organisation/1"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("registration/organisation/find-organisation"))
                .andExpect(model().attributeExists("organisationForm"))
                .andExpect(model().attribute("organisationForm", hasProperty("selectedExistingOrganisationId", equalTo(1L))))
                .andExpect(model().attribute("organisationForm", hasProperty("selectedExistingOrganisationName", equalTo("test"))))
                .andExpect(model().attribute("organisationForm", hasProperty("organisationTypeId", equalTo(2L))))
                .andExpect(model().attribute("organisationForm", hasProperty("manualEntry", equalTo(false))))
                .andExpect(model().attribute("improvedSearchEnabled", equalTo(true)))
                .andExpect(model().attribute("subtitle", equalTo("Your organisation")));
    }
}