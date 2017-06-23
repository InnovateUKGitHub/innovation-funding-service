package org.innovateuk.ifs.admin.controller;

import org.innovateuk.ifs.admin.form.InviteUserForm;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.user.resource.RoleResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.stereotype.Controller;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;
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

    @GetMapping("/invite/user")
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

/*    public void saveUserInvite(Model model,
                               HttpServletRequest request,
                               @Valid @ModelAttribute(FORM_ATTR_NAME) InviteUserForm form,
                               @SuppressWarnings("unused") BindingResult bindingResult, ValidationHandler validationHandler,
                               UserResource loggedInUser) {


        Supplier<String> failureView = () -> "admin/invite-new-user";

        return validationHandler.failNowOrSucceedWith(failureView, () -> {

            UserResource inviteUser = constructUserResource(form);
            // Service call, pass the user resource and the role type from the form

        });

    }*/

    private UserResource constructUserResource(InviteUserForm form) {

        UserResource inviteUser = new UserResource();
        inviteUser.setFirstName(form.getFirstName());
        inviteUser.setLastName(form.getLastName());
        inviteUser.setEmail(form.getEmailAddress());

        return inviteUser;
    }










































}