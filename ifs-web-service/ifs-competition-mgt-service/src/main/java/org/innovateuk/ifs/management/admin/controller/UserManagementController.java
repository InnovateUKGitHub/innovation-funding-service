package org.innovateuk.ifs.management.admin.controller;

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
import org.innovateuk.ifs.management.admin.form.ConfirmEmailForm;
import org.innovateuk.ifs.management.admin.form.EditUserForm;
import org.innovateuk.ifs.management.admin.form.EditUserForm.InternalUserFieldsGroup;
import org.innovateuk.ifs.management.admin.form.SearchExternalUsersForm;
import org.innovateuk.ifs.management.admin.form.UserManagementFilterForm;
import org.innovateuk.ifs.management.admin.viewmodel.ConfirmEmailViewModel;
import org.innovateuk.ifs.management.admin.viewmodel.EditUserViewModel;
import org.innovateuk.ifs.management.admin.viewmodel.UserListViewModel;
import org.innovateuk.ifs.management.registration.service.InternalUserService;
import org.innovateuk.ifs.pagination.PaginationViewModel;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserOrganisationResource;
import org.innovateuk.ifs.user.resource.UserPageResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.RoleProfileStatusRestService;
import org.innovateuk.ifs.user.service.UserRestService;
import org.innovateuk.ifs.util.EncryptedCookieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.Validator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.controller.ErrorToObjectErrorConverterFactory.asGlobalErrors;
import static org.innovateuk.ifs.controller.ErrorToObjectErrorConverterFactory.fieldErrorsToFieldErrors;
import static org.innovateuk.ifs.user.resource.Role.IFS_ADMINISTRATOR;

/**
 * This controller will handle all requests that are related to management of users by IFS Administrators.
 */
@Controller
@RequestMapping("/admin")
public class UserManagementController extends AsyncAdaptor {

    private static final String DEFAULT_PAGE_NUMBER = "1";
    private static final String DEFAULT_PAGE_SIZE = "20";
    private static final String FORM_ATTR_NAME = "form";
    private static final String SEARCH_PAGE_TEMPLATE = "admin/search-external-users";
    private static final String NEW_EMAIL_COOKIE = "NEW_EMAIL_COOKIE";

    @Autowired
    private UserRestService userRestService;

    @Autowired
    private InviteUserRestService inviteUserRestService;

    @Autowired
    private InternalUserService internalUserService;

    @Autowired
    private Validator validator;

    @Autowired
    private EncryptedCookieService cookieService;

    @Autowired
    private RoleProfileStatusRestService roleProfileStatusRestService;

    @Value("${ifs.assessor.profile.feature.toggle}")
    private boolean profileFeatureToggle;

