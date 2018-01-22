package org.innovateuk.ifs.assessment.review.controller;

import org.innovateuk.ifs.application.form.ApplicationForm;
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

        List<ProcessRoleResource> processRoleResources = processRoleService
                .findProcessRolesByApplicationId(applicationId)
                .stream()
                .filter(processRoleResource -> processRoleResource.getUser().equals(user.getId()))
                .filter(processRoleResource -> processRoleResource.getRoleName().equals("assessor"))
                .collect(toList());

        if (!processRoleResources.isEmpty()){
            List<AssessorFormInputResponseResource> questionScore = new ArrayList<>();
            List<AssessorFormInputResponseResource> questionFeedback = new ArrayList<>();

            List<AssessorFormInputResponseResource> inputResponse = assessorFormInputResponseRestService.getAllAssessorFormInputResponsesForPanel(processRoleResources.get(0).getId()).getSuccessObjectOrThrowException();

            for (AssessorFormInputResponseResource assessorFormInputResponseResource : inputResponse) {

                String feedbackType = formInputRestService.getById(assessorFormInputResponseResource.getFormInput()).getSuccessObjectOrThrowException().getDescription();

                if(feedbackType.equals("Question score")){
                    questionScore.add(assessorFormInputResponseResource);
                }else if(feedbackType.equals("Feedback")){
                    questionFeedback.add(assessorFormInputResponseResource);
                }
            }

            List<AssessmentResource> feedbackSummary = new ArrayList<>();

            if(!inputResponse.isEmpty()) {
               feedbackSummary = assessmentRestService.getByUserAndApplication(user.getId(), applicationId).getSuccessObjectOrThrowException();
            }

            model.addAttribute("feedback", questionFeedback);
            model.addAttribute("score", questionScore);
            model.addAttribute("feedbackSummary",feedbackSummary);
        }
        
        return "assessor-panel-application-overview";
    }
}
