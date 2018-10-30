package org.innovateuk.ifs.project.bankdetails.controller;

import org.innovateuk.ifs.address.form.AddressForm;
import org.innovateuk.ifs.address.resource.AddressResource;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.organisation.service.OrganisationAddressRestService;
import org.innovateuk.ifs.project.AddressLookupBaseController;
import org.innovateuk.ifs.project.ProjectService;
import org.innovateuk.ifs.project.bankdetails.form.BankDetailsForm;
import org.innovateuk.ifs.project.bankdetails.resource.BankDetailsResource;
import org.innovateuk.ifs.project.bankdetails.service.BankDetailsRestService;
import org.innovateuk.ifs.project.bankdetails.viewmodel.BankDetailsViewModel;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.service.ProjectRestService;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.method.P;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.function.Supplier;

import static org.innovateuk.ifs.address.form.AddressForm.FORM_ACTION_PARAMETER;
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
    private ProjectRestService projectRestService;

    @Autowired
    private BankDetailsRestService bankDetailsRestService;

    @Autowired
    private OrganisationAddressRestService organisationAddressRestService;

    @PreAuthorize("hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.ProjectCompositeId', 'ACCESS_BANK_DETAILS_SECTION')")
    @GetMapping
    public String bankDetails(Model model,
                              @P("projectId")@PathVariable("projectId") final Long projectId,
                              UserResource loggedInUser,
                              @ModelAttribute(name = FORM_ATTR_NAME, binding = false) BankDetailsForm form) {
        ProjectResource projectResource = projectService.getById(projectId);
        OrganisationResource organisationResource = projectRestService.getOrganisationByProjectAndUser(projectId, loggedInUser.getId()).getSuccess();
        RestResult<BankDetailsResource> bankDetailsResourceRestResult = bankDetailsRestService.getBankDetailsByProjectAndOrganisation(projectId, organisationResource.getId());
        if(bankDetailsResourceRestResult.isSuccess()) {
            BankDetailsResource bankDetailsResource = bankDetailsResourceRestResult.getSuccess();
            populateExitingBankDetailsInForm(bankDetailsResource, form);
        }
        return doViewBankDetails(model, form, projectResource, bankDetailsResourceRestResult, loggedInUser, false);
    }

    @PreAuthorize("hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.ProjectCompositeId', 'ACCESS_BANK_DETAILS_SECTION')")
    @GetMapping("readonly")
    public String bankDetailsAsReadOnly(Model model,
                              @P("projectId")@PathVariable("projectId") final Long projectId,
                              UserResource loggedInUser,
                              @ModelAttribute(name = FORM_ATTR_NAME, binding = false) BankDetailsForm form) {
        ProjectResource projectResource = projectService.getById(projectId);
        OrganisationResource organisationResource = projectRestService.getOrganisationByProjectAndUser(projectId, loggedInUser.getId()).getSuccess();
        RestResult<BankDetailsResource> bankDetailsResourceRestResult = bankDetailsRestService.getBankDetailsByProjectAndOrganisation(projectId, organisationResource.getId());
        if(bankDetailsResourceRestResult.isSuccess()) {
            BankDetailsResource bankDetailsResource = bankDetailsResourceRestResult.getSuccess();
            populateExitingBankDetailsInForm(bankDetailsResource, form);
        }
        return doViewBankDetails(model, form, projectResource, bankDetailsResourceRestResult, loggedInUser, true);
    }


    @PreAuthorize("hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.ProjectCompositeId', 'ACCESS_BANK_DETAILS_SECTION')")
    @PostMapping
    public String submitBankDetails(Model model,
                                    @Valid @ModelAttribute(FORM_ATTR_NAME) BankDetailsForm form,
                                    @SuppressWarnings("unused") BindingResult bindingResult,
                                    ValidationHandler validationHandler,
                                    @P("projectId")@PathVariable("projectId") final Long projectId,
                                    UserResource loggedInUser) {

        final Supplier<String> failureView = () -> bankDetails(model, projectId, loggedInUser, form);

        return validationHandler.failNowOrSucceedWith(failureView,
                () -> {
                    OrganisationResource organisationResource = projectRestService.getOrganisationByProjectAndUser(projectId, loggedInUser.getId()).getSuccess();
                    AddressResource address = form.getAddressForm().getSelectedAddress(this::searchPostcode);

                    BankDetailsResource bankDetailsResource = buildBankDetailsResource(projectId, organisationResource, address, form);
                    ServiceResult<Void> updateResult = bankDetailsRestService.submitBankDetails(projectId, bankDetailsResource).toServiceResult();

                    if (updateResult.isFailure()) {
                        validationHandler.addAnyErrors(updateResult, asGlobalErrors());
                        return bankDetails(model, projectId, loggedInUser, form);
                    }

                    return redirectToBankDetails(projectId);
                }
        );
    }

    @PreAuthorize("hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.ProjectCompositeId', 'ACCESS_BANK_DETAILS_SECTION')")
    @PostMapping("/confirm")
    public String confirmBankDetails(Model model,
                                     @Valid @ModelAttribute(FORM_ATTR_NAME) BankDetailsForm form,
                                     BindingResult bindingResult,
                                     ValidationHandler validationHandler,
                                     @P("projectId")@PathVariable("projectId") final Long projectId,
                                     UserResource loggedInUser) {
        ProjectResource projectResource = projectService.getById(projectId);
        OrganisationResource organisationResource = projectRestService.getOrganisationByProjectAndUser(projectId, loggedInUser.getId()).getSuccess();
        RestResult<BankDetailsResource> bankDetailsResourceRestResult = bankDetailsRestService.getBankDetailsByProjectAndOrganisation(projectId, organisationResource.getId());

        final Supplier<String> failureView = () -> doViewBankDetails(model, form, projectResource, bankDetailsResourceRestResult, loggedInUser, false);

        return validationHandler.failNowOrSucceedWith(failureView,
                () -> doViewConfirmBankDetails(model, form, projectResource, bankDetailsResourceRestResult, loggedInUser));
    }

    @PreAuthorize("hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.ProjectCompositeId', 'ACCESS_BANK_DETAILS_SECTION')")
    @PostMapping(params = FORM_ACTION_PARAMETER)
    public String searchAddress(Model model,
                                @P("projectId")@PathVariable("projectId") Long projectId,
                                @ModelAttribute(FORM_ATTR_NAME) BankDetailsForm form,
                                BindingResult bindingResult,
                                ValidationHandler validationHandler,
                                UserResource loggedInUser) {
        ProjectResource project = projectService.getById(projectId);
        OrganisationResource organisationResource = projectRestService.getOrganisationByProjectAndUser(projectId, loggedInUser.getId()).getSuccess();
        RestResult<BankDetailsResource> bankDetailsResourceRestResult = bankDetailsRestService.getBankDetailsByProjectAndOrganisation(projectId, organisationResource.getId());

        form.getAddressForm().validateAction(bindingResult);
        if (validationHandler.hasErrors()) {
            return doViewBankDetails(model, form, project, bankDetailsResourceRestResult, loggedInUser, false);
        }

        AddressForm addressForm = form.getAddressForm();
        addressForm.handleAction(this::searchPostcode);

        return doViewBankDetails(model, form, project, bankDetailsResourceRestResult, loggedInUser, false);
    }


    private String doViewBankDetails(Model model, BankDetailsForm form, ProjectResource projectResource,
                                     RestResult<BankDetailsResource> bankDetailsResourceRestResult,
                                     UserResource loggedInUser,
                                     boolean isReadOnly) {
        populateBankDetailsModel(model, form, loggedInUser, projectResource, bankDetailsResourceRestResult, isReadOnly);
        return "project/bank-details";
    }

    private String doViewConfirmBankDetails(Model model, BankDetailsForm form, ProjectResource projectResource,
                                            RestResult<BankDetailsResource> bankDetailsResourceRestResult,
                                            UserResource loggedInUser) {
        populateBankDetailsModel(model, form, loggedInUser, projectResource,
                bankDetailsResourceRestResult, false);
        return "project/bank-details-confirm";
    }

    private void populateBankDetailsModel(Model model, BankDetailsForm form, UserResource loggedInUser,
                                          ProjectResource project,
                                          RestResult<BankDetailsResource> bankDetailsResourceRestResult,
                                          boolean readOnlyView){
        OrganisationResource organisationResource = projectRestService.getOrganisationByProjectAndUser(project.getId(), loggedInUser.getId()).getSuccess();
        BankDetailsViewModel bankDetailsViewModel = loadDataIntoModelResource(project, organisationResource);

        if(bankDetailsResourceRestResult.isSuccess()){
            model.addAttribute("bankDetails", bankDetailsResourceRestResult.getSuccess());
        }

        if (!readOnlyView && form.getAddressForm().isPostcodeAddressEntry()) {
            form.getAddressForm().setPostcodeResults(searchPostcode(form.getAddressForm().getPostcodeInput()));
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
                                                     AddressResource addressResource,
                                                     BankDetailsForm form){
        BankDetailsResource bankDetailsResource = new BankDetailsResource();
        bankDetailsResource.setAccountNumber(form.getAccountNumber());
        bankDetailsResource.setSortCode(form.getSortCode());
        bankDetailsResource.setProject(projectId);
        bankDetailsResource.setOrganisation(organisation.getId());
        bankDetailsResource.setCompanyName(organisation.getName());
        bankDetailsResource.setOrganisationTypeName(organisation.getOrganisationTypeName());
        bankDetailsResource.setRegistrationNumber(organisation.getCompaniesHouseNumber());
        bankDetailsResource.setAddress(addressResource);
        return bankDetailsResource;
    }

    private void populateExitingBankDetailsInForm(BankDetailsResource bankDetails, BankDetailsForm bankDetailsForm){
        bankDetailsForm.setSortCode(bankDetails.getSortCode());
        bankDetailsForm.setAccountNumber(bankDetails.getAccountNumber());
    }

    private BankDetailsViewModel loadDataIntoModelResource(final ProjectResource project, final OrganisationResource organisationResource) {
        return new BankDetailsViewModel(project);
    }

    private String redirectToBankDetails(long projectId) {
        return "redirect:/project/" + projectId + "/bank-details";
    }
}
