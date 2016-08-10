package com.worth.ifs.competition.controller;

import com.google.common.collect.Sets;
import com.worth.ifs.BaseControllerIntegrationTest;
import com.worth.ifs.category.repository.CategoryLinkRepository;
import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.competition.domain.Competition;
import com.worth.ifs.competition.repository.CompetitionRepository;
import com.worth.ifs.competition.resource.CompetitionCountResource;
import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.competition.resource.CompetitionSetupSection;
import com.worth.ifs.util.fixtures.CompetitionCoFundersFixture;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

/**
 * Integration test for testing the rest servcies of the competition controller
 */
@Rollback
@Transactional
public class CompetitionControllerIntegrationTest extends BaseControllerIntegrationTest<CompetitionController> {
    public static final int EXISTING_CATEGORY_LINK_BEFORE_TEST = 2;
    @Autowired
    CategoryLinkRepository categoryLinkRepository;

    @Autowired
    CompetitionRepository competitionRepository;


    public static final String COMPETITION_NAME_UPDATED = "Competition name updated";
    public static final int INNOVATION_SECTOR_ID = 1;
    public static final String INNOVATION_SECTOR_NAME = "Health and life sciences";
    public static final int INNOVATION_AREA_ID = 9;
    public static final int INNOVATION_AREA_ID_TWO = 10;
    public static final String INNOVATION_AREA_NAME = "User Experience";
    public static final String EXISTING_COMPETITION_NAME = "Connected digital additive manufacturing";

    final LocalDateTime now = LocalDateTime.now();
    final LocalDateTime sixDaysAgo = now.minusDays(6);
    final LocalDateTime fiveDaysAgo = now.minusDays(5);
    final LocalDateTime fourDaysAgo = now.minusDays(4);
    final LocalDateTime threeDaysAgo = now.minusDays(3);
    final LocalDateTime twoDaysAgo = now.minusDays(2);
    final LocalDateTime oneDayAgo = now.minusDays(1);
    final LocalDateTime oneDayAhead = now.plusDays(1);
    final LocalDateTime twoDaysAhead = now.plusDays(2);
    final LocalDateTime threeDaysAhead = now.plusDays(3);
    final LocalDateTime fourDaysAhead = now.plusDays(4);
    final LocalDateTime fiveDaysAhead = now.plusDays(5);
    final LocalDateTime sixDaysAhead = now.plusDays(6);
    private static final Long COMPETITION_ID = 1L;

    @Override
    @Autowired
    protected void setControllerUnderTest(CompetitionController controller) {
        this.controller = controller;
    }

    @Before
    public void setLoggedInUserOnThread() {
        loginCompAdmin();
    }

    @Rollback
    @Test
    public void testGetAllCompetitions() throws Exception {
        List<CompetitionResource> competitions = getAllCompetitions(2);
        checkExistingCompetition(competitions.get(0));
    }

    @Rollback
    @Test
    public void testGetOneCompetitions() throws Exception {
        RestResult<CompetitionResource> competitionsResult = controller.getCompetitionById(COMPETITION_ID);
        assertTrue(competitionsResult.isSuccess());
        CompetitionResource competition = competitionsResult.getSuccessObject();

        checkExistingCompetition(competition);
    }


    @Rollback
    @Test
    public void testCreateCompetition() throws Exception {
        getAllCompetitions(2);
        createNewCompetition();

        int expectedCompetitionCount = 3;
        List<CompetitionResource> competitions = getAllCompetitions(expectedCompetitionCount);

        checkExistingCompetition(competitions.get(0));
        checkNewCompetition(competitions.get(1));
    }

