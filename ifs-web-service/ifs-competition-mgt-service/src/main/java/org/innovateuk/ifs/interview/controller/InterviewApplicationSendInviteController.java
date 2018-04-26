package org.innovateuk.ifs.interview.controller;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.interview.model.InterviewApplicationsSendModelPopulator;
import org.innovateuk.ifs.interview.service.InterviewAssignmentRestService;
import org.innovateuk.ifs.interview.viewmodel.InterviewAssignmentApplicationsSendViewModel;
import org.innovateuk.ifs.invite.resource.AssessorInviteSendResource;
import org.innovateuk.ifs.management.form.SendInviteForm;
import org.innovateuk.ifs.management.service.CompetitionManagementApplicationServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import java.util.function.Supplier;

import static java.lang.String.format;
import static org.innovateuk.ifs.commons.rest.RestFailure.error;
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
                                   @ModelAttribute(name = "form", binding = false) SendInviteForm form,
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
                              @ModelAttribute("form") @Valid SendInviteForm form,
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

    private String redirectToStatusTab(long competitionId) {
        return format("redirect:/assessment/interview/competition/%s/applications/view-status", competitionId);
    }

    private void populateGroupInviteFormWithExistingValues(SendInviteForm form) {
        form.setSubject("Please attend an interview for an Innovate UK funding competition");
    }


}
