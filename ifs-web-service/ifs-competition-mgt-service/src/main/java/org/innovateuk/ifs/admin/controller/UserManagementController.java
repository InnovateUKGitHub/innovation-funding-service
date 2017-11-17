package org.innovateuk.ifs.admin.controller;

import org.apache.commons.lang3.StringUtils;
import org.innovateuk.ifs.admin.form.EditUserForm;
import org.innovateuk.ifs.admin.viewmodel.EditUserViewModel;
import org.innovateuk.ifs.admin.viewmodel.UserListViewModel;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.invite.resource.EditUserResource;
import org.innovateuk.ifs.invite.resource.ExternalInviteResource;
import org.innovateuk.ifs.invite.service.InviteUserRestService;
import org.innovateuk.ifs.management.viewmodel.PaginationViewModel;
import org.innovateuk.ifs.registration.service.InternalUserService;
import org.innovateuk.ifs.user.resource.SearchCategory;
import org.innovateuk.ifs.user.resource.UserOrganisationResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.resource.UserRoleType;
import org.innovateuk.ifs.user.service.UserRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

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
                               Model model,
                               HttpServletRequest request,
                               UserResource loggedInUser) {

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
                             HttpServletRequest request,
                             @Valid @ModelAttribute(FORM_ATTR_NAME) EditUserForm form,
                             @SuppressWarnings("unused") BindingResult bindingResult, ValidationHandler validationHandler,
                             UserResource loggedInUser) {

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

    @SecuredBySpring(value = "TODO", description = "TODO")
    @PreAuthorize("hasAuthority('support')")
    @GetMapping(value = "/users/created")
    public String allExternalUsers(Model model) {
        return userRestService.findAllExternal().andOnSuccessReturn(users -> {
            model.addAttribute("users", users);
           return "admin/external-users";
        }).getSuccessObjectOrThrowException();
    }

    @PreAuthorize("hasAnyAuthority('support', 'ifs_admin')")
    @GetMapping(value = "/external/users")
    public String findExternalUsers(@RequestParam(value = "searchString", required = false) String searchString,
                                    @RequestParam(value = "searchCategory", required = false) final SearchCategory searchCategory,
                                    Model model) {

        List<UserOrganisationResource> users;
        if (StringUtils.isNotEmpty(searchString) && searchCategory != null) {
            searchString = "%" + StringUtils.trim(searchString) + "%";
            users = userRestService.findExternalUsers(searchString, searchCategory).getSuccessObjectOrThrowException();
        } else {
            users = Collections.emptyList();
        }

        model.addAttribute("users", users);
        return "admin/search-external-users";
    }

/*    @SecuredBySpring(value = "TODO", description = "TODO")
    @PreAuthorize("hasAuthority('support')")
    @GetMapping(value = "/invites")
    public String allExternalInvites(Model model) {
        return inviteUserRestService.getAllExternalInvites().andOnSuccessReturn(invites -> {
            model.addAttribute("invites", invites);
            return "admin/invited-users";
        }).getSuccessObjectOrThrowException();
    }*/

    @PreAuthorize("hasAnyAuthority('support', 'ifs_admin')")
    @GetMapping(value = "/external/invites")
    public String findExternalInvites(@RequestParam(value = "searchString", required = false) String searchString,
                                      @RequestParam(value = "searchCategory", required = false) final SearchCategory searchCategory,
                                      Model model) {

        List<ExternalInviteResource> invites;
        if (StringUtils.isNotEmpty(searchString) && searchCategory != null) {
            searchString = StringUtils.trim(searchString);
            invites = inviteUserRestService.findExternalInvites(searchString, searchCategory).getSuccessObjectOrThrowException();
        } else {
            invites = Collections.emptyList();
        }

        model.addAttribute("invites", invites);
        return "admin/search-invited-users";
    }
}
