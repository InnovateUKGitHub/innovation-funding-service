package org.innovateuk.ifs.project.setup.controller;

import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.project.setup.populator.SetupViewModelPopulator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/project")
@SecuredBySpring(value = "Controller", description = "TODO", securedType = SetupController.class)
@PreAuthorize("hasAnyAuthority('applicant', 'monitoring_officer')")
public class SetupController {

    @Autowired
    private SetupViewModelPopulator setupViewModelPopulator;

    @GetMapping("/{projectId}/setup")
    public String viewProjectSetup(@PathVariable("projectId") long projectId,
                                   Model model) {
        model.addAttribute("model", setupViewModelPopulator.populate(projectId));
        return "project/setup";
    }
}
