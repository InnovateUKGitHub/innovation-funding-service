package org.innovateuk.ifs.management.controller;

import org.innovateuk.ifs.assessment.service.CompetitionInviteRestService;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.invite.resource.AssessorInviteSendResource;
import org.innovateuk.ifs.invite.resource.AssessorInvitesToSendResource;
import org.innovateuk.ifs.management.form.SendInviteForm;
import org.innovateuk.ifs.management.model.SendInviteModelPopulator;
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

    @Autowired
    private SendInviteModelPopulator sendInviteModelPopulator;

    @GetMapping("/send")
    public String getInvitesToSend(Model model,
                                   @PathVariable("competitionId") long competitionId,
                                   @ModelAttribute(name = "form", binding = false) SendInviteForm form,
                                   BindingResult bindingResult) {
        AssessorInvitesToSendResource invites = competitionInviteRestService.getAllInvitesToSend(competitionId).getSuccessObjectOrThrowException();
        model.addAttribute("model", sendInviteModelPopulator.populateModel(invites));

        if (!bindingResult.hasErrors()) {
            populateFormWithExistingValues(form, invites);
        }

        return "assessors/send-invites";
    }

    @PostMapping("/{inviteId}/send")
    public String sendEmail(Model model,
                            @PathVariable("inviteId") long inviteId,
                            @ModelAttribute("form") @Valid SendInviteForm form,
                            BindingResult bindingResult,
                            ValidationHandler validationHandler) {
        AssessorInvitesToSendResource invite = competitionInviteRestService.getInviteToSend(inviteId).getSuccessObjectOrThrowException();

        Supplier<String> failureView = () -> getInvitesToSend(model, inviteId, form, bindingResult);

        return validationHandler.failNowOrSucceedWith(failureView, () -> {
            ServiceResult<Void> sendResult = competitionInviteRestService.sendInvite(inviteId, new AssessorInviteSendResource(
                    form.getSubject(), form.getContent())).toServiceResult();
            return validationHandler.addAnyErrors(sendResult, fieldErrorsToFieldErrors(), asGlobalErrors())
                    .failNowOrSucceedWith(failureView, () -> format("redirect:/competition/%s/assessors/invite", invite.getCompetitionId()));
        });
    }

    private void populateFormWithExistingValues(SendInviteForm form, AssessorInvitesToSendResource assessorInviteToSendResource) {
        form.setSubject(format("Invitation to assess '%s'", assessorInviteToSendResource.getCompetitionName()));
        form.setContent(assessorInviteToSendResource.getContent());
    }
}
