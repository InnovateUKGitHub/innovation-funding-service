package com.worth.ifs.competition.controller;

import com.google.common.collect.Sets;
import com.worth.ifs.BaseControllerIntegrationTest;
import com.worth.ifs.category.repository.CategoryLinkRepository;
import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.competition.domain.Competition;
import com.worth.ifs.competition.domain.Milestone;
import com.worth.ifs.competition.repository.CompetitionRepository;
import com.worth.ifs.competition.repository.MilestoneRepository;
import com.worth.ifs.competition.resource.*;
import com.worth.ifs.util.fixtures.CompetitionCoFundersResourceFixture;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

/**
 * Integration test for testing the rest servcies of the competition controller
 */
public class CompetitionControllerIntegrationTest extends BaseControllerIntegrationTest<CompetitionController> {

    @Autowired
    private CategoryLinkRepository categoryLinkRepository;

    @Autowired
    private CompetitionRepository competitionRepository;

    @Autowired
    private MilestoneRepository milestoneRepository;

    private static final int EXISTING_CATEGORY_LINK_BEFORE_TEST = 2;
    private static final Long COMPETITION_ID = 1L;

    private static final String COMPETITION_NAME_UPDATED = "Competition name updated";
    private static final int INNOVATION_SECTOR_ID = 1;
    private static final String INNOVATION_SECTOR_NAME = "Health and life sciences";
    private static final int INNOVATION_AREA_ID = 9;
    private static final int INNOVATION_AREA_ID_TWO = 10;
    private static final String INNOVATION_AREA_NAME = "User Experience";
    private static final String EXISTING_COMPETITION_NAME = "Connected digital additive manufacturing";

    private final LocalDateTime now = LocalDateTime.now();
    private final LocalDateTime eightDaysAgo = now.minusDays(8);
    private final LocalDateTime sevenDaysAgo = now.minusDays(7);
    private final LocalDateTime sixDaysAgo = now.minusDays(6);
    private final LocalDateTime fiveDaysAgo = now.minusDays(5);
    private final LocalDateTime fourDaysAgo = now.minusDays(4);
    private final LocalDateTime threeDaysAgo = now.minusDays(3);
    private final LocalDateTime twoDaysAgo = now.minusDays(2);
    private final LocalDateTime oneDayAgo = now.minusDays(1);
    private final LocalDateTime oneDayAhead = now.plusDays(1);
    private final LocalDateTime twoDaysAhead = now.plusDays(2);
    private final LocalDateTime threeDaysAhead = now.plusDays(3);
    private final LocalDateTime fourDaysAhead = now.plusDays(4);
    private final LocalDateTime fiveDaysAhead = now.plusDays(5);
    private final LocalDateTime sixDaysAhead = now.plusDays(6);
    private final LocalDateTime sevenDaysAhead = now.plusDays(7);
    private final LocalDateTime eightDaysAhead = now.plusDays(8);

    @Override
    @Autowired
    protected void setControllerUnderTest(CompetitionController controller) {
        this.controller = controller;
    }

    @Before
    public void setLoggedInUserOnThread() {
        loginCompAdmin();
    }

    @Test
    public void testGetAllCompetitions() throws Exception {
        List<CompetitionResource> competitions = checkCompetitionCount(2);
        checkExistingCompetition(competitions.get(0));
    }

    @Test
    public void testGetOneCompetitions() throws Exception {
        RestResult<CompetitionResource> competitionsResult = controller.getCompetitionById(COMPETITION_ID);
        assertTrue(competitionsResult.isSuccess());
        CompetitionResource competition = competitionsResult.getSuccessObject();

        checkExistingCompetition(competition);
    }


    @Test
    public void testCreateCompetition() throws Exception {
        checkCompetitionCount(2);
        createNewCompetition();

        int expectedCompetitionCount = 3;
        List<CompetitionResource> competitions = checkCompetitionCount(expectedCompetitionCount);

        checkExistingCompetition(competitions.get(0));
        checkNewCompetition(competitions.get(1));
    }

