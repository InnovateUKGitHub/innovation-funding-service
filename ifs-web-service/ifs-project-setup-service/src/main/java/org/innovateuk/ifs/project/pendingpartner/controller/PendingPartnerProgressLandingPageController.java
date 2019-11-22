package org.innovateuk.ifs.project.pendingpartner.controller;


import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.project.pendingpartner.populator.PendingPartnerProgressLandingPageViewModelPopulator;
import org.innovateuk.ifs.project.projectteam.PendingPartnerProgressRestService;
import org.innovateuk.ifs.project.status.controller.SetupStatusController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/project/{projectId}/organisation/{organisationId}/pending-partner-progress")
@SecuredBySpring(value = "Controller", description = "TODO", securedType = SetupStatusController.class)
@PreAuthorize("hasAnyAuthority('applicant')")
public class PendingPartnerProgressLandingPageController {

    @Autowired
    private PendingPartnerProgressRestService pendingPartnerProgressRestService;


    @Autowired
    private PendingPartnerProgressLandingPageViewModelPopulator populator;

    @GetMapping
    public String progressLandingPage(@PathVariable long projectId, @PathVariable long organisationId, Model model) {
        model.addAttribute("model", populator.populate(projectId, organisationId));
        return "project/pending-partner-progress/landing-page";
    }

    @PostMapping()
    public String redirectToJoinProjectConfirm(@PathVariable long projectId, @PathVariable long organisationId){
        return "redirect:/project/" + projectId + "/organisation/" + organisationId+ "/pending-partner-progress/join-project-confirm-submit";
    }

    @GetMapping("/join-project-confirm-submit")
    public String joinProjectConfirm(@PathVariable long projectId, @PathVariable long organisationId, Model model){
        model.addAttribute("projectId", projectId);
        model.addAttribute("organisationId", organisationId);
        return "project/pending-partner-progress/join-project-confirm-submit";
    }

    @PostMapping("/join-project-confirm-submit")
    public String joinProject(@PathVariable long projectId, @PathVariable long organisationId){
//        pendingPartnerProgressRestService.completePartnerSetup(projectId, organisationId);
        return "redirect:/TODO";
    }
}
