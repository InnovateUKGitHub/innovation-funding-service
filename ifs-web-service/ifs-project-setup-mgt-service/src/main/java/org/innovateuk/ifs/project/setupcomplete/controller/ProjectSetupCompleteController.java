package org.innovateuk.ifs.project.setupcomplete.controller;

import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.service.ProjectRestService;
import org.innovateuk.ifs.project.service.ProjectStateRestService;
import org.innovateuk.ifs.project.setupcomplete.form.ProjectSetupCompleteForm;
import org.innovateuk.ifs.project.setupcomplete.viewmodel.ProjectSetupCompleteViewModel;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.function.Supplier;

import static java.lang.String.format;

@Controller
@RequestMapping("/competition/{competitionId}/project/{projectId}/setup-complete")
@PreAuthorize("hasAuthority('project_finance')")
@SecuredBySpring(value = "PROJECT_SETUP_COMPLETE", description = "Project finance can view the setup complete page and make changes")
public class ProjectSetupCompleteController {

    @Autowired
    private ProjectRestService projectRestService;

    @Autowired
    private ProjectStateRestService projectStateRestService;

    @GetMapping
    public String viewSetupCompletePage(@ModelAttribute(name = "form", binding = false) ProjectSetupCompleteForm form,
                                        @PathVariable long projectId,
                                        @PathVariable long competitionId,
                                        Model model,
                                        UserResource user) {
        ProjectResource project = projectRestService.getProjectById(projectId).getSuccess();
        model.addAttribute("model", new ProjectSetupCompleteViewModel(project));
        return "project/setup-complete";
    }

    @PostMapping
    public String saveProjectState(@ModelAttribute(name = "form") ProjectSetupCompleteForm form,
                                    BindingResult bindingResult,
                                    ValidationHandler validationHandler,
                                    @PathVariable long projectId,
                                    @PathVariable long competitionId,
                                    Model model,
                                    UserResource user) {
        validate(form, bindingResult);
        Supplier<String> failureView = () -> viewSetupCompletePage(form, projectId, competitionId, model, user);
        return validationHandler.failNowOrSucceedWith(failureView, () -> {
            if (form.getSuccessful()) {
                validationHandler.addAnyErrors(projectStateRestService.markAsSuccessful(projectId));
            } else {
                validationHandler.addAnyErrors(projectStateRestService.markAsUnsuccessful(projectId));
            }
            return validationHandler.failNowOrSucceedWith(failureView, () -> format("redirect:/competition/%d/status", competitionId));
        });
    }

    private void validate(ProjectSetupCompleteForm form, BindingResult bindingResult) {
        if (form.getSuccessful() == null) {
            bindingResult.rejectValue("successful", "validation.field.must.not.be.blank");
        } else if (Boolean.TRUE.equals(form.getSuccessful())) {
            if (!form.isSuccessfulConfirmation()) {
                bindingResult.rejectValue("successfulConfirmation", "validation.field.must.not.be.blank");
            }
        } else if (Boolean.FALSE.equals(form.getSuccessful())) {
            if (!form.isUnsuccessfulConfirmation()) {
                bindingResult.rejectValue("unsuccessfulConfirmation", "validation.field.must.not.be.blank");
            }
        }
    }
}
