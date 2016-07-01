package com.worth.ifs.project;

import com.worth.ifs.address.resource.AddressResource;
import com.worth.ifs.address.resource.AddressTypeResource;
import com.worth.ifs.address.resource.OrganisationAddressType;
import com.worth.ifs.address.service.AddressRestService;
import com.worth.ifs.application.form.AddressForm;
import com.worth.ifs.bankdetails.resource.BankDetailsResource;
import com.worth.ifs.bankdetails.service.BankDetailsRestService;
import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.controller.BindingResultTarget;
import com.worth.ifs.organisation.resource.OrganisationAddressResource;
import com.worth.ifs.organisation.service.OrganisationAddressRestService;
import com.worth.ifs.project.form.BankDetailsForm;
import com.worth.ifs.project.resource.ProjectResource;
import com.worth.ifs.project.viewmodel.BankDetailsViewModel;
import com.worth.ifs.user.resource.OrganisationResource;
import com.worth.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import static com.worth.ifs.address.resource.OrganisationAddressType.*;
import static com.worth.ifs.controller.RestFailuresToValidationErrorBindingUtils.bindAnyErrorsToField;
import static com.worth.ifs.project.BankDetailsUtils.getBankDetails;

/**
 * This controller will handle all requests that are related to project bank details.
 */
@Controller
@RequestMapping("/project/{projectId}/bank-details")
public class BankDetailsController {

    private static final String FORM_ATTR_NAME = "form";
    private static final String MANUAL_ADDRESS = "manual-address";
    private static final String SEARCH_ADDRESS = "search-address";
    private static final String SELECT_ADDRESS = "select-address";

    @Autowired
    private ProjectService projectService;

    @Autowired
    private BankDetailsRestService bankDetailsRestService;

    @Autowired
    private AddressRestService addressRestService;

    @Autowired
    private OrganisationAddressRestService organisationAddressRestService;

    @RequestMapping(method = RequestMethod.GET)
    public String bankDetails(Model model,
                              @PathVariable("projectId") final Long projectId,
                              @ModelAttribute("loggedInUser") UserResource loggedInUser,
                              @ModelAttribute(FORM_ATTR_NAME) BankDetailsForm form) {
        ProjectResource projectResource = projectService.getById(projectId);
        OrganisationResource organisationResource = projectService.getOrganisationByProjectAndUser(projectId, loggedInUser.getId());
        RestResult<BankDetailsResource> bankDetailsResourceRestResult = getBankDetails(projectId, organisationResource.getId(), bankDetailsRestService);
        BankDetailsResource bankDetailsResource = bankDetailsResourceRestResult.getSuccessObject();
        if(bankDetailsResource != null && bankDetailsResource.getId() != null) {
            populateExitingBankDetailsInForm(bankDetailsResource, form);
        }
        return doViewBankDetails(model, form, projectResource, bankDetailsResource, loggedInUser);
    }

    @RequestMapping(method = RequestMethod.POST)
    public String updateBankDetails(Model model,
                                    @Valid @ModelAttribute(FORM_ATTR_NAME) BankDetailsForm form,
                                    BindingResult bindingResult,
                                    @PathVariable("projectId") final Long projectId,
                                    @ModelAttribute("loggedInUser") UserResource loggedInUser) {
        ProjectResource projectResource = projectService.getById(projectId);
        OrganisationResource organisationResource = projectService.getOrganisationByProjectAndUser(projectId, loggedInUser.getId());
        RestResult<BankDetailsResource> bankDetailsResourceRestResult = getBankDetails(projectId, organisationResource.getId(),bankDetailsRestService);
        BankDetailsResource bankDetailsResource = bankDetailsResourceRestResult.getSuccessObject();

        if (hasNonAddressErrors(bindingResult)) {
            form.setBindingResult(bindingResult);
            form.setObjectErrors(bindingResult.getAllErrors());
            return doViewBankDetails(model, form, projectResource, bankDetailsResource, loggedInUser);
        }
        OrganisationAddressResource organisationAddressResource = null;
        switch (form.getAddressType()) {
            case REGISTERED:
            case OPERATING:
            case BANK_DETAILS:
                Optional<OrganisationAddressResource> organisationAddress = getAddress(organisationResource, form.getAddressType());
                if (organisationAddress.isPresent()) {
                    organisationAddressResource = organisationAddress.get();
                }
                break;
            case ADD_NEW:
                if (hasManualAddressErrors(bindingResult)) {
                    form.setBindingResult(bindingResult);
                    form.setObjectErrors(bindingResult.getAllErrors());
                    return doViewBankDetails(model, form, projectResource, bankDetailsResource, loggedInUser);
                }
                form.getAddressForm().setTriedToSave(true);
                AddressResource newAddressResource = form.getAddressForm().getSelectedPostcode();
                organisationAddressResource = new OrganisationAddressResource(organisationResource, newAddressResource, new AddressTypeResource((long)BANK_DETAILS.ordinal(), BANK_DETAILS.name()));
                break;
            default:
                organisationAddressResource = null;
                break;
        }
        bankDetailsResource = buildBankDetailsFrom(projectId, organisationResource.getId(), organisationAddressResource, form);
        ServiceResult<Void> updateResult = bankDetailsRestService.updateBankDetails(projectId, bankDetailsResource).toServiceResult();
        return handleErrorsOrRedirectToProjectOverview("", projectId, model, form, bindingResult, updateResult, () -> bankDetails(model, projectId, loggedInUser, form));
    }

