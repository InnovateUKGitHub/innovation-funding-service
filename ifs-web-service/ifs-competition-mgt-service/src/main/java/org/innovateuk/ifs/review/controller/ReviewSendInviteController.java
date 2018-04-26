package org.innovateuk.ifs.review.controller;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.invite.resource.AssessorInviteSendResource;
import org.innovateuk.ifs.invite.resource.AssessorInvitesToSendResource;
import org.innovateuk.ifs.management.controller.CompetitionManagementCookieController;
import org.innovateuk.ifs.management.form.ResendInviteForm;
import org.innovateuk.ifs.management.form.SendInviteForm;
import org.innovateuk.ifs.management.viewmodel.SendInvitesViewModel;
import org.innovateuk.ifs.review.form.ReviewOverviewSelectionForm;
import org.innovateuk.ifs.review.service.ReviewInviteRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.function.Supplier;

import static java.lang.String.format;
import static org.innovateuk.ifs.commons.rest.RestFailure.error;
import static org.innovateuk.ifs.controller.ErrorToObjectErrorConverterFactory.asGlobalErrors;
import static org.innovateuk.ifs.controller.ErrorToObjectErrorConverterFactory.fieldErrorsToFieldErrors;
import static org.innovateuk.ifs.util.CollectionFunctions.removeDuplicates;
import static org.innovateuk.ifs.util.MapFunctions.asMap;

/**
 * This controller will handle all Competition Management requests related to sending assessment panel invites to assessors
 */
@Controller
@RequestMapping("/panel/competition/{competitionId}/assessors/invite")
@SecuredBySpring(value = "Controller", description = "Only comp admin and project finance users can setup assessment" +
        " panels if they competition supports them", securedType = ReviewSendInviteController.class)
@PreAuthorize("hasPermission(#competitionId, 'org.innovateuk.ifs.competition.resource.CompetitionCompositeId', 'REVIEW')")
public class ReviewSendInviteController extends CompetitionManagementCookieController<ReviewOverviewSelectionForm> {

    private static final String SELECTION_FORM = "assessorPanelOverviewSelectionForm";

    @Autowired
    private ReviewInviteRestService reviewInviteRestService;

    @Override
    protected String getCookieName() {
        return SELECTION_FORM;
    }

    @Override
    protected Class<ReviewOverviewSelectionForm> getFormType() {
        return ReviewOverviewSelectionForm.class;
    }

