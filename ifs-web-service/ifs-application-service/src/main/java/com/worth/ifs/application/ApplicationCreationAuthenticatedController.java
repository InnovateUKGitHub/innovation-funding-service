package com.worth.ifs.application;

import com.worth.ifs.application.resource.ApplicationResource;
import com.worth.ifs.application.service.ApplicationService;
import com.worth.ifs.commons.error.exception.ObjectNotFoundException;
import com.worth.ifs.commons.security.UserAuthenticationService;
import com.worth.ifs.user.resource.UserResource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * This controller is used when a existing user want to create a new application.
 * Shibboleth makes sure the current visitor of this page is authenticated.
 */
@Controller
@RequestMapping("/application/create-authenticated")
public class ApplicationCreationAuthenticatedController {
    public static final String COMPETITION_ID = "competitionId";
    public static final String RADIO_TRUE = "true";
    public static final String RADIO_FALSE = "false";
    private static final Log LOG = LogFactory.getLog(ApplicationCreationAuthenticatedController.class);
    public static final String FORM_RADIO_NAME = "create-application";
    @Autowired
    protected ApplicationService applicationService;

    @Autowired
    protected UserAuthenticationService userAuthenticationService;


    @RequestMapping(value = "/{competitionId}", method = RequestMethod.GET)
    public String view(Model model,
                       @PathVariable(COMPETITION_ID) Long competitionId,
                       HttpServletResponse response) {
        model.addAttribute(COMPETITION_ID, competitionId);
        return "create-application/confirm-new-application";
    }

    @RequestMapping(value = "/{competitionId}", method = RequestMethod.POST)
    public String post(Model model,
                       @PathVariable(COMPETITION_ID) Long competitionId,
                       HttpServletRequest request) {
        String createNewApplication = request.getParameter(FORM_RADIO_NAME);

        if (RADIO_TRUE.equals(createNewApplication)) {
            UserResource user = userAuthenticationService.getAuthenticatedUser(request);
            ApplicationResource application = applicationService.createApplication(competitionId, user.getId(), "");

            if (application != null) {
                return String.format("redirect:/application/%s/contributors/invite?newApplication", String.valueOf(application.getId()));
            } else {
                // com.worth.ifs.Application not created, throw exception
                List<Object> args = new ArrayList<>();
                args.add(competitionId);
                args.add(user.getId());
                throw new ObjectNotFoundException("Could not create a new application", args);
            }
        } else if (RADIO_FALSE.equals(createNewApplication)) {
            // redirect to dashboard
            return "redirect:/";
        }

        // user did not check one of the radio elements, show page again.
        return "redirect:/application/create-authenticated/" + competitionId;
    }
}
