package com.worth.ifs.bankdetails.controller;

import com.worth.ifs.address.resource.AddressResource;
import com.worth.ifs.address.resource.AddressTypeResource;
import com.worth.ifs.application.service.OrganisationService;
import com.worth.ifs.bankdetails.BankDetailsService;
import com.worth.ifs.bankdetails.form.ApproveBankDetailsForm;
import com.worth.ifs.bankdetails.form.ChangeBankDetailsForm;
import com.worth.ifs.bankdetails.resource.BankDetailsResource;
import com.worth.ifs.bankdetails.resource.ProjectBankDetailsStatusSummary;
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
import com.worth.ifs.user.resource.UserRoleType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import java.util.List;
import java.util.function.Supplier;

import static com.worth.ifs.address.resource.OrganisationAddressType.BANK_DETAILS;
import static com.worth.ifs.controller.ErrorToObjectErrorConverterFactory.asGlobalErrors;
import static com.worth.ifs.controller.ErrorToObjectErrorConverterFactory.fieldErrorsToFieldErrors;
import static com.worth.ifs.util.CollectionFunctions.getOnlyElement;
import static com.worth.ifs.util.CollectionFunctions.simpleFilter;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 * This controller is for serving internal project finance user, allowing them to view and manage project bank account details.
 */
@Controller
@RequestMapping("/project/{projectId}")
public class BankDetailsManagementController {

    private static final String FORM_ATTR_NAME = "form";

    @Autowired
    private OrganisationService organisationService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private BankDetailsService bankDetailsService;

    @PreAuthorize("hasPermission(#projectId, 'ACCESS_BANK_DETAILS_SECTION')")
    @RequestMapping(value = "/review-all-bank-details", method = GET)
    public String viewPartnerBankDetails(
            Model model,
            @PathVariable("projectId") Long projectId,
            @ModelAttribute("loggedInUser") UserResource loggedInUser) {
        model.addAttribute("isCompAdminUser", loggedInUser.hasRole(UserRoleType.COMP_ADMIN));
        final ProjectBankDetailsStatusSummary bankDetailsStatusSummary = bankDetailsService.getBankDetailsByProject(projectId);
        return doViewBankDetailsSummaryPage(bankDetailsStatusSummary, model);
    }

    @PreAuthorize("hasPermission(#projectId, 'ACCESS_BANK_DETAILS_SECTION')")
    @RequestMapping(value = "/organisation/{organisationId}/review-bank-details", method = GET)
    public String viewBankDetails(
            Model model,
            @PathVariable("projectId") Long projectId,
            @PathVariable("organisationId") Long organisationId,
            @ModelAttribute("loggedInUser") UserResource loggedInUser) {
        final OrganisationResource organisationResource = organisationService.getOrganisationById(organisationId);
        final ProjectResource project = projectService.getById(projectId);
        final BankDetailsResource bankDetailsResource = bankDetailsService.getBankDetailsByProjectAndOrganisation(projectId, organisationResource.getId());
        return doViewReviewBankDetails(organisationResource, project, bankDetailsResource, model, new ApproveBankDetailsForm());
    }

    @PreAuthorize("hasPermission(#projectId, 'ACCESS_BANK_DETAILS_SECTION')")
    @RequestMapping(value = "/organisation/{organisationId}/review-bank-details", method = POST)
    public String approveBankDetails(
            Model model,
            @ModelAttribute(FORM_ATTR_NAME) ApproveBankDetailsForm form,
            BindingResult bindingResult,
            ValidationHandler validationHandler,
            @PathVariable("projectId") Long projectId,
            @PathVariable("organisationId") Long organisationId,
            @ModelAttribute("loggedInUser") UserResource loggedInUser) {

        final OrganisationResource organisationResource = organisationService.getOrganisationById(organisationId);
        final ProjectResource project = projectService.getById(projectId);
        final BankDetailsResource bankDetailsResource = bankDetailsService.getBankDetailsByProjectAndOrganisation(projectId, organisationResource.getId());
        bankDetailsResource.setManualApproval(true);

        Supplier<String> faliureView = () -> {
            bankDetailsResource.setManualApproval(false);
            return doViewReviewBankDetails(organisationResource, project, bankDetailsResource, model, form);
        };

        return validationHandler.performActionOrBindErrorsToField("",
                faliureView,
                () -> doViewReviewBankDetails(organisationResource, project, bankDetailsResource, model, form),
                () -> bankDetailsService.updateBankDetails(projectId, bankDetailsResource));
    }

    @PreAuthorize("hasPermission(#projectId, 'ACCESS_BANK_DETAILS_SECTION')")
    @RequestMapping(value = "/organisation/{organisationId}/review-bank-details/change", method = GET)
    public String changeBankDetailsView(
            Model model,
            @PathVariable("projectId") Long projectId,
            @PathVariable("organisationId") Long organisationId,
            @ModelAttribute("loggedInUser") UserResource loggedInUser,
            @ModelAttribute(FORM_ATTR_NAME) ChangeBankDetailsForm form) {
        final OrganisationResource organisationResource = organisationService.getOrganisationById(organisationId);
        final ProjectResource project = projectService.getById(projectId);
        final BankDetailsResource bankDetailsResource = bankDetailsService.getBankDetailsByProjectAndOrganisation(projectId, organisationResource.getId());
        populateExitingBankDetailsInForm(organisationResource, bankDetailsResource, form);
        return doViewChangeBankDetailsNotUpdated(organisationResource, project, bankDetailsResource, model);
    }

