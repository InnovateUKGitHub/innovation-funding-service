package org.innovateuk.ifs.registration.controller;

import org.innovateuk.ifs.application.service.CompetitionService;
import org.innovateuk.ifs.registration.form.OrganisationCreationForm;
import org.innovateuk.ifs.registration.form.OrganisationTypeForm;
import org.innovateuk.ifs.registration.populator.OrganisationCreationSelectTypePopulator;
import org.innovateuk.ifs.registration.viewmodel.OrganisationCreationSelectTypeViewModel;
import org.innovateuk.ifs.user.resource.OrganisationTypeEnum;
import org.innovateuk.ifs.user.resource.OrganisationTypeResource;
import org.innovateuk.ifs.user.service.OrganisationTypeRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

/**
 * Provides methods for picking an organisation type as a lead applicant after initialization or the registration process.
 */

@Controller
@RequestMapping(AbstractOrganisationCreationController.BASE_URL + "/" + AbstractOrganisationCreationController.LEAD_ORGANISATION_TYPE)
@PreAuthorize("permitAll")
public class OrganisationCreationLeadTypeController extends AbstractOrganisationCreationController {

    private static final String ORGANISATION_TYPE_ID = "organisationTypeId";

    protected static final String NOT_ELIGIBLE = "not-eligible";

    @Autowired
    private OrganisationCreationSelectTypePopulator organisationCreationSelectTypePopulator;

    @Autowired
    private OrganisationTypeRestService organisationTypeRestService;

    @Autowired
    private CompetitionService competitionService;

    @GetMapping
    public String selectOrganisationType(Model model,
                                         HttpServletRequest request) {
        model.addAttribute("model", organisationCreationSelectTypePopulator.populate());

        Optional<OrganisationCreationForm> organisationCreationFormCookie = registrationCookieService.getOrganisationCreationCookieValue(request);
        if (organisationCreationFormCookie.isPresent()) {
            model.addAttribute(ORGANISATION_FORM, organisationCreationFormCookie.get());
        } else {
            model.addAttribute(ORGANISATION_FORM, new OrganisationCreationForm());
        }


        return TEMPLATE_PATH + "/" + LEAD_ORGANISATION_TYPE;
    }

    @PostMapping
    public String confirmSelectOrganisationType(Model model,
                                                @Valid @ModelAttribute(ORGANISATION_FORM) OrganisationCreationForm organisationForm,
                                                BindingResult bindingResult,
                                                HttpServletRequest request,
                                                HttpServletResponse response) {

        Long organisationTypeId = organisationForm.getOrganisationTypeId();
        if (organisationTypeId == null ||
                !isValidLeadOrganisationType(organisationTypeId)) {
            bindingResult.addError(new FieldError(ORGANISATION_FORM, ORGANISATION_TYPE_ID, "Please select an organisation type."));
        }

        if (!bindingResult.hasFieldErrors(ORGANISATION_TYPE_ID)) {
            OrganisationTypeForm organisationTypeForm = registrationCookieService.getOrganisationTypeCookieValue(request).orElse(new OrganisationTypeForm());
            organisationTypeForm.setOrganisationType(organisationTypeId);
            registrationCookieService.saveToOrganisationTypeCookie(organisationTypeForm, response);
            saveOrganisationTypeToCreationForm(response, organisationTypeForm);

            if (!isAllowedToLeadApplication(organisationTypeId, request)) {
                return redirectToNotEligibleUrl();
            }

            return "redirect:" + BASE_URL + "/" + FIND_ORGANISATION;
        } else {
            organisationForm.setTriedToSave(true);
            OrganisationCreationSelectTypeViewModel selectOrgTypeViewModel = organisationCreationSelectTypePopulator.populate();
            model.addAttribute("model", selectOrgTypeViewModel);
            return TEMPLATE_PATH + "/" + LEAD_ORGANISATION_TYPE;
        }
    }

    private String redirectToNotEligibleUrl() {
        return "redirect:" + BASE_URL + "/" + AbstractOrganisationCreationController.LEAD_ORGANISATION_TYPE + "/" + NOT_ELIGIBLE;
    }

    @GetMapping(NOT_ELIGIBLE)
    public String showNotEligible(Model model, HttpServletRequest request) {
        return TEMPLATE_PATH + "/" + NOT_ELIGIBLE;
    }

    private boolean isAllowedToLeadApplication(Long organisationTypeId, HttpServletRequest request) {
        Optional<Long> competitionIdOpt = registrationCookieService.getCompetitionIdCookieValue(request);

        if (competitionIdOpt.isPresent()) {
            List<OrganisationTypeResource> organisationTypesAllowed = competitionService.getOrganisationTypes(competitionIdOpt.get());
            return organisationTypesAllowed.stream()
                    .map(organisationTypeResource -> organisationTypeResource.getId())
                    .anyMatch(aLong -> aLong.equals(organisationTypeId));
        }

        return Boolean.FALSE;
    }

    private boolean isValidLeadOrganisationType(Long organisationTypeId) {
        return OrganisationTypeEnum.getFromId(organisationTypeId) != null;
    }

    private void saveOrganisationTypeToCreationForm(HttpServletResponse response, OrganisationTypeForm organisationTypeForm) {
        OrganisationCreationForm newOrganisationCreationForm = new OrganisationCreationForm();
        newOrganisationCreationForm.setOrganisationTypeId(organisationTypeForm.getOrganisationType());
        registrationCookieService.saveToOrganisationCreationCookie(newOrganisationCreationForm, response);
    }
}
