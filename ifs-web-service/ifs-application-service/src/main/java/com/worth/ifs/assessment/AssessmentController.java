package com.worth.ifs.assessment;

import com.worth.ifs.application.AbstractApplicationController;
import com.worth.ifs.application.domain.Question;
import com.worth.ifs.application.domain.Response;
import com.worth.ifs.application.form.Form;
import com.worth.ifs.application.resource.ApplicationResource;
import com.worth.ifs.application.resource.SectionResource;
import com.worth.ifs.assessment.domain.Assessment;
import com.worth.ifs.assessment.domain.AssessmentStates;
import com.worth.ifs.assessment.dto.Score;
import com.worth.ifs.assessment.service.AssessmentRestService;
import com.worth.ifs.assessment.viewmodel.AssessmentDashboardModel;
import com.worth.ifs.assessment.viewmodel.AssessmentSubmitReviewModel;
import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.user.domain.Organisation;
import com.worth.ifs.user.domain.ProcessRole;
import com.worth.ifs.user.domain.User;
import com.worth.ifs.user.domain.UserRoleType;
import com.worth.ifs.workflow.domain.ProcessOutcome;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.*;
import java.util.stream.Collectors;

import static com.worth.ifs.application.service.Futures.call;
import static com.worth.ifs.util.CollectionFunctions.simpleMap;
import static java.util.Optional.empty;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.OK;

/**
 * This controller will handle requests related to the current applicant. So pages that are relative to that user,
 * are implemented here. For example the my-applications page.
 */
@Controller
@RequestMapping("/assessor")
public class AssessmentController extends AbstractApplicationController {
    private static final Log LOG = LogFactory.getLog(AssessmentController.class);

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

    // TODO DW - INFUND-1555 - get below code to use the RestResults
    @RequestMapping(value = "/competitions/{competitionId}/applications", method = RequestMethod.GET)
    public ModelAndView competitionAssessmentDashboard(@PathVariable("competitionId") final Long competitionId,
                                                       HttpServletRequest request) {

        CompetitionResource competition = competitionService.getById(competitionId);

        /* gets all the assessments assigned to this assessor in this competition */
        List<Assessment> allAssessments = assessmentRestService.getAllByAssessorAndCompetition(getLoggedUser(request).getId(), competition.getId()).getSuccessObjectOrThrowException();
        allAssessments.sort(new AssessmentStatusComparator());

        List<AssessmentDashboardModel.AssessmentWithApplicationAndScore> assessments = allAssessments.stream()
                .filter(a -> !a.isSubmitted())
                .map(a -> {
                    ApplicationResource ar = applicationService.findByProcessRoleId(a.getProcessRole().getId()).getSuccessObjectOrThrowException();
                    Score score = assessmentRestService.getScore(a.getId()).getSuccessObjectOrThrowException();
                    return new AssessmentDashboardModel.AssessmentWithApplicationAndScore(a, ar, score);
                }).collect(toList());

        long noOfAssessmentsStartedAwaitingSubmission = assessments.stream().filter(a -> a.getAssessment().hasAssessmentStarted()).count();

        List<AssessmentDashboardModel.AssessmentWithApplicationAndScore> submittedAssessments = allAssessments.stream()
                .filter(Assessment::isSubmitted)
                .map(a -> {
                    ApplicationResource ar = applicationService.findByProcessRoleId(a.getProcessRole().getId()).getSuccessObjectOrThrowException();
                    Score score = assessmentRestService.getScore(a.getId()).getSuccessObjectOrThrowException();
                    return new AssessmentDashboardModel.AssessmentWithApplicationAndScore(a, ar, score);
                })
                .collect(toList());

        AssessmentDashboardModel viewModel = new AssessmentDashboardModel(assessments, submittedAssessments, noOfAssessmentsStartedAwaitingSubmission, competition);

        return new ModelAndView(competitionAssessments, "model", viewModel);
    }

    @RequestMapping(value = "/competitions/{competitionId}/applications/{applicationId}", method = RequestMethod.GET)
    public String applicationAssessmentDetails(@ModelAttribute("form") @Valid Form form, BindingResult bindingResult, Model model, @PathVariable("competitionId") final Long competitionId,
                                               @PathVariable("applicationId") final Long applicationId,
                                               HttpServletRequest req) {

        Long userId = getLoggedUser(req).getId();
        form.setBindingResult(bindingResult);
        form.setObjectErrors(bindingResult.getAllErrors());
        model.addAttribute("form", form);
        List<ProcessRole> userApplicationRoles = processRoleService.findProcessRolesByApplicationId(applicationId);
        return solvePageForApplicationAssessment(model, competitionId, applicationId, empty(), userId, userApplicationRoles);
    }

