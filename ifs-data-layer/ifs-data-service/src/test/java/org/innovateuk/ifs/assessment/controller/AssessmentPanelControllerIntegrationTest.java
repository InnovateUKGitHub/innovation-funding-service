package org.innovateuk.ifs.assessment.controller;

import org.innovateuk.ifs.BaseControllerIntegrationTest;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.repository.ApplicationRepository;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class AssessmentPanelControllerIntegrationTest extends BaseControllerIntegrationTest<AssessmentPanelController> {

    @Autowired
    ApplicationRepository applicationRepository;

    @Override
    public void setControllerUnderTest(AssessmentPanelController controller) {
        this.controller = controller;
    }

    @Test
    public void assignApplication() throws Exception {

        loginCompAdmin();
        Application application = newApplication()
                .withId(2L)
                .build();
        applicationRepository.save(application);
        RestResult<Void> result = this.controller.assignApplication(application.getId());
        assertTrue(result.isSuccess());
        assertTrue(application.isInAssessmentPanel());

    }

    @Test
    public void unAssignApplication() throws Exception {
        loginCompAdmin();
        Application application = newApplication()
                .withId(2L)
                .build();
        applicationRepository.save(application);
        RestResult<Void> result = this.controller.unAssignApplication(application.getId());
        assertTrue(result.isSuccess());
        assertFalse(application.isInAssessmentPanel());
    }
}
