package org.innovateuk.ifs.organisation.controller;

import org.innovateuk.ifs.address.resource.AddressResource;
import org.innovateuk.ifs.address.resource.AddressTypeResource;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.organisation.resource.OrganisationAddressResource;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.registration.form.OrganisationInternationalDetailsForm;
import org.innovateuk.ifs.registration.form.OrganisationInternationalForm;
import org.innovateuk.ifs.registration.form.OrganisationTypeForm;
import org.innovateuk.ifs.user.resource.UserResource;
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
import java.util.Optional;
import java.util.function.Supplier;

import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.address.resource.Countries.COUNTRIES;
import static org.innovateuk.ifs.address.resource.OrganisationAddressType.INTERNATIONAL;

/**
 * Provides methods for picking an international organisation as part of the registration process.
 */
@Controller
@RequestMapping(AbstractOrganisationCreationController.BASE_URL + "/" + AbstractOrganisationCreationController.INTERNATIONAL_ORGANISATION)
@SecuredBySpring(value = "Controller", description = "Everyone has permission to create an international",
        securedType = OrganisationCreationInternationalController.class)
@PreAuthorize("permitAll")
public class OrganisationCreationInternationalController extends AbstractOrganisationCreationController {

    @GetMapping
    public String selectInternationalOrganisation(Model model,
                                                  HttpServletRequest request,
                                                  UserResource user,
                                                  @ModelAttribute(ORGANISATION_FORM) OrganisationInternationalForm organisationInternationalForm) {
        Optional<OrganisationInternationalForm> cookieForm = registrationCookieService.getOrganisationInternationalCookieValue(request);
        if (cookieForm.isPresent()) {
            model.addAttribute(ORGANISATION_FORM, cookieForm);
        }
        return viewInternationalSelect(model, request, user);
    }

    @PostMapping
    public String confirmInternationalOrganisation(Model model,
                                                   @Valid @ModelAttribute(ORGANISATION_FORM) OrganisationInternationalForm organisationForm,
                                                   BindingResult bindingResult,
                                                   ValidationHandler validationHandler,
                                                   HttpServletRequest request,
                                                   HttpServletResponse response,
                                                   UserResource user) {

        Supplier<String> failureView = () -> selectInternationalOrganisation(model, request, user, organisationForm);
        Supplier<String> successView = () -> {
            registrationCookieService.saveToOrganisationInternationalCookie(organisationForm, response);
            if (user != null) {
                return "redirect:/organisation/select";
            }
            return "redirect:/organisation/create/organisation-type";
        };

        return validationHandler.failNowOrSucceedWith(failureView, successView);
    }

    private String viewInternationalSelect(Model model,
                                           HttpServletRequest request,
                                           UserResource user) {
        addPageSubtitleToModel(request, user, model);
        return TEMPLATE_PATH + "/" + INTERNATIONAL_ORGANISATION;
    }

    @GetMapping("/details")
    public String internationalOrganisationDetails(Model model,
                                                   HttpServletRequest request,
                                                   UserResource user,
                                                   @ModelAttribute(ORGANISATION_FORM) OrganisationInternationalDetailsForm organisationForm) {
        Optional<OrganisationInternationalDetailsForm> cookieForm = registrationCookieService.getOrganisationInternationalDetailsValue(request);
        if (cookieForm.isPresent()) {
            model.addAttribute(ORGANISATION_FORM, cookieForm);
        }
        return viewInternationalDetails(model, request, user);

    }

    @PostMapping("/details")
    public String saveInternationalOrganisationDetails(Model model,
                                                       HttpServletRequest request,
                                                       HttpServletResponse response,
                                                       @Valid @ModelAttribute(ORGANISATION_FORM) OrganisationInternationalDetailsForm organisationForm,
                                                       BindingResult bindingResult,
                                                       ValidationHandler validationHandler,
                                                       UserResource user) {

        Supplier<String> failureView = () -> internationalOrganisationDetails(model, request, user, organisationForm);
        Supplier<String> successView = () -> {
            registrationCookieService.saveToOrganisationInternationalDetailsCookie(organisationForm, response);
            return "redirect:" + BASE_URL + "/" + INTERNATIONAL_ORGANISATION + "/confirm";
        };

        return validationHandler.failNowOrSucceedWith(failureView, successView);
    }

