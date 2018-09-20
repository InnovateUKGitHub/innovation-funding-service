package org.innovateuk.ifs.admin.controller;

import org.innovateuk.ifs.admin.form.EditUserForm;
import org.innovateuk.ifs.admin.form.SearchExternalUsersForm;
import org.innovateuk.ifs.admin.viewmodel.EditUserViewModel;
import org.innovateuk.ifs.admin.viewmodel.UserListViewModel;
import org.innovateuk.ifs.async.annotations.AsyncMethod;
import org.innovateuk.ifs.async.generation.AsyncAdaptor;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.invite.resource.EditUserResource;
import org.innovateuk.ifs.invite.resource.ExternalInviteResource;
import org.innovateuk.ifs.invite.resource.RoleInvitePageResource;
import org.innovateuk.ifs.invite.service.InviteUserRestService;
import org.innovateuk.ifs.management.navigation.Pagination;
import org.innovateuk.ifs.registration.service.InternalUserService;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserOrganisationResource;
import org.innovateuk.ifs.user.resource.UserPageResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.UserRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.method.P;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.controller.ErrorToObjectErrorConverterFactory.asGlobalErrors;
import static org.innovateuk.ifs.controller.ErrorToObjectErrorConverterFactory.fieldErrorsToFieldErrors;

/**
 * This controller will handle all requests that are related to management of users by IFS Administrators.
 */
@Controller
@RequestMapping("/admin")
public class UserManagementController extends AsyncAdaptor {

    private static final String DEFAULT_PAGE_NUMBER = "0";

    private static final String DEFAULT_PAGE_SIZE = "40";

    private static final String FORM_ATTR_NAME = "form";

    private static final String SEARCH_PAGE_TEMPLATE = "admin/search-external-users";

    @Autowired
    private UserRestService userRestService;

    @Autowired
    private InviteUserRestService inviteUserRestService;

    @Autowired
    private InternalUserService internalUserService;

    @AsyncMethod
    @SecuredBySpring(value = "UserManagementController.viewActive() method",
            description = "Only IFS administrators can view active internal users")
    @PreAuthorize("hasAuthority('ifs_administrator')")
    @GetMapping("/users/active")
    public String viewActive(Model model,
                             HttpServletRequest request,
                             @RequestParam(value = "page", defaultValue = DEFAULT_PAGE_NUMBER) int page,
                             @RequestParam(value = "size", defaultValue = DEFAULT_PAGE_SIZE) int size) {
        return view(model, "active", page, size, Objects.toString(request.getQueryString(), ""));
    }

    @AsyncMethod
    @SecuredBySpring(value = "UserManagementController.viewInactive() method",
            description = "Only IFS administrators can view active internal users")
    @PreAuthorize("hasAuthority('ifs_administrator')")
    @GetMapping("/users/inactive")
    public String viewInactive(Model model,
                               HttpServletRequest request,
                               @RequestParam(value = "page", defaultValue = DEFAULT_PAGE_NUMBER) int page,
                               @RequestParam(value = "size", defaultValue = DEFAULT_PAGE_SIZE) int size) {
        return view(model, "inactive", page, size, Objects.toString(request.getQueryString(), ""));
    }

    @AsyncMethod
    @SecuredBySpring(value = "UserManagementController.viewPending() method",
            description = "Only IFS administrators can view pending internal user invites")
    @PreAuthorize("hasAuthority('ifs_administrator')")
    @GetMapping("/users/pending")
    public String viewPending(Model model,
                               HttpServletRequest request,
                               @RequestParam(value = "page", defaultValue = DEFAULT_PAGE_NUMBER) int page,
                               @RequestParam(value = "size", defaultValue = DEFAULT_PAGE_SIZE) int size) {
        return view(model, "pending", page, size, Objects.toString(request.getQueryString(), ""));
    }

