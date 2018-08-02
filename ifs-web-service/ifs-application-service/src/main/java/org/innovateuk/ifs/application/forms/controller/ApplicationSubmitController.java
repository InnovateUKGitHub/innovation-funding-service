package org.innovateuk.ifs.application.forms.controller;

import org.innovateuk.ifs.application.form.ApplicationForm;
import org.innovateuk.ifs.application.populator.ApplicationModelPopulator;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.ApplicationRestService;
import org.innovateuk.ifs.application.service.ApplicationService;
import org.innovateuk.ifs.application.service.QuestionService;
import org.innovateuk.ifs.commons.error.ValidationMessages;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.filter.CookieFlashMessageFilter;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.ProcessRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.innovateuk.ifs.application.forms.ApplicationFormUtil.ASSIGN_QUESTION_PARAM;
import static org.innovateuk.ifs.application.forms.ApplicationFormUtil.MARK_AS_COMPLETE;
import static org.innovateuk.ifs.application.resource.ApplicationState.SUBMITTED;
import static org.innovateuk.ifs.commons.error.ValidationMessages.collectValidationMessages;

/**
 * This controller will handle all submit requests that are related to the application overview.
 */

@Controller
@RequestMapping("/application")
public class ApplicationSubmitController {

    private QuestionService questionService;
    private ProcessRoleService processRoleService;
    private ApplicationService applicationService;
    private ApplicationRestService applicationRestService;
    private CompetitionRestService competitionRestService;
    private ApplicationModelPopulator applicationModelPopulator;
    private CookieFlashMessageFilter cookieFlashMessageFilter;

    public ApplicationSubmitController() {
    }

    @Autowired
    public ApplicationSubmitController(QuestionService questionService,
                                       ProcessRoleService processRoleService,
                                       ApplicationService applicationService,
                                       ApplicationRestService applicationRestService,
                                       CompetitionRestService competitionRestService,
                                       ApplicationModelPopulator applicationModelPopulator,
                                       CookieFlashMessageFilter cookieFlashMessageFilter) {
        this.questionService = questionService;
        this.processRoleService = processRoleService;
        this.applicationService = applicationService;
        this.applicationRestService = applicationRestService;
        this.competitionRestService = competitionRestService;
        this.applicationModelPopulator = applicationModelPopulator;
        this.cookieFlashMessageFilter = cookieFlashMessageFilter;
    }

    private boolean ableToSubmitApplication(UserResource user, ApplicationResource application) {
        return applicationModelPopulator.userIsLeadApplicant(application, user.getId()) && application.isSubmittable();
    }

    @SecuredBySpring(value = "TODO", description = "TODO")
    @PreAuthorize("hasAuthority('applicant')")
    @PostMapping("/{applicationId}/summary")
    public String applicationSummarySubmit(@PathVariable("applicationId") long applicationId,
                                           UserResource user,
                                           HttpServletRequest request) {

        Map<String, String[]> params = request.getParameterMap();

        if (params.containsKey(ASSIGN_QUESTION_PARAM)) {
            ProcessRoleResource assignedBy = processRoleService.findProcessRole(user.getId(), applicationId);
            questionService.assignQuestion(applicationId, request, assignedBy);
        } else if (params.containsKey(MARK_AS_COMPLETE)) {
            Long markQuestionCompleteId = Long.valueOf(request.getParameter(MARK_AS_COMPLETE));
            if (markQuestionCompleteId != null) {
                ProcessRoleResource processRole = processRoleService.findProcessRole(user.getId(), applicationId);
                List<ValidationMessages> markAsCompleteErrors = questionService.markAsComplete(markQuestionCompleteId, applicationId, processRole.getId());

                if (collectValidationMessages(markAsCompleteErrors).hasErrors()) {
                    questionService.markAsIncomplete(markQuestionCompleteId, applicationId, processRole.getId());
                    return "redirect:/application/" + applicationId + "/form/question/edit/" + markQuestionCompleteId + "?mark_as_complete=true";
                }
            }
        }

        return "redirect:/application/" + applicationId + "/summary";
    }

    @SecuredBySpring(value = "TODO", description = "TODO")
    @PreAuthorize("hasAuthority('applicant')")
    @GetMapping("/{applicationId}/confirm-submit")
    public String applicationConfirmSubmit(ApplicationForm form, Model model, @PathVariable("applicationId") long applicationId,
                                           UserResource user) {
        ApplicationResource application = applicationService.getById(applicationId);
        CompetitionResource competition = competitionRestService.getCompetitionById(application.getCompetition()).getSuccess();
        List<ProcessRoleResource> userApplicationRoles = processRoleService.findProcessRolesByApplicationId(application.getId());
        applicationModelPopulator.addApplicationAndSectionsInternalWithOrgDetails(application, competition, user, model, form, userApplicationRoles, Optional.empty());
        return "application-confirm-submit";
    }

    @SecuredBySpring(value = "TODO", description = "TODO")
    @PreAuthorize("hasAuthority('applicant')")
    @PostMapping("/{applicationId}/submit")
    public String applicationSubmit(ApplicationForm form, Model model, @PathVariable("applicationId") long applicationId,
                                    UserResource user,
                                    HttpServletResponse response) {
        ApplicationResource application = applicationService.getById(applicationId);

        if (!ableToSubmitApplication(user, application)) {
            cookieFlashMessageFilter.setFlashMessage(response, "cannotSubmit");
            return "redirect:/application/" + applicationId + "/confirm-submit";
        }

        applicationRestService.updateApplicationState(applicationId, SUBMITTED).getSuccess();
        CompetitionResource competition = competitionRestService.getCompetitionById(application.getCompetition()).getSuccess();
        applicationModelPopulator.addApplicationWithoutDetails(application, competition, model);

        return "application-submitted";
    }

    @SecuredBySpring(value = "TODO", description = "TODO")
    @PreAuthorize("hasAuthority('applicant')")
    @GetMapping("/{applicationId}/track")
    public String applicationTrack(Model model,
                                   @PathVariable("applicationId") long applicationId) {
        ApplicationResource application = applicationService.getById(applicationId);

        if (!application.isSubmitted()) {
            return "redirect:/application/" + applicationId;
        }

        CompetitionResource competition = competitionRestService.getCompetitionById(application.getCompetition()).getSuccess();
        applicationModelPopulator.addApplicationWithoutDetails(application, competition, model);
        return "application-track";
    }
}


