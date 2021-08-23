package org.innovateuk.ifs.project.monitoringofficer.controller;

import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.project.monitoringofficer.form.MonitoringOfficerDashboardForm;
import org.innovateuk.ifs.project.monitoringofficer.populator.MonitoringOfficerDashboardViewModelPopulator;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/monitoring-officer/dashboard")
@Controller
@SecuredBySpring(value = "Controller", description = "Each monitoring officer has permission to view their own dashboard",
        securedType = MonitoringOfficerDashboardController.class)
@PreAuthorize("hasAnyAuthority('monitoring_officer')")
public class MonitoringOfficerDashboardController {

    private static final String FORM_ATTR_NAME = "form";

    private MonitoringOfficerDashboardViewModelPopulator monitoringOfficerDashboardViewModelPopulator;

    MonitoringOfficerDashboardController() {}

    @Autowired
    public MonitoringOfficerDashboardController(MonitoringOfficerDashboardViewModelPopulator monitoringOfficerDashboardViewModelPopulator) {
        this.monitoringOfficerDashboardViewModelPopulator = monitoringOfficerDashboardViewModelPopulator;
    }

    @GetMapping
    public String viewDashboard(Model model,
                                UserResource user,
                                @ModelAttribute(name = FORM_ATTR_NAME, binding = false) MonitoringOfficerDashboardForm form) {
        form.setProjectInSetup(true);

        model.addAttribute(FORM_ATTR_NAME, form);
        model.addAttribute("model", monitoringOfficerDashboardViewModelPopulator.populate(user
                , form.isProjectInSetup()
                , form.isPreviousProject()
                , form.isDocumentsComplete()
                , form.isDocumentsIncomplete()
                , form.isDocumentsAwaitingReview()
                , form.isSpendProfileComplete()
                , form.isSpendProfileIncomplete()
                , form.isSpendProfileAwaitingReview()));

        return "monitoring-officer/dashboard";
    }

    @PostMapping
    public String filterDashboard(Model model,
                                  UserResource user,
                                  @ModelAttribute(FORM_ATTR_NAME) MonitoringOfficerDashboardForm form) {
        model.addAttribute("model", monitoringOfficerDashboardViewModelPopulator.populate(user
                , form.isProjectInSetup()
                , form.isPreviousProject()
                , form.isDocumentsComplete()
                , form.isDocumentsIncomplete()
                , form.isDocumentsAwaitingReview()
                , form.isSpendProfileComplete()
                , form.isSpendProfileIncomplete()
                , form.isSpendProfileAwaitingReview()));

        return "monitoring-officer/dashboard";
    }
}
