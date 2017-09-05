package org.innovateuk.ifs.management.controller;

import org.innovateuk.ifs.management.model.*;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * This controller will handle all requests that are related to the navigation of applications of a Competition within Competition Management.
 */
@Controller
@RequestMapping("/competition/{competitionId}/applications")
@PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance', 'support', 'innovation_lead', 'ifs_admin')")
public class CompetitionManagementNavigateApplicationsController {

    @Autowired
    private NavigateApplicationsModelPopulator navigateApplicationsModelPopulator;


    @GetMapping("/navigate")
    public String navigateApplications(Model model,
                                       @PathVariable("competitionId") long competitionId,
                                       UserResource loggedInUser) {

        model.addAttribute("model", navigateApplicationsModelPopulator.populateModel(competitionId));
        return "competition/navigate-applications";
    }
}
