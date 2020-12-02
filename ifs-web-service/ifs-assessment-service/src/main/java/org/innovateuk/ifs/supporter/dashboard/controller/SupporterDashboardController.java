package org.innovateuk.ifs.supporter.dashboard.controller;

import org.innovateuk.ifs.supporter.dashboard.populator.SupporterDashboardModelPopulator;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.navigation.NavigationRoot;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * This controller will handle all requests that are related to the supporter dashboard.
 */
@Controller
@RequestMapping(value = "/supporter")
@SecuredBySpring(value = "Controller", description = "Supporters can view their dashboard", securedType = SupporterDashboardController.class)
@PreAuthorize("hasAuthority('supporter')")
public class SupporterDashboardController {

    @Autowired
    private SupporterDashboardModelPopulator supporterDashboardModelPopulator;

    @GetMapping("/dashboard")
    @NavigationRoot
    public String dashboard(Model model, UserResource loggedInUser) {
        model.addAttribute("model", supporterDashboardModelPopulator.populateModel(loggedInUser));
        return "supporter/supporter-dashboard";
    }
}
