package org.innovateuk.ifs.project.monitoringofficer.controller;

import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.project.monitoringofficer.form.MonitoringOfficerDashboardForm;
import org.innovateuk.ifs.project.monitoringofficer.populator.MonitoringOfficerDashboardViewModelPopulator;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.util.CompressedCookieService;
import org.innovateuk.ifs.util.CompressionUtil;
import org.innovateuk.ifs.util.EncryptedCookieService;
import org.innovateuk.ifs.util.JsonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.Optional;
import java.util.function.Supplier;

import static org.innovateuk.ifs.util.JsonUtil.getObjectFromJson;

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

    private static final String KEYWORD_SEARCH = "keywordSearch";
    private static final String PROJECT_IN_SETUP = "projectInSetup";
    private static final String PREVIOUS_PROJECT= "previousProject";
    private static final String BINDING_RESULT_MODASHBOARD_FORM = "org.springframework.validation.BindingResult.form";

    private MonitoringOfficerDashboardViewModelPopulator monitoringOfficerDashboardViewModelPopulator;
    private CompressedCookieService compressedCookieService;
    private EncryptedCookieService cookieUtil;

    protected Validator validator;

    @Autowired
    @Qualifier("mvcValidator")
    protected void setValidator(Validator validator) {
        this.validator = validator;
    }

    MonitoringOfficerDashboardController() {}

    @Autowired
    public MonitoringOfficerDashboardController(MonitoringOfficerDashboardViewModelPopulator monitoringOfficerDashboardViewModelPopulator,
                                                CompressedCookieService compressedCookieService,
                                                EncryptedCookieService cookieUtil) {
        this.monitoringOfficerDashboardViewModelPopulator = monitoringOfficerDashboardViewModelPopulator;
        this.compressedCookieService = compressedCookieService;
        this.cookieUtil = cookieUtil;
    }

    @GetMapping
    public String viewDashboard(Model model,
                                @ModelAttribute(name = FORM_ATTR_NAME, binding = false) MonitoringOfficerDashboardForm form,
                                UserResource user,
                                @RequestParam(value = PAGE_NUMBER_KEY, defaultValue = DEFAULT_PAGE_NUMBER) int pageNumber,
                                @RequestParam(value = PAGE_SIZE_KEY, defaultValue = DEFAULT_PAGE_SIZE) int pageSize,
                                HttpServletRequest request,
                                HttpServletResponse response) {

        if(pageNumber == 0) {
            form.setProjectInSetup(true);
        }
//
//        form = getDataFromCookie(form, model, request);
//        savemonitoringOfficerDashboardCookie(form, response);
//
//        model.addAttribute(FORM_ATTR_NAME, form);
//        model.addAttribute("model", monitoringOfficerDashboardViewModelPopulator.populate(user
//                , form.getKeywordSearch()
//                , form.isProjectInSetup()
//                , form.isPreviousProject()
//                , form.isDocumentsComplete()
//                , form.isDocumentsIncomplete()
//                , form.isDocumentsAwaitingReview()
//                , form.isSpendProfileComplete()
//                , form.isSpendProfileIncomplete()
//                , form.isSpendProfileAwaitingReview() , pageNumber, pageSize ));
        Optional<MonitoringOfficerDashboardForm> cookieForm = getMonitoringOfficerDashboardFormCookieValue(request);
        if(cookieForm.isPresent()) {
            model.addAttribute(FORM_ATTR_NAME, cookieForm.get());
            model.addAttribute("model", monitoringOfficerDashboardViewModelPopulator.populate(user
                    , cookieForm.get().getKeywordSearch()
                    , cookieForm.get().isProjectInSetup()
                    , cookieForm.get().isPreviousProject()
                    , cookieForm.get().isDocumentsComplete()
                    , cookieForm.get().isDocumentsIncomplete()
                    , cookieForm.get().isDocumentsAwaitingReview()
                    , cookieForm.get().isSpendProfileComplete()
                    , cookieForm.get().isSpendProfileIncomplete()
                    , cookieForm.get().isSpendProfileAwaitingReview(), pageNumber, pageSize));
        } else {
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
        }
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
                                  HttpServletRequest request,
                                  HttpServletResponse response)  {
//        addFilters(form,
//                keywordSearchFromCookie(request),
//                filterProjectsInSetupFromCookie(request),
//                filterPreviousProjectsFromCookie(request));

        final Supplier<String> failureView = () -> viewDashboard(model, form, user,pageNumber, pageSize, request, response);

        return validationHandler.failNowOrSucceedWith(failureView,
                () -> {
                    savemonitoringOfficerDashboardCookie(form, response);

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

    private MonitoringOfficerDashboardForm getDataFromCookie(MonitoringOfficerDashboardForm monitoringOfficerDashboardForm, Model model, HttpServletRequest request) {
        return processedMonitoringOfficerDashboardFormFromCookie(model, request)
                .orElseGet(() -> processedMonitoringOfficerDashboardFormFromRequest(monitoringOfficerDashboardForm, request));
    }

    private Optional<MonitoringOfficerDashboardForm> processedMonitoringOfficerDashboardFormFromCookie(Model model, HttpServletRequest request) {
        Optional<MonitoringOfficerDashboardForm> monitoringOfficerDashboardFormFromCookie = getMonitoringOfficerDashboardFormCookieValue(request);
        monitoringOfficerDashboardFormFromCookie.ifPresent(monitoringOfficerDashboardForm -> {
            populateMonitoringOfficerDashboardForm(request, monitoringOfficerDashboardForm);

            BindingResult bindingResult = new BeanPropertyBindingResult(monitoringOfficerDashboardForm, FORM_ATTR_NAME);
            monitoringOfficerDashboardFormValidate(monitoringOfficerDashboardForm, bindingResult);
            model.addAttribute(BINDING_RESULT_MODASHBOARD_FORM, bindingResult);
        });

        return monitoringOfficerDashboardFormFromCookie;
    }

//    protected void saveFormToCookie(HttpServletResponse response, String identifier, T selectionForm) {
//        cookieUtil.saveToCookie(response, format("%s_comp_%s", getCookieName(), identifier), getSerializedObject(selectionForm));
//    }

    protected String getValueFromCookie(String value) {
        return CompressionUtil.getDecompressedString(value);
    }

    public Optional<MonitoringOfficerDashboardForm> getMonitoringOfficerDashboardFormCookieValue(HttpServletRequest request) {
//        return Optional.ofNullable(getObjectFromJson(compressedCookieService.getCookieValue(request, FORM_ATTR_NAME), MonitoringOfficerDashboardForm.class));
        return Optional.ofNullable(getObjectFromJson(cookieUtil.getCookieValue(request, FORM_ATTR_NAME), MonitoringOfficerDashboardForm.class));
    }

    private void populateMonitoringOfficerDashboardForm(HttpServletRequest request, MonitoringOfficerDashboardForm monitoringOfficerDashboardForm) {
        addFilters(monitoringOfficerDashboardForm,
                keywordSearchFromCookie(request),
                filterProjectsInSetupFromCookie(request),
                filterPreviousProjectsFromCookie(request));
    }

    private MonitoringOfficerDashboardForm processedMonitoringOfficerDashboardFormFromRequest(MonitoringOfficerDashboardForm monitoringOfficerDashboardForm,
                                                                                              HttpServletRequest request) {
        addFilters(monitoringOfficerDashboardForm,
                keywordSearchFromCookie(request),
                filterProjectsInSetupFromCookie(request),
                filterPreviousProjectsFromCookie(request));
        return monitoringOfficerDashboardForm;
    }

    private void addFilters(MonitoringOfficerDashboardForm monitoringOfficerDashboardForm,
                                 Optional<String> keywordSearch,
                                 Optional<Boolean> projectInSetup,
                                 Optional<Boolean> previousProject) {
        keywordSearch.ifPresent(monitoringOfficerDashboardForm::setKeywordSearch);
        projectInSetup.ifPresent(monitoringOfficerDashboardForm::setProjectInSetup);
        previousProject.ifPresent(monitoringOfficerDashboardForm::setPreviousProject);
    }

    private Optional<String> keywordSearchFromCookie(HttpServletRequest request) {
        Optional<MonitoringOfficerDashboardForm> keywordSearchMODashboardForm = getKeywordSearchFromCookie(request);

        if(keywordSearchMODashboardForm.isPresent()) {
            return Optional.ofNullable(keywordSearchMODashboardForm.get().getKeywordSearch());
        } else {
            return Optional.empty();
        }
    }
    public Optional<MonitoringOfficerDashboardForm> getKeywordSearchFromCookie(HttpServletRequest request) {

        return Optional.ofNullable(getObjectFromJson(cookieUtil.getCookieValue(request, KEYWORD_SEARCH), MonitoringOfficerDashboardForm.class));
    }

    private Optional<Boolean> filterProjectsInSetupFromCookie(HttpServletRequest request) {
        Optional<MonitoringOfficerDashboardForm> MODashboardForm = getFilterProjectsInSetupFromCookie(request);

        if(MODashboardForm.isPresent()) {
            return Optional.of(MODashboardForm.get().isProjectInSetup());
        } else {
            return Optional.empty();
        }
    }

    public Optional<MonitoringOfficerDashboardForm> getFilterProjectsInSetupFromCookie(HttpServletRequest request) {
        return Optional.ofNullable(getObjectFromJson(cookieUtil.getCookieValue(request, PROJECT_IN_SETUP), MonitoringOfficerDashboardForm.class));
    }

    private Optional<Boolean> filterPreviousProjectsFromCookie(HttpServletRequest request) {
        Optional<MonitoringOfficerDashboardForm> MODashboardForm = getFilterPreviousProjectsFromCookie(request);

        if(MODashboardForm.isPresent()) {
            return Optional.of(MODashboardForm.get().isPreviousProject());
        } else {
            return Optional.empty();
        }
    }

    public Optional<MonitoringOfficerDashboardForm> getFilterPreviousProjectsFromCookie(HttpServletRequest request) {
        return Optional.ofNullable(getObjectFromJson(cookieUtil.getCookieValue(request, PREVIOUS_PROJECT), MonitoringOfficerDashboardForm.class));
    }

    private void monitoringOfficerDashboardFormValidate(MonitoringOfficerDashboardForm monitoringOfficerDashboardForm, BindingResult bindingResult) {
        validator.validate(monitoringOfficerDashboardForm, bindingResult);
    }

    public void savemonitoringOfficerDashboardCookie(MonitoringOfficerDashboardForm monitoringOfficerDashboardCookie, HttpServletResponse response) {
        cookieUtil.saveToCookie(response, FORM_ATTR_NAME, JsonUtil.getSerializedObject(monitoringOfficerDashboardCookie));
        //        compressedCookieService.saveToCookie(response, FORM_ATTR_NAME, JsonUtil.getSerializedObject(monitoringOfficerDashboardCookie));
//        saveKeywordSearchCookie(monitoringOfficerDashboardCookie, response);
//        saveFilterProjectInSetupCookie(monitoringOfficerDashboardCookie, response);
//        saveFilterPreviousProjectCookie(monitoringOfficerDashboardCookie, response);
    }
//    public void saveKeywordSearchCookie(MonitoringOfficerDashboardForm monitoringOfficerDashboardCookie, HttpServletResponse response) {
//        cookieUtil.saveToCookie(response, KEYWORD_SEARCH, JsonUtil.getSerializedObject(monitoringOfficerDashboardCookie));
//    }
//    public void saveFilterProjectInSetupCookie(MonitoringOfficerDashboardForm monitoringOfficerDashboardCookie, HttpServletResponse response) {
//        cookieUtil.saveToCookie(response, PROJECT_IN_SETUP, JsonUtil.getSerializedObject(monitoringOfficerDashboardCookie));
//    }
//    public void saveFilterPreviousProjectCookie(MonitoringOfficerDashboardForm monitoringOfficerDashboardCookie, HttpServletResponse response) {
//        cookieUtil.saveToCookie(response, PREVIOUS_PROJECT, JsonUtil.getSerializedObject(monitoringOfficerDashboardCookie));
//    }
}
