package org.innovateuk.ifs.application.summary.controller;

import org.innovateuk.ifs.application.form.ApplicationForm;
import org.innovateuk.ifs.application.forms.form.InterviewResponseForm;
import org.innovateuk.ifs.application.forms.populator.InterviewFeedbackViewModelPopulator;
import org.innovateuk.ifs.application.populator.ApplicationModelPopulator;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.FormInputResponseResource;
import org.innovateuk.ifs.application.service.ApplicationService;
import org.innovateuk.ifs.application.service.CompetitionService;
import org.innovateuk.ifs.application.service.SectionService;
import org.innovateuk.ifs.application.summary.populator.ApplicationFeedbackSummaryViewModelPopulator;
import org.innovateuk.ifs.application.summary.populator.ApplicationInterviewFeedbackViewModelPopulator;
import org.innovateuk.ifs.application.summary.populator.ApplicationInterviewSummaryViewModelPopulator;
import org.innovateuk.ifs.application.summary.populator.ApplicationSummaryViewModelPopulator;
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
import static org.innovateuk.ifs.controller.ErrorToObjectErrorConverterFactory.defaultConverters;
import static org.innovateuk.ifs.controller.ErrorToObjectErrorConverterFactory.fileUploadField;
import static org.innovateuk.ifs.controller.FileUploadControllerUtils.getMultipartFileBytes;
import static org.innovateuk.ifs.file.controller.FileDownloadControllerUtils.getFileResponseEntity;
import static org.innovateuk.ifs.util.CollectionFunctions.removeDuplicates;

/**
 * This controller will handle all requests that are related to the application summary.
 */
@Controller
@RequestMapping("/application")
public class ApplicationSummaryController {

    private ApplicationService applicationService;
    private CompetitionService competitionService;
    private InterviewAssignmentRestService interviewAssignmentRestService;
    private InterviewResponseRestService interviewResponseRestService;
    private ApplicationInterviewFeedbackViewModelPopulator applicationInterviewFeedbackViewModelPopulator;
    private ApplicationFeedbackSummaryViewModelPopulator applicationFeedbackSummaryViewModelPopulator;
    private ApplicationSummaryViewModelPopulator applicationSummaryViewModelPopulator;

    public ApplicationSummaryController() {
    }

    @Autowired
    public ApplicationSummaryController(ApplicationService applicationService,
                                        CompetitionService competitionService,
                                        InterviewAssignmentRestService interviewAssignmentRestService,
                                        InterviewResponseRestService interviewResponseRestService,
                                        ApplicationInterviewFeedbackViewModelPopulator applicationInterviewFeedbackViewModelPopulator,
                                        ApplicationFeedbackSummaryViewModelPopulator applicationFeedbackSummaryViewModelPopulator,
                                        ApplicationSummaryViewModelPopulator applicationSummaryViewModelPopulator) {
        this.applicationService = applicationService;
        this.competitionService = competitionService;
        this.interviewAssignmentRestService = interviewAssignmentRestService;
        this.interviewResponseRestService = interviewResponseRestService;
        this.applicationInterviewFeedbackViewModelPopulator = applicationInterviewFeedbackViewModelPopulator;
        this.applicationFeedbackSummaryViewModelPopulator = applicationFeedbackSummaryViewModelPopulator;
        this.applicationSummaryViewModelPopulator = applicationSummaryViewModelPopulator;
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

        ApplicationResource application = applicationService.getById(applicationId);
        CompetitionResource competition = competitionService.getById(application.getCompetition());

        boolean isApplicationAssignedToInterview = interviewAssignmentRestService.isAssignedToInterview(applicationId).getSuccess();

        if (competition.getCompetitionStatus().isFeedbackReleased() && !isApplicationAssignedToInterview) {
            model.addAttribute("applicationFeedbackSummaryViewModel", applicationFeedbackSummaryViewModelPopulator.populate(applicationId, user));
            return "application-feedback-summary";
        } else if (isApplicationAssignedToInterview) {
            model.addAttribute("interviewFeedbackViewModel", applicationInterviewFeedbackViewModelPopulator.populate(applicationId, user));
            return "application-interview-feedback";
        }
        else {
            model.addAttribute("applicationSummaryViewModel", applicationSummaryViewModelPopulator.populate(applicationId, user));
            return "application-summary";
        }
    }

    @SecuredBySpring(value = "READ", description = "Applicants have permission to upload interview feedback.")
    @PreAuthorize("hasAuthority('applicant')")
    @PostMapping(value = "/{applicationId}/summary", params = "uploadResponse")
    public String uploadResponse(@ModelAttribute("form") ApplicationForm applicationForm,
                                 @ModelAttribute("interviewResponseForm") InterviewResponseForm form,
                                 BindingResult bindingResult,
                                 ValidationHandler validationHandler,
                                 Model model,
                                 @PathVariable("applicationId") long applicationId,
                                 UserResource user) {

        Supplier<String> failureAndSuccessView = () -> applicationSummary(new ApplicationForm(), form, bindingResult, validationHandler, model, applicationId, user);
        MultipartFile file = form.getResponse();
        RestResult<Void> sendResult = interviewResponseRestService
                .uploadResponse(applicationId, file.getContentType(), file.getSize(), file.getOriginalFilename(), getMultipartFileBytes(file));

        return validationHandler.addAnyErrors(error(removeDuplicates(sendResult.getErrors())), fileUploadField("response"), defaultConverters())
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
