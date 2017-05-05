package org.innovateuk.ifs.application;

import org.innovateuk.ifs.application.populator.ApplicationTeamModelPopulator;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.ApplicationState;
import org.innovateuk.ifs.application.service.ApplicationRestService;
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
    private ApplicationRestService applicationRestService;

    @Autowired
    private ApplicationTeamModelPopulator applicationTeamModelPopulator;

    @GetMapping("/team")
    @PreAuthorize("hasPermission(#applicationId, 'VIEW_APPLICATION_TEAM_PAGE')")
    public String getApplicationTeam(Model model, @PathVariable("applicationId") long applicationId,
                                     @ModelAttribute(name = "loggedInUser", binding = false) UserResource loggedInUser) {
        model.addAttribute("model", applicationTeamModelPopulator.populateModel(applicationId, loggedInUser.getId()));
        return "application-team/team";
    }

    @GetMapping("/begin")
    @PreAuthorize("hasPermission(#applicationId, 'BEGIN_APPLICATION')")
    public String beginApplication(@PathVariable("applicationId") long applicationId,
                                   @ModelAttribute(name = "loggedInUser", binding = false) UserResource loggedInUser) {
        ApplicationResource applicationResource = applicationRestService.getApplicationById(applicationId).getSuccessObjectOrThrowException();
        changeApplicationStatusToOpen(applicationResource);
        return format("redirect:/application/%s", applicationResource.getId());
    }

    private void changeApplicationStatusToOpen(ApplicationResource applicationResource) {
        if (ApplicationState.CREATED == applicationResource.getApplicationState()) {
            applicationRestService.updateApplicationState(applicationResource.getId(), OPEN).getSuccessObjectOrThrowException();
        }
    }
}