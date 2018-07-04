package org.innovateuk.ifs.assessment.dashboard.controller;

import org.innovateuk.ifs.assessment.dashboard.populator.AssessorCompetitionForInterviewDashboardModelPopulator;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * This controller will handle all requests that are related to the assessor interview dashboard.
 */
@Controller
@RequestMapping(value = "/assessor")
@SecuredBySpring(value = "Controller", description = "Assessors can access the assessment interview dashboard", securedType = AssessorCompetitionForInterviewDashboardController.class)
public class AssessorCompetitionForInterviewDashboardController {

    @Autowired
    private AssessorCompetitionForInterviewDashboardModelPopulator assessorCompetitionForInterviewDashboardModelPopulator;

    @GetMapping("/dashboard/competition/{competitionId}/interview")
    @PreAuthorize("hasPermission(#competitionId, 'org.innovateuk.ifs.competition.resource.CompetitionCompositeId', 'ASSESSOR_INTERVIEW')")
    public String viewApplications( Model model,
                                    @PathVariable("competitionId") long competitionId,
                                    UserResource loggedInUser,
                                    @RequestParam(value = "origin", defaultValue = "ASSESSOR_INTERVIEW") String origin,
                                    @RequestParam MultiValueMap<String, String> queryParams
                                ) {
        model.addAttribute("model", assessorCompetitionForInterviewDashboardModelPopulator.populateModel(competitionId, loggedInUser.getId(), origin, queryParams));
        return "assessor-interview-applications";
    }

}