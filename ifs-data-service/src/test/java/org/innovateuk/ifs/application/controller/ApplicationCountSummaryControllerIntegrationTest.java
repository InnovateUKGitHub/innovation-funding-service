package org.innovateuk.ifs.application.controller;

import org.innovateuk.ifs.BaseControllerIntegrationTest;
import org.innovateuk.ifs.application.resource.ApplicationCountSummaryResource;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class ApplicationCountSummaryControllerIntegrationTest extends BaseControllerIntegrationTest<ApplicationCountSummaryController> {

    @Autowired
    @Override
    protected void setControllerUnderTest(ApplicationCountSummaryController controller) {
        this.controller = controller;
    }

    @Test
    public void applicationCountSummariesByCompetitionId() {
        Long competitionId = 1L;
        loginCompAdmin();
        List<ApplicationCountSummaryResource> counts = controller.getApplicationCountSummariesByCompetitionId(competitionId).getSuccessObject();

        assertEquals(6, counts.size());
    }
}
