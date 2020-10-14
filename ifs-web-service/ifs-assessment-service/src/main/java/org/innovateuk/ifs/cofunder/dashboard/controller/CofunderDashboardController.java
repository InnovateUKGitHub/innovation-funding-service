package org.innovateuk.ifs.cofunder.dashboard.controller;

import org.innovateuk.ifs.cofunder.dashboard.populator.CofunderDashboardModelPopulator;
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
 * This controller will handle all requests that are related to the cofunder dashboard.
 */
@Controller
@RequestMapping(value = "/cofunder")
@SecuredBySpring(value = "Controller", description = "Cofunders can view their dashboard", securedType = CofunderDashboardController.class)
@PreAuthorize("hasAuthority('cofunder')")
public class CofunderDashboardController {

    @Autowired
    private CofunderDashboardModelPopulator cofunderDashboardModelPopulator;

    @GetMapping("/dashboard")
    @NavigationRoot
    public String dashboard(Model model, UserResource loggedInUser) {
        model.addAttribute("model", cofunderDashboardModelPopulator.populateModel(loggedInUser));
        return "cofunder/cofunder-dashboard";
    }
}
