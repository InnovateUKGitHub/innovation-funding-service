package org.innovateuk.ifs.admin.controller;

import org.innovateuk.ifs.admin.form.EditUserForm;
import org.innovateuk.ifs.admin.form.SearchExternalUsersForm;
import org.innovateuk.ifs.admin.viewmodel.EditUserViewModel;
import org.innovateuk.ifs.admin.viewmodel.UserListViewModel;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.invite.resource.EditUserResource;
import org.innovateuk.ifs.invite.resource.ExternalInviteResource;
import org.innovateuk.ifs.invite.service.InviteUserRestService;
import org.innovateuk.ifs.management.viewmodel.PaginationViewModel;
import org.innovateuk.ifs.registration.service.InternalUserService;
import org.innovateuk.ifs.user.resource.UserOrganisationResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.resource.UserRoleType;
import org.innovateuk.ifs.user.service.UserRestService;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.util.function.Supplier;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.controller.ErrorToObjectErrorConverterFactory.asGlobalErrors;
import static org.innovateuk.ifs.controller.ErrorToObjectErrorConverterFactory.fieldErrorsToFieldErrors;

/**
 * This controller will handle all requests that are related to management of users by IFS Administrators.
 */
@Controller
@RequestMapping("/admin")
public class UserManagementController {

    private static final String DEFAULT_PAGE_NUMBER = "0";

    private static final String DEFAULT_PAGE_SIZE = "40";

    private static final String FORM_ATTR_NAME = "form";

    @Autowired
    private UserRestService userRestService;

    @Autowired
    private InviteUserRestService inviteUserRestService;

    @Autowired
    private InternalUserService internalUserService;

    @SecuredBySpring(value = "TODO", description = "TODO")
    @PreAuthorize("hasAnyAuthority('ifs_administrator')")
    @GetMapping("/users/active")
    public String viewActive(Model model,
                             HttpServletRequest request,
                             @RequestParam(value = "page", defaultValue = DEFAULT_PAGE_NUMBER) int page,
                             @RequestParam(value = "size", defaultValue = DEFAULT_PAGE_SIZE) int size) {
        return view(model, "active", page, size, Objects.toString(request.getQueryString(), ""));
    }

    @SecuredBySpring(value = "TODO", description = "TODO")
    @PreAuthorize("hasAnyAuthority('ifs_administrator')")
    @GetMapping("/users/inactive")
    public String viewInactive(Model model,
                               HttpServletRequest request,
                               @RequestParam(value = "page", defaultValue = DEFAULT_PAGE_NUMBER) int page,
                               @RequestParam(value = "size", defaultValue = DEFAULT_PAGE_SIZE) int size) {
        return view(model, "inactive", page, size, Objects.toString(request.getQueryString(), ""));
    }

    @SecuredBySpring(value = "TODO", description = "TODO")
    @PreAuthorize("hasAnyAuthority('ifs_administrator')")
    @GetMapping("/users/pending")
    public String viewPending(Model model,
                               HttpServletRequest request,
                               @RequestParam(value = "page", defaultValue = DEFAULT_PAGE_NUMBER) int page,
                               @RequestParam(value = "size", defaultValue = DEFAULT_PAGE_SIZE) int size) {
        return view(model, "pending", page, size, Objects.toString(request.getQueryString(), ""));
    }

    private String view(Model model, String activeTab, int page, int size, String existingQueryString){
        return userRestService.getActiveInternalUsers(page, size)
                .andOnSuccessReturn(activeInternalUsers -> userRestService.getInactiveInternalUsers(page, size)
                        .andOnSuccessReturn(inactiveInternalUsers -> inviteUserRestService.getPendingInternalUserInvites(page, size)
                                .andOnSuccessReturn(pendingInternalUserInvites ->
                                {
                                    model.addAttribute("model",
                                            new UserListViewModel(
                                                    activeTab,
                                                    activeInternalUsers.getContent(),
                                                    inactiveInternalUsers.getContent(),
                                                    pendingInternalUserInvites.getContent(),
                                                    activeInternalUsers.getTotalElements(),
                                                    inactiveInternalUsers.getTotalElements(),
                                                    pendingInternalUserInvites.getTotalElements(),
                                                    new PaginationViewModel(activeInternalUsers, "active?" + existingQueryString),
                                                    new PaginationViewModel(inactiveInternalUsers, "inactive?" + existingQueryString),
                                                    new PaginationViewModel(pendingInternalUserInvites, "pending?" + existingQueryString)));
                                    return "admin/users";
                                }).getSuccessObjectOrThrowException()).getSuccessObjectOrThrowException()).getSuccessObjectOrThrowException();
    }

