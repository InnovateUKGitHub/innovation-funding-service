package org.innovateuk.ifs.project.manage.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/competition/{competitionId}/project/{projectId}/manage-status")
public class ManageProjectStatusController {

    @GetMapping()
    public String manageProjectStatus(@PathVariable Long projectId, Model model) {
        return "project/manage-project-status";
    }
}
