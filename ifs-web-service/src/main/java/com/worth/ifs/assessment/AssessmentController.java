package com.worth.ifs.assessment;

import com.worth.ifs.application.AbstractApplicationController;
import com.worth.ifs.application.Form;
import com.worth.ifs.application.domain.Response;
import com.worth.ifs.application.resource.ApplicationResource;
import com.worth.ifs.assessment.domain.Assessment;
import com.worth.ifs.assessment.domain.AssessmentStates;
import com.worth.ifs.assessment.service.AssessmentRestService;
import com.worth.ifs.assessment.viewmodel.AssessmentSubmitReviewModel;
import com.worth.ifs.competition.domain.Competition;
import com.worth.ifs.user.domain.Organisation;
import com.worth.ifs.user.domain.ProcessRole;
import com.worth.ifs.user.domain.User;
import com.worth.ifs.user.domain.UserRoleType;
import com.worth.ifs.util.JsonStatusResponse;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

/**
 * This controller will handle requests related to the current applicant. So pages that are relative to that user,
 * are implemented here. For example the my-applications page.
 */
@Controller
@RequestMapping("/assessor")
public class AssessmentController extends AbstractApplicationController {
    private final Log log = LogFactory.getLog(getClass());

    /* pages */
    private static final String competitionAssessments = "assessor-competition-applications";
    private static final String assessorDashboard = "assessor-dashboard";
    private static final String assessmentDetails = "assessment-details";
    private static final String assessmentSubmitReview = "assessment-submit-review";
    private static final String applicationReview = "application-assessment-review";
    private static final String rejectInvitation = "reject-assessment-invitation";

    @Autowired
    AssessmentRestService assessmentRestService;

    private String competitionAssessmentsURL(Long competitionID) {
        return "/assessor/competitions/" + competitionID + "/applications";
    }

    @RequestMapping(value = "/competitions/{competitionId}/applications", method = RequestMethod.GET)
    public String competitionAssessmentDashboard(Model model, @PathVariable("competitionId") final Long competitionId,
                                                 HttpServletRequest request) {

        Competition competition = competitionService.getById(competitionId);

        /* gets all the assessments assigned to this assessor in this competition */
        List<Assessment> allAssessments = assessmentRestService.getAllByAssessorAndCompetition(getLoggedUser(request).getId(), competition.getId());
        allAssessments.sort(new AssessmentStatusComparator());

        //filters the assessments to just have the submitted assessments here
        List<Assessment> submittedAssessments = allAssessments.stream().filter(Assessment::isSubmitted).collect(toList());

        //filters the assessments to just the not submmited assessments
        List<Assessment> assessments = allAssessments.stream().filter(a -> ! submittedAssessments.contains(a)).collect(toList());

        //pass to view
        model.addAttribute("competition", competition);
        model.addAttribute("assessments", assessments);
        model.addAttribute("submittedAssessments", submittedAssessments);

        return competitionAssessments;
    }

    @RequestMapping(value = "/competitions/{competitionId}/applications/{applicationId}", method = RequestMethod.GET)
    public String applicationAssessmentDetails(@ModelAttribute("form") @Valid Form form, BindingResult bindingResult, Model model, @PathVariable("competitionId") final Long competitionId,
                                               @PathVariable("applicationId") final Long applicationId,
                                               HttpServletRequest req) {

        Long userId = getLoggedUser(req).getId();
        form.bindingResult = bindingResult;
        form.objectErrors = bindingResult.getAllErrors();
        model.addAttribute("form", form);
        return solvePageForApplicationAssessment(model, competitionId, applicationId, Optional.empty(), userId);
    }

    @RequestMapping(value = "/competitions/{competitionId}/applications/{applicationId}/section/{sectionId}", method = RequestMethod.GET)
    public String applicationAssessmentDetails(@ModelAttribute("form") @Valid Form form, BindingResult bindingResult, Model model, @PathVariable("competitionId") final Long competitionId,
                                               @PathVariable("applicationId") final Long applicationId,
                                               @PathVariable("sectionId") final Long sectionId,
                                               HttpServletRequest req) {

        Long userId = getLoggedUser(req).getId();
        form.bindingResult = bindingResult;
        form.objectErrors = bindingResult.getAllErrors();
        model.addAttribute("form", form);
        return solvePageForApplicationAssessment(model, competitionId, applicationId, Optional.of(sectionId), userId);
    }

    @RequestMapping(value = "/competitions/{competitionId}/applications/{applicationId}/response/{responseId}", method = RequestMethod.PUT, produces = "application/json")
    public @ResponseBody JsonStatusResponse updateQuestionAssessmentFeedback(Model model, @PathVariable("competitionId") final Long competitionId,
                                               @PathVariable("applicationId") final Long applicationId,
                                               @PathVariable("responseId") final Long responseId,
                                               @RequestParam("feedbackValue") final Optional<String> feedbackValueParam,
                                               @RequestParam("feedbackText") final Optional<String> feedbackTextParam,
                                               HttpServletRequest request, HttpServletResponse response) {

        Long userId = getLoggedUser(request).getId();
        Boolean success = responseService.saveQuestionResponseAssessorFeedback(userId, responseId, feedbackValueParam, feedbackTextParam);

        if (success) {
            return JsonStatusResponse.ok();
        } else {
            return JsonStatusResponse.badRequest("Unable to update feedback", response);
        }

    }

