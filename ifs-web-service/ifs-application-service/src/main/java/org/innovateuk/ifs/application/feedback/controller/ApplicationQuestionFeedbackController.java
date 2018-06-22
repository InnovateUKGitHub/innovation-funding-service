package org.innovateuk.ifs.application.feedback.controller;

import org.innovateuk.ifs.application.feedback.populator.AssessorQuestionFeedbackPopulator;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.ApplicationRestService;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.interview.service.InterviewAssignmentRestService;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.ProcessRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/application")
public class ApplicationQuestionFeedbackController {

    @Autowired
    private ApplicationRestService applicationRestService;

    @Autowired
    private ProcessRoleService processRoleService;

    @Autowired
    private InterviewAssignmentRestService interviewAssignmentRestService;

    @Autowired
    private AssessorQuestionFeedbackPopulator assessorQuestionFeedbackPopulator;

    @GetMapping(value = "/{applicationId}/question/{questionId}/feedback")
    @SecuredBySpring(value = "READ", description = "Applicants and Assessors and Comp exec users can view question feedback for an application")
    @PreAuthorize("hasAnyAuthority('applicant', 'assessor', 'comp_admin', 'project_finance', 'innovation_lead')")
    public String applicationAssessorQuestionFeedback(Model model, @PathVariable("applicationId") long applicationId,
                                                      @PathVariable("questionId") long questionId,
                                                      UserResource user) {
        ApplicationResource applicationResource = applicationRestService.getApplicationById(applicationId)
                .getSuccess();

        boolean isApplicationAssignedToInterview = interviewAssignmentRestService.isAssignedToInterview(applicationId).getSuccess();

        if (!applicationResource.getCompetitionStatus().isFeedbackReleased() && !isApplicationAssignedToInterview) {
            return "redirect:/application/" + applicationId + "/summary";
        }
        model.addAttribute("model", assessorQuestionFeedbackPopulator.populate(applicationResource, questionId));
        return "application-assessor-feedback";

    }

}
