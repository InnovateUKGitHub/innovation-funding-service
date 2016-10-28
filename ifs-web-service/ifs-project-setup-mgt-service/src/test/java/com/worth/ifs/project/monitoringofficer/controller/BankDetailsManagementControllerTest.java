package com.worth.ifs.project.monitoringofficer.controller;

import com.worth.ifs.BaseControllerMockMVCTest;
import com.worth.ifs.address.resource.AddressTypeResource;
import com.worth.ifs.bankdetails.controller.BankDetailsManagementController;
import com.worth.ifs.bankdetails.resource.BankDetailsResource;
import com.worth.ifs.bankdetails.resource.ProjectBankDetailsStatusSummary;
import com.worth.ifs.bankdetails.viewmodel.BankDetailsReviewViewModel;
import com.worth.ifs.bankdetails.viewmodel.ChangeBankDetailsViewModel;
import com.worth.ifs.organisation.resource.OrganisationAddressResource;
import com.worth.ifs.project.resource.ProjectResource;
import com.worth.ifs.project.resource.ProjectUserResource;
import com.worth.ifs.user.resource.OrganisationResource;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;
import java.util.Map;

import static com.worth.ifs.address.builder.AddressResourceBuilder.newAddressResource;
import static com.worth.ifs.address.resource.OrganisationAddressType.BANK_DETAILS;
import static com.worth.ifs.bankdetails.builder.BankDetailsResourceBuilder.newBankDetailsResource;
import static com.worth.ifs.bankdetails.builder.ProjectBankDetailsStatusSummaryBuilder.newProjectBankDetailsStatusSummary;
import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;
import static com.worth.ifs.organisation.builder.OrganisationAddressResourceBuilder.newOrganisationAddressResource;
import static com.worth.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static com.worth.ifs.project.builder.ProjectUserResourceBuilder.newProjectUserResource;
import static com.worth.ifs.user.builder.OrganisationResourceBuilder.newOrganisationResource;
import static com.worth.ifs.user.resource.UserRoleType.FINANCE_CONTACT;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

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
    private BankDetailsReviewViewModel updatedSortCodeViewModel;
    private BankDetailsReviewViewModel updatedAddressViewModel;
    private BankDetailsReviewViewModel updatedOrganisationDetailsViewModel;


    @Before
    public void setUp(){
        super.setUp();
        organisationResource = newOrganisationResource().withName("Vitruvius Stonework Limited").withCompanyHouseNumber("60674010").build();
        updatedOrganisationResource = newOrganisationResource().withId(organisationResource.getId()).withName("Vitruvius Stonework").withCompanyHouseNumber("60674010").build();
        OrganisationAddressResource organisationAddressResource = newOrganisationAddressResource().withOrganisation(organisationResource.getId()).withAddress(newAddressResource().withAddressLine1("Montrose House 1").withAddressLine2("Clayhill Park").withAddressLine3("Cheshire West and Chester").withTown("Neston").withCounty("Cheshire").withPostcode("CH64 3RU").build()).build();
        project = newProjectResource().build();

        bankDetailsResource = newBankDetailsResource().withProject(project.getId()).withOrganisation(organisationResource.getId()).withOrganiationAddress(organisationAddressResource).withAccountNumber("51406795").withSortCode("404745").withCompanyName(organisationResource.getName()).withRegistrationNumber(organisationResource.getCompanyHouseNumber()).build();

        AddressTypeResource addressTypeResource = new AddressTypeResource(BANK_DETAILS.getOrdinal(), BANK_DETAILS.name());

        OrganisationAddressResource unmodifiedOrganisationAddressResource = newOrganisationAddressResource().withOrganisation(organisationResource.getId()).withAddressType(addressTypeResource).withAddress(newAddressResource().withAddressLine1("Montrose House 1").withAddressLine2("Clayhill Park").withAddressLine3("Cheshire West and Chester").withTown("Neston").withCounty("Cheshire").withPostcode("CH64 3RU").build()).build();
        unmodifiedOrganisationAddressResource.setId(null);
        unmodifiedOrganisationAddressResource.getAddress().setId(null);

        updatedBankDetailsResource = newBankDetailsResource().withId(bankDetailsResource.getId()).withProject(project.getId()).withOrganisation(organisationResource.getId()).withOrganiationAddress(unmodifiedOrganisationAddressResource).withAccountNumber(bankDetailsResource.getAccountNumber()).withSortCode("404746").withCompanyName(organisationResource.getName()).withRegistrationNumber(bankDetailsResource.getRegistrationNumber()).build();

        notUpdatedBankDetailsResource = newBankDetailsResource().withId(bankDetailsResource.getId()).withProject(project.getId()).withOrganisation(organisationResource.getId()).withOrganiationAddress(unmodifiedOrganisationAddressResource).withAccountNumber(bankDetailsResource.getAccountNumber()).withSortCode(bankDetailsResource.getSortCode()).withCompanyName(organisationResource.getName()).withRegistrationNumber(bankDetailsResource.getRegistrationNumber()).build();

        OrganisationAddressResource updatedLine1OrganisationAddressResource = newOrganisationAddressResource().withOrganisation(organisationResource.getId()).withAddressType(addressTypeResource).withAddress(newAddressResource().withAddressLine1("Montrose House 2").withAddressLine2("Clayhill Park").withAddressLine3("Cheshire West and Chester").withTown("Neston").withCounty("Cheshire").withPostcode("CH64 3RU").build()).build();
        updatedLine1OrganisationAddressResource.setId(null);
        updatedLine1OrganisationAddressResource.getAddress().setId(null);
        updatedAddressBankDetailsResource = newBankDetailsResource().withId(bankDetailsResource.getId()).withProject(project.getId()).withOrganisation(organisationResource.getId()).withOrganiationAddress(updatedLine1OrganisationAddressResource).withAccountNumber(bankDetailsResource.getAccountNumber()).withSortCode(bankDetailsResource.getSortCode()).withCompanyName(organisationResource.getName()).withRegistrationNumber(bankDetailsResource.getRegistrationNumber()).build();

        projectUsers = newProjectUserResource().build(3);
        projectUsers.get(0).setRoleName(FINANCE_CONTACT.getName());
        projectUsers.get(0).setOrganisation(organisationResource.getId());

        bankDetailsReviewViewModel = buildModelView(project, projectUsers.get(0), organisationResource, bankDetailsResource);

        notUpdatedChangeBankDetailsViewModel = new ChangeBankDetailsViewModel(bankDetailsReviewViewModel.getProjectId(), bankDetailsReviewViewModel.getApplicationId(), bankDetailsReviewViewModel.getProjectNumber(), bankDetailsReviewViewModel.getProjectName(), bankDetailsReviewViewModel.getFinanceContactName(), bankDetailsReviewViewModel.getFinanceContactEmail(), bankDetailsReviewViewModel.getFinanceContactPhoneNumber(), bankDetailsReviewViewModel.getOrganisationId(), bankDetailsReviewViewModel.getOrganisationName(), bankDetailsReviewViewModel.getRegistrationNumber(), bankDetailsReviewViewModel.getBankAccountNumber(), bankDetailsReviewViewModel.getSortCode(), bankDetailsReviewViewModel.getOrganisationAddress(), bankDetailsReviewViewModel.getVerified(), bankDetailsReviewViewModel.getCompanyNameScore(), bankDetailsReviewViewModel.getRegistrationNumberMatched(), bankDetailsReviewViewModel.getAddressScore(), bankDetailsReviewViewModel.getApproved(), bankDetailsReviewViewModel.getApprovedManually(), false);
        updatedSortCodeViewModel = new BankDetailsReviewViewModel(bankDetailsReviewViewModel.getProjectId(), bankDetailsReviewViewModel.getApplicationId(), bankDetailsReviewViewModel.getProjectNumber(), bankDetailsReviewViewModel.getProjectName(), bankDetailsReviewViewModel.getFinanceContactName(), bankDetailsReviewViewModel.getFinanceContactEmail(), bankDetailsReviewViewModel.getFinanceContactPhoneNumber(), bankDetailsReviewViewModel.getOrganisationId(), bankDetailsReviewViewModel.getOrganisationName(), bankDetailsReviewViewModel.getRegistrationNumber(), bankDetailsReviewViewModel.getBankAccountNumber(), updatedBankDetailsResource.getSortCode(), bankDetailsReviewViewModel.getOrganisationAddress(), bankDetailsReviewViewModel.getVerified(), bankDetailsReviewViewModel.getCompanyNameScore(), bankDetailsReviewViewModel.getRegistrationNumberMatched(), bankDetailsReviewViewModel.getAddressScore(), bankDetailsReviewViewModel.getApproved(), bankDetailsReviewViewModel.getApprovedManually());
        updatedAddressViewModel = new BankDetailsReviewViewModel(bankDetailsReviewViewModel.getProjectId(), bankDetailsReviewViewModel.getApplicationId(), bankDetailsReviewViewModel.getProjectNumber(), bankDetailsReviewViewModel.getProjectName(), bankDetailsReviewViewModel.getFinanceContactName(), bankDetailsReviewViewModel.getFinanceContactEmail(), bankDetailsReviewViewModel.getFinanceContactPhoneNumber(), bankDetailsReviewViewModel.getOrganisationId(), bankDetailsReviewViewModel.getOrganisationName(), bankDetailsReviewViewModel.getRegistrationNumber(), bankDetailsReviewViewModel.getBankAccountNumber(), bankDetailsReviewViewModel.getSortCode(), updatedLine1OrganisationAddressResource.getAddress().getAsSingleLine(), bankDetailsReviewViewModel.getVerified(), bankDetailsReviewViewModel.getCompanyNameScore(), bankDetailsReviewViewModel.getRegistrationNumberMatched(), bankDetailsReviewViewModel.getAddressScore(), bankDetailsReviewViewModel.getApproved(), bankDetailsReviewViewModel.getApprovedManually());
        updatedOrganisationDetailsViewModel = new BankDetailsReviewViewModel(bankDetailsReviewViewModel.getProjectId(), bankDetailsReviewViewModel.getApplicationId(), bankDetailsReviewViewModel.getProjectNumber(), bankDetailsReviewViewModel.getProjectName(), bankDetailsReviewViewModel.getFinanceContactName(), bankDetailsReviewViewModel.getFinanceContactEmail(), bankDetailsReviewViewModel.getFinanceContactPhoneNumber(), bankDetailsReviewViewModel.getOrganisationId(), updatedOrganisationResource.getName(), updatedOrganisationResource.getCompanyHouseNumber(), bankDetailsReviewViewModel.getBankAccountNumber(), bankDetailsReviewViewModel.getSortCode(), bankDetailsReviewViewModel.getOrganisationAddress(), bankDetailsReviewViewModel.getVerified(), bankDetailsReviewViewModel.getCompanyNameScore(), bankDetailsReviewViewModel.getRegistrationNumberMatched(), bankDetailsReviewViewModel.getAddressScore(), bankDetailsReviewViewModel.getApproved(), bankDetailsReviewViewModel.getApprovedManually());
    }

    private BankDetailsReviewViewModel buildModelView(ProjectResource project, ProjectUserResource financeContact, OrganisationResource organisation, BankDetailsResource bankDetails){
        return new BankDetailsReviewViewModel(
                project.getId(),
                project.getApplication(),
                project.getFormattedId(),
                project.getName(),
                financeContact.getUserName(),
                financeContact.getEmail(),
                financeContact.getPhoneNumber(),
                organisation.getId(),
                organisation.getName(),
                organisation.getCompanyHouseNumber(),
                bankDetails.getAccountNumber(),
                bankDetails.getSortCode(),
                bankDetails.getOrganisationAddress().getAddress().getAsSingleLine(),
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
        when(organisationService.getOrganisationById(organisationResource.getId())).thenReturn(organisationResource);
        when(bankDetailsService.getBankDetailsByProjectAndOrganisation(project.getId(), organisationResource.getId())).thenReturn(bankDetailsResource);
        when(projectService.getById(project.getId())).thenReturn(project);
        when(projectService.getProjectUsersForProject(project.getId())).thenReturn(projectUsers);
        MvcResult result = mockMvc.perform(get("/project/" + project.getId() + "/organisation/" + organisationResource.getId() + "/review-bank-details")).
                andExpect(view().name("project/review-bank-details")).
                andExpect(status().isOk()).
                andReturn();

        Map<String, Object> modelMap = result.getModelAndView().getModel();
        BankDetailsReviewViewModel model = (BankDetailsReviewViewModel) modelMap.get("model");
        assertEquals(model, bankDetailsReviewViewModel);
    }

    @Test
    public void canViewBankDetailsChangeForm() throws Exception {
        when(organisationService.getOrganisationById(organisationResource.getId())).thenReturn(organisationResource);
        when(projectService.getById(project.getId())).thenReturn(project);
        when(bankDetailsService.getBankDetailsByProjectAndOrganisation(project.getId(), organisationResource.getId())).thenReturn(bankDetailsResource);
        when(projectService.getProjectUsersForProject(project.getId())).thenReturn(projectUsers);

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
        when(organisationService.getOrganisationById(organisationResource.getId())).thenReturn(organisationResource);
        when(projectService.getById(project.getId())).thenReturn(project);
        when(bankDetailsService.getBankDetailsByProjectAndOrganisation(project.getId(), organisationResource.getId())).thenReturn(bankDetailsResource);
        when(projectService.getProjectUsersForProject(project.getId())).thenReturn(projectUsers);
        when(bankDetailsService.updateBankDetails(project.getId(), updatedBankDetailsResource)).thenReturn(serviceSuccess());
        when(organisationService.updateNameAndRegistration(organisationResource)).thenReturn(organisationResource);

        MvcResult result = mockMvc.perform(post("/project/" + project.getId() + "/organisation/" + organisationResource.getId() + "/review-bank-details/change").
                contentType(MediaType.APPLICATION_FORM_URLENCODED).
                param("organisationName", "Vitruvius Stonework Limited").
                param("registrationNumber", "60674010").
                param("accountNumber", "51406795").
                param("sortCode", "404746").
                param("addressForm.selectedPostcode.addressLine1", "Montrose House 1").
                param("addressForm.selectedPostcode.addressLine2", "Clayhill Park").
                param("addressForm.selectedPostcode.addressLine3", "Cheshire West and Chester").
                param("addressForm.selectedPostcode.town", "Neston").
                param("addressForm.selectedPostcode.county", "Cheshire").
                param("addressForm.selectedPostcode.postcode", "CH64 3RU")).
                andExpect(status().isOk()).
                andExpect(view().name("project/review-bank-details")).
                andReturn();

        Map<String, Object> modelMap = result.getModelAndView().getModel();
        BankDetailsReviewViewModel model = (BankDetailsReviewViewModel) modelMap.get("model");
        assertEquals(model, updatedSortCodeViewModel);
    }

    @Test
    public void canUpdateBankAddress() throws Exception {
        when(organisationService.getOrganisationById(organisationResource.getId())).thenReturn(organisationResource);
        when(projectService.getById(project.getId())).thenReturn(project);
        when(bankDetailsService.getBankDetailsByProjectAndOrganisation(project.getId(), organisationResource.getId())).thenReturn(bankDetailsResource);
        when(projectService.getProjectUsersForProject(project.getId())).thenReturn(projectUsers);
        when(bankDetailsService.updateBankDetails(project.getId(), updatedAddressBankDetailsResource)).thenReturn(serviceSuccess());
        when(organisationService.updateNameAndRegistration(organisationResource)).thenReturn(organisationResource);

        MvcResult result = mockMvc.perform(post("/project/" + project.getId() + "/organisation/" + organisationResource.getId() + "/review-bank-details/change").
                contentType(MediaType.APPLICATION_FORM_URLENCODED).
                param("organisationName", "Vitruvius Stonework Limited").
                param("registrationNumber", "60674010").
                param("accountNumber", "51406795").
                param("sortCode", "404745").
                param("addressForm.selectedPostcode.addressLine1", "Montrose House 2").
                param("addressForm.selectedPostcode.addressLine2", "Clayhill Park").
                param("addressForm.selectedPostcode.addressLine3", "Cheshire West and Chester").
                param("addressForm.selectedPostcode.town", "Neston").
                param("addressForm.selectedPostcode.county", "Cheshire").
                param("addressForm.selectedPostcode.postcode", "CH64 3RU")).
                andExpect(status().isOk()).
                andExpect(view().name("project/review-bank-details")).
                andReturn();

        Map<String, Object> modelMap = result.getModelAndView().getModel();
        BankDetailsReviewViewModel model = (BankDetailsReviewViewModel) modelMap.get("model");
        assertEquals(model, updatedAddressViewModel);
    }

    @Test
    public void canUpdateOrganisationDetails() throws Exception {
        when(organisationService.getOrganisationById(organisationResource.getId())).thenReturn(organisationResource);
        when(projectService.getById(project.getId())).thenReturn(project);
        when(bankDetailsService.getBankDetailsByProjectAndOrganisation(project.getId(), organisationResource.getId())).thenReturn(bankDetailsResource);
        when(projectService.getProjectUsersForProject(project.getId())).thenReturn(projectUsers);
        when(bankDetailsService.updateBankDetails(project.getId(), notUpdatedBankDetailsResource)).thenReturn(serviceSuccess());
        when(organisationService.updateNameAndRegistration(updatedOrganisationResource)).thenReturn(updatedOrganisationResource);

        MvcResult result = mockMvc.perform(post("/project/" + project.getId() + "/organisation/" + organisationResource.getId() + "/review-bank-details/change").
                contentType(MediaType.APPLICATION_FORM_URLENCODED).
                param("organisationName", "Vitruvius Stonework").
                param("registrationNumber", "60674010").
                param("accountNumber", "51406795").
                param("sortCode", "404745").
                param("addressForm.selectedPostcode.addressLine1", "Montrose House 1").
                param("addressForm.selectedPostcode.addressLine2", "Clayhill Park").
                param("addressForm.selectedPostcode.addressLine3", "Cheshire West and Chester").
                param("addressForm.selectedPostcode.town", "Neston").
                param("addressForm.selectedPostcode.county", "Cheshire").
                param("addressForm.selectedPostcode.postcode", "CH64 3RU")).
                andExpect(status().isOk()).
                andExpect(view().name("project/review-bank-details")).
                andReturn();

        Map<String, Object> modelMap = result.getModelAndView().getModel();
        BankDetailsReviewViewModel model = (BankDetailsReviewViewModel) modelMap.get("model");
        assertEquals(model, updatedOrganisationDetailsViewModel);
    }

    @Test
    public void testViewPartnerBankDetails() throws  Exception {
        Long projectId = 123L;
        final ProjectBankDetailsStatusSummary bankDetailsStatusSummary = newProjectBankDetailsStatusSummary().build();
        when(bankDetailsService.getBankDetailsByProject(projectId)).thenReturn(bankDetailsStatusSummary);
        MvcResult result = mockMvc.perform(get("/project/" + projectId + "/review-all-bank-details")).andExpect(status().isOk()).andExpect(view().name("project/bank-details-status")).andReturn();
        assertEquals(bankDetailsStatusSummary, result.getModelAndView().getModel().get("model"));
    }
}
