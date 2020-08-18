package org.innovateuk.ifs.organisation.controller;

import org.innovateuk.ifs.address.form.AddressForm;
import org.innovateuk.ifs.address.resource.AddressResource;
import org.innovateuk.ifs.address.resource.AddressTypeResource;
import org.innovateuk.ifs.address.service.AddressRestService;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.competition.publiccontent.resource.FundingType;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.organisation.resource.OrganisationAddressResource;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.organisation.resource.OrganisationTypeEnum;
import org.innovateuk.ifs.organisation.resource.OrganisationTypeResource;
import org.innovateuk.ifs.organisation.viewmodel.OrganisationCreationSelectTypeViewModel;
import org.innovateuk.ifs.project.bankdetails.resource.BankDetailsResource;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.registration.form.*;
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
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.address.form.AddressForm.FORM_ACTION_PARAMETER;
import static org.innovateuk.ifs.address.resource.OrganisationAddressType.INTERNATIONAL;
import static org.innovateuk.ifs.organisation.resource.OrganisationTypeEnum.*;
import static org.innovateuk.ifs.organisation.resource.OrganisationTypeEnum.UNIVERSITY;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleFilter;

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
        model.addAttribute("types", getOrganisationTypes());
        return "registration/organisation/knowledge-base-details";
    }

    private List<OrganisationTypeResource> getOrganisationTypes() {

        EnumSet<OrganisationTypeEnum> allowedTypes = knowledgeBaseLeadTypes;

        List<OrganisationTypeResource> organisationTypeResourceList = organisationTypeRestService.getAll().getSuccess()
                .stream()
                .filter(resource -> allowedTypes.contains(OrganisationTypeEnum.getFromId(resource.getId())))
                .collect(Collectors.toList());

        return simpleFilter(organisationTypeResourceList,
                        o -> OrganisationTypeEnum.getFromId(o.getId()) != null);

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
            registrationCookieService.saveToKnowledgeBaseDetailsCookie(organisationForm, response);
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
            OrganisationCreationForm organisationCreationForm = registrationCookieService.getOrganisationCreationCookieValue(request).get();
            organisationCreationForm.setOrganisationName(organisationForm.getKnowledgeBase());
            registrationCookieService.saveToOrganisationCreationCookie(organisationCreationForm, response);
            return "redirect:" + AbstractOrganisationCreationController.BASE_URL + "/" + CONFIRM_ORGANISATION;
        });
    }

    @GetMapping("/confirm")
    public String confirmKnowledgeBaseDetails(Model model,
                                                          UserResource user,
                                                          HttpServletRequest request) {
        Optional<OrganisationTypeForm> organisationTypeForm = registrationCookieService.getOrganisationTypeCookieValue(request);
        Optional<KnowledgeBaseCreateForm> knowledgeBaseCreateForm = registrationCookieService.getKnowledgeBaseDetailsValue(request);
        model.addAttribute("knowledgeBaseType", organisationTypeForm.isPresent() ? organisationTypeRestService.findOne(organisationTypeForm.get().getOrganisationType()).getSuccess() : null);
        model.addAttribute("organisationName", knowledgeBaseCreateForm.isPresent() ? knowledgeBaseCreateForm.get().getName() : null);
        model.addAttribute("identification", knowledgeBaseCreateForm.isPresent() ? knowledgeBaseCreateForm.get().getIdentification() : null);
        model.addAttribute("address", createAddressResource(knowledgeBaseCreateForm));
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

        OrganisationResource organisationResource = new OrganisationResource();
        organisationResource.setName(knowledgeBaseCreateForm.get().getName());
        organisationResource.setOrganisationType(organisationTypeForm.get().getOrganisationType());
        organisationResource.setInternational(false);
//        organisationResource.setAddresses(singletonList(createOrganisationAddressResource(knowledgeBaseCreateForm));

        organisationResource = organisationRestService.createOrMatch(organisationResource).getSuccess();

        return organisationJourneyEnd.completeProcess(request, response, userResource, organisationResource.getId());
    }

//    private OrganisationAddressResource createOrganisationAddressResource(OrganisationResource organisationResource, Optional<KnowledgeBaseCreateForm> knowledgeBaseCreateForm) {
//        return new OrganisationAddressResource(organisationResource, createAddressResource(knowledgeBaseCreateForm), new AddressTypeResource(INTERNATIONAL.getId(), INTERNATIONAL.name()));
//    }

    private AddressResource createAddressResource(Optional<KnowledgeBaseCreateForm> form) {
        if (form.isPresent() && form.get().getAddressForm() != null) {
            AddressForm addressForm = form.get().getAddressForm();
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