    @Rollback
    @Test
    public void testCompetitionCodeGeneration() throws Exception {
        // Setup test data
        getAllCompetitions(2);
        createNewCompetition();
        createNewCompetition();
        createNewCompetition();
        List<CompetitionResource> competitions = getAllCompetitions(5);

        // Generate number 1 in this month year combination
        RestResult<String> generatedCode = controller.generateCompetitionCode(LocalDateTime.of(2016, 6, 5, 12, 00), competitions.get(0).getId());
        assertTrue(generatedCode.isSuccess());
        assertEquals("1606-1", generatedCode.getSuccessObject());
        flushAndClearSession();


        // Generate number 2 in this month year combination
        generatedCode = controller.generateCompetitionCode(LocalDateTime.of(2016, 6, 5, 12, 00), competitions.get(1).getId());
        assertTrue(generatedCode.isSuccess());
        assertEquals("1606-2", generatedCode.getSuccessObject());
        flushAndClearSession();

        // Generate number 3 in this month year combination
        generatedCode = controller.generateCompetitionCode(LocalDateTime.of(2016, 6, 5, 12, 00), competitions.get(2).getId());
        assertTrue(generatedCode.isSuccess());
        assertEquals("1606-3", generatedCode.getSuccessObject());

        // if generated twice the first code should not be updated.
        generatedCode = controller.generateCompetitionCode(LocalDateTime.of(2020, 11, 11, 12, 00), competitions.get(2).getId());
        assertTrue(generatedCode.isSuccess());
        assertEquals("1606-3", generatedCode.getSuccessObject());

    }

    private List<CompetitionResource> getAllCompetitions(int expectedCompetitionCount) {
        RestResult<List<CompetitionResource>> allCompetitionsResult = controller.findAll();
        assertTrue(allCompetitionsResult.isSuccess());
        List<CompetitionResource> competitions = allCompetitionsResult.getSuccessObject();
        assertThat("Checking if the amount of competitions is what we expect.", competitions, hasSize(expectedCompetitionCount));
        return competitions;
    }

    @Rollback
    @Test
    public void testUpdateCompetition() throws Exception {
        getAllCompetitions(2);

        // Create new competition
        CompetitionResource competition = createNewCompetition();

        getAllCompetitions(3);

        // Update competition
        competition.setName(COMPETITION_NAME_UPDATED);
        RestResult<CompetitionResource> saveResult = controller.saveCompetition(competition, competition.getId());
        assertTrue("Assert save is success", saveResult.isSuccess());

        getAllCompetitions(3);

        CompetitionResource savedCompetition = saveResult.getSuccessObject();
        assertEquals(COMPETITION_NAME_UPDATED, savedCompetition.getName());
    }

    @Rollback
    @Test
    public void testUpdateCompetitionCategories() throws Exception {
        getAllCompetitions(2);

        // Create new competition
        CompetitionResource competition = createNewCompetition();

        getAllCompetitions(3);

        // Update competition
        competition.setName(COMPETITION_NAME_UPDATED);
        Long sectorId = Long.valueOf(INNOVATION_SECTOR_ID);
        Long areaId = Long.valueOf(INNOVATION_AREA_ID);
        competition.setInnovationSector(sectorId);
        competition.setInnovationArea(areaId);
        RestResult<CompetitionResource> saveResult = controller.saveCompetition(competition, competition.getId());
        assertTrue("Assert save is success", saveResult.isSuccess());

        getAllCompetitions(3);

        CompetitionResource savedCompetition = saveResult.getSuccessObject();
        checkUpdatedCompetitionCategories(savedCompetition);
    }


    @Rollback
    @Test
    public void testUpdateCompetitionCoFunders() throws Exception {
        getAllCompetitions(2);

        // Create new competition
        CompetitionResource competition = createNewCompetition();

        getAllCompetitions(3);

        // Update competition
        competition.setName(COMPETITION_NAME_UPDATED);
        Long sectorId = Long.valueOf(INNOVATION_SECTOR_ID);
        Long areaId = Long.valueOf(INNOVATION_AREA_ID);
        competition.setInnovationSector(sectorId);
        competition.setInnovationArea(areaId);

        //With one co-funder
        competition.setCoFunders(CompetitionCoFundersFixture.getNewTestCoFundersResouces(1, competition.getId()));
        RestResult<CompetitionResource> saveResult = controller.saveCompetition(competition, competition.getId());
        assertTrue("Assert save is success", saveResult.isSuccess());
        getAllCompetitions(3);
        CompetitionResource savedCompetition = saveResult.getSuccessObject();
        assertEquals(1, savedCompetition.getCoFunders().size());

        // Now re-insert with 2 co-funders
        competition.setCoFunders(CompetitionCoFundersFixture.getNewTestCoFundersResouces(2, competition.getId()));
        saveResult = controller.saveCompetition(competition, competition.getId());
        assertTrue("Assert save is success", saveResult.isSuccess());
        savedCompetition = saveResult.getSuccessObject();
        // we should expect in total two co-funders.
        assertEquals(2, savedCompetition.getCoFunders().size());
    }

