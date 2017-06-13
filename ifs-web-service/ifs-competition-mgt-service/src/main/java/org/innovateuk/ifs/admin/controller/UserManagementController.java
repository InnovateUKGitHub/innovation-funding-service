package org.innovateuk.ifs.admin.controller;

import org.innovateuk.ifs.admin.viewmodel.EditUserViewModel;
import org.innovateuk.ifs.admin.viewmodel.UserListViewModel;
import org.innovateuk.ifs.management.viewmodel.PaginationViewModel;
import org.innovateuk.ifs.profile.service.ProfileRestService;
import org.innovateuk.ifs.user.service.UserRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * This controller will handle all requests that are related to management of users by IFS Administrators.
 */
@Controller
@RequestMapping("/admin")
@PreAuthorize("hasAnyAuthority('ifs_administrator')")
public class UserManagementController {
    private static final int PAGE_SIZE = 5;
    @Autowired
    private UserRestService userRestService;

    @Autowired
    private ProfileRestService profileRestService;

    @GetMapping("/users/active")
    public String viewActive(Model model,
                       @RequestParam(value = "page", defaultValue = "0") int page) {
        return view(model, "active", page);
    }

    @GetMapping("/users/inactive")
    public String viewInactive(Model model,
                             @RequestParam(value = "page", defaultValue = "0") int page) {
        return view(model, "inactive", page);
    }

    private String view(Model model, String activeTab, int page){
        return userRestService.getActiveInternalUsers(page, PAGE_SIZE).andOnSuccessReturn(activeInternalUsers -> userRestService.getInactiveInternalUsers(page, PAGE_SIZE).andOnSuccessReturn(inactiveInternalUsers -> {
            model.addAttribute("model",
                    new UserListViewModel(
                            activeTab,
                            activeInternalUsers.getContent(),
                            inactiveInternalUsers.getContent(),
                            userRestService.countActiveInternalUsers().getOrElse(0L),
                            userRestService.countInactiveInternalUsers().getOrElse(0L),
                            new PaginationViewModel(activeInternalUsers, "active") ,
                            new PaginationViewModel(inactiveInternalUsers, "inactive")));
            return "admin/users";
        }).getSuccessObjectOrThrowException()).getSuccessObjectOrThrowException();
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