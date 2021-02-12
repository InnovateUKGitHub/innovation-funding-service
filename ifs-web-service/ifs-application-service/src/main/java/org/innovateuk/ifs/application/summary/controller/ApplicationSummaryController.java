package org.innovateuk.ifs.application.summary.controller;

import org.innovateuk.ifs.application.forms.form.InterviewResponseForm;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.ApplicationService;
import org.innovateuk.ifs.application.summary.populator.ApplicationSummaryViewModelPopulator;
import org.innovateuk.ifs.async.annotations.AsyncMethod;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.granttransfer.service.EuGrantTransferRestService;
import org.innovateuk.ifs.interview.service.InterviewAssignmentRestService;
import org.innovateuk.ifs.interview.service.InterviewResponseRestService;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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

    @Autowired
    private ApplicationService applicationService;
    @Autowired
    private CompetitionRestService competitionRestService;
    @Autowired
    private InterviewAssignmentRestService interviewAssignmentRestService;
    @Autowired
    private InterviewResponseRestService interviewResponseRestService;
    @Autowired
    private ApplicationSummaryViewModelPopulator applicationSummaryViewModelPopulator;
    @Autowired
    private EuGrantTransferRestService euGrantTransferRestService;


    @SecuredBySpring(value = "READ", description = "Applicants, monitoring officers and kta have permission to view the application summary page")
    @PreAuthorize("hasAnyAuthority('applicant', 'assessor', 'monitoring_officer', 'knowledge_transfer_adviser', 'supporter')")
    @GetMapping("/{applicationId}/summary")
    @AsyncMethod
    public String applicationSummary(@ModelAttribute("interviewResponseForm") InterviewResponseForm interviewResponseForm,
                                     BindingResult bindingResult,
                                     ValidationHandler validationHandler,
                                     Model model,
                                     @PathVariable("applicationId") long applicationId,
                                     UserResource user) {
        ApplicationResource application = applicationService.getById(applicationId);
        CompetitionResource competition = competitionRestService.getCompetitionById(application.getCompetition()).getSuccess();

        model.addAttribute("model", applicationSummaryViewModelPopulator.populate(application, competition, user));
        return "application-summary";
    }

    @SecuredBySpring(value = "READ", description = "Applicants, support staff, innovation leads and stakeholders have permission to view the horizon 2020 grant agreement")
    @PreAuthorize("hasAnyAuthority('applicant', 'support', 'innovation_lead', 'stakeholder', 'comp_admin')")
    @GetMapping("/{applicationId}/grant-agreement")
    public @ResponseBody
    ResponseEntity<ByteArrayResource> downloadGrantAgreement(@PathVariable long applicationId) {
        return getFileResponseEntity(euGrantTransferRestService.downloadGrantAgreement(applicationId).getSuccess(),
                euGrantTransferRestService.findGrantAgreement(applicationId).getSuccess());
    }

    @SecuredBySpring(value = "READ", description = "Applicants have permission to upload interview feedback.")
    @PreAuthorize("hasAuthority('applicant')")
    @PostMapping(value = "/{applicationId}/summary",params = "uploadResponse")
    public String uploadResponse(@ModelAttribute("interviewResponseForm") InterviewResponseForm form,
                                 BindingResult bindingResult,
                                 ValidationHandler validationHandler,
                                 Model model,
                                 @PathVariable("applicationId") long applicationId,
                                 UserResource user) {
        Supplier<String> failureAndSuccessView = () -> applicationSummary(form, bindingResult, validationHandler, model, applicationId, user);
        MultipartFile file = form.getResponse();

        return validationHandler.performFileUpload("response", failureAndSuccessView, () -> interviewResponseRestService
                .uploadResponse(applicationId, file.getContentType(), file.getSize(), file.getOriginalFilename(), getMultipartFileBytes(file)));
    }

    @SecuredBySpring(value = "READ", description = "Applicants have permission to remove interview feedback.")
    @PreAuthorize("hasAuthority('applicant')")
    @PostMapping(value = "/{applicationId}/summary", params = "removeResponse")
    public String removeResponse(@ModelAttribute("interviewResponseForm") InterviewResponseForm interviewResponseForm,
                                 BindingResult bindingResult,
                                 ValidationHandler validationHandler,
                                 Model model,
                                 @PathVariable("applicationId") long applicationId,
                                 UserResource user) {

        Supplier<String> failureAndSuccessView = () -> applicationSummary(interviewResponseForm, bindingResult, validationHandler, model, applicationId, user);
        RestResult<Void> sendResult = interviewResponseRestService
                .deleteResponse(applicationId);

        return validationHandler.addAnyErrors(error(removeDuplicates(sendResult.getErrors())))
                .failNowOrSucceedWith(failureAndSuccessView, failureAndSuccessView);
    }

    @GetMapping("/{applicationId}/summary/download-response")
    @SecuredBySpring(value = "READ", description = "Applicants, support staff, innovation leads, stakeholders, comp admins and project finance users have permission to view uploaded interview feedback.")
    @PreAuthorize("hasAnyAuthority('applicant', 'assessor', 'comp_admin', 'innovation_lead', 'stakeholder', 'monitoring_officer')")
    public @ResponseBody
    ResponseEntity<ByteArrayResource> downloadResponse(Model model,
                                                       @PathVariable("applicationId") long applicationId) {
        return getFileResponseEntity(interviewResponseRestService.downloadResponse(applicationId).getSuccess(),
                interviewResponseRestService.findResponse(applicationId).getSuccess());
    }

    @GetMapping("/{applicationId}/summary/download-feedback")
    @SecuredBySpring(value = "READ", description = "Applicants, support staff, innovation leads, stakeholders, comp admins and project finance users have permission to view uploaded interview feedback.")
    @PreAuthorize("hasAnyAuthority('applicant', 'assessor', 'comp_admin', 'innovation_lead', 'stakeholder', 'monitoring_officer')")
    public @ResponseBody
    ResponseEntity<ByteArrayResource> downloadFeedback(Model model,
                                                       @PathVariable("applicationId") long applicationId) {
        return getFileResponseEntity(interviewAssignmentRestService.downloadFeedback(applicationId).getSuccess(),
                interviewAssignmentRestService.findFeedback(applicationId).getSuccess());
    }
}
