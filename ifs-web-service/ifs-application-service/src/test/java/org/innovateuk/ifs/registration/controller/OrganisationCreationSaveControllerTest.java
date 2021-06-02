package org.innovateuk.ifs.registration.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.address.form.AddressForm;
import org.innovateuk.ifs.address.resource.AddressResource;
import org.innovateuk.ifs.organisation.controller.OrganisationCreationSaveController;
import org.innovateuk.ifs.organisation.resource.OrganisationExecutiveOfficerResource;
import org.innovateuk.ifs.organisation.resource.OrganisationSearchResult;
import org.innovateuk.ifs.organisation.resource.OrganisationSicCodeResource;
import org.innovateuk.ifs.organisation.resource.OrganisationTypeResource;
import org.innovateuk.ifs.registration.form.OrganisationCreationForm;
import org.innovateuk.ifs.registration.form.OrganisationTypeForm;
import org.innovateuk.ifs.registration.service.OrganisationJourneyEnd;
import org.innovateuk.ifs.registration.service.RegistrationCookieService;
import org.innovateuk.ifs.user.service.OrganisationRestService;
import org.innovateuk.ifs.user.service.OrganisationSearchRestService;
import org.innovateuk.ifs.user.service.OrganisationTypeRestService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.validation.Validator;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasProperty;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.organisation.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class OrganisationCreationSaveControllerTest extends BaseControllerMockMVCTest<OrganisationCreationSaveController> {

    private static final String COMPANY_NAME = "organisation name";
    private static final String COMPANY_ID = "1";
    private static final String INVITE_HASH = "123abc";
    private static final String VIEW = "some-view";
    private List<OrganisationSicCodeResource> SIC_CODES = getOrgSicCodeResource();
    private List<OrganisationExecutiveOfficerResource> DIRECTORS = getOrgDirectorsResource();
    private LocalDate DATE_OF_INCORPORATION =  LocalDate.parse("2015-05-18", DateTimeFormatter.ofPattern("yyyy-MM-dd"));

    protected OrganisationCreationSaveController supplyControllerUnderTest() {
        return new OrganisationCreationSaveController();
    }

    @Mock
    private OrganisationJourneyEnd organisationJourneyEnd;

    @Mock
    protected RegistrationCookieService registrationCookieService;

    @Mock
    protected OrganisationTypeRestService organisationTypeRestService;

    @Mock
    protected OrganisationSearchRestService organisationSearchRestService;

    @Mock
    private OrganisationRestService organisationRestService;

    @Mock
    protected Validator validator;

    private OrganisationTypeForm organisationTypeForm;
    private OrganisationCreationForm organisationForm;
    private AddressForm addressFormManual;
    private AddressForm addressFormPostCode;

    @Before
    public void setupForms() {

        setLoggedInUser(null);

        OrganisationSearchResult organisationSearchResult = new OrganisationSearchResult(COMPANY_ID, COMPANY_NAME);
        organisationSearchResult.setOrganisationExecutiveOfficers(DIRECTORS);
        organisationSearchResult.setOrganisationSicCodes(SIC_CODES);
        when(organisationSearchRestService.getOrganisation(anyLong(), anyString())).thenReturn(restSuccess(organisationSearchResult));
        when(organisationTypeRestService.findOne(anyLong())).thenReturn(restSuccess(new OrganisationTypeResource()));

        organisationTypeForm = new OrganisationTypeForm();
        organisationTypeForm.setOrganisationType(1L);

        organisationForm = new OrganisationCreationForm();
        organisationForm.setTriedToSave(true);
        organisationForm.setOrganisationSearchName(null);
        organisationForm.setOrganisationTypeId(1L);
        organisationForm.setSearchOrganisationId(COMPANY_ID);
        organisationForm.setOrganisationSearching(false);
        organisationForm.setManualEntry(false);
        organisationForm.setOrganisationSearchResults(Collections.emptyList());
        organisationForm.setOrganisationName("NOMENSA LTD");
        organisationForm.setDateOfIncorporation(DATE_OF_INCORPORATION);
        organisationForm.setSicCodes(SIC_CODES);
        organisationForm.setExecutiveOfficers(DIRECTORS);

        AddressResource addressResource = new AddressResource();
        addressResource.setAddressLine1("l1");
        addressResource.setAddressLine2("l2");
        addressResource.setAddressLine3("l3");
        addressResource.setCountry("Antigua");
        addressResource.setCounty("Hampshire");
        addressResource.setPostcode("SW113QT");
        addressResource.setTown("London");

        addressFormManual = new AddressForm();
        addressFormManual.setAddressType(AddressForm.AddressType.MANUAL_ENTRY);
        addressFormManual.setManualAddress(addressResource);

        List<AddressResource> postCodeResults = new ArrayList<AddressResource>();
        postCodeResults.add(addressResource);

        addressFormPostCode = new AddressForm();
        addressFormPostCode.setAddressType(AddressForm.AddressType.POSTCODE_LOOKUP);
        addressFormPostCode.setSelectedPostcodeIndex(0);
        addressFormPostCode.setPostcodeResults(postCodeResults);
    }

    @Test
    public void saveOrganisation_lead() throws Exception {
        ReflectionTestUtils.setField(controller, "isNewOrganisationSearchEnabled", false);
        when(registrationCookieService.isCollaboratorJourney(any())).thenReturn(false);
        when(registrationCookieService.getOrganisationTypeCookieValue(any())).thenReturn(Optional.of(organisationTypeForm));
        when(registrationCookieService.getOrganisationCreationCookieValue(any())).thenReturn(Optional.of(organisationForm));
        when(organisationRestService.createOrMatch(any())).thenReturn(restSuccess(newOrganisationResource().withId(2L).build()));
        when(organisationJourneyEnd.completeProcess(any(), any(), any(), eq(2L))).thenReturn(VIEW);

        mockMvc.perform(post("/organisation/create/save-organisation")
                .param("searchOrganisationId", "123").param("organisationTypeId", "123"))
                .andExpect(status().isOk())
                .andExpect(view().name(VIEW));

        verify(organisationRestService).createOrMatch(any());
        verify(organisationJourneyEnd).completeProcess(any(), any(), any(), eq(2L));
    }

    @Test
    public void saveOrganisation_WithSicCodes() throws Exception {
        ReflectionTestUtils.setField(controller, "isNewOrganisationSearchEnabled", true);
        when(registrationCookieService.isCollaboratorJourney(any())).thenReturn(false);
        when(registrationCookieService.getOrganisationTypeCookieValue(any())).thenReturn(Optional.of(organisationTypeForm));
        when(registrationCookieService.getOrganisationCreationCookieValue(any())).thenReturn(Optional.of(organisationForm));
        when(organisationRestService.createOrMatch(any())).thenReturn(restSuccess(newOrganisationResource().withId(2L).build()));
        when(organisationJourneyEnd.completeProcess(any(), any(), any(), eq(2L))).thenReturn(VIEW);

        mockMvc.perform(post("/organisation/create/save-organisation")
                .param("searchOrganisationId", "123").param("organisationTypeId", "123"))
                .andExpect(status().isOk())
                .andExpect(view().name(VIEW));

        verify(organisationRestService).createOrMatch(any());
        verify(organisationJourneyEnd).completeProcess(any(), any(), any(), eq(2L));
    }

    @Test
    public void saveOrganisation_manual_entry() throws Exception {

        organisationForm.setAddressForm(addressFormManual);
        organisationForm.setOrganisationAddress(null);

        ReflectionTestUtils.setField(controller, "isNewOrganisationSearchEnabled", true);
        when(registrationCookieService.isCollaboratorJourney(any())).thenReturn(false);
        when(registrationCookieService.getOrganisationTypeCookieValue(any())).thenReturn(Optional.of(organisationTypeForm));
        when(registrationCookieService.getOrganisationCreationCookieValue(any())).thenReturn(Optional.of(organisationForm));
        when(organisationRestService.createOrMatch(any())).thenReturn(restSuccess(newOrganisationResource().withId(2L).build()));
        when(organisationJourneyEnd.completeProcess(any(), any(), any(), eq(2L))).thenReturn(VIEW);

        mockMvc.perform(post("/organisation/create/save-organisation")
                .param("searchOrganisationId", "123").param("organisationTypeId", "123"))
                .andExpect(status().isOk())
                .andExpect(view().name(VIEW));

        verify(organisationRestService).createOrMatch(any());
        verify(organisationJourneyEnd).completeProcess(any(), any(), any(), eq(2L));
    }

    @Test
    public void saveOrganisation_postcode_entry() throws Exception {

        organisationForm.setAddressForm(addressFormPostCode);
        organisationForm.setOrganisationAddress(null);

        ReflectionTestUtils.setField(controller, "isNewOrganisationSearchEnabled", true);
        when(registrationCookieService.isCollaboratorJourney(any())).thenReturn(false);
        when(registrationCookieService.getOrganisationTypeCookieValue(any())).thenReturn(Optional.of(organisationTypeForm));
        when(registrationCookieService.getOrganisationCreationCookieValue(any())).thenReturn(Optional.of(organisationForm));
        when(organisationRestService.createOrMatch(any())).thenReturn(restSuccess(newOrganisationResource().withId(2L).build()));
        when(organisationJourneyEnd.completeProcess(any(), any(), any(), eq(2L))).thenReturn(VIEW);

        mockMvc.perform(post("/organisation/create/save-organisation")
                .param("searchOrganisationId", "123").param("organisationTypeId", "123"))
                .andExpect(status().isOk())
                .andExpect(view().name(VIEW));

        verify(organisationRestService).createOrMatch(any());
        verify(organisationJourneyEnd).completeProcess(any(), any(), any(), eq(2L));
    }




    @Test
    public void updateOrganisation_WithAdditionalDetails() throws Exception {
        Long existingOrganisationId = 1L;

        ReflectionTestUtils.setField(controller, "isNewOrganisationSearchEnabled", true);

        organisationForm.setSelectedExistingOrganisationId(existingOrganisationId);


        when(registrationCookieService.isCollaboratorJourney(any())).thenReturn(false);
        when(registrationCookieService.getOrganisationTypeCookieValue(any())).thenReturn(Optional.of(organisationTypeForm));
        when(registrationCookieService.getOrganisationCreationCookieValue(any())).thenReturn(Optional.of(organisationForm));
        when(organisationRestService.createOrMatch(any())).thenReturn(restSuccess(newOrganisationResource().withId(existingOrganisationId).build()));
        when(organisationJourneyEnd.completeProcess(any(), any(), any(), eq(existingOrganisationId))).thenReturn(VIEW);
        when(organisationRestService.getOrganisationById(existingOrganisationId)).thenReturn(restSuccess(newOrganisationResource().withId(existingOrganisationId).withOrganisationType(1L).build()));

        mockMvc.perform(post("/organisation/create/save-organisation")
                .param("searchOrganisationId", "123").param("organisationTypeId", "123"))
                .andExpect(status().isOk())
                .andExpect(view().name(VIEW));

        verify(organisationRestService).createOrMatch(any());
        verify(organisationJourneyEnd).completeProcess(any(), any(), any(), eq(1L));
    }

    @Test
    public void saveOrganisation_collaborator() throws Exception {
        ReflectionTestUtils.setField(controller, "isNewOrganisationSearchEnabled", false);
        when(registrationCookieService.isCollaboratorJourney(any())).thenReturn(true);
        when(registrationCookieService.getInviteHashCookieValue(any())).thenReturn(Optional.of(INVITE_HASH));
        when(registrationCookieService.getOrganisationTypeCookieValue(any())).thenReturn(Optional.of(organisationTypeForm));
        when(registrationCookieService.getOrganisationCreationCookieValue(any())).thenReturn(Optional.of(organisationForm));
        when(organisationRestService.createOrMatch(any())).thenReturn(restSuccess(newOrganisationResource().withId(2L).build()));
        when(organisationJourneyEnd.completeProcess(any(), any(), any(), eq(2L))).thenReturn(VIEW);

        mockMvc.perform(post("/organisation/create/save-organisation")
                .param("searchOrganisationId", "123").param("organisationTypeId", "123"))
                .andExpect(status().isOk())
                .andExpect(view().name(VIEW));

        verify(organisationRestService).createOrMatch(any());
        verify(organisationJourneyEnd).completeProcess(any(), any(), any(), eq(2L));
    }

    @Test
    public void confirmBusiness() throws Exception {
        ReflectionTestUtils.setField(controller, "isNewOrganisationSearchEnabled", false);
        when(registrationCookieService.getOrganisationTypeCookieValue(any())).thenReturn(Optional.of(organisationTypeForm));
        when(registrationCookieService.getOrganisationCreationCookieValue(any())).thenReturn(Optional.of(organisationForm));

        mockMvc.perform(get("/organisation/create/confirm-organisation"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("registration/organisation/confirm-organisation"))
                .andExpect(model().attributeExists("organisationForm"));
    }

    @Test
    public void confirmCompany() throws Exception {
        ReflectionTestUtils.setField(controller, "isNewOrganisationSearchEnabled", false);
        when(registrationCookieService.getOrganisationTypeCookieValue(any())).thenReturn(Optional.of(organisationTypeForm));
        when(registrationCookieService.getOrganisationCreationCookieValue(any())).thenReturn(Optional.of(organisationForm));

        mockMvc.perform(get("/organisation/create/confirm-organisation"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(model().attributeExists("selectedOrganisation"))
                .andExpect(model().attribute("selectedOrganisation", hasProperty("name", equalTo(COMPANY_NAME))))
                .andExpect(view().name("registration/organisation/confirm-organisation"));
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
}