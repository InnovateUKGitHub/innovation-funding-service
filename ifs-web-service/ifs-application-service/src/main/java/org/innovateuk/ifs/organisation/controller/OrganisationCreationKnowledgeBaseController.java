package org.innovateuk.ifs.organisation.controller;

import org.innovateuk.ifs.address.form.AddressForm;
import org.innovateuk.ifs.address.resource.AddressResource;
import org.innovateuk.ifs.address.service.AddressRestService;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.project.bankdetails.resource.BankDetailsResource;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.registration.form.KnowledgeBaseCreateForm;
import org.innovateuk.ifs.registration.form.KnowledgeBaseForm;
import org.innovateuk.ifs.registration.form.OrganisationCreationForm;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.KnowledgeBaseRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import static org.innovateuk.ifs.address.form.AddressForm.FORM_ACTION_PARAMETER;

/**
 * Provides methods for both:
 * - Finding your company or research type organisation through Companies House or JES search.
 * - Verifying or amending the address attached to the organisation.
 */
@Controller
@RequestMapping(AbstractOrganisationCreationController.BASE_URL + "/knowledge-base")
@SecuredBySpring(value = "Controller", description = "Applicants, support staff, innovation leads, stakeholders, comp admins and project finance users have permission to select knowledge base organisations.", securedType = OrganisationCreationKnowledgeBaseController.class)
@PreAuthorize("permitAll")
public class OrganisationCreationKnowledgeBaseController extends AbstractOrganisationCreationController {

    @Autowired
    private KnowledgeBaseRestService knowledgeBaseRestService;

    @Autowired
    private AddressRestService addressRestService;

    @GetMapping
    public String selectKnowledgeBase(@ModelAttribute(name = "form", binding = false) KnowledgeBaseForm organisationForm,
                                      Model model,
                                      HttpServletRequest request,
                                      UserResource user) {
        addPageSubtitleToModel(request, user, model);
        model.addAttribute("knowledgeBases", knowledgeBaseRestService.getKnowledgeBases().getSuccess());
        return "registration/organisation/knowledge-base";
    }

    @GetMapping("/details")
    public String knowledgeBaseDetails(@ModelAttribute(name = "form", binding = false) KnowledgeBaseCreateForm organisationForm,
                                       Model model,
                                       HttpServletRequest request,
                                       UserResource user) {
        addPageSubtitleToModel(request, user, model);
        return viewKnowledgeBaseDetails(organisationForm, model);
    }

    private String viewKnowledgeBaseDetails(KnowledgeBaseCreateForm organisationForm, Model model) {
        if (organisationForm.getAddressForm().isPostcodeAddressEntry()) {
            organisationForm.getAddressForm().setPostcodeResults(searchPostcode(organisationForm.getAddressForm().getPostcodeInput()));
        }

        model.addAttribute("form", organisationForm);

        return "registration/organisation/knowledge-base-details";
    }

    @PostMapping("/details")
    public String saveKnowledgeBaseDetails(@ModelAttribute(name = "form") KnowledgeBaseCreateForm organisationForm,
                                           BindingResult bindingResult,
                                           ValidationHandler validationHandler,
                                           Model model,
                                           HttpServletRequest request,
                                           HttpServletResponse response,
                                           UserResource user) {

        Supplier<String> failureView = () -> knowledgeBaseDetails(organisationForm, model, request, user);
        return validationHandler.failNowOrSucceedWith(failureView, () -> {
            // dont think needed
            OrganisationCreationForm organisationCreationForm = registrationCookieService.getOrganisationCreationCookieValue(request).get();
//            organisationCreationForm.setOrganisationName(organisationForm.getKnowledgeBase());
            registrationCookieService.saveToOrganisationCreationCookie(organisationCreationForm, response);
            return "redirect:" + AbstractOrganisationCreationController.BASE_URL + "/" + CONFIRM_ORGANISATION;
        });
    }

    @PostMapping(value = "/details", params = FORM_ACTION_PARAMETER)
    public String addressFormAction(Model model,
                                    @ModelAttribute(name = "form") KnowledgeBaseCreateForm organisationForm,
                                    BindingResult bindingResult,
                                    ValidationHandler validationHandler,
                                    HttpServletRequest request,
                                    UserResource user) {
//        OrganisationResource organisationResource = projectRestService.getOrganisationByProjectAndUser(projectId, loggedInUser.getId()).getSuccess();
//        RestResult<BankDetailsResource> bankDetailsResourceRestResult = bankDetailsRestService.getBankDetailsByProjectAndOrganisation(projectId, organisationResource.getId());

        organisationForm.getAddressForm().validateAction(bindingResult);
        if (validationHandler.hasErrors()) {
            return knowledgeBaseDetails(organisationForm, model, request, user);
        }

        AddressForm addressForm = organisationForm.getAddressForm();
        addressForm.handleAction(this::searchPostcode);

        return viewKnowledgeBaseDetails(organisationForm, model);
    }

    @PostMapping
    public String selectedKnowledgeBase(@Valid @ModelAttribute("form") KnowledgeBaseForm organisationForm,
                                        BindingResult bindingResult,
                                        ValidationHandler validationHandler,
                                        Model model,
                                        HttpServletRequest request,
                                        HttpServletResponse response,
                                        UserResource user) {
        Supplier<String> failureView = () -> selectKnowledgeBase(organisationForm, model, request, user);
        return validationHandler.failNowOrSucceedWith(failureView, () -> {
            OrganisationCreationForm organisationCreationForm = registrationCookieService.getOrganisationCreationCookieValue(request).get();
            organisationCreationForm.setOrganisationName(organisationForm.getKnowledgeBase());
            registrationCookieService.saveToOrganisationCreationCookie(organisationCreationForm, response);
            return "redirect:" + AbstractOrganisationCreationController.BASE_URL + "/" + CONFIRM_ORGANISATION;
        });
    }

    protected List<AddressResource> searchPostcode(String postcodeInput) {
        RestResult<List<AddressResource>> addressLookupRestResult = addressRestService.doLookup(postcodeInput);
        return addressLookupRestResult.handleSuccessOrFailure(
                failure -> new ArrayList<>(),
                addresses -> addresses);
    }
}
