package org.innovateuk.ifs.project.status.controller;

import org.innovateuk.ifs.project.status.populator.ProjectSetupStatusViewModelPopulator;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.NativeWebRequest;

import javax.servlet.http.HttpServletRequest;

/**
 * This controller will handle all requests that are related to a project.
 */
@Controller
@RequestMapping("/project")
@PreAuthorize("hasAuthority('applicant')")
public class ProjectSetupStatusController {

    @Autowired
    private ProjectSetupStatusViewModelPopulator projectSetupStatusViewModelPopulator;

    public static final String PROJECT_SETUP_PAGE = "project/setup-status";

    @GetMapping("/{projectId}")
    public String viewProjectSetupStatus(Model model, @PathVariable("projectId") final Long projectId,
                                         @ModelAttribute("loggedInUser") UserResource loggedInUser,
                                         NativeWebRequest springRequest) {

        HttpServletRequest request = springRequest.getNativeRequest(HttpServletRequest.class);
        String dashboardUrl = request.getScheme() + "://" +
            request.getServerName() +
            ":" + request.getServerPort() +
            "/applicant/dashboard";

        model.addAttribute("model", projectSetupStatusViewModelPopulator.populateViewModel(projectId, loggedInUser));
        model.addAttribute("url", dashboardUrl);
        return PROJECT_SETUP_PAGE;
    }
}
