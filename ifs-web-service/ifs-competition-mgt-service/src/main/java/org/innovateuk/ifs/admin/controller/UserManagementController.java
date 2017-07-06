package org.innovateuk.ifs.admin.controller;

import org.innovateuk.ifs.admin.form.InviteUserForm;
import org.innovateuk.ifs.admin.viewmodel.EditUserViewModel;
import org.innovateuk.ifs.admin.viewmodel.UserListViewModel;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.management.viewmodel.PaginationViewModel;
import org.innovateuk.ifs.profile.service.ProfileRestService;
import org.innovateuk.ifs.user.resource.UserResource;
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
import java.util.Objects;
import java.util.function.Supplier;

/**
 * This controller will handle all requests that are related to management of users by IFS Administrators.
 */
@Controller
@RequestMapping("/admin")
@PreAuthorize("hasAnyAuthority('ifs_administrator')")
public class UserManagementController {

    private static final String DEFAULT_PAGE_NUMBER = "0";

    private static final String DEFAULT_PAGE_SIZE = "40";

    private static final String FORM_ATTR_NAME = "form";

    @Autowired
    private UserRestService userRestService;

    @Autowired
    private ProfileRestService profileRestService;

    @GetMapping("/users/active")
    public String viewActive(Model model,
                             HttpServletRequest request,
                             @RequestParam(value = "page", defaultValue = DEFAULT_PAGE_NUMBER) int page,
                             @RequestParam(value = "size", defaultValue = DEFAULT_PAGE_SIZE) int size) {
        return view(model, "active", page, size, Objects.toString(request.getQueryString(), ""));
    }

    @GetMapping("/users/inactive")
    public String viewInactive(Model model,
                               HttpServletRequest request,
                               @RequestParam(value = "page", defaultValue = DEFAULT_PAGE_NUMBER) int page,
                               @RequestParam(value = "size", defaultValue = DEFAULT_PAGE_SIZE) int size) {
        return view(model, "inactive", page, size, Objects.toString(request.getQueryString(), ""));
    }

    private String view(Model model, String activeTab, int page, int size, String existingQueryString){
        return userRestService.getActiveInternalUsers(page, size).andOnSuccessReturn(activeInternalUsers -> userRestService.getInactiveInternalUsers(page, size).andOnSuccessReturn(inactiveInternalUsers -> {
            model.addAttribute("model",
                    new UserListViewModel(
                            activeTab,
                            activeInternalUsers.getContent(),
                            inactiveInternalUsers.getContent(),
                            activeInternalUsers.getTotalElements(),
                            inactiveInternalUsers.getTotalElements(),
                            new PaginationViewModel(activeInternalUsers, "active?" + existingQueryString) ,
                            new PaginationViewModel(inactiveInternalUsers, "inactive?" + existingQueryString)));
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

    @GetMapping("/user/{userId}/edit")
    public String viewEditUser(Model model,
                             HttpServletRequest request,
                             UserResource loggedInUser) {

        return viewEditUser(model);
    }

    private String viewEditUser(Model model) {
        InviteUserForm form = new InviteUserForm();
        model.addAttribute(FORM_ATTR_NAME, form);

        return "admin/edit-user";

    }

    @PostMapping("/user/{userId}/edit")
    public String updateUser(@PathVariable Long userId,
                             Model model,
                             HttpServletRequest request,
                             @Valid @ModelAttribute(FORM_ATTR_NAME) InviteUserForm form,
                             @SuppressWarnings("unused") BindingResult bindingResult, ValidationHandler validationHandler,
                             UserResource loggedInUser) {

        Supplier<String> failureView = () -> "admin/user/" + userId + "/edit";

        return "redirect:/admin/users/active";
    }

}