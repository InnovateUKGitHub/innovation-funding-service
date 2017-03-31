package org.innovateuk.ifs.application;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.application.form.ApplicationForm;
import org.innovateuk.ifs.application.populator.*;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.SectionResource;
import org.innovateuk.ifs.application.service.*;
import org.innovateuk.ifs.assessment.service.AssessmentRestService;
import org.innovateuk.ifs.assessment.service.AssessorFormInputResponseRestService;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.rest.ValidationMessages;
import org.innovateuk.ifs.commons.security.UserAuthenticationService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.file.controller.viewmodel.OptionalFileDetailsViewModel;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.filter.CookieFlashMessageFilter;
import org.innovateuk.ifs.form.resource.FormInputResponseResource;
import org.innovateuk.ifs.form.service.FormInputResponseService;
import org.innovateuk.ifs.form.service.FormInputService;
import org.innovateuk.ifs.populator.OrganisationDetailsModelPopulator;
import org.innovateuk.ifs.profiling.ProfileExecution;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.ProcessRoleService;
import org.innovateuk.ifs.user.service.UserRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.innovateuk.ifs.application.resource.ApplicationStatus.SUBMITTED;
import static org.innovateuk.ifs.commons.rest.ValidationMessages.collectValidationMessages;
import static org.innovateuk.ifs.competition.resource.CompetitionStatus.PROJECT_SETUP;
import static org.innovateuk.ifs.file.controller.FileDownloadControllerUtils.getFileResponseEntity;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 * This controller will handle all requests that are related to the application overview.
 * Application overview is the page that contains the most basic information about the current application and
 * the basic information about the competition the application is related to.
 */

@Controller
@RequestMapping("/application")
@PreAuthorize("hasAuthority('applicant')")
public class ApplicationController {
    private static final Log LOG = LogFactory.getLog(ApplicationController.class);

    public static final String ASSIGN_QUESTION_PARAM = "assign_question";
    public static final String MARK_AS_COMPLETE = "mark_as_complete";

    @Autowired
    private ApplicationOverviewModelPopulator applicationOverviewModelPopulator;

    @Autowired
    private AssessorFeedbackRestService assessorFeedbackRestService;

    @Autowired
    private UserAuthenticationService userAuthenticationService;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private ProcessRoleService processRoleService;

    @Autowired
    private SectionService sectionService;

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private CompetitionService competitionService;

    @Autowired
    private ApplicationModelPopulator applicationModelPopulator;

    @Autowired
    private CookieFlashMessageFilter cookieFlashMessageFilter;

    @Autowired
    private ApplicationPrintPopulator applicationPrintPopulator;

    @Autowired
    private FormInputResponseService formInputResponseService;

    @Autowired
    private ApplicationSectionAndQuestionModelPopulator applicationSectionAndQuestionModelPopulator;

    @Autowired
    private OrganisationDetailsModelPopulator organisationDetailsModelPopulator;

    @Autowired
    private AssessorQuestionFeedbackPopulator assessorQuestionFeedbackPopulator;

    @Autowired
    private FormInputService formInputService;

    @Autowired
    private UserRestService userRestService;

    @Autowired
    private AssessorFormInputResponseRestService assessorFormInputResponseRestService;

    @Autowired
    private AssessmentRestService assessmentRestService;

    public static String redirectToApplication(ApplicationResource application){
        return "redirect:/application/"+application.getId();
    }

    @ProfileExecution
    @RequestMapping(value= "/{applicationId}", method = RequestMethod.GET)
    public String applicationDetails(ApplicationForm form, Model model, @PathVariable("applicationId") long applicationId,
                                     HttpServletRequest request) {

        Long userId = userAuthenticationService.getAuthenticatedUser(request).getId();
        applicationOverviewModelPopulator.populateModel(applicationId, userId, form, model);
        return "application-details";
    }

    @ProfileExecution
    @RequestMapping(value= "/{applicationId}", method = RequestMethod.POST)
    public String applicationDetails(@PathVariable("applicationId") long applicationId, HttpServletRequest request) {

        UserResource user = userAuthenticationService.getAuthenticatedUser(request);
        ProcessRoleResource assignedBy = processRoleService.findProcessRole(user.getId(), applicationId);

        questionService.assignQuestion(applicationId, request, assignedBy);
        return "redirect:/application/"+applicationId;
    }

    @ProfileExecution
    @RequestMapping("/{applicationId}/section/{sectionId}")
    public String applicationDetailsOpenSection(ApplicationForm form, Model model,
                                     @PathVariable("applicationId") long applicationId,
                                     @PathVariable("sectionId") long sectionId,
                                                HttpServletRequest request){
        UserResource user = userAuthenticationService.getAuthenticatedUser(request);
        ApplicationResource application = applicationService.getById(applicationId);
        SectionResource section = sectionService.getById(sectionId);
        CompetitionResource competition = competitionService.getById(application.getCompetition());
        ProcessRoleResource userApplicationRole = userRestService.findProcessRole(user.getId(), applicationId).getSuccessObjectOrThrowException();

        addApplicationAndSectionsInternalWithOrgDetails(application, competition, user.getId(), Optional.ofNullable(section), Optional.empty(), model, form);
        applicationModelPopulator.addOrganisationAndUserFinanceDetails(competition.getId(), applicationId, user, model, form, userApplicationRole.getOrganisationId());
        model.addAttribute("ableToSubmitApplication", ableToSubmitApplication(user, application));
        return "application-details";
    }

