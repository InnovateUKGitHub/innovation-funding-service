package com.worth.ifs.assessment.controller.dashboard;

import com.worth.ifs.assessment.model.AssessorDashboardModelPopulator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletResponse;

/**
 * This controller will handle all requests that are related to the assessor dashboard.
 */
@Controller
@RequestMapping(value = "/assessor")
public class AssessorDashboardController {

    @Autowired
    private AssessorDashboardModelPopulator assessorDashboardModelPopulator;

    @RequestMapping(value = "/dashboard", method = RequestMethod.GET)
    public String dashboard(final Model model,
                            final HttpServletResponse response) {

        model.addAttribute("model", assessorDashboardModelPopulator.populateModel());
        return "assessor-dashboard";
    }
}