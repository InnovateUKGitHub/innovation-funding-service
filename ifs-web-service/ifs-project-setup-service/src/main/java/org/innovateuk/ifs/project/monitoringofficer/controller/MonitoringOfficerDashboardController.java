package org.innovateuk.ifs.project.monitoringofficer.controller;

import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.project.monitoringofficer.form.MonitoringOfficerDashboardForm;
import org.innovateuk.ifs.project.monitoringofficer.populator.MonitoringOfficerDashboardViewModelPopulator;
import org.innovateuk.ifs.project.monitoringofficer.service.MonitoringOfficerDashBoardCookieService;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.function.Supplier;

@RequestMapping("/monitoring-officer/dashboard")
@Controller
@SecuredBySpring(value = "Controller", description = "Each monitoring officer has permission to view their own dashboard",
        securedType = MonitoringOfficerDashboardController.class)
@PreAuthorize("hasAnyAuthority('monitoring_officer')")
public class MonitoringOfficerDashboardController {

    private static final String FORM_ATTR_NAME = "form";
    private static final String PAGE_NUMBER_KEY = "page";
    private static final String PAGE_SIZE_KEY = "size";
    private static final String DEFAULT_PAGE_NUMBER = "0";
    private static final String DEFAULT_PAGE_SIZE = "10";
    @Autowired
    private MonitoringOfficerDashboardViewModelPopulator monitoringOfficerDashboardViewModelPopulator;
    @Autowired
    private MonitoringOfficerDashBoardCookieService monitoringOfficerDashBoardCookieService;

    public MonitoringOfficerDashboardController() {

    }

    @GetMapping
    public String viewDashboard(Model model,
                                @ModelAttribute(name = FORM_ATTR_NAME, binding = false) MonitoringOfficerDashboardForm form,
                                UserResource user,
                                @RequestParam(value = PAGE_NUMBER_KEY, defaultValue = DEFAULT_PAGE_NUMBER) int pageNumber,
                                @RequestParam(value = PAGE_SIZE_KEY, defaultValue = DEFAULT_PAGE_SIZE) int pageSize,
                                HttpServletResponse response) {

        monitoringOfficerDashBoardCookieService.deleteMODashBoardDataFromCookie(response);

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
                , form.isSpendProfileAwaitingReview(), pageNumber, pageSize));

        return "monitoring-officer/dashboard";
    }

    @PostMapping
    public String filterDashboard(Model model,
                                  @Valid @ModelAttribute(FORM_ATTR_NAME) MonitoringOfficerDashboardForm form,
                                  @SuppressWarnings("unused") BindingResult bindingResult,
                                  ValidationHandler validationHandler,
                                  UserResource user,
                                  @RequestParam(value = PAGE_NUMBER_KEY, defaultValue = DEFAULT_PAGE_NUMBER) int pageNumber,
                                  @RequestParam(value = PAGE_SIZE_KEY, defaultValue = DEFAULT_PAGE_SIZE) int pageSize,
                                  HttpServletResponse response) {
        final Supplier<String> failureView = () -> viewDashboard(model, form, user, pageNumber, pageSize, response);

        return validationHandler.failNowOrSucceedWith(failureView,
                () -> {
                    monitoringOfficerDashBoardCookieService.saveMODashboardDataIntoCookie(form, response);
                    return "redirect:/monitoring-officer/dashboard/results";
                });
    }

    @GetMapping("/results")
    public String getResultsDashboard(Model model,
                                UserResource user,
                                @RequestParam(value = PAGE_NUMBER_KEY, defaultValue = DEFAULT_PAGE_NUMBER) int pageNumber,
                                @RequestParam(value = PAGE_SIZE_KEY, defaultValue = DEFAULT_PAGE_SIZE) int pageSize,
                                HttpServletRequest request) {

        MonitoringOfficerDashboardForm moDashboardForm = monitoringOfficerDashBoardCookieService.getMODashboardFormCookieValue(request);
            model.addAttribute(FORM_ATTR_NAME, moDashboardForm);
            model.addAttribute("model", monitoringOfficerDashboardViewModelPopulator.populate(user
                    , moDashboardForm.getKeywordSearch()
                    , moDashboardForm.isProjectInSetup()
                    , moDashboardForm.isPreviousProject()
                    , moDashboardForm.isDocumentsComplete()
                    , moDashboardForm.isDocumentsIncomplete()
                    , moDashboardForm.isDocumentsAwaitingReview()
                    , moDashboardForm.isSpendProfileComplete()
                    , moDashboardForm.isSpendProfileIncomplete()
                    , moDashboardForm.isSpendProfileAwaitingReview(), pageNumber, pageSize));

        return "monitoring-officer/dashboard";
    }

    @PostMapping("/results")
    public String postResultsDashboard(Model model,
                                  @Valid @ModelAttribute(FORM_ATTR_NAME) MonitoringOfficerDashboardForm form,
                                  @SuppressWarnings("unused") BindingResult bindingResult,
                                  ValidationHandler validationHandler,
                                  UserResource user,
                                  @RequestParam(value = PAGE_NUMBER_KEY, defaultValue = DEFAULT_PAGE_NUMBER) int pageNumber,
                                  @RequestParam(value = PAGE_SIZE_KEY, defaultValue = DEFAULT_PAGE_SIZE) int pageSize,
                                  HttpServletRequest request) {
        final Supplier<String> failureView = () -> getResultsDashboard(model, user, pageNumber, pageSize, request);

        return validationHandler.failNowOrSucceedWith(failureView,
                () -> {
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
                });
    }

}
