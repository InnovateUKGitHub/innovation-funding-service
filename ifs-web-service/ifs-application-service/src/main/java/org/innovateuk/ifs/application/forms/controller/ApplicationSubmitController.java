package org.innovateuk.ifs.application.forms.controller;

import org.innovateuk.ifs.application.form.ApplicationForm;
import org.innovateuk.ifs.application.populator.ApplicationModelPopulator;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.form.resource.SectionResource;
import org.innovateuk.ifs.application.service.*;
import org.innovateuk.ifs.assessment.service.AssessmentRestService;
import org.innovateuk.ifs.assessment.service.AssessorFormInputResponseRestService;
import org.innovateuk.ifs.commons.rest.ValidationMessages;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.filter.CookieFlashMessageFilter;
import org.innovateuk.ifs.application.resource.FormInputResponseResource;
import org.innovateuk.ifs.form.service.FormInputResponseRestService;
import org.innovateuk.ifs.form.service.FormInputResponseService;
import org.innovateuk.ifs.populator.OrganisationDetailsModelPopulator;
import org.innovateuk.ifs.project.ProjectService;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.ProcessRoleService;
import org.innovateuk.ifs.user.service.UserRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.innovateuk.ifs.application.forms.ApplicationFormUtil.ASSIGN_QUESTION_PARAM;
import static org.innovateuk.ifs.application.forms.ApplicationFormUtil.MARK_AS_COMPLETE;
import static org.innovateuk.ifs.application.resource.ApplicationState.SUBMITTED;
import static org.innovateuk.ifs.commons.rest.ValidationMessages.collectValidationMessages;

/**
 * This controller will handle all submit requests that are related to the application overview.
 */

@Controller
@RequestMapping("/application")
public class ApplicationSubmitController {

    private QuestionService questionService;
    private ProcessRoleService processRoleService;
    private SectionService sectionService;
    private ApplicationService applicationService;
    private ApplicationRestService applicationRestService;
    private CompetitionService competitionService;
    private ApplicationModelPopulator applicationModelPopulator;
    private CookieFlashMessageFilter cookieFlashMessageFilter;
    private FormInputResponseService formInputResponseService;
    private FormInputResponseRestService formInputResponseRestService;
    private OrganisationDetailsModelPopulator organisationDetailsModelPopulator;
    private UserRestService userRestService;
    private AssessorFormInputResponseRestService assessorFormInputResponseRestService;
    private AssessmentRestService assessmentRestService;
    private ProjectService projectService;

    @Autowired
    public ApplicationSubmitController(QuestionService questionService,
                                       ProcessRoleService processRoleService,
                                       SectionService sectionService,
                                       ApplicationService applicationService,
                                       ApplicationRestService applicationRestService,
                                       CompetitionService competitionService,
                                       ApplicationModelPopulator applicationModelPopulator,
                                       CookieFlashMessageFilter cookieFlashMessageFilter,
                                       FormInputResponseService formInputResponseService,
                                       FormInputResponseRestService formInputResponseRestService,
                                       OrganisationDetailsModelPopulator organisationDetailsModelPopulator,
                                       UserRestService userRestService,
                                       AssessorFormInputResponseRestService assessorFormInputResponseRestService,
                                       AssessmentRestService assessmentRestService,
                                       ProjectService projectService) {
        this.questionService = questionService;
        this.processRoleService = processRoleService;
        this.sectionService = sectionService;
        this.applicationService = applicationService;
        this.applicationRestService = applicationRestService;
        this.competitionService = competitionService;
        this.applicationModelPopulator = applicationModelPopulator;
        this.cookieFlashMessageFilter = cookieFlashMessageFilter;
        this.formInputResponseService = formInputResponseService;
        this.formInputResponseRestService = formInputResponseRestService;
        this.organisationDetailsModelPopulator = organisationDetailsModelPopulator;
        this.userRestService = userRestService;
        this.assessorFormInputResponseRestService = assessorFormInputResponseRestService;
        this.assessmentRestService = assessmentRestService;
        this.projectService = projectService;
    }


    private boolean ableToSubmitApplication(UserResource user, ApplicationResource application) {
        return applicationModelPopulator.userIsLeadApplicant(application, user.getId()) && application.isSubmittable();
    }

