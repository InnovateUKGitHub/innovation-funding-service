package org.innovateuk.ifs.project.status.controller;

import org.innovateuk.ifs.async.annotations.AsyncMethod;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.project.status.populator.SetupStatusViewModelPopulator;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

/**
 * This controller will handle all requests that are related to a project.
 */
@Controller
@RequestMapping("/project")
@SecuredBySpring(value = "Controller", description = "TODO", securedType = SetupStatusController.class)
@PreAuthorize("hasAnyAuthority('applicant', 'monitoring_officer', 'knowledge_transfer_adviser')")
public class SetupStatusController {

    @Autowired
    private SetupStatusViewModelPopulator setupStatusViewModelPopulator;

    @GetMapping("/{projectId}")
    @AsyncMethod
    public String viewProjectSetupStatus(@PathVariable("projectId") long projectId,
                                         Model model,
                                         UserResource loggedInUser,
                                         HttpServletRequest request,
                                         @RequestParam MultiValueMap<String, String> queryParams) {
        queryParams.add("projectId", String.valueOf(projectId));
        model.addAttribute("model", setupStatusViewModelPopulator.populateViewModel(projectId, loggedInUser));
        return "project/setup-status";
    }
}
