package org.innovateuk.ifs.competition.controller;

import org.innovateuk.ifs.BaseControllerIntegrationTest;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.competition.domain.AssessmentPeriod;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.repository.AssessmentPeriodRepository;
import org.innovateuk.ifs.competition.repository.CompetitionRepository;
import org.innovateuk.ifs.competition.resource.MilestoneResource;
import org.innovateuk.ifs.competition.resource.MilestoneType;
import org.innovateuk.ifs.user.domain.User;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.innovateuk.ifs.competition.builder.AssessmentPeriodBuilder.newAssessmentPeriod;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.user.builder.UserBuilder.newUser;
import static org.junit.Assert.*;

/**
 * Integration test for testing the rest services of the milestone controller
 */
public class MilestoneControllerIntegrationTest extends BaseControllerIntegrationTest<MilestoneController> {

    private static final Long COMPETITION_ID_VALID = 1L;
    private static final Long COMPETITION_ID_UPDATE = 7L;

    @Autowired
    private CompetitionRepository competitionRepository;

    @Autowired
    private AssessmentPeriodRepository assessmentPeriodRepository;

    @Override
    @Autowired
    protected void setControllerUnderTest(MilestoneController controller) {
        this.controller = controller;
    }

    @Before
    public void setLoggedInUserOnThread() {
        loginCompAdmin();
    }

    @Test
    public void testGetAllMilestonesByCompetitionId() throws Exception {
        RestResult<List<MilestoneResource>> milestoneResult = controller.getAllMilestonesByCompetitionId(COMPETITION_ID_VALID);
        assertTrue(milestoneResult.isSuccess());
        List<MilestoneResource> milestone = milestoneResult.getSuccess();
        assertNotNull(milestone);
        assertEquals(15, milestone.size());
    }

    @Test
    public void testGetAllPublicMilestonesByCompetitionId() throws Exception {
        loginSystemRegistrationUser();
        RestResult<List<MilestoneResource>> milestoneResult = controller.getAllPublicMilestonesByCompetitionId(COMPETITION_ID_VALID);
        assertTrue(milestoneResult.isSuccess());
        List<MilestoneResource> milestone = milestoneResult.getSuccess();
        assertNotNull(milestone);
        assertEquals(3, milestone.size());
    }

    @Test
    public void testGetMilestoneByTypeAndCompetitionId() throws Exception {
        RestResult<MilestoneResource> milestoneResult = controller.getMilestoneByTypeAndCompetitionId(MilestoneType.BRIEFING_EVENT, COMPETITION_ID_VALID);
        assertTrue(milestoneResult.isSuccess());
        MilestoneResource milestone = milestoneResult.getSuccess();
        assertNotNull(milestone);
        assertEquals(ZonedDateTime.of(2036, 3, 15, 9, 0, 0, 0, ZoneId.systemDefault()), milestone.getDate());
    }

    @Test
    public void testGetMilestoneByTypeAndCompetitionIdWithNullDate() throws Exception {
        RestResult<MilestoneResource> milestoneResult = controller.getMilestoneByTypeAndCompetitionId(MilestoneType.LINE_DRAW, COMPETITION_ID_VALID);
        assertTrue(milestoneResult.isSuccess());
        MilestoneResource milestone = milestoneResult.getSuccess();
        assertNull(milestone.getDate());
    }

    @Test
    public void testCreateSingleMilestone() throws Exception {
        Competition newCompetition = competitionRepository.save(newCompetition().withId((Long) null).build());

        List<MilestoneResource> milestones = getMilestonesForCompetition(newCompetition.getId());
        assertNotNull(milestones);
        assertTrue(milestones.isEmpty());

        MilestoneResource newMilestone = createNewMilestone(MilestoneType.BRIEFING_EVENT, newCompetition.getId());

        assertNotNull(newMilestone.getId());
        assertEquals(MilestoneType.BRIEFING_EVENT, newMilestone.getType());
        assertNull(newMilestone.getDate());
    }

