package org.innovateuk.ifs.interview.controller;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.interview.form.InterviewApplicationSendForm;
import org.innovateuk.ifs.interview.model.InterviewApplicationsSendModelPopulator;
import org.innovateuk.ifs.interview.service.InterviewAssignmentRestService;
import org.innovateuk.ifs.interview.viewmodel.InterviewAssignmentApplicationsSendViewModel;
import org.innovateuk.ifs.invite.resource.AssessorInviteSendResource;
import org.innovateuk.ifs.management.service.CompetitionManagementApplicationServiceImpl;
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

import javax.validation.Valid;
import java.util.function.Supplier;

import static java.lang.String.format;
import static org.innovateuk.ifs.commons.rest.RestFailure.error;
import static org.innovateuk.ifs.controller.FileUploadControllerUtils.getMultipartFileBytes;
import static org.innovateuk.ifs.file.controller.FileDownloadControllerUtils.getFileResponseEntity;
import static org.innovateuk.ifs.util.BackLinkUtil.buildOriginQueryString;
import static org.innovateuk.ifs.util.CollectionFunctions.removeDuplicates;

/**
 * This controller will handle all Competition Management requests related to sending interview panel invites to assessors
 */
@Controller
@RequestMapping("/assessment/interview/competition/{competitionId}/applications/invite")
@SecuredBySpring(value = "Controller", description = "Comp Admins and Project Finance users can invite applications to an Interview Panel", securedType = InterviewApplicationSendInviteController.class)
@PreAuthorize("hasPermission(#competitionId, 'org.innovateuk.ifs.competition.resource.CompetitionCompositeId', 'INTERVIEW_APPLICATIONS')")
public class InterviewApplicationSendInviteController {

    @Autowired
    private InterviewApplicationsSendModelPopulator interviewApplicationsSendModelPopulator;

    @Autowired
    private InterviewAssignmentRestService interviewAssignmentRestService;

    @GetMapping("/send")
    public String getInvitesToSend(Model model,
                                   @PathVariable("competitionId") long competitionId,
                                   @RequestParam(defaultValue = "0") int page,
                                   @RequestParam MultiValueMap<String, String> queryParams,
                                   @ModelAttribute(name = "form", binding = false) InterviewApplicationSendForm form,
                                   BindingResult bindingResult) {

        String originQuery = buildOriginQueryString(CompetitionManagementApplicationServiceImpl.ApplicationOverviewOrigin.INTERVIEW_PANEL_SEND, queryParams);
        InterviewAssignmentApplicationsSendViewModel viewModel = interviewApplicationsSendModelPopulator.populateModel(competitionId, page, originQuery);

        model.addAttribute("model", viewModel);

        if (!bindingResult.hasErrors()) {
            populateGroupInviteFormWithExistingValues(form);
        }

        return "assessors/interview/application-send-invites";
    }

    @PostMapping("/send")
    public String sendInvites(Model model,
                              @PathVariable("competitionId") long competitionId,
                              @RequestParam MultiValueMap<String, String> queryParams,
                              @ModelAttribute("form") @Valid InterviewApplicationSendForm form,
                              BindingResult bindingResult,
                              ValidationHandler validationHandler) {

        Supplier<String> failureView = () -> getInvitesToSend(model, competitionId, 0, queryParams, form, bindingResult);

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
                              @RequestParam MultiValueMap<String, String> queryParams,
                              @ModelAttribute("form") InterviewApplicationSendForm form,
                              BindingResult bindingResult,
                              ValidationHandler validationHandler) {

        Supplier<String> failureView = () -> {
            model.addAttribute("applicationInError", form.getAttachFeedbackApplicationId());
            return getInvitesToSend(model, competitionId, 0, queryParams, form, bindingResult);
        };

        MultipartFile file = form.getFeedback();
        RestResult<Void> sendResult = interviewAssignmentRestService
                    .uploadFeedback(form.getAttachFeedbackApplicationId(), file.getContentType(), file.getSize(), file.getOriginalFilename(), getMultipartFileBytes(file));

        return validationHandler.addAnyErrors(error(removeDuplicates(sendResult.getErrors())))
                .failNowOrSucceedWith(failureView, () -> getInvitesToSend(model, competitionId, 0, queryParams, form, bindingResult));
    }

    @PostMapping(value = "/send", params = {"removeFeedbackApplicationId"})
    public String removeFeedback(Model model,
                                 @PathVariable("competitionId") long competitionId,
                                 @RequestParam MultiValueMap<String, String> queryParams,
                                 @ModelAttribute("form") InterviewApplicationSendForm form,
                                 BindingResult bindingResult,
                                 ValidationHandler validationHandler) {

        Supplier<String> failureView = () -> {
            model.addAttribute("applicationInError", form.getRemoveFeedbackApplicationId());
            return getInvitesToSend(model, competitionId, 0, queryParams, form, bindingResult);
        };

        RestResult<Void> sendResult = interviewAssignmentRestService
                .deleteFeedback(form.getRemoveFeedbackApplicationId());

        return validationHandler.addAnyErrors(error(removeDuplicates(sendResult.getErrors())))
                .failNowOrSucceedWith(failureView, () -> getInvitesToSend(model, competitionId, 0, queryParams, form, bindingResult));
    }


    @GetMapping("/send/view-feedback/{applicationId}")
    public @ResponseBody ResponseEntity<ByteArrayResource> downloadFeedback(Model model,
                                   @PathVariable("competitionId") long competitionId,
                                   @PathVariable("applicationId") long applicationId) {

        return getFileResponseEntity(interviewAssignmentRestService.downloadFeedback(applicationId).getSuccess(),
                interviewAssignmentRestService.findFeedback(applicationId).getSuccess());
    }

    private String redirectToStatusTab(long competitionId) {
        return format("redirect:/assessment/interview/competition/%s/applications/view-status", competitionId);
    }

    private void populateGroupInviteFormWithExistingValues(InterviewApplicationSendForm form) {
        form.setSubject("Please attend an interview for an Innovate UK funding competition");
    }


}