    private String viewInternationalDetails(Model model,
                                            HttpServletRequest request,
                                            UserResource user) {
        model.addAttribute("countries", COUNTRIES);
        addPageSubtitleToModel(request, user, model);
        return TEMPLATE_PATH + "/" + INTERNATIONAL_ORGANISATION_DETAILS;

    }

    @GetMapping("/confirm")
    public String confirmInternationalOrganisationDetails(Model model,
                                                          UserResource user,
                                                          HttpServletRequest request) {
        Optional<OrganisationTypeForm> organisationTypeForm = registrationCookieService.getOrganisationTypeCookieValue(request);
        Optional<OrganisationInternationalDetailsForm> organisationInternationalDetailsForm = registrationCookieService.getOrganisationInternationalDetailsValue(request);
        model.addAttribute("organisationType", organisationTypeForm.isPresent() ? organisationTypeRestService.findOne(organisationTypeForm.get().getOrganisationType()).getSuccess() : null);
        model.addAttribute("organisationName", organisationInternationalDetailsForm.isPresent() ? organisationInternationalDetailsForm.get().getName() : null);
        model.addAttribute("registrationNumber", organisationInternationalDetailsForm.isPresent() ? organisationInternationalDetailsForm.get().getCompanyRegistrationNumber() : null);
        model.addAttribute("address", createAddressResource(organisationInternationalDetailsForm));
        model.addAttribute("isApplicantJourney", registrationCookieService.isApplicantJourney(request));
        model.addAttribute("isLeadApplicant", registrationCookieService.isLeadJourney(request));

        addPageSubtitleToModel(request, user, model);
        return TEMPLATE_PATH + "/" + INTERNATIONAL_CONFIRM_ORGANISATION;
    }

    @PostMapping("/save-organisation")
    public String saveOrganisation(UserResource userResource,
                                   HttpServletRequest request,
                                   HttpServletResponse response) {
        Optional<OrganisationInternationalDetailsForm> organisationInternationalDetailsForm = registrationCookieService.getOrganisationInternationalDetailsValue(request);
        Optional<OrganisationTypeForm> organisationTypeForm = registrationCookieService.getOrganisationTypeCookieValue(request);

        OrganisationResource organisationResource = new OrganisationResource();
        organisationResource.setName(organisationInternationalDetailsForm.get().getName());
        organisationResource.setOrganisationType(organisationTypeForm.get().getOrganisationType());
        organisationResource.setInternational(true);
        organisationResource.setAddresses(singletonList(createOrganisationAddressResource(organisationResource, organisationInternationalDetailsForm)));
        organisationResource.setInternationalRegistrationNumber(organisationInternationalDetailsForm.get().getCompanyRegistrationNumber());

        organisationResource = organisationRestService.createOrMatch(organisationResource).getSuccess();

        return organisationJourneyEnd.completeProcess(request, response, userResource, organisationResource.getId());
    }

    private OrganisationAddressResource createOrganisationAddressResource(OrganisationResource organisationResource, Optional<OrganisationInternationalDetailsForm> organisationInternationalDetailsForm) {
        return new OrganisationAddressResource(organisationResource, createAddressResource(organisationInternationalDetailsForm), new AddressTypeResource(INTERNATIONAL.getId(), INTERNATIONAL.name()));
    }

    private AddressResource createAddressResource(Optional<OrganisationInternationalDetailsForm> organisationInternationalDetailsForm) {
        if (organisationInternationalDetailsForm.isPresent()) {
            return new AddressResource(organisationInternationalDetailsForm.get().getAddressLine1(),
                    organisationInternationalDetailsForm.get().getAddressLine2(),
                    organisationInternationalDetailsForm.get().getTown(),
                    organisationInternationalDetailsForm.get().getCountry(),
                    organisationInternationalDetailsForm.get().getZipCode());
        }

        return null;
    }
}
