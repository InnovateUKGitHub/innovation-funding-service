package com.worth.ifs.assessment.controller.dashboard;

import com.worth.ifs.assessment.model.AssessorCompetitionDashboardModelPopulator;
import com.worth.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * This controller will handle all requests that are related to the assessor competition dashboard.
 */
@Controller
@RequestMapping(value = "/assessor")
public class AssessorCompetitionDashboardController {

    @Autowired
    private AssessorCompetitionDashboardModelPopulator assessorCompetitionDashboardModelPopulator;

    @RequestMapping(value = "/dashboard/competition/{competitionId}", method = RequestMethod.GET)
    public String competitionDashboard(final Model model,
                                       @ModelAttribute("loggedInUser") UserResource loggedInUser,
                                       @PathVariable("competitionId") final Long competitionId) {

        model.addAttribute("model", assessorCompetitionDashboardModelPopulator.populateModel(competitionId, loggedInUser.getId()));
        return "assessor-competition-dashboard";
    }
}