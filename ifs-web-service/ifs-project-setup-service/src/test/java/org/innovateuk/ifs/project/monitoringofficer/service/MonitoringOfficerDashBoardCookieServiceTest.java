package org.innovateuk.ifs.project.monitoringofficer.service;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.util.CompressedCookieService;
import org.innovateuk.ifs.util.EncryptedCookieService;
import org.innovateuk.ifs.util.JsonUtil;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.innovateuk.ifs.project.monitoringofficer.service.MonitoringOfficerDashBoardCookieService.*;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

class MonitoringOfficerDashBoardCookieServiceTest extends BaseServiceUnitTest<MonitoringOfficerDashBoardCookieService> {

    @Mock
    private EncryptedCookieService cookieUtil;

    @Mock
    private CompressedCookieService compressedCookieService;

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
        MonitoringOfficerDashboardForm monitoringOfficerDashboardForm = new MonitoringOfficerDashboardForm();

        service.saveMODashboardDataIntoCookie(monitoringOfficerDashboardForm, response);

        verify(cookieUtil, times(1)).saveToCookie(response, MO_DASHBOARD_FORM_NAME, JsonUtil.getSerializedObject(monitoringOfficerDashboardForm));
    }

    @Test
    public void getMODashboardFormCookieValue() throws Exception {
        MonitoringOfficerDashboardForm monitoringOfficerDashboardForm = new MonitoringOfficerDashboardForm();

        when(cookieUtil.getCookieValue(request, MO_DASHBOARD_FORM_NAME)).thenReturn(JsonUtil.getSerializedObject(monitoringOfficerDashboardForm));
        when(cookieUtil.getCookieValue(request, KEYWORD_SEARCH)).thenReturn(JsonUtil.getSerializedObject(monitoringOfficerDashboardForm));
        when(cookieUtil.getCookieValue(request, PROJECT_IN_SETUP)).thenReturn(JsonUtil.getSerializedObject(monitoringOfficerDashboardForm));
        when(cookieUtil.getCookieValue(request, PREVIOUS_PROJECT)).thenReturn(JsonUtil.getSerializedObject(monitoringOfficerDashboardForm));

        Optional<MonitoringOfficerDashboardForm> result = service.getMODashboardFormCookieValue(monitoringOfficerDashboardForm, model, request);

        assertEquals(result.get(), monitoringOfficerDashboardForm);
        verify(cookieUtil, times(1)).getCookieValue(request, MO_DASHBOARD_FORM_NAME);
        verify(cookieUtil, times(1)).getCookieValue(request, KEYWORD_SEARCH);
        verify(cookieUtil, times(1)).getCookieValue(request, PROJECT_IN_SETUP);
        verify(cookieUtil, times(1)).getCookieValue(request, PREVIOUS_PROJECT);
    }

    @Test
    public void getMonitoringOfficerDashboardFormFromCookieValue() throws Exception {
        MonitoringOfficerDashboardForm monitoringOfficerDashboardForm = new MonitoringOfficerDashboardForm();

        when(cookieUtil.getCookieValue(request, MO_DASHBOARD_FORM_NAME)).thenReturn(JsonUtil.getSerializedObject(monitoringOfficerDashboardForm));

        Optional<MonitoringOfficerDashboardForm> result = service.getMonitoringOfficerDashboardFormFromCookieValue(request);

        assertEquals(result.get(), monitoringOfficerDashboardForm);
        verify(cookieUtil, times(1)).getCookieValue(request, MO_DASHBOARD_FORM_NAME);
    }

    @Test
    public void getKeywordSearchFromCookie() throws Exception {
        MonitoringOfficerDashboardForm monitoringOfficerDashboardForm = new MonitoringOfficerDashboardForm();

        when(cookieUtil.getCookieValue(request, KEYWORD_SEARCH)).thenReturn(JsonUtil.getSerializedObject(monitoringOfficerDashboardForm));

        Optional<MonitoringOfficerDashboardForm> result = service.getMonitoringOfficerDashboardFormFromCookieValue(request);

        assertEquals(result.get(), monitoringOfficerDashboardForm);
        verify(cookieUtil, times(1)).getCookieValue(request, KEYWORD_SEARCH);
    }

    @Test
    public void getFilterProjectsInSetupFromCookie() throws Exception {
        MonitoringOfficerDashboardForm monitoringOfficerDashboardForm = new MonitoringOfficerDashboardForm();

        when(cookieUtil.getCookieValue(request, PROJECT_IN_SETUP)).thenReturn(JsonUtil.getSerializedObject(monitoringOfficerDashboardForm));

        Optional<MonitoringOfficerDashboardForm> result = service.getMonitoringOfficerDashboardFormFromCookieValue(request);

        assertEquals(result.get(), monitoringOfficerDashboardForm);
        verify(cookieUtil, times(1)).getCookieValue(request, PROJECT_IN_SETUP);
    }

    @Test
    public void getFilterPreviousProjectsFromCookie() throws Exception {
        MonitoringOfficerDashboardForm monitoringOfficerDashboardForm = new MonitoringOfficerDashboardForm();

        when(cookieUtil.getCookieValue(request, PREVIOUS_PROJECT)).thenReturn(JsonUtil.getSerializedObject(monitoringOfficerDashboardForm));

        Optional<MonitoringOfficerDashboardForm> result = service.getMonitoringOfficerDashboardFormFromCookieValue(request);

        assertEquals(result.get(), monitoringOfficerDashboardForm);
        verify(cookieUtil, times(1)).getCookieValue(request, PREVIOUS_PROJECT);
    }

    @Test
    public void deleteMODashBoardDataFromCookie() throws Exception {
        service.deleteMODashBoardDataFromCookie(response);

        verify(cookieUtil, times(1)).removeCookie(response, MO_DASHBOARD_FORM_NAME);
    }
}