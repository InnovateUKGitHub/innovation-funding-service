package org.innovateuk.ifs.project.bankdetails.controller;

import org.innovateuk.ifs.address.resource.OrganisationAddressType;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.form.AddressForm;
import org.innovateuk.ifs.organisation.resource.OrganisationAddressResource;
import org.innovateuk.ifs.organisation.service.OrganisationAddressRestService;
import org.innovateuk.ifs.project.AddressLookupBaseController;
import org.innovateuk.ifs.project.ProjectService;
import org.innovateuk.ifs.project.bankdetails.resource.BankDetailsResource;
import org.innovateuk.ifs.project.bankdetails.service.BankDetailsRestService;
import org.innovateuk.ifs.project.bankdetails.form.BankDetailsForm;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.bankdetails.viewmodel.BankDetailsViewModel;
import org.innovateuk.ifs.user.resource.OrganisationResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;
import java.util.function.Supplier;

import static org.innovateuk.ifs.address.resource.OrganisationAddressType.*;
import static org.innovateuk.ifs.controller.ErrorToObjectErrorConverterFactory.asGlobalErrors;

/**
 * This controller will handle all requests that are related to project bank details.
 */
@Controller
@RequestMapping("/project/{projectId}/bank-details")
public class BankDetailsController extends AddressLookupBaseController {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private BankDetailsRestService bankDetailsRestService;

    @Autowired
    private OrganisationAddressRestService organisationAddressRestService;

    @PreAuthorize("hasPermission(#projectId, 'ACCESS_BANK_DETAILS_SECTION')")
    @GetMapping
    public String bankDetails(Model model,
                              @PathVariable("projectId") final Long projectId,
                              @ModelAttribute("loggedInUser") UserResource loggedInUser,
                              @ModelAttribute(FORM_ATTR_NAME) BankDetailsForm form) {
        ProjectResource projectResource = projectService.getById(projectId);
        OrganisationResource organisationResource = projectService.getOrganisationByProjectAndUser(projectId, loggedInUser.getId());
        RestResult<BankDetailsResource> bankDetailsResourceRestResult = bankDetailsRestService.getBankDetailsByProjectAndOrganisation(projectId, organisationResource.getId());
        if(bankDetailsResourceRestResult.isSuccess()) {
            BankDetailsResource bankDetailsResource = bankDetailsResourceRestResult.getSuccessObject();
            populateExitingBankDetailsInForm(bankDetailsResource, form);
        }
        return doViewBankDetails(model, form, projectResource, bankDetailsResourceRestResult, loggedInUser, false);
    }

    @PreAuthorize("hasPermission(#projectId, 'ACCESS_BANK_DETAILS_SECTION')")
    @GetMapping("readonly")
    public String bankDetailsAsReadOnly(Model model,
                              @PathVariable("projectId") final Long projectId,
                              @ModelAttribute("loggedInUser") UserResource loggedInUser,
                              @ModelAttribute(FORM_ATTR_NAME) BankDetailsForm form) {
        ProjectResource projectResource = projectService.getById(projectId);
        OrganisationResource organisationResource = projectService.getOrganisationByProjectAndUser(projectId, loggedInUser.getId());
        RestResult<BankDetailsResource> bankDetailsResourceRestResult = bankDetailsRestService.getBankDetailsByProjectAndOrganisation(projectId, organisationResource.getId());
        if(bankDetailsResourceRestResult.isSuccess()) {
            BankDetailsResource bankDetailsResource = bankDetailsResourceRestResult.getSuccessObject();
            populateExitingBankDetailsInForm(bankDetailsResource, form);
        }
        return doViewBankDetails(model, form, projectResource, bankDetailsResourceRestResult, loggedInUser, true);
    }


    @PreAuthorize("hasPermission(#projectId, 'ACCESS_BANK_DETAILS_SECTION')")
    @PostMapping
    public String submitBankDetails(Model model,
                                    @Valid @ModelAttribute(FORM_ATTR_NAME) BankDetailsForm form,
                                    @SuppressWarnings("unused") BindingResult bindingResult,
                                    ValidationHandler validationHandler,
                                    @PathVariable("projectId") final Long projectId,
                                    @ModelAttribute("loggedInUser") UserResource loggedInUser) {

        final Supplier<String> failureView = () -> bankDetails(model, projectId, loggedInUser, form);

        return validationHandler.failNowOrSucceedWithFilter(e -> !e.getField().contains("addressForm"), failureView,
                () -> {
                    if (isNewAddressNotValid(form)) {
                        addAddressNotProvidedValidationError(bindingResult, validationHandler);
                        return bankDetails(model, projectId, loggedInUser, form);
                    }

                    OrganisationResource organisationResource = projectService.getOrganisationByProjectAndUser(projectId, loggedInUser.getId());
                    OrganisationAddressResource organisationAddressResource = getOrganisationAddressResourceOrNull(form, organisationResource, BANK_DETAILS);

                    BankDetailsResource bankDetailsResource = buildBankDetailsResource(projectId, organisationResource, organisationAddressResource, form);
                    ServiceResult<Void> updateResult = bankDetailsRestService.submitBankDetails(projectId, bankDetailsResource).toServiceResult();

                    if (updateResult.isFailure()) {
                        validationHandler.addAnyErrors(updateResult, asGlobalErrors());
                        return bankDetails(model, projectId, loggedInUser, form);
                    }

                    return redirectToBankDetails(projectId);
                }
        );
    }

