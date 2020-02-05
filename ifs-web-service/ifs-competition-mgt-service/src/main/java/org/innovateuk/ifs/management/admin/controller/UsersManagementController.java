package org.innovateuk.ifs.management.admin.controller;

import org.innovateuk.ifs.async.annotations.AsyncMethod;
import org.innovateuk.ifs.async.generation.AsyncAdaptor;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.invite.resource.RoleInvitePageResource;
import org.innovateuk.ifs.invite.service.InviteUserRestService;
import org.innovateuk.ifs.management.admin.form.UserManagementFilterForm;
import org.innovateuk.ifs.management.admin.viewmodel.UserListViewModel;
import org.innovateuk.ifs.pagination.PaginationViewModel;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserPageResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.UserRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.concurrent.CompletableFuture;

import static org.innovateuk.ifs.user.resource.Role.IFS_ADMINISTRATOR;

/**
 * This controller will handle all requests that are related to management of users by IFS Administrators.
 */
@Controller
@RequestMapping("/admin/users")
public class UsersManagementController extends AsyncAdaptor {

    private static final String DEFAULT_PAGE_NUMBER = "1";
    private static final String DEFAULT_PAGE_SIZE = "20";
    private static final String FORM_ATTR_NAME = "form";

    @Autowired
    private UserRestService userRestService;

    @Autowired
    private InviteUserRestService inviteUserRestService;

    @AsyncMethod
    @SecuredBySpring(value = "UserManagementController.viewActive() method",
            description = "Only IFS administrators can view active internal users")
    @PreAuthorize("hasAnyAuthority('ifs_administrator', 'support')")
    @GetMapping("/active")
    public String viewActiveUsers(Model model,
                             UserResource user,
                             @ModelAttribute(FORM_ATTR_NAME) UserManagementFilterForm filterForm,
                             @RequestParam(defaultValue = DEFAULT_PAGE_NUMBER) int page,
                             @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int size) {
        return view(model, "active", filterForm.getFilter(), page, size, user.hasRole(Role.IFS_ADMINISTRATOR));
    }

    @AsyncMethod
    @SecuredBySpring(value = "UserManagementController.viewInactive() method",
            description = "Only IFS administrators can view active internal users")
    @PreAuthorize("hasAnyAuthority('ifs_administrator', 'support')")
    @GetMapping("/inactive")
    public String viewInactiveUsers(Model model,
                               UserResource user,
                               @ModelAttribute(FORM_ATTR_NAME) UserManagementFilterForm filterForm,
                               @RequestParam(defaultValue = DEFAULT_PAGE_NUMBER) int page,
                               @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int size) {
        return view(model, "inactive", filterForm.getFilter(), page, size, user.hasRole(IFS_ADMINISTRATOR));
    }

    @AsyncMethod
    @SecuredBySpring(value = "UserManagementController.viewPending() method",
            description = "IFS administrators can view pending user invites")
    @PreAuthorize("hasAuthority('ifs_administrator')")
    @GetMapping("/pending")
    public String viewPendingUsers(Model model,
                              HttpServletRequest request,
                              UserResource user,
                              @Valid @ModelAttribute(FORM_ATTR_NAME) UserManagementFilterForm filterForm,
                              @RequestParam(defaultValue = DEFAULT_PAGE_NUMBER) int page,
                              @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int size) {
        return view(model, "pending", filterForm.getFilter(), page, size, true);
    }


    @SecuredBySpring(value = "RESEND_INTERNAL_USER_INVITE", description = "Only the IFS Administrators can resend invites to internal users")
    @PreAuthorize("hasAuthority('ifs_administrator')")
    @PostMapping("/pending/resend-invite")
    public String resendInvite(@RequestParam("inviteId") long inviteId) {
        inviteUserRestService.resendInternalUserInvite(inviteId).getSuccess();
        return "redirect:/admin/users/pending";
    }

    private String view(Model model, String activeTab, String filter, int page, int size, boolean adminUser) {
        final CompletableFuture<UserPageResource> activeUsers;
        final CompletableFuture<UserPageResource> inactiveUsers;
        final CompletableFuture<RoleInvitePageResource> pendingUsers;
        if (adminUser) {
            activeUsers = async(() -> userRestService.getActiveUsers(filter, page - 1 , size).getSuccess());
            inactiveUsers = async(() -> userRestService.getInactiveUsers(filter, page - 1, size).getSuccess());
            pendingUsers = async(() -> inviteUserRestService.getPendingInternalUserInvites(filter, page - 1, size).getSuccess());
        }
        else {
            activeUsers = async(() -> userRestService.getActiveExternalUsers(filter, page - 1, size).getSuccess());
            inactiveUsers = async(() -> userRestService.getInactiveExternalUsers(filter, page - 1, size).getSuccess());
            pendingUsers = async(() -> new RoleInvitePageResource());
        }

        awaitAll(activeUsers, inactiveUsers, pendingUsers)
                .thenAccept((activeInternalUsers, inactiveInternalUsers, pendingInternalUserInvites) -> {
                    UserListViewModel viewModel = new UserListViewModel(
                            activeTab,
                            filter,
                            activeInternalUsers.getContent(),
                            inactiveInternalUsers.getContent(),
                            pendingInternalUserInvites.getContent(),
                            activeInternalUsers.getTotalElements(),
                            inactiveInternalUsers.getTotalElements(),
                            pendingInternalUserInvites.getTotalElements(),
                            new PaginationViewModel(activeInternalUsers),
                            new PaginationViewModel(inactiveInternalUsers),
                            new PaginationViewModel(pendingInternalUserInvites),
                            adminUser
                    );
                    model.addAttribute("model", viewModel);
                });

        return "admin/users";
    }
}