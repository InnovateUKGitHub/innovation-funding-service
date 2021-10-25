package org.innovateuk.ifs.project.monitoringofficer.service;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.project.monitoringofficer.form.MODashboardForm;
import org.innovateuk.ifs.util.CompressedCookieService;
import org.innovateuk.ifs.util.EncryptedCookieService;
import org.innovateuk.ifs.util.JsonUtil;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;

import static org.innovateuk.ifs.project.monitoringofficer.service.MonitoringOfficerDashBoardCookieService.*;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class MonitoringOfficerDashBoardCookieServiceTest extends BaseServiceUnitTest<MonitoringOfficerDashBoardCookieService> {

    @Mock
    private EncryptedCookieService cookieUtil;

    @Mock
    private CompressedCookieService compressedCookieService;

    private MockHttpServletResponse response;
    private MockHttpServletRequest request;

    @Mock
    private Validator validator;

    private BindingResult bindingResult;

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
        MODashboardForm moDashboardForm = new MODashboardForm();

        service.saveMODashboardDataIntoCookie(moDashboardForm, response);

        verify(cookieUtil, times(1)).saveToCookie(response, MO_DASHBOARD_FORM_NAME, JsonUtil.getSerializedObject(moDashboardForm));
    }

    @Test
    public void getMODashboardFormCookieValue() throws Exception {
        MODashboardForm mODashboardForm = new MODashboardForm();
        Model model = mock(Model.class);

        when(cookieUtil.getCookieValue(request, MO_DASHBOARD_FORM_NAME)).thenReturn(JsonUtil.getSerializedObject(mODashboardForm));
        when(cookieUtil.getCookieValue(request, KEYWORD_SEARCH)).thenReturn(JsonUtil.getSerializedObject(mODashboardForm));
        when(cookieUtil.getCookieValue(request, PROJECT_IN_SETUP)).thenReturn(JsonUtil.getSerializedObject(mODashboardForm));
        when(cookieUtil.getCookieValue(request, PREVIOUS_PROJECT)).thenReturn(JsonUtil.getSerializedObject(mODashboardForm));

        MODashboardForm result = service.getMODashboardFormCookieValue(mODashboardForm, model, request);

        assertEquals(result, mODashboardForm);
        verify(cookieUtil, times(1)).getCookieValue(request, MO_DASHBOARD_FORM_NAME);
        verify(cookieUtil, times(1)).getCookieValue(request, KEYWORD_SEARCH);
        verify(cookieUtil, times(1)).getCookieValue(request, PROJECT_IN_SETUP);
        verify(cookieUtil, times(1)).getCookieValue(request, PREVIOUS_PROJECT);
    }

    @Test
    public void deleteMODashBoardDataFromCookie() throws Exception {
        service.deleteMODashBoardDataFromCookie(response);

        verify(cookieUtil, times(1)).removeCookie(response, MO_DASHBOARD_FORM_NAME);
    }
}