package com.worth.ifs.application.controller;

import com.worth.ifs.BaseControllerIntegrationTest;
import com.worth.ifs.application.domain.Application;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;

import static org.junit.Assert.assertEquals;

public class ApplicationControllerIntegrationTest extends BaseControllerIntegrationTest<ApplicationController> {

    @Override
    @Autowired
    protected void setControllerUnderTest(ApplicationController controller) {
        this.controller = controller;
    }

    @Test
    @Rollback
    public void test_updateApplication() {

        String originalTitle= "A novel solution to an old problem";
        String newTitle = "A new title";

        Application application = controller.getApplicationById(1L);
        assertEquals(originalTitle, application.getName());

        application.setName(newTitle);
        controller.saveApplicationDetails(1L, application);

        Application updated = controller.getApplicationById(1L);
        assertEquals(newTitle, updated.getName());

    }
}
