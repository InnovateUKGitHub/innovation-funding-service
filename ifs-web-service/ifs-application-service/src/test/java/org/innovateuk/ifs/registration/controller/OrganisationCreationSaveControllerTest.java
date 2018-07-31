package org.innovateuk.ifs.registration.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.application.service.OrganisationService;
import org.innovateuk.ifs.form.AddressForm;
import org.innovateuk.ifs.organisation.resource.OrganisationSearchResult;
import org.innovateuk.ifs.organisation.resource.OrganisationTypeResource;
import org.innovateuk.ifs.registration.form.OrganisationCreationForm;
import org.innovateuk.ifs.registration.form.OrganisationTypeForm;
import org.innovateuk.ifs.registration.service.OrganisationJourneyEnd;
import org.innovateuk.ifs.registration.service.RegistrationCookieService;
import org.innovateuk.ifs.user.service.OrganisationSearchRestService;
import org.innovateuk.ifs.user.service.OrganisationTypeRestService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.validation.Validator;

import java.util.Collections;
import java.util.Optional;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasProperty;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.organisation.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.mockito.Matchers.*;
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

    protected OrganisationCreationSaveController supplyControllerUnderTest() {
        return new OrganisationCreationSaveController();
    }

    @Mock
    private OrganisationService organisationService;

    @Mock
    private OrganisationJourneyEnd organisationJourneyEnd;

    @Mock
    protected RegistrationCookieService registrationCookieService;

    @Mock
    protected OrganisationTypeRestService organisationTypeRestService;

    @Mock
    protected OrganisationSearchRestService organisationSearchRestService;

    @Mock
    protected Validator validator;

    private OrganisationTypeForm organisationTypeForm;
    private OrganisationCreationForm organisationForm;

    @Before
    public void setup(){
        super.setUp();
        setLoggedInUser(null);

        OrganisationSearchResult organisationSearchResult = new OrganisationSearchResult(COMPANY_ID, COMPANY_NAME);

        when(organisationSearchRestService.getOrganisation(anyLong(), anyString())).thenReturn(restSuccess(organisationSearchResult));
        when(organisationTypeRestService.findOne(anyLong())).thenReturn(restSuccess(new OrganisationTypeResource()));

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
        organisationForm.setOrganisationSearchResults(Collections.emptyList());
        organisationForm.setOrganisationName("NOMENSA LTD");
    }

    @Test
    public void saveOrganisation_lead() throws Exception {
        when(registrationCookieService.isCollaboratorJourney(any())).thenReturn(false);
        when(registrationCookieService.getOrganisationTypeCookieValue(any())).thenReturn(Optional.of(organisationTypeForm));
        when(registrationCookieService.getOrganisationCreationCookieValue(any())).thenReturn(Optional.of(organisationForm));
        when(organisationService.createOrMatch(any())).thenReturn(newOrganisationResource().withId(2L).build());
        when(organisationJourneyEnd.completeProcess(any(), any(), any(), eq(2L))).thenReturn(VIEW);

        mockMvc.perform(post("/organisation/create/save-organisation")
                .param("searchOrganisationId", "123"))
                .andExpect(status().isOk())
                .andExpect(view().name(VIEW));

        verify(organisationService).createOrMatch(any());
        verify(organisationJourneyEnd).completeProcess(any(), any(), any(), eq(2L));
    }

    @Test
    public void saveOrganisation_collaborator() throws Exception {
        when(registrationCookieService.isCollaboratorJourney(any())).thenReturn(true);
        when(registrationCookieService.getInviteHashCookieValue(any())).thenReturn(Optional.of(INVITE_HASH));
        when(registrationCookieService.getOrganisationTypeCookieValue(any())).thenReturn(Optional.of(organisationTypeForm));
        when(registrationCookieService.getOrganisationCreationCookieValue(any())).thenReturn(Optional.of(organisationForm));
        when(organisationService.createAndLinkByInvite(any(), eq(INVITE_HASH))).thenReturn(newOrganisationResource().withId(2L).build());
        when(organisationJourneyEnd.completeProcess(any(), any(), any(), eq(2L))).thenReturn(VIEW);

        mockMvc.perform(post("/organisation/create/save-organisation")
                .param("searchOrganisationId", "123"))
                .andExpect(status().isOk())
                .andExpect(view().name(VIEW));

        verify(organisationService).createAndLinkByInvite(any(), eq(INVITE_HASH));
        verify(organisationJourneyEnd).completeProcess(any(), any(), any(), eq(2L));
    }

    @Test
    public void confirmBusiness() throws Exception {
        when(registrationCookieService.getOrganisationTypeCookieValue(any())).thenReturn(Optional.of(organisationTypeForm));
        when(registrationCookieService.getOrganisationCreationCookieValue(any())).thenReturn(Optional.of(organisationForm));

        mockMvc.perform(get("/organisation/create/confirm-organisation"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("registration/organisation/confirm-organisation"))
                .andExpect(model().attributeExists("organisationForm"));
    }

    @Test
    public void confirmCompany() throws Exception {
        when(registrationCookieService.getOrganisationTypeCookieValue(any())).thenReturn(Optional.of(organisationTypeForm));
        when(registrationCookieService.getOrganisationCreationCookieValue(any())).thenReturn(Optional.of(organisationForm));

        mockMvc.perform(get("/organisation/create/confirm-organisation"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(model().attributeExists("selectedOrganisation"))
                .andExpect(model().attribute("selectedOrganisation", hasProperty("name", equalTo(COMPANY_NAME))))
                .andExpect(view().name("registration/organisation/confirm-organisation"));
    }
}