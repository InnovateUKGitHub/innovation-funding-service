package com.worth.ifs.assessment;

import com.worth.ifs.application.domain.Application;
import com.worth.ifs.application.service.ApplicationRestService;
import com.worth.ifs.assessment.constant.AssessmentStatus;
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
public class AssessmentController {
    private final Log log = LogFactory.getLog(getClass());

    @Autowired
    CompetitionsRestService competitionService;

    @Autowired
    AssessmentRestService assessmentRestService;

    @Autowired
    UserAuthenticationService userAuthenticationService;


    @RequestMapping(value="/competitions/{competitionId}/applications", method= RequestMethod.GET)
    public String competitionAssessmentDashboard(Model model, @PathVariable("competitionId") final Long competitionId,
                                          HttpServletRequest request) {

        Competition competition = competitionService.getCompetitionById(competitionId);

        List<Assessment> assessments = assessmentRestService.getAllByAssessorAndCompetition(getLoggedUser(request).getId(), competition.getId());

        //pass to view
        model.addAttribute("competition", competition);
        model.addAttribute("assessments", assessments);

        return "assessor-competition-applications";
    }

    @RequestMapping(value="/competitions/{competitionId}/applications/{applicationId}", method= RequestMethod.GET)
    public String applicationAssessmentDetails(Model model, @PathVariable("competitionId") final Long competitionId,
                                                        @PathVariable("applicationId") final Long applicationId,
                                                        HttpServletRequest req) {


        Competition competition = competitionService.getCompetitionById(competitionId);
        Assessment assessment = assessmentRestService.getOneByAssessorAndApplication(getLoggedUser(req).getId(), applicationId);

        //pass to view
        model.addAttribute("competition", competition);
        model.addAttribute("assessment", assessment);

        return solvePageForApplicationAssessment(assessment);
    }

    private String solvePageForApplicationAssessment(Assessment assessment) {

        String pageToShow;


        System.out.println("solvePageForApplicationAssessment - Assessment has id ? " + assessment == null ? -50 : assessment.getId());

        if ( assessment == null || assessment.getStatus().equals(AssessmentStatus.INVALID) )
            pageToShow = "assessor-dashboard";
        else if ( assessment.getStatus().equals(AssessmentStatus.PENDING) )
            pageToShow = "application-assessment-review";
        else {
            pageToShow = "assessment-details";
        }

        return pageToShow;
    }


    private User getLoggedUser(HttpServletRequest req) {
        return userAuthenticationService.getAuthenticatedUser(req);
    }


}
