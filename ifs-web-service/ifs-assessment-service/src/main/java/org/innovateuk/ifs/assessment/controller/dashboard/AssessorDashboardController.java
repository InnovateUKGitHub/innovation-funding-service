package org.innovateuk.ifs.assessment.controller.dashboard;

import org.innovateuk.ifs.assessment.model.AssessorDashboardModelPopulator;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * This controller will handle all requests that are related to the assessor dashboard.
 */
@Controller
@RequestMapping(value = "/assessor")
public class AssessorDashboardController {

    @Autowired
    private AssessorDashboardModelPopulator assessorDashboardModelPopulator;

    @RequestMapping(value = "/dashboard", method = RequestMethod.GET)
    public String dashboard(Model model, @ModelAttribute("loggedInUser") UserResource loggedInUser) {

        model.addAttribute("model", assessorDashboardModelPopulator.populateModel(loggedInUser.getId()));
        return "assessor-dashboard";
    }

    @RequestMapping(value = "/terms-and-conditions", method = RequestMethod.GET)
    public String termsAndConditions() {
        return "terms-and-conditions";
    }
}
