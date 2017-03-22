package org.innovateuk.ifs.application.controller;

import org.innovateuk.ifs.BaseControllerIntegrationTest;
import org.innovateuk.ifs.application.resource.ApplicationCountSummaryPageResource;
import org.innovateuk.ifs.application.resource.ApplicationCountSummaryResource;
import org.innovateuk.ifs.application.resource.ApplicationSummaryPageResource;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

import static java.util.Optional.empty;
import static java.util.Optional.ofNullable;
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
        ApplicationCountSummaryPageResource counts = controller.getApplicationCountSummariesByCompetitionId(competitionId,0,3, empty()).getSuccessObject();

        assertEquals(5, counts.getTotalElements());
        assertEquals(0, counts.getNumber());
        assertEquals(2, counts.getTotalPages());
        assertEquals(3, counts.getContent().size());

    }

    @Test
    public void applicationCountSummariesBuCompetitionIdFiltered() {
        Long competitionId = 1L;
        loginCompAdmin();

        ApplicationCountSummaryPageResource counts = controller.getApplicationCountSummariesByCompetitionId(competitionId, 0, 6, ofNullable("3")).getSuccessObject();

        assertEquals(1, counts.getTotalElements());
        assertEquals(0, counts.getNumber());
        assertEquals(1, counts.getTotalPages());
        ApplicationCountSummaryResource summaryResource = counts.getContent().get(0);
        assertEquals(3, (long)summaryResource.getId());
        assertEquals("Mobile Phone Data for Logistics Analytics", summaryResource.getName());
        assertEquals("Empire Ltd", summaryResource.getLeadOrganisation());
        assertEquals(3, summaryResource.getAssessors());
        assertEquals(2, summaryResource.getAccepted());
        assertEquals(2, summaryResource.getSubmitted());
    }
}
