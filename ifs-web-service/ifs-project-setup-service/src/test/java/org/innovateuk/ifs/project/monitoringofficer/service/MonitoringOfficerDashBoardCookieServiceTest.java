package org.innovateuk.ifs.project.monitoringofficer.service;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.project.monitoringofficer.form.MonitoringOfficerDashboardForm;
import org.innovateuk.ifs.util.EncryptedCookieService;
import org.innovateuk.ifs.util.JsonUtil;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.innovateuk.ifs.project.monitoringofficer.service.MonitoringOfficerDashBoardCookieService.MO_DASHBOARD_FORM_NAME;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class MonitoringOfficerDashBoardCookieServiceTest extends BaseServiceUnitTest<MonitoringOfficerDashBoardCookieService> {

    @Mock
    private EncryptedCookieService cookieUtil;

    private MockHttpServletResponse response;
    private MockHttpServletRequest request;

    protected MonitoringOfficerDashBoardCookieService supplyServiceUnderTest() {
        return new MonitoringOfficerDashBoardCookieService();
    }

    @Before
    public void setUp() {
        response = new MockHttpServletResponse();
        request = new MockHttpServletRequest();

        super.setup();
    }

    @Test
    public void saveMODashboardDataIntoCookie() throws Exception {
        MonitoringOfficerDashboardForm moDashboardForm = new MonitoringOfficerDashboardForm();

        service.saveMODashboardDataIntoCookie(moDashboardForm, response);

        verify(cookieUtil, times(1)).saveToCookie(response, MO_DASHBOARD_FORM_NAME, JsonUtil.getSerializedObject(moDashboardForm));
    }

    @Test
    public void getMODashboardFormCookieValue() throws Exception {
        MonitoringOfficerDashboardForm mODashboardForm = new MonitoringOfficerDashboardForm();

        when(cookieUtil.getCookieValue(request, MO_DASHBOARD_FORM_NAME)).thenReturn(JsonUtil.getSerializedObject(mODashboardForm));

        MonitoringOfficerDashboardForm result = service.getMODashboardFormCookieValue(request);

        assertEquals(result, mODashboardForm);
        verify(cookieUtil, times(1)).getCookieValue(request, MO_DASHBOARD_FORM_NAME);
    }

    @Test
    public void deleteMODashBoardDataFromCookie() throws Exception {
        service.deleteMODashBoardDataFromCookie(response);

        verify(cookieUtil, times(1)).removeCookie(response, MO_DASHBOARD_FORM_NAME);
    }
}