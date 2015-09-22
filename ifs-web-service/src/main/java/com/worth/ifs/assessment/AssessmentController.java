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

    /* pages */
    private final String competitionAssessments = "assessor-competition-applications";
    private final String assessorDashboard = "assessor-dashboard";
    private final String assessmentDetails = "assessment-details";
    private final String applicationReview = "application-assessment-review";
    private final String rejectInvitation = "reject-assessment-invitation";


    @Autowired
    CompetitionsRestService competitionService;
    @Autowired
    AssessmentRestService assessmentRestService;
    @Autowired
    UserAuthenticationService userAuthenticationService;

    private String competitionAssessmentsURL(Long competitionID) {
        return "/assessor/competitions/" + competitionID + "/applications";
    }


    @RequestMapping(value = "/competitions/{competitionId}/applications", method = RequestMethod.GET)
    public String competitionAssessmentDashboard(Model model, @PathVariable("competitionId") final Long competitionId,
                                                 HttpServletRequest request) {

        Competition competition = competitionService.getCompetitionById(competitionId);

        List<Assessment> assessments = assessmentRestService.getAllByAssessorAndCompetition(getLoggedUser(request).getId(), competition.getId());

        //pass to view
        model.addAttribute("competition", competition);
        model.addAttribute("assessments", assessments);
        return competitionAssessments;
    }

    @RequestMapping(value = "/competitions/{competitionId}/applications/{applicationId}", method = RequestMethod.GET)
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

        if (assessment == null || assessment.getAssessmentStatus().equals(AssessmentStatus.INVALID))
            pageToShow = assessorDashboard;
        else if (assessment.getAssessmentStatus().equals(AssessmentStatus.PENDING))
            pageToShow = applicationReview;
        else
            pageToShow = assessmentDetails;


        return pageToShow;
    }


    private User getLoggedUser(HttpServletRequest req) {
        return userAuthenticationService.getAuthenticatedUser(req);
    }


    @RequestMapping(value = "/competitions/{competitionId}/applications/{applicationId}/reject-invitation", method = RequestMethod.GET)
    public String applicationAssessmentDetailsReject(Model model, @PathVariable("competitionId") final Long competitionId,
                                               @PathVariable("applicationId") final Long applicationId,
                                               HttpServletRequest req) {

        Competition competition = competitionService.getCompetitionById(competitionId);
        Assessment assessment = assessmentRestService.getOneByAssessorAndApplication(getLoggedUser(req).getId(), applicationId);

        //pass to view
        model.addAttribute("competition", competition);
        model.addAttribute("assessment", assessment);

        return rejectInvitation;
    }




    @RequestMapping(value = "/invitation_answer", method = RequestMethod.POST)
    public String invitationAnswer(Model model, HttpServletRequest req) {

        Map<String, String[]> params = req.getParameterMap();

        /*** avoids to trigger an response if any other button was clicked that not accept / reject ***/
        if ( params.containsKey("accept") || params.containsKey("reject") ) {

            /** builds invitation response data **/
            Boolean decision = params.containsKey("accept");
            Long userId = getLoggedUser(req).getId();
            Long applicationId = Long.valueOf(req.getParameter("applicationId"));
            String decisionReason = params.containsKey("decisionReason") ? req.getParameter("decisionReason") : "none";
            String observations = params.containsKey("observations") ? req.getParameter("observations") : "";

            /** asserts the invitation response **/
            assessmentRestService.respondToAssessmentInvitation(userId, applicationId, decision, decisionReason, observations);
        }

        //gets the competition id to redirect
        Long competitionId = Long.valueOf(req.getParameter("competitionId"));
        return "redirect:" + competitionAssessmentsURL(competitionId);
    }


}
