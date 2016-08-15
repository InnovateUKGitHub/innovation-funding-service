package com.worth.ifs.project.monitoringofficer.controller;

import com.worth.ifs.BaseControllerMockMVCTest;
import com.worth.ifs.bankdetails.controller.BankDetailsManagementController;
import com.worth.ifs.bankdetails.resource.BankDetailsResource;
import com.worth.ifs.bankdetails.viewmodel.BankDetailsReviewViewModel;
import com.worth.ifs.project.resource.ProjectResource;
import com.worth.ifs.project.resource.ProjectUserResource;
import com.worth.ifs.user.resource.OrganisationResource;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;
import java.util.Map;

import static com.worth.ifs.address.builder.AddressResourceBuilder.newAddressResource;
import static com.worth.ifs.bankdetails.builder.BankDetailsResourceBuilder.newBankDetailsResource;
import static com.worth.ifs.organisation.builder.OrganisationAddressResourceBuilder.newOrganisationAddressResource;
import static com.worth.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static com.worth.ifs.project.builder.ProjectUserResourceBuilder.newProjectUserResource;
import static com.worth.ifs.user.builder.OrganisationResourceBuilder.newOrganisationResource;
import static com.worth.ifs.user.resource.UserRoleType.FINANCE_CONTACT;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

public class BankDetailsManagementControllerTest extends BaseControllerMockMVCTest<BankDetailsManagementController> {
    private ProjectResource project;
    private OrganisationResource organisationResource;
    private BankDetailsResource bankDetailsResource;
    private List<ProjectUserResource> projectUsers;
    private BankDetailsReviewViewModel bankDetailsReviewViewModel;

    @Before
    public void setUp(){
        super.setUp();
        organisationResource = newOrganisationResource().build();
        bankDetailsResource = newBankDetailsResource().build();
        bankDetailsResource.setOrganisationAddress(newOrganisationAddressResource().withAddress(newAddressResource().build()).build());
        project = newProjectResource().build();
        projectUsers = newProjectUserResource().build(3);
        projectUsers.get(0).setRoleName(FINANCE_CONTACT.getName());
        projectUsers.get(0).setOrganisation(organisationResource.getId());
        bankDetailsReviewViewModel = buildModelView(project, projectUsers.get(0), organisationResource, bankDetailsResource);
    }

    private BankDetailsReviewViewModel buildModelView(ProjectResource project, ProjectUserResource financeContact, OrganisationResource organisation, BankDetailsResource bankDetails){
        return new BankDetailsReviewViewModel(
                project.getId(),
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
}
