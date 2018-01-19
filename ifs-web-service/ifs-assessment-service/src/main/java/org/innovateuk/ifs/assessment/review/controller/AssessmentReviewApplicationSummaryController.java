package org.innovateuk.ifs.assessment.review.controller;

import org.innovateuk.ifs.application.form.ApplicationForm;
import org.innovateuk.ifs.application.service.CompetitionService;
import org.innovateuk.ifs.assessment.resource.ApplicationAssessmentFeedbackResource;
import org.innovateuk.ifs.assessment.resource.AssessmentFundingDecisionOutcomeResource;
import org.innovateuk.ifs.assessment.resource.AssessmentResource;
import org.innovateuk.ifs.assessment.resource.AssessorFormInputResponseResource;
import org.innovateuk.ifs.assessment.review.populator.AssessmentReviewApplicationSummaryModelPopulator;
import org.innovateuk.ifs.assessment.service.AssessmentRestService;
import org.innovateuk.ifs.assessment.service.AssessorFormInputResponseRestService;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.form.resource.FormInputResource;
import org.innovateuk.ifs.form.service.FormInputRestService;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.ProcessRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.print.DocFlavor;
import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * Controller to manage display of applications in panel review
 */
@Controller
@RequestMapping(value = "/review/{reviewId}")
@SecuredBySpring(value = "Controller", description = "Assessors can access applications for review", securedType = AssessmentReviewController.class)
@PreAuthorize("hasAuthority('assessor')")
public class AssessmentReviewApplicationSummaryController {

    @Autowired
    private AssessmentReviewApplicationSummaryModelPopulator assessmentReviewApplicationSummaryModelPopulator;

    @Autowired
    private AssessorFormInputResponseRestService assessorFormInputResponseRestService;

    @Autowired
    private FormInputRestService formInputRestService;

    @Autowired
    private ProcessRoleService processRoleService;

    @Autowired
    private AssessmentRestService assessmentRestService;

    @GetMapping("/application/{applicationId}")
    public String viewApplication(@PathVariable("applicationId") long applicationId,
                                  @ModelAttribute("form") ApplicationForm form,
                                  Model model,
                                  UserResource user) {

        assessmentReviewApplicationSummaryModelPopulator.populateModel(model, form, user, applicationId);

//        retrieve application id
        List<ProcessRoleResource> processRoleResources = processRoleService
                .findProcessRolesByApplicationId(applicationId)
                .stream()
                .filter(processRoleResource -> processRoleResource.getUser().equals(user.getId()))
                .filter(processRoleResource -> processRoleResource.getRoleName().equals("assessor"))
                .collect(toList());

//        if assessor has previously assessed application then show feedback
        if (processRoleResources.size() != 0){
            List<AssessorFormInputResponseResource> questionScore = new ArrayList<>();
            List<AssessorFormInputResponseResource> questionFeedback = new ArrayList<>();

//            get assessor feedback
            List<AssessorFormInputResponseResource> inputResponse = assessorFormInputResponseRestService.getAllAssessorFormInputResponses(processRoleResources.get(0).getId()).getSuccessObjectOrThrowException();

//            split up the feedback into text feedback and score
            for (AssessorFormInputResponseResource assessorFormInputResponseResource : inputResponse) {

                FormInputResource feedbackType = formInputRestService.getById(assessorFormInputResponseResource.getFormInput()).getSuccessObjectOrThrowException();

                if(feedbackType.getDescription().equals("Question score")){
                    questionScore.add(assessorFormInputResponseResource);
                }else if(feedbackType.getDescription().equals("Feedback")){
                    questionFeedback.add(assessorFormInputResponseResource);
                }
            }

//            retrieve feedback summary
            List<String> feedback = new ArrayList<>();
            List<String> comment = new ArrayList<>();
            List<Boolean> outcome = new ArrayList<>();

            List<AssessmentResource> assessmentResource = assessmentRestService.getByUserAndApplication(user.getId(),applicationId).getSuccessObjectOrThrowException();
            for(AssessmentResource assessment : assessmentResource){
                feedback.add(assessment.getFundingDecision().getFeedback());
                comment.add(assessment.getFundingDecision().getComment());
                outcome.add(assessment.getFundingDecision().getFundingConfirmation());
            }


//            ApplicationAssessmentFeedbackResource applicationAssessmentFeedbackResourceFeedback = assessmentRestService.getApplicationFeedback(applicationId).getSuccessObjectOrThrowException();
//            ApplicationAssessmentFeedbackResource applicationAssessmentFeedbackResourceComment = assessmentRestService.getApplicationComment(applicationId).getSuccessObjectOrThrowException();
//            ApplicationAssessmentFeedbackResource applicationAssessmentFeedbackResourceOutcome = assessmentRestService.getApplicationOutcome(applicationId).getSuccessObjectOrThrowException();
//
//            model.addAttribute("feedbackSummary", applicationAssessmentFeedbackResourceFeedback);
//            model.addAttribute("feedbackComment", applicationAssessmentFeedbackResourceComment);
//            model.addAttribute("feedbackOutcome", applicationAssessmentFeedbackResourceOutcome);
            model.addAttribute("feedback", questionFeedback);
            model.addAttribute("score", questionScore);
        }
        
        return "assessor-panel-application-overview";
    }


}
