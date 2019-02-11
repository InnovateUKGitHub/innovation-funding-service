package org.innovateuk.ifs.project.bankdetails;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.address.form.AddressForm;
import org.innovateuk.ifs.address.resource.AddressResource;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.finance.service.ApplicationFinanceRestService;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.organisation.service.OrganisationAddressRestService;
import org.innovateuk.ifs.project.ProjectService;
import org.innovateuk.ifs.project.bankdetails.controller.BankDetailsController;
import org.innovateuk.ifs.project.bankdetails.form.BankDetailsForm;
import org.innovateuk.ifs.project.bankdetails.resource.BankDetailsResource;
import org.innovateuk.ifs.project.bankdetails.service.BankDetailsRestService;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.service.ProjectRestService;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.validation.BindingResult;

import static org.innovateuk.ifs.address.builder.AddressResourceBuilder.newAddressResource;
import static org.innovateuk.ifs.address.resource.OrganisationAddressType.ADD_NEW;
import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.BANK_DETAILS_DONT_EXIST_FOR_GIVEN_PROJECT_AND_ORGANISATION;
import static org.innovateuk.ifs.commons.rest.RestResult.restFailure;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.finance.builder.ApplicationFinanceResourceBuilder.newApplicationFinanceResource;
import static org.innovateuk.ifs.organisation.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.innovateuk.ifs.project.AddressLookupBaseController.FORM_ATTR_NAME;
import static org.innovateuk.ifs.project.bankdetails.builder.BankDetailsResourceBuilder.newBankDetailsResource;
import static org.innovateuk.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class BankDetailsControllerTest extends BaseControllerMockMVCTest<BankDetailsController> {

    private final static String SEARCH_ADDRESS = "search-address";

    @Mock
    private ProjectService projectService;

    @Mock
    private ProjectRestService projectRestService;

    @Mock
    private BankDetailsRestService bankDetailsRestService;

    @Mock
    private OrganisationAddressRestService organisationAddressRestService;

    @Mock
    private ApplicationFinanceRestService applicationFinanceRestService;

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
        when(projectRestService.getOrganisationByProjectAndUser(projectResource.getId(), loggedInUser.getId())).thenReturn(restSuccess(organisationResource));
        when(bankDetailsRestService.getBankDetailsByProjectAndOrganisation(projectResource.getId(), organisationResource.getId())).thenReturn(restFailure(new Error(BANK_DETAILS_DONT_EXIST_FOR_GIVEN_PROJECT_AND_ORGANISATION)));
        when(applicationFinanceRestService.getApplicationFinance(applicationResource.getId(), organisationResource.getId())).thenReturn(restSuccess(newApplicationFinanceResource().withWorkPostcode("ABC 123").build()));

        BankDetailsForm form = new BankDetailsForm();
        form.getAddressForm().setPostcodeInput("ABC 123");

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
        AddressResource addressResource = newAddressResource().build();

        BankDetailsResource bankDetailsResource = newBankDetailsResource().withAddress(addressResource).build();

        when(projectService.getById(projectResource.getId())).thenReturn(projectResource);
        when(projectRestService.getOrganisationByProjectAndUser(projectResource.getId(), loggedInUser.getId())).thenReturn(restSuccess(organisationResource));
        when(bankDetailsRestService.getBankDetailsByProjectAndOrganisation(projectResource.getId(), organisationResource.getId())).thenReturn(RestResult.restSuccess(bankDetailsResource, HttpStatus.OK));

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
    public void testSubmitBankDetailsWhenAddressResourceIsNull() throws Exception {

        ProjectResource projectResource = setUpMockingForSubmitBankDetails();

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
    public void testSubmitBankDetailsWhenInvalidAccountDetails() throws Exception {

        ProjectResource projectResource = setUpMockingForSubmitBankDetails();

        MvcResult result = mockMvc.perform(post("/project/{id}/bank-details", projectResource.getId()).
                contentType(MediaType.APPLICATION_FORM_URLENCODED).
                param("sortCode", "1234WE").
                param("accountNumber", "123tt678").
                param("addressForm.addressType", AddressForm.AddressType.MANUAL_ENTRY.name()).
                param("addressForm.manualAddress.addressLine1", "Montrose House 1").
                param("addressForm.manualAddress.town", "Neston").
                param("addressForm.manualAddress.postcode", "CH64 3RU")).
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
    public void testSubmitBankDetailsWhenInvalidSortCode() throws Exception {

        ProjectResource projectResource = setUpMockingForSubmitBankDetails();

        MvcResult result = mockMvc.perform(post("/project/{id}/bank-details", projectResource.getId()).
                contentType(MediaType.APPLICATION_FORM_URLENCODED).
                param("sortCode", "q234WE").
                param("accountNumber", "12345678").
                param("addressForm.addressType", AddressForm.AddressType.MANUAL_ENTRY.name()).
                param("addressForm.manualAddress.addressLine1", "Montrose House 1").
                param("addressForm.manualAddress.town", "Neston").
                param("addressForm.manualAddress.postcode", "CH64 3RU")).
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
        ProjectResource projectResource = setUpMockingForSubmitBankDetails();

        mockMvc.perform(post("/project/{id}/bank-details", projectResource.getId()).
                contentType(MediaType.APPLICATION_FORM_URLENCODED).
                param(SEARCH_ADDRESS, "").
                param("sortCode", "123456").
                param("accountNumber", "12345678").
                param("addressForm.action", AddressForm.Action.SEARCH_POSTCODE.name()).
                param("addressForm.postcodeInput", "")).
                andExpect(view().name("project/bank-details")).
                andExpect(model().hasErrors()).
                andExpect(model().attributeExists("readOnlyView")).
                andExpect(model().attribute("readOnlyView", false)).
                andExpect(model().attributeHasFieldErrors("form", "addressForm.postcodeInput"));

        verify(bankDetailsRestService, never()).submitBankDetails(any(), any());

    }

    private ProjectResource setUpMockingForSubmitBankDetails() {

        CompetitionResource competitionResource = newCompetitionResource().build();
        ApplicationResource applicationResource = newApplicationResource().withCompetition(competitionResource.getId()).build();
        ProjectResource projectResource = newProjectResource().withApplication(applicationResource).build();
        OrganisationResource organisationResource = newOrganisationResource().build();

        when(projectService.getById(projectResource.getId())).thenReturn(projectResource);
        when(projectRestService.getOrganisationByProjectAndUser(projectResource.getId(), loggedInUser.getId())).thenReturn(restSuccess(organisationResource));
        when(bankDetailsRestService.getBankDetailsByProjectAndOrganisation(projectResource.getId(), organisationResource.getId())).thenReturn(restFailure(new Error(BANK_DETAILS_DONT_EXIST_FOR_GIVEN_PROJECT_AND_ORGANISATION)));
        when(applicationFinanceRestService.getApplicationFinance(applicationResource.getId(), organisationResource.getId())).thenReturn(restSuccess(newApplicationFinanceResource().build()));

        return projectResource;
    }
}
