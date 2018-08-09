package org.innovateuk.ifs.application.creation.controller;

import org.innovateuk.ifs.application.creation.form.ApplicationCreationAuthenticatedForm;
import org.innovateuk.ifs.application.creation.viewmodel.AuthenticatedNotEligibleViewModel;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.ApplicationService;
import org.innovateuk.ifs.user.service.OrganisationService;
import org.innovateuk.ifs.application.service.QuestionRestService;
import org.innovateuk.ifs.commons.exception.ObjectNotFoundException;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.registration.service.RegistrationCookieService;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.function.Supplier;

/**
 * This controller is used when a existing user want to create a new application.
 * Shibboleth makes sure the current visitor of this page is authenticated.
 */
@Controller
@RequestMapping("/application/create-authenticated")
@SecuredBySpring(value = "Controller", description = "TODO", securedType = ApplicationCreationAuthenticatedController.class)
@PreAuthorize("hasAnyAuthority('applicant', 'assessor')")
public class ApplicationCreationAuthenticatedController {
    public static final String COMPETITION_ID = "competitionId";
    public static final String FORM_NAME = "form";

    @Autowired
    private UserService userService;

    @Autowired
    private RegistrationCookieService registrationCookieService;

    @GetMapping("/{competitionId}")
    public String view(Model model,
                       @PathVariable(COMPETITION_ID) long competitionId,
                       UserResource user,
                       HttpServletResponse response) {
        Boolean userHasApplication = userService.userHasApplicationForCompetition(user.getId(), competitionId);
        if (Boolean.TRUE.equals(userHasApplication)) {
            model.addAttribute(COMPETITION_ID, competitionId);
            model.addAttribute(FORM_NAME, new ApplicationCreationAuthenticatedForm());
            return "create-application/confirm-new-application";
        } else {
            return redirectToOrganisationCreation(competitionId, response);
        }
    }

    @PostMapping("/{competitionId}")
    public String post(@PathVariable(COMPETITION_ID) long competitionId,
                       @Valid @ModelAttribute(FORM_NAME) ApplicationCreationAuthenticatedForm form,
                       BindingResult bindingResult,
                       ValidationHandler validationHandler,
                       HttpServletResponse response) {
        Supplier<String> failureView = () -> "create-application/confirm-new-application";
        Supplier<String> successView = () -> {
            if (form.getCreateNewApplication()) {
                return redirectToOrganisationCreation(competitionId, response);
            }
            // redirect to dashboard
            return "redirect:/";
        };

        return validationHandler.failNowOrSucceedWith(failureView, successView);
    }

    private String redirectToOrganisationCreation(long competitionId, HttpServletResponse response) {
        registrationCookieService.deleteAllRegistrationJourneyCookies(response);
        registrationCookieService.saveToCompetitionIdCookie(competitionId, response);
        return "redirect:/organisation/select";
    }
}
