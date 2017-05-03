package org.innovateuk.ifs.application;

import org.innovateuk.ifs.application.populator.ApplicationTeamModelPopulator;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.ApplicationState;
import org.innovateuk.ifs.application.service.ApplicationService;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import static java.lang.String.format;
import static org.innovateuk.ifs.application.resource.ApplicationState.OPEN;

/**
 * This controller will handle all requests that are related to the read only view of the application team.
 */
@Controller
@RequestMapping("/application/{applicationId}")
@PreAuthorize("hasAuthority('applicant')")
public class ApplicationTeamController {

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private ApplicationTeamModelPopulator applicationTeamModelPopulator;

    @GetMapping("/team")
    @PreAuthorize("hasPermission(#applicationId, 'APPLICATION_NOT_YET_SUBMITTED')")
    public String getApplicationTeam(Model model, @PathVariable("applicationId") long applicationId,
                                     @ModelAttribute("loggedInUser") UserResource loggedInUser) {
        model.addAttribute("model", applicationTeamModelPopulator.populateModel(applicationId, loggedInUser.getId()));
        return "application-team/team";
    }

    @GetMapping("/begin")
    @PreAuthorize("hasPermission(#applicationId, 'LEAD_APPLICANT')")
    public String beginApplication(@PathVariable("applicationId") long applicationId,
                                   @ModelAttribute("loggedInUser") UserResource loggedInUser) {
        ApplicationResource applicationResource = applicationService.getById(applicationId);
        changeApplicationStatusToOpen(applicationResource);
        return format("redirect:/application/%s", applicationResource.getId());
    }

    private void changeApplicationStatusToOpen(ApplicationResource applicationResource) {
        if (ApplicationState.CREATED == applicationResource.getApplicationState()) {
            applicationService.updateState(applicationResource.getId(), OPEN).getSuccessObjectOrThrowException();
        }
    }
}