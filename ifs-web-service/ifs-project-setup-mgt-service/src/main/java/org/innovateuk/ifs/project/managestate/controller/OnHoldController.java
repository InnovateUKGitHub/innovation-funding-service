package org.innovateuk.ifs.project.managestate.controller;


import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.project.managestate.viewmodel.OnHoldViewModel;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.service.ProjectRestService;
import org.innovateuk.ifs.project.service.ProjectStateRestService;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import static java.lang.String.format;
import static org.innovateuk.ifs.project.resource.ProjectState.ON_HOLD;

@Controller
@RequestMapping("/competition/{competitionId}/project/{projectId}/on-hold-status")
@PreAuthorize("hasAuthority('project_finance')")
@SecuredBySpring(value = "MANAGE_PROJECT_STATE", description = "Only project finance users can manage project on hold state")
public class OnHoldController {

    @Autowired
    private ProjectRestService projectRestService;

    @Autowired
    private ProjectStateRestService projectStateRestService;

    @GetMapping
    public String viewOnHoldStatus(@PathVariable long projectId,
                                   @PathVariable long competitionId,
                                   Model model) {
        ProjectResource project = projectRestService.getProjectById(projectId).getSuccess();

        if (project.getProjectState() != ON_HOLD) {
            return redirectToManagePage(projectId, competitionId);
        }
        model.addAttribute("model",
                new OnHoldViewModel(project));
        return "project/on-hold-status";
    }

    @PostMapping
    public String resumeProject(@PathVariable long projectId,
                                @PathVariable long competitionId,
                                UserResource user,
                                RedirectAttributes redirectAttributes) {
        projectStateRestService.resumeProject(projectId).getSuccess();
        redirectAttributes.addFlashAttribute("resumedFromOnHold", true);
        return user.hasRole(Role.IFS_ADMINISTRATOR)
                ? redirectToManagePage(projectId, competitionId)
                : redirectToProjectDetails(projectId, competitionId);
    }

    private String redirectToManagePage(long projectId,
                                        long competitionId) {
        return format("redirect:/competition/%d/project/%d/manage-status", competitionId, projectId);
    }

    private String redirectToProjectDetails(long projectId,
                                        long competitionId) {
        return format("redirect:/competition/%d/project/%d/details", competitionId, projectId);
    }
}
