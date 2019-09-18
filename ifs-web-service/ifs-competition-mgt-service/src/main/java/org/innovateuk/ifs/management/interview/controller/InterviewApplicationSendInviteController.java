package org.innovateuk.ifs.management.interview.controller;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.interview.service.InterviewAssignmentRestService;
import org.innovateuk.ifs.invite.resource.AssessorInviteSendResource;
import org.innovateuk.ifs.management.interview.form.InterviewApplicationResendForm;
import org.innovateuk.ifs.management.interview.form.InterviewApplicationSendForm;
import org.innovateuk.ifs.management.interview.model.InterviewApplicationSentInviteModelPopulator;
import org.innovateuk.ifs.management.interview.model.InterviewApplicationsSendModelPopulator;
import org.innovateuk.ifs.management.interview.viewmodel.InterviewAssignmentApplicationsSendViewModel;
import org.innovateuk.ifs.management.interview.viewmodel.InterviewAssignmentApplicationsSentInviteViewModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.function.Supplier;

import static java.lang.String.format;
import static org.innovateuk.ifs.commons.rest.RestFailure.error;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.controller.ErrorToObjectErrorConverterFactory.fileUploadField;
import static org.innovateuk.ifs.controller.FileUploadControllerUtils.getMultipartFileBytes;
import static org.innovateuk.ifs.file.controller.FileDownloadControllerUtils.getFileResponseEntity;
import static org.innovateuk.ifs.util.CollectionFunctions.removeDuplicates;

/**
 * This controller will handle all Competition Management requests related to sending interview panel invites to assessors
 */
@Controller
@RequestMapping("/assessment/interview/competition/{competitionId}/applications/invite")
@SecuredBySpring(value = "Controller", description = "Comp Admins and Project Finance users can invite applications to an Interview Panel", securedType = InterviewApplicationSendInviteController.class)
@PreAuthorize("hasPermission(#competitionId, 'org.innovateuk.ifs.competition.resource.CompetitionCompositeId', 'INTERVIEW_APPLICATIONS')")
public class InterviewApplicationSendInviteController {

    private InterviewApplicationsSendModelPopulator interviewApplicationsSendModelPopulator;
    private InterviewApplicationSentInviteModelPopulator interviewApplicationSentInviteModelPopulator;
    private InterviewAssignmentRestService interviewAssignmentRestService;

    @Autowired
    public InterviewApplicationSendInviteController(InterviewApplicationsSendModelPopulator interviewApplicationsSendModelPopulator,
                                                    InterviewApplicationSentInviteModelPopulator interviewApplicationSentInviteModelPopulator,
                                                    InterviewAssignmentRestService interviewAssignmentRestService) {
        this.interviewApplicationsSendModelPopulator = interviewApplicationsSendModelPopulator;
        this.interviewApplicationSentInviteModelPopulator = interviewApplicationSentInviteModelPopulator;
        this.interviewAssignmentRestService = interviewAssignmentRestService;
    }

    @GetMapping("/send")
    public String getInvitesToSend(Model model,
                                   @PathVariable("competitionId") long competitionId,
                                   @RequestParam(defaultValue = "0") int page,
                                   @ModelAttribute(name = "form", binding = false) InterviewApplicationSendForm form,
                                   BindingResult bindingResult) {

        InterviewAssignmentApplicationsSendViewModel viewModel = interviewApplicationsSendModelPopulator.populateModel(competitionId, page, form);

        model.addAttribute("model", viewModel);

        if (!bindingResult.hasErrors()) {
            populateGroupInviteFormWithExistingValues(form);
        }

        return "assessors/interview/application-send-invites";
    }

    @PostMapping("/send")
    public String sendInvites(Model model,
                              @PathVariable("competitionId") long competitionId,
                              @ModelAttribute("form") @Valid InterviewApplicationSendForm form,
                              BindingResult bindingResult,
                              ValidationHandler validationHandler) {

        Supplier<String> failureView = () -> getInvitesToSend(model, competitionId, 0, form, bindingResult);
        return validationHandler.failNowOrSucceedWith(failureView, () -> {
            RestResult<Void> sendResult = interviewAssignmentRestService
                    .sendAllInvites(competitionId, new AssessorInviteSendResource(form.getSubject(), form.getContent()));

            return validationHandler.addAnyErrors(error(removeDuplicates(sendResult.getErrors())))
                    .failNowOrSucceedWith(failureView, () -> redirectToStatusTab(competitionId));
        });
    }