    private boolean ableToSubmitApplication(UserResource user, ApplicationResource application) {
        return applicationModelPopulator.userIsLeadApplicant(application, user.getId()) && application.isSubmitable();
    }

    @ProfileExecution
    @RequestMapping(value = "/{applicationId}/summary", method = RequestMethod.GET)
    public String applicationSummary(@ModelAttribute("form") ApplicationForm form, Model model, @PathVariable("applicationId") long applicationId,
                                     HttpServletRequest request) {
        List<FormInputResponseResource> responses = formInputResponseService.getByApplication(applicationId);
        model.addAttribute("incompletedSections", sectionService.getInCompleted(applicationId));
        model.addAttribute("responses", formInputResponseService.mapFormInputResponsesToFormInput(responses));

        UserResource user = userAuthenticationService.getAuthenticatedUser(request);
        ApplicationResource application = applicationService.getById(applicationId);
        CompetitionResource competition = competitionService.getById(application.getCompetition());
        addApplicationAndSectionsInternalWithOrgDetails(application, competition, user.getId(), model, form);
        ProcessRoleResource userApplicationRole = userRestService.findProcessRole(user.getId(), applicationId).getSuccessObjectOrThrowException();

        applicationModelPopulator.addOrganisationAndUserFinanceDetails(competition.getId(), applicationId, user, model, form, userApplicationRole.getOrganisationId());

        model.addAttribute("applicationReadyForSubmit", applicationService.isApplicationReadyForSubmit(application.getId()));

        if (PROJECT_SETUP.equals(competition.getCompetitionStatus())) {
            OptionalFileDetailsViewModel assessorFeedbackViewModel = getAssessorFeedbackViewModel(application);
            model.addAttribute("assessorFeedback", assessorFeedbackViewModel);
        }

        if (competition.getCompetitionStatus().isFeedbackReleased()) {
            model.addAttribute("scores", assessorFormInputResponseRestService.getApplicationAssessmentAggregate(applicationId).getSuccessObjectOrThrowException());
            model.addAttribute("feedback", assessmentRestService.getApplicationFeedback(applicationId)
                    .getSuccessObjectOrThrowException()
                    .getFeedback()
            );

            return "application-feedback-summary";
        }
        else {
            return "application-summary";
        }
    }

