package org.innovateuk.ifs.management.admin.controller;

import org.innovateuk.ifs.assessment.service.AssessorRestService;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.management.admin.viewmodel.RoleProfileViewModel;
import org.innovateuk.ifs.user.resource.RoleProfileStatusResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.RoleProfileStatusRestService;
import org.innovateuk.ifs.user.service.UserRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import static org.innovateuk.ifs.user.resource.ProfileRole.ASSESSOR;

@Controller
@RequestMapping("/admin/user/{userId}/role-profile")
@SecuredBySpring(value = "Controller", description = "Project finance, competition admin, support, innovation lead " +
        "can view assessors role profile details",
        securedType = AssessorManagementController.class)
@PreAuthorize("hasAnyAuthority('project_finance','comp_admin', 'support')")
public class AssessorManagementController {

    @Autowired
    private RoleProfileStatusRestService roleProfileStatusRestService;

    @Autowired
    private UserRestService userRestService;

    @Autowired
    private AssessorRestService assessorRestService;

    @GetMapping
    public String viewUser(@PathVariable long userId,
                           Model model) {

        RoleProfileStatusResource roleProfileStatusResource =
                roleProfileStatusRestService.findByUserIdAndProfileRole(userId, ASSESSOR).getSuccess();

        UserResource modifiedUser = userRestService.retrieveUserById(roleProfileStatusResource.getModifiedBy()).getSuccess();

        model.addAttribute("model", new RoleProfileViewModel(roleProfileStatusResource, modifiedUser, hasApplicationsAssigned(userId)));

        return "admin/role-profile-details";

    }

    private boolean hasApplicationsAssigned(long userId) {
        return assessorRestService.hasApplicationsAssigned(userId).getSuccess();
    }
}
