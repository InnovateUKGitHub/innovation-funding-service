package org.innovateuk.ifs.management.admin.controller;

import org.innovateuk.ifs.management.admin.form.InviteUserForm;
import org.innovateuk.ifs.management.admin.form.validation.Primary;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.invite.resource.InviteUserResource;
import org.innovateuk.ifs.management.invite.service.InviteUserService;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.groups.Default;
import java.util.function.Supplier;

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

    @Autowired
    private InviteUserService inviteUserService;

    @GetMapping("/invite-user")
    public String inviteNewUser(Model model) {
        return viewInviteNewUser(model);
    }

    private static String viewInviteNewUser(Model model) {
        InviteUserForm form = new InviteUserForm();
        model.addAttribute(FORM_ATTR_NAME, form);

        return "admin/invite-new-user";

    }

    @PostMapping("/invite-user")
    public String saveUserInvite(@Validated({Default.class, Primary.class}) @ModelAttribute(FORM_ATTR_NAME) InviteUserForm form,
                               @SuppressWarnings("unused") BindingResult bindingResult, ValidationHandler validationHandler) {

        Supplier<String> failureView = () -> "admin/invite-new-user";

        return validationHandler.failNowOrSucceedWith(failureView, () -> {

            InviteUserResource inviteUserResource = constructInviteUserResource(form);

            ServiceResult<Void> saveResult = inviteUserService.saveUserInvite(inviteUserResource);

            return handleSaveUserInviteErrors(saveResult, validationHandler).
                    failNowOrSucceedWith(failureView, () -> "redirect:/admin/users/pending");

        });
    }

    private ValidationHandler handleSaveUserInviteErrors(ServiceResult<Void> saveResult, ValidationHandler validationHandler) {
        return validationHandler.addAnyErrors(saveResult, mappingErrorKeyToField(USER_ROLE_INVITE_INVALID_EMAIL, "emailAddress"), fieldErrorsToFieldErrors(), asGlobalErrors());
    }

    private InviteUserResource constructInviteUserResource(InviteUserForm form) {

        UserResource invitedUser = new UserResource();
        invitedUser.setFirstName(form.getFirstName());
        invitedUser.setLastName(form.getLastName());
        invitedUser.setEmail(form.getEmailAddress());

        return new InviteUserResource(invitedUser, form.getRole());
    }
}