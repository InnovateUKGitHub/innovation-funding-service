package org.innovateuk.ifs.management.invite.controller;

import org.innovateuk.ifs.assessment.service.CompetitionInviteRestService;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.invite.resource.AssessorInviteSendResource;
import org.innovateuk.ifs.invite.resource.AssessorInvitesToSendResource;
import org.innovateuk.ifs.management.assessor.form.OverviewSelectionForm;
import org.innovateuk.ifs.management.cookie.CompetitionManagementCookieController;
import org.innovateuk.ifs.management.invite.form.ResendInviteForm;
import org.innovateuk.ifs.management.invite.form.SendInviteForm;
import org.innovateuk.ifs.management.invite.viewmodel.SendInvitesViewModel;
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
import java.util.Optional;
import java.util.function.Supplier;

import static java.lang.String.format;
import static org.innovateuk.ifs.commons.rest.RestFailure.error;
import static org.innovateuk.ifs.controller.ErrorToObjectErrorConverterFactory.asGlobalErrors;
import static org.innovateuk.ifs.controller.ErrorToObjectErrorConverterFactory.fieldErrorsToFieldErrors;
import static org.innovateuk.ifs.util.CollectionFunctions.removeDuplicates;
import static org.innovateuk.ifs.util.MapFunctions.asMap;

/**
 * This controller will handle all Competition Management requests related to sending competition invites to assessors
 */
@Controller
@RequestMapping("/competition/{competitionId}/assessors/invite")
@SecuredBySpring(value = "Controller", description = "TODO", securedType = CompetitionManagementSendInviteController.class)
@PreAuthorize("hasAnyAuthority('comp_admin','project_finance')")
public class CompetitionManagementSendInviteController extends CompetitionManagementCookieController<OverviewSelectionForm> {

    private static final String SELECTION_FORM = "overviewSelectionForm";

    @Autowired
    private CompetitionInviteRestService competitionInviteRestService;

    @Autowired
    private CompetitionRestService competitionRestService;

    @Override
    protected String getCookieName() {
        return SELECTION_FORM;
    }

    @Override
    protected Class<OverviewSelectionForm> getFormType() {
        return OverviewSelectionForm.class;
    }

    @GetMapping("/send")
    public String getInvitesToSend(Model model,
                                   @PathVariable("competitionId") long competitionId,
                                   @ModelAttribute(name = "form", binding = false) SendInviteForm form,
                                   BindingResult bindingResult) {
        AssessorInvitesToSendResource invites = competitionInviteRestService.getAllInvitesToSend(competitionId).getSuccess();
        CompetitionResource competition = competitionRestService.getCompetitionById(competitionId).getSuccess();
        boolean alwaysOpen = competition.isAlwaysOpen();

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
            setInviteSubjectContent(form, invites, alwaysOpen);
        }

