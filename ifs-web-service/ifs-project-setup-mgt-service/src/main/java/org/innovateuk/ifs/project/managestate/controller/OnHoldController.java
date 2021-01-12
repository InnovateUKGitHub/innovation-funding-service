package org.innovateuk.ifs.project.managestate.controller;

import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.project.managestate.form.OnHoldCommentForm;
import org.innovateuk.ifs.project.managestate.viewmodel.OnHoldViewModel;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.service.ProjectRestService;
import org.innovateuk.ifs.project.service.ProjectStateRestService;
import org.innovateuk.ifs.threads.resource.PostResource;
import org.innovateuk.ifs.threads.resource.ProjectStateCommentsResource;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.function.Supplier;

import static java.lang.String.format;
import static java.time.ZonedDateTime.now;
import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.project.resource.ProjectState.ON_HOLD;

@Controller
@RequestMapping("/competition/{competitionId}/project/{projectId}/on-hold-status")
@PreAuthorize("hasAuthority('project_finance')")
@SecuredBySpring(value = "MANAGE_PROJECT_STATE", description = "Only project finance users can manage project on hold state")
public class OnHoldController {

    @Autowired
    private ProjectRestService projectRestService;

    @Autowired
    private ProjectStateRestService projectStateRestService;

    @GetMapping
    public String viewOnHoldStatus(@ModelAttribute(value = "form", binding = false) OnHoldCommentForm form,
                                   BindingResult bindingResult,
                                   @PathVariable long projectId,
                                   @PathVariable long competitionId,
                                   Model model) {
        ProjectResource project = projectRestService.getProjectById(projectId).getSuccess();

        if (project.getProjectState() != ON_HOLD) {
            return redirectToManagePage(projectId, competitionId, false);
        }
        ProjectStateCommentsResource comments = projectStateRestService.findOpenComments(projectId).getSuccess();
        form.setCommentId(comments.id);
        model.addAttribute("model", new OnHoldViewModel(project, comments));
        return "project/on-hold-status";
    }

    @PostMapping
    public String resumeProject(@PathVariable long projectId,
                                @PathVariable long competitionId,
                                UserResource user) {
        projectStateRestService.resumeProject(projectId).getSuccess();
        return user.hasRole(Role.IFS_ADMINISTRATOR)
                ? redirectToManagePage(projectId, competitionId, true)
                : redirectToProjectDetails(projectId, competitionId, true);
    }

    @PostMapping(params = "add-comment")
    public String addComment(@Valid @ModelAttribute(value = "form") OnHoldCommentForm form,
                             BindingResult bindingResult,
                             ValidationHandler validationHandler,
                             @PathVariable long projectId,
                             @PathVariable long competitionId,
                             Model model,
                             UserResource user) {
        Supplier<String> failureView = () -> viewOnHoldStatus(form, bindingResult, projectId, competitionId, model);

        return validationHandler.failNowOrSucceedWith(failureView, () -> {
            validationHandler.addAnyErrors(projectStateRestService.addPost
                    (new PostResource(null, user, form.getDetails(), emptyList(), now()),
                            projectId,
                            form.getCommentId()));
            return validationHandler.failNowOrSucceedWith(failureView, () -> redirectToOnHoldStatusPage(projectId, competitionId));
        });
    }

    private String redirectToOnHoldStatusPage(long projectId,
                                        long competitionId) {
        return format("redirect:/competition/%d/project/%d/on-hold-status", competitionId, projectId);
    }

    private String redirectToManagePage(long projectId,
                                        long competitionId, boolean resumedFromOnHold) {
        return format("redirect:/competition/%d/project/%d/manage-status?resumedFromOnHold=%s", competitionId, projectId, resumedFromOnHold);
    }

    private String redirectToProjectDetails(long projectId,
                                        long competitionId, boolean resumedFromOnHold) {
        return format("redirect:/competition/%d/project/%d/details?resumedFromOnHold=%s", competitionId, projectId, resumedFromOnHold);
    }
}
