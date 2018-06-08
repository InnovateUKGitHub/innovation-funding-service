package org.innovateuk.ifs.application.summary.controller;

import org.innovateuk.ifs.application.form.ApplicationForm;
import org.innovateuk.ifs.application.forms.form.InterviewResponseForm;
import org.innovateuk.ifs.application.forms.populator.InterviewFeedbackViewModelPopulator;
import org.innovateuk.ifs.application.populator.ApplicationModelPopulator;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.FormInputResponseResource;
import org.innovateuk.ifs.application.service.*;
import org.innovateuk.ifs.application.summary.populator.ApplicationInterviewFeedbackViewModelPopulator;
import org.innovateuk.ifs.application.summary.populator.ApplicationInterviewSummaryViewModelPopulator;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.form.service.FormInputResponseRestService;
import org.innovateuk.ifs.form.service.FormInputResponseService;
import org.innovateuk.ifs.interview.service.InterviewAssignmentRestService;
import org.innovateuk.ifs.interview.service.InterviewResponseRestService;
import org.innovateuk.ifs.project.ProjectService;
import org.innovateuk.ifs.project.resource.ProjectResource;
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
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import static org.innovateuk.ifs.commons.rest.RestFailure.error;
import static org.innovateuk.ifs.controller.FileUploadControllerUtils.getMultipartFileBytes;
import static org.innovateuk.ifs.file.controller.FileDownloadControllerUtils.getFileResponseEntity;
import static org.innovateuk.ifs.util.CollectionFunctions.removeDuplicates;

/**
 * This controller will handle all requests that are related to the application summary.
 */
@Controller
@RequestMapping("/application")
public class ApplicationSummaryController {

    private ProcessRoleService processRoleService;
    private SectionService sectionService;
    private ApplicationService applicationService;
    private CompetitionService competitionService;
    private ApplicationModelPopulator applicationModelPopulator;
    private FormInputResponseService formInputResponseService;
    private FormInputResponseRestService formInputResponseRestService;
    private UserRestService userRestService;
    private ProjectService projectService;
    private InterviewAssignmentRestService interviewAssignmentRestService;
    private InterviewFeedbackViewModelPopulator interviewFeedbackViewModelPopulator;
    private InterviewResponseRestService interviewResponseRestService;
    private ApplicationInterviewFeedbackViewModelPopulator applicationInterviewFeedbackViewModelPopulator;

    public ApplicationSummaryController() {
    }

    @Autowired
    public ApplicationSummaryController(ProcessRoleService processRoleService,
                                        SectionService sectionService,
                                        ApplicationService applicationService,
                                        CompetitionService competitionService,
                                        ApplicationModelPopulator applicationModelPopulator,
                                        FormInputResponseService formInputResponseService,
                                        FormInputResponseRestService formInputResponseRestService,
                                        UserRestService userRestService,
                                        ProjectService projectService,
                                        InterviewAssignmentRestService interviewAssignmentRestService,
                                        InterviewFeedbackViewModelPopulator interviewFeedbackViewModelPopulator,
                                        InterviewResponseRestService interviewResponseRestService,
                                        ApplicationInterviewFeedbackViewModelPopulator applicationInterviewFeedbackViewModelPopulator) {
        this.processRoleService = processRoleService;
        this.sectionService = sectionService;
        this.applicationService = applicationService;
        this.competitionService = competitionService;
        this.applicationModelPopulator = applicationModelPopulator;
        this.formInputResponseService = formInputResponseService;
        this.formInputResponseRestService = formInputResponseRestService;
        this.userRestService = userRestService;
        this.projectService = projectService;
        this.interviewAssignmentRestService = interviewAssignmentRestService;
        this.interviewFeedbackViewModelPopulator = interviewFeedbackViewModelPopulator;
        this.interviewResponseRestService = interviewResponseRestService;
        this.applicationInterviewFeedbackViewModelPopulator = applicationInterviewFeedbackViewModelPopulator;
    }

