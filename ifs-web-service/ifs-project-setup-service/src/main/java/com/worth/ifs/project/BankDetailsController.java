package com.worth.ifs.project;

import com.worth.ifs.address.resource.OrganisationAddressType;
import com.worth.ifs.project.bankdetails.resource.BankDetailsResource;
import com.worth.ifs.project.bankdetails.service.BankDetailsRestService;
import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.controller.ValidationHandler;
import com.worth.ifs.form.AddressForm;
import com.worth.ifs.organisation.resource.OrganisationAddressResource;
import com.worth.ifs.organisation.service.OrganisationAddressRestService;
import com.worth.ifs.bankdetails.form.BankDetailsForm;
import com.worth.ifs.project.resource.ProjectResource;
import com.worth.ifs.project.viewmodel.BankDetailsViewModel;
import com.worth.ifs.user.resource.OrganisationResource;
import com.worth.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.validation.Valid;
import java.util.Optional;

import static com.worth.ifs.address.resource.OrganisationAddressType.*;
import static com.worth.ifs.controller.ErrorToObjectErrorConverterFactory.asGlobalErrors;

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
    @RequestMapping(method = RequestMethod.GET)
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
        return doViewBankDetails(model, form, projectResource, bankDetailsResourceRestResult, loggedInUser);
    }

    @PreAuthorize("hasPermission(#projectId, 'ACCESS_BANK_DETAILS_SECTION')")
    @RequestMapping(method = RequestMethod.POST)
    public String submitBankDetails(Model model,
                                    @Valid @ModelAttribute(FORM_ATTR_NAME) BankDetailsForm form,
                                    @SuppressWarnings("unused") BindingResult bindingResult,
                                    ValidationHandler validationHandler,
                                    @PathVariable("projectId") final Long projectId,
                                    @ModelAttribute("loggedInUser") UserResource loggedInUser) {

        if (isNewAddressNotValid(form)) {
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

    @PreAuthorize("hasPermission(#projectId, 'ACCESS_BANK_DETAILS_SECTION')")
    @RequestMapping(value = "/confirm", method = RequestMethod.POST)
    public String confirmBankDetails(Model model,
                                    @Valid @ModelAttribute(FORM_ATTR_NAME) BankDetailsForm form,
                                    BindingResult bindingResult,
                                    @PathVariable("projectId") final Long projectId,
                                    @ModelAttribute("loggedInUser") UserResource loggedInUser) {
        ProjectResource projectResource = projectService.getById(projectId);
        OrganisationResource organisationResource = projectService.getOrganisationByProjectAndUser(projectId, loggedInUser.getId());
        RestResult<BankDetailsResource> bankDetailsResourceRestResult = bankDetailsRestService.getBankDetailsByProjectAndOrganisation(projectId, organisationResource.getId());

        if (hasNonAddressErrors(bindingResult)) {
            form.setBindingResult(bindingResult);
            form.setObjectErrors(bindingResult.getAllErrors());
            return doViewBankDetails(model, form, projectResource, bankDetailsResourceRestResult, loggedInUser);
        }
        return doViewConfirmBankDetails(model, form, projectResource, bankDetailsResourceRestResult, loggedInUser);
    }

    @PreAuthorize("hasPermission(#projectId, 'ACCESS_BANK_DETAILS_SECTION')")
    @RequestMapping(params = SEARCH_ADDRESS, method = RequestMethod.POST)
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
        return doViewBankDetails(model, form, project, bankDetailsResourceRestResult, loggedInUser);
    }

    @PreAuthorize("hasPermission(#projectId, 'ACCESS_BANK_DETAILS_SECTION')")
    @RequestMapping(params = SELECT_ADDRESS, method = RequestMethod.POST)
    public String selectAddress(Model model,
                                @PathVariable("projectId") Long projectId,
                                @ModelAttribute(FORM_ATTR_NAME) BankDetailsForm form,
                                @ModelAttribute("loggedInUser") UserResource loggedInUser) {
        form.getAddressForm().setSelectedPostcode(null);
        ProjectResource project = projectService.getById(projectId);
        OrganisationResource organisationResource = projectService.getOrganisationByProjectAndUser(projectId, loggedInUser.getId());
        RestResult<BankDetailsResource> bankDetailsResourceRestResult = bankDetailsRestService.getBankDetailsByProjectAndOrganisation(projectId, organisationResource.getId());
        return doViewBankDetails(model, form, project, bankDetailsResourceRestResult, loggedInUser);
    }

    @PreAuthorize("hasPermission(#projectId, 'ACCESS_BANK_DETAILS_SECTION')")
    @RequestMapping(params = MANUAL_ADDRESS, method = RequestMethod.POST)
    public String manualAddress(Model model,
                                @ModelAttribute(FORM_ATTR_NAME) BankDetailsForm form,
                                @PathVariable("projectId") Long projectId,
                                @ModelAttribute("loggedInUser") UserResource loggedInUser) {
        AddressForm addressForm = form.getAddressForm();
        addressForm.setManualAddress(true);
        ProjectResource project = projectService.getById(projectId);
        OrganisationResource organisationResource = projectService.getOrganisationByProjectAndUser(projectId, loggedInUser.getId());
        RestResult<BankDetailsResource> bankDetailsResourceRestResult = bankDetailsRestService.getBankDetailsByProjectAndOrganisation(projectId, organisationResource.getId());
        return doViewBankDetails(model, form, project, bankDetailsResourceRestResult, loggedInUser);
    }

    private boolean isNewAddressNotValid(BankDetailsForm form) {

        return ( (form.getAddressForm().getSelectedPostcode() == null
                || StringUtils.isEmpty(form.getAddressForm().getSelectedPostcode().getAddressLine1())
                || StringUtils.isEmpty(form.getAddressForm().getSelectedPostcode().getPostcode())
                || StringUtils.isEmpty(form.getAddressForm().getSelectedPostcode().getTown())
        ) && OrganisationAddressType.ADD_NEW.name().equals(form.getAddressType().name()));
    }

    private String doViewBankDetails(Model model, BankDetailsForm form, ProjectResource projectResource, RestResult<BankDetailsResource> bankDetailsResourceRestResult, UserResource loggedInUser) {
        populateBankDetailsModel(model, form, loggedInUser, projectResource, bankDetailsResourceRestResult);
        processAddressLookupFields(form);
        return "project/bank-details";
    }

    private String doViewConfirmBankDetails(Model model, BankDetailsForm form, ProjectResource projectResource, RestResult<BankDetailsResource> bankDetailsResourceRestResult, UserResource loggedInUser) {
        populateBankDetailsModel(model, form, loggedInUser, projectResource, bankDetailsResourceRestResult);
        processAddressLookupFields(form);
        return "project/bank-details-confirm";
    }

    private void populateBankDetailsModel(Model model, BankDetailsForm form, UserResource loggedInUser, ProjectResource project, RestResult<BankDetailsResource> bankDetailsResourceRestResult){
        OrganisationResource organisationResource = projectService.getOrganisationByProjectAndUser(project.getId(), loggedInUser.getId());
        BankDetailsViewModel bankDetailsViewModel = loadDataIntoModelResource(project, organisationResource);

        if(bankDetailsResourceRestResult.isSuccess()){
            model.addAttribute("bankDetails", bankDetailsResourceRestResult.getSuccessObject());
        }

        model.addAttribute("project", project);
        model.addAttribute("currentUser", loggedInUser);
        model.addAttribute("organisation", organisationResource);
        model.addAttribute(FORM_ATTR_NAME, form);
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
        if(registeredAddress.isPresent()){
            bankDetailsViewModel.setRegisteredAddress(registeredAddress.get().getAddress());
        }

        Optional<OrganisationAddressResource> operatingAddress = getAddress(organisationResource, OPERATING);
        if(operatingAddress.isPresent()){
            bankDetailsViewModel.setOperatingAddress(operatingAddress.get().getAddress());
        }

        Optional<OrganisationAddressResource> bankAddress = getAddress(organisationResource, BANK_DETAILS);
        if(bankAddress.isPresent()){
            bankDetailsViewModel.setBankAddress(bankAddress.get().getAddress());
        }

        return bankDetailsViewModel;
    }

    private String redirectToBankDetails(long projectId) {
        return "redirect:/project/" + projectId + "/bank-details";
    }
}
