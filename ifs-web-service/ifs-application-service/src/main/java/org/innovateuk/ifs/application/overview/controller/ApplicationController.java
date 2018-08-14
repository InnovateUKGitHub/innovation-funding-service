package org.innovateuk.ifs.application.overview.controller;

import org.innovateuk.ifs.application.overview.populator.ApplicationOverviewModelPopulator;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.ApplicationState;
import org.innovateuk.ifs.application.service.ApplicationRestService;
import org.innovateuk.ifs.application.service.QuestionService;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.competition.resource.CompetitionStatus;
import org.innovateuk.ifs.filter.CookieFlashMessageFilter;
import org.innovateuk.ifs.form.ApplicationForm;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.UserRestService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static java.lang.String.format;
import static org.innovateuk.ifs.application.resource.ApplicationState.OPEN;
import static org.innovateuk.ifs.user.resource.Role.LEADAPPLICANT;

/**
 * This controller will handle all requests that are related to the application overview.
 * Application overview is the page that contains the most basic information about the current application and
 * the basic information about the competition the application is related to.
 */
@Controller
@RequestMapping("/application")
@PreAuthorize("hasAuthority('applicant')")
@SecuredBySpring(value="Controller",
        description = "Only applicants on an application are allowed to view the corresponding application overview",
        securedType = ApplicationController.class)
public class ApplicationController {

    private ApplicationOverviewModelPopulator applicationOverviewModelPopulator;
    private QuestionService questionService;
    private UserRestService userRestService;
    private ApplicationRestService applicationRestService;
    private CookieFlashMessageFilter cookieFlashMessageFilter;

    public ApplicationController(ApplicationOverviewModelPopulator applicationOverviewModelPopulator,
                                 QuestionService questionService,
                                 UserRestService userRestService,
                                 ApplicationRestService applicationRestService,
                                 CookieFlashMessageFilter cookieFlashMessageFilter) {
        this.applicationOverviewModelPopulator = applicationOverviewModelPopulator;
        this.questionService = questionService;
        this.userRestService = userRestService;
        this.applicationRestService = applicationRestService;
        this.cookieFlashMessageFilter = cookieFlashMessageFilter;
    }

    @GetMapping("/{applicationId}")
    public String applicationOverview(ApplicationForm form,
                                     Model model,
                                     @PathVariable("applicationId") long applicationId,
                                     UserResource user) {
        ApplicationResource application = applicationRestService.getApplicationById(applicationId)
                .getSuccess();

        if (application.getCompetitionStatus() != CompetitionStatus.OPEN) {
            return format("redirect:/application/%s/summary", application.getId());
        }

        if (application.isSubmitted()) {
            return format("redirect:/application/%s/track", application.getId());
        }

        if (form == null) {
            form = new ApplicationForm();
        }

        form.setApplication(application);
        changeApplicationStatusToOpen(application, user);

        Long userId = user.getId();
        model.addAttribute("model", applicationOverviewModelPopulator.populateModel(application, userId));
        return "application-overview";
    }

    private void changeApplicationStatusToOpen(ApplicationResource applicationResource, UserResource userResource) {
        if (ApplicationState.CREATED.equals(applicationResource.getApplicationState())
                && userIsLeadApplicant(userResource.getId(), applicationResource.getId())) {
            applicationRestService.updateApplicationState(applicationResource.getId(), OPEN).getSuccess();
        }
    }

    private boolean userIsLeadApplicant(long userId, long applicationId) {
        return userRestService.findProcessRole(userId, applicationId).getSuccess()
                .getRole() == LEADAPPLICANT;
    }

    @PostMapping(value = "/{applicationId}")
    public String applicationOverview(@PathVariable("applicationId") long applicationId,
                                     UserResource user,
                                     HttpServletRequest request) {

        ProcessRoleResource assignedBy = userRestService.findProcessRole(user.getId(), applicationId).getSuccess();

        questionService.assignQuestion(applicationId, request, assignedBy);
        return "redirect:/application/" + applicationId;
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
        ProcessRoleResource assignedBy = userRestService.findProcessRole(user.getId(), applicationId).getSuccess();

        questionService.assignQuestion(applicationId, request, assignedBy);
        cookieFlashMessageFilter.setFlashMessage(response, "assignedQuestion");
    }
}
