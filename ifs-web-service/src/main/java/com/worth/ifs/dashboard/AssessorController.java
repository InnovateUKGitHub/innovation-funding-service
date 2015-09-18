package com.worth.ifs.dashboard;

import com.worth.ifs.application.domain.Application;
import com.worth.ifs.application.service.ApplicationRestService;
import com.worth.ifs.assessment.domain.Assessment;
import com.worth.ifs.assessment.service.AssessmentRestService;
import com.worth.ifs.competition.domain.Competition;
import com.worth.ifs.competition.service.CompetitionsRestService;
import com.worth.ifs.security.UserAuthenticationService;
import com.worth.ifs.user.domain.ProcessRole;
import com.worth.ifs.user.domain.User;
import com.worth.ifs.user.domain.UserRoleType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
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
    private final Log log = LogFactory.getLog(getClass());

    @Autowired
    CompetitionsRestService competitionService;

    @Autowired
    AssessmentRestService assessmentRestService;

    @Autowired
    UserAuthenticationService userAuthenticationService;


    private User getLoggedUser(HttpServletRequest req) {
        return userAuthenticationService.getAuthenticatedUser(req);
    }

    @RequestMapping(value="/dashboard", method= RequestMethod.GET)
    public String dashboard(Model model, HttpServletRequest request) {

        //gets logged user to know what to show
        User user = getLoggedUser(request);

        //for now gets all the competitions to show in the dashboard (assumes user was invited and accepted all)
        List<Competition> competitions = competitionService.getAll();

        Map<Long, Integer> competitionsTotalAssignedAssessments = new HashMap<>();
        Map<Long, Integer> competitionsSubmittedAssessments = new HashMap<>();

        for ( Competition c : competitions ) {
            competitionsTotalAssignedAssessments.put(c.getId(), assessmentRestService.getTotalAssignedByAssessorAndCompetition(getLoggedUser(request).getId(), c.getId()));
            competitionsSubmittedAssessments.put(c.getId(), assessmentRestService.getTotalSubmittedByAssessorAndCompetition(getLoggedUser(request).getId(), c.getId()));
        }

        //pass to view
        model.addAttribute("competitionsForAssessment", competitions);
        model.addAttribute("totalAssignedAssessments", competitionsTotalAssignedAssessments);
        model.addAttribute("submittedAssessments", competitionsSubmittedAssessments);


        return "assessor-dashboard";
    }


}