    @AsyncMethod
    @SecuredBySpring(value = "UserManagementController.viewActive() method",
            description = "Only IFS administrators can view active internal users")
    @PreAuthorize("hasAnyAuthority('ifs_administrator', 'support')")
    @GetMapping("/users/active")
    public String viewActive(Model model,
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
    @GetMapping("/users/inactive")
    public String viewInactive(Model model,
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
    @GetMapping("/users/pending")
    public String viewPending(Model model,
                              HttpServletRequest request,
                              UserResource user,
                              @Valid @ModelAttribute(FORM_ATTR_NAME) UserManagementFilterForm filterForm,
                              @RequestParam(defaultValue = DEFAULT_PAGE_NUMBER) int page,
                              @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int size) {
        return view(model, "pending", filterForm.getFilter(), page, size, true);
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

    @PreAuthorize("hasAnyAuthority('ifs_administrator', 'support')")
    @SecuredBySpring(value = "UserManagementController.viewUser() method",
            description = "IFS admins and support users can view users.")
    @GetMapping("/user/{userId}")
    public String viewUser(@PathVariable long userId, Model model, UserResource loggedInUser) {
        return userRestService.retrieveUserById(userId).andOnSuccessReturn( user -> {
                    model.addAttribute("model", populateEditUserViewModel(user, loggedInUser));
                    return "admin/user";
        }).getSuccess();
    }

    private EditUserViewModel populateEditUserViewModel(UserResource user, UserResource loggedInUser) {
        return new EditUserViewModel(user,
                                     roleProfileStatusRestService.findByUserId(user.getId())
                                             .getOptionalSuccessObject()
                                             .orElse(emptyList()),
                                     loggedInUser.hasRole(IFS_ADMINISTRATOR),
                                     profileFeatureToggle);
    }

    @PreAuthorize("hasAnyAuthority('ifs_administrator', 'support')")
    @SecuredBySpring(value = "UserManagementController.viewEditUser() method",
            description = "IFS admins and support users can edit users.")
    @GetMapping("/user/{userId}/edit")
    public String viewEditUser(@PathVariable long userId, Model model, UserResource loggedInUser) {
        UserResource user = userRestService.retrieveUserById(userId).getSuccess();
        model.addAttribute(FORM_ATTR_NAME, populateForm(user));
        return viewEditUser(model, user, loggedInUser);
    }

    private EditUserForm populateForm(UserResource user) {
        EditUserForm form = new EditUserForm();
        form.setFirstName(user.getFirstName());
        form.setLastName(user.getLastName());

        if (user.getRoles().contains(IFS_ADMINISTRATOR)) {
            form.setRole(IFS_ADMINISTRATOR);
        } else {
            form.setRole(user.getRoles().stream().findFirst().get());
        }
        form.setEmail(user.getEmail());
        return form;
    }

    private String viewEditUser(Model model, UserResource user, UserResource loggedInUser) {
        model.addAttribute("model", populateEditUserViewModel(user, loggedInUser));
        return "admin/edit-user";
    }

    @PreAuthorize("hasAnyAuthority('ifs_administrator', 'support')")
    @SecuredBySpring(value = "UserManagementController.updateUser() method",
            description = "IFS admins and support users can edit users.")
    @PostMapping("/user/{userId}/edit")
    public String updateUser(@PathVariable long userId,
                             Model model,
                             UserResource loggedInUser,
                             @Valid @ModelAttribute(FORM_ATTR_NAME) EditUserForm form,
                             @SuppressWarnings("unused") BindingResult bindingResult,
                             ValidationHandler validationHandler,
                             HttpServletResponse response) {
        UserResource user = userRestService.retrieveUserById(userId).getSuccess();
        validateUser(form, user);

        Supplier<String> failureView = () -> viewEditUser(model, user, loggedInUser);
        Supplier<String> noEmailChangeSuccess = () -> "redirect:/admin/users/active";
        Supplier<String> emailChangeSuccess = () ->  {
            cookieService.saveToCookie(response, NEW_EMAIL_COOKIE, form.getEmail());
            return String.format("redirect:/admin/user/%d/edit/confirm", userId);
        };
        Supplier<String> successView = !user.getEmail().equals(form.getEmail()) ? emailChangeSuccess : noEmailChangeSuccess;

        return validationHandler.failNowOrSucceedWith(failureView, () -> {
            ServiceResult<Void> saveResult = serviceSuccess();
            if (user.isInternalUser()) {
                saveResult = internalUserService.editInternalUser(constructEditUserResource(form, userId));
            }
            return validationHandler.addAnyErrors(saveResult, fieldErrorsToFieldErrors(), asGlobalErrors()).
                    failNowOrSucceedWith(failureView, successView);
        });
    }

    private void validateUser(EditUserForm form, UserResource user) {
        if (user.isInternalUser()) {
            validator.validate(form, InternalUserFieldsGroup.class);
        }
    }

    @PreAuthorize("hasAnyAuthority('ifs_administrator', 'support')")
    @SecuredBySpring(value = "UserManagementController.confirmEmailChange() method",
            description = "IFS admins and support users can confirm email change.")
    @GetMapping("/user/{userId}/edit/confirm")
    public String confirmEmailChange(@PathVariable long userId,
                                     Model model,
                                     @ModelAttribute(value = FORM_ATTR_NAME, binding = false) ConfirmEmailForm form,
                                     @SuppressWarnings("unused") BindingResult bindingResult,
                                     HttpServletRequest request) {
        String email = cookieService.getCookieValue(request, NEW_EMAIL_COOKIE);
        if (email.isEmpty()) {
            return String.format("redirect:/admin/user/%d/edit", userId);
        }
        UserResource user = userRestService.retrieveUserById(userId).getSuccess();
        model.addAttribute("model", new ConfirmEmailViewModel(user, email));
        return "admin/confirm-email";
    }

    @PreAuthorize("hasAnyAuthority('ifs_administrator', 'support')")
    @SecuredBySpring(value = "UserManagementController.confirmEmailChange() method",
            description = "IFS admins and support users can confirm email change.")
    @PostMapping("/user/{userId}/edit/confirm")
    public String confirmEmailChangePost(@PathVariable long userId,
                                         Model model,
                                         @Valid @ModelAttribute(value = FORM_ATTR_NAME) ConfirmEmailForm form,
                                         @SuppressWarnings("unused") BindingResult bindingResult,
                                         ValidationHandler validationHandler,
                                         HttpServletRequest request,
                                         HttpServletResponse response,
                                         RedirectAttributes redirectAttributes) {
        Supplier<String> failureView = () -> confirmEmailChange(userId, model, form, bindingResult, request);
        Supplier<String> successView = () -> {
            cookieService.removeCookie(response, NEW_EMAIL_COOKIE);
            redirectAttributes.addFlashAttribute("showEmailUpdateSuccess", true);
            return "redirect:/admin/users/active";
        };

        return validationHandler.failNowOrSucceedWith(failureView, () -> {
            String email = cookieService.getCookieValue(request, NEW_EMAIL_COOKIE);
            if (email.isEmpty()) {
                return String.format("redirect:/admin/user/%d/edit", userId);
            }
            validationHandler.addAnyErrors(userRestService.updateEmail(userId, email));
            return validationHandler.failNowOrSucceedWith(failureView, successView);
        });
    }

    private static EditUserResource constructEditUserResource(EditUserForm form, long userId) {
        return new EditUserResource(userId, form.getFirstName(), form.getLastName(), form.getRole());
    }

    @PreAuthorize("hasAnyAuthority('ifs_administrator', 'support')")
    @SecuredBySpring(value = "UserManagementController.deactivateUser() method",
            description = "IFS admins and support users can deactivate users.")
    @PostMapping(value = "/user/{userId}/edit", params = "deactivateUser")
    public String deactivateUser(@PathVariable long userId) {
        return userRestService.retrieveUserById(userId).andOnSuccess( user ->
                userRestService.deactivateUser(userId).andOnSuccessReturn(p -> "redirect:/admin/user/" + userId)).getSuccess();
    }

    @PreAuthorize("hasAnyAuthority('ifs_administrator', 'support')")
    @SecuredBySpring(value = "UserManagementController.reactivateUser() method",
            description = "IFS admins and support users can reactivate users.")
    @PostMapping(value = "/user/{userId}", params = "reactivateUser")
    public String reactivateUser(@PathVariable long userId) {
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