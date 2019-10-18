package org.innovateuk.ifs.project.projectteam.controller;


import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.project.invite.resource.ProjectPartnerInviteResource;
import org.innovateuk.ifs.project.invite.service.ProjectPartnerInviteRestService;
import org.innovateuk.ifs.project.projectteam.form.ProjectPartnerInviteForm;
import org.innovateuk.ifs.project.projectteam.viewmodel.ProjectPartnerInviteViewModel;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.service.ProjectRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.function.Supplier;

@Controller
@RequestMapping("/competition/{competitionId}/project/{projectId}/team/partner")
@PreAuthorize("hasAuthority('project_finance')")
@SecuredBySpring(value = "VIEW_PROJECT_TEAM", description = "Project finance users can invite new project team partners.")
public class ProjectPartnerController {

    @Autowired
    private ProjectRestService projectRestService;

    @Autowired
    private ProjectPartnerInviteRestService projectPartnerInviteRestService;

    @GetMapping
    public String inviteProjectPartnerForm(@ModelAttribute(value = "form", binding = false) ProjectPartnerInviteForm form,
                                           BindingResult bindingResult,
                                           @PathVariable long projectId,
                                           Model model) {
        ProjectResource project = projectRestService.getProjectById(projectId).getSuccess();
        model.addAttribute("model", new ProjectPartnerInviteViewModel(project));
        return "project/project-partner-invite";
    }

    @PostMapping
    public String inviteProjectPartner(@Valid @ModelAttribute(value = "form") ProjectPartnerInviteForm form,
                                       BindingResult bindingResult,
                                       ValidationHandler validationHandler,
                                       @PathVariable long competitionId,
                                       @PathVariable long projectId,
                                       Model model) {
        Supplier<String> success = () -> String.format("redirect:/competition/%d/project/%d/team", competitionId, projectId);
        Supplier<String> failure = () -> inviteProjectPartnerForm(form, bindingResult, projectId, model);

        return validationHandler.failNowOrSucceedWith(failure, () -> {
            validationHandler.addAnyErrors(
                    projectPartnerInviteRestService.invitePartnerOrganisation(projectId, new ProjectPartnerInviteResource(form.getOrganisationName(), form.getUserName(), form.getEmail())));
            return validationHandler.failNowOrSucceedWith(failure, success);
        });
    }
}
