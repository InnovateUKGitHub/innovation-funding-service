package org.innovateuk.ifs.organisation.controller;

import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.organisation.form.ConfirmResearchOrganisationEligibilityForm;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
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
import static org.innovateuk.ifs.organisation.resource.OrganisationTypeEnum.isValidCollaborator;
import static org.innovateuk.ifs.organisation.resource.OrganisationTypeEnum.isValidKtpCollaborator;

@Controller
@RequestMapping(BASE_URL + "/{competitionId}/confirm-eligibility/{organisationId}")
public class ConfirmResearchOrganisationEligibilityController extends AbstractOrganisationCreationController {

    private static final String FORM_NAME = "form";
    protected static final String TEMPLATE_PATH = "registration/organisation";
    protected static final String NOT_ELIGIBLE = "research-not-eligible";

    @Autowired
    private UserService userService;

    @Autowired
    private CompetitionRestService competitionRestService;


    @GetMapping
    public String view(
            @PathVariable("competitionId") long competitionId,
            @PathVariable("organisationId") long organisationId,
            Model model,
            UserResource user) {

        String organisationName = organisationRestService.getOrganisationById(organisationId).getSuccess().getOrganisationTypeName();
        model.addAttribute("organisationName", organisationName);
        model.addAttribute(FORM_NAME, new ConfirmResearchOrganisationEligibilityForm());

        return TEMPLATE_PATH + "/confirm-research-organisation-eligibility";
    }

    @PostMapping()
    public String post(
            @PathVariable("competitionId") long competitionId,
            @PathVariable("organisationId") long organisationId,
            @Valid @ModelAttribute(FORM_NAME) ConfirmResearchOrganisationEligibilityForm form,
            BindingResult bindingResult,
            ValidationHandler validationHandler,
            HttpServletRequest request,
            HttpServletResponse response,
            UserResource user) {
        Supplier<String> failureView = () -> BASE_URL +  "/" + organisationId + "/confirm-eligibility";
        Supplier<String> successView = () -> {
            if (form.getConfirmEligibility()) {
                return redirectToResearchNotEligiblePage(competitionId, organisationId, user, request, response);
            }
            // redirect to dashboard
            return "redirect:/";
        };
        return validationHandler.failNowOrSucceedWith(failureView, successView);
    }
//
//    @GetMapping("/research-not-eligible")
//    public String showNotEligible(Model model, HttpServletRequest request) {
//        model.addAttribute("collaborator", registrationCookieService.isCollaboratorJourney(request));
//        return "registration/organisation/research-not-eligible";
//    }

    private String redirectToResearchNotEligiblePage(long competitionId, long organisationId, UserResource user, HttpServletRequest request, HttpServletResponse response) {
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
        return TEMPLATE_PATH + "/" + NOT_ELIGIBLE;
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
            return isValidCollaborator(organisationTypeId);
        }

    }


}
