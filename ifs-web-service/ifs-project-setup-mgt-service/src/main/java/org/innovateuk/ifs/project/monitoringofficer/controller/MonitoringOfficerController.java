package org.innovateuk.ifs.project.monitoringofficer.controller;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.service.MonitoringOfficerRegistrationRestService;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.invite.resource.MonitoringOfficerCreateResource;
import org.innovateuk.ifs.project.monitoring.service.MonitoringOfficerRestService;
import org.innovateuk.ifs.project.monitoringofficer.form.MonitoringOfficerAssignProjectForm;
import org.innovateuk.ifs.project.monitoringofficer.form.MonitoringOfficerAssignRoleForm;
import org.innovateuk.ifs.project.monitoringofficer.form.MonitoringOfficerCreateForm;
import org.innovateuk.ifs.project.monitoringofficer.form.MonitoringOfficerSearchByEmailForm;
import org.innovateuk.ifs.project.monitoringofficer.form.MonitoringOfficerViewAllForm;
import org.innovateuk.ifs.project.monitoringofficer.populator.MonitoringOfficerAssignRoleViewModelPopulator;
import org.innovateuk.ifs.project.monitoringofficer.populator.MonitoringOfficerProjectsViewModelPopulator;
import org.innovateuk.ifs.project.monitoringofficer.populator.MonitoringOfficerViewAllViewModelPopulator;
import org.innovateuk.ifs.project.monitoringofficer.viewmodel.MonitoringOfficerAssignRoleViewModel;
import org.innovateuk.ifs.user.resource.Title;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.UserRestService;
import org.innovateuk.ifs.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import java.util.Optional;
import java.util.function.Supplier;

import static com.google.common.base.Strings.isNullOrEmpty;
import static java.lang.String.format;
import static java.util.Optional.ofNullable;
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
    private MonitoringOfficerAssignRoleViewModelPopulator monitoringOfficerAssignRoleViewModelPopulator;

    @Autowired
    private MonitoringOfficerViewAllViewModelPopulator monitoringOfficerViewAllViewModelPopulator;

    @Autowired
    private MonitoringOfficerRestService projectMonitoringOfficerRestService;

    @Autowired
    private MonitoringOfficerRegistrationRestService monitoringOfficerRegistrationRestService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRestService userRestService;

    @GetMapping("/search-by-email")
    public String searchByEmail(Model model) {
        model.addAttribute(FORM, new MonitoringOfficerSearchByEmailForm());
        return "project/monitoring-officer/search-by-email";
    }

    @PostMapping("/search-by-email")
    public String searchByEmail(@Valid @ModelAttribute(FORM) MonitoringOfficerSearchByEmailForm form,
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
            return format("redirect:/monitoring-officer/%s/assign-role", userResource.getId());
        }
        return "redirect:/monitoring-officer/create/" + form.getEmailAddress();
    }

    @GetMapping("/{userId}/assign-role")
    public String assignRole(@PathVariable long userId,
                             Model model) {
        UserResource userResource = userRestService.retrieveUserById(userId).getSuccess();
        if (!userResource.hasRole(MONITORING_OFFICER)) {
            MonitoringOfficerAssignRoleViewModel viewModel = monitoringOfficerAssignRoleViewModelPopulator.populate(userId);
            model.addAttribute(MODEL, viewModel);
            model.addAttribute(FORM, new MonitoringOfficerAssignRoleForm());
            if (isNullOrEmpty(viewModel.getPhoneNumber())) {
                return "project/monitoring-officer/assign-role";
            }
            return "project/monitoring-officer/assign-role-without-edit";
        }
        return monitoringOfficerProjectsRedirect(userResource.getId());
    }

    @PostMapping("/{userId}/assign-role")
    public String assignRole(@PathVariable long userId,
                             @Valid @ModelAttribute(FORM) MonitoringOfficerAssignRoleForm form,
                             BindingResult bindingResult,
                             ValidationHandler validationHandler,
                             Model model) {
        if (validationHandler.hasErrors()) {
            model.addAttribute(MODEL, monitoringOfficerAssignRoleViewModelPopulator.populate(userId));
            model.addAttribute(FORM, form);
            return "project/monitoring-officer/assign-role";
        }

        userRestService.grantRole(userId, MONITORING_OFFICER).getSuccess();
        updateUserPhoneNumber(userId, form.getPhoneNumber());

        return monitoringOfficerProjectsRedirect(userId);
    }

    @PostMapping("/{userId}/assign-role-without-edit")
    public String assignRoleWithoutEdit(@PathVariable long userId) {
        userRestService.grantRole(userId, MONITORING_OFFICER).getSuccess();

        return monitoringOfficerProjectsRedirect(userId);
    }

    @GetMapping("/create/{emailAddress}")
    public String create(@PathVariable String emailAddress,
                         Model model) {
        MonitoringOfficerCreateForm form = new MonitoringOfficerCreateForm();
        model.addAttribute("emailAddress", emailAddress);
        model.addAttribute(FORM, form);
        return "project/monitoring-officer/create-new";
    }

    @PostMapping("/create")
    public String createUser(@Valid @ModelAttribute(FORM) MonitoringOfficerCreateForm form,
                             BindingResult bindingResult,
                             ValidationHandler validationHandler,
                             Model model) {

        Supplier<String> failureView = () -> {
            model.addAttribute("emailAddress", form.getEmailAddress());
            model.addAttribute(FORM, form);
            return "project/monitoring-officer/create-new";
        };

        return validationHandler
                .failNowOrSucceedWith(failureView, () -> {
                    MonitoringOfficerCreateResource resource = new MonitoringOfficerCreateResource(form.getFirstName(),
                            form.getLastName(),
                            form.getPhoneNumber(),
                            form.getEmailAddress());

                    RestResult<Void> result = monitoringOfficerRegistrationRestService.createMonitoringOfficer(resource);
                    return validationHandler
                            .addAnyErrors(result)
                            .failNowOrSucceedWith(failureView,
                                    () -> "redirect:/monitoring-officer/view-all");
                });
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
                                    () -> monitoringOfficerProjectsRedirect(monitoringOfficerId));
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
        model.addAttribute(MODEL, monitoringOfficerViewAllViewModelPopulator.populate());
        model.addAttribute(FORM, new MonitoringOfficerViewAllForm());
        return "project/monitoring-officer-view-all";
    }

    @GetMapping("/view-monitoring-officer")
    public String redirectToMoProjectPage(@ModelAttribute("form") MonitoringOfficerViewAllForm form) {
        // required to allow auto complete to send back the data about the selection
        if (form == null || form.getUserId() == null) {
            return "redirect://monitoring-officer/view-all";
        }

        return monitoringOfficerProjectsRedirect(form.getUserId());
    }

    private static String monitoringOfficerProjectsRedirect(long monitoringOfficerId) {
        return format("redirect:/monitoring-officer/%s/projects", monitoringOfficerId);
    }

    private ServiceResult<UserResource> updateUserPhoneNumber(long userId, String phoneNumber) {
        UserResource userResource = userRestService.retrieveUserById(userId).getSuccess();
        return userService.updateDetails(userId,
                userResource.getEmail(),
                userResource.getFirstName(),
                userResource.getLastName(),
                ofNullable(userResource.getTitle()).map(Title::getDisplayName).orElse(null),
                phoneNumber,
                userResource.getAllowMarketingEmails());
    }
}