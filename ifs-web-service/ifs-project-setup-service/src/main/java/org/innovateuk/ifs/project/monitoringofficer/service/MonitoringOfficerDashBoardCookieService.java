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

    public static final String MO_DASHBOARD_FORM_NAME = "moDashboardForm";
    public static final String KEYWORD_SEARCH = "keywordSearch";
    public static final String PROJECT_IN_SETUP = "projectInSetup";
    public static final String PREVIOUS_PROJECT = "previousProject";
    public static final String BINDING_RESULT_MODASHBOARD_FORM = "org.springframework.validation.BindingResult.moDashboardForm";

    @Autowired
    private EncryptedCookieService cookieUtil;

    protected Validator validator;

    @Autowired
    @Qualifier("mvcValidator")
    protected void setValidator(Validator validator) {
        this.validator = validator;
    }

    public MonitoringOfficerDashboardForm getMODashboardFormCookieValue(MonitoringOfficerDashboardForm monitoringOfficerDashboardForm, Model model, HttpServletRequest request) {
        return processedMODashboardFormFromCookie(model, request)
                .orElseGet(() -> processedMODashboardFormFromRequest(monitoringOfficerDashboardForm, request));
    }

    public void saveMODashboardDataIntoCookie(MonitoringOfficerDashboardForm monitoringOfficerDashboardCookie, HttpServletResponse response) {
        cookieUtil.saveToCookie(response, MO_DASHBOARD_FORM_NAME, JsonUtil.getSerializedObject(monitoringOfficerDashboardCookie));
    }

    public Optional<MonitoringOfficerDashboardForm> getMODashboardFormFromCookieValue(HttpServletRequest request) {
        return Optional.ofNullable(getObjectFromJson(cookieUtil.getCookieValue(request, MO_DASHBOARD_FORM_NAME), MonitoringOfficerDashboardForm.class));
    }


    public Optional<MonitoringOfficerDashboardForm> getKeywordSearchFromCookie(HttpServletRequest request) {
        return Optional.ofNullable(getObjectFromJson(cookieUtil.getCookieValue(request, KEYWORD_SEARCH), MonitoringOfficerDashboardForm.class));
    }

    public Optional<MonitoringOfficerDashboardForm> getFilterProjectsInSetupFromCookie(HttpServletRequest request) {
        return Optional.ofNullable(getObjectFromJson(cookieUtil.getCookieValue(request, PROJECT_IN_SETUP), MonitoringOfficerDashboardForm.class));
    }

    public Optional<MonitoringOfficerDashboardForm> getFilterPreviousProjectsFromCookie(HttpServletRequest request) {
        return Optional.ofNullable(getObjectFromJson(cookieUtil.getCookieValue(request, PREVIOUS_PROJECT), MonitoringOfficerDashboardForm.class));
    }

    public void deleteMODashBoardDataFromCookie(HttpServletResponse response) {
        cookieUtil.removeCookie(response, MO_DASHBOARD_FORM_NAME);
    }

    private Optional<MonitoringOfficerDashboardForm> processedMODashboardFormFromCookie(Model model, HttpServletRequest request) {
        Optional<MonitoringOfficerDashboardForm> moDashboardFormFromCookie = getMODashboardFormFromCookieValue(request);
        moDashboardFormFromCookie.ifPresent(monitoringOfficerDashboardForm -> {
            populateMODashboardForm(request, monitoringOfficerDashboardForm);

            BindingResult bindingResult = new BeanPropertyBindingResult(monitoringOfficerDashboardForm, MO_DASHBOARD_FORM_NAME);
            monitoringOfficerDashboardValidate(monitoringOfficerDashboardForm, bindingResult);
            model.addAttribute(BINDING_RESULT_MODASHBOARD_FORM, bindingResult);
        });

        return moDashboardFormFromCookie;
    }

    private MonitoringOfficerDashboardForm processedMODashboardFormFromRequest(MonitoringOfficerDashboardForm monitoringOfficerDashboardForm,
                                                                                              HttpServletRequest request) {
        addFilters(monitoringOfficerDashboardForm,
                keywordSearchFromCookie(request),
                filterProjectsInSetupFromCookie(request),
                filterPreviousProjectsFromCookie(request));
        return monitoringOfficerDashboardForm;
    }

    private void populateMODashboardForm(HttpServletRequest request, MonitoringOfficerDashboardForm monitoringOfficerDashboardForm) {
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

    private Optional<Boolean> filterProjectsInSetupFromCookie(HttpServletRequest request) {
        Optional<MonitoringOfficerDashboardForm> monitoringOfficerDashboardForm = getFilterProjectsInSetupFromCookie(request);

        if (monitoringOfficerDashboardForm.isPresent()) {
            return Optional.of(monitoringOfficerDashboardForm.get().isProjectInSetup());
        } else {
            return Optional.empty();
        }
    }

    private Optional<Boolean> filterPreviousProjectsFromCookie(HttpServletRequest request) {
        Optional<MonitoringOfficerDashboardForm> monitoringOfficerDashboardForm = getFilterPreviousProjectsFromCookie(request);

        if (monitoringOfficerDashboardForm.isPresent()) {
            return Optional.of(monitoringOfficerDashboardForm.get().isPreviousProject());
        } else {
            return Optional.empty(); }
    }

    private void monitoringOfficerDashboardValidate(MonitoringOfficerDashboardForm monitoringOfficerDashboardForm, BindingResult bindingResult) {
        validator.validate(monitoringOfficerDashboardForm, bindingResult);
    }

}