    private String view(Model model, String activeTab, int page, int size, String existingQueryString){

        CompletableFuture<UserPageResource> activeUsers = async(() ->
                userRestService.getActiveInternalUsers(page, size).getSuccess());

        CompletableFuture<UserPageResource> inactiveUsers = async(() ->
                userRestService.getInactiveInternalUsers(page, size).getSuccess());

        CompletableFuture<RoleInvitePageResource> pendingUsers = async(() ->
                inviteUserRestService.getPendingInternalUserInvites(page, size).getSuccess());

        awaitAll(activeUsers, inactiveUsers, pendingUsers).thenAccept(
                (activeInternalUsers, inactiveInternalUsers, pendingInternalUserInvites) -> {

            UserListViewModel viewModel = new UserListViewModel(
                    activeTab,
                    activeInternalUsers.getContent(),
                    inactiveInternalUsers.getContent(),
                    pendingInternalUserInvites.getContent(),
                    activeInternalUsers.getTotalElements(),
                    inactiveInternalUsers.getTotalElements(),
                    pendingInternalUserInvites.getTotalElements(),
                    new Pagination(activeInternalUsers, "active?" + existingQueryString),
                    new Pagination(inactiveInternalUsers, "inactive?" + existingQueryString),
                    new Pagination(pendingInternalUserInvites, "pending?" + existingQueryString));

            model.addAttribute("model",
                    viewModel);
        });

        return "admin/users";
    }

    @PreAuthorize("hasPermission(#userId, 'org.innovateuk.ifs.user.resource.UserCompositeId' ,'ACCESS_INTERNAL_USER')")
    @GetMapping("/user/{userId}")
    public String viewUser(@P("userId")@PathVariable Long userId, Model model){
        return userRestService.retrieveUserById(userId).andOnSuccessReturn( user -> {
                    model.addAttribute("model", new EditUserViewModel(user));
                    return "admin/user";
        }).getSuccess();
    }

    @PreAuthorize("hasPermission(#userId, 'org.innovateuk.ifs.user.resource.UserCompositeId', 'EDIT_INTERNAL_USER')")
    @GetMapping("/user/{userId}/edit")
    public String viewEditUser(@P("userId")@PathVariable Long userId,
                               Model model) {

        return viewEditUser(model, userId, new EditUserForm());
    }

    private String viewEditUser(Model model, Long userId, EditUserForm form) {

        UserResource userResource = userRestService.retrieveUserById(userId).getSuccess();
        form.setFirstName(userResource.getFirstName());
        form.setLastName(userResource.getLastName());

        if (userResource.getRoles().contains(Role.IFS_ADMINISTRATOR)) {
            form.setRole(Role.IFS_ADMINISTRATOR);
        } else {
            form.setRole(userResource.getRoles().stream().findFirst().get());
        }
        form.setEmailAddress(userResource.getEmail());
        model.addAttribute(FORM_ATTR_NAME, form);
        model.addAttribute("user", userResource);

        return "admin/edit-user";

    }

    @PreAuthorize("hasPermission(#userId, 'org.innovateuk.ifs.user.resource.UserCompositeId', 'EDIT_INTERNAL_USER')")
    @PostMapping("/user/{userId}/edit")
    public String updateUser(@P("userId")@PathVariable Long userId,
                             Model model,
                             @Valid @ModelAttribute(FORM_ATTR_NAME) EditUserForm form,
                             @SuppressWarnings("unused") BindingResult bindingResult, ValidationHandler validationHandler) {

        Supplier<String> failureView = () -> viewEditUser(model, userId, form);

        return validationHandler.failNowOrSucceedWith(failureView, () -> {

            EditUserResource editUserResource = constructEditUserResource(form, userId);

            ServiceResult<Void> saveResult = internalUserService.editInternalUser(editUserResource);

            return validationHandler.addAnyErrors(saveResult, fieldErrorsToFieldErrors(), asGlobalErrors()).
                    failNowOrSucceedWith(failureView, () -> "redirect:/admin/users/active");

        });
    }

    private static EditUserResource constructEditUserResource(EditUserForm form, Long userId) {
        return new EditUserResource(userId, form.getFirstName(), form.getLastName(), form.getRole());
    }

    @PreAuthorize("hasPermission(#userId, 'org.innovateuk.ifs.user.resource.UserCompositeId', 'EDIT_INTERNAL_USER')")
    @PostMapping(value = "/user/{userId}/edit", params = "deactivateUser")
    public String deactivateUser(@P("userId")@PathVariable Long userId) {
        return userRestService.retrieveUserById(userId).andOnSuccess( user ->
                userRestService.deactivateUser(userId).andOnSuccessReturn(p -> "redirect:/admin/user/" + userId)).getSuccess();
    }

