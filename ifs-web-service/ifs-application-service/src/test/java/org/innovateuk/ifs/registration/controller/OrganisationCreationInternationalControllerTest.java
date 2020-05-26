package org.innovateuk.ifs.registration.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.organisation.controller.OrganisationCreationInternationalController;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.organisation.resource.OrganisationTypeResource;
import org.innovateuk.ifs.registration.form.OrganisationInternationalDetailsForm;
import org.innovateuk.ifs.registration.form.OrganisationInternationalForm;
import org.innovateuk.ifs.registration.form.OrganisationTypeForm;
import org.innovateuk.ifs.registration.service.OrganisationJourneyEnd;
import org.innovateuk.ifs.registration.service.RegistrationCookieService;
import org.innovateuk.ifs.user.service.OrganisationRestService;
import org.innovateuk.ifs.user.service.OrganisationTypeRestService;
import org.junit.Test;
import org.mockito.Mock;

import java.util.Optional;

import static java.util.Collections.emptyList;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class OrganisationCreationInternationalControllerTest extends BaseControllerMockMVCTest<OrganisationCreationInternationalController> {

    @Mock
    private RegistrationCookieService registrationCookieService;
    @Mock
    private OrganisationTypeRestService organisationTypeRestService;
    @Mock
    private OrganisationRestService organisationRestService;
    @Mock
    private OrganisationJourneyEnd organisationJourneyEnd;

    protected OrganisationCreationInternationalController supplyControllerUnderTest() {
        return new OrganisationCreationInternationalController();
    }

    @Test
    public void selectInternationalOrganisation() throws Exception {
        mockMvc.perform(
                    get("/organisation/create/international-organisation")
                )
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("registration/organisation/international-organisation"));
    }

    @Test
    public void confirmInternationalOrganisation() throws Exception {
        OrganisationInternationalForm organisationInternationalForm = new OrganisationInternationalForm();
        organisationInternationalForm.setInternational(true);

        when(registrationCookieService.getCompetitionIdCookieValue(any())).thenReturn(Optional.of(1L));
        when(organisationRestService.getAllByUserId(anyLong())).thenReturn(RestResult.restSuccess(emptyList()));

        mockMvc.perform(
                post("/organisation/create/international-organisation")
                        .flashAttr("organisationForm", organisationInternationalForm)

        )
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/organisation/select"));
    }

    @Test
    public void getInternationalOrganisationDetails() throws Exception {
        when(registrationCookieService.getCompetitionIdCookieValue(any())).thenReturn(Optional.of(1L));

        mockMvc.perform(
                get("/organisation/create/international-organisation/details")
        )
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("registration/organisation/international-organisation-details"))
                .andExpect(model().attributeExists("countries"));
    }

    @Test
    public void saveInternationalOrganisationDetails() throws Exception {
        OrganisationInternationalDetailsForm organisationInternationalDetailsForm = new OrganisationInternationalDetailsForm();
        organisationInternationalDetailsForm.setName("ABC123");
        organisationInternationalDetailsForm.setAddressLine1("line 1");
        organisationInternationalDetailsForm.setTown("Bristol");
        organisationInternationalDetailsForm.setCountry("France");

        when(registrationCookieService.getCompetitionIdCookieValue(any())).thenReturn(Optional.of(1L));
        when(organisationRestService.getAllByUserId(anyLong())).thenReturn(RestResult.restSuccess(emptyList()));

        mockMvc.perform(
                post("/organisation/create/international-organisation/details")
                        .flashAttr("organisationForm", organisationInternationalDetailsForm)

        )
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/organisation/create/international-organisation/confirm"));
    }

    @Test
    public void confirmInternationalOrganisationDetails() throws Exception {
        OrganisationTypeForm organisationTypeForm = new OrganisationTypeForm();
        organisationTypeForm.setOrganisationType(1L);
        organisationTypeForm.setLeadApplicant(true);

        OrganisationInternationalDetailsForm organisationInternationalDetailsForm = new OrganisationInternationalDetailsForm();
        organisationInternationalDetailsForm.setName("The Organisation");
        organisationInternationalDetailsForm.setAddressLine1("line 1");
        organisationInternationalDetailsForm.setAddressLine2("line 2");
        organisationInternationalDetailsForm.setTown("Brizzle");
        organisationInternationalDetailsForm.setCountry("France");
        organisationInternationalDetailsForm.setZipCode("BS1 1SB");
        organisationInternationalDetailsForm.setCompanyRegistrationNumber("ABC123");

        OrganisationTypeResource organisationTypeResource = new OrganisationTypeResource();
        organisationTypeResource.setId(1L);
        organisationTypeResource.setDescription("great description");
        organisationTypeResource.setName("Greatest resource");

        when(registrationCookieService.getCompetitionIdCookieValue(any())).thenReturn(Optional.of(1L));
        when(registrationCookieService.getOrganisationTypeCookieValue(any())).thenReturn(Optional.of(organisationTypeForm));
        when(registrationCookieService.getOrganisationInternationalDetailsValue(any())).thenReturn(Optional.of(organisationInternationalDetailsForm));
        when(organisationTypeRestService.findOne(1L)).thenReturn(RestResult.restSuccess(organisationTypeResource));


        mockMvc.perform(
                get("/organisation/create/international-organisation/confirm")
        )
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("registration/organisation/international-confirm-organisation"))
                .andExpect(model().attribute("competitionId", 1L))
                .andExpect(model().attributeExists("organisationType", "organisationName", "registrationNumber", "address"));
    }

    @Test
    public void saveOrganisation() throws Exception {
        OrganisationTypeForm organisationTypeForm = new OrganisationTypeForm();
        organisationTypeForm.setOrganisationType(1L);
        organisationTypeForm.setLeadApplicant(true);

        OrganisationInternationalDetailsForm organisationInternationalDetailsForm = new OrganisationInternationalDetailsForm();
        organisationInternationalDetailsForm.setName("The Organisation");
        organisationInternationalDetailsForm.setAddressLine1("line 1");
        organisationInternationalDetailsForm.setAddressLine2("line 2");
        organisationInternationalDetailsForm.setTown("Brizzle");
        organisationInternationalDetailsForm.setCountry("France");
        organisationInternationalDetailsForm.setZipCode("BS1 1SB");
        organisationInternationalDetailsForm.setCompanyRegistrationNumber("ABC123");

        OrganisationResource organisationFromForm = new OrganisationResource();
        organisationFromForm.setName("The Organisation");
        organisationFromForm.setOrganisationType(1L);
        organisationFromForm.setInternational(true);
        organisationFromForm.setInternationalRegistrationNumber("ABC123");
        organisationFromForm.setId(1L);

        when(registrationCookieService.getOrganisationTypeCookieValue(any())).thenReturn(Optional.of(organisationTypeForm));
        when(registrationCookieService.getOrganisationInternationalDetailsValue(any())).thenReturn(Optional.of(organisationInternationalDetailsForm));
        when(organisationRestService.createOrMatch(any())).thenReturn(RestResult.restSuccess(organisationFromForm));
        when(organisationJourneyEnd.completeProcess(any(), any(), any(), anyLong())).thenReturn("redirect:/registration/register");

        mockMvc.perform(
                    post("/organisation/create/international-organisation/save-organisation")
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/registration/register"));
    }
}