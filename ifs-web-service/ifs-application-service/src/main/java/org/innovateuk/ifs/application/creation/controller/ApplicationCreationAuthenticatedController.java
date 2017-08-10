package org.innovateuk.ifs.application.creation.controller;

import org.innovateuk.ifs.application.creation.viewmodel.AuthenticatedNotEligibleViewModel;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.ApplicationService;
import org.innovateuk.ifs.application.service.CompetitionService;
import org.innovateuk.ifs.application.service.OrganisationService;
import org.innovateuk.ifs.commons.error.exception.ObjectNotFoundException;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.user.resource.OrganisationResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

import static java.lang.String.format;

/**
 * This controller is used when a existing user want to create a new application.
 * Shibboleth makes sure the current visitor of this page is authenticated.
 */
@Controller
@RequestMapping("/application/create-authenticated")
@PreAuthorize("hasAuthority('applicant')")
public class ApplicationCreationAuthenticatedController {
    public static final String COMPETITION_ID = "competitionId";
    public static final String RADIO_TRUE = "true";
    public static final String RADIO_FALSE = "false";
    public static final String FORM_RADIO_NAME = "create-application";

    @Autowired
    protected ApplicationService applicationService;

    @Autowired
    protected CompetitionService competitionService;

    @Autowired
    protected OrganisationService organisationService;

    @Autowired
    protected UserService userService;

    @GetMapping("/{competitionId}")
    public String view(Model model,
                       @PathVariable(COMPETITION_ID) Long competitionId,
                       UserResource user) {
        if(!isAllowedToLeadApplication(user.getId(), competitionId)) {
            return redirectToNotEligible(competitionId);
        }

        Boolean userHasApplication = userService.userHasApplicationForCompetition(user.getId(), competitionId);
        if (Boolean.TRUE.equals(userHasApplication)) {
            model.addAttribute(COMPETITION_ID, competitionId);
            return "create-application/confirm-new-application";
        } else {
            return createApplicationAndShowInvitees(user, competitionId);
        }
    }

    private String redirectToNotEligible(Long competitionId) {
       return format("redirect:/application/create-authenticated/%s/not-eligible", competitionId);
    }

    @PostMapping(value = "/{competitionId}")
    public String post(Model model,
                       @PathVariable(COMPETITION_ID) Long competitionId,
                       UserResource user,
                       HttpServletRequest request) {
        if(!isAllowedToLeadApplication(user.getId(), competitionId)) {
            return redirectToNotEligible(competitionId);
        }

        final String createNewApplication = request.getParameter(FORM_RADIO_NAME);

        if (RADIO_TRUE.equals(createNewApplication)) {
            return createApplicationAndShowInvitees(user, competitionId);
        } else if (RADIO_FALSE.equals(createNewApplication)) {
            // redirect to dashboard
            return "redirect:/";
        }

        // user did not check one of the radio elements, show page again.
        return "redirect:/application/create-authenticated/" + competitionId;
    }

    @GetMapping(value = "/{competitionId}/not-eligible")
    public String showNotEligiblePage(Model model,
                                      @PathVariable(COMPETITION_ID) Long competitionId,
                                      UserResource userResource) {
        OrganisationResource organisation = organisationService.getOrganisationForUser(userResource.getId());

        model.addAttribute("model", new AuthenticatedNotEligibleViewModel(organisation.getOrganisationTypeName(), competitionId));
        return "create-application/authenticated-not-eligible";
    }

    private String createApplicationAndShowInvitees(UserResource user, Long competitionId) {
        ApplicationResource application = applicationService.createApplication(competitionId, user.getId(), "");

        if (application != null) {
            return format("redirect:/application/%s/team", application.getId());
        } else {
            // Application not created, throw exception
            List<Object> args = new ArrayList<>();
            args.add(competitionId);
            args.add(user.getId());
            throw new ObjectNotFoundException("Could not create a new application", args);
        }
    }

    private boolean isAllowedToLeadApplication(Long userId, Long competitionId) {
        OrganisationResource organisation = organisationService.getOrganisationForUser(userId);
        CompetitionResource competition = competitionService.getById(competitionId);

        return competition.getLeadApplicantTypes().contains(organisation.getOrganisationType());
    }
}
