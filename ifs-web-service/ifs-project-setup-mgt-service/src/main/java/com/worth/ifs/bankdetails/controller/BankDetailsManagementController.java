package com.worth.ifs.bankdetails.controller;

import com.worth.ifs.application.service.OrganisationService;
import com.worth.ifs.bankdetails.BankDetailsService;
import com.worth.ifs.bankdetails.form.AmendBankDetailsForm;
import com.worth.ifs.bankdetails.form.BankDetailsForm;
import com.worth.ifs.bankdetails.resource.BankDetailsResource;
import com.worth.ifs.bankdetails.viewmodel.AmendBankDetailsViewModel;
import com.worth.ifs.bankdetails.viewmodel.BankDetailsReviewViewModel;
import com.worth.ifs.project.ProjectService;
import com.worth.ifs.project.resource.ProjectResource;
import com.worth.ifs.project.resource.ProjectUserResource;
import com.worth.ifs.user.resource.OrganisationResource;
import com.worth.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

import static com.worth.ifs.util.CollectionFunctions.getOnlyElement;
import static com.worth.ifs.util.CollectionFunctions.simpleFilter;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 * This controller is for serving internal project finance user, allowing them to view and manage project bank account details.
 */
@Controller
@RequestMapping("/project/{projectId}/organisation/{organisationId}/review-bank-details")
public class BankDetailsManagementController {

    private static final String FORM_ATTR_NAME = "form";

    @Autowired
    private OrganisationService organisationService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private BankDetailsService bankDetailsService;


    @RequestMapping(method = GET)
    public String viewBankDetails(
            Model model,
            @PathVariable("projectId") Long projectId,
            @PathVariable("organisationId") Long organisationId,
            @ModelAttribute("loggedInUser") UserResource loggedInUser) {
        return doViewReviewBankDetails(model, projectId, organisationId);
    }

    @RequestMapping(method = GET, path = "/change")
    public String changeBankDetailsView(
            Model model,
            @PathVariable("projectId") Long projectId,
            @PathVariable("organisationId") Long organisationId,
            @ModelAttribute("loggedInUser") UserResource loggedInUser,
            @ModelAttribute(FORM_ATTR_NAME) AmendBankDetailsForm form) {
        final OrganisationResource organisationResource = organisationService.getOrganisationById(organisationId);
        BankDetailsResource bankDetailsResource = bankDetailsService.getBankDetailsByProjectAndOrganisation(projectId, organisationResource.getId());
        ProjectResource project = projectService.getById(projectId);
        populateExitingBankDetailsInForm(bankDetailsResource, form);
        return doViewChangeBankDetails(loggedInUser, organisationResource, project, bankDetailsResource, form, model);
    }

    private String doViewReviewBankDetails(Model model, Long projectId, Long organisationId) {
        final OrganisationResource organisationResource = organisationService.getOrganisationById(organisationId);
        BankDetailsResource bankDetailsResource = bankDetailsService.
                getBankDetailsByProjectAndOrganisation(projectId, organisationResource.getId());
        BankDetailsReviewViewModel viewModel = populateBankDetailsReviewViewModel(projectId, organisationResource, bankDetailsResource);
        model.addAttribute("model", viewModel);
        return "project/review-bank-details";
    }

    private String doViewChangeBankDetails(UserResource loggedInUser,
                                           OrganisationResource organisationResource,
                                           ProjectResource projectResource,
                                           BankDetailsResource bankDetailsResource,
                                           AmendBankDetailsForm form,
                                           Model model) {
        populateBankDetailsModel(loggedInUser, organisationResource, projectResource, bankDetailsResource, form, model);
        return "project/change-bank-details";
    }

    private BankDetailsReviewViewModel populateBankDetailsReviewViewModel(Long projectId, OrganisationResource organisation, BankDetailsResource bankDetails){
        ProjectResource project = projectService.getById(projectId);
        List<ProjectUserResource> projectUsers = projectService.getProjectUsersForProject(projectId);
        ProjectUserResource financeContact = getOnlyElement(simpleFilter(projectUsers, pr -> pr.isFinanceContact() && organisation.getId().equals(pr.getOrganisation())));
        return buildViewModel(project, financeContact, organisation, bankDetails);
    }

    private BankDetailsReviewViewModel buildViewModel(ProjectResource project, ProjectUserResource financeContact, OrganisationResource organisation, BankDetailsResource bankDetails){
        return new BankDetailsReviewViewModel(
                project.getFormattedId(),
                project.getName(),
                financeContact.getUserName(),
                financeContact.getEmail(),
                financeContact.getPhoneNumber(),
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

    private AmendBankDetailsViewModel buildAmendBankDetailsViewModel(ProjectResource project, OrganisationResource organisation, BankDetailsResource bankDetails){
        return new AmendBankDetailsViewModel(project);
    };

    private void populateExitingBankDetailsInForm(BankDetailsResource bankDetails, AmendBankDetailsForm form){
        form.setSortCode(bankDetails.getSortCode());
        form.setAccountNumber(bankDetails.getAccountNumber());
    }

    private void populateBankDetailsModel(UserResource loggedInUser, OrganisationResource organisationResource, ProjectResource project, BankDetailsResource bankDetails, BankDetailsForm form, Model model){
        AmendBankDetailsViewModel bankDetailsViewModel = buildAmendBankDetailsViewModel(project, organisationResource, bankDetails);
        model.addAttribute("project", project);
        model.addAttribute("currentUser", loggedInUser);
        model.addAttribute("organisation", organisationResource);
        model.addAttribute(FORM_ATTR_NAME, form);
        model.addAttribute("model", bankDetailsViewModel);
    }
}
