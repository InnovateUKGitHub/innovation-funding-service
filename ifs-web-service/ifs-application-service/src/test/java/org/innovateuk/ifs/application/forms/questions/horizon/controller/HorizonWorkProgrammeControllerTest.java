package org.innovateuk.ifs.application.forms.questions.horizon.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.application.forms.questions.horizon.HorizonWorkProgrammeCookieService;
import org.innovateuk.ifs.application.forms.questions.horizon.populator.HorizonWorkProgrammePopulator;
import org.innovateuk.ifs.application.service.QuestionStatusRestService;
import org.innovateuk.ifs.horizon.service.HorizonWorkProgrammeRestService;
import org.innovateuk.ifs.util.EncryptedCookieService;
import org.junit.Before;
import org.mockito.Mock;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

public class HorizonWorkProgrammeControllerTest extends BaseControllerMockMVCTest<HorizonWorkProgrammeController> {

    private static final String QUESTION_URL = "application/questions/horizon-work-programmes";

    @Mock
    private HorizonWorkProgrammeRestService restService;

    @Mock
    private HorizonWorkProgrammePopulator populator;

    @Mock
    private EncryptedCookieService encryptedCookieService;

    @Mock
    private HorizonWorkProgrammeCookieService horizonWorkProgrammeCookieService;

    @Mock
    private QuestionStatusRestService questionStatusRestService;

    private MockHttpServletResponse response;
    private MockHttpServletRequest request;

    @Before
    public void setUp() {
        response = new MockHttpServletResponse();
        request = new MockHttpServletRequest();

        super.setup();
    }

    @Override
    protected HorizonWorkProgrammeController supplyControllerUnderTest() {
        return new HorizonWorkProgrammeController();
    }


}
