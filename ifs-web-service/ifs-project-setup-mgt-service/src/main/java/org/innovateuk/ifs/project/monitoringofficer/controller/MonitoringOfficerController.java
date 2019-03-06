package org.innovateuk.ifs.project.monitoringofficer.controller;


import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.project.monitoring.service.ProjectMonitoringOfficerRestService;
import org.innovateuk.ifs.project.monitoringofficer.form.MonitoringOfficerAssignProjectForm;
import org.innovateuk.ifs.project.monitoringofficer.populator.MonitoringOfficerProjectsViewModelPopulator;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.function.Supplier;

import static java.lang.String.format;

@Controller
@RequestMapping("/monitoring-officer/{monitoringOfficerId}")
@SecuredBySpring(value = "Controller",
        description = "Comp Admin and Project Finance can view and assign projects to Monitoring Officers",
        securedType = MonitoringOfficerController.class)
@PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance')")
public class MonitoringOfficerController {

    // TODO rename to ProjectMonitoringOfficerController? or MontoringOfficerAssignmentController?

    private static final String FORM_ATTR_NAME = "form";

    private final MonitoringOfficerProjectsViewModelPopulator modelPopulator;

    private final ProjectMonitoringOfficerRestService projectMonitoringOfficerRestService;

    public MonitoringOfficerController(MonitoringOfficerProjectsViewModelPopulator modelPopulator,
                                       ProjectMonitoringOfficerRestService projectMonitoringOfficerRestService) {
        this.modelPopulator = modelPopulator;
        this.projectMonitoringOfficerRestService = projectMonitoringOfficerRestService;
    }

    @GetMapping("/projects")
    public String viewProjects(@PathVariable long monitoringOfficerId, Model model) {
        model.addAttribute("model", modelPopulator.populate(monitoringOfficerId));
        model.addAttribute(FORM_ATTR_NAME, new MonitoringOfficerAssignProjectForm());
        return "project/monitoring-officer-projects";
    }

    @GetMapping("/unassign/{projectId}")
    public String unassignProject(@PathVariable long monitoringOfficerId,
                                  @PathVariable long projectId) {
        return projectMonitoringOfficerRestService
                .unassignMonitoringOfficerFromProject(monitoringOfficerId, projectId)
                .andOnSuccessReturn(() -> monitoringOfficerProjectsRedirect(monitoringOfficerId))
                .getSuccess();
    }

    @PostMapping("/assign")
    public String assignProject(@PathVariable long monitoringOfficerId,
                                @Valid @ModelAttribute(FORM_ATTR_NAME) MonitoringOfficerAssignProjectForm form,
                                BindingResult bindingResult,
                                ValidationHandler validationHandler,
                                Model model,
                                UserResource user) {

        Supplier<String> failureView = () -> viewProjects(monitoringOfficerId, model);
        RestResult<Void> result = projectMonitoringOfficerRestService.assignMonitoringOfficerToProject(monitoringOfficerId, form.getProjectNumber());

        return validationHandler
                .addAnyErrors(result)
                .failNowOrSucceedWith(failureView, () -> monitoringOfficerProjectsRedirect(monitoringOfficerId));
    }

    private static String monitoringOfficerProjectsRedirect(long monitoringOfficerId) {
        return format("redirect:/monitoring-officer/%s/projects", monitoringOfficerId);
    }
}