    @PostMapping(value = "/send", params = {"attachFeedbackApplicationId"})
    public String uploadFeedback(Model model,
                              @PathVariable("competitionId") long competitionId,
                              @ModelAttribute("form") InterviewApplicationSendForm form,
                              BindingResult bindingResult,
                              ValidationHandler validationHandler) {

        Supplier<String> failureAndSuccesView = () ->
                getInvitesToSend(model, competitionId, form.getPage(), form, bindingResult);

        MultipartFile file = form.getNotEmptyFile();
        int index = form.getFeedback().indexOf(file);
        return validationHandler.performFileUpload(String.format("feedback[%s]", index), failureAndSuccesView, () -> interviewAssignmentRestService
                .uploadFeedback(form.getAttachFeedbackApplicationId(), file.getContentType(), file.getSize(), file.getOriginalFilename(), getMultipartFileBytes(file)));
    }

    @PostMapping(value = "/send", params = {"removeFeedbackApplicationId"})
    public String removeFeedback(Model model,
                                 @PathVariable("competitionId") long competitionId,
                                 @ModelAttribute("form") InterviewApplicationSendForm form,
                                 BindingResult bindingResult,
                                 ValidationHandler validationHandler) {

        Supplier<String> failureAndSuccessView = () -> getInvitesToSend(model, competitionId, form.getPage(), form, bindingResult);

        RestResult<Void> sendResult = interviewAssignmentRestService
                .deleteFeedback(form.getRemoveFeedbackApplicationId());

        return validationHandler.addAnyErrors(error(removeDuplicates(sendResult.getErrors())))
                .failNowOrSucceedWith(failureAndSuccessView, failureAndSuccessView);
    }

    @GetMapping("/send/view-feedback/{applicationId}")
    public @ResponseBody ResponseEntity<ByteArrayResource> downloadFeedback(Model model,
                                   @PathVariable("competitionId") long competitionId,
                                   @PathVariable("applicationId") long applicationId) {

        return getFileResponseEntity(interviewAssignmentRestService.downloadFeedback(applicationId).getSuccess(),
                interviewAssignmentRestService.findFeedback(applicationId).getSuccess());
    }

    @GetMapping(value = "/{applicationId}/view")
    public String viewInvite(Model model,
                             @PathVariable("competitionId") long competitionId,
                             @PathVariable("applicationId") long applicationId) {
        InterviewAssignmentApplicationsSentInviteViewModel viewModel = interviewApplicationSentInviteModelPopulator.populate(competitionId, applicationId);
        model.addAttribute("model", viewModel);
        return "assessors/interview/application-view-invite";
    }

    @GetMapping(value = "/{applicationId}/edit")
    public String editInvite(Model model,
                             @ModelAttribute("form") InterviewApplicationResendForm form,
                             @PathVariable("competitionId") long competitionId,
                             @PathVariable("applicationId") long applicationId) {
        InterviewAssignmentApplicationsSentInviteViewModel viewModel = interviewApplicationSentInviteModelPopulator.populate(competitionId, applicationId);
        model.addAttribute("model", viewModel);
        if (form.getSubject() == null) {
            form.setSubject(viewModel.getSubject());
            form.setContent(viewModel.getAdditionalText());
        }
        return "assessors/interview/application-edit-invite";
    }

    @PostMapping(value = "/{applicationId}/edit")
    public String resendInvite(Model model, @Valid @ModelAttribute("form") InterviewApplicationResendForm form,
                             BindingResult bindingResult,
                             ValidationHandler validationHandler,
                             @PathVariable("competitionId") long competitionId,
                             @PathVariable("applicationId") long applicationId) {
        Supplier<String> failureView = () -> editInvite(model,  form, competitionId, applicationId);
        Supplier<String> successView = () -> redirectToStatusTab(competitionId);

        return validationHandler.failNowOrSucceedWith(failureView, () -> {
            final RestResult<Void> fileResult;
            if (form.getFeedback() != null && !form.getFeedback().isEmpty()) {
                MultipartFile file = form.getFeedback();
                interviewAssignmentRestService.deleteFeedback(applicationId);
                fileResult = interviewAssignmentRestService
                        .uploadFeedback(applicationId, file.getContentType(), file.getSize(), file.getOriginalFilename(), getMultipartFileBytes(file));

            } else if (form.isRemoveFile()) {
                fileResult = interviewAssignmentRestService.deleteFeedback(applicationId);
            } else {
                fileResult = restSuccess();
            }

            return validationHandler.addAnyErrors(fileResult, fileUploadField("feedback")).failNowOrSucceedWith(failureView, () -> {
                RestResult<Void> result = interviewAssignmentRestService
                        .resendInvite(applicationId, new AssessorInviteSendResource(form.getSubject(), form.getContent()));

                return validationHandler.addAnyErrors(error(removeDuplicates(result.getErrors())))
                        .failNowOrSucceedWith(failureView, successView);
                }
            );
        });
    }

    private String redirectToStatusTab(long competitionId) {
        return format("redirect:/assessment/interview/competition/%s/applications/view-status", competitionId);
    }

    private void populateGroupInviteFormWithExistingValues(InterviewApplicationSendForm form) {
        form.setSubject("Please attend an interview for an Innovate UK funding competition");
    }
}
