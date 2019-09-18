package org.innovateuk.ifs.dashboard.controller;

import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.dashboard.populator.ApplicantDashboardPopulator;
import org.innovateuk.ifs.navigation.NavigationRoot;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * This controller will handle requests related to the current applicant's dashboard. So pages that are relative to
 * that user are implemented here, for example the my-applications page.
 */
@Controller
@RequestMapping("/applicant")
@SecuredBySpring(value = "Controller", description = "Each applicant has permission to view their own dashboard",
        securedType = ApplicantDashboardController.class)
@PreAuthorize("hasAuthority('applicant')")
public class ApplicantDashboardController {

    @Autowired
    private ApplicantDashboardPopulator applicantDashboardPopulator;

    @GetMapping("/dashboard")
    @NavigationRoot
    public String dashboard(Model model,
                            UserResource user) {
        model.addAttribute("model", applicantDashboardPopulator.populate(user.getId()));
        return "applicant-dashboard";
    }
}
