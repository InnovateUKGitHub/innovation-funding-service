package org.innovateuk.ifs.project.projectteam.controller;


import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.project.invite.resource.ProjectPartnerInviteResource;
import org.innovateuk.ifs.project.invite.service.ProjectPartnerInviteRestService;
import org.innovateuk.ifs.project.projectteam.form.ProjectPartnerInviteForm;
import org.innovateuk.ifs.project.projectteam.viewmodel.ProjectPartnerInviteViewModel;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.service.ProjectRestService;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/competition/{competitionId}/project/{projectId}/team/partner")
@PreAuthorize("hasAnyAuthority('project_finance', 'comp_admin')")
@SecuredBySpring(value = "VIEW_PROJECT_TEAM", description = "Project finance, comp admin, support, innovation lead and stakeholders can view the project team page")
public class ProjectPartnerController {

    @Autowired
    private ProjectRestService projectRestService;

    @Autowired
    private ProjectPartnerInviteRestService projectPartnerInviteRestService;

    @GetMapping
    public String inviteProjectPartnerForm(@ModelAttribute(value = "form", binding = false) ProjectPartnerInviteForm form,
                                           BindingResult bindingResult,
                                           @PathVariable long projectId,
                                           Model model,
                                           UserResource loggedInUser) {
        ProjectResource project = projectRestService.getProjectById(projectId).getSuccess();
        model.addAttribute("model", new ProjectPartnerInviteViewModel(project));
        return "project/project-partner-invite";
    }

    @PostMapping
    public String inviteProjectPartner(@ModelAttribute(value = "form") ProjectPartnerInviteForm form,
                                       BindingResult bindingResult,
                                       @PathVariable long competitionId,
                                       @PathVariable long projectId,
                                       Model model,
                                       UserResource loggedInUser) {
        projectPartnerInviteRestService.invitePartnerOrganisation(projectId, new ProjectPartnerInviteResource(form.getOrganisationName(), form.getUserName(), form.getEmail())).getSuccess();
        return String.format("redirect/competition/%d/project/%d/team", competitionId, projectId);
    }
}
