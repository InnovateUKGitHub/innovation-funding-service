package org.innovateuk.ifs.project.projectteam.controller;


import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.project.projectteam.form.ProjectTeamForm;
import org.innovateuk.ifs.project.projectteam.populator.ProjectTeamViewModelPopulator;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.function.Supplier;

/**
 * This controller will handle all requests that are related to the project team.
 */

@Controller
@RequestMapping("/project")
public class ProjectTeamController {

    private final ProjectTeamViewModelPopulator projectTeamPopulator;

    @Autowired
    public ProjectTeamController(ProjectTeamViewModelPopulator projectTeamPopulator) {
        this.projectTeamPopulator = projectTeamPopulator;
    }

    @PreAuthorize("hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.ProjectCompositeId', 'ACCESS_PROJECT_DETAILS_SECTION')")
    @GetMapping("/{projectId}/team")
    public String viewProjectTeam(@ModelAttribute(value = "form", binding = false) ProjectTeamForm form,
                                  BindingResult bindingResult,
                                  @PathVariable("projectId") final long projectId,
                                  Model model,
                                  UserResource loggedInUser) {
        model.addAttribute("model", projectTeamPopulator.populate(projectId, loggedInUser));
        return "project/project-team";
    }

    @PostMapping(value = "/{projectId}/team", params = "add-team-member")
    public String addTeamMember(@ModelAttribute(value = "form") ProjectTeamForm form,
                                BindingResult bindingResult,
                                @PathVariable("projectId") final long projectId,
                                @RequestParam("add-team-member") final long organisationId,
                                Model model,
                                UserResource loggedInUser) {
        model.addAttribute("model", projectTeamPopulator.populate(projectId, loggedInUser)
                .addTeamMember(organisationId));
        return "project/project-team";
    }

    @PostMapping(value = "/{projectId}/team", params = "invite-to-project")
    public String inviteToProject(@Valid @ModelAttribute("form") ProjectTeamForm form,
                                  BindingResult bindingResult,
                                  ValidationHandler validationHandler,
                                  @PathVariable("projectId") final long projectId,
                                  @RequestParam("invite-to-project") final long organisationId,
                                  Model model,
                                  UserResource loggedInUser) {
        Supplier<String> failureView = () -> {
            model.addAttribute("model", projectTeamPopulator.populate(projectId, loggedInUser)
                    .addTeamMember(organisationId));
            return "project/project-team";
        };

        return validationHandler.failNowOrSucceedWith(failureView, () -> String.format("redirect:/project/%d/team", projectId));
    }


}
