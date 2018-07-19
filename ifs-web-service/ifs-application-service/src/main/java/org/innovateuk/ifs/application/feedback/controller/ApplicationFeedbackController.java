package org.innovateuk.ifs.application.feedback.controller;

import org.innovateuk.ifs.application.feedback.populator.ApplicationFeedbackViewModelPopulator;
import org.innovateuk.ifs.application.forms.form.InterviewResponseForm;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.ApplicationService;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.interview.service.InterviewAssignmentRestService;
import org.innovateuk.ifs.interview.service.InterviewResponseRestService;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.function.Supplier;

import static org.innovateuk.ifs.commons.rest.RestFailure.error;
import static org.innovateuk.ifs.controller.ErrorToObjectErrorConverterFactory.defaultConverters;
import static org.innovateuk.ifs.controller.ErrorToObjectErrorConverterFactory.fileUploadField;
import static org.innovateuk.ifs.controller.FileUploadControllerUtils.getMultipartFileBytes;
import static org.innovateuk.ifs.file.controller.FileDownloadControllerUtils.getFileResponseEntity;
import static org.innovateuk.ifs.util.CollectionFunctions.removeDuplicates;

@Controller
@RequestMapping("/application")
public class ApplicationFeedbackController {

    private InterviewAssignmentRestService interviewAssignmentRestService;
    private InterviewResponseRestService interviewResponseRestService;
    private ApplicationFeedbackViewModelPopulator applicationFeedbackViewModelPopulator;
    private ApplicationService applicationService;
    private CompetitionRestService competitionRestService;

    public ApplicationFeedbackController() {
    }

    @Autowired
    public ApplicationFeedbackController(InterviewAssignmentRestService interviewAssignmentRestService,
                                         InterviewResponseRestService interviewResponseRestService,
                                         ApplicationFeedbackViewModelPopulator applicationFeedbackViewModelPopulator,
                                         ApplicationService applicationService,
                                         CompetitionRestService competitionRestService) {
        this.interviewAssignmentRestService = interviewAssignmentRestService;
        this.interviewResponseRestService = interviewResponseRestService;
        this.applicationFeedbackViewModelPopulator = applicationFeedbackViewModelPopulator;
        this.applicationService = applicationService;
        this.competitionRestService = competitionRestService;
    }

    @SecuredBySpring(value = "READ", description = "Applicants, support staff, innovation leads, comp admins and project finance users have permission to view the application summary page")
    @PreAuthorize("hasAnyAuthority('applicant', 'assessor', 'comp_admin', 'project_finance', 'innovation_lead')")
    @GetMapping("/{applicationId}/feedback")
    public String feedback(@ModelAttribute("interviewResponseForm") InterviewResponseForm interviewResponseForm,
                           BindingResult bindingResult,
                           ValidationHandler validationHandler,
                           Model model,
                           @PathVariable("applicationId") long applicationId,
                           UserResource user,
                           @RequestParam(value = "origin", defaultValue = "APPLICANT_DASHBOARD") String origin,
                           @RequestParam MultiValueMap<String, String> queryParams) {

        ApplicationResource application = applicationService.getById(applicationId);
        CompetitionResource competition = competitionRestService.getCompetitionById(application.getCompetition()).getSuccess();
        boolean isApplicationAssignedToInterview = interviewAssignmentRestService.isAssignedToInterview(applicationId).getSuccess();
        if (!competition.getCompetitionStatus().isFeedbackReleased() && !isApplicationAssignedToInterview) {
            return redirectToSummary(applicationId, queryParams);
        }
        model.addAttribute("model", applicationFeedbackViewModelPopulator.populate(applicationId, user, queryParams, origin));
        return "application-feedback";
    }

