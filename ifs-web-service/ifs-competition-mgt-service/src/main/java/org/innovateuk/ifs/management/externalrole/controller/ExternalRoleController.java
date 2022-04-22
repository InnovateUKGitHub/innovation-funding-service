package org.innovateuk.ifs.management.externalrole.controller;

import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.management.admin.form.SelectExternalRoleForm;
import org.innovateuk.ifs.management.externalrole.form.ExternalRoleForm;
import org.innovateuk.ifs.management.externalrole.viewmodel.ExternalRoleViewModel;
import org.innovateuk.ifs.profile.service.ProfileRestService;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserProfileResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.UserRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Comparator;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static org.innovateuk.ifs.commons.error.CommonFailureKeys.USER_ADD_ROLE_INVALID_EMAIL;
import static org.innovateuk.ifs.controller.ErrorToObjectErrorConverterFactory.*;

@Controller
@RequestMapping("/admin/user/{userId}")
@SecuredBySpring(value = "Controller", description = "Admins can add an external role to existing users",
        securedType = ExternalRoleController.class)
@PreAuthorize("hasAuthority('ifs_administrator')")
public class ExternalRoleController {

    @Autowired
    private UserRestService userRestService;

    @Autowired
    private ProfileRestService profileRestService;

    @Value("${ifs.assessor.pool.enabled}")
    private Boolean isAssessorPoolEnabled;

    @GetMapping("/external-role")
    public String viewUser(@PathVariable long userId,
                           @ModelAttribute(name = "form") ExternalRoleForm form,
                           @RequestParam(value = "role") Role role,
                           Model model) {

        UserResource user = userRestService.retrieveUserById(userId).getSuccess();

        model.addAttribute("model", new ExternalRoleViewModel(userId, user.getName(), user.getEmail(), role));
        return "externalrole/external-role";
    }

    @PostMapping("/external-role")
    public String addRoleToUser(@PathVariable long userId,
                                @ModelAttribute(name = "form") @Valid ExternalRoleForm form,
                                BindingResult bindingResult,
                                Model model,
                                ValidationHandler validationHandler) {

        Supplier<String> failureView = () -> viewUser(userId, form, form.getRole(), model);
        return validationHandler.failNowOrSucceedWith(failureView, () -> {
            validationHandler.addAnyErrors(userRestService.grantRole(userId, form.getRole()), mappingErrorKeyToField(USER_ADD_ROLE_INVALID_EMAIL, "email"), fieldErrorsToFieldErrors(), asGlobalErrors());
            return validationHandler.failNowOrSucceedWith(failureView, () -> {
                UserProfileResource userProfileResource = profileRestService.getUserProfile(userId).getSuccess();
                userProfileResource.setSimpleOrganisation(form.getOrganisation());
                validationHandler.addAnyErrors(profileRestService.updateUserProfile(userId, userProfileResource), mappingErrorKeyToField(USER_ADD_ROLE_INVALID_EMAIL, "email"), fieldErrorsToFieldErrors(), asGlobalErrors());

                return validationHandler.failNowOrSucceedWith(failureView,
                        () -> redirectToUserPage(userId));
            });
        });
    }

    @GetMapping("/select")
    public String selectRole(@PathVariable long userId,
                             @ModelAttribute(name = "form") SelectExternalRoleForm form,
                             Model model) {

        UserResource user = userRestService.retrieveUserById(userId).getSuccess();
        model.addAttribute("roles", (isAssessorPoolEnabled ? Role.externalRolesIncludingAssessorToInvite() : Role.externalRolesToInvite())
                .stream().filter(role -> !user.hasRole(role)).sorted(Comparator.comparing(Role::getDisplayName)).collect(Collectors.toList()));
        return "admin/select-external-role";
    }

    @PostMapping("/select")
    public String selectedRole(@PathVariable long userId,
                               @ModelAttribute(name = "form") @Valid SelectExternalRoleForm form,
                               BindingResult bindingResult,
                               ValidationHandler validationHandler,
                               Model model) {

        Supplier<String> failureView = () -> selectRole(userId, form, model);
        return validationHandler.failNowOrSucceedWith(failureView, () -> redirectToAddRolePage(form.getRole(), userId));
    }

    private String redirectToAddRolePage(Role role, long userId) {
        return String.format("redirect:/admin/user/%d/external-role?role=%s", userId, role.toString());
    }

    private String redirectToUserPage(long userId) {
        return String.format("redirect:/admin/user/%d/active", userId);
    }
}
