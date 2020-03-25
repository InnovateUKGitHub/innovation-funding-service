package org.innovateuk.ifs.registration.controller;

import org.innovateuk.ifs.address.resource.AddressResource;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.organisation.resource.OrganisationTypeEnum;
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

import static org.innovateuk.ifs.address.resource.Countries.COUNTRIES;

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
        model.addAttribute("countries", COUNTRIES);

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
            return "redirect:" + BASE_URL + "/" + INTERNATIONAL_ORGANISATION + "/confirm";
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
        model.addAttribute("organisationName", organisationInternationalDetailsForm.isPresent() ? organisationInternationalDetailsForm.get().getName() : null);
        model.addAttribute("registrationNumber", organisationInternationalDetailsForm.isPresent() ? organisationInternationalDetailsForm.get().getCompanyRegistrationNumber() : null);
        model.addAttribute("address", createAddressResource(organisationInternationalDetailsForm));

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

        if (OrganisationTypeEnum.RESEARCH.getId() != organisationTypeForm.get().getOrganisationType()) {
            organisationResource.setInternationalRegistrationNumber(organisationInternationalDetailsForm.get().getCompanyRegistrationNumber());
        }

        organisationResource = organisationRestService.createOrMatch(organisationResource).getSuccess();

        return organisationJourneyEnd.completeProcess(request, response, userResource, organisationResource.getId());
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