    @Rollback
    @Test
    public void testCompetitionCategorySaving() throws Exception {
        getAllCompetitions(2);
        // Create new competition
        CompetitionResource competition = createNewCompetition();
        getAllCompetitions(3);
        assertEquals(EXISTING_CATEGORY_LINK_BEFORE_TEST, categoryLinkRepository.count());


        // Update competition
        competition.setName(COMPETITION_NAME_UPDATED);
        Long sectorId = Long.valueOf(INNOVATION_SECTOR_ID);
        Long areaId = Long.valueOf(INNOVATION_AREA_ID);
        competition.setInnovationSector(sectorId);
        competition.setInnovationArea(areaId);
        // Check if the categorylink is only stored once.
        RestResult<CompetitionResource> saveResult = controller.saveCompetition(competition, competition.getId());
        assertTrue("Assert save is success", saveResult.isSuccess());
        assertEquals(EXISTING_CATEGORY_LINK_BEFORE_TEST + 2, categoryLinkRepository.count());

        CompetitionResource savedCompetition = saveResult.getSuccessObject();
        checkUpdatedCompetitionCategories(savedCompetition);

        // check that the link is not duplicated
        saveResult = controller.saveCompetition(competition, competition.getId());
        assertTrue("Assert save is success", saveResult.isSuccess());
        assertEquals(EXISTING_CATEGORY_LINK_BEFORE_TEST + 2, categoryLinkRepository.count());

        // check that the link is removed
        competition.setInnovationSector(null);
        saveResult = controller.saveCompetition(competition, competition.getId());
        assertTrue("Assert save is success", saveResult.isSuccess());
        assertEquals(EXISTING_CATEGORY_LINK_BEFORE_TEST + 1, categoryLinkRepository.count());

        // check that the link is updated (or removed and added)
        competition.setInnovationArea(Long.valueOf(INNOVATION_AREA_ID_TWO));
        saveResult = controller.saveCompetition(competition, competition.getId());
        assertTrue("Assert save is success", saveResult.isSuccess());
        assertEquals(EXISTING_CATEGORY_LINK_BEFORE_TEST + 1, categoryLinkRepository.count());

        getAllCompetitions(3);
    }

    @Rollback
    @Test
    public void testCompetitionCompletedSections() throws Exception {
        Long competitionId = 7L;
        RestResult<CompetitionResource> competitionsResult = controller.getCompetitionById(competitionId);
        
        assertEquals(0, competitionsResult.getSuccessObject().getSectionSetupStatus().size());
    }

    @Rollback
    @Test
    public void testCompetitionCompleteSection() throws Exception {
    	Long competitionId = 7L;
    	
    	controller.markSectionComplete(competitionId, CompetitionSetupSection.INITIAL_DETAILS);
    	
        RestResult<CompetitionResource> competitionsResult = controller.getCompetitionById(competitionId);
        assertEquals(Boolean.TRUE, competitionsResult.getSuccessObject().getSectionSetupStatus().get(CompetitionSetupSection.INITIAL_DETAILS));
    }

    @Rollback
    @Test
    public void testCompetitionInCompleteSection() throws Exception {
    	Long competitionId = 7L;
    	
    	controller.markSectionInComplete(competitionId, CompetitionSetupSection.INITIAL_DETAILS);
    	
        RestResult<CompetitionResource> competitionsResult = controller.getCompetitionById(competitionId);
        assertEquals(Boolean.FALSE, competitionsResult.getSuccessObject().getSectionSetupStatus().get(CompetitionSetupSection.INITIAL_DETAILS));
    }

