package org.innovateuk.ifs.eugrant.organisation.controller;

import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.eugrant.organisation.form.OrganisationCreationForm;
import org.innovateuk.ifs.eugrant.organisation.form.OrganisationTypeForm;
import org.innovateuk.ifs.eugrant.organisation.populator.OrganisationCreationSelectTypePopulator;
import org.innovateuk.ifs.eugrant.organisation.viewmodel.OrganisationCreationSelectTypeViewModel;
import org.innovateuk.ifs.organisation.resource.OrganisationTypeEnum;
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
import java.util.Optional;

/**
 * Provides methods for picking an organisation type as a lead applicant after initialization or the registration process.
 */

@Controller
@RequestMapping(AbstractOrganisationCreationController.BASE_URL + "/" + AbstractOrganisationCreationController.LEAD_ORGANISATION_TYPE)
@SecuredBySpring(value = "Controller", description = "TODO", securedType = OrganisationCreationLeadTypeController.class)
@PreAuthorize("permitAll")
public class OrganisationCreationLeadTypeController extends AbstractOrganisationCreationController {

    private static final String ORGANISATION_TYPE_ID = "organisationTypeId";

    protected static final String NOT_ELIGIBLE = "not-eligible";

    @Autowired
    private OrganisationCreationSelectTypePopulator organisationCreationSelectTypePopulator;

    @Autowired
    private CompetitionRestService competitionRestService;

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
        if (!isValidLeadOrganisationType(organisationTypeId)) {
            bindingResult.addError(new FieldError(ORGANISATION_FORM, ORGANISATION_TYPE_ID, "Please select an organisation type."));
        }

        if (!bindingResult.hasFieldErrors(ORGANISATION_TYPE_ID)) {
            OrganisationTypeForm organisationTypeForm = registrationCookieService.getOrganisationTypeCookieValue(request).orElse(new OrganisationTypeForm());
            organisationTypeForm.setOrganisationType(organisationTypeId);
            registrationCookieService.saveToOrganisationTypeCookie(organisationTypeForm, response);
            saveOrganisationTypeToCreationForm(response, organisationTypeForm);

            return "redirect:" + BASE_URL + "/" + FIND_ORGANISATION;
        } else {
            organisationForm.setTriedToSave(true);
            OrganisationCreationSelectTypeViewModel selectOrgTypeViewModel = organisationCreationSelectTypePopulator.populate();
            model.addAttribute("model", selectOrgTypeViewModel);
            return TEMPLATE_PATH + "/" + LEAD_ORGANISATION_TYPE;
        }
    }

    private boolean isValidLeadOrganisationType(Long organisationTypeId) {
        return organisationTypeId != null && OrganisationTypeEnum.getFromId(organisationTypeId) != null;
    }

    private void saveOrganisationTypeToCreationForm(HttpServletResponse response, OrganisationTypeForm organisationTypeForm) {
        OrganisationCreationForm newOrganisationCreationForm = new OrganisationCreationForm();
        newOrganisationCreationForm.setOrganisationTypeId(organisationTypeForm.getOrganisationType());
        registrationCookieService.saveToOrganisationCreationCookie(newOrganisationCreationForm, response);
    }
}