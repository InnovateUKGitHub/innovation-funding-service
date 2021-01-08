package org.innovateuk.ifs.organisation.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.organisation.resource.OrganisationTypeEnum;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.registration.form.OrganisationCreationForm;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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

    private static final Log LOG = LogFactory.getLog(OrganisationCreationSaveController.class);
    @GetMapping("/" + CONFIRM_ORGANISATION)
    public String confirmOrganisation(@ModelAttribute(name = ORGANISATION_FORM, binding = false) OrganisationCreationForm organisationForm,
                                 Model model,
                                 HttpServletRequest request,
                                 UserResource user) {
        organisationForm = getFormDataFromCookie(organisationForm, model, request);
        addOrganisationType(organisationForm, organisationTypeIdFromCookie(request));
        addSelectedOrganisation(organisationForm, model);
        model.addAttribute(ORGANISATION_FORM, organisationForm);
        model.addAttribute("isApplicantJourney", registrationCookieService.isApplicantJourney(request));
        model.addAttribute("isLeadApplicant", registrationCookieService.isLeadJourney(request));
        model.addAttribute("organisationType", organisationTypeRestService.findOne(organisationForm.getOrganisationTypeId()).getSuccess());
        model.addAttribute("includeInternationalQuestion", registrationCookieService.getOrganisationInternationalCookieValue(request).isPresent());
        addPageSubtitleToModel(request, user, model);
        return TEMPLATE_PATH + "/" + CONFIRM_ORGANISATION;
    }

    @PostMapping("/save-organisation")
    public String saveOrganisation(@ModelAttribute(name = ORGANISATION_FORM, binding = false) OrganisationCreationForm organisationForm,
                                   Model model,
                                   UserResource user,
                                   HttpServletRequest request,
                                   HttpServletResponse response) {
        organisationForm = getFormDataFromCookie(organisationForm, model, request);

        BindingResult bindingResult = new BeanPropertyBindingResult(organisationForm, ORGANISATION_FORM);
        validator.validate(organisationForm, bindingResult);

        //Ignore not null errors on organisationSearchName as its not relevant here. This is due to the same form being used.
        if (bindingResult.hasErrors() && (bindingResult.getAllErrors().size() != 1 || !bindingResult.hasFieldErrors("organisationSearchName"))) {
            return "redirect:/";
        }

        OrganisationResource organisationResource = new OrganisationResource();
        organisationResource.setName(organisationForm.getOrganisationName());
        organisationResource.setOrganisationType(organisationForm.getOrganisationTypeId());

        if (OrganisationTypeEnum.RESEARCH.getId() != organisationForm.getOrganisationTypeId()) {
            organisationResource.setCompaniesHouseNumber(organisationForm.getSearchOrganisationId());
        }

        organisationResource = organisationRestService.createOrMatch(organisationResource).getSuccess();

        return organisationJourneyEnd.completeProcess(request, response, user, organisationResource.getId());
    }

   // @PreAuthorize("hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.ProjectCompositeId', 'ACCESS_PROJECT_ADDRESS_PAGE')")
    @GetMapping("/organisation/create/organisation-type/manually-enter-organisation-details/")
    public String viewAddress(Model model,
                              @ModelAttribute(name = ORGANISATION_FORM, binding = false) OrganisationCreationForm form) {
        LOG.info("Here");
        model.addAttribute("model", model);
        return "organisation/details-address";
    }
}
