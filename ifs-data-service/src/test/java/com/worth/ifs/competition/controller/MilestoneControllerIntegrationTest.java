package com.worth.ifs.competition.controller;

import com.worth.ifs.BaseControllerIntegrationTest;
import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.competition.resource.MilestoneResource;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.isEmptyOrNullString;
import static org.junit.Assert.*;

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
    public void testGetAllDatesByCompetitionId() throws Exception {
        RestResult<List<MilestoneResource>> milestoneResult = controller.getAllDatesByCompetitionId(COMPETITION_ID_VALID);
        assertTrue(milestoneResult.isSuccess());
        List<MilestoneResource> milestone = milestoneResult.getSuccessObject();
        assertNotNull(milestone);
        assertTrue(milestone.size() == 5);
    }

    @Rollback
    @Test
    public void testEmptyGetAllDatesByCompetitionId() throws Exception {
        RestResult<List<MilestoneResource>> milestoneResult = controller.getAllDatesByCompetitionId(COMPETITION_ID_INVALID);
        assertTrue(milestoneResult.isSuccess());
        List<MilestoneResource> milestone = milestoneResult.getSuccessObject();
        assertTrue(milestone.isEmpty());
        assertNotNull(milestone);
    }

    @Rollback
    @Test
    public void testCreateMilestone() throws Exception {
        RestResult<List<MilestoneResource>> milestoneResult = controller.getAllDatesByCompetitionId(COMPETITION_ID_VALID);
        assertTrue(milestoneResult.isSuccess());

        List<MilestoneResource> milestones = milestoneResult.getSuccessObject();
        assertNotNull(milestones);
        assertTrue(milestones.size() == 5);
        Long id = milestones.get(milestones.size() -1).getId();

        MilestoneResource newMilestone = createNewMilestone();
        assertNotNull(newMilestone.getId());
        assertNull(newMilestone.getName());
        assertNull(newMilestone.getDate());
        assertNull(newMilestone.getCompetition());
    }

    @Rollback
    @Test
    public void testUpdateMilestone() throws Exception {

        RestResult<List<MilestoneResource>> milestoneResult = controller.getAllDatesByCompetitionId(COMPETITION_ID_VALID);
        assertTrue(milestoneResult.isSuccess());
        List<MilestoneResource> milestones = milestoneResult.getSuccessObject();

        MilestoneResource milestone = milestones.get(0);
        milestone.setName("testUpdate");
        milestone.setDate(LocalDateTime.now());

    }

    private MilestoneResource createNewMilestone() {
        RestResult<MilestoneResource> milestoneResult = controller.create();
        assertTrue(milestoneResult.isSuccess());
        return  milestoneResult.getSuccessObject();
    }
}
