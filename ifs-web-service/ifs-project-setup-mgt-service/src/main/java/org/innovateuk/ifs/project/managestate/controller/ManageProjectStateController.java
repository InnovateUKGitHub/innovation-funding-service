package org.innovateuk.ifs.project.managestate.controller;

import org.innovateuk.ifs.commons.exception.IFSRuntimeException;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.project.managestate.form.ManageProjectStateForm;
import org.innovateuk.ifs.project.managestate.viewmodel.ManageProjectStateViewModel;
import org.innovateuk.ifs.project.service.ProjectRestService;
import org.innovateuk.ifs.project.service.ProjectStateRestService;
import org.innovateuk.ifs.project.state.OnHoldReasonResource;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.function.Supplier;

import static java.lang.Boolean.TRUE;
import static java.lang.String.format;
import static org.apache.commons.lang.StringUtils.isBlank;

@Controller
@RequestMapping("/competition/{competitionId}/project/{projectId}/manage-status")
@PreAuthorize("hasAuthority('project_finance')")
@SecuredBySpring(value = "MANAGE_PROJECT_STATE", description = "Only project finance users can manage project state")
public class ManageProjectStateController {

    @Autowired
    private ProjectRestService projectRestService;

    @Autowired
    private ProjectStateRestService projectStateRestService;

    @GetMapping
    public String manageProjectState(@ModelAttribute(value = "form", binding = false) ManageProjectStateForm form,
                                     BindingResult result,
                                     @PathVariable long projectId,
                                     @RequestParam(required = false, defaultValue = "false") boolean resumedFromOnHold,
                                     Model model,
                                     UserResource user) {
        model.addAttribute("resumedFromOnHold", resumedFromOnHold);
        model.addAttribute("model",
                new ManageProjectStateViewModel(projectRestService.getProjectById(projectId).getSuccess(),
                        user.hasRole(Role.IFS_ADMINISTRATOR)));
        return "project/manage-project-state";
    }

    @PostMapping
    public String setProjectState(@Valid @ModelAttribute(value = "form") ManageProjectStateForm form,
                                  BindingResult result,
                                  ValidationHandler validationHandler,
                                  @PathVariable long projectId,
                                  @PathVariable long competitionId,
                                  Model model,
                                  UserResource user) {
        validate(form, result);
        Supplier<String> failureView = () -> manageProjectState(form, result, projectId, false, model, user);
        Supplier<String> successView = () -> format("redirect:/competition/%d/project/%d/manage-status", competitionId, projectId);

        return validationHandler.failNowOrSucceedWith(failureView, () -> {
            validationHandler.addAnyErrors(updateProjectState(form, projectId));
            return validationHandler.failNowOrSucceedWith(failureView, successView);
        });
    }

    private RestResult<Void> updateProjectState(ManageProjectStateForm form, long projectId) {
        switch (form.getState()) {
            case WITHDRAWN:
                return projectStateRestService.withdrawProject(projectId);
            case HANDLED_OFFLINE:
                return projectStateRestService.handleProjectOffline(projectId);
            case COMPLETED_OFFLINE:
                return projectStateRestService.completeProjectOffline(projectId);
            case ON_HOLD:
                return projectStateRestService.putProjectOnHold(projectId, new OnHoldReasonResource(form.getOnHoldReason(), form.getOnHoldDetails()));
        }
        throw new IFSRuntimeException("Unknown project state");
    }

    private void validate(@Valid ManageProjectStateForm form, BindingResult result) {
        if (result.hasFieldErrors("state")) {
            return;
        }

        if (form.isHandledOffline() && !TRUE.equals(form.getConfirmationOffline())) {
            result.rejectValue("confirmationOffline", "validation.field.must.not.be.blank");
            return;
        }

        if (form.isWithdrawn() && !TRUE.equals(form.getConfirmationWithdrawn())) {
            result.rejectValue("confirmationWithdrawn", "validation.field.must.not.be.blank");
            return;
        }

        if (form.isCompletedOffline() && !TRUE.equals(form.getConfirmationCompleteOffline())) {
            result.rejectValue("confirmationCompleteOffline", "validation.field.must.not.be.blank");
            return;
        }

        if (form.isOnHold()) {
            if (isBlank(form.getOnHoldReason())) {
                result.rejectValue("onHoldReason", "validation.manage.project.on.hold.reason.required");
            }
            if (isBlank(form.getOnHoldDetails())) {
                result.rejectValue("onHoldDetails", "validation.manage.project.on.hold.details.required");
            }
        }
    }
}
