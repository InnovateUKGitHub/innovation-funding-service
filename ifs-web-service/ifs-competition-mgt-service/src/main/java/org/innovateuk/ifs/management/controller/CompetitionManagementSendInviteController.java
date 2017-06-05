package org.innovateuk.ifs.management.controller;

import org.innovateuk.ifs.assessment.service.CompetitionInviteRestService;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.invite.resource.AssessorInviteSendResource;
import org.innovateuk.ifs.invite.resource.AssessorInvitesToSendResource;
import org.innovateuk.ifs.management.form.SendInviteForm;
import org.innovateuk.ifs.management.viewmodel.SendInviteViewModel;
import org.innovateuk.ifs.management.viewmodel.SendInvitesViewModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.function.Supplier;

import static java.lang.String.format;
import static org.innovateuk.ifs.controller.ErrorToObjectErrorConverterFactory.asGlobalErrors;
import static org.innovateuk.ifs.controller.ErrorToObjectErrorConverterFactory.fieldErrorsToFieldErrors;

/**
 * This controller will handle all Competition Management requests related to sending competition invites to assessors
 */
@Controller
@RequestMapping("/competition/{competitionId}/assessors/invite")
@PreAuthorize("hasAnyAuthority('comp_admin','project_finance')")
public class CompetitionManagementSendInviteController {

    @Autowired
    private CompetitionInviteRestService competitionInviteRestService;

    @GetMapping("/send")
    public String getInvitesToSend(Model model,
                                   @PathVariable("competitionId") long competitionId,
                                   @ModelAttribute(name = "form", binding = false) SendInviteForm form,
                                   BindingResult bindingResult) {
        AssessorInvitesToSendResource invites = competitionInviteRestService.getAllInvitesToSend(competitionId).getSuccessObjectOrThrowException();

        if (invites.getRecipients().isEmpty()) {
            return redirectToInviteListView(competitionId);
        }

        model.addAttribute("model", new SendInvitesViewModel(
                invites.getCompetitionId(),
                invites.getCompetitionName(),
                invites.getRecipients(),
                invites.getContent()
        ));

        if (!bindingResult.hasErrors()) {
            populateGroupInviteFormWithExistingValues(form, invites);
        }

        return "assessors/send-invites";
    }

    @GetMapping("/{inviteId}/resend")
    public String getInviteToResend(Model model,
                                    @PathVariable("inviteId") long inviteId,
                                    @ModelAttribute(name = "form", binding = false) SendInviteForm form,
                                    BindingResult bindingResult) {
        AssessorInvitesToSendResource invite = competitionInviteRestService.getInviteToSend(inviteId).getSuccessObjectOrThrowException();
        model.addAttribute("model", new SendInviteViewModel(
                invite.getCompetitionId(),
                inviteId,
                invite.getCompetitionName(),
                invite.getRecipients().get(0),
                invite.getContent())
        );

        if (!bindingResult.hasErrors()) {
            populateFormWithExistingValues(form, invite);
        }

        return "assessors/resend-invite";
    }

    @PostMapping("/send")
    public String sendInvites(Model model,
                              @PathVariable("competitionId") long competitionId,
                              @ModelAttribute("form") @Valid SendInviteForm form,
                              BindingResult bindingResult,
                              ValidationHandler validationHandler) {
        Supplier<String> failureView = () -> getInvitesToSend(model, competitionId, form, bindingResult);

        return validationHandler.failNowOrSucceedWith(failureView, () -> {
            ServiceResult<Void> sendResult = competitionInviteRestService.sendAllInvites(
                    competitionId,
                    new AssessorInviteSendResource(form.getSubject(), form.getContent())
            )
                    .toServiceResult();

            return validationHandler.addAnyErrors(sendResult, fieldErrorsToFieldErrors(), asGlobalErrors())
                    .failNowOrSucceedWith(failureView, () -> redirectToInviteListView(competitionId));
        });
    }

    @PostMapping("/{inviteId}/resend")
    public String resendInvite (Model model,
                                @PathVariable("inviteId") long inviteId,
                                @ModelAttribute("form") @Valid SendInviteForm form,
                                BindingResult bindingResult,
                                ValidationHandler validationHandler){
        AssessorInvitesToSendResource invite = competitionInviteRestService.getInviteToSend(inviteId).getSuccessObjectOrThrowException();

        Supplier<String> failureView = () -> getInviteToResend(model, inviteId, form, bindingResult);

        return validationHandler.failNowOrSucceedWith(failureView, () -> {
            ServiceResult<Void> sendResult = competitionInviteRestService.resendInvite(inviteId, new AssessorInviteSendResource(
                    form.getSubject(), form.getContent())).toServiceResult();
            return validationHandler.addAnyErrors(sendResult, fieldErrorsToFieldErrors(), asGlobalErrors())
                    .failNowOrSucceedWith(failureView, () -> format("redirect:/competition/%s/assessors/overview", invite.getCompetitionId()));
        });
    }

    private String redirectToInviteListView(long competitionId) {
        return format("redirect:/competition/%s/assessors/invite", competitionId);
    }

    private void populateFormWithExistingValues(SendInviteForm form, AssessorInvitesToSendResource assessorInviteToSendResource) {
        form.setSubject(format("Invitation to assess '%s'", assessorInviteToSendResource.getCompetitionName()));
        form.setContent(assessorInviteToSendResource.getContent());
    }

    private void populateGroupInviteFormWithExistingValues(SendInviteForm form, AssessorInvitesToSendResource assessorInviteToSendResource) {
        form.setSubject(format("Invitation to assess '%s'", assessorInviteToSendResource.getCompetitionName()));
    }
}