    @Rollback
    @Test
    public void testFindMethods() throws Exception {
        List<CompetitionResource> existingComps = getAllCompetitions(2);

        CompetitionResource notStartedCompetition = createWithDates(oneDayAhead, twoDaysAhead, threeDaysAhead, fourDaysAhead, fiveDaysAhead, sixDaysAhead);
        assertThat(notStartedCompetition.getCompetitionStatus(), equalTo(CompetitionResource.Status.NOT_STARTED));

        CompetitionResource openCompetition = createWithDates(oneDayAgo, oneDayAhead, twoDaysAhead, threeDaysAhead, fourDaysAhead, fiveDaysAhead);
        assertThat(openCompetition.getCompetitionStatus(), equalTo(CompetitionResource.Status.OPEN));

        CompetitionResource closedCompetition = createWithDates(twoDaysAgo, oneDayAgo, twoDaysAhead, threeDaysAhead, fourDaysAhead, fiveDaysAhead);
        assertThat(closedCompetition.getCompetitionStatus(), equalTo(CompetitionResource.Status.CLOSED));

        CompetitionResource inAssessmentCompetition = createWithDates(threeDaysAgo, twoDaysAgo, oneDayAgo, threeDaysAhead, fourDaysAhead, fiveDaysAhead);
        assertThat(inAssessmentCompetition.getCompetitionStatus(), equalTo(CompetitionResource.Status.IN_ASSESSMENT));

        CompetitionResource inPanelCompetition = createWithDates(fourDaysAgo, threeDaysAgo, twoDaysAgo, oneDayAgo, fourDaysAhead, fiveDaysAhead);
        assertThat(inPanelCompetition.getCompetitionStatus(), equalTo(CompetitionResource.Status.FUNDERS_PANEL));

        CompetitionResource assessorFeedbackCompetition = createWithDates(fiveDaysAgo, fourDaysAgo, threeDaysAgo, twoDaysAgo, oneDayAgo, fiveDaysAhead);
        assertThat(assessorFeedbackCompetition.getCompetitionStatus(), equalTo(CompetitionResource.Status.ASSESSOR_FEEDBACK));

        CompetitionResource projectSetup = createWithDates(sixDaysAgo, fiveDaysAgo, fourDaysAgo, threeDaysAgo, twoDaysAgo, oneDayAgo);
        assertThat(projectSetup.getCompetitionStatus(), equalTo(CompetitionResource.Status.PROJECT_SETUP));

        CompetitionCountResource counts = controller.count().getSuccessObjectOrThrowException();

        List<CompetitionResource> liveCompetitions = controller.live().getSuccessObjectOrThrowException();

        Set<Long> liveCompetitionIds = Sets.newHashSet(openCompetition.getId(),
                closedCompetition.getId(), inAssessmentCompetition.getId(), inPanelCompetition.getId(),
                assessorFeedbackCompetition.getId());
        Set<Long> notLiveCompetitionIds = Sets.newHashSet(notStartedCompetition.getId(), projectSetup.getId());

        //Live competitions plus one the test data.
        assertThat(liveCompetitions.size(), equalTo(liveCompetitionIds.size() + 1));
        assertThat(counts.getLiveCount(), equalTo((long) (liveCompetitionIds.size() + 1)));

        liveCompetitions.stream().forEach(competitionResource -> {
            //Existing competitions in the db should be ignored.
            if (!existingComps.get(0).getId().equals(competitionResource.getId())
                    && !existingComps.get(1).getId().equals(competitionResource.getId())) {
                assertTrue(liveCompetitionIds.contains(competitionResource.getId()));
                assertFalse(notLiveCompetitionIds.contains(competitionResource.getId()));
            }
        });

        List<CompetitionResource> projectSetupCompetitions = controller.projectSetup().getSuccessObjectOrThrowException();

        Set<Long> projectSetupCompetitionIds = Sets.newHashSet(projectSetup.getId());
        Set<Long> notProjectSetupCompetitionIds = Sets.newHashSet(notStartedCompetition.getId(), openCompetition.getId(),
                closedCompetition.getId(), inAssessmentCompetition.getId(), inPanelCompetition.getId(),
                assessorFeedbackCompetition.getId());

        assertThat(projectSetupCompetitions.size(), equalTo(projectSetupCompetitionIds.size()));
        assertThat(counts.getProjectSetupCount(), equalTo((long) projectSetupCompetitionIds.size()));

        projectSetupCompetitions.stream().forEach(competitionResource -> {
            assertTrue(projectSetupCompetitionIds.contains(competitionResource.getId()));
            assertFalse(notProjectSetupCompetitionIds.contains(competitionResource.getId()));
        });

        List<CompetitionResource> upcomingCompetitions = controller.upcoming().getSuccessObjectOrThrowException();

        //One existing comp is upcoming and the new one.
        assertThat(upcomingCompetitions.size(), equalTo(2));
        assertThat(counts.getUpcomingCount(), equalTo(2L));
        Set<Long> upcomingCompetitionIds = Sets.newHashSet(notStartedCompetition.getId());
        Set<Long> notUpcomingCompetitionIds = Sets.newHashSet(projectSetup.getId(), openCompetition.getId(),
                closedCompetition.getId(), inAssessmentCompetition.getId(), inPanelCompetition.getId(),
                assessorFeedbackCompetition.getId());

        upcomingCompetitions.stream().forEach(competitionResource -> {
            //Existing competitions in the db should be ignored.
            if (!existingComps.get(0).getId().equals(competitionResource.getId())
                    && !existingComps.get(1).getId().equals(competitionResource.getId())) {
                assertTrue(upcomingCompetitionIds.contains(competitionResource.getId()));
                assertFalse(notUpcomingCompetitionIds.contains(competitionResource.getId()));
            }
        });

   }


