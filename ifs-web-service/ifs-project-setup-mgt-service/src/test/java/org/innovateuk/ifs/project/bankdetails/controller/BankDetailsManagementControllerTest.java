package org.innovateuk.ifs.project.bankdetails.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.address.form.AddressForm;
import org.innovateuk.ifs.address.resource.AddressResource;
import org.innovateuk.ifs.address.resource.AddressTypeResource;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.project.ProjectService;
import org.innovateuk.ifs.project.bankdetails.populator.BankDetailsReviewModelPopulator;
import org.innovateuk.ifs.project.bankdetails.resource.BankDetailsResource;
import org.innovateuk.ifs.project.bankdetails.resource.ProjectBankDetailsStatusSummary;
import org.innovateuk.ifs.project.bankdetails.service.BankDetailsRestService;
import org.innovateuk.ifs.project.bankdetails.viewmodel.BankDetailsReviewViewModel;
import org.innovateuk.ifs.project.bankdetails.viewmodel.ChangeBankDetailsViewModel;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.resource.ProjectUserResource;
import org.innovateuk.ifs.user.service.OrganisationRestService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;
import java.util.Map;

import static org.innovateuk.ifs.address.builder.AddressResourceBuilder.newAddressResource;
import static org.innovateuk.ifs.address.resource.OrganisationAddressType.BANK_DETAILS;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.organisation.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.innovateuk.ifs.project.bankdetails.builder.BankDetailsResourceBuilder.newBankDetailsResource;
import static org.innovateuk.ifs.project.bankdetails.builder.BankDetailsStatusResourceBuilder.newBankDetailsStatusResource;
import static org.innovateuk.ifs.project.bankdetails.builder.ProjectBankDetailsStatusSummaryBuilder.newProjectBankDetailsStatusSummary;
import static org.innovateuk.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static org.innovateuk.ifs.project.builder.ProjectUserResourceBuilder.newProjectUserResource;
import static org.innovateuk.ifs.project.resource.ProjectState.SETUP;
import static org.innovateuk.ifs.user.resource.Role.FINANCE_CONTACT;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class BankDetailsManagementControllerTest extends BaseControllerMockMVCTest<BankDetailsManagementController> {
    private ProjectResource project;
    private OrganisationResource organisationResource;
    private OrganisationResource updatedOrganisationResource;

    private BankDetailsResource bankDetailsResource;
    private BankDetailsResource notUpdatedBankDetailsResource;
    private BankDetailsResource updatedBankDetailsResource;
    private BankDetailsResource updatedAddressBankDetailsResource;

    private List<ProjectUserResource> projectUsers;
    private BankDetailsReviewViewModel bankDetailsReviewViewModel;
    private ChangeBankDetailsViewModel notUpdatedChangeBankDetailsViewModel;

    @Mock
    private BankDetailsReviewModelPopulator bankDetailsReviewModelPopulator;

    @Mock
    private OrganisationRestService organisationRestService;

    @Mock
    private BankDetailsRestService bankDetailsRestService;

    @Mock
    private ProjectService projectService;

    @Before
    public void setupCommonExpectations() {

        organisationResource = newOrganisationResource().withName("Vitruvius Stonework Limited").withCompaniesHouseNumber("60674010").build();
        updatedOrganisationResource = newOrganisationResource().withId(organisationResource.getId()).withName("Vitruvius Stonework").withCompaniesHouseNumber("60674010").build();
        AddressResource address = newAddressResource().withAddressLine1("Montrose House 1").withAddressLine2("Clayhill Park").withAddressLine3("Cheshire West and Chester").withTown("Neston").withCounty("Cheshire").withPostcode("CH64 3RU").build();
        project = newProjectResource().withProjectState(SETUP).build();

        bankDetailsResource = newBankDetailsResource().withProject(project.getId()).withOrganisation(organisationResource.getId()).withAddress(address).withAccountNumber("51406795").withSortCode("404745").withCompanyName(organisationResource.getName()).withRegistrationNumber(organisationResource.getCompaniesHouseNumber()).build();

        AddressTypeResource addressTypeResource = new AddressTypeResource(BANK_DETAILS.getOrdinal(), BANK_DETAILS.name());

        AddressResource unmodifiedAddressResource = newAddressResource().withAddressLine1("Montrose House 1").withAddressLine2("Clayhill Park").withAddressLine3("Cheshire West and Chester").withTown("Neston").withCounty("Cheshire").withPostcode("CH64 3RU").build();
        unmodifiedAddressResource.setId(null);

        updatedBankDetailsResource = newBankDetailsResource().withId(bankDetailsResource.getId()).withProject(project.getId()).withOrganisation(organisationResource.getId()).withAddress(unmodifiedAddressResource).withAccountNumber(bankDetailsResource.getAccountNumber()).withSortCode("404746").withCompanyName(organisationResource.getName()).withRegistrationNumber(bankDetailsResource.getRegistrationNumber()).build();

        notUpdatedBankDetailsResource = newBankDetailsResource().withId(bankDetailsResource.getId()).withProject(project.getId()).withOrganisation(organisationResource.getId()).withAddress(unmodifiedAddressResource).withAccountNumber(bankDetailsResource.getAccountNumber()).withSortCode(bankDetailsResource.getSortCode()).withCompanyName(organisationResource.getName()).withRegistrationNumber(bankDetailsResource.getRegistrationNumber()).build();

        AddressResource updatedLine1AddressResource = newAddressResource().withAddressLine1("Montrose House 2").withAddressLine2("Clayhill Park").withAddressLine3("Cheshire West and Chester").withTown("Neston").withCounty("Cheshire").withPostcode("CH64 3RU").build();
        updatedLine1AddressResource.setId(null);
        updatedAddressBankDetailsResource = newBankDetailsResource().withId(bankDetailsResource.getId()).withProject(project.getId()).withOrganisation(organisationResource.getId()).withAddress(updatedLine1AddressResource).withAccountNumber(bankDetailsResource.getAccountNumber()).withSortCode(bankDetailsResource.getSortCode()).withCompanyName(organisationResource.getName()).withRegistrationNumber(bankDetailsResource.getRegistrationNumber()).build();

        projectUsers = newProjectUserResource().build(3);
        projectUsers.get(0).setRoleName(FINANCE_CONTACT.getName());
        projectUsers.get(0).setOrganisation(organisationResource.getId());

        bankDetailsReviewViewModel = buildModelView(project, projectUsers.get(0), organisationResource, bankDetailsResource);

        notUpdatedChangeBankDetailsViewModel = new ChangeBankDetailsViewModel(project,
                                                                              bankDetailsReviewViewModel.getFinanceContactName(),
                                                                              bankDetailsReviewViewModel.getFinanceContactEmail(),
                                                                              bankDetailsReviewViewModel.getFinanceContactPhoneNumber(),
                                                                              bankDetailsReviewViewModel.getOrganisationId(),
                                                                              bankDetailsReviewViewModel.getOrganisationName(),
                                                                              bankDetailsReviewViewModel.getRegistrationNumber(),
                                                                              bankDetailsReviewViewModel.getBankAccountNumber(),
                                                                              bankDetailsReviewViewModel.getSortCode(),
                                                                              bankDetailsReviewViewModel.getOrganisationAddress(),
                                                                              bankDetailsReviewViewModel.getVerified(),
                                                                              bankDetailsReviewViewModel.getCompanyNameScore(),
                                                                              bankDetailsReviewViewModel.getRegistrationNumberMatched(),
                                                                              bankDetailsReviewViewModel.getAddressScore(),
                                                                              bankDetailsReviewViewModel.getApproved(),
                                                                              bankDetailsReviewViewModel.getApprovedManually(),
                                                                              false);
    }

    private BankDetailsReviewViewModel buildModelView(ProjectResource project, ProjectUserResource financeContact, OrganisationResource organisation, BankDetailsResource bankDetails){
        return new BankDetailsReviewViewModel(
                project,
                financeContact.getUserName(),
                financeContact.getEmail(),
                financeContact.getPhoneNumber(),
                organisation.getId(),
                organisation.getName(),
                organisation.getCompaniesHouseNumber(),
                bankDetails.getAccountNumber(),
                bankDetails.getSortCode(),
                bankDetails.getAddress().getAsSingleLine(),
                bankDetails.isVerified(),
                bankDetails.getCompanyNameScore(),
                bankDetails.getRegistrationNumberMatched(),
                bankDetails.getAddressScore(),
                bankDetails.isApproved(),
                bankDetails.isManualApproval());
    }

    @Override
    protected BankDetailsManagementController supplyControllerUnderTest() {
        return new BankDetailsManagementController();
    }

    @Test
    public void canViewBankDetailsWhenBankDetailsSubmitted() throws Exception {
        when(organisationRestService.getOrganisationById(organisationResource.getId())).thenReturn(restSuccess(organisationResource));
        when(bankDetailsRestService.getBankDetailsByProjectAndOrganisation(project.getId(), organisationResource.getId())).thenReturn(restSuccess(bankDetailsResource));
        when(projectService.getById(project.getId())).thenReturn(project);
        when(projectService.getProjectUsersForProject(project.getId())).thenReturn(projectUsers);
        when(bankDetailsReviewModelPopulator.populateBankDetailsReviewViewModel(organisationResource, project, bankDetailsResource)).thenReturn(bankDetailsReviewViewModel);
        MvcResult result = mockMvc.perform(get("/project/" + project.getId() + "/organisation/" + organisationResource.getId() + "/review-bank-details")).
                andExpect(view().name("project/review-bank-details")).
                andExpect(status().isOk()).
                andReturn();

        Map<String, Object> modelMap = result.getModelAndView().getModel();
        BankDetailsReviewViewModel model = (BankDetailsReviewViewModel) modelMap.get("model");
        assertEquals(model, bankDetailsReviewViewModel);
    }

    @Test
    public void canViewBankDetailsWhenBankDetailsReSubmitted() throws Exception {
        when(organisationRestService.getOrganisationById(organisationResource.getId())).thenReturn(restSuccess(organisationResource));
        when(bankDetailsRestService.getBankDetailsByProjectAndOrganisation(project.getId(), organisationResource.getId())).thenReturn(restSuccess(bankDetailsResource));
        when(projectService.getById(project.getId())).thenReturn(project);
        when(projectService.getProjectUsersForProject(project.getId())).thenReturn(projectUsers);
        when(bankDetailsReviewModelPopulator.populateBankDetailsReviewViewModel(organisationResource, project, bankDetailsResource)).thenReturn(bankDetailsReviewViewModel);
        bankDetailsReviewViewModel.setApprovedManually(true);

        //The manualApproval flag will be 'true' in case of re-submission
        bankDetailsResource.setManualApproval(true);
        MvcResult result = mockMvc.perform(get("/project/" + project.getId() + "/organisation/" + organisationResource.getId() + "/review-bank-details")).
                andExpect(view().name("project/review-bank-details")).
                andExpect(status().isOk()).
                andReturn();

        Map<String, Object> modelMap = result.getModelAndView().getModel();
        BankDetailsReviewViewModel model = (BankDetailsReviewViewModel) modelMap.get("model");
        assertEquals(bankDetailsReviewViewModel.getBankAccountNumber(), model.getBankAccountNumber());
            assertEquals(true, model.getApprovedManually());
    }

    @Test
    public void canViewBankDetailsChangeForm() throws Exception {
        when(organisationRestService.getOrganisationById(organisationResource.getId())).thenReturn(restSuccess(organisationResource));
        when(projectService.getById(project.getId())).thenReturn(project);
        when(bankDetailsRestService.getBankDetailsByProjectAndOrganisation(project.getId(), organisationResource.getId())).thenReturn(restSuccess(bankDetailsResource));
        when(projectService.getProjectUsersForProject(project.getId())).thenReturn(projectUsers);
        when(bankDetailsReviewModelPopulator.populateBankDetailsReviewViewModel(organisationResource, project, bankDetailsResource)).thenReturn(bankDetailsReviewViewModel);

        MvcResult result = mockMvc.perform(get("/project/" + project.getId() + "/organisation/" + organisationResource.getId() + "/review-bank-details/change")).
                andExpect(view().name("project/change-bank-details")).
                andExpect(status().isOk()).
                andReturn();

        Map<String, Object> modelMap = result.getModelAndView().getModel();
        BankDetailsReviewViewModel model = (BankDetailsReviewViewModel) modelMap.get("model");
        assertEquals(model, notUpdatedChangeBankDetailsViewModel);
    }

    @Test
    public void canUpdateAccountDetails() throws Exception {
        when(organisationRestService.getOrganisationById(organisationResource.getId())).thenReturn(restSuccess(organisationResource));
        when(projectService.getById(project.getId())).thenReturn(project);
        when(bankDetailsRestService.getBankDetailsByProjectAndOrganisation(project.getId(), organisationResource.getId())).thenReturn(restSuccess(bankDetailsResource));
        when(projectService.getProjectUsersForProject(project.getId())).thenReturn(projectUsers);
        when(bankDetailsRestService.updateBankDetails(project.getId(), updatedBankDetailsResource)).thenReturn(restSuccess());
        when(organisationRestService.updateNameAndRegistration(organisationResource)).thenReturn(restSuccess(organisationResource));

        mockMvc.perform(post("/project/" + project.getId() + "/organisation/" + organisationResource.getId() + "/review-bank-details/change").
                contentType(MediaType.APPLICATION_FORM_URLENCODED).
                param("organisationName", "Vitruvius Stonework Limited").
                param("registrationNumber", "60674010").
                param("accountNumber", "51406795").
                param("sortCode", "404746").
                param("addressForm.addressType", AddressForm.AddressType.MANUAL_ENTRY.name()).
                param("addressForm.manualAddress.addressLine1", "Montrose House 1").
                param("addressForm.manualAddress.addressLine2", "Clayhill Park").
                param("addressForm.manualAddress.addressLine3", "Cheshire West and Chester").
                param("addressForm.manualAddress.town", "Neston").
                param("addressForm.manualAddress.county", "Cheshire").
                param("addressForm.manualAddress.postcode", "CH64 3RU")).
                andExpect(status().is3xxRedirection()).
                andExpect(view().name("redirect:/project/" + project.getId() +"/organisation/" + organisationResource.getId() + "/review-bank-details"));

        verify(bankDetailsRestService).updateBankDetails(project.getId(), updatedBankDetailsResource);
        verify(organisationRestService).updateNameAndRegistration(organisationResource);
    }

    @Test
    public void canUpdateBankAddress() throws Exception {
        when(organisationRestService.getOrganisationById(organisationResource.getId())).thenReturn(restSuccess(organisationResource));
        when(projectService.getById(project.getId())).thenReturn(project);
        when(bankDetailsRestService.getBankDetailsByProjectAndOrganisation(project.getId(), organisationResource.getId())).thenReturn(restSuccess(bankDetailsResource));
        when(projectService.getProjectUsersForProject(project.getId())).thenReturn(projectUsers);
        when(bankDetailsRestService.updateBankDetails(project.getId(), updatedAddressBankDetailsResource)).thenReturn(restSuccess());
        when(organisationRestService.updateNameAndRegistration(organisationResource)).thenReturn(restSuccess(organisationResource));

        mockMvc.perform(post("/project/" + project.getId() + "/organisation/" + organisationResource.getId() + "/review-bank-details/change").
                contentType(MediaType.APPLICATION_FORM_URLENCODED).
                param("organisationName", "Vitruvius Stonework Limited").
                param("registrationNumber", "60674010").
                param("accountNumber", "51406795").
                param("sortCode", "404745").
                param("addressForm.addressType", AddressForm.AddressType.MANUAL_ENTRY.name()).
                param("addressForm.manualAddress.addressLine1", "Montrose House 2").
                param("addressForm.manualAddress.addressLine2", "Clayhill Park").
                param("addressForm.manualAddress.addressLine3", "Cheshire West and Chester").
                param("addressForm.manualAddress.town", "Neston").
                param("addressForm.manualAddress.county", "Cheshire").
                param("addressForm.manualAddress.postcode", "CH64 3RU")).
                andExpect(status().is3xxRedirection()).
                andExpect(view().name("redirect:/project/" + project.getId() +"/organisation/" + organisationResource.getId() + "/review-bank-details"));

        verify(bankDetailsRestService).updateBankDetails(project.getId(), updatedAddressBankDetailsResource);
        verify(organisationRestService).updateNameAndRegistration(organisationResource);
    }

    @Test
    public void canUpdateOrganisationDetails() throws Exception {
        when(organisationRestService.getOrganisationById(organisationResource.getId())).thenReturn(restSuccess(organisationResource));
        when(projectService.getById(project.getId())).thenReturn(project);
        when(bankDetailsRestService.getBankDetailsByProjectAndOrganisation(project.getId(), organisationResource.getId())).thenReturn(restSuccess(bankDetailsResource));
        when(projectService.getProjectUsersForProject(project.getId())).thenReturn(projectUsers);
        when(bankDetailsRestService.updateBankDetails(project.getId(), notUpdatedBankDetailsResource)).thenReturn(restSuccess());
        when(organisationRestService.updateNameAndRegistration(organisationResource)).thenReturn(restSuccess(organisationResource));

        mockMvc.perform(post("/project/" + project.getId() + "/organisation/" + organisationResource.getId() + "/review-bank-details/change").
                contentType(MediaType.APPLICATION_FORM_URLENCODED).
                param("organisationName", "Vitruvius Stonework").
                param("registrationNumber", "60674010").
                param("accountNumber", "51406795").
                param("sortCode", "404745").
                param("addressForm.addressType", AddressForm.AddressType.MANUAL_ENTRY.name()).
                param("addressForm.manualAddress.addressLine1", "Montrose House 1").
                param("addressForm.manualAddress.addressLine2", "Clayhill Park").
                param("addressForm.manualAddress.addressLine3", "Cheshire West and Chester").
                param("addressForm.manualAddress.town", "Neston").
                param("addressForm.manualAddress.county", "Cheshire").
                param("addressForm.manualAddress.postcode", "CH64 3RU")).
                andExpect(status().is3xxRedirection()).
                andExpect(view().name("redirect:/project/" + project.getId() +"/organisation/" + organisationResource.getId() + "/review-bank-details"));

        verify(bankDetailsRestService).updateBankDetails(any(Long.class), any(BankDetailsResource.class));
        verify(organisationRestService).updateNameAndRegistration(updatedOrganisationResource);
    }

    @Test
    public void testViewPartnerBankDetails() throws  Exception {
        Long projectId = 123L;
        final ProjectBankDetailsStatusSummary bankDetailsStatusSummary = newProjectBankDetailsStatusSummary().withBankDetailsStatusResources(newBankDetailsStatusResource().build(2)).build();
        when(bankDetailsRestService.getBankDetailsStatusSummaryByProject(projectId)).thenReturn(restSuccess(bankDetailsStatusSummary));
        MvcResult result = mockMvc.perform(get("/project/" + projectId + "/review-all-bank-details")).andExpect(status().isOk()).andExpect(view().name("project/bank-details-status")).andReturn();
        assertEquals(bankDetailsStatusSummary, result.getModelAndView().getModel().get("model"));
    }

    @Test
    public void testViewPartnerBankDetails_redirect() throws  Exception {
        long projectId = 123L;
        long orgId = 7L;
        final ProjectBankDetailsStatusSummary bankDetailsStatusSummary = newProjectBankDetailsStatusSummary().withBankDetailsStatusResources(newBankDetailsStatusResource().withOrganisationId(orgId).build(1)).build();
        when(bankDetailsRestService.getBankDetailsStatusSummaryByProject(projectId)).thenReturn(restSuccess(bankDetailsStatusSummary));
        mockMvc.perform(get("/project/" + projectId + "/review-all-bank-details"))
                .andExpect(redirectedUrl("/project/123/organisation/7/review-bank-details?isCompAdminUser=false"));
    }
}
