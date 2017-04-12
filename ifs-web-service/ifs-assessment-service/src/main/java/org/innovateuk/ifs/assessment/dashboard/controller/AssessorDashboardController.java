package org.innovateuk.ifs.assessment.dashboard.controller;

import org.innovateuk.ifs.assessment.dashboard.populator.AssessorDashboardModelPopulator;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * This controller will handle all requests that are related to the assessor dashboard.
 */
@Controller
@RequestMapping(value = "/assessor")
@PreAuthorize("hasAuthority('assessor')")
public class AssessorDashboardController {

    @Autowired
    private AssessorDashboardModelPopulator assessorDashboardModelPopulator;

    @PreAuthorize("hasAuthority('assessor')")
    @GetMapping("/dashboard")
    public String dashboard(Model model, @ModelAttribute("loggedInUser") UserResource loggedInUser) {

        model.addAttribute("model", assessorDashboardModelPopulator.populateModel(loggedInUser.getId()));
        return "assessor-dashboard";
    }

    @GetMapping("/terms-and-conditions")
    public String termsAndConditions() {
        return "terms-and-conditions";
    }
}
