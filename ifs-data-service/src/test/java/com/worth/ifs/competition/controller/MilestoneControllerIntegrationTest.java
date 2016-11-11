package com.worth.ifs.competition.controller;

import com.worth.ifs.BaseControllerIntegrationTest;
import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.competition.resource.MilestoneResource;
import com.worth.ifs.competition.resource.MilestoneType;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.Assert.*;

/**
 * Integration test for testing the rest services of the milestone controller
 */
@Rollback
@Transactional
public class MilestoneControllerIntegrationTest extends BaseControllerIntegrationTest<MilestoneController> {

    private static final Long COMPETITION_ID_VALID = 1L;
    private static final Long COMPETITION_ID_NEW_MILESTONES = 2L;
    private static final Long COMPETITION_ID_UPDATE = 7L;
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
    public void testGetAllMilestonesByCompetitionId() throws Exception {
        RestResult<List<MilestoneResource>> milestoneResult = controller.getAllMilestonesByCompetitionId(COMPETITION_ID_VALID);
        assertTrue(milestoneResult.isSuccess());
        List<MilestoneResource> milestone = milestoneResult.getSuccessObject();
        assertNotNull(milestone);
        assertEquals(13, milestone.size());
    }

    @Rollback
    @Test
    public void testEmptyGetAllMilestonesByCompetitionId() throws Exception {
        List<MilestoneResource> milestone = getMilestonesForCompetition(COMPETITION_ID_INVALID);
        assertTrue(milestone.isEmpty());
        assertNotNull(milestone);
    }

    @Rollback
    @Test
    public void testGetDateByTypeAndCompetitionId() throws Exception {
        RestResult<MilestoneResource> milestoneResult = controller.getMilestoneByTypeAndCompetitionId(MilestoneType.BRIEFING_EVENT, COMPETITION_ID_VALID);
        assertTrue(milestoneResult.isSuccess());
        MilestoneResource milestone = milestoneResult.getSuccessObject();
        assertNotNull(milestone);
        assertEquals(LocalDateTime.of(2036, 3, 15, 9, 0), milestone.getDate());
    }

    @Rollback
    @Test
    public void testGetNullDateByTypeAndCompetitionId() throws Exception {
        RestResult<MilestoneResource> milestoneResult = controller.getMilestoneByTypeAndCompetitionId(MilestoneType.NOTIFICATIONS, COMPETITION_ID_VALID);
        assertTrue(milestoneResult.isSuccess());
        MilestoneResource milestone = milestoneResult.getSuccessObject();
        assertNull(milestone.getDate());
    }


    @Ignore
    // TODO this fails because there's no competition with id=2
    // there are only competitions with ids=1,7 so we need to create a new competition
    @Rollback
    @Test
    public void testCreateSingleMilestone() throws Exception {
        List<MilestoneResource> milestones = getMilestonesForCompetition(COMPETITION_ID_NEW_MILESTONES);

        assertNotNull(milestones);
        assertTrue(milestones.isEmpty());

        MilestoneResource newMilestone = createNewMilestone(MilestoneType.BRIEFING_EVENT, COMPETITION_ID_NEW_MILESTONES);

        assertNotNull(newMilestone.getId());
        assertTrue(newMilestone.getType().equals(MilestoneType.BRIEFING_EVENT));
        assertNull(newMilestone.getDate());
    }

    @Ignore
    // TODO this fails because there's no competition with id=2
    // there are only competitions with ids=1,7 so we need to create a new competition
    @Rollback
    @Test
    public void testCreateMilestones() throws Exception {
        List<MilestoneResource> milestones = getMilestonesForCompetition(COMPETITION_ID_NEW_MILESTONES);

        assertNotNull(milestones);
        assertTrue(milestones.isEmpty());

        List<MilestoneResource> newMilestones = createNewMilestones(COMPETITION_ID_NEW_MILESTONES);

        assertTrue(newMilestones.size() == 13);

        newMilestones.sort((c1, c2) -> c1.getType().compareTo(c2.getType()));

        newMilestones.forEach(m -> {
            assertNotNull(m.getId());
            assertNull(m.getDate());
        });
    }

