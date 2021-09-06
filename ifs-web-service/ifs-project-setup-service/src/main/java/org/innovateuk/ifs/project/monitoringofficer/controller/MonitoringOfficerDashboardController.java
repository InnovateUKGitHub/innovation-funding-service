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
    private static final String PAGE_NUMBER_KEY = "page";
    private static final String PAGE_SIZE_KEY = "size";
    private static final String DEFAULT_PAGE_NUMBER = "0";
    private static final String DEFAULT_PAGE_SIZE = "10";

    MonitoringOfficerDashboardController() {}

    @Autowired
    public MonitoringOfficerDashboardController(MonitoringOfficerDashboardViewModelPopulator monitoringOfficerDashboardViewModelPopulator) {
        this.monitoringOfficerDashboardViewModelPopulator = monitoringOfficerDashboardViewModelPopulator;
    }

    @GetMapping
    public String viewDashboard(Model model,
                                UserResource user,
                                @ModelAttribute(name = FORM_ATTR_NAME, binding = false) MonitoringOfficerDashboardForm form,
                                @RequestParam(value = PAGE_NUMBER_KEY, defaultValue = DEFAULT_PAGE_NUMBER) int pageNumber,
                                @RequestParam(value = PAGE_SIZE_KEY, defaultValue = DEFAULT_PAGE_SIZE) int pageSize) {
        form.setProjectInSetup(true);

        model.addAttribute(FORM_ATTR_NAME, form);
        model.addAttribute("model", monitoringOfficerDashboardViewModelPopulator.populate(user
                , form.getKeywordSearch()
                , form.isProjectInSetup()
                , form.isPreviousProject()
                , form.isDocumentsComplete()
                , form.isDocumentsIncomplete()
                , form.isDocumentsAwaitingReview()
                , form.isSpendProfileComplete()
                , form.isSpendProfileIncomplete()
                , form.isSpendProfileAwaitingReview() , pageNumber, pageSize ));
        return "monitoring-officer/dashboard";
    }

    @PostMapping
    public String filterDashboard(Model model,
                                  UserResource user,
                                  @ModelAttribute(FORM_ATTR_NAME) MonitoringOfficerDashboardForm form,
                                  @RequestParam(value = PAGE_NUMBER_KEY, defaultValue = DEFAULT_PAGE_NUMBER) int pageNumber,
                                  @RequestParam(value = PAGE_SIZE_KEY, defaultValue = DEFAULT_PAGE_SIZE) int pageSize) {

        model.addAttribute("model", monitoringOfficerDashboardViewModelPopulator.populate(user
                , form.getKeywordSearch()
                , form.isProjectInSetup()
                , form.isPreviousProject()
                , form.isDocumentsComplete()
                , form.isDocumentsIncomplete()
                , form.isDocumentsAwaitingReview()
                , form.isSpendProfileComplete()
                , form.isSpendProfileIncomplete()
                , form.isSpendProfileAwaitingReview(), pageNumber, pageSize));

        return "monitoring-officer/dashboard";

    }
}
