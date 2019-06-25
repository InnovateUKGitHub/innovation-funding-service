package org.innovateuk.ifs.project.managestate.controller;


import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/competition/{competitionId}/project/{projectId}/on-hold-status")
@PreAuthorize("hasAuthority('project_finance')")
@SecuredBySpring(value = "MANAGE_PROJECT_STATE", description = "Only project finance users can manage project on hold state")
public class OnHoldController {


    @GetMapping
    public String viewOnHoldStatus() {
        return "project/on-hold-status";
    }
}
