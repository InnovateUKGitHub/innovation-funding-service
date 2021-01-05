package org.innovateuk.ifs.organisation.controller;

import org.innovateuk.ifs.address.resource.AddressResource;
import org.innovateuk.ifs.address.resource.AddressTypeResource;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.organisation.resource.OrganisationAddressResource;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.organisation.resource.OrganisationTypeEnum;
import org.innovateuk.ifs.registration.form.OrganisationCreationForm;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static java.util.Arrays.asList;
import static org.innovateuk.ifs.address.resource.OrganisationAddressType.REGISTERED;

/**
 * Provides methods for confirming and saving the organisation as an intermediate step in the registration flow.
 */
@Controller
@RequestMapping(AbstractOrganisationCreationController.BASE_URL)
@SecuredBySpring(value = "Controller",
        description = "Any user can confirm and save their organisation as part of registering their account",
        securedType = OrganisationCreationSaveController.class)
@PreAuthorize("permitAll")
public class OrganisationCreationSaveController extends AbstractOrganisationCreationController {

    @GetMapping("/" + CONFIRM_ORGANISATION)
    public String confirmOrganisation(@ModelAttribute(name = ORGANISATION_FORM, binding = false) OrganisationCreationForm organisationForm,
                                 Model model,
                                 HttpServletRequest request,
                                 UserResource user) {
        organisationForm = getImprovedSearchFormDataFromCookie(organisationForm, model, request, DEFAULT_PAGE_NUMBER_VALUE, false);
        addOrganisationType(organisationForm, organisationTypeIdFromCookie(request));
        addSelectedOrganisation(organisationForm, model);
        model.addAttribute(ORGANISATION_FORM, organisationForm);
        model.addAttribute("isApplicantJourney", registrationCookieService.isApplicantJourney(request));
        model.addAttribute("isLeadApplicant", registrationCookieService.isLeadJourney(request));
        model.addAttribute("organisationType", organisationTypeRestService.findOne(organisationForm.getOrganisationTypeId()).getSuccess());
        model.addAttribute("includeInternationalQuestion", registrationCookieService.getOrganisationInternationalCookieValue(request).isPresent());
        model.addAttribute("isImprovedSearchEnabled", isNewOrganisationSearchEnabled);
        addPageSubtitleToModel(request, user, model);
        return TEMPLATE_PATH + "/" + CONFIRM_ORGANISATION;
    }

    @PostMapping("/save-organisation")
    public String saveOrganisation(@ModelAttribute(name = ORGANISATION_FORM, binding = false) OrganisationCreationForm organisationForm,
                                   Model model,
                                   UserResource user,
                                   HttpServletRequest request,
                                   HttpServletResponse response) {
        organisationForm = getImprovedSearchFormDataFromCookie(organisationForm, model, request, DEFAULT_PAGE_NUMBER_VALUE, false);

        BindingResult bindingResult = new BeanPropertyBindingResult(organisationForm, ORGANISATION_FORM);
        validator.validate(organisationForm, bindingResult);

        //Ignore not null errors on organisationSearchName as its not relevant here. This is due to the same form being used.
        if (bindingResult.hasErrors() && (bindingResult.getAllErrors().size() != 1 || !bindingResult.hasFieldErrors("organisationSearchName"))) {
            return "redirect:/";
        }
        OrganisationResource organisationResource = getOrganisationResourceToPersist(organisationForm);
        organisationResource = organisationRestService.createOrMatch(organisationResource).getSuccess();
        return organisationJourneyEnd.completeProcess(request, response, user, organisationResource.getId());
    }

     private OrganisationResource getOrganisationResourceToPersist(OrganisationCreationForm organisationForm) {
        OrganisationResource organisationResource = new OrganisationResource();
        organisationResource.setName(organisationForm.getOrganisationName());
        organisationResource.setOrganisationType(organisationForm.getOrganisationTypeId());

        if (isNewOrganisationSearchEnabled)  {
            organisationResource.setDateOfIncorporation(organisationForm.getDateOfIncorporation());
            AddressResource addressResource = organisationForm.getOrganisationAddress();
            OrganisationAddressResource orgAddressResource = new OrganisationAddressResource(organisationResource, addressResource, new AddressTypeResource(REGISTERED.getId(), REGISTERED.name()));
            organisationResource.setAddresses(asList(orgAddressResource));
            organisationResource.setSicCodes(organisationForm.getSicCodes());
            organisationResource.setExecutiveOfficers(organisationForm.getExecutiveOfficers());
        }

        if (OrganisationTypeEnum.RESEARCH.getId() != organisationForm.getOrganisationTypeId()) {
            organisationResource.setCompaniesHouseNumber(organisationForm.getSearchOrganisationId());
        }
        return organisationResource;
    }

}
