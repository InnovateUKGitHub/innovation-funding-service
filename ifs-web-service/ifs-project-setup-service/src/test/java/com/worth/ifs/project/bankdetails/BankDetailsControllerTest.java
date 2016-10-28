package com.worth.ifs.project.bankdetails;

import com.worth.ifs.BaseControllerMockMVCTest;
import com.worth.ifs.address.resource.AddressResource;
import com.worth.ifs.address.resource.AddressTypeResource;
import com.worth.ifs.application.resource.ApplicationResource;
import com.worth.ifs.bankdetails.form.BankDetailsForm;
import com.worth.ifs.project.bankdetails.resource.BankDetailsResource;
import com.worth.ifs.commons.error.Error;
import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.organisation.resource.OrganisationAddressResource;
import com.worth.ifs.project.BankDetailsController;
import com.worth.ifs.project.resource.ProjectResource;
import com.worth.ifs.user.resource.OrganisationResource;
import org.junit.Test;
import org.springframework.http.MediaType;

import java.util.Collections;

import static com.worth.ifs.address.builder.AddressResourceBuilder.newAddressResource;
import static com.worth.ifs.address.builder.AddressTypeResourceBuilder.newAddressTypeResource;
import static com.worth.ifs.address.resource.OrganisationAddressType.ADD_NEW;
import static com.worth.ifs.address.resource.OrganisationAddressType.REGISTERED;
import static com.worth.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static com.worth.ifs.project.bankdetails.builder.BankDetailsResourceBuilder.newBankDetailsResource;
import static com.worth.ifs.commons.error.CommonFailureKeys.BANK_DETAILS_DONT_EXIST_FOR_GIVEN_PROJECT_AND_ORGANISATION;
import static com.worth.ifs.commons.rest.RestResult.restFailure;
import static com.worth.ifs.commons.rest.RestResult.restSuccess;
import static com.worth.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static com.worth.ifs.organisation.builder.OrganisationAddressResourceBuilder.newOrganisationAddressResource;
import static com.worth.ifs.project.AddressLookupBaseController.FORM_ATTR_NAME;
import static com.worth.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static com.worth.ifs.user.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class BankDetailsControllerTest extends BaseControllerMockMVCTest<BankDetailsController> {

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
                .andExpect(model().attribute("currentUser", loggedInUser))
                .andExpect(model().attribute("organisation", organisationResource))
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
                andExpect(view().name("project/bank-details"));

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
