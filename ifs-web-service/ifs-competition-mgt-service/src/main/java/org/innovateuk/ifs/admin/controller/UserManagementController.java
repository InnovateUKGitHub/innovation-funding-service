package org.innovateuk.ifs.admin.controller;

import org.innovateuk.ifs.admin.viewmodel.UserListViewModel;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Set;

/**
 * This controller will handle all requests that are related to management of users by IFS Administrators.
 */
@Controller
@RequestMapping("/admin/users")
@PreAuthorize("hasAnyAuthority('ifs_administrator')")
public class UserManagementController {
    @Autowired
    private UserService userService;

    @GetMapping
    public String view(Model model) {
        Set<UserResource> internalUsers = userService.getInternalUsers();
        model.addAttribute("model", new UserListViewModel(internalUsers));
        return "admin/users";
    }
}
