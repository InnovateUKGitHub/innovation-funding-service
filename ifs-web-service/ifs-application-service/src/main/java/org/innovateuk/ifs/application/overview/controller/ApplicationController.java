package org.innovateuk.ifs.application.overview.controller;

import org.innovateuk.ifs.application.form.ApplicationForm;
import org.innovateuk.ifs.application.overview.populator.ApplicationOverviewModelPopulator;
import org.innovateuk.ifs.application.forms.populator.AssessorQuestionFeedbackPopulator;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.ApplicationService;
import org.innovateuk.ifs.application.service.QuestionService;
import org.innovateuk.ifs.filter.CookieFlashMessageFilter;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.ProcessRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static java.lang.String.format;

/**
 * This controller will handle all requests that are related to the application overview.
 * Application overview is the page that contains the most basic information about the current application and
 * the basic information about the competition the application is related to.
 */

@Controller
@RequestMapping("/application")
@PreAuthorize("hasAuthority('applicant')")
public class ApplicationController {
    @Autowired
    private ApplicationOverviewModelPopulator applicationOverviewModelPopulator;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private ProcessRoleService processRoleService;

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private CookieFlashMessageFilter cookieFlashMessageFilter;

    @Autowired
    private AssessorQuestionFeedbackPopulator assessorQuestionFeedbackPopulator;

    @GetMapping("/{applicationId}")
    public String applicationDetails(ApplicationForm form,
                                     Model model,
                                     @PathVariable("applicationId") long applicationId,
                                     UserResource user) {
        ApplicationResource application = applicationService.getById(applicationId);

        if (application.isSubmitted()) {
            return format("redirect:/application/%s/summary", application.getId());
        }

        if (form == null) {
            form = new ApplicationForm();
        }

        form.setApplication(application);

        Long userId = user.getId();
        model.addAttribute("form", form);
        model.addAttribute("model", applicationOverviewModelPopulator.populateModel(application, userId));
        return "application-details";
    }

    @PostMapping(value = "/{applicationId}")
    public String applicationDetails(@PathVariable("applicationId") long applicationId,
                                     UserResource user,
                                     HttpServletRequest request) {

        ProcessRoleResource assignedBy = processRoleService.findProcessRole(user.getId(), applicationId);

        questionService.assignQuestion(applicationId, request, assignedBy);
        return "redirect:/application/" + applicationId;
    }

    @GetMapping(value = "/{applicationId}/question/{questionId}/feedback")
    public String applicationAssessorQuestionFeedback(Model model, @PathVariable("applicationId") long applicationId,
                                                      @PathVariable("questionId") long questionId) {
        ApplicationResource applicationResource = applicationService.getById(applicationId);
        if (!applicationResource.getCompetitionStatus().isFeedbackReleased()) {
            return "redirect:/application/" + applicationId + "/summary";
        }
        model.addAttribute("model", assessorQuestionFeedbackPopulator.populate(applicationResource, questionId));
        return "application-assessor-feedback";

    }

    @GetMapping("/terms-and-conditions")
    public String termsAndConditions() {
        return "application-terms-and-conditions";
    }

    /**
     * Assign a question to a user
     *
     * @param applicationId the application for which the user is assigned
     * @param sectionId     section id for showing details
     * @param request       request parameters
     * @return
     */
    @PostMapping("/{applicationId}/section/{sectionId}")
    public String assignQuestion(@PathVariable("applicationId") long applicationId,
                                 @PathVariable("sectionId") long sectionId,
                                 UserResource user,
                                 HttpServletRequest request,
                                 HttpServletResponse response) {
        doAssignQuestion(applicationId, user, request, response);

        return "redirect:/application/" + applicationId + "/section/" + sectionId;
    }

    private void doAssignQuestion(Long applicationId, UserResource user, HttpServletRequest request, HttpServletResponse response) {
        ProcessRoleResource assignedBy = processRoleService.findProcessRole(user.getId(), applicationId);

        questionService.assignQuestion(applicationId, request, assignedBy);
        cookieFlashMessageFilter.setFlashMessage(response, "assignedQuestion");
    }
}
