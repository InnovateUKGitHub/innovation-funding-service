package com.worth.ifs.assessment;

import com.worth.ifs.application.AbstractApplicationController;
import com.worth.ifs.assessment.constant.AssessmentStatus;
import com.worth.ifs.assessment.domain.Assessment;
import com.worth.ifs.assessment.service.AssessmentRestService;
import com.worth.ifs.competition.domain.Competition;
import com.worth.ifs.competition.service.CompetitionsRestService;
import com.worth.ifs.security.UserAuthenticationService;
import com.worth.ifs.user.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

/**
 * This controller will handle requests related to the current applicant. So pages that are relative to that user,
 * are implemented here. For example the my-applications page.
 */
@Controller
@RequestMapping("/assessor")
public class AssessmentController extends AbstractApplicationController {

    /* pages */
    private final String competitionAssessments = "assessor-competition-applications";
    private final String assessorDashboard = "assessor-dashboard";
    private final String assessmentDetails = "assessment-details";
    private final String assessmentSubmitReview = "assessment-submit-review";
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

        /* gets all the assessments assigned to this assessor in this competition */
        List<Assessment> allAssessments = assessmentRestService.getAllByAssessorAndCompetition(getLoggedUser(request).getId(), competition.getId());

        //filters the assessments to just have the submitted assessments here
        List<Assessment> submittedAssessments = allAssessments.stream().filter(a -> a.isSubmitted()).collect(Collectors.toList());

        //filters the assessments to just the not submmited assessments
        List<Assessment> assessments = allAssessments.stream().filter(a -> ! submittedAssessments.contains(a)).collect(Collectors.toList());

        //pass to view
        model.addAttribute("competition", competition);
        model.addAttribute("assessments", assessments);
        model.addAttribute("submittedAssessments", submittedAssessments);

