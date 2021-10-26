package org.innovateuk.ifs.project.monitoringofficer.service;

import org.innovateuk.ifs.project.monitoringofficer.form.MonitoringOfficerDashboardForm;
import org.innovateuk.ifs.util.EncryptedCookieService;
import org.innovateuk.ifs.util.JsonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    @Autowired
    private EncryptedCookieService cookieUtil;

    public MonitoringOfficerDashboardForm getMODashboardFormCookieValue(HttpServletRequest request) {
            Optional<MonitoringOfficerDashboardForm> moDashboardForm = getMODashboardFormFromCookieValue(request);

            if(moDashboardForm.isPresent()) {
                return moDashboardForm.get();
            }
            else {
                MonitoringOfficerDashboardForm newMoDashboardForm =   new MonitoringOfficerDashboardForm();
                newMoDashboardForm.setProjectInSetup(true);
                return newMoDashboardForm;
            }
    }

    public void saveMODashboardDataIntoCookie(MonitoringOfficerDashboardForm monitoringOfficerDashboardCookie, HttpServletResponse response) {
        cookieUtil.saveToCookie(response, MO_DASHBOARD_FORM_NAME, JsonUtil.getSerializedObject(monitoringOfficerDashboardCookie));
    }

    public Optional<MonitoringOfficerDashboardForm> getMODashboardFormFromCookieValue(HttpServletRequest request) {
        return Optional.ofNullable(getObjectFromJson(cookieUtil.getCookieValue(request, MO_DASHBOARD_FORM_NAME), MonitoringOfficerDashboardForm.class));
    }

    public void deleteMODashBoardDataFromCookie(HttpServletResponse response) {
        cookieUtil.removeCookie(response, MO_DASHBOARD_FORM_NAME);
    }
}
