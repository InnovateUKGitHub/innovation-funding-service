package org.innovateuk.ifs.project.monitoringofficer.controller;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.project.monitoring.resource.MonitoringOfficerResource;
import org.innovateuk.ifs.project.monitoring.service.MonitoringOfficerRestService;
import org.innovateuk.ifs.project.monitoringofficer.form.MonitoringOfficerAssignProjectForm;
import org.innovateuk.ifs.project.monitoringofficer.form.MonitoringOfficerViewAllForm;
import org.innovateuk.ifs.project.monitoringofficer.form.MonitoringOfficerSearchByEmailForm;
import org.innovateuk.ifs.project.monitoringofficer.populator.MonitoringOfficerProjectsViewModelPopulator;
import org.innovateuk.ifs.project.monitoringofficer.populator.MonitoringOfficerViewAllViewModelPopulator;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import static java.lang.String.format;
import static org.innovateuk.ifs.user.resource.Role.MONITORING_OFFICER;

@Controller
@RequestMapping("/monitoring-officer")
@SecuredBySpring(value = "Controller",
        description = "Comp Admin, Project Finance and IFS admins can view and assign projects to Monitoring Officers",
        securedType = MonitoringOfficerController.class)
@PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance', 'ifs_administrator')")
public class MonitoringOfficerController {

    private static final String FORM = "form";
    private static final String MODEL = "model";

    @Autowired
    private MonitoringOfficerProjectsViewModelPopulator modelPopulator;

    @Autowired
    private MonitoringOfficerViewAllViewModelPopulator monitoringOfficerViewAllViewModelPopulator;

    @Autowired
    private MonitoringOfficerRestService projectMonitoringOfficerRestService;

    @Autowired
    private UserService userService;

    @GetMapping("/search-by-email")
    public String searchByEmail(Model model) {
        model.addAttribute(FORM, new MonitoringOfficerSearchByEmailForm());
        return "project/monitoring-officer/search-by-email";
    }

    @PostMapping("/create")
    public String create(@Valid @ModelAttribute(FORM) MonitoringOfficerSearchByEmailForm form,
                         BindingResult bindingResult,
                         ValidationHandler validationHandler,
                         Model model) {
        if (validationHandler.hasErrors()) {
            return "project/monitoring-officer/search-by-email";
        }
        Optional<UserResource> userByEmail = userService.findUserByEmail(form.getEmailAddress());
        if (userByEmail.isPresent()) {
            UserResource userResource = userByEmail.get();
            if (userResource.hasRole(MONITORING_OFFICER)) {
                return monitoringOfficerProjectsRedirect(userResource.getId());
            }
            return "project/monitoring-officer/assign-role";
        }
        return "project/monitoring-officer/create";
    }

    @GetMapping("/{monitoringOfficerId}/projects")
    public String viewProjects(@PathVariable long monitoringOfficerId, Model model) {
        model.addAttribute(MODEL, modelPopulator.populate(monitoringOfficerId));
        model.addAttribute(FORM, new MonitoringOfficerAssignProjectForm());
        return "project/monitoring-officer-projects";
    }

    @PostMapping("/{monitoringOfficerId}/assign")
    public String assignProject(@PathVariable long monitoringOfficerId,
                                @Valid @ModelAttribute(FORM) MonitoringOfficerAssignProjectForm form,
                                BindingResult bindingResult,
                                ValidationHandler validationHandler,
                                Model model,
                                UserResource user) {

        Supplier<String> failureView = () -> {
            model.addAttribute(MODEL, modelPopulator.populate(monitoringOfficerId));
            model.addAttribute(FORM, form);
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
    public String viewAll(Model model) {
        List<MonitoringOfficerResource> monitoringOfficers = projectMonitoringOfficerRestService.findAll().getSuccess();
        model.addAttribute(MODEL, monitoringOfficerViewAllViewModelPopulator.populate(monitoringOfficers));
        model.addAttribute(FORM, new MonitoringOfficerViewAllForm());
        return "project/monitoring-officer-view-all";
    }

    @GetMapping("/view-monitoring-officer")
    public String redirectToMoProjectPage(@ModelAttribute("form") MonitoringOfficerViewAllForm form) {
        // required to allow auto complete to send back the data about the selection
        if(form == null || form.getUserId() == null) {
            return "redirect://monitoring-officer/view-all";
        }

        return monitoringOfficerProjectsRedirect(form.getUserId());
    }

    private static String monitoringOfficerProjectsRedirect(long monitoringOfficerId) {
        return format("redirect:/monitoring-officer/%s/projects", monitoringOfficerId);
    }
}