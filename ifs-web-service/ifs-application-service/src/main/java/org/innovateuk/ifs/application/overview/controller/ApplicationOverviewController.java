package org.innovateuk.ifs.application.overview.controller;

import org.innovateuk.ifs.application.overview.populator.ApplicationOverviewModelPopulator;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.ApplicationState;
import org.innovateuk.ifs.application.service.ApplicationRestService;
import org.innovateuk.ifs.async.annotations.AsyncMethod;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.competition.resource.CompetitionStatus;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.UserRestService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import static java.lang.String.format;
import static org.innovateuk.ifs.application.resource.ApplicationState.OPENED;
import static org.innovateuk.ifs.user.resource.Role.LEADAPPLICANT;

/**
 * This controller will handle all requests that are related to the application overview.
 * Application overview is the page that contains the most basic information about the current application and
 * the basic information about the competition the application is related to.
 */
@Controller
@RequestMapping("/application")
@PreAuthorize("hasAuthority('applicant')")
@SecuredBySpring(value="Controller",
        description = "Only applicants on an application are allowed to view the corresponding application overview",
        securedType = ApplicationOverviewController.class)
public class ApplicationOverviewController {

    private ApplicationOverviewModelPopulator applicationOverviewModelPopulator;

    private UserRestService userRestService;
    private ApplicationRestService applicationRestService;

    public ApplicationOverviewController(ApplicationOverviewModelPopulator applicationOverviewModelPopulator,
                                 UserRestService userRestService,
                                 ApplicationRestService applicationRestService) {
        this.applicationOverviewModelPopulator = applicationOverviewModelPopulator;
        this.userRestService = userRestService;
        this.applicationRestService = applicationRestService;
    }

    @GetMapping("/{applicationId}")
    @AsyncMethod
    public String applicationOverview(Model model,
                                     @PathVariable("applicationId") long applicationId,
                                     UserResource user) {
        ApplicationResource application = applicationRestService.getApplicationById(applicationId)
                .getSuccess();

        if (application.getCompetitionStatus() != CompetitionStatus.OPEN) {
            return format("redirect:/application/%s/summary", application.getId());
        }

        if (application.isSubmitted()) {
            return format("redirect:/application/%s/track", application.getId());
        }

        changeApplicationStatusToOpen(application, user);

        model.addAttribute("model", applicationOverviewModelPopulator.populateModel(application, user));
        model.addAttribute("reopened", applicationRestService.applicationInReopenedState(applicationId).getSuccess());
        model.addAttribute("reopenedDate", applicationRestService.getApplicationReopenedDate(applicationId).getSuccess());
        return "application-overview";
    }

    private void changeApplicationStatusToOpen(ApplicationResource applicationResource, UserResource userResource) {
        if (ApplicationState.CREATED.equals(applicationResource.getApplicationState())
                && userIsLeadApplicant(userResource.getId(), applicationResource.getId())) {
            applicationRestService.updateApplicationState(applicationResource.getId(), OPENED).getSuccess();
        }
    }

    private boolean userIsLeadApplicant(long userId, long applicationId) {
        return userRestService.findProcessRole(userId, applicationId).getSuccess()
                .getRole() == LEADAPPLICANT;
    }

    @GetMapping("/terms-and-conditions")
    public String termsAndConditions() {
        return "application-terms-and-conditions";
    }
}