    @GetMapping("/send")
    public String getInvitesToSend(Model model,
                                   @PathVariable("competitionId") long competitionId,
                                   @ModelAttribute(name = "form", binding = false) SendInviteForm form,
                                   BindingResult bindingResult) {
        AssessorInvitesToSendResource invites = reviewInviteRestService.getAllInvitesToSend(competitionId).getSuccess();

        if (invites.getRecipients().isEmpty()) {
            return redirectToPanelOverviewTab(competitionId);
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

        return "assessors/panel-send-invites";
    }

    @PostMapping("/send")
    public String sendInvites(Model model,
                              @PathVariable("competitionId") long competitionId,
                              @ModelAttribute("form") @Valid SendInviteForm form,
                              BindingResult bindingResult,
                              ValidationHandler validationHandler) {
        Supplier<String> failureView = () -> getInvitesToSend(model, competitionId, form, bindingResult);

        return validationHandler.failNowOrSucceedWith(failureView, () -> {
            RestResult<Void> sendResult = reviewInviteRestService
                    .sendAllInvites(competitionId, new AssessorInviteSendResource(form.getSubject(), form.getContent()));

            return validationHandler.addAnyErrors(error(removeDuplicates(sendResult.getErrors())))
                    .failNowOrSucceedWith(failureView, () -> redirectToPanelOverviewTab(competitionId));
        });
    }

    @GetMapping("/reviewResend")
    public String getInvitesToResendFailureView(Model model,
                                                @PathVariable("competitionId") long competitionId,
                                                @ModelAttribute(name = "form", binding = false) ResendInviteForm inviteform,
                                                BindingResult bindingResult,
                                                ValidationHandler validationHandler) {
        if(inviteform.getInviteIds() == null || inviteform.getInviteIds().isEmpty()){
            return redirectToOverview(competitionId, 0);
        }

        AssessorInvitesToSendResource invites = reviewInviteRestService.getAllInvitesToResend(
                competitionId,
                inviteform.getInviteIds()).getSuccess();
        model.addAttribute("model", new SendInvitesViewModel(
                invites.getCompetitionId(),
                invites.getCompetitionName(),
                invites.getRecipients(),
                invites.getContent()
        ));
        populateResendInviteFormWithExistingValues(inviteform, invites);
        return "assessors/panel-resend-invites";
    }

    @PostMapping("/reviewResend")
    public String getInvitesToResend(Model model,
                                     @PathVariable("competitionId") long competitionId,
                                     @RequestParam(defaultValue = "0") int page,
                                     @ModelAttribute(SELECTION_FORM) ReviewOverviewSelectionForm selectionForm,
                                     @ModelAttribute(name = "form", binding = false) ResendInviteForm inviteform,
                                     ValidationHandler validationHandler,
                                     BindingResult bindingResult,
                                     HttpServletRequest request) {

        ReviewOverviewSelectionForm submittedSelectionForm = getSelectionFormFromCookie(request, competitionId)
                .filter(form -> !form.getSelectedInviteIds().isEmpty())
                .orElse(selectionForm);
        Supplier<String> failureView = () -> redirectToOverview(competitionId, page);

        return validationHandler.failNowOrSucceedWith(failureView, () -> {
            AssessorInvitesToSendResource invites = reviewInviteRestService.getAllInvitesToResend(
                    competitionId,
                    submittedSelectionForm.getSelectedInviteIds()).getSuccess();
            model.addAttribute("model", new SendInvitesViewModel(
                    invites.getCompetitionId(),
                    invites.getCompetitionName(),
                    invites.getRecipients(),
                    invites.getContent()
            ));
            inviteform.setInviteIds(submittedSelectionForm.getSelectedInviteIds());
            populateResendInviteFormWithExistingValues(inviteform, invites);
            return "assessors/panel-resend-invites";
        });
    }

    @PostMapping("/resend")
    public String resendInvites (Model model,
                                 @PathVariable("competitionId") long competitionId,
                                 @ModelAttribute("form") @Valid ResendInviteForm form,
                                 BindingResult bindingResult,
                                 ValidationHandler validationHandler,
                                 HttpServletResponse response){

        Supplier<String> failureView = () -> getInvitesToResendFailureView(model, competitionId, form, bindingResult, validationHandler);

        return validationHandler.failNowOrSucceedWith(failureView, () -> {
            ServiceResult<Void> resendResult = reviewInviteRestService.resendInvites(form.getInviteIds(),
                    new AssessorInviteSendResource(form.getSubject(), form.getContent()))
                    .toServiceResult();
            return validationHandler.addAnyErrors(resendResult, fieldErrorsToFieldErrors(), asGlobalErrors())
                    .failNowOrSucceedWith(failureView, () -> {
                        removeCookie(response, competitionId);
                        return redirectToOverview(competitionId, 0);
                    });
        });
    }

    private String redirectToOverview(long competitionId, int page) {
        UriComponentsBuilder builder = UriComponentsBuilder
                .fromPath("/assessment/panel/competition/{competitionId}/assessors/pending-and-declined")
                .queryParam("page", page);

        return "redirect:" + builder.buildAndExpand(asMap("competitionId", competitionId))
                .toUriString();
    }

    private String redirectToResendView(long competitionId) {
        return format("redirect:/assessment/panel/competition/%s/assessors/invite", competitionId);
    }

    private void populateResendInviteFormWithExistingValues(ResendInviteForm form, AssessorInvitesToSendResource assessorInviteToSendResource) {
        form.setSubject(format("Invitation to assessment panel for '%s'", assessorInviteToSendResource.getCompetitionName()));
    }

    private void populateGroupInviteFormWithExistingValues(SendInviteForm form, AssessorInvitesToSendResource assessorInviteToSendResource) {
        form.setSubject(format("Invitation to assessment panel for '%s'", assessorInviteToSendResource.getCompetitionName()));
    }

    private String redirectToPanelOverviewTab(long competitionId) {
        return format("redirect:/assessment/panel/competition/%s/assessors/pending-and-declined", competitionId);
    }
}
