package org.innovateuk.ifs.registration.controller;

import org.innovateuk.ifs.address.resource.AddressResource;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.registration.form.OrganisationInternationalDetailsForm;
import org.innovateuk.ifs.registration.form.OrganisationInternationalForm;
import org.innovateuk.ifs.registration.form.OrganisationTypeForm;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * Provides methods for picking an international organisation as part of the registration process.
 */
@Controller
@RequestMapping(AbstractOrganisationCreationController.BASE_URL + "/" + AbstractOrganisationCreationController.INTERNATIONAL_ORGANISATION)
@SecuredBySpring(value = "Controller", description = "Everyone has permission to selct if their organisation is international or not",
        securedType = OrganisationCreationInternationalController.class)
@PreAuthorize("permitAll")
public class OrganisationCreationInternationalController extends AbstractOrganisationCreationController {

    @GetMapping
    public String selectInternationalOrganisation(Model model,
                                         HttpServletRequest request,
                                         @ModelAttribute(ORGANISATION_FORM) OrganisationInternationalForm organisationInternationalForm) {
        Optional<Long> competitionIdOpt = registrationCookieService.getCompetitionIdCookieValue(request);
        model.addAttribute("competitionId", competitionIdOpt.orElse(null));

        return TEMPLATE_PATH + "/" + INTERNATIONAL_ORGANISATION;
    }

    @PostMapping
    public String confirmInternationalOrganisation(Model model,
                                                   @Valid @ModelAttribute(ORGANISATION_FORM) OrganisationInternationalForm organisationForm,
                                                   BindingResult bindingResult,
                                                   ValidationHandler validationHandler,
                                                   HttpServletRequest request,
                                                   HttpServletResponse response) {

        Supplier<String> failureView = () -> selectInternationalOrganisation(model, request, organisationForm);
        Supplier<String> successView = () -> {
            registrationCookieService.saveToOrganisationInternationalCookie(organisationForm, response);
            return "redirect:" + BASE_URL + "/" + LEAD_ORGANISATION_TYPE;
        };

        return validationHandler.failNowOrSucceedWith(failureView, successView);
    }

    @GetMapping("/details")
    public String internationalOrganisationDetails(Model model,
                                                   HttpServletRequest request,
                                                   @ModelAttribute(ORGANISATION_FORM) OrganisationInternationalDetailsForm organisationForm) {
        Optional<Long> competitionIdOpt = registrationCookieService.getCompetitionIdCookieValue(request);
        model.addAttribute("competitionId", competitionIdOpt.orElse(null));

        return TEMPLATE_PATH + "/" + INTERNATIONAL_ORGANISATION_DETAILS;
    }

    @PostMapping("/details")
    public String saveInternationalOrganisationDetails(Model model,
                                                       HttpServletRequest request,
                                                       HttpServletResponse response,
                                                       @Valid @ModelAttribute(ORGANISATION_FORM) OrganisationInternationalDetailsForm organisationForm,
                                                       BindingResult bindingResult,
                                                       ValidationHandler validationHandler) {

        Supplier<String> failureView = () -> internationalOrganisationDetails(model, request, organisationForm);
        Supplier<String> successView = () -> {
            registrationCookieService.saveToOrganisationInternationalDetailsCookie(organisationForm, response);
            return internationalOrganisationDetails(model, request, organisationForm);
        };

        return validationHandler.failNowOrSucceedWith(failureView, successView);
    }

    @GetMapping("/confirm")
    public String confirmInternationalOrganisationDetails(Model model,
                                                          HttpServletRequest request) {
        Optional<Long> competitionIdOpt = registrationCookieService.getCompetitionIdCookieValue(request);
        model.addAttribute("competitionId", competitionIdOpt.orElse(null));

        Optional<OrganisationTypeForm> organisationTypeForm = registrationCookieService.getOrganisationTypeCookieValue(request);
        Optional<OrganisationInternationalDetailsForm> organisationInternationalDetailsForm = registrationCookieService.getOrganisationInternationalDetailsValue(request);
        model.addAttribute("organisationType", organisationTypeForm.isPresent() ? organisationTypeRestService.findOne(organisationTypeForm.get().getOrganisationType()).getSuccess() : null);
        model.addAttribute("registrationNumber", organisationInternationalDetailsForm.isPresent() ? organisationInternationalDetailsForm.get().getCompanyRegistrationNumber() : null);
        model.addAttribute("address", createAddressResource(organisationInternationalDetailsForm));

        return TEMPLATE_PATH + "/" + INTERNATIONAL_ORGANISATION_DETAILS;
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
