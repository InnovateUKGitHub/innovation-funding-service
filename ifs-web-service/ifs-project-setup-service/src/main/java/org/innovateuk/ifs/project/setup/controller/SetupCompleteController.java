package org.innovateuk.ifs.project.setup.controller;

import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.project.setup.populator.SetupCompleteViewModelPopulator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/project")
@SecuredBySpring(value = "Controller", description = "Applicants can view the status of their projects",
        securedType = SetupCompleteController.class)
@PreAuthorize("hasAuthority('applicant')")
public class SetupCompleteController {

    @Autowired
    private SetupCompleteViewModelPopulator setupCompleteViewModelPopulator;

    @GetMapping("/{projectId}/setup")
    public String viewProjectSetupComplete(@PathVariable("projectId") long projectId,
                                   Model model) {
        model.addAttribute("model", setupCompleteViewModelPopulator.populate(projectId));
        return "project/project-setup-complete";
    }
}