    @RequestMapping(value = "/competitions/{competitionId}/applications/{applicationId}/section/{sectionId}", method = RequestMethod.GET)
    public String applicationAssessmentDetails(@ModelAttribute("form") @Valid Form form, BindingResult bindingResult, Model model, @PathVariable("competitionId") final Long competitionId,
                                               @PathVariable("applicationId") final Long applicationId,
                                               @PathVariable("sectionId") final Long sectionId,
                                               HttpServletRequest req) {

        Long userId = getLoggedUser(req).getId();
        form.setBindingResult(bindingResult);
        form.setObjectErrors(bindingResult.getAllErrors());
        model.addAttribute("form", form);
        List<ProcessRole> userApplicationRoles = processRoleService.findProcessRolesByApplicationId(applicationId);
        return solvePageForApplicationAssessment(model, competitionId, applicationId, Optional.of(sectionId), userId, userApplicationRoles);
    }

    @RequestMapping(value = "/competitions/{competitionId}/applications/{applicationId}/response/{responseId}", method = RequestMethod.PUT, produces = "application/json")
    public ResponseEntity<Void> updateQuestionAssessmentFeedback(@PathVariable("responseId") final Long responseId,
                                                              @RequestParam("feedbackValue") final Optional<String> feedbackValueParam,
                                                              @RequestParam("feedbackText") final Optional<String> feedbackTextParam,
                                                              HttpServletRequest request) {

        Long userId = getLoggedUser(request).getId();
        RestResult<Void> result = responseService.saveQuestionResponseAssessorFeedback(userId, responseId, feedbackValueParam, feedbackTextParam);

        // TODO DW - INFUND-854 - develop a handler in the web layer for RestResults
        return result.handleSuccessOrFailure(
                failure -> new ResponseEntity<>(BAD_REQUEST),
                success -> new ResponseEntity<>(OK)
        );
    }

    private String solvePageForApplicationAssessment(
            Model model,
            Long competitionId,
            Long applicationId,
            Optional<Long> sectionId,
            Long userId,
            List<ProcessRole> userApplicationRoles) {
        ProcessRole assessorProcessRole = processRoleService.findProcessRole(userId, applicationId);
        boolean invalidAssessor = assessorProcessRole == null || !assessorProcessRole.getRole().getName().equals(UserRoleType.ASSESSOR.getName());
        if (invalidAssessor) {
            LOG.warn("User is not an Assessor on this application");
            return showInvalidAssessmentView(model, competitionId, null);
        }
        Assessment assessment = assessmentRestService.getOneByProcessRole(assessorProcessRole.getId()).getSuccessObjectOrThrowException();
        if (assessment == null) {
            LOG.warn("No assessment could be found for the User " + userId + " and the Application " + applicationId);
            return showInvalidAssessmentView(model, competitionId, null);
        }

        boolean invalidAssessment = assessment.getProcessStatus().equals(AssessmentStates.REJECTED.getState());
        if (invalidAssessment) {
            return showInvalidAssessmentView(model, competitionId, assessment);
        }

        ApplicationResource application = applicationService.getById(applicationId);

        boolean pendingApplication = assessment.getProcessStatus().equals(AssessmentStates.PENDING.getState());
        if (pendingApplication) {
            return showApplicationReviewView(model, competitionId, userId, application, userApplicationRoles);
        }

        return showReadOnlyApplicationFormView(model, sectionId, userId, assessorProcessRole, application, userApplicationRoles);
    }


    private String showReadOnlyApplicationFormView(
            Model model,
            Optional<Long> sectionId,
            Long userId,
            ProcessRole assessorProcessRole,
            ApplicationResource application,
            List<ProcessRole> userApplicationRoles) {
        CompetitionResource competition = competitionService.getById(application.getCompetition());
        Optional<SectionResource> currentSection = getSection(simpleMap(competition.getSections(),section -> sectionService.getById(section)), sectionId, true);
        addApplicationDetails(application, competition, userId, currentSection, Optional.empty(), model, null, userApplicationRoles);
        addSectionDetails(model, currentSection);
        List<Response> questionResponses = responseService.getByApplication(application.getId());
        Map<Long, Response> questionResponsesMap = responseService.mapResponsesToQuestion(questionResponses);
        model.addAttribute("processRole", assessorProcessRole);
        model.addAttribute("questionResponses", questionResponsesMap);
        financeOverviewModelManager.addFinanceDetails(model, competition.getId(), application.getId());
        return assessmentDetails;
    }

