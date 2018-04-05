package org.innovateuk.ifs.interview.controller;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.interview.form.InterviewOverviewSelectionForm;
import org.innovateuk.ifs.interview.service.InterviewInviteRestService;
import org.innovateuk.ifs.invite.resource.AssessorInviteSendResource;
import org.innovateuk.ifs.invite.resource.AssessorInvitesToSendResource;
import org.innovateuk.ifs.management.controller.CompetitionManagementCookieController;
import org.innovateuk.ifs.management.form.SendInviteForm;
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
import static org.innovateuk.ifs.commons.rest.RestFailure.error;
import static org.innovateuk.ifs.util.CollectionFunctions.removeDuplicates;

/**
 * This controller will handle all Competition Management requests related to sending interview panel invites to assessors
 */
@Controller
@RequestMapping("/assessment/interview/competition/{competitionId}/assessors/invite")
@SecuredBySpring(value = "Controller", description = "Comp Admins and Project Finance users can invite assessors to an Interview Panel", securedType = InterviewAssessorSendInviteController.class)
@PreAuthorize("hasAnyAuthority('comp_admin','project_finance')")
public class InterviewAssessorSendInviteController extends CompetitionManagementCookieController<InterviewOverviewSelectionForm> {

    private static final String SELECTION_FORM = "assessmentInterviewPanelOverviewSelectionForm";

    @Autowired
    private InterviewInviteRestService interviewInviteRestService;

    @Override
    protected String getCookieName() {
        return SELECTION_FORM;
    }

    @Override
    protected Class<InterviewOverviewSelectionForm> getFormType() {
        return InterviewOverviewSelectionForm.class;
    }

    @GetMapping("/send")
    public String getInvitesToSend(Model model,
                                   @PathVariable("competitionId") long competitionId,
                                   @ModelAttribute(name = "form", binding = false) SendInviteForm form,
                                   BindingResult bindingResult) {
        AssessorInvitesToSendResource invites = interviewInviteRestService.getAllInvitesToSend(competitionId).getSuccess();

        if (invites.getRecipients().isEmpty()) {
            return redirectToInterviewPanelFindTab(competitionId);
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

        return "assessors/interview/assessor-send-invites";
    }

    @PostMapping("/send")
    public String sendInvites(Model model,
                              @PathVariable("competitionId") long competitionId,
                              @ModelAttribute("form") @Valid SendInviteForm form,
                              BindingResult bindingResult,
                              ValidationHandler validationHandler) {
        Supplier<String> failureView = () -> getInvitesToSend(model, competitionId, form, bindingResult);

        return validationHandler.failNowOrSucceedWith(failureView, () -> {
            RestResult<Void> sendResult = interviewInviteRestService
                    .sendAllInvites(competitionId, new AssessorInviteSendResource(form.getSubject(), form.getContent()));

            return validationHandler.addAnyErrors(error(removeDuplicates(sendResult.getErrors())))
                    .failNowOrSucceedWith(failureView, () -> redirectToPanelOverviewTab(competitionId));
        });
    }

    private void populateGroupInviteFormWithExistingValues(SendInviteForm form, AssessorInvitesToSendResource assessorInviteToSendResource) {
        form.setSubject(format("Invitation to Innovate UK interview panel for '%s'", assessorInviteToSendResource.getCompetitionName()));
    }

    private String redirectToInterviewPanelFindTab(long competitionId) {
        return format("redirect:/assessment/interview/competition/%s/assessors/find", competitionId);
    }

    private String redirectToPanelOverviewTab(long competitionId) {
        return format("redirect:/assessment/interview/competition/%s/assessors/pending-and-declined", competitionId);
    }
}