    @PreAuthorize("hasPermission(#userId, 'ACCESS_INTERNAL_USER')")
    @GetMapping("/user/{userId}")
    public String viewUser(@PathVariable Long userId, Model model){
        return userRestService.retrieveUserById(userId).andOnSuccessReturn( user -> {
                    model.addAttribute("model", new EditUserViewModel(user));
                    return "admin/user";
        }).getSuccessObjectOrThrowException();
    }

    @PreAuthorize("hasPermission(#userId, 'EDIT_INTERNAL_USER')")
    @GetMapping("/user/{userId}/edit")
    public String viewEditUser(@PathVariable Long userId,
                               Model model) {

        return viewEditUser(model, userId, new EditUserForm());
    }

    private String viewEditUser(Model model, Long userId, EditUserForm form) {

        UserResource userResource = userRestService.retrieveUserById(userId).getSuccessObjectOrThrowException();
        form.setFirstName(userResource.getFirstName());
        form.setLastName(userResource.getLastName());
        // userResource.getRolesString() will return a single role for internal users
        form.setRole(UserRoleType.fromDisplayName(userResource.getRolesString()));
        form.setEmailAddress(userResource.getEmail());
        model.addAttribute(FORM_ATTR_NAME, form);
        model.addAttribute("user", userResource);

        return "admin/edit-user";

    }

    @PreAuthorize("hasPermission(#userId, 'EDIT_INTERNAL_USER')")
    @PostMapping("/user/{userId}/edit")
    public String updateUser(@PathVariable Long userId,
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

    private EditUserResource constructEditUserResource(EditUserForm form, Long userId) {
        return new EditUserResource(userId, form.getFirstName(), form.getLastName(), form.getRole());
    }

    @PreAuthorize("hasPermission(#userId, 'EDIT_INTERNAL_USER')")
    @PostMapping(value = "/user/{userId}/edit", params = "deactivateUser")
    public String deactivateUser(@PathVariable Long userId) {
        return userRestService.retrieveUserById(userId).andOnSuccess( user ->
                userRestService.deactivateUser(userId).andOnSuccessReturn(p -> "redirect:/admin/user/" + userId)).getSuccessObjectOrThrowException();
    }

    @PreAuthorize("hasPermission(#userId, 'ACCESS_INTERNAL_USER')")
    @PostMapping(value = "/user/{userId}", params = "reactivateUser")
    public String reactivateUser(@PathVariable Long userId) {
        return userRestService.retrieveUserById(userId).andOnSuccess( user ->
                userRestService.reactivateUser(userId).andOnSuccessReturn(p -> "redirect:/admin/user/" + userId)).getSuccessObjectOrThrowException();
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

    private String emptyPage(Model model){
        model.addAttribute("mode", "init");
        model.addAttribute("users", emptyList());
        return "admin/search-external-users";
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

    private String findExternalUsers(SearchExternalUsersForm form, ValidationHandler validationHandler, Model model) {
        Supplier<String> failureView = () -> viewFindExternalUsers(form, model);

        return validationHandler.failNowOrSucceedWith(failureView, () -> {
            RestResult<List<UserOrganisationResource>> users = userRestService.findExternalUsers(form.getSearchString(), form.getSearchCategory());
            return validationHandler.addAnyErrors(users, fieldErrorsToFieldErrors(), asGlobalErrors()).
                    failNowOrSucceedWith(failureView, () -> {
                                model.addAttribute("mode", "search");
                                model.addAttribute("tab", "users");
                                model.addAttribute("users", users.getSuccessObjectOrThrowException());
                                return "admin/search-external-users";
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
                                model.addAttribute("invites", invites.getSuccessObjectOrThrowException());
                                return "admin/search-external-users";
                            }
                    );
        });
    }
}