    @Test
    public void testCreateMilestones() throws Exception {
        Competition newCompetition = competitionRepository.save(newCompetition().withId((Long) null).build());

        List<MilestoneResource> milestones = getMilestonesForCompetition(newCompetition.getId());

        assertNotNull(milestones);
        assertTrue(milestones.isEmpty());

        List<MilestoneResource> newMilestones = createNewMilestones(newCompetition.getId());

        assertEquals(MilestoneType.values().length, newMilestones.size());

        newMilestones.sort((c1, c2) -> c1.getType().compareTo(c2.getType()));

        newMilestones.forEach(m -> {
            assertNotNull(m.getId());
            assertNull(m.getDate());
        });
    }

    @Test
    public void testCreateSingleMilestoneWithAssessmentPeriod() {
        User compAdminUser = newUser().withId(getLoggedInUser().getId()).build();
        Competition newCompetition = competitionRepository.save(
                newCompetition().withId((Long) null).withCreatedBy(compAdminUser).build());
        AssessmentPeriod newAssessmentPeriod = assessmentPeriodRepository.save(
                newAssessmentPeriod().withCompetition(newCompetition).withIndex(1).build());

        List<MilestoneResource> milestones = getMilestonesForCompetition(newCompetition.getId());
        assertNotNull(milestones);
        assertTrue(milestones.isEmpty());

        MilestoneResource newMilestone = createNewMilestoneWithAssessmentPeriod(MilestoneType.ASSESSOR_BRIEFING, newCompetition.getId(), newAssessmentPeriod.getId());

        assertNotNull(newMilestone.getId());
        assertEquals(MilestoneType.ASSESSOR_BRIEFING, newMilestone.getType());
        assertNull(newMilestone.getDate());
        assertEquals(newAssessmentPeriod.getId(), newMilestone.getAssessmentPeriodId());
    }

    @Test
    public void testCreateMilestonesWithAssessmentPeriod() {
        User compAdminUser = newUser().withId(getLoggedInUser().getId()).build();
        Competition newCompetition = competitionRepository.save(
                newCompetition().withId((Long) null).withCreatedBy(compAdminUser).build());
        AssessmentPeriod newAssessmentPeriod = assessmentPeriodRepository.save(
                newAssessmentPeriod().withId((Long) null).withCompetition(newCompetition).withIndex(1).build());

        List<MilestoneResource> milestones = getMilestonesForCompetition(newCompetition.getId());

        assertNotNull(milestones);
        assertTrue(milestones.isEmpty());

        List<MilestoneResource> newMilestones = createNewMilestonesWithAssessmentPeriod(newCompetition.getId(), newAssessmentPeriod.getId());

        assertEquals(MilestoneType.alwaysOpenValues().length, newMilestones.size());

        newMilestones.forEach(m -> {
            assertNotNull(m.getId());
            assertNull(m.getDate());
            assertEquals(newAssessmentPeriod.getId(), m.getAssessmentPeriodId());
        });
    }

    @Test
    public void testUpdateMilestones() throws Exception {
        List<MilestoneResource> milestones = getMilestonesForCompetition(COMPETITION_ID_VALID);

        //Open date
        MilestoneResource milestone = milestones.get(0);
        milestone.setDate(ZonedDateTime.of(2036, 03, 15, 9, 0, 0, 0, ZoneId.systemDefault()));

        //Submission date
        milestone = milestones.get(1);
        milestone.setDate(ZonedDateTime.of(2036, 03, 15, 9, 0, 0, 0, ZoneId.systemDefault()));

        //Funders panel date
        milestone = milestones.get(2);
        milestone.setDate(ZonedDateTime.of(2036, 03, 15, 9, 0, 0, 0, ZoneId.systemDefault()));

        //Assesors accepts date
        milestone = milestones.get(3);
        milestone.setDate(ZonedDateTime.of(2036, 03, 15, 9, 0, 0, 0, ZoneId.systemDefault()));

        //Assessor deadline date
        milestone = milestones.get(4);
        milestone.setDate(ZonedDateTime.of(2036, 03, 15, 9, 0, 0, 0, ZoneId.systemDefault()));

        //Notifications date
        milestone = milestones.get(5);
        milestone.setDate(ZonedDateTime.of(2036, 03, 15, 9, 0, 0, 0, ZoneId.systemDefault()));

        //Line draw
        milestone = milestones.get(9);
        milestone.setDate(ZonedDateTime.of(2036, 03, 15, 9, 0, 0, 0, ZoneId.systemDefault()));

        RestResult<Void> milestoneResult = controller.saveMilestones(milestones);
        assertTrue(milestoneResult.isSuccess());
        assertTrue(milestoneResult.getErrors().isEmpty());
    }