    @SecuredBySpring(value = "READ", description = "Applicants, support staff, and innovation leads have permission to view the application summary page")
    @PreAuthorize("hasAnyAuthority('applicant', 'support', 'innovation_lead')")
    @GetMapping("/{applicationId}/summary")
    public String applicationSummary(@ModelAttribute("form") ApplicationForm form, Model model, @PathVariable("applicationId") long applicationId,
                                     UserResource user) {
        List<FormInputResponseResource> responses = formInputResponseRestService.getResponsesByApplicationId(applicationId).getSuccess();
        model.addAttribute("incompletedSections", sectionService.getInCompleted(applicationId));
        model.addAttribute("responses", formInputResponseService.mapFormInputResponsesToFormInput(responses));

        ApplicationResource application = applicationService.getById(applicationId);
        CompetitionResource competition = competitionService.getById(application.getCompetition());
        List<ProcessRoleResource> userApplicationRoles = processRoleService.findProcessRolesByApplicationId(application.getId());

        addApplicationAndSectionsInternalWithOrgDetails(application, competition, user, model, form, userApplicationRoles, Optional.of(Boolean.FALSE));
        ProcessRoleResource userApplicationRole = userRestService.findProcessRole(user.getId(), applicationId).getSuccess();

        applicationModelPopulator.addOrganisationAndUserFinanceDetails(competition.getId(), applicationId, user, model, form, userApplicationRole.getOrganisationId());

        model.addAttribute("applicationReadyForSubmit", applicationService.isApplicationReadyForSubmit(application.getId()));

        ProjectResource project = projectService.getByApplicationId(applicationId);
        boolean projectWithdrawn = (project != null && project.isWithdrawn());
        model.addAttribute("projectWithdrawn", projectWithdrawn);

        if (competition.getCompetitionStatus().isFeedbackReleased()) {
            model.addAttribute("scores", assessorFormInputResponseRestService.getApplicationAssessmentAggregate(applicationId).getSuccess());
            model.addAttribute("feedback", assessmentRestService.getApplicationFeedback(applicationId)
                    .getSuccess()
                    .getFeedback()
            );

            return "application-feedback-summary";
        } else {
            return "application-summary";
        }
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
        CompetitionResource competition = competitionService.getById(application.getCompetition());
        List<ProcessRoleResource> userApplicationRoles = processRoleService.findProcessRolesByApplicationId(application.getId());
        addApplicationAndSectionsInternalWithOrgDetails(application, competition, user, model, form, userApplicationRoles, Optional.empty());
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
        CompetitionResource competition = competitionService.getById(application.getCompetition());
        applicationModelPopulator.addApplicationWithoutDetails(application, competition, model);

        return "application-submitted";
    }

    @SecuredBySpring(value = "TODO", description = "TODO")
    @PreAuthorize("hasAuthority('applicant')")
    @GetMapping("/{applicationId}/track")
    public String applicationTrack(Model model,
                                   @PathVariable("applicationId") long applicationId) {
        ApplicationResource application = applicationService.getById(applicationId);
        CompetitionResource competition = competitionService.getById(application.getCompetition());
        applicationModelPopulator.addApplicationWithoutDetails(application, competition, model);
        return "application-track";
    }

    @PreAuthorize("hasPermission(#applicationId, 'org.innovateuk.ifs.application.resource.ApplicationCompositeId', 'APPLICATION_FEEDBACK')")
    @GetMapping("/{applicationId}/interview-feedback")
    public String interviewFeedback(@ModelAttribute("form") ApplicationForm form,
                                    Model model,
                                    @PathVariable("applicationId") long applicationId,
                                    UserResource user) {

        ApplicationResource application = applicationService.getById(applicationId);
        CompetitionResource competition = competitionService.getById(application.getCompetition());
        List<ProcessRoleResource> userApplicationRoles = processRoleService.findProcessRolesByApplicationId(application.getId());

        addApplicationAndSectionsInternalWithOrgDetails(application, competition, user, model, form, userApplicationRoles, Optional.of(Boolean.FALSE));
        ProcessRoleResource userApplicationRole = userRestService.findProcessRole(user.getId(), applicationId).getSuccess();

        applicationModelPopulator.addOrganisationAndUserFinanceDetails(competition.getId(), applicationId, user, model, form, userApplicationRole.getOrganisationId());

        model.addAttribute("scores", assessorFormInputResponseRestService.getApplicationAssessmentAggregate(applicationId).getSuccess());
        model.addAttribute("feedback", assessmentRestService.getApplicationFeedback(applicationId)
                .getSuccess()
                .getFeedback()
        );

        return "application-interview-feedback";
    }

    private void addApplicationAndSectionsInternalWithOrgDetails(final ApplicationResource application,
                                                                 final CompetitionResource competition,
                                                                 final UserResource user, final Model model,
                                                                 final ApplicationForm form,
                                                                 List<ProcessRoleResource> userApplicationRoles,
                                                                 final Optional<Boolean> markAsCompleteEnabled) {
        addApplicationAndSectionsInternalWithOrgDetails(application, competition, user, Optional.empty(), Optional.empty(), model, form, userApplicationRoles, markAsCompleteEnabled);
    }

    private void addApplicationAndSectionsInternalWithOrgDetails(final ApplicationResource application,
                                                                 final CompetitionResource competition,
                                                                 final UserResource user,
                                                                 Optional<SectionResource> section,
                                                                 Optional<Long> currentQuestionId,
                                                                 final Model model,
                                                                 final ApplicationForm form,
                                                                 List<ProcessRoleResource> userApplicationRoles,
                                                                 final Optional<Boolean> markAsCompleteEnabled) {
        organisationDetailsModelPopulator.populateModel(model, application.getId(), userApplicationRoles);
        applicationModelPopulator.addApplicationAndSections(application, competition, user, section, currentQuestionId, model, form, userApplicationRoles, markAsCompleteEnabled);
    }
}
