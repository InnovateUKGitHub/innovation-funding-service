package org.innovateuk.ifs.project.status.controller;

import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.project.status.populator.SetupStatusViewModelPopulator;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.NativeWebRequest;

import javax.servlet.http.HttpServletRequest;

/**
 * This controller will handle all requests that are related to a project.
 */
@Controller
@RequestMapping("/project")
@SecuredBySpring(value = "Controller", description = "TODO", securedType = SetupStatusController.class)
@PreAuthorize("hasAuthority('applicant')")
public class SetupStatusController {

    @Autowired
    private SetupStatusViewModelPopulator setupStatusViewModelPopulator;

    public static final String PROJECT_SETUP_PAGE = "project/setup-status";

    @GetMapping("/{projectId}")
    public String viewProjectSetupStatus(Model model, @PathVariable("projectId") final Long projectId,
                                         UserResource loggedInUser,
                                         NativeWebRequest springRequest) {

        HttpServletRequest request = springRequest.getNativeRequest(HttpServletRequest.class);
        String dashboardUrl = request.getScheme() + "://" +
            request.getServerName() +
            ":" + request.getServerPort() +
            "/applicant/dashboard";

        model.addAttribute("model", setupStatusViewModelPopulator.populateViewModel(projectId, loggedInUser));
        model.addAttribute("url", dashboardUrl);
        return PROJECT_SETUP_PAGE;
    }
}