    private String solvePageForApplicationAssessment(Model model, Long competitionId, Long applicationId, Optional<Long> sectionId, Long userId) {

        Assessment assessment = assessmentRestService.getOneByAssessorAndApplication(userId, applicationId);
        if (assessment == null) {
            log.warn("No assessment could be found for the User " + userId + " and the Application " + applicationId);
            return showInvalidAssessmentView(model, competitionId, assessment);
        }

        boolean invalidAssessment = assessment.getProcessStatus().equals(AssessmentStates.REJECTED.getState());
        if (invalidAssessment) {
            return showInvalidAssessmentView(model, competitionId, assessment);
        }

        boolean pendingApplication = !invalidAssessment && assessment.getProcessStatus().equals(AssessmentStates.PENDING.getState());
        if (pendingApplication) {
            return showApplicationReviewView(model, competitionId, userId, assessment);
        }

        ProcessRole assessorProcessRole = processRoleService.findProcessRole(userId, new ApplicationResource(assessment.getApplication()).getId());
        boolean invalidAssessor = assessorProcessRole == null || !assessorProcessRole.getRole().getName().equals(UserRoleType.ASSESSOR.getName());
        if (invalidAssessor) {
            log.warn("User is not an Assessor on this application");
            return showInvalidAssessmentView(model, competitionId, assessment);
        }

        return showReadOnlyApplicationFormView(model, assessment, sectionId, userId, assessorProcessRole);
    }

    private String showReadOnlyApplicationFormView(Model model, Assessment assessment, Optional<Long> sectionId, Long userId, ProcessRole assessorProcessRole) {
        addApplicationDetails(new ApplicationResource(assessment.getApplication()).getId(), userId, sectionId, model, null);
        addSectionDetails(model, new ApplicationResource(assessment.getApplication()), sectionId, true);

        List<Response> questionResponses = responseService.getByApplication(new ApplicationResource(assessment.getApplication()).getId());
        Map<Long, Response> questionResponsesMap = responseService.mapResponsesToQuestion(questionResponses);

        model.addAttribute("processRole", assessorProcessRole);
        model.addAttribute("questionResponses", questionResponsesMap);
        addFinanceDetails(model, new ApplicationResource(assessment.getApplication()));
        return assessmentDetails;
    }

    private String showInvalidAssessmentView(Model model, Long competitionId, Assessment assessment) {
        Competition competition = competitionService.getById(competitionId);
        model.addAttribute("competition", competition);
        model.addAttribute("assessment", assessment);
        return assessorDashboard;
    }

    private String showApplicationReviewView(Model model, Long competitionId, Long userId, Assessment assessment) {
        ApplicationResource application = applicationService.getById(assessment.getApplication().getId());
        getAndPassAssessmentDetails(competitionId, application.getId(), userId, model);
        List<Long> processRoleIds = application.getProcessRoleIds();
        Set<String> partners = processRoleIds.stream()
                .map(id -> processRoleService.getById(id))
                .map(ProcessRole::getOrganisation)
                .map(Organisation::getName)
            .collect(Collectors.toSet());
        model.addAttribute("partners", partners);

        return applicationReview;
    }


    @ModelAttribute
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
    public ModelAndView getAssessmentSubmitReview(Model model, @PathVariable("competitionId") final Long competitionId,
                                            @PathVariable("applicationId") final Long applicationId,
                                            User user) {

        Assessment assessment = assessmentRestService.getOneByAssessorAndApplication(user.getId(), applicationId);
        List<Response> responses = getResponses(new ApplicationResource(assessment.getApplication()));
        ProcessRole assessorProcessRole = processRoleService.findProcessRole(user.getId(), applicationId);

        if (assessorProcessRole == null || !assessorProcessRole.getRole().getName().equals(UserRoleType.ASSESSOR.getName())) {
            throw new IllegalStateException("User is not an Assessor on this application");
        }

        AssessmentSubmitReviewModel viewModel = new AssessmentSubmitReviewModel(assessment, responses, assessorProcessRole);

        return new ModelAndView(assessmentSubmitReview, "model", viewModel);
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

    @RequestMapping(value = "/confirm-submit")
    public String confirmSubmit(Model model, HttpServletRequest req) {
        return "assessment-confirm-submit";
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
            String overallScore = req.getParameter("overall-score");

            /** asserts the invitation response **/
            if ( assessmentSummaryIsValidToSave(suitableForFundingValue, suitableFeedback) )
                assessmentRestService.saveAssessmentSummary(userId, applicationId, suitableForFundingValue, suitableFeedback, commentsToShare, Double.valueOf(overallScore));
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


    private Pair<Competition, Assessment> getAndPassAssessmentDetails(Long competitionId, Long applicationId, Long userId, Model model) {
        //gets
        Competition competition = competitionService.getById(competitionId);
        Assessment assessment = assessmentRestService.getOneByAssessorAndApplication(userId, applicationId);

        //pass to view
        model.addAttribute("competition", competition);
        model.addAttribute("assessment", assessment);

        return Pair.of(competition, assessment);
    }
}
