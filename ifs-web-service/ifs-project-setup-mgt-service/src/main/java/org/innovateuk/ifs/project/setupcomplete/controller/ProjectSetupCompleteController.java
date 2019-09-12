package org.innovateuk.ifs.project.setupcomplete.controller;

import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.service.ProjectRestService;
import org.innovateuk.ifs.project.setupcomplete.form.ProjectSetupCompleteForm;
import org.innovateuk.ifs.project.setupcomplete.viewmodel.ProjectSetupCompleteViewModel;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/competition/{competitionId}/project/{projectId}/setup-complete")
public class ProjectSetupCompleteController {

    @Autowired
    private ProjectRestService projectRestService;

    @PreAuthorize("hasAuthority('project_finance')")
    @SecuredBySpring(value = "PROJECT_SETUP_COMPLETE", description = "Project finance, comp admin, support, innovation lead and stakeholders can view the project team page")
    @GetMapping
    public String viewSetupCompletePage(@ModelAttribute(name = "form", binding = false) ProjectSetupCompleteForm form,
                                        @PathVariable long projectId,
                                        @PathVariable long competitionId,
                                        Model model,
                                        UserResource user) {
        ProjectResource project = projectRestService.getProjectById(projectId).getSuccess();
        model.addAttribute("model", new ProjectSetupCompleteViewModel(project));
        return "project/setup-complete";
    }
}