    @PreAuthorize("hasPermission(#userId, 'org.innovateuk.ifs.user.resource.UserCompositeId', 'ACCESS_INTERNAL_USER')")
    @PostMapping(value = "/user/{userId}", params = "reactivateUser")
    public String reactivateUser(@P("userId") @PathVariable Long userId) {
        return userRestService.retrieveUserById(userId).andOnSuccess( user ->
                userRestService.reactivateUser(userId).andOnSuccessReturn(p -> "redirect:/admin/user/" + userId)).getSuccess();
    }

    @SecuredBySpring(value = "FIND_EXTERNAL_USERS", description = "Only the support user or IFS Admin can access external user information")
    @PreAuthorize("hasAnyAuthority('support', 'ifs_administrator')")
    @GetMapping(value = "/external/users")
    public String viewFindExternalUsers(@ModelAttribute(FORM_ATTR_NAME) SearchExternalUsersForm form, Model model) {
        model.addAttribute("tab", "users");
        return emptyPage(model);
    }

    @SecuredBySpring(value = "FIND_EXTERNAL_INVITES", description = "Only the support user or IFS Admin can access external user invites")
    @PreAuthorize("hasAnyAuthority('support', 'ifs_administrator')")
    @GetMapping(value = "/external/invites")
    public String viewFindExternalInvites(@ModelAttribute(FORM_ATTR_NAME) SearchExternalUsersForm form, Model model) {
        model.addAttribute("tab", "invites");
        return emptyPage(model);
    }

    private static String emptyPage(Model model){
        model.addAttribute("mode", "init");
        model.addAttribute("users", emptyList());
        return SEARCH_PAGE_TEMPLATE;
    }

    @SecuredBySpring(value = "FIND_EXTERNAL_USERS", description = "Only the support user or IFS Admin can access external user information")
    @PreAuthorize("hasAnyAuthority('support', 'ifs_administrator')")
    @PostMapping({"/external/users", "/external/invites"})
    public String findExternalUsers(@Valid @ModelAttribute(FORM_ATTR_NAME) SearchExternalUsersForm form,
                                    @SuppressWarnings("unused") BindingResult bindingResult, ValidationHandler validationHandler,
                                    Model model, HttpServletRequest request) {
        Map<String, String[]> requestParams = request.getParameterMap();
        if (requestParams.containsKey("pending")) {
            return findExternalInvites(form, validationHandler, model);
        } else {
            return findExternalUsers(form, validationHandler, model);
        }
    }

    @SecuredBySpring(value = "RESEND_INTERNAL_USER_INVITE", description = "Only the IFS Administrators can resend invites to internal users")
    @PreAuthorize("hasAuthority('ifs_administrator')")
    @PostMapping("/users/pending/resend-invite")
    public String resendInvite(@RequestParam("inviteId") long inviteId) {
        inviteUserRestService.resendInternalUserInvite(inviteId).getSuccess();
        return "redirect:/admin/users/pending";
    }

    private String findExternalUsers(SearchExternalUsersForm form, ValidationHandler validationHandler, Model model) {
        Supplier<String> failureView = () -> viewFindExternalUsers(form, model);

        return validationHandler.failNowOrSucceedWith(failureView, () -> {
            RestResult<List<UserOrganisationResource>> users = userRestService.findExternalUsers(form.getSearchString(), form.getSearchCategory());
            return validationHandler.addAnyErrors(users, fieldErrorsToFieldErrors(), asGlobalErrors()).
                    failNowOrSucceedWith(failureView, () -> {
                                model.addAttribute("mode", "search");
                                model.addAttribute("tab", "users");
                                model.addAttribute("users", users.getSuccess());
                                return SEARCH_PAGE_TEMPLATE;
                            }
                    );
        });
    }

    private String findExternalInvites(SearchExternalUsersForm form, ValidationHandler validationHandler, Model model) {
        Supplier<String> failureView = () -> viewFindExternalInvites(form, model);

        return validationHandler.failNowOrSucceedWith(failureView, () -> {
            RestResult<List<ExternalInviteResource>> invites = inviteUserRestService.findExternalInvites(form.getSearchString().trim(), form.getSearchCategory());
            return validationHandler.addAnyErrors(invites, fieldErrorsToFieldErrors(), asGlobalErrors()).
                    failNowOrSucceedWith(failureView, () -> {
                                model.addAttribute("mode", "search");
                                model.addAttribute("tab", "invites");
                                model.addAttribute("invites", invites.getSuccess());
                                return SEARCH_PAGE_TEMPLATE;
                            }
                    );
        });
    }
}
