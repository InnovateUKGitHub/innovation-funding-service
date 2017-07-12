package org.innovateuk.ifs.admin.controller;

import org.innovateuk.ifs.admin.form.InviteUserForm;
import org.innovateuk.ifs.admin.form.validation.Primary;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.invite.resource.InviteUserResource;
import org.innovateuk.ifs.invite.service.InviteUserService;
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

import javax.servlet.http.HttpServletRequest;
import javax.validation.groups.Default;
import java.util.function.Supplier;

import static org.innovateuk.ifs.controller.ErrorToObjectErrorConverterFactory.asGlobalErrors;
import static org.innovateuk.ifs.controller.ErrorToObjectErrorConverterFactory.fieldErrorsToFieldErrors;

/**
 * Controller for handling requests related to invitation of new users by the IFS Administrator
 */
@Controller
@RequestMapping("/admin")
@PreAuthorize("hasAuthority('ifs_administrator')")
public class InviteUserController {

    private static final String FORM_ATTR_NAME = "form";

    @Autowired
    private InviteUserService inviteUserService;

    @GetMapping("/invite-user")
    public String inviteNewUser(Model model,
                                HttpServletRequest request,
                                UserResource loggedInUser) {

        return viewInviteNewUser(model);
    }

    private String viewInviteNewUser(Model model) {
        InviteUserForm form = new InviteUserForm();
        model.addAttribute(FORM_ATTR_NAME, form);

        return "admin/invite-new-user";

    }

    @PostMapping("/invite-user")
    public String saveUserInvite(@Validated({Default.class, Primary.class}) @ModelAttribute(FORM_ATTR_NAME) InviteUserForm form,
                               @SuppressWarnings("unused") BindingResult bindingResult, ValidationHandler validationHandler) {
        System.out.println("IN SAVE USER INVITE");

        Supplier<String> failureView = () -> "admin/invite-new-user";

        return validationHandler.failNowOrSucceedWith(failureView, () -> {

            InviteUserResource inviteUserResource = constructInviteUserResource(form);

            ServiceResult<Void> saveResult = inviteUserService.saveUserInvite(inviteUserResource);

            return validationHandler.addAnyErrors(saveResult, fieldErrorsToFieldErrors(), asGlobalErrors()).
                    failNowOrSucceedWith(failureView, () -> "redirect:/admin/users/active");

        });
    }

    private InviteUserResource constructInviteUserResource(InviteUserForm form) {

        UserResource invitedUser = new UserResource();
        invitedUser.setFirstName(form.getFirstName());
        invitedUser.setLastName(form.getLastName());
        invitedUser.setEmail(form.getEmailAddress());

        InviteUserResource inviteUserResource = new InviteUserResource(invitedUser, form.getRole());

        return inviteUserResource;
    }
}