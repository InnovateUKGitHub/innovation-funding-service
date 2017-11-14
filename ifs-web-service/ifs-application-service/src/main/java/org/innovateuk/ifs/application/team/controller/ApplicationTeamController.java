package org.innovateuk.ifs.application.team.controller;

import org.innovateuk.ifs.application.team.populator.ApplicationTeamModelPopulator;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * This controller will handle all requests that are related to the read only view of the application team.
 */
@Controller
@RequestMapping("/application/{applicationId}")
@PreAuthorize("hasAuthority('applicant')")
public class ApplicationTeamController {

    @Autowired
    private ApplicationTeamModelPopulator applicationTeamModelPopulator;

    @GetMapping("/team")
    @PreAuthorize("hasPermission(#applicationId, 'VIEW_APPLICATION_TEAM_PAGE')")
    public String getApplicationTeam(Model model, @PathVariable("applicationId") long applicationId,
                                     UserResource loggedInUser) {
        model.addAttribute("model", applicationTeamModelPopulator.populateModel(applicationId, loggedInUser.getId()));
        return "application-team/team";
    }
}