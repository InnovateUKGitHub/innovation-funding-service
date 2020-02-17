package org.innovateuk.ifs.management.admin.controller;

import org.innovateuk.ifs.assessment.service.AssessorRestService;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.management.admin.form.ChangeRoleProfileForm;
import org.innovateuk.ifs.management.admin.viewmodel.RoleProfileViewModel;
import org.innovateuk.ifs.user.resource.RoleProfileState;
import org.innovateuk.ifs.user.resource.RoleProfileStatusResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.RoleProfileStatusRestService;
import org.innovateuk.ifs.user.service.UserRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.util.StringUtils;

import javax.validation.Valid;
import java.util.function.Supplier;

import static java.lang.String.format;
import static org.innovateuk.ifs.user.resource.ProfileRole.ASSESSOR;
import static org.innovateuk.ifs.user.resource.RoleProfileState.*;

@Controller
@RequestMapping("/admin/user/{userId}/role-profile")
@SecuredBySpring(value = "Controller", description = "Project finance, competition admin, support, innovation lead " +
        "can view assessors role profile details",
        securedType = AssessorManagementController.class)
@PreAuthorize("hasAnyAuthority('project_finance','comp_admin')")
public class AssessorManagementController {

    @Autowired
    private RoleProfileStatusRestService roleProfileStatusRestService;

    @Autowired
    private UserRestService userRestService;

    @Autowired
    private AssessorRestService assessorRestService;

    @GetMapping
    public String viewUser(@PathVariable long userId,
                           Model model) {

        RoleProfileStatusResource roleProfileStatusResource =
                roleProfileStatusRestService.findByUserIdAndProfileRole(userId, ASSESSOR).getSuccess();

        UserResource modifiedUser = userRestService.retrieveUserById(roleProfileStatusResource.getModifiedBy()).getSuccess();

        model.addAttribute("model", new RoleProfileViewModel(roleProfileStatusResource, modifiedUser, hasApplicationsAssigned(userId)));

        return "admin/role-profile-details";

    }

    @GetMapping("/status")
    public String viewStatus(@ModelAttribute(name = "form") ChangeRoleProfileForm form,
                             @PathVariable long userId,
                             Model model) {

        if (form.getRoleProfileState() == null) {
            form.setRoleProfileState(ACTIVE.toString());
        }

        model.addAttribute("userId", userId);

        return "admin/change-status";
    }

    @PostMapping("/status")
    public String UpdateStatus(@ModelAttribute(name = "form") @Valid ChangeRoleProfileForm form,
                               BindingResult bindingResult,
                               @PathVariable long userId,
                               Model model,
                               ValidationHandler validationHandler) {

        model.addAttribute("userId", userId);

        Supplier<String> failureView = () -> viewStatus(form, userId, model);
        Supplier<String> successView = () -> format("redirect:/admin/user/%d/active", userId);

        return validationHandler.failNowOrSucceedWith(failureView, () -> {
            validateForm(bindingResult, form);
            return validationHandler.failNowOrSucceedWith(failureView, () -> {
                RoleProfileStatusResource roleProfileStatusResource = createRoleProfileStatusResource(userId, form);
                RestResult<Void> result = roleProfileStatusRestService.updateUserStatus(userId, roleProfileStatusResource);
                return validationHandler.addAnyErrors(result).failNowOrSucceedWith(failureView, successView);
            });
        });
    }

    private RoleProfileStatusResource createRoleProfileStatusResource(long userId, ChangeRoleProfileForm form) {
        RoleProfileState roleProfileState = getRoleProfileStateFromString(form.getRoleProfileState());
        if (UNAVAILABLE.equals(roleProfileState)) {
            return new RoleProfileStatusResource(userId, ASSESSOR, roleProfileState, form.getUnavailableReason());
        } else  if (DISABLED.equals(roleProfileState)) {
            return new RoleProfileStatusResource(userId, ASSESSOR, roleProfileState, form.getDisabledReason());
        } else {
            return new RoleProfileStatusResource(userId, ASSESSOR, roleProfileState, "");
        }
    }

    private boolean hasApplicationsAssigned(long userId) {
        return assessorRestService.hasApplicationsAssigned(userId).getSuccess();
    }

    private void validateForm(BindingResult bindingResult, ChangeRoleProfileForm form) {
        if (UNAVAILABLE.equals(getRoleProfileStateFromString(form.getRoleProfileState())) && StringUtils.isEmpty(form.getUnavailableReason())) {
            bindingResult.addError(new FieldError("form", "unavailableReason", "Enter some text."));
        }

        if (DISABLED.equals(getRoleProfileStateFromString(form.getRoleProfileState())) && StringUtils.isEmpty(form.getDisabledReason())) {
            bindingResult.addError(new FieldError("form", "disabledReason", "Enter some text."));
        }
    }
}
