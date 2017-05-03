package org.innovateuk.ifs.application;

import org.innovateuk.ifs.application.form.ApplicationForm;
import org.innovateuk.ifs.application.populator.ApplicationModelPopulator;
import org.innovateuk.ifs.application.populator.ApplicationOverviewModelPopulator;
import org.innovateuk.ifs.application.populator.AssessorQuestionFeedbackPopulator;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.ApplicationService;
import org.innovateuk.ifs.application.service.CompetitionService;
import org.innovateuk.ifs.application.service.QuestionService;
import org.innovateuk.ifs.application.service.SectionService;
import org.innovateuk.ifs.filter.CookieFlashMessageFilter;
import org.innovateuk.ifs.populator.OrganisationDetailsModelPopulator;
import org.innovateuk.ifs.profiling.ProfileExecution;
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

import static org.innovateuk.ifs.application.resource.ApplicationState.SUBMITTED;

/**
 * This controller will handle all requests that are related to the application overview.
 * Application overview is the page that contains the most basic information about the current application and
 * the basic information about the competition the application is related to.
 */

@Controller
@RequestMapping("/application")
@PreAuthorize("hasAuthority('applicant')")
public class ApplicationController {
    public static final String ASSIGN_QUESTION_PARAM = "assign_question";
    public static final String MARK_AS_COMPLETE = "mark_as_complete";

    @Autowired
    private ApplicationOverviewModelPopulator applicationOverviewModelPopulator;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private ProcessRoleService processRoleService;

    @Autowired
    private SectionService sectionService;

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private ApplicationRestService applicationRestService;

    @Autowired
    private CompetitionService competitionService;

    @Autowired
    private ApplicationModelPopulator applicationModelPopulator;

    @Autowired
    private CookieFlashMessageFilter cookieFlashMessageFilter;

    @Autowired
    private OrganisationDetailsModelPopulator organisationDetailsModelPopulator;

    @Autowired
    private AssessorQuestionFeedbackPopulator assessorQuestionFeedbackPopulator;

    @Autowired
    private UserRestService userRestService;

    public static String redirectToApplication(ApplicationResource application) {
        return "redirect:/application/" + application.getId();
    }

    @ProfileExecution
    @GetMapping("/{applicationId}")
    public String applicationDetails(ApplicationForm form, Model model, @PathVariable("applicationId") long applicationId,
                                     @ModelAttribute("loggedInUser") UserResource user) {
        ApplicationResource application = applicationService.getById(applicationId);

        if(form == null){
            form = new ApplicationForm();
        }

        form.setApplication(application);

        Long userId = user.getId();
        model.addAttribute("form", form);
        model.addAttribute("model", applicationOverviewModelPopulator.populateModel(application, userId));
        return "application-details";
    }

    @ProfileExecution
    @PostMapping(value = "/{applicationId}")
    public String applicationDetails(@PathVariable("applicationId") long applicationId,
                                     @ModelAttribute("loggedInUser") UserResource user,
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

    @ProfileExecution
    @PostMapping("/{applicationId}/summary")
    public String applicationSummarySubmit(@PathVariable("applicationId") long applicationId,
                                           @ModelAttribute("loggedInUser") UserResource user,
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
                    questionService.markAsInComplete(markQuestionCompleteId, applicationId, processRole.getId());
                }
            }
        }

        return "redirect:/application/" + applicationId + "/summary";
    }

    @ProfileExecution
    @GetMapping("/{applicationId}/confirm-submit")
    public String applicationConfirmSubmit(ApplicationForm form, Model model, @PathVariable("applicationId") long applicationId,
                                           @ModelAttribute("loggedInUser") UserResource user) {
        ApplicationResource application = applicationService.getById(applicationId);
        CompetitionResource competition = competitionService.getById(application.getCompetition());
        addApplicationAndSectionsInternalWithOrgDetails(application, competition, user.getId(), model, form);
        return "application-confirm-submit";
    }

    @PostMapping("/{applicationId}/submit")
    public String applicationSubmit(ApplicationForm form, Model model, @PathVariable("applicationId") long applicationId,
                                    @ModelAttribute("loggedInUser") UserResource user,
                                    HttpServletResponse response) {
        ApplicationResource application = applicationService.getById(applicationId);

        if (!ableToSubmitApplication(user, application)) {
            cookieFlashMessageFilter.setFlashMessage(response, "cannotSubmit");
            return "redirect:/application/" + applicationId + "/confirm-submit";
        }

        applicationRestService.updateApplicationState(applicationId, SUBMITTED).getSuccessObjectOrThrowException();
        application = applicationService.getById(applicationId);
        CompetitionResource competition = competitionService.getById(application.getCompetition());
        addApplicationAndSectionsInternalWithOrgDetails(application, competition, user.getId(), model, form);
        return "application-submitted";
    }

    @ProfileExecution
    @GetMapping("/{applicationId}/track")
    public String applicationTrack(ApplicationForm form, Model model, @PathVariable("applicationId") long applicationId,
                                   @ModelAttribute("loggedInUser") UserResource user) {
        ApplicationResource application = applicationService.getById(applicationId);
        CompetitionResource competition = competitionService.getById(application.getCompetition());
        addApplicationAndSectionsInternalWithOrgDetails(application, competition, user.getId(), model, form);
        return "application-track";
    }

    @ProfileExecution
    @GetMapping("/create/{competitionId}")
    public String applicationCreatePage() {
        return "application-create";
    }

    @ProfileExecution
    @PostMapping("/create/{competitionId}")
    public String applicationCreate(Model model,
                                    @PathVariable("competitionId") long competitionId,
                                    @RequestParam(value = "application_name", required = true) String applicationName,
                                    @ModelAttribute("loggedInUser") UserResource user) {
        Long userId = user.getId();

        String applicationNameWithoutWhiteSpace = applicationName.replaceAll("\\s", "");

        if (applicationNameWithoutWhiteSpace.length() > 0) {
            ApplicationResource application = applicationService.createApplication(competitionId, userId, applicationName);
            return "redirect:/application/" + application.getId();
        } else {
            model.addAttribute("applicationNameEmpty", true);
            return "application-create";
        }
    }

    @ProfileExecution
    @GetMapping(value = "/create-confirm-competition")
    public String competitionCreateApplication() {
        return "application-create-confirm-competition";
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
    @ProfileExecution
    @PostMapping("/{applicationId}/section/{sectionId}")
    public String assignQuestion(@PathVariable("applicationId") long applicationId,
                                 @PathVariable("sectionId") long sectionId,
                                 @ModelAttribute("loggedInUser") UserResource user,
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
