package org.innovateuk.ifs.application.forms.questions.horizon;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.application.forms.questions.horizon.model.HorizonWorkProgrammeSelectionData;
import org.innovateuk.ifs.horizon.resource.HorizonWorkProgrammeResource;
import org.innovateuk.ifs.util.EncryptedCookieService;
import org.innovateuk.ifs.util.JsonUtil;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.util.Optional;

import static org.innovateuk.ifs.application.forms.questions.horizon.HorizonWorkProgrammeCookieService.HORIZON_SELECTION_DATA;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class HorizonWorkProgrammeResourceCookieServiceTest extends BaseServiceUnitTest<HorizonWorkProgrammeCookieService> {

    @Mock
    private EncryptedCookieService encryptedCookieService;

    private MockHttpServletResponse response;
    private MockHttpServletRequest request;

    @Override
    protected HorizonWorkProgrammeCookieService supplyServiceUnderTest() {
        return new HorizonWorkProgrammeCookieService();
    }

    @Before
    public void setUp() {
        response = new MockHttpServletResponse();
        request = new MockHttpServletRequest();

        super.setup();
    }

    @Test
    public void saveWorkProgrammeSelectionData() {
        HorizonWorkProgrammeSelectionData horizonWorkProgrammeSelectionData = new HorizonWorkProgrammeSelectionData();

        service.saveWorkProgrammeSelectionData(horizonWorkProgrammeSelectionData, response);

        verify(encryptedCookieService).saveToCookie(response, HORIZON_SELECTION_DATA, JsonUtil.getSerializedObject(horizonWorkProgrammeSelectionData));
    }

    @Test
    public void getHorizonWorkProgrammeSelectionData() {

        HorizonWorkProgrammeSelectionData horizonWorkProgrammeSelectionData = new HorizonWorkProgrammeSelectionData();
        horizonWorkProgrammeSelectionData.setWorkProgramme(new HorizonWorkProgrammeResource(1, "CL2", true));

        when(encryptedCookieService.getCookieValue(request, HORIZON_SELECTION_DATA)).thenReturn(JsonUtil.getSerializedObject(horizonWorkProgrammeSelectionData));

        Optional<HorizonWorkProgrammeSelectionData> result = service.getHorizonWorkProgrammeSelectionData(request);

        assertTrue(result.isPresent());
        assertEquals(horizonWorkProgrammeSelectionData.getWorkProgramme(), result.get().getWorkProgramme());
        verify(encryptedCookieService).getCookieValue(request, HORIZON_SELECTION_DATA);
    }

    @Test
    public void deleteWorkProgrammeSelectionData() {

        service.deleteWorkProgrammeSelectionData(response);
        verify(encryptedCookieService).removeCookie(response, HORIZON_SELECTION_DATA);
    }
}