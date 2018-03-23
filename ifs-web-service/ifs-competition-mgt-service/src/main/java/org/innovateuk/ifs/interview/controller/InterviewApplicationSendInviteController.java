package org.innovateuk.ifs.interview.controller;

import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.interview.model.InterviewAssignmentApplicationsSendModelPopulator;
import org.innovateuk.ifs.interview.viewmodel.InterviewAssignmentApplicationsSendViewModel;
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

import static org.innovateuk.ifs.util.BackLinkUtil.buildOriginQueryString;

/**
 * This controller will handle all Competition Management requests related to sending interview panel invites to assessors
 */
@Controller
@RequestMapping("/assessment/interview/competition/{competitionId}/applications/invite")
@SecuredBySpring(value = "Controller", description = "Comp Admins and Project Finance users can invite applications to an Interview Panel", securedType = InterviewApplicationSendInviteController.class)
@PreAuthorize("hasAnyAuthority('comp_admin','project_finance')")
public class InterviewApplicationSendInviteController {

    @Autowired
    private InterviewAssignmentApplicationsSendModelPopulator interviewAssignmentApplicationsSendModelPopulator;

    @GetMapping("/send")
    public String getInvitesToSend(Model model,
                                   @PathVariable("competitionId") long competitionId,
                                   @RequestParam(defaultValue = "0") int page,
                                   @RequestParam MultiValueMap<String, String> queryParams,
                                   @ModelAttribute(name = "form", binding = false) SendInviteForm form,
                                   BindingResult bindingResult) {

        String originQuery = buildOriginQueryString(CompetitionManagementApplicationServiceImpl.ApplicationOverviewOrigin.INTERVIEW_PANEL_SEND, queryParams);
        InterviewAssignmentApplicationsSendViewModel viewModel = interviewAssignmentApplicationsSendModelPopulator.populateModel(competitionId, page, originQuery);

        model.addAttribute("model", viewModel);

        if (!bindingResult.hasErrors()) {
            populateGroupInviteFormWithExistingValues(form);
        }

        return "assessors/interview/application-send-invites";
    }

    @PostMapping("/send")
    public String sendInvites(Model model,
                              @PathVariable("competitionId") long competitionId,
                              @ModelAttribute("form") @Valid SendInviteForm form,
                              BindingResult bindingResult,
                              ValidationHandler validationHandler) {


        return "redirect:/";
    }

    private void populateGroupInviteFormWithExistingValues(SendInviteForm form) {
        form.setSubject("Please attend an interview for an Innovate UK funding competition");
    }


}
