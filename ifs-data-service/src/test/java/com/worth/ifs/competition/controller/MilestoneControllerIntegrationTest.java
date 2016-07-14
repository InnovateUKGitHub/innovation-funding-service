package com.worth.ifs.competition.controller;

import com.worth.ifs.BaseControllerIntegrationTest;
import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.competition.resource.MilestoneResource;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Integration test for testing the rest services of the milestone controller
 */
@Rollback
@Transactional
public class MilestoneControllerIntegrationTest extends BaseControllerIntegrationTest<MilestoneController> {

    private static final Long COMPETITION_ID_VALID = 7L;
    private static final Long COMPETITION_ID_INVALID = 8L;

    @Override
    @Autowired
    protected void setControllerUnderTest(MilestoneController controller)  {
        this.controller = controller;
    }

    @Before
    public void setLoggedInUserOnThread() {
        loginCompAdmin();
    }

    @Rollback
    @Test
    public void test_getAllDatesByCompetitionId() throws Exception {
        RestResult<List<MilestoneResource>> milestoneResult = controller.getAllDatesByCompetitionId(COMPETITION_ID_VALID);
        assertTrue(milestoneResult.isSuccess());
        List<MilestoneResource> milestone = milestoneResult.getSuccessObject();
        assertNotNull(milestone);
        assertTrue(milestone.size() == 5);
    }

    @Rollback
    @Test
    public void testEmpty_getAllDatesByCompetitionId() throws Exception {
        RestResult<List<MilestoneResource>> milestoneResult = controller.getAllDatesByCompetitionId(COMPETITION_ID_INVALID);
        assertTrue(milestoneResult.isSuccess());
        List<MilestoneResource> milestone = milestoneResult.getSuccessObject();
        assertTrue(milestone.isEmpty());
        assertNotNull(milestone);
    }
}