        return competitionAssessments;
    }

    @RequestMapping(value = "/competitions/{competitionId}/applications/{applicationId}", method = RequestMethod.GET)
    public String applicationAssessmentDetails(Model model, @PathVariable("competitionId") final Long competitionId,
                                               @PathVariable("applicationId") final Long applicationId,
                                               HttpServletRequest req) {

        Long userId = getLoggedUser(req).getId();
        return solvePageForApplicationAssessment(model, competitionId, applicationId, Optional.empty(), userId);
    }

    @RequestMapping(value = "/competitions/{competitionId}/applications/{applicationId}/section/{sectionId}", method = RequestMethod.GET)
    public String applicationAssessmentDetails(Model model, @PathVariable("competitionId") final Long competitionId,
                                               @PathVariable("applicationId") final Long applicationId,
                                               @PathVariable("sectionId") final Long sectionId,
                                               HttpServletRequest req) {

        Long userId = getLoggedUser(req).getId();
        return solvePageForApplicationAssessment(model, competitionId, applicationId, Optional.of(sectionId), userId);
    }

    private String solvePageForApplicationAssessment(Model model, Long competitionId, Long applicationId, Optional<Long> sectionId, Long userId) {

        Assessment assessment = assessmentRestService.getOneByAssessorAndApplication(userId, applicationId);
        boolean invalidAssessment = assessment == null || assessment.getAssessmentStatus().equals(AssessmentStatus.INVALID);
        boolean pendingApplication = !invalidAssessment && assessment.getAssessmentStatus().equals(AssessmentStatus.PENDING);

        if (invalidAssessment)
            return showInvalidAssessmentView(model, competitionId, assessment);
        else if (pendingApplication) {
            return showApplicationReviewView(model, competitionId, assessment);
        }

        return showReadOnlyApplicationFormView(model, assessment, sectionId, userId);
    }

    private String showReadOnlyApplicationFormView(Model model, Assessment assessment, Optional<Long> sectionId, Long userId) {
        addApplicationDetails(assessment.getApplication().getId(), userId, sectionId, model);
        return assessmentDetails;
    }

    private String showInvalidAssessmentView(Model model, Long competitionId, Assessment assessment) {
        Competition competition = competitionService.getCompetitionById(competitionId);
        model.addAttribute("competition", competition);
        model.addAttribute("assessment", assessment);
        return assessorDashboard;
    }

    private String showApplicationReviewView(Model model, Long competitionId, Assessment assessment) {
        Competition competition = competitionService.getCompetitionById(competitionId);
        model.addAttribute("competition", competition);
        model.addAttribute("assessment", assessment);
        return applicationReview;
    }


    private User getLoggedUser(HttpServletRequest req) {
        return userAuthenticationService.getAuthenticatedUser(req);
    }

    private Long getLoggedUserId( HttpServletRequest req) {
        return getLoggedUser(req).getId();
    }


    @RequestMapping(value = "/competitions/{competitionId}/applications/{applicationId}/reject-invitation", method = RequestMethod.GET)
    public String applicationAssessmentDetailsReject(Model model, @PathVariable("competitionId") final Long competitionId,
                                               @PathVariable("applicationId") final Long applicationId,
                                               HttpServletRequest req) {

        getAndPassAssessmentDetails(competitionId, applicationId, getLoggedUserId(req), model);

        return rejectInvitation;
    }

    @RequestMapping(value = "/competitions/{competitionId}/applications/{applicationId}/summary", method = RequestMethod.GET)
    public String getAssessmentSubmitReview(Model model, @PathVariable("competitionId") final Long competitionId,
                                                     @PathVariable("applicationId") final Long applicationId,
                                                     HttpServletRequest req) {

        getAndPassAssessmentDetails(competitionId, applicationId, getLoggedUserId(req), model);

        return assessmentSubmitReview;
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

    @RequestMapping(value = "/submit-assessments", method = RequestMethod.POST)
    public String assessmentsSubmissions(Model model, HttpServletRequest req) {

        Map<String, String[]> params = req.getParameterMap();

        /*** avoids to trigger an response if any other button was clicked that not accept / reject ***/
        if ( params.containsKey("submit_assessments") && params.containsKey("submitted[]") ) {

            // gets the set of assessments Ids to be submitted
            Set<Long> assessmentsToSubmit = convertStringListToLongSet(Arrays.asList(req.getParameterValues("submitted[]")));
            //gets the logged assessor Id
            Long userId = getLoggedUser(req).getId();

            /** asserts the invitation response **/
            assessmentRestService.submitAssessments(userId, assessmentsToSubmit);
        }

        //gets the competition id to redirect
        Long competitionId = Long.valueOf(req.getParameter("competitionId"));
        return "redirect:" + competitionAssessmentsURL(competitionId);
    }

    @RequestMapping(value = "/competitions/{competitionId}/applications/{applicationId}/complete", method = RequestMethod.POST)
    public String assessmentSubmissionComplete(Model model, @PathVariable("competitionId") final Long competitionId,
                                            @PathVariable("applicationId") final Long applicationId,
                                            HttpServletRequest req)
    {

        Map<String, String[]> params = req.getParameterMap();

        System.out.println("AssessmentController - complete - has the button? " + params.containsKey("confirm-submission"));

        if ( params.containsKey("confirm-submission") ) {

            //gets the logged assessor Id
            Long userId = getLoggedUser(req).getId();

            //gets the values from the form
            String suitableForFundingValue = req.getParameter("is-suitable-for-funding");
            String suitableFeedback = req.getParameter("suitable-for-funding-feedback");
            String commentsToShare = req.getParameter("comments-to-share");

            /** asserts the invitation response **/
            if ( assessmentSummaryIsValidToSave(suitableForFundingValue, suitableFeedback) )
                assessmentRestService.saveAssessmentSummary(userId, applicationId, suitableForFundingValue, suitableFeedback, commentsToShare);
        }

        //gets the competition id to redirect
        return "redirect:" + competitionAssessmentsURL(competitionId);
    }

    private Set<Long> convertStringListToLongSet(List<String> aList)
    {
        Set<Long> converted = new HashSet<>();
        for ( String value : aList )
            converted.add(Long.valueOf(value));

        return converted;
    }

    public boolean assessmentSummaryIsValidToSave(String recommendationValue, String feedback) {
        return ! (recommendationValue.equals("no") && feedback.isEmpty());
    }


    private void getAndPassAssessmentDetails(Long competitionId, Long applicationId, Long userId, Model model) {
        //gets
        Competition competition = competitionService.getCompetitionById(competitionId);
        Assessment assessment = assessmentRestService.getOneByAssessorAndApplication(userId, applicationId);

        //pass to view
        model.addAttribute("competition", competition);
        model.addAttribute("assessment", assessment);
    }
}