        return "assessors/send-invites";
    }

    @PostMapping("/send")
    public String sendInvites(Model model,
                              @PathVariable("competitionId") long competitionId,
                              @ModelAttribute("form") @Valid SendInviteForm form,
                              BindingResult bindingResult,
                              ValidationHandler validationHandler) {
        Supplier<String> failureView = () -> getInvitesToSend(model, competitionId, form, bindingResult);

        return validationHandler.failNowOrSucceedWith(failureView, () -> {
            RestResult<Void> sendResult = competitionInviteRestService.sendAllInvites(
                    competitionId,
                    new AssessorInviteSendResource(form.getSubject(), form.getContent()));

            return validationHandler.addAnyErrors(error(removeDuplicates(sendResult.getErrors())))
                    .failNowOrSucceedWith(failureView, () -> redirectToOverview(competitionId, 0));
        });
    }

    @GetMapping("/reviewResend")
    public String getInvitesToResendFailureView(Model model,
                                     @PathVariable("competitionId") long competitionId,
                                     @ModelAttribute(name = "form", binding = false) ResendInviteForm inviteform,
                                     BindingResult bindingResult,
                                     ValidationHandler validationHandler) {
        if (inviteform.getInviteIds() == null || inviteform.getInviteIds().isEmpty()) {
            return redirectToOverview(competitionId, 0);
        }

        AssessorInvitesToSendResource invites = competitionInviteRestService.getAllInvitesToResend(
                competitionId,
                inviteform.getInviteIds()).getSuccess();
        CompetitionResource competition = competitionRestService.getCompetitionById(competitionId).getSuccess();
        boolean alwaysOpen =  competition.isAlwaysOpen();

        model.addAttribute("model", new SendInvitesViewModel(
                invites.getCompetitionId(),
                invites.getCompetitionName(),
                invites.getRecipients(),
                invites.getContent()
        ));
        setInviteSubjectContent(inviteform, invites, alwaysOpen);
        return "assessors/resend-invites";
    }

    @PostMapping("/reviewResend")
    public String getInvitesToResend(Model model,
                                     @PathVariable("competitionId") long competitionId,
                                     @RequestParam(defaultValue = "0") int page,
                                     @ModelAttribute(SELECTION_FORM) OverviewSelectionForm selectionForm,
                                     @ModelAttribute(name = "form", binding = false) ResendInviteForm inviteform,
                                     ValidationHandler validationHandler,
                                     BindingResult bindingResult,
                                     HttpServletRequest request) {

        OverviewSelectionForm submittedSelectionForm = getSelectionFormFromCookie(request, competitionId)
                .filter(form -> !form.getSelectedInviteIds().isEmpty())
                .orElse(selectionForm);
        Supplier<String> failureView = () -> redirectToOverview(competitionId, page);

        return validationHandler.failNowOrSucceedWith(failureView, () -> {
            AssessorInvitesToSendResource invites = competitionInviteRestService.getAllInvitesToResend(
                    competitionId,
                    submittedSelectionForm.getSelectedInviteIds()).getSuccess();
            CompetitionResource competition = competitionRestService.getCompetitionById(competitionId).getSuccess();
            boolean alwaysOpen = competition.isAlwaysOpen();

            model.addAttribute("model", new SendInvitesViewModel(
                    invites.getCompetitionId(),
                    invites.getCompetitionName(),
                    invites.getRecipients(),
                    invites.getContent()
            ));
            inviteform.setInviteIds(submittedSelectionForm.getSelectedInviteIds());
            setInviteSubjectContent(inviteform, invites, alwaysOpen);
            return "assessors/resend-invites";
        });
    }

    @PostMapping("/resend")
    public String resendInvites(Model model,
                                @PathVariable("competitionId") long competitionId,
                                @ModelAttribute("form") @Valid ResendInviteForm form,
                                BindingResult bindingResult,
                                ValidationHandler validationHandler,
                                HttpServletResponse response){

        Supplier<String> failureView = () -> getInvitesToResendFailureView(model, competitionId, form, bindingResult, validationHandler);

        return validationHandler.failNowOrSucceedWith(failureView, () -> {
            ServiceResult<Void> resendResult = competitionInviteRestService.resendInvites(form.getInviteIds(),
                    new AssessorInviteSendResource(form.getSubject(), form.getContent()))
                    .toServiceResult();
            removeCookie(response, competitionId);
            return validationHandler.addAnyErrors(resendResult, fieldErrorsToFieldErrors(), asGlobalErrors())
                    .failNowOrSucceedWith(failureView, () -> redirectToOverview(competitionId, 0));
        });
    }

    private String redirectToOverview(long competitionId, int page) {
        UriComponentsBuilder builder = UriComponentsBuilder
                .fromPath("/competition/{competitionId}/assessors/pending-and-declined")
                .queryParam("page", page);

        return "redirect:" + builder.buildAndExpand(asMap("competitionId", competitionId))
                .toUriString();
    }

    private void setInviteSubjectContent(SendInviteForm form, AssessorInvitesToSendResource invites, boolean alwaysOpen) {
        if (alwaysOpen) {
            form.setSubject(format("Invitation to be an assessor for competition: '%s'", invites.getCompetitionName()));
        } else {
            form.setSubject(format("Invitation to assess '%s'", invites.getCompetitionName()));
        }
    }

    private String redirectToInviteListView(long competitionId) {
        return format("redirect:/competition/%s/assessors/invite", competitionId);
    }

}
