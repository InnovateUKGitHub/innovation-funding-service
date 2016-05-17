package com.worth.ifs.dashboard;

import com.worth.ifs.application.service.CompetitionService;
import com.worth.ifs.assessment.service.AssessmentRestService;
import com.worth.ifs.commons.security.UserAuthenticationService;
import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This controller will handle requests related to the current applicant. So pages that are relative to that user,
 * are implemented here. For example the my-applications page.
 */
@Controller
@RequestMapping("/assessor")
public class AssessorController {

    @Autowired
    CompetitionService competitionService;

    @Autowired
    AssessmentRestService assessmentRestService;

    @Autowired
    UserAuthenticationService userAuthenticationService;


    private UserResource getLoggedUser(HttpServletRequest req) {
        return userAuthenticationService.getAuthenticatedUser(req);
    }

    @RequestMapping(value="/dashboard", method= RequestMethod.GET)
    public String dashboard(Model model, HttpServletRequest request) {

        //for now gets all the competitions to show in the dashboard (assumes user was invited and accepted all)

        // TODO DW - INFUND-1555 - handle success or failure properly
        List<CompetitionResource> competitions = competitionService.getAllCompetitions();

        Map<Long, Integer> competitionsTotalAssignedAssessments = new HashMap<>();
        Map<Long, Integer> competitionsSubmittedAssessments = new HashMap<>();

        for ( CompetitionResource c : competitions ) {
            competitionsTotalAssignedAssessments.put(c.getId(), assessmentRestService.getTotalAssignedByAssessorAndCompetition(getLoggedUser(request).getId(), c.getId()).getSuccessObjectOrThrowException());
            competitionsSubmittedAssessments.put(c.getId(), assessmentRestService.getTotalSubmittedByAssessorAndCompetition(getLoggedUser(request).getId(), c.getId()).getSuccessObjectOrThrowException());
        }

        //pass to view
        model.addAttribute("competitionsForAssessment", competitions);
        model.addAttribute("totalAssignedAssessments", competitionsTotalAssignedAssessments);
        model.addAttribute("submittedAssessments", competitionsSubmittedAssessments);


        return "assessor-dashboard";
    }


}
