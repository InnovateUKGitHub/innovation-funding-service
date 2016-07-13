package com.worth.ifs.competition.controller;

import com.worth.ifs.BaseControllerIntegrationTest;
import com.worth.ifs.competition.domain.Milestone;
import com.worth.ifs.competition.resource.CompetitionResource;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.Assert.assertTrue;

/**
 * Integration test for testing the rest servcies of the competition controller
 */
@Rollback
@Transactional
public class MilestoneControllerIntegrationTest extends BaseControllerIntegrationTest<MilestoneController> {

    private static final Long COMPETITION_ID = 7L;

    @Override
    protected void setControllerUnderTest(MilestoneController controller)  {
        this.controller = controller;
    }

    @Before
    public void setLoggedInUserOnThread() {
        loginCompAdmin();
    }

    @Rollback
    @Test
    public void testAllMilestoneDatesByCompetitionId() throws Exception {
        List<Milestone> milestone = (List<Milestone>) controller.findAllMilestonesByCompetitionId(COMPETITION_ID);
        assertTrue(milestone.size() == 5);

    }
}
