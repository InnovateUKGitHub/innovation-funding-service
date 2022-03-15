package org.innovateuk.ifs.organisation.controller;

import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.organisation.form.ConfirmResearchOrganisationEligibilityForm;
import org.innovateuk.ifs.organisation.viewmodel.ConfirmResearchOrganisationEligibilityViewModel;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.function.Supplier;

import static org.innovateuk.ifs.organisation.controller.AbstractOrganisationCreationController.BASE_URL;
import static org.innovateuk.ifs.organisation.resource.OrganisationTypeEnum.RESEARCH;
import static org.innovateuk.ifs.organisation.resource.OrganisationTypeEnum.isValidKtpCollaborator;

@Controller
@RequestMapping(BASE_URL + "/{competitionId}/confirm-eligibility")
public class ConfirmResearchOrganisationEligibilityController extends AbstractOrganisationCreationController {

    private static final String FORM_NAME = "form";
    private static final String RESEARCH_ELIGIBILITY_TEMPLATE = "confirm-research-organisation-eligibility";
    private static final String TEMPLATE_PATH = "registration/organisation";
    private static final String RESEARCH_NOT_ELIGIBLE = "research-not-eligible";
    private static final String NOT_ELIGIBLE = "not-eligible";

    @Autowired
    private CompetitionRestService competitionRestService;

    @PreAuthorize("hasPermission(#user,'APPLICATION_CREATION')")
    @GetMapping()
    public String researchOrganisationConfirmEligibilityViewPage(
            @PathVariable("competitionId") long competitionId,
            Model model,
            UserResource user,
            HttpServletRequest request) {

        if (registrationCookieService.getOrganisationIdCookieValue(request).isPresent()) {
            String organisationName = organisationRestService.getOrganisationById(registrationCookieService.getOrganisationIdCookieValue(request).get()).getSuccess().getName();
            model.addAttribute("model", new ConfirmResearchOrganisationEligibilityViewModel(competitionId, organisationName));
        } else {
            model.addAttribute("model", new ConfirmResearchOrganisationEligibilityViewModel(competitionId, null));
        }
        model.addAttribute(FORM_NAME, new ConfirmResearchOrganisationEligibilityForm());

        return TEMPLATE_PATH + "/" + RESEARCH_ELIGIBILITY_TEMPLATE;
    }

    @PreAuthorize("hasPermission(#user,'APPLICATION_CREATION')")
    @PostMapping()
    public String researchOrganisationConfirmEligibilitySubmit(
            @PathVariable("competitionId") long competitionId,
            @Valid @ModelAttribute(FORM_NAME) ConfirmResearchOrganisationEligibilityForm form,
            BindingResult bindingResult,
            ValidationHandler validationHandler,
            HttpServletRequest request,
            HttpServletResponse response,
            Model model,
            UserResource user) {

        Supplier<String> failureView = () -> {
            model.addAttribute("model", new ConfirmResearchOrganisationEligibilityViewModel(competitionId, null));
            return TEMPLATE_PATH + "/" + RESEARCH_ELIGIBILITY_TEMPLATE;
        };
        Supplier<String> successView = () -> {
            if (form.getConfirmEligibility()) {
                return "redirect:" + BASE_URL + "/" + competitionId + "/confirm-eligibility/" + RESEARCH_NOT_ELIGIBLE;
            }
            return "redirect:" + BASE_URL + "/" + FIND_ORGANISATION;
        };

        if (registrationCookieService.getOrganisationIdCookieValue(request).isPresent()) {
            long organisationId = registrationCookieService.getOrganisationIdCookieValue(request).get();
            failureView = () -> {
                String organisationName = organisationRestService.getOrganisationById(organisationId).getSuccess().getName();
                model.addAttribute("model", new ConfirmResearchOrganisationEligibilityViewModel(competitionId, organisationName));
                return TEMPLATE_PATH + "/" + RESEARCH_ELIGIBILITY_TEMPLATE;
            };
            successView = () -> {
                if (form.getConfirmEligibility()) {
                    return "redirect:" + BASE_URL + "/" + competitionId + "/confirm-eligibility/" + RESEARCH_NOT_ELIGIBLE;
                }
                return validateAndCompleteProcess(competitionId, organisationId, user, request, response);
            };
        }

        return validationHandler.failNowOrSucceedWith(failureView, successView);
    }

    @PreAuthorize("hasPermission(#user,'APPLICATION_CREATION')")
    @GetMapping("/research-not-eligible")
    public String showNotEligible(@PathVariable("competitionId") long competitionId,
                                  Model model,
                                  UserResource user,
                                  HttpServletRequest request) {
        model.addAttribute("collaborator", registrationCookieService.isCollaboratorJourney(request));
        model.addAttribute("competitionId", competitionId);
        return TEMPLATE_PATH + "/" + RESEARCH_NOT_ELIGIBLE;
    }

    private String validateAndCompleteProcess(long competitionId, long organisationId, UserResource user, HttpServletRequest request, HttpServletResponse response) {
        if (registrationCookieService.isLeadJourney(request)) {
            if (!validateResearchLeadApplicant(competitionId)) {
                return redirectToNotEligiblePage();
            }
        }
        if (registrationCookieService.isCollaboratorJourney(request)) {
            if (!validateResearchCollaborator(competitionId)) {
                return redirectToNotEligiblePage();
            }
        }

        return organisationJourneyEnd.completeProcess(request, response, user, organisationId);
    }

    private String redirectToNotEligiblePage() {
        return "redirect:" + BASE_URL + "/" + ORGANISATION_TYPE + "/" + NOT_ELIGIBLE;
    }

    private boolean validateResearchLeadApplicant(long competitionId) {

        return competitionRestService.getCompetitionById(competitionId).getSuccess().getLeadApplicantTypes().contains(RESEARCH.getId());
    }

    private boolean validateResearchCollaborator(long competitionId) {
        boolean ktpCompetition = competitionRestService.getCompetitionById(competitionId).getSuccess().isKtp();

        if (ktpCompetition) {
            return isValidKtpCollaborator(RESEARCH.getId());
        } else {
            return competitionRestService.getCompetitionById(competitionId).getSuccess().getMaxResearchRatio() != 0;
        }
    }
}
