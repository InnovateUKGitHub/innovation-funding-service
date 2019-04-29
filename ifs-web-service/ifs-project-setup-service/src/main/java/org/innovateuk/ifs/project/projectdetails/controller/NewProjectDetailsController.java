package org.innovateuk.ifs.project.projectdetails.controller;

import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/project")
public class NewProjectDetailsController {

    @GetMapping("/{projectId}/new-details")
    public String viewProjectDetails(@PathVariable("projectId") final Long projectId, Model model,
                                     UserResource loggedInUser) {
        return "project/new-details";
    }
}