    private boolean hasNonAddressErrors(BindingResult bindingResult){
        return bindingResult.getFieldErrors().stream().filter(e -> (!e.getField().contains("addressForm"))).count() > 0;
    }

    private boolean hasManualAddressErrors(BindingResult bindingResult){
        return bindingResult.getFieldErrors().stream().filter(e -> e.getField().contains("addressForm")).count() > 0;
    }

    @RequestMapping(params = SEARCH_ADDRESS, method = RequestMethod.POST)
    public String searchAddress(Model model,
                                @PathVariable("projectId") Long projectId,
                                @Valid @ModelAttribute(FORM_ATTR_NAME) BankDetailsForm form,
                                @ModelAttribute("loggedInUser") UserResource loggedInUser) {
        form.getAddressForm().setSelectedPostcodeIndex(null);
        form.getAddressForm().setTriedToSearch(true);
        form.setAddressType(OrganisationAddressType.valueOf(form.getAddressType().name()));
        ProjectResource project = projectService.getById(projectId);
        OrganisationResource organisationResource = projectService.getOrganisationByProjectAndUser(projectId, loggedInUser.getId());
        RestResult<BankDetailsResource> bankDetailsResourceRestResult = getBankDetails(projectId, organisationResource.getId(), bankDetailsRestService);
        return doViewBankDetails(model, form, project, bankDetailsResourceRestResult.getSuccessObject(), loggedInUser);
    }

    @RequestMapping(params = SELECT_ADDRESS, method = RequestMethod.POST)
    public String selectAddress(Model model,
                                @PathVariable("projectId") Long projectId,
                                @ModelAttribute(FORM_ATTR_NAME) BankDetailsForm form,
                                @ModelAttribute("loggedInUser") UserResource loggedInUser) {
        form.getAddressForm().setSelectedPostcode(null);
        ProjectResource project = projectService.getById(projectId);
        OrganisationResource organisationResource = projectService.getOrganisationByProjectAndUser(projectId, loggedInUser.getId());
        RestResult<BankDetailsResource> bankDetailsResource = getBankDetails(projectId, organisationResource.getId(), bankDetailsRestService);
        return doViewBankDetails(model, form, project, bankDetailsResource.getSuccessObject(), loggedInUser);
    }

    @RequestMapping(params = MANUAL_ADDRESS, method = RequestMethod.POST)
    public String manualAddress(Model model,
                                @ModelAttribute(FORM_ATTR_NAME) BankDetailsForm form,
                                @PathVariable("projectId") Long projectId,
                                @ModelAttribute("loggedInUser") UserResource loggedInUser) {
        AddressForm addressForm = form.getAddressForm();
        addressForm.setManualAddress(true);
        ProjectResource project = projectService.getById(projectId);
        OrganisationResource organisationResource = projectService.getOrganisationByProjectAndUser(projectId, loggedInUser.getId());
        RestResult<BankDetailsResource> bankDetailsResourceRestResult = getBankDetails(projectId, organisationResource.getId(), bankDetailsRestService);
        return doViewBankDetails(model, form, project, bankDetailsResourceRestResult.getSuccessObject(), loggedInUser);
    }

