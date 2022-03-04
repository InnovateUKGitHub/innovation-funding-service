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
import java.util.List;
import java.util.function.Supplier;

import static org.innovateuk.ifs.organisation.controller.AbstractOrganisationCreationController.BASE_URL;
import static org.innovateuk.ifs.organisation.resource.OrganisationTypeEnum.isValidKtpCollaborator;

@Controller
@RequestMapping(BASE_URL + "/{competitionId}/confirm-eligibility/{organisationId}")
public class ConfirmResearchOrganisationEligibilityController extends AbstractOrganisationCreationController {

    private static final String FORM_NAME = "form";
    private static final String RESEARCH_ELIGIBILITY_TEMPLATE = "confirm-research-organisation-eligibility";
    private static final String TEMPLATE_PATH = "registration/organisation";
    private static final String RESEARCH_NOT_ELIGIBLE = "research-not-eligible";
    private static final String NOT_ELIGIBLE = "not-eligible";

    @Autowired
    private CompetitionRestService competitionRestService;

    @PreAuthorize("hasPermission(#user,'APPLICATION_CREATION')")
    @GetMapping
    public String view(
            @PathVariable("competitionId") long competitionId,
            @PathVariable("organisationId") long organisationId,
            Model model,
            UserResource user) {

        String organisationName = organisationRestService.getOrganisationById(organisationId).getSuccess().getName();
        model.addAttribute("model", new ConfirmResearchOrganisationEligibilityViewModel(competitionId, organisationId, organisationName));
        model.addAttribute(FORM_NAME, new ConfirmResearchOrganisationEligibilityForm());

        return TEMPLATE_PATH + "/" + RESEARCH_ELIGIBILITY_TEMPLATE;
    }

    @PreAuthorize("hasPermission(#user,'APPLICATION_CREATION')")
    @PostMapping()
    public String post(
            @PathVariable("competitionId") long competitionId,
            @PathVariable("organisationId") long organisationId,
            @Valid @ModelAttribute(FORM_NAME) ConfirmResearchOrganisationEligibilityForm form,
            BindingResult bindingResult,
            ValidationHandler validationHandler,
            HttpServletRequest request,
            HttpServletResponse response,
            Model model,
            UserResource user) {
        Supplier<String> failureView = () ->  {
            String organisationName = organisationRestService.getOrganisationById(organisationId).getSuccess().getName();
            model.addAttribute("model", new ConfirmResearchOrganisationEligibilityViewModel(competitionId, organisationId, organisationName));
            return TEMPLATE_PATH + "/" + RESEARCH_ELIGIBILITY_TEMPLATE;
        };
        Supplier<String> successView = () -> {
            if (form.getConfirmEligibility()) {
                return "redirect:" + BASE_URL + "/" + competitionId + "/confirm-eligibility/" + organisationId + "/" + RESEARCH_NOT_ELIGIBLE;
            }
            return validateAndCompleteProcess(competitionId, organisationId, user, request, response);
        };
        return validationHandler.failNowOrSucceedWith(failureView, successView);
    }

    @GetMapping("/research-not-eligible")
    public String showNotEligible(Model model, HttpServletRequest request) {
        model.addAttribute("collaborator", registrationCookieService.isCollaboratorJourney(request));
        return TEMPLATE_PATH + "/" + RESEARCH_NOT_ELIGIBLE;
    }

    private String validateAndCompleteProcess(long competitionId, long organisationId, UserResource user, HttpServletRequest request, HttpServletResponse response) {
        if (registrationCookieService.isLeadJourney(request)) {
            if (!validateLeadApplicant(competitionId, organisationId)) {
                return redirectToNotEligiblePage();
            }
        }
        if (registrationCookieService.isCollaboratorJourney(request)) {
            if (!validateCollaborator(competitionId, organisationId)) {
                return redirectToNotEligiblePage();
            }
        }

        return organisationJourneyEnd.completeProcess(request, response, user, organisationId);
    }

    private String redirectToNotEligiblePage() {
        return "redirect:" + BASE_URL + "/" + ORGANISATION_TYPE + "/" + NOT_ELIGIBLE;
    }

    private boolean validateLeadApplicant(long competitionId, long organisationId) {
        List<Long> competitionLeadApplicantTypeIds = competitionRestService.getCompetitionById(competitionId).getSuccess().getLeadApplicantTypes();
        long organisationTypeId = organisationRestService.getOrganisationById(organisationId).getSuccess().getOrganisationType();

        return competitionLeadApplicantTypeIds.contains(organisationTypeId);
    }

    private boolean validateCollaborator(long competitionId, long organisationId) {
        boolean ktpCompetition = competitionRestService.getCompetitionById(competitionId).getSuccess().isKtp();
        long organisationTypeId = organisationRestService.getOrganisationById(organisationId).getSuccess().getOrganisationType();

        if (ktpCompetition) {
            return isValidKtpCollaborator(organisationTypeId);
        } else {
            return competitionRestService.getCompetitionById(competitionId).getSuccess().getMaxResearchRatio() != 0;
        }
    }
}
