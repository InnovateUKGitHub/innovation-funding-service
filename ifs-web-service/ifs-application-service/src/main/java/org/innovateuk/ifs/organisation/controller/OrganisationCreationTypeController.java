package org.innovateuk.ifs.organisation.controller;

import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.competition.publiccontent.resource.FundingType;
import org.innovateuk.ifs.competition.resource.CompetitionOrganisationConfigResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionOrganisationConfigRestService;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.organisation.resource.OrganisationTypeEnum;
import org.innovateuk.ifs.organisation.resource.OrganisationTypeResource;
import org.innovateuk.ifs.registration.form.OrganisationCreationForm;
import org.innovateuk.ifs.registration.form.OrganisationTypeForm;
import org.innovateuk.ifs.organisation.populator.OrganisationCreationSelectTypePopulator;
import org.innovateuk.ifs.organisation.viewmodel.OrganisationCreationSelectTypeViewModel;
import org.innovateuk.ifs.user.resource.UserResource;
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
@RequestMapping(AbstractOrganisationCreationController.BASE_URL + "/" + AbstractOrganisationCreationController.ORGANISATION_TYPE)
@SecuredBySpring(value = "Controller", description = "TODO", securedType = OrganisationCreationTypeController.class)
@PreAuthorize("permitAll")
public class OrganisationCreationTypeController extends AbstractOrganisationCreationController {

    private static final String ORGANISATION_TYPE_ID = "organisationTypeId";
    public static final String COMPETITION_ID = "competitionId";

    protected static final String NOT_ELIGIBLE = "not-eligible";

    @Autowired
    private OrganisationCreationSelectTypePopulator organisationCreationSelectTypePopulator;

    @Autowired
    private CompetitionRestService competitionRestService;

    @Autowired
    private CompetitionOrganisationConfigRestService competitionOrganisationConfigRestService;

    @GetMapping
    public String selectOrganisationType(Model model,
                                         UserResource user,
                                         HttpServletRequest request,
                                         HttpServletResponse response) {
        CompetitionResource competition = competitionRestService.getPublishedCompetitionById(getCompetitionIdFromInviteOrCookie(request)).getSuccess();
        if (registrationCookieService.isLeadJourney(request)
         && competition.getFundingType() == FundingType.KTP) {
            return handleKtpLeadOrganisationType(request, response);
        }
        Optional<Long> competitionIdOpt = registrationCookieService.getCompetitionIdCookieValue(request);
        model.addAttribute("model", organisationCreationSelectTypePopulator.populate(request, competition));
        model.addAttribute(COMPETITION_ID, competitionIdOpt.orElse(null));
        Optional<OrganisationCreationForm> organisationCreationFormCookie = registrationCookieService.getOrganisationCreationCookieValue(request);
        addPageSubtitleToModel(request, user, model);

        if (organisationCreationFormCookie.isPresent()) {
            model.addAttribute(ORGANISATION_FORM, organisationCreationFormCookie.get());
        } else {
            model.addAttribute(ORGANISATION_FORM, new OrganisationCreationForm());
        }

        return TEMPLATE_PATH + "/" + ORGANISATION_TYPE;
    }

    private String handleKtpLeadOrganisationType(HttpServletRequest request, HttpServletResponse response) {
        OrganisationTypeForm organisationTypeForm = registrationCookieService.getOrganisationTypeCookieValue(request).orElse(new OrganisationTypeForm());
        organisationTypeForm.setOrganisationType(OrganisationTypeEnum.CATAPULT.getId()); // Temporary add as catapult
        registrationCookieService.saveToOrganisationTypeCookie(organisationTypeForm, response);
        saveOrganisationTypeToCreationForm(response, organisationTypeForm);
        return "redirect:" + BASE_URL + "/" + "knowledge-base";
    }

    @PostMapping
    public String confirmSelectOrganisationType(Model model,
                                                @Valid @ModelAttribute(ORGANISATION_FORM) OrganisationCreationForm organisationForm,
                                                BindingResult bindingResult,
                                                HttpServletRequest request,
                                                HttpServletResponse response) {

        Long organisationTypeId = organisationForm.getOrganisationTypeId();
        if ( !bindingResult.hasFieldErrors(ORGANISATION_TYPE_ID) && !isValidLeadOrganisationType(organisationTypeId)) {
            bindingResult.addError(new FieldError(ORGANISATION_FORM, ORGANISATION_TYPE_ID, "Please select an organisation type."));
        }

        if (!bindingResult.hasFieldErrors(ORGANISATION_TYPE_ID)) {
            OrganisationTypeForm organisationTypeForm = registrationCookieService.getOrganisationTypeCookieValue(request).orElse(new OrganisationTypeForm());
            organisationTypeForm.setOrganisationType(organisationTypeId);
            registrationCookieService.saveToOrganisationTypeCookie(organisationTypeForm, response);
            saveOrganisationTypeToCreationForm(response, organisationTypeForm);

            if (registrationCookieService.isLeadJourney(request) && !isAllowedToLeadApplication(organisationTypeId, request)) {
                return redirectToNotEligibleUrl();
            }

            if (registrationCookieService.isInternationalJourney(request)) {
                return "redirect:" + BASE_URL + "/" + INTERNATIONAL_ORGANISATION + "/details";
            }

            return "redirect:" + BASE_URL + "/" + FIND_ORGANISATION;
        } else {
            organisationForm.setTriedToSave(true);
            CompetitionResource competition = competitionRestService.getPublishedCompetitionById(getCompetitionIdFromInviteOrCookie(request)).getSuccess();
            OrganisationCreationSelectTypeViewModel selectOrgTypeViewModel = organisationCreationSelectTypePopulator.populate(request, competition);
            model.addAttribute("model", selectOrgTypeViewModel);
            return TEMPLATE_PATH + "/" + ORGANISATION_TYPE;
        }
    }

    private String redirectToNotEligibleUrl() {
        return "redirect:" + BASE_URL + "/" + AbstractOrganisationCreationController.ORGANISATION_TYPE + "/" + NOT_ELIGIBLE;
    }

    @GetMapping(NOT_ELIGIBLE)
    public String showNotEligible(Model model, HttpServletRequest request) {
        return TEMPLATE_PATH + "/" + NOT_ELIGIBLE;
    }

    private boolean isAllowedToLeadApplication(Long organisationTypeId, HttpServletRequest request) {
        Optional<Long> competitionIdOpt = registrationCookieService.getCompetitionIdCookieValue(request);

        if (competitionIdOpt.isPresent()) {

            CompetitionOrganisationConfigResource competitionOrganisationConfigResource = competitionOrganisationConfigRestService.findByCompetitionId(competitionIdOpt.get()).getSuccess();

            if(!competitionOrganisationConfigResource.cantInternationalApplicantsLead()
                    && registrationCookieService.isInternationalJourney(request)) {
                return false;
            }

            List<OrganisationTypeResource> organisationTypesAllowed = competitionRestService.getCompetitionOrganisationType(competitionIdOpt.get()).getSuccess();
            return organisationTypesAllowed.stream()
                    .map(organisationTypeResource -> organisationTypeResource.getId())
                    .anyMatch(aLong -> aLong.equals(organisationTypeId));
        }

        return false;
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