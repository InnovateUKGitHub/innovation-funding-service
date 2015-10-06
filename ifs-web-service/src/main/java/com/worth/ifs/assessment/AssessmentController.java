package com.worth.ifs.assessment;

import com.worth.ifs.application.AbstractApplicationController;
import com.worth.ifs.application.service.ResponseService;
import com.worth.ifs.assessment.domain.Assessment;
import com.worth.ifs.assessment.domain.AssessmentStates;
import com.worth.ifs.assessment.service.AssessmentRestService;
import com.worth.ifs.competition.domain.Competition;
import com.worth.ifs.competition.service.CompetitionsRestService;
import com.worth.ifs.security.UserAuthenticationService;
import com.worth.ifs.user.domain.ProcessRole;
import com.worth.ifs.user.domain.User;
import com.worth.ifs.util.JsonStatusResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static com.worth.ifs.util.IfsFunctionUtils.requestParameterPresent;

/**
 * This controller will handle requests related to the current applicant. So pages that are relative to that user,
 * are implemented here. For example the my-applications page.
 */
@Controller
@RequestMapping("/assessor")
public class AssessmentController extends AbstractApplicationController {
    private final Log log = LogFactory.getLog(getClass());

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
    @Autowired
    ResponseService responseService;

    private String competitionAssessmentsURL(Long competitionID) {
        return "/assessor/competitions/" + competitionID + "/applications";
    }

    @RequestMapping(value = "/competitions/{competitionId}/applications", method = RequestMethod.GET)
    public String competitionAssessmentDashboard(Model model, @PathVariable("competitionId") final Long competitionId,
                                                 HttpServletRequest request) {

        Competition competition = competitionService.getCompetitionById(competitionId);

        /* gets all the assessments assigned to this assessor in this competition */
        List<Assessment> allAssessments = assessmentRestService.getAllByAssessorAndCompetition(getLoggedUser(request).getId(), competition.getId());
        allAssessments.sort(new AssessmentStatusComparator());

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

    @RequestMapping(value = "/competitions/{competitionId}/applications/{applicationId}/response/{responseId}", method = RequestMethod.PUT, produces = "application/json")
    public @ResponseBody JsonStatusResponse updateQuestionAssessmentFeedback(Model model, @PathVariable("competitionId") final Long competitionId,
                                               @PathVariable("applicationId") final Long applicationId,
                                               @PathVariable("responseId") final Long responseId,
                                               @RequestParam("score") final Optional<Integer> scoreParam,
                                               @RequestParam("confirmationAnswer") Optional<Boolean> confirmationAnswerParam,
                                               @RequestParam("feedbackText") final Optional<String> feedbackTextParam,
                                               HttpServletRequest request) {

        Long userId = getLoggedUser(request).getId();
        requestParameterPresent("score", request).ifPresent(b -> responseService.saveQuestionResponseAssessorScore(userId, responseId, scoreParam.orElse(null)));
        requestParameterPresent("confirmationAnswer", request).ifPresent(b -> responseService.saveQuestionResponseAssessorConfirmationAnswer(userId, responseId, confirmationAnswerParam.orElse(null)));
        requestParameterPresent("feedbackText", request).ifPresent(b -> responseService.saveQuestionResponseAssessorFeedback(userId, responseId, feedbackTextParam.orElse(null)));
        return JsonStatusResponse.ok();

    }

    private String solvePageForApplicationAssessment(Model model, Long competitionId, Long applicationId, Optional<Long> sectionId, Long userId) {
        Assessment assessment = assessmentRestService.getOneByAssessorAndApplication(userId, applicationId);
        boolean invalidAssessment = assessment == null || assessment.getProcessStatus().equals(AssessmentStates.REJECTED.getState());
        boolean pendingApplication = !invalidAssessment && assessment.getProcessStatus().equals(AssessmentStates.PENDING.getState());

        if (invalidAssessment)
            return showInvalidAssessmentView(model, competitionId, assessment);
        else if (pendingApplication) {
            return showApplicationReviewView(model, competitionId, assessment);
        }

        return showReadOnlyApplicationFormView(model, assessment, sectionId, userId);
    }

    private String showReadOnlyApplicationFormView(Model model, Assessment assessment, Optional<Long> sectionId, Long userId) {
        addApplicationDetails(assessment.getApplication().getId(), userId, sectionId, model, true);
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
        Set<String> partners =  assessment.getApplication().getProcessRoles().stream().map(pc -> pc.getOrganisation().getName()).collect(Collectors.toSet());
        model.addAttribute("partners", partners);

        return applicationReview;
    }


    private User getLoggedUser(HttpServletRequest request) {
        return userAuthenticationService.getAuthenticatedUser(request);
    }

    private Long getLoggedUserId( HttpServletRequest request) {
        return getLoggedUser(request).getId();
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
                                                     HttpServletRequest request) {
        getAndPassAssessmentDetails(competitionId, applicationId, getLoggedUserId(request), model);
        return assessmentSubmitReview;
    }


    @RequestMapping(value = "/invitation_answer", method = RequestMethod.POST)
    public String invitationAnswer(Model model, HttpServletRequest request) {
        Map<String, String[]> params = request.getParameterMap();
        if ( params.containsKey("accept") || params.containsKey("reject") ) {
            sendInvitation(request);
        }

        //gets the competition id to redirect
        Long competitionId = Long.valueOf(request.getParameter("competitionId"));
        return "redirect:" + competitionAssessmentsURL(competitionId);
    }

    private void sendInvitation(HttpServletRequest request) {
        Map<String, String[]> params = request.getParameterMap();
        Boolean accept = params.containsKey("accept");
        Long userId = getLoggedUser(request).getId();
        Long applicationId = Long.valueOf(request.getParameter("applicationId"));

        if(accept) {
            acceptInvitation(applicationId, userId);
        } else {
            String decisionReason = params.containsKey("decisionReason") ? request.getParameter("decisionReason") : "none";
            String observations = params.containsKey("observations") ? request.getParameter("observations") : "";
            rejectInvitation(applicationId, userId, decisionReason, observations);
        }
    }

    private void acceptInvitation(Long applicationId, Long userId) {
        Assessment assessment = new Assessment();
        assessmentRestService.acceptAssessmentInvitation(applicationId, userId, assessment);
    }

    private void rejectInvitation(Long applicationId, Long userId, String decisionReason, String observations) {
        Assessment assessment = new Assessment();
        assessment.setDecisionReason(decisionReason);
        assessment.setObservations(observations);
        assessmentRestService.rejectAssessmentInvitation(applicationId, userId, assessment);
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
    public String assessmentSummaryComplete(Model model, @PathVariable("competitionId") final Long competitionId,
                                            @PathVariable("applicationId") final Long applicationId,
                                            HttpServletRequest req)
    {
        Map<String, String[]> params = req.getParameterMap();
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
