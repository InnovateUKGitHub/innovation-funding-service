package org.innovateuk.ifs.project.monitoringofficer.controller;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.project.monitoring.resource.ProjectMonitoringOfficerResource;
import org.innovateuk.ifs.project.monitoring.service.ProjectMonitoringOfficerRestService;
import org.innovateuk.ifs.project.monitoringofficer.form.MonitoringOfficerAssignProjectForm;
import org.innovateuk.ifs.project.monitoringofficer.form.MonitoringOfficerViewForm;
import org.innovateuk.ifs.project.monitoringofficer.populator.MonitoringOfficerProjectsViewModelPopulator;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.function.Supplier;

import static java.lang.String.format;

@Controller
@RequestMapping("/monitoring-officer")
@SecuredBySpring(value = "Controller",
        description = "Comp Admin and Project Finance can view and assign projects to Monitoring Officers",
        securedType = MonitoringOfficerController.class)
@PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance', 'ifs_administrator')")
public class MonitoringOfficerController {

    private static final String FORM_ATTR_NAME = "form";

    @Autowired
    private MonitoringOfficerProjectsViewModelPopulator modelPopulator;

    @Autowired
    private ProjectMonitoringOfficerRestService projectMonitoringOfficerRestService;


    @GetMapping("/{monitoringOfficerId}/projects")
    public String viewProjects(@PathVariable long monitoringOfficerId, Model model) {
        model.addAttribute("model", modelPopulator.populate(monitoringOfficerId));
        model.addAttribute(FORM_ATTR_NAME, new MonitoringOfficerAssignProjectForm());
        return "project/monitoring-officer-projects";
    }

    @PostMapping("/{monitoringOfficerId}/assign")
    public String assignProject(@PathVariable long monitoringOfficerId,
                                @Valid @ModelAttribute(FORM_ATTR_NAME) MonitoringOfficerAssignProjectForm form,
                                BindingResult bindingResult,
                                ValidationHandler validationHandler,
                                Model model,
                                UserResource user) {

        Supplier<String> failureView = () -> {
            model.addAttribute("model", modelPopulator.populate(monitoringOfficerId));
            model.addAttribute(FORM_ATTR_NAME, form);
            return "project/monitoring-officer-projects";
        };

        return validationHandler
                .failNowOrSucceedWith(failureView, () -> {
                    RestResult<Void> result = projectMonitoringOfficerRestService.assignMonitoringOfficerToProject(monitoringOfficerId, form.getProjectId());
                    return validationHandler
                            .addAnyErrors(result)
                            .failNowOrSucceedWith(failureView,
                                    () ->  monitoringOfficerProjectsRedirect(monitoringOfficerId));
                });
    }

    @GetMapping("/{monitoringOfficerId}/unassign/{projectId}")
    public String unassignProject(@PathVariable long monitoringOfficerId,
                                  @PathVariable long projectId) {
        return projectMonitoringOfficerRestService
                .unassignMonitoringOfficerFromProject(monitoringOfficerId, projectId)
                .andOnSuccessReturn(() -> monitoringOfficerProjectsRedirect(monitoringOfficerId))
                .getSuccess();
    }

    @GetMapping("/view-all")
    public String viewAll(Model model,
                          MonitoringOfficerViewForm form) {
        List<ProjectMonitoringOfficerResource> monitoringOfficers = projectMonitoringOfficerRestService.findAll().getSuccess();
        model.addAttribute("monitoringOfficers", monitoringOfficers);
        return "project/monitoring-officer-view-all";
    }

    @PostMapping("/view-monitoring-officer")
    public String redirectToMoProjectPage(@ModelAttribute("form") MonitoringOfficerViewForm form) {
        // required to allow auto complete to send back the data about the selection
        long moId = form.getUserId();
        return monitoringOfficerProjectsRedirect(form.getUserId());
    }

    private static String monitoringOfficerProjectsRedirect(long monitoringOfficerId) {
        return format("redirect:/monitoring-officer/%s/projects", monitoringOfficerId);
    }
}