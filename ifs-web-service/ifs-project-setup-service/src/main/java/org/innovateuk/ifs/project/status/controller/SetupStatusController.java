package org.innovateuk.ifs.project.status.controller;

import org.innovateuk.ifs.origin.ApplicationSummaryOrigin;
import org.innovateuk.ifs.async.annotations.AsyncMethod;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.project.status.populator.SetupStatusViewModelPopulator;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.util.RedirectUtils;
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

import static org.innovateuk.ifs.origin.BackLinkUtil.buildOriginQueryString;

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

    @PreAuthorize("hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.ProjectCompositeId', 'ACCESS_PROJECT_SETUP_STATUS')")
    @GetMapping("/{projectId}")
    @AsyncMethod
    public String viewProjectSetupStatus(@PathVariable("projectId") long projectId,
                                         Model model,
                                         UserResource loggedInUser,
                                         HttpServletRequest request,
                                         @RequestParam MultiValueMap<String, String> queryParams) {
        queryParams.add("projectId", String.valueOf(projectId));
        String originQuery = buildOriginQueryString(ApplicationSummaryOrigin.SET_UP_YOUR_PROJECT , queryParams);

        model.addAttribute("model", setupStatusViewModelPopulator.populateViewModel(projectId, loggedInUser, originQuery));
        model.addAttribute("url", RedirectUtils.redirectToApplicationService(request, "applicant/dashboard"));

        return "project/setup-status";
    }
}
