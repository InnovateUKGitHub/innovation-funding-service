package org.innovateuk.ifs.management.externalrole.controller;

import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.management.externalrole.form.ExternalRoleForm;
import org.innovateuk.ifs.management.externalrole.viewmodel.ExternalRoleViewModel;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.UserRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.function.Supplier;

import static org.innovateuk.ifs.commons.error.CommonFailureKeys.USER_ADD_ROLE_INVALID_EMAIL;
import static org.innovateuk.ifs.controller.ErrorToObjectErrorConverterFactory.*;

@Controller
@RequestMapping("/admin/user/{userId}/external-role")
@SecuredBySpring(value = "Controller", description = "Admins can add an external role to existing users",
        securedType = ExternalRoleController.class)
@PreAuthorize("hasAuthority('ifs_administrator')")
public class ExternalRoleController {

    @Autowired
    private UserRestService userRestService;

    @GetMapping
    public String viewUser(@PathVariable long userId,
                           @ModelAttribute(name = "form") ExternalRoleForm form,
                           Model model) {

        UserResource user = userRestService.retrieveUserById(userId).getSuccess();

        model.addAttribute("model", new ExternalRoleViewModel(userId, user.getName(), user.getEmail()));
        return "externalrole/external-role";
    }

    @PostMapping
    public String addRoleToUser(@PathVariable long userId,
                                @ModelAttribute(name = "form") ExternalRoleForm form,
                                Model model,
                                ValidationHandler validationHandler) {

        form.setRole(Role.KNOWLEDGE_TRANSFER_ADVISOR);

        Supplier<String> failureView = () -> viewUser(userId, form, model);
        return validationHandler.failNowOrSucceedWith(failureView, () -> {
            validationHandler.addAnyErrors(userRestService.grantRole(userId, form.getRole()), mappingErrorKeyToField(USER_ADD_ROLE_INVALID_EMAIL, "email"), fieldErrorsToFieldErrors(), asGlobalErrors());
            return validationHandler.failNowOrSucceedWith(failureView,
                    () -> redirectToUserPage(userId));
        });
    }

    private String redirectToUserPage(long userId) {
        return String.format("redirect:/admin/user/%d/active", userId);
    }
}
