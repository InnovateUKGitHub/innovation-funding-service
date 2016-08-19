package com.worth.ifs.bankdetails.controller;

import com.worth.ifs.address.resource.AddressResource;
import com.worth.ifs.address.resource.AddressTypeResource;
import com.worth.ifs.application.service.OrganisationService;
import com.worth.ifs.bankdetails.BankDetailsService;
import com.worth.ifs.bankdetails.form.AmendBankDetailsForm;
import com.worth.ifs.bankdetails.resource.BankDetailsResource;
import com.worth.ifs.bankdetails.viewmodel.BankDetailsReviewViewModel;
import com.worth.ifs.bankdetails.viewmodel.ChangeBankDetailsViewModel;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.controller.ValidationHandler;
import com.worth.ifs.form.AddressForm;
import com.worth.ifs.organisation.resource.OrganisationAddressResource;
import com.worth.ifs.project.ProjectService;
import com.worth.ifs.project.resource.ProjectResource;
import com.worth.ifs.project.resource.ProjectUserResource;
import com.worth.ifs.user.resource.OrganisationResource;
import com.worth.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import java.util.List;

import static com.worth.ifs.address.resource.OrganisationAddressType.BANK_DETAILS;
import static com.worth.ifs.controller.ErrorToObjectErrorConverterFactory.asGlobalErrors;
import static com.worth.ifs.util.CollectionFunctions.getOnlyElement;
import static com.worth.ifs.util.CollectionFunctions.simpleFilter;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

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
        final OrganisationResource organisationResource = organisationService.getOrganisationById(organisationId);
        final ProjectResource project = projectService.getById(projectId);
        final BankDetailsResource bankDetailsResource = bankDetailsService.getBankDetailsByProjectAndOrganisation(projectId, organisationResource.getId());
        return doViewReviewBankDetails(organisationResource, project, bankDetailsResource, model);
    }

    @RequestMapping(method = POST)
    public String approveBankDetails(
            Model model,
            @PathVariable("projectId") Long projectId,
            @PathVariable("organisationId") Long organisationId,
            @ModelAttribute("loggedInUser") UserResource loggedInUser) {
        final OrganisationResource organisationResource = organisationService.getOrganisationById(organisationId);
        final ProjectResource project = projectService.getById(projectId);
        final BankDetailsResource bankDetailsResource = bankDetailsService.getBankDetailsByProjectAndOrganisation(projectId, organisationResource.getId());
        return doViewReviewBankDetails(organisationResource, project, bankDetailsResource, model);
    }

    @RequestMapping(method = GET, path = "/change")
    public String changeBankDetailsView(
            Model model,
            @PathVariable("projectId") Long projectId,
            @PathVariable("organisationId") Long organisationId,
            @ModelAttribute("loggedInUser") UserResource loggedInUser,
            @ModelAttribute(FORM_ATTR_NAME) AmendBankDetailsForm form) {
        final OrganisationResource organisationResource = organisationService.getOrganisationById(organisationId);
        final ProjectResource project = projectService.getById(projectId);
        final BankDetailsResource bankDetailsResource = bankDetailsService.getBankDetailsByProjectAndOrganisation(projectId, organisationResource.getId());
        populateExitingBankDetailsInForm(organisationResource, bankDetailsResource, form);
        return doViewChangeBankDetailsNotUpdated(organisationResource, project, bankDetailsResource, model);
    }

    @RequestMapping(method = POST, path = "/change")
    public String changeBankDetails(
            Model model,
            @PathVariable("projectId") Long projectId,
            @PathVariable("organisationId") Long organisationId,
            @ModelAttribute("loggedInUser") UserResource loggedInUser,
            @Valid @ModelAttribute(FORM_ATTR_NAME) AmendBankDetailsForm form,
            @SuppressWarnings("unused") BindingResult bindingResult,
            ValidationHandler validationHandler) {
        final OrganisationResource organisationResource = organisationService.getOrganisationById(organisationId);
        final ProjectResource project = projectService.getById(projectId);
        final BankDetailsResource existingBankDetails = bankDetailsService.getBankDetailsByProjectAndOrganisation(projectId, organisationResource.getId());
        final OrganisationAddressResource organisationAddressResource = buildOrganisationAddressResource(organisationResource, form);
        return validationHandler.failNowOrSucceedWith(
                () -> doViewChangeBankDetailsNotUpdated(organisationResource, project, existingBankDetails, model), () -> {
                    final BankDetailsResource updatedBankDetailsResource = buildBankDetailsResource(existingBankDetails, projectId, organisationResource, organisationAddressResource, form);
                    if (!existingBankDetails.equals(updatedBankDetailsResource)) {
                        ServiceResult<Void> updateResult = bankDetailsService.updateBankDetails(projectId, updatedBankDetailsResource);
                        if (updateResult.isFailure()) {
                            validationHandler.addAnyErrors(updateResult, asGlobalErrors());
                        }
                        return doViewChangeBankDetailsUpdated(organisationResource, project, updatedBankDetailsResource, model);
                    } else {
                        return doViewChangeBankDetailsNotUpdated(organisationResource, project, existingBankDetails, model);
                    }
                }
        );
    }

    private OrganisationAddressResource buildOrganisationAddressResource(OrganisationResource organisation, AmendBankDetailsForm form){
        AddressResource address = form.getAddressForm().getSelectedPostcode();
        return new OrganisationAddressResource(organisation, address, new AddressTypeResource(BANK_DETAILS.getOrdinal(), BANK_DETAILS.name()));
    }

    private BankDetailsResource buildBankDetailsResource(
            BankDetailsResource existingBankDetailsResource,
            Long projectId,
            OrganisationResource organisation,
            OrganisationAddressResource organisationAddressResource,
            AmendBankDetailsForm form){
        BankDetailsResource bankDetailsResource = new BankDetailsResource();

        bankDetailsResource.setId(existingBankDetailsResource.getId());
        bankDetailsResource.setOrganisationTypeName(existingBankDetailsResource.getOrganisationTypeName());
        bankDetailsResource.setAddressScore(existingBankDetailsResource.getAddressScore());
        bankDetailsResource.setCompanyNameScore(existingBankDetailsResource.getCompanyNameScore());
        bankDetailsResource.setRegistrationNumberMatched(existingBankDetailsResource.getRegistrationNumberMatched());
        bankDetailsResource.setManualApproval(existingBankDetailsResource.isManualApproval());
        bankDetailsResource.setVerified(bankDetailsResource.isVerified());

        bankDetailsResource.setAccountNumber(form.getAccountNumber());
        bankDetailsResource.setSortCode(form.getSortCode());
        bankDetailsResource.setProject(projectId);
        bankDetailsResource.setOrganisation(organisation.getId());
        bankDetailsResource.setCompanyName(organisation.getName());
        bankDetailsResource.setRegistrationNumber(organisation.getCompanyHouseNumber());
        bankDetailsResource.setOrganisationAddress(organisationAddressResource);

        return bankDetailsResource;
    }

    private String doViewReviewBankDetails(OrganisationResource organisationResource, ProjectResource projectResource, BankDetailsResource bankDetailsResource, Model model) {
        BankDetailsReviewViewModel viewModel = populateBankDetailsReviewViewModel(organisationResource, projectResource, bankDetailsResource);
        model.addAttribute("model", viewModel);
        return "project/review-bank-details";
    }

    private String doViewChangeBankDetailsUpdated(OrganisationResource organisationResource,
                                           ProjectResource projectResource,
                                           BankDetailsResource bankDetailsResource,
                                           Model model) {
        return doViewChangeBankDetails(organisationResource, projectResource, bankDetailsResource, true, model);
    }

    private String doViewChangeBankDetailsNotUpdated(OrganisationResource organisationResource,
                                                  ProjectResource projectResource,
                                                  BankDetailsResource bankDetailsResource,
                                                  Model model) {
        return doViewChangeBankDetails(organisationResource, projectResource, bankDetailsResource, false, model);
    }

    private String doViewChangeBankDetails(OrganisationResource organisationResource,
                                           ProjectResource projectResource,
                                           BankDetailsResource bankDetailsResource,
                                           boolean updated,
                                           Model model) {
        BankDetailsReviewViewModel bankDetailsReviewViewModel = populateBankDetailsReviewViewModel(organisationResource, projectResource, bankDetailsResource);
        ChangeBankDetailsViewModel changeBankDetailsViewModel = new ChangeBankDetailsViewModel(bankDetailsReviewViewModel.getProjectId(), bankDetailsReviewViewModel.getProjectNumber(), bankDetailsReviewViewModel.getProjectName(), bankDetailsReviewViewModel.getFinanceContactName(), bankDetailsReviewViewModel.getFinanceContactEmail(), bankDetailsReviewViewModel.getFinanceContactPhoneNumber(), bankDetailsReviewViewModel.getOrganisationId(), bankDetailsReviewViewModel.getOrganisationName(), bankDetailsReviewViewModel.getRegistrationNumber(), bankDetailsReviewViewModel.getBankAccountNumber(), bankDetailsReviewViewModel.getSortCode(), bankDetailsReviewViewModel.getOrganisationAddress(), bankDetailsReviewViewModel.getVerified(), bankDetailsReviewViewModel.getCompanyNameScore(), bankDetailsReviewViewModel.getRegistrationNumberMatched(), bankDetailsReviewViewModel.getAddressScore(), bankDetailsReviewViewModel.getApproved(), bankDetailsReviewViewModel.getApprovedManually(), updated);
        model.addAttribute("model", changeBankDetailsViewModel);
        return "project/change-bank-details";
    }

    private BankDetailsReviewViewModel populateBankDetailsReviewViewModel(OrganisationResource organisation, ProjectResource project,  BankDetailsResource bankDetails){
        List<ProjectUserResource> projectUsers = projectService.getProjectUsersForProject(project.getId());
        ProjectUserResource financeContact = getOnlyElement(simpleFilter(projectUsers, pr -> pr.isFinanceContact() && organisation.getId().equals(pr.getOrganisation())));
        return buildViewModel(project, financeContact, organisation, bankDetails);
    }

    private BankDetailsReviewViewModel buildViewModel(ProjectResource project, ProjectUserResource financeContact, OrganisationResource organisation, BankDetailsResource bankDetails){
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


    private void populateExitingBankDetailsInForm(OrganisationResource organisation, BankDetailsResource bankDetails, AmendBankDetailsForm form){
        form.setOrganisationName(organisation.getName());
        form.setRegistrationNumber(organisation.getCompanyHouseNumber());
        form.setSortCode(bankDetails.getSortCode());
        form.setAccountNumber(bankDetails.getAccountNumber());
        populateAddress(form.getAddressForm(), bankDetails);
    }

    private void populateAddress(AddressForm addressForm, BankDetailsResource bankDetails){
        addressForm.setManualAddress(true);
        addressForm.setSelectedPostcode(bankDetails.getOrganisationAddress().getAddress());
    }
}