    @PreAuthorize("hasPermission(#projectId, 'ACCESS_BANK_DETAILS_SECTION')")
    @PostMapping("/confirm")
    public String confirmBankDetails(Model model,
                                     @Valid @ModelAttribute(FORM_ATTR_NAME) BankDetailsForm form,
                                     BindingResult bindingResult,
                                     ValidationHandler validationHandler,
                                     @PathVariable("projectId") final Long projectId,
                                     @ModelAttribute("loggedInUser") UserResource loggedInUser) {
        ProjectResource projectResource = projectService.getById(projectId);
        OrganisationResource organisationResource = projectService.getOrganisationByProjectAndUser(projectId, loggedInUser.getId());
        RestResult<BankDetailsResource> bankDetailsResourceRestResult = bankDetailsRestService.getBankDetailsByProjectAndOrganisation(projectId, organisationResource.getId());

        final Supplier<String> failureView = () -> doViewBankDetails(model, form, projectResource, bankDetailsResourceRestResult, loggedInUser, false);

        return validationHandler.failNowOrSucceedWithFilter(e -> !e.getField().contains("addressForm"), failureView,
                () -> doViewConfirmBankDetails(model, form, projectResource, bankDetailsResourceRestResult, loggedInUser));
    }

    @PreAuthorize("hasPermission(#projectId, 'ACCESS_BANK_DETAILS_SECTION')")
    @PostMapping(params = SEARCH_ADDRESS)
    public String searchAddress(Model model,
                                @PathVariable("projectId") Long projectId,
                                @Valid @ModelAttribute(FORM_ATTR_NAME) BankDetailsForm form,
                                BindingResult bindingResult,
                                @ModelAttribute("loggedInUser") UserResource loggedInUser) {
        if(StringUtils.isEmpty(form.getAddressForm().getPostcodeInput())){
            bindingResult.addError(createPostcodeSearchFieldError());
        }
        form.getAddressForm().setSelectedPostcodeIndex(null);
        form.getAddressForm().setTriedToSearch(true);
        form.setAddressType(OrganisationAddressType.valueOf(form.getAddressType().name()));
        ProjectResource project = projectService.getById(projectId);
        OrganisationResource organisationResource = projectService.getOrganisationByProjectAndUser(projectId, loggedInUser.getId());
        RestResult<BankDetailsResource> bankDetailsResourceRestResult = bankDetailsRestService.getBankDetailsByProjectAndOrganisation(projectId, organisationResource.getId());
        return doViewBankDetails(model, form, project, bankDetailsResourceRestResult, loggedInUser, false);
    }

    @PreAuthorize("hasPermission(#projectId, 'ACCESS_BANK_DETAILS_SECTION')")
    @PostMapping(params = SELECT_ADDRESS)
    public String selectAddress(Model model,
                                @PathVariable("projectId") Long projectId,
                                @ModelAttribute(FORM_ATTR_NAME) BankDetailsForm form,
                                @ModelAttribute("loggedInUser") UserResource loggedInUser) {
        form.getAddressForm().setSelectedPostcode(null);
        ProjectResource project = projectService.getById(projectId);
        OrganisationResource organisationResource = projectService.getOrganisationByProjectAndUser(projectId, loggedInUser.getId());
        RestResult<BankDetailsResource> bankDetailsResourceRestResult = bankDetailsRestService.getBankDetailsByProjectAndOrganisation(projectId, organisationResource.getId());
        return doViewBankDetails(model, form, project, bankDetailsResourceRestResult, loggedInUser, false);
    }

    @PreAuthorize("hasPermission(#projectId, 'ACCESS_BANK_DETAILS_SECTION')")
    @PostMapping(params = MANUAL_ADDRESS)
    public String manualAddress(Model model,
                                @ModelAttribute(FORM_ATTR_NAME) BankDetailsForm form,
                                @PathVariable("projectId") Long projectId,
                                @ModelAttribute("loggedInUser") UserResource loggedInUser) {
        AddressForm addressForm = form.getAddressForm();
        addressForm.setManualAddress(true);
        ProjectResource project = projectService.getById(projectId);
        OrganisationResource organisationResource = projectService.getOrganisationByProjectAndUser(projectId, loggedInUser.getId());
        RestResult<BankDetailsResource> bankDetailsResourceRestResult = bankDetailsRestService.getBankDetailsByProjectAndOrganisation(projectId, organisationResource.getId());
        return doViewBankDetails(model, form, project, bankDetailsResourceRestResult, loggedInUser, false);
    }

