package org.innovateuk.ifs.admin.controller;

import org.innovateuk.ifs.admin.form.InviteUserForm;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.stereotype.Controller;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

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

}