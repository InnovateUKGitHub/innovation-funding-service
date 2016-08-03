package com.worth.ifs.assessment.controller.dashboard;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping(value = "/assessor")
public class AssessorDashboardController {

    private static String DASHBOARD = "assessor-dashboard";
    private static String COMPETITION_DASHBOARD = "assessor-competition-dashboard";

    @RequestMapping(value = "/dashboard", method = RequestMethod.GET)
    public String dashboard(final Model model,
                            final HttpServletResponse response) {
        return DASHBOARD;
    }

    @RequestMapping(value = "/dashboard/competition/{competitionId}", method = RequestMethod.GET)
    public String competitionDashboard(final Model model,
                                       final HttpServletResponse response,
                                       @PathVariable("competitionId") final Long competitionId) {
        return COMPETITION_DASHBOARD;
    }
}