    private String doViewBankDetails(Model model, BankDetailsForm form, ProjectResource projectResource, BankDetailsResource bankDetailsResource, UserResource loggedInUser) {
        populateBankDetailsModel(model, form, loggedInUser, projectResource, bankDetailsResource);
        processAddressLookupFields(form);
        return "project/bank-details";
    }

    private void populateBankDetailsModel(Model model, BankDetailsForm form, UserResource loggedInUser, ProjectResource project, BankDetailsResource bankDetails){
        OrganisationResource organisationResource = projectService.getOrganisationByProjectAndUser(project.getId(), loggedInUser.getId());
        BankDetailsViewModel bankDetailsViewModel = loadDataIntoModelResource(project, organisationResource);

        model.addAttribute("project", project);
        model.addAttribute("currentUser", loggedInUser);
        model.addAttribute("organisation", organisationResource);
        model.addAttribute(FORM_ATTR_NAME, form);
        model.addAttribute("bankDetails", bankDetails);
        model.addAttribute("model", bankDetailsViewModel);
    }

    private BankDetailsResource buildBankDetailsFrom(Long projectId,
                                                     Long organisationId,
                                                     OrganisationAddressResource organisationAddressResource,
                                                     BankDetailsForm form){
        BankDetailsResource bankDetailsResource = new BankDetailsResource();
        bankDetailsResource.setAccountNumber(form.getAccountNumber());
        bankDetailsResource.setSortCode(form.getSortCode());
        bankDetailsResource.setProject(projectId);
        bankDetailsResource.setOrganisation(organisationId);
        bankDetailsResource.setOrganisationAddress(organisationAddressResource);
        return bankDetailsResource;
    }

    private void processAddressLookupFields(BankDetailsForm form) {
        addAddressOptions(form);
        addSelectedAddress(form);
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

    private Optional<OrganisationAddressResource> getAddress(final OrganisationResource organisation, final OrganisationAddressType addressType) {
        return organisation.getAddresses().stream().filter(a -> OrganisationAddressType.valueOf(a.getAddressType().getName()).equals(addressType)).findFirst();
    }

    /**
     * Get the list of postcode options, with the entered postcode. Add those results to the form.
     */
    private void addAddressOptions(BankDetailsForm bankDetailsForm) {
        if (StringUtils.hasText(bankDetailsForm.getAddressForm().getPostcodeInput())) {
            AddressForm addressForm = bankDetailsForm.getAddressForm();
            addressForm.setPostcodeOptions(searchPostcode(bankDetailsForm.getAddressForm().getPostcodeInput()));
            addressForm.setPostcodeInput(bankDetailsForm.getAddressForm().getPostcodeInput());
        }
    }

    /**
     * if user has selected a address from the dropdown, get it from the list, and set it as selected.
     */
    private void addSelectedAddress(BankDetailsForm bankDetailsForm) {
        AddressForm addressForm = bankDetailsForm.getAddressForm();
        if (StringUtils.hasText(addressForm.getSelectedPostcodeIndex()) && addressForm.getSelectedPostcode() == null) {
            addressForm.setSelectedPostcode(addressForm.getPostcodeOptions().get(Integer.parseInt(addressForm.getSelectedPostcodeIndex())));
        }
    }

    private List<AddressResource> searchPostcode(String postcodeInput) {
        RestResult<List<AddressResource>> addressLookupRestResult = addressRestService.doLookup(postcodeInput);
        return addressLookupRestResult.handleSuccessOrFailure(
                failure -> new ArrayList<>(),
                addresses -> addresses);
    }

    private String handleErrorsOrRedirectToProjectOverview(
            String fieldName, long projectId, Model model,
            BindingResultTarget form, BindingResult bindingResult,
            ServiceResult<?> result,
            Supplier<String> viewSupplier) {

        if (result.isFailure()) {
            bindAnyErrorsToField(result, fieldName, bindingResult, form);
            model.addAttribute(FORM_ATTR_NAME, form);
            return viewSupplier.get();
        }

        return redirectToBankDetails(projectId);
    }

    private String redirectToBankDetails(long projectId) {
        return "redirect:/project/" + projectId + "/bank-details";
    }
}
