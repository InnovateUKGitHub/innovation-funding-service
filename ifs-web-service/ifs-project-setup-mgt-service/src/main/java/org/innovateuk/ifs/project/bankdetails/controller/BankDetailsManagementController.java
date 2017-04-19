package org.innovateuk.ifs.project.bankdetails.controller;

import org.innovateuk.ifs.address.resource.AddressResource;
import org.innovateuk.ifs.address.resource.AddressTypeResource;
import org.innovateuk.ifs.application.service.OrganisationService;
import org.innovateuk.ifs.bankdetails.BankDetailsService;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.form.AddressForm;
import org.innovateuk.ifs.organisation.resource.OrganisationAddressResource;
import org.innovateuk.ifs.project.ProjectService;
import org.innovateuk.ifs.project.bankdetails.form.ApproveBankDetailsForm;
import org.innovateuk.ifs.project.bankdetails.form.ChangeBankDetailsForm;
import org.innovateuk.ifs.project.bankdetails.populator.BankDetailsReviewModelPopulator;
import org.innovateuk.ifs.project.bankdetails.resource.BankDetailsResource;
import org.innovateuk.ifs.project.bankdetails.resource.ProjectBankDetailsStatusSummary;
import org.innovateuk.ifs.project.bankdetails.viewmodel.BankDetailsReviewViewModel;
import org.innovateuk.ifs.project.bankdetails.viewmodel.ChangeBankDetailsViewModel;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.resource.ProjectUserResource;
import org.innovateuk.ifs.user.resource.OrganisationResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.resource.UserRoleType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.function.Supplier;

import static org.innovateuk.ifs.address.resource.OrganisationAddressType.BANK_DETAILS;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.controller.ErrorToObjectErrorConverterFactory.asGlobalErrors;
import static org.innovateuk.ifs.controller.ErrorToObjectErrorConverterFactory.fieldErrorsToFieldErrors;
import static org.innovateuk.ifs.util.CollectionFunctions.getOnlyElement;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleFilter;

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

    @Autowired
    private BankDetailsReviewModelPopulator bankDetailsReviewModelPopulator;

    @PreAuthorize("hasPermission(#projectId, 'ACCESS_BANK_DETAILS_SECTION')")
    @GetMapping("/review-all-bank-details")
    public String viewPartnerBankDetails(
            Model model,
            @PathVariable("projectId") Long projectId,
            @ModelAttribute("loggedInUser") UserResource loggedInUser) {
        model.addAttribute("isCompAdminUser", loggedInUser.hasRole(UserRoleType.COMP_ADMIN));
        final ProjectBankDetailsStatusSummary bankDetailsStatusSummary = bankDetailsService.getBankDetailsByProject(projectId);
        return doViewBankDetailsSummaryPage(bankDetailsStatusSummary, model);
    }

    @PreAuthorize("hasPermission(#projectId, 'ACCESS_BANK_DETAILS_SECTION')")
    @GetMapping("/organisation/{organisationId}/review-bank-details")
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
    @PostMapping("/organisation/{organisationId}/review-bank-details")
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
        if(bankDetailsResource.isManualApproval()) {
            return "redirect:/project/" + projectId + "/organisation/" + organisationId + "/review-bank-details";
        }
        bankDetailsResource.setManualApproval(true);

        Supplier<String> faliureView = () -> {
            bankDetailsResource.setManualApproval(false);
            return doViewReviewBankDetails(organisationResource, project, bankDetailsResource, model, form);
        };

        return validationHandler.performActionOrBindErrorsToField("",
                faliureView,
                () -> doViewReviewBankDetails(organisationResource, project, bankDetailsResource, model, form),
                () -> {
                    Void result = bankDetailsService.updateBankDetails(projectId, bankDetailsResource).getSuccessObjectOrThrowException();
                    return serviceSuccess(result);
                }
        );
    }

    @PreAuthorize("hasPermission(#projectId, 'ACCESS_BANK_DETAILS_SECTION')")
    @GetMapping("/organisation/{organisationId}/review-bank-details/change")
    public String changeBankDetailsView(
            Model model,
            @PathVariable("projectId") Long projectId,
            @PathVariable("organisationId") Long organisationId,
            @ModelAttribute("loggedInUser") UserResource loggedInUser,
            @ModelAttribute(FORM_ATTR_NAME) ChangeBankDetailsForm form) {
        final OrganisationResource organisationResource = organisationService.getOrganisationById(organisationId);
        final ProjectResource project = projectService.getById(projectId);
        final BankDetailsResource bankDetailsResource = bankDetailsService.getBankDetailsByProjectAndOrganisation(projectId, organisationResource.getId());
        bankDetailsReviewModelPopulator.populateExitingBankDetailsInForm(organisationResource, bankDetailsResource, form);
        return doViewChangeBankDetailsNotUpdated(organisationResource, project, bankDetailsResource, model);
    }

    @PreAuthorize("hasPermission(#projectId, 'ACCESS_BANK_DETAILS_SECTION')")
    @PostMapping("/organisation/{organisationId}/review-bank-details/change")
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
                        organisationService.updateNameAndRegistration(updatedOrganisationResource);
                        return "redirect:/project/" + projectId + "/organisation/" + organisationId + "/review-bank-details";
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
        model.addAttribute("model", bankDetailsReviewModelPopulator.populateBankDetailsReviewViewModel(organisationResource, projectResource, bankDetailsResource));
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
        BankDetailsReviewViewModel bankDetailsReviewViewModel = bankDetailsReviewModelPopulator.populateBankDetailsReviewViewModel(organisationResource, projectResource, bankDetailsResource);
        ChangeBankDetailsViewModel changeBankDetailsViewModel = new ChangeBankDetailsViewModel(bankDetailsReviewViewModel.getProjectId(), bankDetailsReviewViewModel.getApplicationId(), bankDetailsReviewViewModel.getProjectName(), bankDetailsReviewViewModel.getFinanceContactName(), bankDetailsReviewViewModel.getFinanceContactEmail(), bankDetailsReviewViewModel.getFinanceContactPhoneNumber(), bankDetailsReviewViewModel.getOrganisationId(), bankDetailsReviewViewModel.getOrganisationName(), bankDetailsReviewViewModel.getRegistrationNumber(), bankDetailsReviewViewModel.getBankAccountNumber(), bankDetailsReviewViewModel.getSortCode(), bankDetailsReviewViewModel.getOrganisationAddress(), bankDetailsReviewViewModel.getVerified(), bankDetailsReviewViewModel.getCompanyNameScore(), bankDetailsReviewViewModel.getRegistrationNumberMatched(), bankDetailsReviewViewModel.getAddressScore(), bankDetailsReviewViewModel.getApproved(), bankDetailsReviewViewModel.getApprovedManually(), updated);
        model.addAttribute("model", changeBankDetailsViewModel);
        return "project/change-bank-details";
    }

    private String doViewBankDetailsSummaryPage(ProjectBankDetailsStatusSummary projectBankDetailsStatusSummary, Model model){
        model.addAttribute("model", projectBankDetailsStatusSummary);
        return "project/bank-details-status";
    }
}
