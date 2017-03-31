package org.innovateuk.ifs.application;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.ApplicationService;
import org.innovateuk.ifs.commons.error.exception.ObjectNotFoundException;
import org.innovateuk.ifs.commons.security.UserAuthenticationService;
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
    private static final Log LOG = LogFactory.getLog(ApplicationCreationAuthenticatedController.class);
    public static final String FORM_RADIO_NAME = "create-application";
    @Autowired
    protected ApplicationService applicationService;
    @Autowired
    protected UserService userService;

    @Autowired
    protected UserAuthenticationService userAuthenticationService;


    @GetMapping("/{competitionId}")
    public String view(Model model,
                       @PathVariable(COMPETITION_ID) Long competitionId,
                       HttpServletRequest request) {
        UserResource user = userAuthenticationService.getAuthenticatedUser(request);

        Boolean userHasApplication = userService.userHasApplicationForCompetition(user.getId(), competitionId);
        if (Boolean.TRUE.equals(userHasApplication)) {
            model.addAttribute(COMPETITION_ID, competitionId);
            return "create-application/confirm-new-application";
        } else {
            return createApplicationAndShowInvitees(user, competitionId);
        }
    }

    @PostMapping("/{competitionId}")
    public String post(Model model,
                       @PathVariable(COMPETITION_ID) Long competitionId,
                       HttpServletRequest request) {
        String createNewApplication = request.getParameter(FORM_RADIO_NAME);

        if (RADIO_TRUE.equals(createNewApplication)) {
            UserResource user = userAuthenticationService.getAuthenticatedUser(request);
            return createApplicationAndShowInvitees(user, competitionId);
        } else if (RADIO_FALSE.equals(createNewApplication)) {
            // redirect to dashboard
            return "redirect:/";
        }

        // user did not check one of the radio elements, show page again.
        return "redirect:/application/create-authenticated/" + competitionId;
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
}
