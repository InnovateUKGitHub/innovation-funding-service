package org.innovateuk.ifs.dashboard.controller;

import org.innovateuk.ifs.application.service.ApplicationRestService;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.dashboard.populator.ApplicantDashboardPopulator;
import org.innovateuk.ifs.navigation.NavigationRoot;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import static java.lang.String.format;

/**
 * This controller will handle requests related to the current applicant's dashboard. So pages that are relative to
 * that user are implemented here, for example the my-applications page.
 */
@Controller
@RequestMapping("/applicant/dashboard")
@SecuredBySpring(value = "Controller", description = "Each applicant has permission to view their own dashboard",
        securedType = ApplicantDashboardController.class)
@PreAuthorize("hasAuthority('applicant')")
public class ApplicantDashboardController {

    @Autowired
    private ApplicantDashboardPopulator applicantDashboardPopulator;

    @Autowired
    private ApplicationRestService applicationRestService;

    @GetMapping
    @NavigationRoot
    public String dashboard(Model model,
                            UserResource user) {
        model.addAttribute("model", applicantDashboardPopulator.populate(user.getId()));
        return "applicant-dashboard";
    }

    @PostMapping(params = "hide-application")
    public String hideApplication(@RequestParam("hide-application") long applicationId,
                                  UserResource user) {
        applicationRestService.hideApplication(applicationId, user.getId());
        return format("redirect:/applicant/dashboard");
    }

    @PostMapping(params = "delete-application")
    public String deleteApplication(@RequestParam("delete-application") long applicationId) {
        applicationRestService.deleteApplication(applicationId);
        return format("redirect:/applicant/dashboard");
    }

}
