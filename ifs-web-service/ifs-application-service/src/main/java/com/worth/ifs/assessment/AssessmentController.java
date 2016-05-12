package com.worth.ifs.assessment;

import com.worth.ifs.application.AbstractApplicationController;
import com.worth.ifs.application.resource.ApplicationResource;
import com.worth.ifs.application.resource.ResponseResource;
import com.worth.ifs.application.resource.SectionResource;
import com.worth.ifs.assessment.service.AssessmentRestService;
import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.user.resource.OrganisationResource;
import com.worth.ifs.user.resource.ProcessRoleResource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

import static com.worth.ifs.application.service.Futures.call;
import static com.worth.ifs.util.CollectionFunctions.simpleMap;
import static java.util.Optional.empty;
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
    private AssessmentRestService assessmentRestService;

    private String competitionAssessmentsURL(Long competitionID) {
        return "/assessor/competitions/" + competitionID + "/applications";
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


    private String showReadOnlyApplicationFormView(
            Model model,
            Optional<Long> sectionId,
            Long userId,
            ProcessRoleResource assessorProcessRole,
            ApplicationResource application,
            List<ProcessRoleResource> userApplicationRoles) {
        CompetitionResource competition = competitionService.getById(application.getCompetition());
        Optional<SectionResource> currentSection = getSection(simpleMap(competition.getSections(),section -> sectionService.getById(section)), sectionId, true);
        addOrganisationDetails(model, application, userApplicationRoles);
        addApplicationDetails(application, competition, userId, currentSection, Optional.empty(), model, null, userApplicationRoles);
        addSectionDetails(model, currentSection);
        List<ResponseResource> questionResponses = responseService.getByApplication(application.getId());
        Map<Long, ResponseResource> questionResponsesMap = responseService.mapResponsesToQuestion(questionResponses);
        model.addAttribute("processRole", assessorProcessRole);
        model.addAttribute("questionResponses", questionResponsesMap);
        financeOverviewModelManager.addFinanceDetails(model, competition.getId(), application.getId());
        return assessmentDetails;
    }

    private String showApplicationReviewView(Model model, Long competitionId, Long userId, ApplicationResource application,
                                             List<ProcessRoleResource> userApplicationRoles) {
        CompetitionResource competition = competitionService.getById(application.getCompetition());
        addOrganisationDetails(model, application, userApplicationRoles);
        addApplicationDetails(application, competition, userId, empty(), Optional.empty(), model, null, userApplicationRoles);
        //getAndPassAssessmentDetails(competitionId, application.getId(), userId, model);
        Set<String> partners = call(application.getProcessRoles().stream().
                map(processRoleService::getById)).
                map(uar -> organisationService.getOrganisationById(uar.getOrganisation())).
                map(OrganisationResource::getName).
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
        return rejectInvitation;
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

}
