package org.innovateuk.ifs.admin.controller;

import org.innovateuk.ifs.admin.form.InviteUserForm;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.controller.ValidationHandler;
//import org.innovateuk.ifs.invite.resource.InviteUserResource;
import org.innovateuk.ifs.invite.service.InviteUserService;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.function.Supplier;

import static org.innovateuk.ifs.controller.ErrorToObjectErrorConverterFactory.asGlobalErrors;
import static org.innovateuk.ifs.controller.ErrorToObjectErrorConverterFactory.fieldErrorsToFieldErrors;

/**
 * This controller will handle all CRUD requests related to users managed by IFS Administrators.
 */
@Controller
@RequestMapping("/admin")
@PreAuthorize("hasAnyAuthority('ifs_administrator')")
public class InviteUserController {

    private static final String FORM_ATTR_NAME = "form";

/*    @Autowired
    private InviteUserService inviteUserService;*/

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
    public String saveUserInvite(Model model,
                               HttpServletRequest request,
                               @Valid @ModelAttribute(FORM_ATTR_NAME) InviteUserForm form,
                               @SuppressWarnings("unused") BindingResult bindingResult, ValidationHandler validationHandler,
                               UserResource loggedInUser) {


        //Supplier<String> failureView = () -> "admin/invite-new-user";

/*        return validationHandler.failNowOrSucceedWith(failureView, () -> {

            InviteUserResource inviteUserResource = constructInviteUserResource(form);

            ServiceResult<Void> saveResult = inviteUserService.saveUserInvite(inviteUserResource);

            return validationHandler.addAnyErrors(saveResult, fieldErrorsToFieldErrors(), asGlobalErrors()).
                    failNowOrSucceedWith(failureView, () -> "admin/users");

        });*/

        return "admin/users";

    }

/*    private InviteUserResource constructInviteUserResource(InviteUserForm form) {

        UserResource invitedUser = new UserResource();
        invitedUser.setFirstName(form.getFirstName());
        invitedUser.setLastName(form.getLastName());
        invitedUser.setEmail(form.getEmailAddress());

        InviteUserResource inviteUserResource = new InviteUserResource(invitedUser, form.getRole());

        return inviteUserResource;
    }*/
}