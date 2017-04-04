package org.innovateuk.ifs.application;

import org.innovateuk.ifs.application.constant.ApplicationStatusConstants;
import org.innovateuk.ifs.application.populator.ApplicationTeamModelPopulator;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.ApplicationService;
import org.innovateuk.ifs.commons.error.exception.ForbiddenActionException;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import static java.lang.String.format;
import static org.innovateuk.ifs.application.constant.ApplicationStatusConstants.CREATED;
import static org.innovateuk.ifs.application.constant.ApplicationStatusConstants.OPEN;

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
    private UserService userService;

    @Autowired
    private ApplicationTeamModelPopulator applicationTeamModelPopulator;

    @GetMapping("/team")
    public String getApplicationTeam(Model model, @PathVariable("applicationId") long applicationId,
                                     @ModelAttribute("loggedInUser") UserResource loggedInUser) {
        model.addAttribute("model", applicationTeamModelPopulator.populateModel(applicationId, loggedInUser.getId()));
        return "application-team/team";
    }

    @GetMapping("/begin")
    public String beginApplication(@PathVariable("applicationId") long applicationId,
                                   @ModelAttribute("loggedInUser") UserResource loggedInUser) {
        ApplicationResource applicationResource = applicationService.getById(applicationId);
        checkUserIsLeadApplicant(applicationResource, loggedInUser.getId());
        changeApplicationStatusToOpen(applicationResource);
        return format("redirect:/application/%s", applicationResource.getId());
    }

    private void checkUserIsLeadApplicant(ApplicationResource applicationResource, long loggedInUserId) {
        if (loggedInUserId != getLeadApplicantId(applicationResource)) {
            throw new ForbiddenActionException("Unable to assign Monitoring Officers until the Project Details have been submitted");
        }
    }

    private long getLeadApplicantId(ApplicationResource applicationResource) {
        return userService.getLeadApplicantProcessRoleOrNull(applicationResource).getUser();
    }

    private void changeApplicationStatusToOpen(ApplicationResource applicationResource) {
        if (CREATED == ApplicationStatusConstants.getFromId(applicationResource.getApplicationStatus())) {
            applicationService.updateStatus(applicationResource.getId(), OPEN.getId()).getSuccessObjectOrThrowException();
        }
    }
}