    private boolean isNewAddressNotValid(BankDetailsForm form) {

        return ( (form.getAddressForm().getSelectedPostcode() == null
                || StringUtils.isEmpty(form.getAddressForm().getSelectedPostcode().getAddressLine1())
                || StringUtils.isEmpty(form.getAddressForm().getSelectedPostcode().getPostcode())
                || StringUtils.isEmpty(form.getAddressForm().getSelectedPostcode().getTown())
        ) && OrganisationAddressType.ADD_NEW.name().equals(form.getAddressType().name()));
    }

    private String doViewBankDetails(Model model, BankDetailsForm form, ProjectResource projectResource,
                                     RestResult<BankDetailsResource> bankDetailsResourceRestResult,
                                     UserResource loggedInUser,
                                     boolean isReadOnly) {
        populateBankDetailsModel(model, form, loggedInUser, projectResource, bankDetailsResourceRestResult, isReadOnly);
        processAddressLookupFields(form);
        return "project/bank-details";
    }

    private String doViewConfirmBankDetails(Model model, BankDetailsForm form, ProjectResource projectResource,
                                            RestResult<BankDetailsResource> bankDetailsResourceRestResult,
                                            UserResource loggedInUser) {
        populateBankDetailsModel(model, form, loggedInUser, projectResource,
                bankDetailsResourceRestResult, false);
        processAddressLookupFields(form);
        return "project/bank-details-confirm";
    }

    private void populateBankDetailsModel(Model model, BankDetailsForm form, UserResource loggedInUser,
                                          ProjectResource project,
                                          RestResult<BankDetailsResource> bankDetailsResourceRestResult,
                                          boolean readOnlyView){
        OrganisationResource organisationResource = projectService.getOrganisationByProjectAndUser(project.getId(), loggedInUser.getId());
        BankDetailsViewModel bankDetailsViewModel = loadDataIntoModelResource(project, organisationResource);

        if(bankDetailsResourceRestResult.isSuccess()){
            model.addAttribute("bankDetails", bankDetailsResourceRestResult.getSuccessObject());
        }

        model.addAttribute("project", project);
        model.addAttribute("applicationId", project.getApplication());
        model.addAttribute("currentUser", loggedInUser);
        model.addAttribute("organisation", organisationResource);
        model.addAttribute(FORM_ATTR_NAME, form);
        model.addAttribute("readOnlyView", readOnlyView);
        model.addAttribute("model", bankDetailsViewModel);
    }

    private BankDetailsResource buildBankDetailsResource(Long projectId,
                                                     OrganisationResource organisation,
                                                     OrganisationAddressResource organisationAddressResource,
                                                     BankDetailsForm form){
        BankDetailsResource bankDetailsResource = new BankDetailsResource();
        bankDetailsResource.setAccountNumber(form.getAccountNumber());
        bankDetailsResource.setSortCode(form.getSortCode());
        bankDetailsResource.setProject(projectId);
        bankDetailsResource.setOrganisation(organisation.getId());
        bankDetailsResource.setCompanyName(organisation.getName());
        bankDetailsResource.setOrganisationTypeName(organisation.getOrganisationTypeName());
        bankDetailsResource.setRegistrationNumber(organisation.getCompanyHouseNumber());
        bankDetailsResource.setOrganisationAddress(organisationAddressResource);
        return bankDetailsResource;
    }

    private void populateExitingBankDetailsInForm(BankDetailsResource bankDetails, BankDetailsForm bankDetailsForm){
        OrganisationAddressResource organisationAddressResource = organisationAddressRestService.findOne(bankDetails.getOrganisationAddress().getId()).getSuccessObjectOrThrowException();
        bankDetailsForm.setAddressType(OrganisationAddressType.valueOf(organisationAddressResource.getAddressType().getName()));
        bankDetailsForm.setSortCode(bankDetails.getSortCode());
        bankDetailsForm.setAccountNumber(bankDetails.getAccountNumber());
    }

    private BankDetailsViewModel loadDataIntoModelResource(final ProjectResource project, final OrganisationResource organisationResource){
        BankDetailsViewModel bankDetailsViewModel = new BankDetailsViewModel(project);

        Optional<OrganisationAddressResource> registeredAddress = getAddress(organisationResource, REGISTERED);
        registeredAddress.ifPresent(organisationAddressResource -> bankDetailsViewModel.setRegisteredAddress(organisationAddressResource.getAddress()));

        Optional<OrganisationAddressResource> operatingAddress = getAddress(organisationResource, OPERATING);
        operatingAddress.ifPresent(organisationAddressResource -> bankDetailsViewModel.setOperatingAddress(organisationAddressResource.getAddress()));

        Optional<OrganisationAddressResource> bankAddress = getAddress(organisationResource, BANK_DETAILS);
        bankAddress.ifPresent(organisationAddressResource -> bankDetailsViewModel.setBankAddress(organisationAddressResource.getAddress()));

        return bankDetailsViewModel;
    }

    private String redirectToBankDetails(long projectId) {
        return "redirect:/project/" + projectId + "/bank-details";
    }
}
