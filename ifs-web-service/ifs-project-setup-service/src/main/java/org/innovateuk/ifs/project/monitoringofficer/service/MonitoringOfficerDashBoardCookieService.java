package org.innovateuk.ifs.project.monitoringofficer.service;

import org.innovateuk.ifs.project.monitoringofficer.form.MODashboardForm;
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

    public MODashboardForm getMODashboardFormCookieValue(MODashboardForm mODashboardForm, Model model, HttpServletRequest request) {
        return processedMODashboardFormFromCookie(model, request)
                .orElseGet(() -> processedMODashboardFormFromRequest(mODashboardForm, request));
    }

    public void saveMODashboardDataIntoCookie(MODashboardForm monitoringOfficerDashboardCookie, HttpServletResponse response) {
        cookieUtil.saveToCookie(response, MO_DASHBOARD_FORM_NAME, JsonUtil.getSerializedObject(monitoringOfficerDashboardCookie));
    }

    public Optional<MODashboardForm> getMODashboardFormFromCookieValue(HttpServletRequest request) {
        return Optional.ofNullable(getObjectFromJson(cookieUtil.getCookieValue(request, MO_DASHBOARD_FORM_NAME), MODashboardForm.class));
    }


    public Optional<MODashboardForm> getKeywordSearchFromCookie(HttpServletRequest request) {
        return Optional.ofNullable(getObjectFromJson(cookieUtil.getCookieValue(request, KEYWORD_SEARCH), MODashboardForm.class));
    }

    public Optional<MODashboardForm> getFilterProjectsInSetupFromCookie(HttpServletRequest request) {
        return Optional.ofNullable(getObjectFromJson(cookieUtil.getCookieValue(request, PROJECT_IN_SETUP), MODashboardForm.class));
    }

    public Optional<MODashboardForm> getFilterPreviousProjectsFromCookie(HttpServletRequest request) {
        return Optional.ofNullable(getObjectFromJson(cookieUtil.getCookieValue(request, PREVIOUS_PROJECT), MODashboardForm.class));
    }

    public void deleteMODashBoardDataFromCookie(HttpServletResponse response) {
        cookieUtil.removeCookie(response, MO_DASHBOARD_FORM_NAME);
    }

    private Optional<MODashboardForm> processedMODashboardFormFromCookie(Model model, HttpServletRequest request) {
        Optional<MODashboardForm> mODashboardFormFromCookie = getMODashboardFormFromCookieValue(request);
        mODashboardFormFromCookie.ifPresent(mODashboardForm -> {
            populateMODashboardForm(request, mODashboardForm);

            BindingResult bindingResult = new BeanPropertyBindingResult(mODashboardForm, MO_DASHBOARD_FORM_NAME);
            mODashboardFormValidate(mODashboardForm, bindingResult);
            model.addAttribute(BINDING_RESULT_MODASHBOARD_FORM, bindingResult);
        });

        return mODashboardFormFromCookie;
    }

    private MODashboardForm processedMODashboardFormFromRequest(MODashboardForm mODashboardForm,
                                                                                              HttpServletRequest request) {
        addFilters(mODashboardForm,
                keywordSearchFromCookie(request),
                filterProjectsInSetupFromCookie(request),
                filterPreviousProjectsFromCookie(request));
        return mODashboardForm;
    }

    private void populateMODashboardForm(HttpServletRequest request, MODashboardForm mODashboardForm) {
        addFilters(mODashboardForm,
                keywordSearchFromCookie(request),
                filterProjectsInSetupFromCookie(request),
                filterPreviousProjectsFromCookie(request));
    }

    private void addFilters(MODashboardForm mODashboardForm,
                            Optional<String> keywordSearch,
                            Optional<Boolean> projectInSetup,
                            Optional<Boolean> previousProject) {
        keywordSearch.ifPresent(mODashboardForm::setKeywordSearch);
        projectInSetup.ifPresent(mODashboardForm::setProjectInSetup);
        previousProject.ifPresent(mODashboardForm::setPreviousProject);
    }

    private Optional<String> keywordSearchFromCookie(HttpServletRequest request) {
        Optional<MODashboardForm> keywordSearchMODashboardForm = getKeywordSearchFromCookie(request);

        if (keywordSearchMODashboardForm.isPresent()) {
            return Optional.ofNullable(keywordSearchMODashboardForm.get().getKeywordSearch());
        } else {
            return Optional.empty();
        }
    }

    private Optional<Boolean> filterProjectsInSetupFromCookie(HttpServletRequest request) {
        Optional<MODashboardForm> MODashboardForm = getFilterProjectsInSetupFromCookie(request);

        if (MODashboardForm.isPresent()) {
            return Optional.of(MODashboardForm.get().isProjectInSetup());
        } else {
            return Optional.empty();
        }
    }

    private Optional<Boolean> filterPreviousProjectsFromCookie(HttpServletRequest request) {
        Optional<MODashboardForm> MODashboardForm = getFilterPreviousProjectsFromCookie(request);

        if (MODashboardForm.isPresent()) {
            return Optional.of(MODashboardForm.get().isPreviousProject());
        } else {
            return Optional.empty(); }
    }

    private void mODashboardFormValidate(MODashboardForm mODashboardForm, BindingResult bindingResult) {
        validator.validate(mODashboardForm, bindingResult);
    }

}
