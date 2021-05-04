package org.innovateuk.ifs.management.admin.controller;

import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.invite.resource.InviteUserResource;
import org.innovateuk.ifs.management.admin.form.InviteUserForm;
import org.innovateuk.ifs.management.admin.form.InviteUserView;
import org.innovateuk.ifs.management.admin.form.SelectExternalRoleForm;
import org.innovateuk.ifs.management.admin.form.validation.Primary;
import org.innovateuk.ifs.management.admin.populator.InviteUserModelPopulator;
import org.innovateuk.ifs.management.admin.viewmodel.InviteUserViewModel;
import org.innovateuk.ifs.management.invite.service.InviteUserService;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.groups.Default;
import java.util.Set;
import java.util.function.Supplier;

import static java.util.Collections.singleton;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.KTA_USER_ROLE_INVITE_INVALID_EMAIL;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.USER_ROLE_INVITE_INVALID_EMAIL;
import static org.innovateuk.ifs.controller.ErrorToObjectErrorConverterFactory.*;

/**
 * Controller for handling requests related to invitation of new users by the IFS Administrator
 */
@Controller
@RequestMapping("/admin")
@SecuredBySpring(value = "Controller", description = "Only IFS Administrators can invite internal users", securedType = InviteUserController.class)
@PreAuthorize("hasAuthority('ifs_administrator')")
public class InviteUserController {

    private static final String FORM_ATTR_NAME = "form";
    private static final String MODEL_ATTR_NAME = "model";

    @Autowired
    private InviteUserService inviteUserService;

    @Autowired
    private InviteUserModelPopulator inviteUserModelPopulator;

    @GetMapping("/select-external-role")
    public String selectRole(@ModelAttribute(name = "form") SelectExternalRoleForm form,
                             Model model) {

        model.addAttribute("roles", Role.externalRolesToInvite());
        return "admin/select-external-role";
    }

    @PostMapping("/select-external-role")
    public String selectedRole(@ModelAttribute(name = "form") @Valid SelectExternalRoleForm form,
                               BindingResult bindingResult,
                               ValidationHandler validationHandler,
                               Model model) {

        Supplier<String> failureView = () -> selectRole(form, model);
        return validationHandler.failNowOrSucceedWith(failureView, () -> redirectToInviteExternalUserPage(form.getRole()));
    }

    private String redirectToInviteExternalUserPage(Role role) {
        return String.format("redirect:/admin/invite-external-user?role=%s", role.toString());
    }

    @GetMapping("/invite-user")
    public String inviteNewUser(Model model) {
        InviteUserForm form = new InviteUserForm();

        return doViewInviteNewUser(model, form, InviteUserView.INTERNAL_USER, Role.internalRoles());
    }

    @GetMapping("/invite-external-user")
    public String inviteNewExternalUser(@RequestParam(value = "role") Role role,
                                        Model model) {
        InviteUserForm form = new InviteUserForm();

        return doViewInviteNewUser(model, form, InviteUserView.EXTERNAL_USER, singleton(role));
    }

    private String doViewInviteNewUser(Model model, InviteUserForm form, InviteUserView type, Set<Role> roles) {

        InviteUserViewModel viewModel = inviteUserModelPopulator.populate(type, roles);

        model.addAttribute(FORM_ATTR_NAME, form);
        model.addAttribute(MODEL_ATTR_NAME, viewModel);

        return "admin/invite-new-user";
    }

    @PostMapping("/invite-user")
    public String saveUserInvite(Model model, @Validated({Default.class, Primary.class}) @ModelAttribute(FORM_ATTR_NAME) InviteUserForm form,
                                 @SuppressWarnings("unused") BindingResult bindingResult, ValidationHandler validationHandler) {

        Supplier<String> failureView = () -> doViewInviteNewUser(model, form, InviteUserView.INTERNAL_USER, Role.internalRoles());

        return saveInvite(form, validationHandler, failureView);
    }

    private String saveInvite(InviteUserForm form, ValidationHandler validationHandler, Supplier<String> failureView) {
        return validationHandler.failNowOrSucceedWith(failureView, () -> {

            InviteUserResource inviteUserResource = constructInviteUserResource(form);

            ServiceResult<Void> saveResult = inviteUserService.saveUserInvite(inviteUserResource);

            return handleSaveUserInviteErrors(saveResult, validationHandler).
                    failNowOrSucceedWith(failureView, () -> "redirect:/admin/users/pending");

        });
    }

    @PostMapping("/invite-external-user")
    public String saveExternalUserInvite(Model model, @Validated({Default.class, Primary.class}) @ModelAttribute(FORM_ATTR_NAME) InviteUserForm form,
                                         @SuppressWarnings("unused") BindingResult bindingResult, ValidationHandler validationHandler) {

        Supplier<String> failureView = () -> doViewInviteNewUser(model, form, InviteUserView.EXTERNAL_USER, singleton(form.getRole()));

        return saveInvite(form, validationHandler, failureView);
    }

    private ValidationHandler handleSaveUserInviteErrors(ServiceResult<Void> saveResult, ValidationHandler validationHandler) {
        return validationHandler.addAnyErrors(saveResult,
                mappingErrorKeyToField(USER_ROLE_INVITE_INVALID_EMAIL, "emailAddress"),
                mappingErrorKeyToField(KTA_USER_ROLE_INVITE_INVALID_EMAIL, "emailAddress"),
                fieldErrorsToFieldErrors(),
                asGlobalErrors());
    }

    private InviteUserResource constructInviteUserResource(InviteUserForm form) {

        UserResource invitedUser = new UserResource();
        invitedUser.setFirstName(form.getFirstName());
        invitedUser.setLastName(form.getLastName());
        invitedUser.setEmail(form.getEmailAddress());

        return new InviteUserResource(invitedUser, form.getOrganisation(), form.getRole());
    }
}