    @SecuredBySpring(value = "READ", description = "Applicants, support staff, and innovation leads have permission to view the application summary page")
    @PreAuthorize("hasAnyAuthority('applicant', 'support', 'innovation_lead')")
    @GetMapping("/{applicationId}/summary")
    public String applicationSummary(@ModelAttribute("form") ApplicationForm form,
                                     @ModelAttribute("interviewResponseForm") InterviewResponseForm interviewResponseForm,
                                     BindingResult bindingResult,
                                     ValidationHandler validationHandler,
                                     Model model,
                                     @PathVariable("applicationId") long applicationId,
                                     UserResource user) {

//        List<FormInputResponseResource> responses = formInputResponseRestService.getResponsesByApplicationId(applicationId).getSuccess();
//        model.addAttribute("incompletedSections", sectionService.getInCompleted(applicationId));
//        model.addAttribute("responses", formInputResponseService.mapFormInputResponsesToFormInput(responses));
//
        ApplicationResource application = applicationService.getById(applicationId);
        CompetitionResource competition = competitionService.getById(application.getCompetition());
//        List<ProcessRoleResource> userApplicationRoles = processRoleService.findProcessRolesByApplicationId(application.getId());
//
//        applicationModelPopulator.addApplicationAndSectionsInternalWithOrgDetails(application, competition, user, model, form, userApplicationRoles, Optional.of(Boolean.FALSE));
//        ProcessRoleResource userApplicationRole = userRestService.findProcessRole(user.getId(), applicationId).getSuccess();
//
//        applicationModelPopulator.addOrganisationAndUserFinanceDetails(competition.getId(), applicationId, user, model, form, userApplicationRole.getOrganisationId());
//
//        model.addAttribute("applicationReadyForSubmit", applicationService.isApplicationReadyForSubmit(application.getId()));
//
//        ProjectResource project = projectService.getByApplicationId(applicationId);
//        boolean projectWithdrawn = (project != null && project.isWithdrawn());
//        model.addAttribute("projectWithdrawn", projectWithdrawn);
//
        boolean isApplicationAssignedToInterview = interviewAssignmentRestService.isAssignedToInterview(applicationId).getSuccess();
//
        if (competition.getCompetitionStatus().isFeedbackReleased() && !isApplicationAssignedToInterview) {
//            applicationModelPopulator.addFeedbackAndScores(model, applicationId);
            return "application-feedback-summary";
        } else if (isApplicationAssignedToInterview) {
//            applicationModelPopulator.addFeedbackAndScores(model, applicationId);
//            model.addAttribute("interviewFeedbackViewModel", interviewFeedbackViewModelPopulator.populate(applicationId, userApplicationRole, competition.getCompetitionStatus().isFeedbackReleased(), false));
        model.addAttribute("interviewFeedbackViewModel", applicationInterviewFeedbackViewModelPopulator.populate(applicationId, user));

        return "application-interview-feedback";
        }
        else {
            return "application-summary";
        }
    }

    @SecuredBySpring(value = "READ", description = "Applicants have permission to upload interview feedback.")
    @PreAuthorize("hasAuthority('applicant')")
    @PostMapping(value = "/{applicationId}/summary", params = "uploadResponse")
    public String uploadResponse(@ModelAttribute("interviewResponseForm") InterviewResponseForm form,
                                     BindingResult bindingResult,
                                     ValidationHandler validationHandler,
                                     Model model,
                                     @PathVariable("applicationId") long applicationId,
                                     UserResource user) {

        Supplier<String> failureAndSuccessView = () -> applicationSummary(new ApplicationForm(), form, bindingResult, validationHandler, model, applicationId, user);
        MultipartFile file = form.getResponse();
        RestResult<Void> sendResult = interviewResponseRestService
                .uploadResponse(applicationId, file.getContentType(), file.getSize(), file.getOriginalFilename(), getMultipartFileBytes(file));

        return validationHandler.addAnyErrors(error(removeDuplicates(sendResult.getErrors())))
                .failNowOrSucceedWith(failureAndSuccessView, failureAndSuccessView);
    }

    @SecuredBySpring(value = "READ", description = "Applicants have permission to remove interview feedback.")
    @PreAuthorize("hasAuthority('applicant')")
    @PostMapping(value = "/{applicationId}/summary", params = "removeResponse")
    public String removeResponse(@ModelAttribute("form") ApplicationForm form,
                                 @ModelAttribute("interviewResponseForm") InterviewResponseForm interviewResponseForm,
                                 BindingResult bindingResult,
                                 ValidationHandler validationHandler,
                                 Model model,
                                 @PathVariable("applicationId") long applicationId,
                                 UserResource user) {

        Supplier<String> failureAndSuccessView = () -> applicationSummary(form, interviewResponseForm, bindingResult, validationHandler, model, applicationId, user);
        RestResult<Void> sendResult = interviewResponseRestService
                .deleteResponse(applicationId);

        return validationHandler.addAnyErrors(error(removeDuplicates(sendResult.getErrors())))
                .failNowOrSucceedWith(failureAndSuccessView, failureAndSuccessView);
    }

    @GetMapping("/{applicationId}/summary/download-response")
    @SecuredBySpring(value = "READ", description = "Applicants have permission to view uploaded interview feedback.")
    @PreAuthorize("hasAnyAuthority('applicant', 'assessor', 'comp_admin', 'project_finance', 'innovation_lead')")
    public @ResponseBody
    ResponseEntity<ByteArrayResource> downloadResponse(Model model,
                                                       @PathVariable("applicationId") long applicationId) {
        return getFileResponseEntity(interviewResponseRestService.downloadResponse(applicationId).getSuccess(),
                interviewResponseRestService.findResponse(applicationId).getSuccess());
    }

    @GetMapping("/{applicationId}/summary/download-feedback")
    @SecuredBySpring(value = "READ", description = "Applicants have permission to view uploaded interview feedback.")
    @PreAuthorize("hasAnyAuthority('applicant', 'assessor', 'comp_admin', 'project_finance', 'innovation_lead')")
    public @ResponseBody
    ResponseEntity<ByteArrayResource> downloadFeedback(Model model,
                                                       @PathVariable("applicationId") long applicationId) {
        return getFileResponseEntity(interviewAssignmentRestService.downloadFeedback(applicationId).getSuccess(),
                interviewAssignmentRestService.findFeedback(applicationId).getSuccess());
    }
}