    @Test
    public void testUpdateMilestonesWithValidDateOrder() throws Exception {
        List<MilestoneResource> milestones = getMilestonesForCompetition(COMPETITION_ID_UPDATE);

        assertTrue(!milestones.isEmpty() && milestones.size() == 15);

        milestones.sort((c1, c2) -> c1.getType().compareTo(c2.getType()));

        ZonedDateTime milestoneDate = ZonedDateTime.now();
        milestones.forEach(milestone -> {
            assertNull(milestone.getDate());
            milestone.setDate(milestoneDate.plusDays(1));
        });

        RestResult<Void> milestoneResult = controller.saveMilestones(milestones);
        assertTrue(milestoneResult.isSuccess());
    }

    @Test
    public void testUpdateSingleMilestone() throws Exception {
        MilestoneResource milestone = getMilestoneByCompetitionByType(COMPETITION_ID_UPDATE, MilestoneType.BRIEFING_EVENT);

        assertNotNull(milestone);

        ZonedDateTime milestoneDate = ZonedDateTime.now();
        milestone.setDate(milestoneDate.plusMonths(1));

        RestResult<Void> result = controller.saveMilestone(milestone);
        assertTrue(result.isSuccess());
        assertTrue(result.getErrors().isEmpty());
    }

    private MilestoneResource createNewMilestone(MilestoneType name, Long competitionId) {
        RestResult<MilestoneResource> milestoneResult = controller.create(name, competitionId);
        assertTrue(milestoneResult.isSuccess());
        return milestoneResult.getSuccess();
    }

    private List<MilestoneResource> createNewMilestones(Long competitionId) {
        List<MilestoneResource> newMilestones = new ArrayList<>();
        Stream.of(MilestoneType.values()).forEach(name -> {
            MilestoneResource newMilestone = createNewMilestone(name, competitionId);
            newMilestone.setType(name);
            newMilestones.add(newMilestone);
        });
        return newMilestones;
    }

    private MilestoneResource createNewMilestoneWithAssessmentPeriod(MilestoneType name, Long competitionId, Long assessmentPeriodId) {
        RestResult<MilestoneResource> milestoneResult = controller.create(name, assessmentPeriodId, competitionId);
        assertTrue(milestoneResult.isSuccess());
        return milestoneResult.getSuccess();
    }

    private List<MilestoneResource> createNewMilestonesWithAssessmentPeriod(Long competitionId, Long assessmentPeriodId) {
        List<MilestoneResource> newMilestones = new ArrayList<>();
        Stream.of(MilestoneType.alwaysOpenValues()).forEach(name -> {
            MilestoneResource newMilestone = createNewMilestoneWithAssessmentPeriod(name, competitionId, assessmentPeriodId);
            newMilestone.setType(name);
            newMilestones.add(newMilestone);
        });
        return newMilestones;
    }

    private List<MilestoneResource> getMilestonesForCompetition(Long competitionId) {
        RestResult<List<MilestoneResource>> milestoneResult = controller.getAllMilestonesByCompetitionId(competitionId);
        assertTrue(milestoneResult.isSuccess());
        return milestoneResult.getSuccess();
    }

    private MilestoneResource getMilestoneByCompetitionByType(Long competitionId, MilestoneType type) {
        RestResult<MilestoneResource> milestoneResult = controller.getMilestoneByTypeAndCompetitionId(type, competitionId);
        assertTrue(milestoneResult.isSuccess());
        return milestoneResult.getSuccess();
    }
}