    @Test
    public void testCompetitionCodeGeneration() throws Exception {
        // Setup test data
        checkCompetitionCount(2);
        createNewCompetition();
        createNewCompetition();
        createNewCompetition();
        List<CompetitionResource> competitions = checkCompetitionCount(5);

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

    private List<CompetitionResource> checkCompetitionCount(int expectedCompetitionCount) {
        RestResult<List<CompetitionResource>> allCompetitionsResult = controller.findAll();
        assertTrue(allCompetitionsResult.isSuccess());
        List<CompetitionResource> competitions = allCompetitionsResult.getSuccessObject();
        assertThat("Checking if the amount of competitions is what we expect.", competitions, hasSize(expectedCompetitionCount));
        return competitions;
    }

    @Test
    public void testUpdateCompetition() throws Exception {
        checkCompetitionCount(2);

        // Create new competition
        CompetitionResource competition = createNewCompetition();

        checkCompetitionCount(3);

        // Update competition
        competition.setName(COMPETITION_NAME_UPDATED);
        RestResult<CompetitionResource> saveResult = controller.saveCompetition(competition, competition.getId());
        assertTrue("Assert save is success", saveResult.isSuccess());

        checkCompetitionCount(3);

        CompetitionResource savedCompetition = saveResult.getSuccessObject();
        assertEquals(COMPETITION_NAME_UPDATED, savedCompetition.getName());
    }

    @Test
    public void testUpdateCompetitionCategories() throws Exception {
        checkCompetitionCount(2);

        // Create new competition
        CompetitionResource competition = createNewCompetition();

        checkCompetitionCount(3);

        // Update competition
        competition.setName(COMPETITION_NAME_UPDATED);
        Long sectorId = Long.valueOf(INNOVATION_SECTOR_ID);
        Long areaId = Long.valueOf(INNOVATION_AREA_ID);
        competition.setInnovationSector(sectorId);
        competition.setInnovationArea(areaId);
        RestResult<CompetitionResource> saveResult = controller.saveCompetition(competition, competition.getId());
        assertTrue("Assert save is success", saveResult.isSuccess());

        checkCompetitionCount(3);

        CompetitionResource savedCompetition = saveResult.getSuccessObject();
        checkUpdatedCompetitionCategories(savedCompetition);
    }


    @Test
    public void testUpdateCompetitionCoFunders() throws Exception {
        checkCompetitionCount(2);

        // Create new competition
        CompetitionResource competition = createNewCompetition();

        checkCompetitionCount(3);

        // Update competition
        competition.setName(COMPETITION_NAME_UPDATED);
        Long sectorId = Long.valueOf(INNOVATION_SECTOR_ID);
        Long areaId = Long.valueOf(INNOVATION_AREA_ID);
        competition.setInnovationSector(sectorId);
        competition.setInnovationArea(areaId);

        //With one co-funder
        competition.setFunders(CompetitionCoFundersResourceFixture.getNewTestCoFundersResouces(1, competition.getId()));
        RestResult<CompetitionResource> saveResult = controller.saveCompetition(competition, competition.getId());
        assertTrue("Assert save is success", saveResult.isSuccess());
        checkCompetitionCount(3);
        CompetitionResource savedCompetition = saveResult.getSuccessObject();
        assertEquals(1, savedCompetition.getFunders().size());

        // Now re-insert with 2 co-funders
        competition.setFunders(CompetitionCoFundersResourceFixture.getNewTestCoFundersResouces(2, competition.getId()));
        saveResult = controller.saveCompetition(competition, competition.getId());
        assertTrue("Assert save is success", saveResult.isSuccess());
        savedCompetition = saveResult.getSuccessObject();
        // we should expect in total two co-funders.
        assertEquals(2, savedCompetition.getFunders().size());
    }

    @Test
    public void testCompetitionCategorySaving() throws Exception {
        checkCompetitionCount(2);
        // Create new competition
        CompetitionResource competition = createNewCompetition();
        checkCompetitionCount(3);
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

        checkCompetitionCount(3);
    }

    @Test
    public void testCompetitionCompletedSections() throws Exception {
        Long competitionId = 7L;
        RestResult<CompetitionResource> competitionsResult = controller.getCompetitionById(competitionId);
        
        assertEquals(0, competitionsResult.getSuccessObject().getSectionSetupStatus().size());
    }

    @Test
    public void testCompetitionCompleteSection() throws Exception {
    	Long competitionId = 7L;
    	
    	controller.markSectionComplete(competitionId, CompetitionSetupSection.INITIAL_DETAILS);
    	
        RestResult<CompetitionResource> competitionsResult = controller.getCompetitionById(competitionId);
        assertEquals(Boolean.TRUE, competitionsResult.getSuccessObject().getSectionSetupStatus().get(CompetitionSetupSection.INITIAL_DETAILS));
    }

    @Test
    public void testCompetitionInCompleteSection() throws Exception {
        Long competitionId = 7L;

        controller.markSectionInComplete(competitionId, CompetitionSetupSection.INITIAL_DETAILS);

        RestResult<CompetitionResource> competitionsResult = controller.getCompetitionById(competitionId);
        assertEquals(Boolean.FALSE, competitionsResult.getSuccessObject().getSectionSetupStatus().get(CompetitionSetupSection.INITIAL_DETAILS));
    }

    @Test
    public void testMarkAsSetup() throws Exception {
        checkCompetitionCount(2);

        // Create new competition
        CompetitionResource competition = createNewCompetition();

        checkCompetitionCount(3);

        controller.markAsSetup(competition.getId()).getSuccessObject();

        RestResult<CompetitionResource> competitionsResult = controller.getCompetitionById(competition.getId());
        competitionsResult.getSuccessObject().getCompetitionStatus().equals(CompetitionStatus.READY_TO_OPEN);
    }

    @Test
    public void testReturnToSetup() throws Exception {
        checkCompetitionCount(2);

        // Create new competition
        CompetitionResource competition = createNewCompetition();

        checkCompetitionCount(3);

        controller.markAsSetup(competition.getId()).getSuccessObject();
        controller.returnToSetup(competition.getId()).getSuccessObject();

        RestResult<CompetitionResource> competitionsResult = controller.getCompetitionById(competition.getId());
        competitionsResult.getSuccessObject().getCompetitionStatus().equals(CompetitionStatus.COMPETITION_SETUP);
    }

    @Test
    public void testCompetitionSearch() throws Exception {
        String matchAllQuery = "a";
        String matchOneQuery = "Connected";
        String matchNoneQuery = "XSAMXLAMSXSA";
        //Small page size to test pagination with two competitions in the db.
        int size = 1;
        int pageOne = 0;
        int pageTwo = 1;

        CompetitionSearchResult pageOneResult = controller.search(matchAllQuery, pageOne, size).getSuccessObjectOrThrowException();
        assertThat(pageOneResult.getNumber(), equalTo(pageOne));
        assertThat(pageOneResult.getTotalElements(), equalTo(2L));

        CompetitionSearchResult pageTwoResult = controller.search(matchAllQuery, pageTwo, size).getSuccessObjectOrThrowException();
        assertThat(pageTwoResult.getNumber(), equalTo(pageTwo));
        assertThat(pageTwoResult.getTotalElements(), equalTo(2L));


        CompetitionSearchResult matchOneResult = controller.search(matchOneQuery, pageOne, size).getSuccessObjectOrThrowException();
        assertThat(matchOneResult.getTotalElements(), equalTo(1L));

        CompetitionSearchResult matchNoneResult = controller.search(matchNoneQuery, pageOne, size).getSuccessObjectOrThrowException();
        assertThat(matchNoneResult.getTotalElements(), equalTo(0L));
   }

    @Test
    public void testSearchSpecialCharacters() throws Exception {
        String specialChar = "!@£$%^&*(*()_";
        int size = 20;
        int pageOne = 0;

        CompetitionResource comp = controller.create().getSuccessObjectOrThrowException();
        comp.setName(specialChar);
        controller.saveCompetition(comp, comp.getId()).getSuccessObjectOrThrowException();

        CompetitionSearchResult pageOneResult = controller.search(specialChar, pageOne, size).getSuccessObjectOrThrowException();
        assertThat(pageOneResult.getNumber(), equalTo(pageOne));
        assertThat(pageOneResult.getTotalElements(), equalTo(1L));
        assertEquals(comp.getId(), pageOneResult.getContent().get(0).getId());
    }

    @Test
    public void testFindMethods() throws Exception {
        List<CompetitionResource> existingComps = checkCompetitionCount(2);

        CompetitionResource notStartedCompetition = createWithDates(oneDayAhead, twoDaysAhead, threeDaysAhead, fourDaysAhead, fiveDaysAhead, sixDaysAhead, sevenDaysAhead, eightDaysAhead);
        assertThat(notStartedCompetition.getCompetitionStatus(), equalTo(CompetitionStatus.READY_TO_OPEN));

        CompetitionResource openCompetition = createWithDates(oneDayAgo, oneDayAhead, twoDaysAhead, threeDaysAhead, fourDaysAhead, fiveDaysAhead, sixDaysAhead, sevenDaysAhead);
        assertThat(openCompetition.getCompetitionStatus(), equalTo(CompetitionStatus.OPEN));

        CompetitionResource closedCompetition = createWithDates(twoDaysAgo, oneDayAgo, twoDaysAhead, threeDaysAhead, fourDaysAhead, fiveDaysAhead, sixDaysAhead, sevenDaysAhead);
        assertThat(closedCompetition.getCompetitionStatus(), equalTo(CompetitionStatus.CLOSED));

        CompetitionResource inAssessmentCompetition = createWithDates(fiveDaysAgo, fourDaysAgo, twoDaysAgo, oneDayAgo, fourDaysAhead, fiveDaysAhead, sixDaysAhead, sevenDaysAhead);
        assertThat(inAssessmentCompetition.getCompetitionStatus(), equalTo(CompetitionStatus.IN_ASSESSMENT));

        CompetitionResource inPanelCompetition = createWithDates(fiveDaysAgo, fourDaysAgo, threeDaysAgo, twoDaysAgo, oneDayAgo, fiveDaysAhead, sixDaysAhead, sevenDaysAhead);
        assertThat(inPanelCompetition.getCompetitionStatus(), equalTo(CompetitionStatus.FUNDERS_PANEL));

        CompetitionResource assessorFeedbackCompetition = createWithDates(sevenDaysAgo, sixDaysAgo, fiveDaysAgo, fourDaysAgo, threeDaysAgo, twoDaysAgo, oneDayAgo, oneDayAhead);
        assertThat(assessorFeedbackCompetition.getCompetitionStatus(), equalTo(CompetitionStatus.ASSESSOR_FEEDBACK));

        CompetitionResource projectSetup = createWithDates(eightDaysAgo, sevenDaysAgo, sixDaysAgo, fiveDaysAgo, fourDaysAgo, threeDaysAgo, twoDaysAgo, oneDayAgo);
        assertThat(projectSetup.getCompetitionStatus(), equalTo(CompetitionStatus.PROJECT_SETUP));

        CompetitionCountResource counts = controller.count().getSuccessObjectOrThrowException();;

        List<CompetitionSearchResultItem> liveCompetitions = controller.live().getSuccessObjectOrThrowException();

        Set<Long> expectedLiveCompetitionIds = Sets.newHashSet(openCompetition.getId(),
                closedCompetition.getId(), inAssessmentCompetition.getId(), inPanelCompetition.getId(),
                assessorFeedbackCompetition.getId());
        Set<Long> expectedNotLiveCompetitionIds = Sets.newHashSet(notStartedCompetition.getId(), projectSetup.getId());

        //Live competitions plus one the test data.
        assertThat(liveCompetitions.size(), equalTo(expectedLiveCompetitionIds.size() + 1));
        assertThat(counts.getLiveCount(), equalTo(expectedLiveCompetitionIds.size() + 1L));



        liveCompetitions.stream().forEach(competitionResource -> {
            //Existing competitions in the db should be ignored.
            if (!existingComps.get(0).getId().equals(competitionResource.getId())
                    && !existingComps.get(1).getId().equals(competitionResource.getId())) {
                assertTrue(expectedLiveCompetitionIds.contains(competitionResource.getId()));
                assertFalse(expectedNotLiveCompetitionIds.contains(competitionResource.getId()));
            }
        });

        List<CompetitionSearchResultItem> projectSetupCompetitions = controller.projectSetup().getSuccessObjectOrThrowException();

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

        List<CompetitionSearchResultItem> upcomingCompetitions = controller.upcoming().getSuccessObjectOrThrowException();

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


    @Test
    public void testInitApplicationFormByType() throws Exception {
        Long competitionId = 7L;
        Long competitionTypeId = 1L;

        controller.initialiseForm(competitionId, competitionTypeId);

        RestResult<CompetitionResource> competitionsResult = controller.getCompetitionById(competitionId);
        assertEquals(competitionTypeId, competitionsResult.getSuccessObject().getCompetitionType());
    }

   private CompetitionResource createWithDates(LocalDateTime startDate,
                                               LocalDateTime endDate,
                                               LocalDateTime assessorAcceptsDate,
                                               LocalDateTime assessorsNotifiedDate,
                                               LocalDateTime assessmentClosedDate,
                                               LocalDateTime fundersPanelDate,
                                               LocalDateTime fundersPanelEndDate,
                                               LocalDateTime assessorFeedbackDate
                                               ) {
       CompetitionResource comp = controller.create().getSuccessObjectOrThrowException();

       List<Milestone> milestones = createNewMilestones(comp, startDate, endDate, assessorAcceptsDate,
               fundersPanelDate, fundersPanelEndDate, assessorFeedbackDate, assessorsNotifiedDate, assessmentClosedDate);

       milestones.forEach(milestone -> milestoneRepository.save(milestone));

       controller.saveCompetition(comp, comp.getId()).getSuccessObjectOrThrowException();

       //TODO replace with controller endpoint for competition setup finished
       Competition compEntity = competitionRepository.findById(comp.getId());
       compEntity.setSetupComplete(true);
       competitionRepository.save(compEntity);
       flushAndClearSession();

       return controller.getCompetitionById(comp.getId()).getSuccessObjectOrThrowException();
   }

    private List<Milestone> createNewMilestones(CompetitionResource comp, LocalDateTime startDate,
                                      LocalDateTime endDate, LocalDateTime assessorAcceptsDate,
                                      LocalDateTime fundersPanelDate, LocalDateTime fundersPanelEndDate,
                                      LocalDateTime assessorFeedbackDate, LocalDateTime assessorsNotifiedDate,
                                                LocalDateTime assessmentClosedDate) {

        return EnumSet.allOf(MilestoneType.class).stream().map(milestoneType -> {
            final LocalDateTime milestoneDate;
            switch (milestoneType) {
                case OPEN_DATE: milestoneDate = startDate; break;
                case SUBMISSION_DATE: milestoneDate = endDate; break;
                case ASSESSOR_ACCEPTS: milestoneDate = assessorAcceptsDate; break;
                case ASSESSORS_NOTIFIED: milestoneDate = assessorsNotifiedDate; break;
                case ASSESSMENT_CLOSED: milestoneDate = assessmentClosedDate; break;
                case ASSESSOR_DEADLINE: milestoneDate = assessorFeedbackDate; break;
                case FUNDERS_PANEL: milestoneDate = fundersPanelDate; break;
                case NOTIFICATIONS: milestoneDate = fundersPanelEndDate; break;
                default: milestoneDate = LocalDateTime.now();
            }
            return new Milestone(milestoneType, milestoneDate, assignCompetitionId(comp));
        }).collect(Collectors.toList());
    }

    private Competition assignCompetitionId(CompetitionResource competition) {
        Competition newComp = new Competition();
        newComp.setId(competition.getId());
        return newComp;
    }

    private CompetitionResource createNewCompetition() {
        RestResult<CompetitionResource> competitionsResult = controller.create();
        assertTrue(competitionsResult.isSuccess());
        CompetitionResource competition = competitionsResult.getSuccessObject();
        assertThat(competition.getName(), isEmptyOrNullString());
        return competition;
    }

    private List<MilestoneType> populateMilestoneTypes() {
        return new ArrayList<>(EnumSet.allOf(MilestoneType.class));
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
        assertThat(competition.getCompetitionStatus(), is(CompetitionStatus.OPEN));
    }

    private void checkNewCompetition(CompetitionResource competition) {
        assertThat(competition, notNullValue());
        assertThat(competition.getName(), isEmptyOrNullString());
        assertThat(competition.getCompetitionStatus(), is(CompetitionStatus.COMPETITION_SETUP));
    }
}
