package org.innovateuk.ifs.admin.controller;

import org.innovateuk.ifs.admin.viewmodel.EditUserViewModel;
import org.innovateuk.ifs.admin.viewmodel.UserListViewModel;
import org.innovateuk.ifs.profile.service.ProfileRestService;
import org.innovateuk.ifs.user.service.UserRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * This controller will handle all requests that are related to management of users by IFS Administrators.
 */
@Controller
@RequestMapping("/admin")
@PreAuthorize("hasAnyAuthority('ifs_administrator')")
public class UserManagementController {
    @Autowired
    private UserRestService userRestService;

    @Autowired
    private ProfileRestService profileRestService;

    @GetMapping("/users")
    public String view(Model model) {
        return userRestService.getInternalUsers().andOnSuccessReturn(internalUsers -> {
            model.addAttribute("model", new UserListViewModel(internalUsers));
            return "admin/users";
        }).getSuccessObjectOrThrowException();
    }

    @GetMapping("/user/{userId}")
    public String viewUser(@PathVariable Long userId, Model model){
        return userRestService.retrieveUserById(userId).andOnSuccess( user ->
                profileRestService.getUserProfile(userId).andOnSuccessReturn(profile -> {
                    model.addAttribute("model", new EditUserViewModel(profile.getCreatedBy(), profile.getCreatedOn(), user));
                    return "admin/user";
                })).getSuccessObjectOrThrowException();
    }
}