    @PreAuthorize("hasPermission(#projectId, 'ACCESS_BANK_DETAILS_SECTION')")
    @RequestMapping(value = "/organisation/{organisationId}/review-bank-details/change", method = POST)
    public String changeBankDetails(
            Model model,
            @PathVariable("projectId") Long projectId,
            @PathVariable("organisationId") Long organisationId,
            @ModelAttribute("loggedInUser") UserResource loggedInUser,
            @Valid @ModelAttribute(FORM_ATTR_NAME) ChangeBankDetailsForm form,
            @SuppressWarnings("unused") BindingResult bindingResult,
            ValidationHandler validationHandler) {
        final OrganisationResource organisationResource = organisationService.getOrganisationById(organisationId);
        final ProjectResource project = projectService.getById(projectId);
        final BankDetailsResource existingBankDetails = bankDetailsService.getBankDetailsByProjectAndOrganisation(projectId, organisationResource.getId());

        Supplier<String> failureView = () -> doViewChangeBankDetailsNotUpdated(organisationResource, project, existingBankDetails, model);

        return validationHandler.failNowOrSucceedWith(failureView, () -> {
            final OrganisationAddressResource updatedOrganisationAddressResource = buildOrganisationAddressResource(organisationResource, form);
            final BankDetailsResource updatedBankDetailsResource = buildBankDetailsResource(existingBankDetails, projectId, organisationResource, updatedOrganisationAddressResource, form);
            final ServiceResult<Void> updateResult = bankDetailsService.updateBankDetails(projectId, updatedBankDetailsResource);
            return validationHandler.addAnyErrors(updateResult, fieldErrorsToFieldErrors(), asGlobalErrors()).failNowOrSucceedWith(
                    failureView, () -> {
                        OrganisationResource updatedOrganisationResource = buildOrganisationResource(organisationResource, form);
                        updatedOrganisationResource = organisationService.updateNameAndRegistration(updatedOrganisationResource);
                        return doViewReviewBankDetails(updatedOrganisationResource, project, updatedBankDetailsResource, model, new ApproveBankDetailsForm());
                    });
        });
    }

    private OrganisationAddressResource buildOrganisationAddressResource(OrganisationResource organisation, ChangeBankDetailsForm form){
        AddressResource address = form.getAddressForm().getSelectedPostcode();
        return new OrganisationAddressResource(organisation, address, new AddressTypeResource(BANK_DETAILS.getOrdinal(), BANK_DETAILS.name()));
    }

    private OrganisationResource buildOrganisationResource(final OrganisationResource organisationResource, ChangeBankDetailsForm form){
        organisationResource.setName(form.getOrganisationName());
        organisationResource.setCompanyHouseNumber(form.getRegistrationNumber());
        return organisationResource;
    }

    private BankDetailsResource buildBankDetailsResource(
            BankDetailsResource existingBankDetailsResource,
            Long projectId,
            OrganisationResource organisation,
            OrganisationAddressResource organisationAddressResource,
            ChangeBankDetailsForm form){
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

    private String doViewReviewBankDetails(OrganisationResource organisationResource, ProjectResource projectResource, BankDetailsResource bankDetailsResource, Model model, ApproveBankDetailsForm form) {
        BankDetailsReviewViewModel viewModel = populateBankDetailsReviewViewModel(organisationResource, projectResource, bankDetailsResource);
        model.addAttribute("model", viewModel);
        model.addAttribute(FORM_ATTR_NAME, form);
        return "project/review-bank-details";
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
        ChangeBankDetailsViewModel changeBankDetailsViewModel = new ChangeBankDetailsViewModel(bankDetailsReviewViewModel.getProjectId(), bankDetailsReviewViewModel.getApplicationId(), bankDetailsReviewViewModel.getProjectNumber(), bankDetailsReviewViewModel.getProjectName(), bankDetailsReviewViewModel.getFinanceContactName(), bankDetailsReviewViewModel.getFinanceContactEmail(), bankDetailsReviewViewModel.getFinanceContactPhoneNumber(), bankDetailsReviewViewModel.getOrganisationId(), bankDetailsReviewViewModel.getOrganisationName(), bankDetailsReviewViewModel.getRegistrationNumber(), bankDetailsReviewViewModel.getBankAccountNumber(), bankDetailsReviewViewModel.getSortCode(), bankDetailsReviewViewModel.getOrganisationAddress(), bankDetailsReviewViewModel.getVerified(), bankDetailsReviewViewModel.getCompanyNameScore(), bankDetailsReviewViewModel.getRegistrationNumberMatched(), bankDetailsReviewViewModel.getAddressScore(), bankDetailsReviewViewModel.getApproved(), bankDetailsReviewViewModel.getApprovedManually(), updated);
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


    private void populateExitingBankDetailsInForm(OrganisationResource organisation, BankDetailsResource bankDetails, ChangeBankDetailsForm form){
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

    private String doViewBankDetailsSummaryPage(ProjectBankDetailsStatusSummary projectBankDetailsStatusSummary, Model model){
        model.addAttribute("model", projectBankDetailsStatusSummary);
        return "project/bank-details-status";
    }
}