    @SecuredBySpring(value = "READ", description = "Applicants have permission to upload interview feedback.")
    @PreAuthorize("hasAuthority('applicant')")
    @PostMapping(value = "/{applicationId}/feedback", params = "uploadResponse")
    public String uploadResponse(@ModelAttribute("interviewResponseForm") InterviewResponseForm form,
                                 BindingResult bindingResult,
                                 ValidationHandler validationHandler,
                                 Model model,
                                 @PathVariable("applicationId") long applicationId,
                                 UserResource user,
                                 @RequestParam(value = "origin", defaultValue = "APPLICANT_DASHBOARD") String origin,
                                 @RequestParam MultiValueMap<String, String> queryParams) {

        Supplier<String> failureAndSuccessView = () -> feedback(form, bindingResult, validationHandler, model, applicationId, user, origin, queryParams);
        MultipartFile file = form.getResponse();
        RestResult<Void> sendResult = interviewResponseRestService
                .uploadResponse(applicationId, file.getContentType(), file.getSize(), file.getOriginalFilename(), getMultipartFileBytes(file));

        return validationHandler.addAnyErrors(error(removeDuplicates(sendResult.getErrors())), fileUploadField("response"), defaultConverters())
                .failNowOrSucceedWith(failureAndSuccessView, failureAndSuccessView);
    }

    @SecuredBySpring(value = "READ", description = "Applicants have permission to remove interview feedback.")
    @PreAuthorize("hasAuthority('applicant')")
    @PostMapping(value = "/{applicationId}/feedback", params = "removeResponse")
    public String removeResponse(@ModelAttribute("interviewResponseForm") InterviewResponseForm interviewResponseForm,
                                 BindingResult bindingResult,
                                 ValidationHandler validationHandler,
                                 Model model,
                                 @PathVariable("applicationId") long applicationId,
                                 UserResource user,
                                 @RequestParam(value = "origin", defaultValue = "APPLICANT_DASHBOARD") String origin,
                                 @RequestParam MultiValueMap<String, String> queryParams) {

        Supplier<String> failureAndSuccessView = () -> feedback(interviewResponseForm, bindingResult, validationHandler, model, applicationId, user, origin, queryParams);
        RestResult<Void> sendResult = interviewResponseRestService
                .deleteResponse(applicationId);

        return validationHandler.addAnyErrors(error(removeDuplicates(sendResult.getErrors())))
                .failNowOrSucceedWith(failureAndSuccessView, failureAndSuccessView);
    }

    @GetMapping("/{applicationId}/feedback/download-response")
    @SecuredBySpring(value = "READ", description = "Applicants, support staff, innovation leads, comp admins and project finance users have permission to view uploaded interview feedback.")
    @PreAuthorize("hasAnyAuthority('applicant', 'assessor', 'comp_admin', 'project_finance', 'innovation_lead')")
    public @ResponseBody
    ResponseEntity<ByteArrayResource> downloadResponse(Model model,
                                                       @PathVariable("applicationId") long applicationId) {
        return getFileResponseEntity(interviewResponseRestService.downloadResponse(applicationId).getSuccess(),
                interviewResponseRestService.findResponse(applicationId).getSuccess());
    }

    @GetMapping("/{applicationId}/feedback/download-feedback")
    @SecuredBySpring(value = "READ", description = "Applicants, support staff, innovation leads, comp admins and project finance users have permission to view uploaded interview feedback.")
    @PreAuthorize("hasAnyAuthority('applicant', 'assessor', 'comp_admin', 'project_finance', 'innovation_lead')")
    public @ResponseBody
    ResponseEntity<ByteArrayResource> downloadFeedback(Model model,
                                                       @PathVariable("applicationId") long applicationId) {
        return getFileResponseEntity(interviewAssignmentRestService.downloadFeedback(applicationId).getSuccess(),
                interviewAssignmentRestService.findFeedback(applicationId).getSuccess());
    }

    private String redirectToSummary(long applicationId, MultiValueMap<String, String> queryParams) {
        return UriComponentsBuilder.fromPath(String.format("redirect:/application/%s/summary", applicationId))
                .queryParams(queryParams)
                .build()
                .encode()
                .toUriString();
    }
}