    @Rollback
    @Test
    public void testInitApplicationFormByType() throws Exception {
        Long competitionId = 7L;
        Long competitionTypeId = 1L;

        controller.initialiseForm(competitionId, competitionTypeId);

        RestResult<CompetitionResource> competitionsResult = controller.getCompetitionById(competitionId);
        assertEquals(Boolean.TRUE, competitionsResult.getSuccessObject().getQuestions().size() > 0);
        assertEquals(competitionTypeId, competitionsResult.getSuccessObject().getCompetitionType());
    }

   private CompetitionResource createWithDates(LocalDateTime startDate,
                                               LocalDateTime endDate,
                                               LocalDateTime assessmentStartDate,
                                               LocalDateTime assessmentEndDate,
                                               LocalDateTime fundersPanelEndDate,
                                               LocalDateTime assessorFeedbackDate) {
       CompetitionResource comp = controller.create().getSuccessObjectOrThrowException();

       comp.setStartDate(startDate);
       comp.setEndDate(endDate);
       comp.setAssessmentStartDate(assessmentStartDate);
       comp.setAssessmentEndDate(assessmentEndDate);
       comp.setFundersPanelEndDate(fundersPanelEndDate);
       comp.setAssessorFeedbackDate(assessorFeedbackDate);

       controller.saveCompetition(comp, comp.getId()).getSuccessObjectOrThrowException();

       //TODO replace with controller endpoint for competition setup finished
       Competition compEntity = competitionRepository.findById(comp.getId());
       compEntity.setStatus(CompetitionResource.Status.COMPETITION_SETUP_FINISHED);
       competitionRepository.save(compEntity);
       flushAndClearSession();

       return controller.getCompetitionById(comp.getId()).getSuccessObjectOrThrowException();
   }

    private CompetitionResource createNewCompetition() {
        RestResult<CompetitionResource> competitionsResult = controller.create();
        assertTrue(competitionsResult.isSuccess());
        CompetitionResource competition = competitionsResult.getSuccessObject();
        assertThat(competition.getName(), isEmptyOrNullString());
        return competition;
    }


    private void checkUpdatedCompetitionCategories(CompetitionResource savedCompetition) {
        assertEquals(COMPETITION_NAME_UPDATED, savedCompetition.getName());

        assertEquals(INNOVATION_SECTOR_ID, (long) savedCompetition.getInnovationSector());
        assertEquals(INNOVATION_SECTOR_NAME, savedCompetition.getInnovationSectorName());

        assertEquals(INNOVATION_AREA_ID, (long) savedCompetition.getInnovationArea());
        assertEquals(INNOVATION_AREA_NAME, savedCompetition.getInnovationAreaName());
    }

    private void checkExistingCompetition(CompetitionResource competition) {
        assertThat(competition, notNullValue());
        assertThat(competition.getName(), is(EXISTING_COMPETITION_NAME));
        assertThat(competition.getCompetitionStatus(), is(CompetitionResource.Status.OPEN));
    }

    private void checkNewCompetition(CompetitionResource competition) {
        assertThat(competition, notNullValue());
        assertThat(competition.getName(), isEmptyOrNullString());
        assertThat(competition.getCompetitionStatus(), is(CompetitionResource.Status.COMPETITION_SETUP));
    }
}
