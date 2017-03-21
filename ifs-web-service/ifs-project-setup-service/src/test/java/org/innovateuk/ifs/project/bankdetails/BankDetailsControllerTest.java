package org.innovateuk.ifs.project.bankdetails;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.address.resource.AddressResource;
import org.innovateuk.ifs.address.resource.AddressTypeResource;
import org.innovateuk.ifs.address.resource.OrganisationAddressType;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.organisation.resource.OrganisationAddressResource;
import org.innovateuk.ifs.project.bankdetails.controller.BankDetailsController;
import org.innovateuk.ifs.project.bankdetails.resource.BankDetailsResource;
import org.innovateuk.ifs.project.bankdetails.form.BankDetailsForm;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.user.resource.OrganisationResource;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.validation.BindingResult;

import java.util.Collections;

import static org.innovateuk.ifs.address.builder.AddressResourceBuilder.newAddressResource;
import static org.innovateuk.ifs.address.builder.AddressTypeResourceBuilder.newAddressTypeResource;
import static org.innovateuk.ifs.address.resource.OrganisationAddressType.ADD_NEW;
import static org.innovateuk.ifs.address.resource.OrganisationAddressType.REGISTERED;
import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.BANK_DETAILS_DONT_EXIST_FOR_GIVEN_PROJECT_AND_ORGANISATION;
import static org.innovateuk.ifs.commons.rest.RestResult.restFailure;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.organisation.builder.OrganisationAddressResourceBuilder.newOrganisationAddressResource;
import static org.innovateuk.ifs.project.AddressLookupBaseController.FORM_ATTR_NAME;
import static org.innovateuk.ifs.project.bankdetails.builder.BankDetailsResourceBuilder.newBankDetailsResource;
import static org.innovateuk.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static org.innovateuk.ifs.user.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class BankDetailsControllerTest extends BaseControllerMockMVCTest<BankDetailsController> {

    private final static String SEARCH_ADDRESS = "search-address";

    @Override
    protected BankDetailsController supplyControllerUnderTest() {
        return new BankDetailsController();
    }

    @Test
    public void testEmptyFormWhenNoBankDetailsExist() throws Exception {
        CompetitionResource competitionResource = newCompetitionResource().build();
        ApplicationResource applicationResource = newApplicationResource().withCompetition(competitionResource.getId()).build();
        ProjectResource projectResource = newProjectResource().withApplication(applicationResource).build();
        OrganisationResource organisationResource = newOrganisationResource().build();

        when(projectService.getById(projectResource.getId())).thenReturn(projectResource);
        when(projectService.getOrganisationByProjectAndUser(projectResource.getId(), loggedInUser.getId())).thenReturn(organisationResource);
        when(bankDetailsRestService.getBankDetailsByProjectAndOrganisation(projectResource.getId(), organisationResource.getId())).thenReturn(restFailure(new Error(BANK_DETAILS_DONT_EXIST_FOR_GIVEN_PROJECT_AND_ORGANISATION)));

        BankDetailsForm form = new BankDetailsForm();

        mockMvc.perform(get("/project/{id}/bank-details", projectResource.getId()))
                .andExpect(status().isOk())
                .andExpect(model().attribute("project", projectResource))
                .andExpect(model().attribute("applicationId", projectResource.getApplication()))
                .andExpect(model().attribute("currentUser", loggedInUser))
                .andExpect(model().attribute("organisation", organisationResource))
                .andExpect(model().attributeExists("readOnlyView"))
                .andExpect(model().attribute("readOnlyView", false))
                .andExpect(model().attribute(FORM_ATTR_NAME, form))
                .andExpect(view().name("project/bank-details"));
    }

    @Test
    public void testReadOnlyViewBankDetails() throws Exception {
        CompetitionResource competitionResource = newCompetitionResource().build();
        ApplicationResource applicationResource = newApplicationResource().withCompetition(competitionResource.getId()).build();
        ProjectResource projectResource = newProjectResource().withApplication(applicationResource).build();
        OrganisationResource organisationResource = newOrganisationResource().build();
        OrganisationAddressResource organisationAddressResource = newOrganisationAddressResource()
                .withAddressType(newAddressTypeResource().withName(OrganisationAddressType.BANK_DETAILS.name()).build())
                .build();

        BankDetailsResource bankDetailsResource = newBankDetailsResource().withOrganiationAddress(organisationAddressResource).build();

        when(projectService.getById(projectResource.getId())).thenReturn(projectResource);
        when(projectService.getOrganisationByProjectAndUser(projectResource.getId(), loggedInUser.getId())).thenReturn(organisationResource);
        when(bankDetailsRestService.getBankDetailsByProjectAndOrganisation(projectResource.getId(), organisationResource.getId())).thenReturn(RestResult.restSuccess(bankDetailsResource, HttpStatus.OK));
        when(organisationAddressRestService.findOne(bankDetailsResource.getOrganisationAddress().getId())).thenReturn(RestResult.restSuccess(organisationAddressResource, HttpStatus.OK));

        BankDetailsForm form = new BankDetailsForm();

        mockMvc.perform(get("/project/{id}/bank-details/readonly", projectResource.getId()))
                .andExpect(status().isOk())
                .andExpect(model().attribute("project", projectResource))
                .andExpect(model().attribute("applicationId", projectResource.getApplication()))
                .andExpect(model().attribute("currentUser", loggedInUser))
                .andExpect(model().attribute("organisation", organisationResource))
                .andExpect(model().attribute("readOnlyView", true))
                .andExpect(model().attribute(FORM_ATTR_NAME, form))
                .andExpect(view().name("project/bank-details"));
    }


    @Test
    public void testsubmitBankDetailsWithAddressToBeSameAsRegistered() throws Exception {
        OrganisationResource organisationResource = newOrganisationResource().build();
        AddressResource addressResource = newAddressResource().withOrganisationList(Collections.singletonList(organisationResource.getId())).build();
        AddressTypeResource addressTypeResource = newAddressTypeResource().withId((long)REGISTERED.getOrdinal()).withName(REGISTERED.name()).build();
        OrganisationAddressResource organisationAddressResource = newOrganisationAddressResource().withAddressType(addressTypeResource).withAddress(addressResource).build();
        organisationResource.setAddresses(Collections.singletonList(organisationAddressResource));
        organisationResource.setName("Acme Corporation");
        organisationResource.setOrganisationTypeName("Business");
        organisationResource.setCompanyHouseNumber("00123");
        CompetitionResource competitionResource = newCompetitionResource().build();
        ApplicationResource applicationResource = newApplicationResource().withCompetition(competitionResource.getId()).build();
        ProjectResource projectResource = newProjectResource().withApplication(applicationResource).withAddress(addressResource).build();
        BankDetailsResource bankDetailsResource = newBankDetailsResource().
                withId(). // This is not set to signal it is a new bank detail resource that doesn't exist in DB yet.
                withSortCode("123456").
                withAccountNumber("12345678").
                withOrganiationAddress(organisationAddressResource).
                withOrganisation(organisationResource.getId()).
                withCompanyName("Acme Corporation").
                withOrganisationTypeName("Business").
                withRegistrationNumber("00123").
                withProject(projectResource.getId()).build();

        when(projectService.getById(projectResource.getId())).thenReturn(projectResource);
        when(projectService.getOrganisationByProjectAndUser(projectResource.getId(), loggedInUser.getId())).thenReturn(organisationResource);
        when(bankDetailsRestService.getBankDetailsByProjectAndOrganisation(projectResource.getId(), organisationResource.getId())).thenReturn(restSuccess(bankDetailsResource));
        when(bankDetailsRestService.submitBankDetails(projectResource.getId(), bankDetailsResource)).thenReturn(restSuccess());

        mockMvc.perform(post("/project/{id}/bank-details", projectResource.getId()).
                contentType(MediaType.APPLICATION_FORM_URLENCODED).
                param("sortCode", "123456").
                param("accountNumber", "12345678").
                param("addressType", REGISTERED.name())).
                andExpect(status().is3xxRedirection()).
                andExpect(model().attributeDoesNotExist("readOnlyView")).
                andExpect(redirectedUrl("/project/" + projectResource.getId() + "/bank-details")).
                andReturn();
    }

    @Test
    public void testsubmitBankDetailsWhenAddressResourceIsNull() throws Exception {

        ProjectResource projectResource = setUpMockingForsubmitBankDetails();

        mockMvc.perform(post("/project/{id}/bank-details", projectResource.getId()).
                contentType(MediaType.APPLICATION_FORM_URLENCODED).
                param("sortCode", "123456").
                param("accountNumber", "12345678").
                param("addressType", ADD_NEW.name())).
                andExpect(model().attributeExists("readOnlyView")).
                andExpect(model().attribute("readOnlyView", false)).
                andExpect(view().name("project/bank-details"));

        verify(bankDetailsRestService, never()).submitBankDetails(any(), any());

    }

    @Test
    public void testsubmitBankDetailsWhenInvalidAccountDetails() throws Exception {

        ProjectResource projectResource = setUpMockingForsubmitBankDetails();

        MvcResult result = mockMvc.perform(post("/project/{id}/bank-details", projectResource.getId()).
                contentType(MediaType.APPLICATION_FORM_URLENCODED).
                param("sortCode", "1234WE").
                param("accountNumber", "123tt678").
                param("addressType", ADD_NEW.name())).
                andExpect(view().name("project/bank-details")).
                andExpect(model().hasErrors()).
                andExpect(model().errorCount(2)).
                andExpect(model().attributeHasFieldErrors(FORM_ATTR_NAME, "accountNumber")).
                andExpect(model().attributeHasFieldErrors(FORM_ATTR_NAME, "sortCode")).
                andExpect(model().attributeExists("readOnlyView")).
                andExpect(model().attribute("readOnlyView", false)).
                andReturn();

        verify(bankDetailsRestService, never()).submitBankDetails(any(), any());

        BindingResult bindingResult = ((BankDetailsForm)result.getModelAndView().getModel().get(FORM_ATTR_NAME)).getBindingResult();
        assertEquals("Please enter a valid account number.", bindingResult.getFieldError("accountNumber").getDefaultMessage());
        assertEquals("Please enter a valid sort code.", bindingResult.getFieldError("sortCode").getDefaultMessage());
    }

    @Test
    public void testsubmitBankDetailsWhenInvalidSortCode() throws Exception {

        ProjectResource projectResource = setUpMockingForsubmitBankDetails();

        MvcResult result = mockMvc.perform(post("/project/{id}/bank-details", projectResource.getId()).
                contentType(MediaType.APPLICATION_FORM_URLENCODED).
                param("sortCode", "q234WE").
                param("accountNumber", "12345678").
                param("addressType", ADD_NEW.name())).
                andExpect(view().name("project/bank-details")).
                andExpect(model().hasErrors()).
                andExpect(model().errorCount(1)).
                andExpect(model().attributeExists("readOnlyView")).
                andExpect(model().attribute("readOnlyView", false)).
                andExpect(model().attributeHasFieldErrors(FORM_ATTR_NAME, "sortCode")).
                andReturn();

        verify(bankDetailsRestService, never()).submitBankDetails(any(), any());

        BindingResult bindingResult = ((BankDetailsForm)result.getModelAndView().getModel().get(FORM_ATTR_NAME)).getBindingResult();
        assertEquals("Please enter a valid sort code.", bindingResult.getFieldError("sortCode").getDefaultMessage());
    }

    @Test
    public void testSearchAddressWithoutPostCode() throws Exception {
        ProjectResource projectResource = setUpMockingForsubmitBankDetails();

        mockMvc.perform(post("/project/{id}/bank-details", projectResource.getId()).
                contentType(MediaType.APPLICATION_FORM_URLENCODED).
                param(SEARCH_ADDRESS, "").
                param("sortCode", "123456").
                param("accountNumber", "12345678").
                param("addressType", ADD_NEW.name()).
                param("addressForm.postcodeInput", "")).
                andExpect(view().name("project/bank-details")).
                andExpect(model().hasErrors()).
                andExpect(model().attributeExists("readOnlyView")).
                andExpect(model().attribute("readOnlyView", false)).
                andExpect(model().attributeHasFieldErrors("form", "addressForm.postcodeInput"));

        verify(bankDetailsRestService, never()).submitBankDetails(any(), any());

    }

    private ProjectResource setUpMockingForsubmitBankDetails() {

        CompetitionResource competitionResource = newCompetitionResource().build();
        ApplicationResource applicationResource = newApplicationResource().withCompetition(competitionResource.getId()).build();
        ProjectResource projectResource = newProjectResource().withApplication(applicationResource).build();
        OrganisationResource organisationResource = newOrganisationResource().build();

        when(projectService.getById(projectResource.getId())).thenReturn(projectResource);
        when(projectService.getOrganisationByProjectAndUser(projectResource.getId(), loggedInUser.getId())).thenReturn(organisationResource);
        when(bankDetailsRestService.getBankDetailsByProjectAndOrganisation(projectResource.getId(), organisationResource.getId())).thenReturn(restFailure(new Error(BANK_DETAILS_DONT_EXIST_FOR_GIVEN_PROJECT_AND_ORGANISATION)));

        return projectResource;
    }
}