    @RequestMapping(value = "/{applicationId}/question/{questionId}/feedback")
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
    @RequestMapping(value = "/{applicationId}/summary", method = RequestMethod.POST)
    public String applicationSummarySubmit(@PathVariable("applicationId") long applicationId,
                                           HttpServletRequest request) {
        UserResource user = userAuthenticationService.getAuthenticatedUser(request);

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
    @RequestMapping("/{applicationId}/confirm-submit")
    public String applicationConfirmSubmit(ApplicationForm form, Model model, @PathVariable("applicationId") long applicationId,
                                           HttpServletRequest request){
        UserResource user = userAuthenticationService.getAuthenticatedUser(request);
        ApplicationResource application = applicationService.getById(applicationId);
        CompetitionResource competition = competitionService.getById(application.getCompetition());
        addApplicationAndSectionsInternalWithOrgDetails(application, competition, user.getId(), model, form);
        return "application-confirm-submit";
    }

    @RequestMapping(value = "/{applicationId}/submit", method = RequestMethod.POST)
    public String applicationSubmit(ApplicationForm form, Model model, @PathVariable("applicationId") long applicationId,
                                    HttpServletRequest request, HttpServletResponse response){
    	UserResource user = userAuthenticationService.getAuthenticatedUser(request);
    	ApplicationResource application = applicationService.getById(applicationId);
    	
    	if(!ableToSubmitApplication(user, application)){
    		cookieFlashMessageFilter.setFlashMessage(response, "cannotSubmit");
    		return "redirect:/application/" + applicationId + "/confirm-submit";
    	}
       
        applicationService.updateStatus(applicationId, SUBMITTED);
        application = applicationService.getById(applicationId);
        CompetitionResource competition = competitionService.getById(application.getCompetition());
        addApplicationAndSectionsInternalWithOrgDetails(application, competition, user.getId(), model, form);
        return "application-submitted";
    }

    @ProfileExecution
    @RequestMapping("/{applicationId}/track")
    public String applicationTrack(ApplicationForm form, Model model, @PathVariable("applicationId") long applicationId,
                                    HttpServletRequest request){
        UserResource user = userAuthenticationService.getAuthenticatedUser(request);
        ApplicationResource application = applicationService.getById(applicationId);
        CompetitionResource competition = competitionService.getById(application.getCompetition());
        addApplicationAndSectionsInternalWithOrgDetails(application, competition, user.getId(), model, form);
        return "application-track";
    }
    @ProfileExecution
    @RequestMapping("/create/{competitionId}")
    public String applicationCreatePage(){
        return "application-create";
    }

    @ProfileExecution
    @RequestMapping(value = "/create/{competitionId}", method = RequestMethod.POST)
    public String applicationCreate(Model model,
                                    @PathVariable("competitionId") long competitionId,
                                    @RequestParam(value = "application_name", required = true) String applicationName,
                                    HttpServletRequest request){
        Long userId = userAuthenticationService.getAuthenticatedUser(request).getId();

        String applicationNameWithoutWhiteSpace= applicationName.replaceAll("\\s","");

        if(applicationNameWithoutWhiteSpace.length() > 0) {
            ApplicationResource application = applicationService.createApplication(competitionId, userId, applicationName);
            return "redirect:/application/"+application.getId();
        }
        else {
            model.addAttribute("applicationNameEmpty", true);
            return "application-create";
        }
    }
    @ProfileExecution
    @RequestMapping(value = "/create-confirm-competition")
    public String competitionCreateApplication(){
        return "application-create-confirm-competition";
    }

    @RequestMapping(value = "/terms-and-conditions")
    public String termsAndConditions(){
        return "application-terms-and-conditions";
    }

    @RequestMapping(value = "/{applicationId}/assessorFeedback", method = GET)
    public @ResponseBody ResponseEntity<ByteArrayResource> downloadAssessorFeedbackFile(
            @PathVariable("applicationId") long applicationId) {

        ByteArrayResource resource = assessorFeedbackRestService.getAssessorFeedbackFile(applicationId).getSuccessObjectOrThrowException();
        FileEntryResource fileDetails = assessorFeedbackRestService.getAssessorFeedbackFileDetails(applicationId).getSuccessObjectOrThrowException();
        return getFileResponseEntity(resource, fileDetails);
    }

    /**
     * Printable version of the application
     */
    @RequestMapping(value="/{applicationId}/print")
    public String printApplication(@PathVariable("applicationId") long applicationId,
                                             Model model, HttpServletRequest request) {
        return applicationPrintPopulator.print(applicationId, model, request);
    }

    /**
     * Assign a question to a user
     *
     * @param applicationId the application for which the user is assigned
     * @param sectionId section id for showing details
     * @param request request parameters
     * @return
     */
    @ProfileExecution
    @RequestMapping(value = "/{applicationId}/section/{sectionId}", method = RequestMethod.POST)
    public String assignQuestion(@PathVariable("applicationId") long applicationId,
                                 @PathVariable("sectionId") long sectionId,
                                 HttpServletRequest request,
                                 HttpServletResponse response){

        doAssignQuestion(applicationId, request, response);

        return "redirect:/application/" + applicationId + "/section/" +sectionId;
    }

    private OptionalFileDetailsViewModel getAssessorFeedbackViewModel(ApplicationResource application) {

        boolean readonly = true;

        Long assessorFeedbackFileEntry = application.getAssessorFeedbackFileEntry();

        if (assessorFeedbackFileEntry != null) {
            RestResult<FileEntryResource> fileEntry = assessorFeedbackRestService.getAssessorFeedbackFileDetails(application.getId());
            return OptionalFileDetailsViewModel.withExistingFile(fileEntry.getSuccessObjectOrThrowException(), readonly);
        } else {
            return OptionalFileDetailsViewModel.withNoFile(readonly);
        }
    }

    private void doAssignQuestion(Long applicationId, HttpServletRequest request, HttpServletResponse response) {
        UserResource user = userAuthenticationService.getAuthenticatedUser(request);
        ProcessRoleResource assignedBy = processRoleService.findProcessRole(user.getId(), applicationId);

        questionService.assignQuestion(applicationId, request, assignedBy);
        cookieFlashMessageFilter.setFlashMessage(response, "assignedQuestion");
    }

    private void addApplicationAndSectionsInternalWithOrgDetails(final ApplicationResource application, final CompetitionResource competition, final Long userId, final Model model, final ApplicationForm form) {
        addApplicationAndSectionsInternalWithOrgDetails(application, competition, userId, Optional.empty(), Optional.empty(), model, form);
    }

    private void addApplicationAndSectionsInternalWithOrgDetails(final ApplicationResource application, final CompetitionResource competition, final Long userId, Optional<SectionResource> section, Optional<Long> currentQuestionId, final Model model, final ApplicationForm form) {
        organisationDetailsModelPopulator.populateModel(model, application.getId());
        applicationModelPopulator.addApplicationAndSections(application, competition, userId, section, currentQuestionId, model, form);
    }
}