    @Rollback
    @Test
    public void testUpdateMilestones() throws Exception {
        List<MilestoneResource> milestones = getMilestonesForCompetition(COMPETITION_ID_VALID);

        //Open date
        MilestoneResource milestone = milestones.get(0);
        milestone.setDate(LocalDateTime.of(2036, 03, 15, 9, 0));

        //Submission date
        milestone = milestones.get(1);
        milestone.setDate(LocalDateTime.of(2036, 03, 15, 9, 0));

        //Funders panel date
        milestone = milestones.get(2);
        milestone.setDate(LocalDateTime.of(2036, 03, 15, 9, 0));

        //Assesors accepts date
        milestone = milestones.get(3);
        milestone.setDate(LocalDateTime.of(2036, 03, 15, 9, 0));

        //Assessor deadline date
        milestone = milestones.get(4);
        milestone.setDate(LocalDateTime.of(2036, 03, 15, 9, 0));

        //Notifications date
        milestone = milestones.get(5);
        milestone.setDate(LocalDateTime.of(2036, 03, 15, 9, 0));

        RestResult<Void> milestoneResult = controller.saveMilestones(milestones, COMPETITION_ID_VALID);
        assertTrue(milestoneResult.isSuccess());
        assertTrue(milestoneResult.getErrors().isEmpty());
    }

    @Rollback
    @Test
    public void testUpdateMilestonesWithValidDateOrder() throws Exception {
        List<MilestoneResource> milestones = getMilestonesForCompetition(COMPETITION_ID_UPDATE);

        assertTrue(!milestones.isEmpty() && milestones.size() == 13);

        milestones.sort((c1, c2) -> c1.getType().compareTo(c2.getType()));

        LocalDateTime milestoneDate = LocalDateTime.now();
        milestones.forEach(milestone -> {
            assertNull(milestone.getDate());
            milestone.setDate(milestoneDate.plusDays(1));
        });

        RestResult<Void> milestoneResult = controller.saveMilestones(milestones, COMPETITION_ID_UPDATE);
        assertTrue(milestoneResult.isSuccess());
    }

    @Test
    @Rollback
    public void testUpdateSingleMilestone() throws Exception {
        MilestoneResource milestone = getMilestoneByCompetitionByType(COMPETITION_ID_UPDATE, MilestoneType.BRIEFING_EVENT);

        assertNotNull(milestone);

        LocalDateTime milestoneDate = LocalDateTime.now();
        milestone.setDate(milestoneDate.plusMonths(1));

        RestResult<Void> result = controller.saveMilestone(milestone);
        assertTrue(result.isSuccess());
        assertTrue(result.getErrors().isEmpty());
    }

    private MilestoneResource createNewMilestone(MilestoneType name, Long competitionId) {
        RestResult<MilestoneResource> milestoneResult = controller.create(name, competitionId);
        assertTrue(milestoneResult.isSuccess());
        return  milestoneResult.getSuccessObject();
    }

    private List<MilestoneResource> createNewMilestones(Long competitionId){
        List<MilestoneResource> newMilestones = new ArrayList<>();
        Stream.of(MilestoneType.values()).forEach(name -> {
            MilestoneResource newMilestone = createNewMilestone(name, competitionId);
            newMilestone.setType(name);
            newMilestones.add(newMilestone);
        });
        return newMilestones;
    }

    private List<MilestoneResource> getMilestonesForCompetition(Long competitionId){
        RestResult<List<MilestoneResource>> milestoneResult = controller.getAllMilestonesByCompetitionId(competitionId);
        assertTrue(milestoneResult.isSuccess());
        return milestoneResult.getSuccessObject();
    }

    private MilestoneResource getMilestoneByCompetitionByType(Long competitionId, MilestoneType type) {
        RestResult<MilestoneResource> milestoneResult = controller.getMilestoneByTypeAndCompetitionId(type, competitionId);
        assertTrue(milestoneResult.isSuccess());
        return milestoneResult.getSuccessObject();
    }
}
