package org.innovateuk.ifs.registration;

import org.innovateuk.ifs.application.service.CompetitionService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.registration.form.OrganisationCreationForm;
import org.innovateuk.ifs.registration.form.OrganisationTypeForm;
import org.innovateuk.ifs.registration.populator.OrganisationCreationSelectTypePopulator;
import org.innovateuk.ifs.registration.viewmodel.NotEligibleViewModel;
import org.innovateuk.ifs.registration.viewmodel.OrganisationCreationSelectTypeViewModel;
import org.innovateuk.ifs.user.resource.OrganisationTypeEnum;
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
import java.util.Optional;

@Controller
@RequestMapping(AbstractOrganisationCreationController.BASE_URL + "/")
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

    @GetMapping(AbstractOrganisationCreationController.LEAD_ORGANISATION_TYPE)
    public String selectOrganisationType(Model model,
                                         HttpServletRequest request) {
        model.addAttribute("model", organisationCreationSelectTypePopulator.populate());

        Optional<OrganisationCreationForm> organisationCreationFormFromCookie = registrationCookieService.getOrganisationCreationCookieValue(request);
        if (organisationCreationFormFromCookie.isPresent()) {
            model.addAttribute(ORGANISATION_FORM, organisationCreationFormFromCookie.get());
        } else {
            model.addAttribute(ORGANISATION_FORM, new OrganisationCreationForm());
        }


        return TEMPLATE_PATH + "/" + LEAD_ORGANISATION_TYPE;
    }

    @PostMapping(AbstractOrganisationCreationController.LEAD_ORGANISATION_TYPE)
    public String confirmSelectOrganisationType(Model model,
                                                @Valid @ModelAttribute(ORGANISATION_FORM) OrganisationCreationForm organisationForm,
                                                BindingResult bindingResult,
                                                HttpServletRequest request,
                                                HttpServletResponse response) {

        Long organisationTypeId = organisationForm.getOrganisationTypeId();
        if (organisationTypeId != null &&
                !isValidLeadOrganisationType(organisationTypeId)) {
            bindingResult.addError(new FieldError(ORGANISATION_FORM, ORGANISATION_TYPE_ID, "Please select an organisation type."));
        }

        if (!bindingResult.hasFieldErrors(ORGANISATION_TYPE_ID)) {
            OrganisationTypeForm organisationTypeForm = new OrganisationTypeForm();
            organisationTypeForm.setOrganisationType(organisationTypeId);
            registrationCookieService.saveToOrganisationTypeCookie(organisationTypeForm, response);
            saveOrganisationTypeToCreationForm(request, response, organisationTypeForm);

            if (!isAllowedToLeadApplication(organisationTypeId, request)) {
                return "redirect:" + BASE_URL + "/" + NOT_ELIGIBLE;
            }

            return "redirect:" + BASE_URL + "/" + FIND_ORGANISATION;
        } else {
            organisationForm.setTriedToSave(true);
            OrganisationCreationSelectTypeViewModel selectOrgTypeViewModel = organisationCreationSelectTypePopulator.populate();
            model.addAttribute("model", selectOrgTypeViewModel);
            return TEMPLATE_PATH + "/" + LEAD_ORGANISATION_TYPE;
        }
    }

    @GetMapping(NOT_ELIGIBLE)
    public String showNotEligible(Model model,
                                  HttpServletRequest request) {
        Optional<OrganisationTypeForm> organisationTypeChosenOpt = registrationCookieService.getOrganisationTypeCookieValue(request);

        organisationTypeChosenOpt.ifPresent(organisationTypeForm ->
                model.addAttribute("model", new NotEligibleViewModel(
                        organisationTypeRestService.findOne(organisationTypeForm.getOrganisationType())
                                .getSuccessObjectOrThrowException()
                                .getName())));

        return TEMPLATE_PATH + "/" + NOT_ELIGIBLE;
    }

    private boolean isAllowedToLeadApplication(Long organisationTypeId, HttpServletRequest request) {
        Optional<Long> competitionIdOpt = registrationCookieService.getCompetitionIdCookieValue(request);

        if (competitionIdOpt.isPresent()) {
            CompetitionResource competition = competitionService.getPublishedById(competitionIdOpt.get());
            return competition.getLeadApplicantTypes().contains(organisationTypeId);
        }

        return Boolean.FALSE;
    }

    private boolean isValidLeadOrganisationType(Long organisationTypeId) {
        return OrganisationTypeEnum.getFromId(organisationTypeId) != null;
    }

    private void saveOrganisationTypeToCreationForm(HttpServletRequest request, HttpServletResponse response, OrganisationTypeForm organisationTypeForm) {
        Optional<OrganisationCreationForm> organisationCreationFormFromCookie = registrationCookieService.getOrganisationCreationCookieValue(request);
        if (organisationCreationFormFromCookie.isPresent()) {
            organisationCreationFormFromCookie.get().setOrganisationTypeId(organisationTypeForm.getOrganisationType());
        }

        OrganisationCreationForm newOrganisationCreationForm = organisationCreationFormFromCookie.orElse(new OrganisationCreationForm());
        registrationCookieService.saveToOrganisationCreationCookie(newOrganisationCreationForm, response);
    }
}
