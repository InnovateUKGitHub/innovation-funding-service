package com.worth.ifs.assessment.controller.dashboard;

import com.worth.ifs.assessment.model.AssessorDashboardModelPopulator;
import com.worth.ifs.commons.security.UserAuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;

/**
 * This controller will handle all requests that are related to the assessor dashboard.
 */
@Controller
@RequestMapping(value = "/assessor")
public class AssessorDashboardController {

    @Autowired
    private AssessorDashboardModelPopulator assessorDashboardModelPopulator;

    @Autowired
    private UserAuthenticationService userAuthenticationService;

    @RequestMapping(value = "/dashboard", method = RequestMethod.GET)
    public String dashboard(Model model,
                            HttpServletRequest request) {

        Long userId = userAuthenticationService.getAuthenticatedUser(request).getId();
        model.addAttribute("model", assessorDashboardModelPopulator.populateModel(userId));
        return "assessor-dashboard";
    }
}