package org.innovateuk.ifs.assessment.controller;

import org.innovateuk.ifs.BaseControllerIntegrationTest;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.repository.ApplicationRepository;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class AssessmentPanelControllerIntegrationTest extends BaseControllerIntegrationTest<AssessmentPanelController> {

    private long applicationId = 2L;
    private Application application;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    @Override
    public void setControllerUnderTest(AssessmentPanelController controller) {
        this.controller = controller;
    }

    @Before
    public void setUp() {
        loginCompAdmin();
    }

    @After
    public void clearDown() {
        flushAndClearSession();
    }

    @Test
    public void assignApplication() throws Exception {
        application = newApplication()
                .withId(applicationId)
                .withAssessmentPanelStatus(false)
                .build();
        applicationRepository.save(application);
        RestResult<Void> result = controller.assignApplication(application.getId());
        assertTrue(result.isSuccess());
        application = applicationRepository.findOne(applicationId);
        assertTrue(application.isInAssessmentPanel());

    }

    @Test
    public void unAssignApplication() throws Exception {
        application = newApplication()
                .withId(applicationId)
                .withAssessmentPanelStatus(true)
                .build();
        applicationRepository.save(application);
        RestResult<Void> result = controller.unAssignApplication(application.getId());
        assertTrue(result.isSuccess());
        application = applicationRepository.findOne(applicationId);
        assertFalse(application.isInAssessmentPanel());
    }
}
