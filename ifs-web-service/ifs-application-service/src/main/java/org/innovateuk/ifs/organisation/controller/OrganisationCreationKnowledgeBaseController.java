package org.innovateuk.ifs.organisation.controller;

import org.innovateuk.ifs.address.form.AddressForm;
import org.innovateuk.ifs.address.resource.AddressResource;
import org.innovateuk.ifs.address.resource.AddressTypeResource;
import org.innovateuk.ifs.address.service.AddressRestService;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.knowledgebase.resourse.KnowledgeBaseResource;
import org.innovateuk.ifs.knowledgebase.resourse.KnowledgeBaseType;
import org.innovateuk.ifs.organisation.resource.OrganisationAddressResource;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.organisation.resource.OrganisationTypeEnum;
import org.innovateuk.ifs.registration.form.KnowledgeBaseCreateForm;
import org.innovateuk.ifs.registration.form.KnowledgeBaseForm;
import org.innovateuk.ifs.registration.form.OrganisationTypeForm;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.KnowledgeBaseRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.address.form.AddressForm.FORM_ACTION_PARAMETER;
import static org.innovateuk.ifs.address.resource.OrganisationAddressType.KNOWLEDGE_BASE;

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
        model.addAttribute("types", KnowledgeBaseType.values());
        return "registration/organisation/knowledge-base-details";
    }

    @PostMapping("/details")
    public String saveKnowledgeBaseDetails(@ModelAttribute(name = "form") @Valid KnowledgeBaseCreateForm organisationForm,
                                           BindingResult bindingResult,
                                           ValidationHandler validationHandler,
                                           Model model,
                                           HttpServletRequest request,
                                           HttpServletResponse response,
                                           UserResource user) {

        Supplier<String> failureView = () -> knowledgeBaseDetails(organisationForm, model, request, user);
        return validationHandler.failNowOrSucceedWith(failureView, () -> {

            OrganisationTypeForm organisationTypeForm = new OrganisationTypeForm();
            organisationTypeForm.setOrganisationType(OrganisationTypeEnum.KNOWLEDGE_BASE.getId());
            registrationCookieService.saveToKnowledgeBaseDetailsCookie(organisationForm, response);
            registrationCookieService.saveToKnowledgeBaseAddressCookie(createAddressResource(organisationForm), response);
            registrationCookieService.saveToOrganisationTypeCookie(organisationTypeForm, response);
            return "redirect:" + AbstractOrganisationCreationController.BASE_URL + "/knowledge-base/confirm";
        });
    }

    @PostMapping(value = "/details", params = FORM_ACTION_PARAMETER)
    public String addressFormAction(Model model,
                                    @ModelAttribute(name = "form") KnowledgeBaseCreateForm organisationForm,
                                    BindingResult bindingResult,
                                    ValidationHandler validationHandler,
                                    HttpServletRequest request,
                                    UserResource user) {
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
            KnowledgeBaseResource knowledgeBaseResource = knowledgeBaseRestService.getKnowledgeBaseByName(organisationForm.getKnowledgeBase()).getSuccess();
            KnowledgeBaseCreateForm knowledgeBaseCreateForm = processCreationForm(knowledgeBaseResource);

            OrganisationTypeForm organisationTypeForm = new OrganisationTypeForm();
            organisationTypeForm.setOrganisationType(OrganisationTypeEnum.KNOWLEDGE_BASE.getId());

            registrationCookieService.saveToKnowledgeBaseDetailsCookie(knowledgeBaseCreateForm, response);
            registrationCookieService.saveToKnowledgeBaseAddressCookie(knowledgeBaseResource.getAddress(), response);
            registrationCookieService.saveToOrganisationTypeCookie(organisationTypeForm, response);
            return "redirect:" + AbstractOrganisationCreationController.BASE_URL + "/knowledge-base/confirm";
        });
    }

    private KnowledgeBaseCreateForm processCreationForm(KnowledgeBaseResource knowledgeBaseResource) {
        KnowledgeBaseCreateForm form = new KnowledgeBaseCreateForm();
        form.setName(knowledgeBaseResource.getName());
        form.setType(knowledgeBaseResource.getType());
        form.setIdentification(knowledgeBaseResource.getRegistrationNumber());
        AddressResource addressResource = knowledgeBaseResource.getAddress();
        form.getAddressForm().setPostcodeResults(singletonList(addressResource));
        form.getAddressForm().setSelectedPostcodeIndex(0);
        return form;

    }

    @GetMapping("/confirm")
    public String confirmKnowledgeBaseDetails(Model model,
                                              UserResource user,
                                              HttpServletRequest request) {
        Optional<OrganisationTypeForm> organisationTypeForm = registrationCookieService.getOrganisationTypeCookieValue(request);
        Optional<KnowledgeBaseCreateForm> knowledgeBaseCreateForm = registrationCookieService.getKnowledgeBaseDetailsValue(request);
        Optional<AddressResource> address = registrationCookieService.getKnowledgeBaseAddressCookie(request);
        model.addAttribute("type", knowledgeBaseCreateForm.isPresent() ? knowledgeBaseCreateForm.get().getType() : null);
        model.addAttribute("organisationName", knowledgeBaseCreateForm.isPresent() ? knowledgeBaseCreateForm.get().getName() : null);
        model.addAttribute("identification", knowledgeBaseCreateForm.isPresent() ? knowledgeBaseCreateForm.get().getIdentification() : null);
        model.addAttribute("address", address.orElse(null));
        model.addAttribute("isLeadApplicant", registrationCookieService.isLeadJourney(request));


        addPageSubtitleToModel(request, user, model);
        return TEMPLATE_PATH + "/" + KNOWLEDGE_BASE_CONFIRM_ORGANISATION;
    }

    @PostMapping("/save-organisation")
    public String saveOrganisation(UserResource userResource,
                                   HttpServletRequest request,
                                   HttpServletResponse response) {
        Optional<OrganisationTypeForm> organisationTypeForm = registrationCookieService.getOrganisationTypeCookieValue(request);
        Optional<KnowledgeBaseCreateForm> knowledgeBaseCreateForm = registrationCookieService.getKnowledgeBaseDetailsValue(request);
        Optional<AddressResource> address = registrationCookieService.getKnowledgeBaseAddressCookie(request);

        OrganisationResource organisationResource = new OrganisationResource();
        organisationResource.setName(knowledgeBaseCreateForm.get().getName());
        organisationResource.setOrganisationType(organisationTypeForm.get().getOrganisationType());
        organisationResource.setRegistrationNumber(knowledgeBaseCreateForm.get().getIdentification());
        organisationResource.setAddresses(singletonList(createOrganisationAddressResource(organisationResource, address.orElse(null))));

        organisationResource = organisationRestService.createOrMatch(organisationResource).getSuccess();

        return organisationJourneyEnd.completeProcess(request, response, userResource, organisationResource.getId());
    }

    private OrganisationAddressResource createOrganisationAddressResource(OrganisationResource organisationResource, AddressResource address) {
        return new OrganisationAddressResource(organisationResource, address, new AddressTypeResource(KNOWLEDGE_BASE.getId(), KNOWLEDGE_BASE.name()));
    }

    private AddressResource createAddressResource(KnowledgeBaseCreateForm form) {
        if (form.getAddressForm() != null) {
            AddressForm addressForm = form.getAddressForm();
            if (addressForm.isManualAddressEntry()) {
                return addressForm.getManualAddress();
            }
            return addressForm.getPostcodeResults().get(addressForm.getSelectedPostcodeIndex());
        }

        return null;
    }

    protected List<AddressResource> searchPostcode(String postcodeInput) {
        RestResult<List<AddressResource>> addressLookupRestResult = addressRestService.doLookup(postcodeInput);
        return addressLookupRestResult.handleSuccessOrFailure(
                failure -> new ArrayList<>(),
                addresses -> addresses);
    }
}