    private String showInvalidAssessmentView(Model model, Long competitionId, Assessment assessment) {
        CompetitionResource competition = competitionService.getById(competitionId);
        model.addAttribute("competition", competition);
        model.addAttribute("assessment", assessment);
        return assessorDashboard;
    }

    private String showApplicationReviewView(Model model, Long competitionId, Long userId, ApplicationResource application,
                                             List<ProcessRole> userApplicationRoles) {
        CompetitionResource competition = competitionService.getById(application.getCompetition());
        addApplicationDetails(application, competition, userId, empty(), Optional.empty(), model, null, userApplicationRoles);
        getAndPassAssessmentDetails(competitionId, application.getId(), userId, model);
        Set<String> partners = call(application.getProcessRoles().stream().
                map(processRoleService::getById)).
                map(ProcessRole::getOrganisation).
                map(Organisation::getName).
                collect(toSet());
        model.addAttribute("partners", partners);
        return applicationReview;
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
    public ModelAndView getAssessmentSubmitReview(@PathVariable("competitionId") final Long competitionId,
                                                  @PathVariable("applicationId") final Long applicationId,
                                                  User user) {
        ProcessRole assessorProcessRole = processRoleService.findProcessRole(user.getId(), applicationId);

        if (assessorProcessRole == null || !assessorProcessRole.getRole().getName().equals(UserRoleType.ASSESSOR.getName())) {
            throw new IllegalStateException("User is not an Assessor on this application");
        }

        Assessment assessment = assessmentRestService.getOneByProcessRole(assessorProcessRole.getId()).getSuccessObjectOrThrowException();
        ApplicationResource application = applicationService.getById(applicationId);
        CompetitionResource competition = competitionService.getById(competitionId);
        List<Response> responses = getResponses(application);

        Score score = assessmentRestService.getScore(assessment.getId()).getSuccessObjectOrThrowException();

        List<Question> questions = competition.getSections()
                .stream()
                .map(sectionService::getById)
                .flatMap(section -> section.getQuestions()
                        .stream())
                .map(questionService::getById)
                .collect(toList());

        List<SectionResource> sections = competition.getSections()
                .stream()
                .map(sectionService::getById)
                .collect(toList());

        AssessmentSubmitReviewModel viewModel = new AssessmentSubmitReviewModel(assessment, responses, application, competition, score, questions, sections);

        return new ModelAndView(assessmentSubmitReview, "model", viewModel);
    }


    @RequestMapping(value = "/invitation_answer", method = RequestMethod.POST)
    public String invitationAnswer(HttpServletRequest request) {
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
        ProcessRole assessorProcessRole = processRoleService.findProcessRole(userId, applicationId);
        assessmentRestService.acceptAssessmentInvitation(assessorProcessRole.getId(), assessment);
    }

    private void rejectInvitation(Long applicationId, Long userId, String decisionReason, String observations) {
        ProcessRole assessorProcessRole = processRoleService.findProcessRole(userId, applicationId);
        ProcessOutcome processOutcome = new ProcessOutcome();
        processOutcome.setOutcome(decisionReason);
        processOutcome.setDescription(observations);
        assessmentRestService.rejectAssessmentInvitation(assessorProcessRole.getId(), processOutcome);
    }

    @RequestMapping(value = "/submit-assessments", method = RequestMethod.POST)
    public String assessmentsSubmissions(HttpServletRequest req) {

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
    public String confirmSubmit() {
        return "assessment-confirm-submit";
    }

    @RequestMapping(value = "/competitions/{competitionId}/applications/{applicationId}/complete", method = RequestMethod.POST)
    public String assessmentSummaryComplete(@PathVariable("competitionId") final Long competitionId,
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

    private Set<Long> convertStringListToLongSet(List<String> aList) {
        return aList.stream().map(Long::valueOf).collect(Collectors.toSet());
    }

    public boolean assessmentSummaryIsValidToSave(String recommendationValue, String feedback) {
        return ! ("no".equals(recommendationValue) && feedback.isEmpty());
    }

    private Pair<CompetitionResource, Assessment> getAndPassAssessmentDetails(Long competitionId, Long applicationId, Long userId, Model model) {
        //gets
        CompetitionResource competition = competitionService.getById(competitionId);
        ProcessRole assessmentProcessRole = processRoleService.findProcessRole(userId, applicationId);
        Assessment assessment = assessmentRestService.getOneByProcessRole(assessmentProcessRole.getId()).getSuccessObjectOrThrowException();

        //pass to view
        model.addAttribute("competition", competition);
        model.addAttribute("assessment", assessment);

        return Pair.of(competition, assessment);
    }
}
