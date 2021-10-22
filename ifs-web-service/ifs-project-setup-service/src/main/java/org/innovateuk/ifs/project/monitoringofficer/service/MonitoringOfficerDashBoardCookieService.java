package org.innovateuk.ifs.project.monitoringofficer.service;

import org.innovateuk.ifs.project.monitoringofficer.form.MonitoringOfficerDashboardForm;
import org.innovateuk.ifs.util.EncryptedCookieService;
import org.innovateuk.ifs.util.JsonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

import static org.innovateuk.ifs.util.JsonUtil.getObjectFromJson;

@Service
public class MonitoringOfficerDashBoardCookieService {

    private static final String MO_DASHBOARD_FORM_NAME = "moDashboardForm";
    private static final String KEYWORD_SEARCH = "keywordSearch";
    private static final String PROJECT_IN_SETUP = "projectInSetup";
    private static final String PREVIOUS_PROJECT = "previousProject";
    private static final String BINDING_RESULT_MODASHBOARD_FORM = "org.springframework.validation.BindingResult.moDashboardForm";

    @Autowired
    private EncryptedCookieService cookieUtil;

    protected Validator validator;

    @Autowired
    @Qualifier("mvcValidator")
    protected void setValidator(Validator validator) {
        this.validator = validator;
    }

    public MonitoringOfficerDashboardForm getMODashboardFormCookieValue(MonitoringOfficerDashboardForm monitoringOfficerDashboardForm, Model model, HttpServletRequest request) {
        return processedMonitoringOfficerDashboardFormFromCookie(model, request)
                .orElseGet(() -> processedMonitoringOfficerDashboardFormFromRequest(monitoringOfficerDashboardForm, request));
    }

    private Optional<MonitoringOfficerDashboardForm> processedMonitoringOfficerDashboardFormFromCookie(Model model, HttpServletRequest request) {
        Optional<MonitoringOfficerDashboardForm> monitoringOfficerDashboardFormFromCookie = getMonitoringOfficerDashboardFormCookieValue(request);
        monitoringOfficerDashboardFormFromCookie.ifPresent(monitoringOfficerDashboardForm -> {
            populateMonitoringOfficerDashboardForm(request, monitoringOfficerDashboardForm);

            BindingResult bindingResult = new BeanPropertyBindingResult(monitoringOfficerDashboardForm, MO_DASHBOARD_FORM_NAME);
            monitoringOfficerDashboardFormValidate(monitoringOfficerDashboardForm, bindingResult);
            model.addAttribute(BINDING_RESULT_MODASHBOARD_FORM, bindingResult);
        });

        return monitoringOfficerDashboardFormFromCookie;
    }

    private MonitoringOfficerDashboardForm processedMonitoringOfficerDashboardFormFromRequest(MonitoringOfficerDashboardForm monitoringOfficerDashboardForm,
                                                                                              HttpServletRequest request) {
        addFilters(monitoringOfficerDashboardForm,
                keywordSearchFromCookie(request),
                filterProjectsInSetupFromCookie(request),
                filterPreviousProjectsFromCookie(request));
        return monitoringOfficerDashboardForm;
    }

    public void saveMODashboardDataIntoCookie(MonitoringOfficerDashboardForm monitoringOfficerDashboardCookie, HttpServletResponse response) {
        cookieUtil.saveToCookie(response, MO_DASHBOARD_FORM_NAME, JsonUtil.getSerializedObject(monitoringOfficerDashboardCookie));
    }


    public Optional<MonitoringOfficerDashboardForm> getMonitoringOfficerDashboardFormCookieValue(HttpServletRequest request) {
        return Optional.ofNullable(getObjectFromJson(cookieUtil.getCookieValue(request, MO_DASHBOARD_FORM_NAME), MonitoringOfficerDashboardForm.class));
    }

    private void populateMonitoringOfficerDashboardForm(HttpServletRequest request, MonitoringOfficerDashboardForm monitoringOfficerDashboardForm) {
        addFilters(monitoringOfficerDashboardForm,
                keywordSearchFromCookie(request),
                filterProjectsInSetupFromCookie(request),
                filterPreviousProjectsFromCookie(request));
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

        if (keywordSearchMODashboardForm.isPresent()) {
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

        if (MODashboardForm.isPresent()) {
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

        if (MODashboardForm.isPresent()) {
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
}
