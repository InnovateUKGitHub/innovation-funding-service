package org.innovateuk.ifs.competition.controller;

import org.innovateuk.ifs.BaseControllerIntegrationTest;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.domain.FundingDecisionStatus;
import org.innovateuk.ifs.application.repository.ApplicationRepository;
import org.innovateuk.ifs.application.resource.ApplicationState;
import org.innovateuk.ifs.assessment.domain.Assessment;
import org.innovateuk.ifs.assessment.repository.AssessmentRepository;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.domain.Milestone;
import org.innovateuk.ifs.competition.mapper.CompetitionMapper;
import org.innovateuk.ifs.competition.repository.CompetitionRepository;
import org.innovateuk.ifs.competition.repository.MilestoneRepository;
import org.innovateuk.ifs.competition.resource.*;
import org.innovateuk.ifs.competition.resource.fixtures.CompetitionCoFundersResourceFixture;
import org.innovateuk.ifs.finance.domain.ProjectFinance;
import org.innovateuk.ifs.finance.repository.ProjectFinanceRepository;
import org.innovateuk.ifs.project.domain.Project;
import org.innovateuk.ifs.project.repository.ProjectRepository;
import org.innovateuk.ifs.threads.domain.Note;
import org.innovateuk.ifs.threads.domain.Post;
import org.innovateuk.ifs.threads.domain.Query;
import org.innovateuk.ifs.threads.repository.NoteRepository;
import org.innovateuk.ifs.threads.repository.QueryRepository;
import org.innovateuk.ifs.user.domain.Organisation;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.mapper.UserMapper;
import org.innovateuk.ifs.user.repository.OrganisationRepository;
import org.innovateuk.ifs.user.repository.UserRepository;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.resource.UserRoleType;
import org.innovateuk.ifs.workflow.domain.ActivityState;
import org.innovateuk.ifs.workflow.repository.ActivityStateRepository;
import org.innovateuk.threads.resource.FinanceChecksSectionType;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.google.common.collect.Sets.newHashSet;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singleton;
import static java.util.Collections.singletonList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.assessment.builder.AssessmentBuilder.newAssessment;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.competition.resource.CompetitionStatus.IN_ASSESSMENT;
import static org.innovateuk.ifs.competition.resource.MilestoneType.FEEDBACK_RELEASED;
import static org.innovateuk.ifs.finance.domain.builder.ProjectFinanceBuilder.newProjectFinance;
import static org.innovateuk.ifs.project.builder.ProjectBuilder.newProject;
import static org.innovateuk.ifs.user.builder.OrganisationBuilder.newOrganisation;
import static org.innovateuk.ifs.user.builder.ProcessRoleBuilder.newProcessRole;
import static org.innovateuk.ifs.user.builder.RoleBuilder.newRole;
import static org.innovateuk.ifs.user.builder.RoleResourceBuilder.newRoleResource;
import static org.innovateuk.ifs.user.builder.UserBuilder.newUser;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;
import static org.innovateuk.ifs.workflow.domain.ActivityType.APPLICATION;
import static org.innovateuk.ifs.workflow.domain.ActivityType.APPLICATION_ASSESSMENT;
import static org.innovateuk.ifs.workflow.resource.State.CREATED;
import static org.innovateuk.ifs.workflow.resource.State.PENDING;
import static org.junit.Assert.*;

/**
 * Integration test for testing the rest services of the competition controller
 */
public class CompetitionControllerIntegrationTest extends BaseControllerIntegrationTest<CompetitionController> {

    @Autowired
    private CompetitionRepository competitionRepository;

    @Autowired
    private MilestoneRepository milestoneRepository;

    @Autowired
    private AssessmentRepository assessmentRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private ActivityStateRepository activityStateRepository;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private CompetitionMapper competitionMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private QueryRepository queryRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ProjectFinanceRepository projectFinanceRepository;

    @Autowired
    private OrganisationRepository organisationRepository;

    @Autowired
    private NoteRepository noteRepository;

    private static final Long COMPETITION_ID = 1L;

    private static final String COMPETITION_NAME_UPDATED = "Competition name updated";
    private static final long INNOVATION_SECTOR_ID = 1L;
    private static final String INNOVATION_SECTOR_NAME = "Health and life sciences";
    private static final long INNOVATION_AREA_ID = 6L;
    private static final long INNOVATION_AREA_ID_TWO = 7L;
    private static final long INNOVATION_AREA_ID_THREE = 8L;
    private static final String INNOVATION_AREA_NAME = "Satellite applications";
    private static final String INNOVATION_AREA_NAME_TWO = "Emerging technology";
    private static final String INNOVATION_AREA_NAME_THREE = "Robotics and autonomous systems";
    private static final String EXISTING_COMPETITION_NAME = "Connected digital additive manufacturing";
    private static final long RESEARCH_CATEGORY_ID_ONE = 33L;

    private final ZonedDateTime now = ZonedDateTime.now();
    private final ZonedDateTime eightDaysAgo = now.minusDays(8);
    private final ZonedDateTime sevenDaysAgo = now.minusDays(7);
    private final ZonedDateTime sixDaysAgo = now.minusDays(6);
    private final ZonedDateTime fiveDaysAgo = now.minusDays(5);
    private final ZonedDateTime fourDaysAgo = now.minusDays(4);
    private final ZonedDateTime threeDaysAgo = now.minusDays(3);
    private final ZonedDateTime twoDaysAgo = now.minusDays(2);
    private final ZonedDateTime oneDayAgo = now.minusDays(1);
    private final ZonedDateTime oneDayAhead = now.plusDays(1);
    private final ZonedDateTime twoDaysAhead = now.plusDays(2);
    private final ZonedDateTime threeDaysAhead = now.plusDays(3);
    private final ZonedDateTime fourDaysAhead = now.plusDays(4);
    private final ZonedDateTime fiveDaysAhead = now.plusDays(5);
    private final ZonedDateTime sixDaysAhead = now.plusDays(6);
    private final ZonedDateTime sevenDaysAhead = now.plusDays(7);
    private User projectFinanceUser;
    private User projectManagerUser;
    private UserResource projectFinanceUserLogin;

    @Override
    @Autowired
    protected void setControllerUnderTest(CompetitionController controller) {
        this.controller = controller;
    }

    @Before
    public void setLoggedInUserOnThread() {
        loginCompAdmin();
    }

    private void setupUserData() {
        projectFinanceUser = newUser().withRoles(newRole().withType(UserRoleType.PROJECT_FINANCE).buildSet(1)).withUid("uid2").withFirstName("z").withLastName("y").withEmailAddress("z@y.com").withCreatedOn(ZonedDateTime.now()).withCreatedBy(userMapper.mapToDomain(getSystemRegistrationUser())).build();
        projectFinanceUser = userRepository.save(projectFinanceUser);
        projectManagerUser = newUser().withRoles(newRole().withType(UserRoleType.PROJECT_MANAGER).buildSet(1)).withUid("uid1").withFirstName("a").withLastName("b").withEmailAddress("a@b.com").withCreatedOn(ZonedDateTime.now()).withCreatedBy(userMapper.mapToDomain(getSystemRegistrationUser())).build();
        projectManagerUser = userRepository.save(projectManagerUser);
        projectFinanceUserLogin = newUserResource().withRolesGlobal(singletonList(newRoleResource().withType(UserRoleType.PROJECT_FINANCE).build())).withCreatedOn(ZonedDateTime.now()).withCreatedBy("creator").withModifiedOn(ZonedDateTime.now()).withModifiedBy("modifier").build();
    }

    @Test
    public void getAllCompetitions() throws Exception {
        List<CompetitionResource> competitions = checkCompetitionCount(2);
        checkExistingCompetition(competitions.get(0));
    }

    @Test
    public void getOneCompetitions() throws Exception {
        RestResult<CompetitionResource> competitionsResult = controller.getCompetitionById(COMPETITION_ID);
        assertTrue(competitionsResult.isSuccess());
        CompetitionResource competition = competitionsResult.getSuccessObject();

        checkExistingCompetition(competition);
    }

    @Test
    public void createCompetition() throws Exception {
        checkCompetitionCount(2);
        createNewCompetition();

        int expectedCompetitionCount = 3;
        List<CompetitionResource> competitions = checkCompetitionCount(expectedCompetitionCount);

        checkExistingCompetition(competitions.get(0));
        checkNewCompetition(competitions.get(1));
    }

    @Test
    public void competitionCodeGeneration() throws Exception {
        // Setup test data
        checkCompetitionCount(2);
        createNewCompetition();
        createNewCompetition();
        createNewCompetition();
        List<CompetitionResource> competitions = checkCompetitionCount(5);

        // Generate number 1 in this month year combination
        RestResult<String> generatedCode = controller.generateCompetitionCode(ZonedDateTime.of(2016, 6, 5, 12, 0,0,0, ZoneId.systemDefault()), competitions.get(0).getId());
        assertTrue(generatedCode.isSuccess());
        assertEquals("1606-1", generatedCode.getSuccessObject());
        flushAndClearSession();


        // Generate number 2 in this month year combination
        generatedCode = controller.generateCompetitionCode(ZonedDateTime.of(2016, 6, 5, 12, 0,0,0, ZoneId.systemDefault()), competitions.get(1).getId());
        assertTrue(generatedCode.isSuccess());
        assertEquals("1606-2", generatedCode.getSuccessObject());
        flushAndClearSession();

        // Generate number 3 in this month year combination
        generatedCode = controller.generateCompetitionCode(ZonedDateTime.of(2016, 6, 5, 12, 0,0,0, ZoneId.systemDefault()), competitions.get(2).getId());
        assertTrue(generatedCode.isSuccess());
        assertEquals("1606-3", generatedCode.getSuccessObject());

        // if generated twice the first code should not be updated.
        generatedCode = controller.generateCompetitionCode(ZonedDateTime.of(2020, 11, 11, 12, 0,0,0, ZoneId.systemDefault()), competitions.get(2).getId());
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
    public void saveCompetition() throws Exception {
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
    public void saveCompetition_categories() throws Exception {
        checkCompetitionCount(2);

        // Create new competition
        CompetitionResource competition = createNewCompetition();

        checkCompetitionCount(3);

        // Update competition
        competition.setName(COMPETITION_NAME_UPDATED);
        competition.setInnovationSector(INNOVATION_SECTOR_ID);
        competition.setInnovationAreas(singleton(INNOVATION_AREA_ID));
        competition.setResearchCategories(singleton(RESEARCH_CATEGORY_ID_ONE));

        RestResult<CompetitionResource> saveResult = controller.saveCompetition(competition, competition.getId());
        assertTrue("Assert save is success", saveResult.isSuccess());

        checkCompetitionCount(3);

        CompetitionResource savedCompetition = saveResult.getSuccessObject();
        checkUpdatedCompetitionCategories(savedCompetition);
    }

    @Test
    public void saveCompetition_multipleInnovationAreas() throws Exception {
        checkCompetitionCount(2);

        // Create new competition
        CompetitionResource competition = createNewCompetition();

        checkCompetitionCount(3);

        // Update competition
        competition.setName(COMPETITION_NAME_UPDATED);
        competition.setInnovationSector(INNOVATION_SECTOR_ID);
        competition.setInnovationAreas(newHashSet(INNOVATION_AREA_ID, INNOVATION_AREA_ID_TWO, INNOVATION_AREA_ID_THREE));

        RestResult<CompetitionResource> saveResult = controller.saveCompetition(competition, competition.getId());
        assertTrue("Assert save is success", saveResult.isSuccess());

        checkCompetitionCount(3);

        CompetitionResource savedCompetition = saveResult.getSuccessObject();

        assertThat(savedCompetition.getInnovationAreas(), hasItems(INNOVATION_AREA_ID, INNOVATION_AREA_ID_TWO, INNOVATION_AREA_ID_THREE));
        assertEquals(3, savedCompetition.getInnovationAreas().size());
        assertThat(savedCompetition.getInnovationAreaNames(), hasItems(INNOVATION_AREA_NAME, INNOVATION_AREA_NAME_TWO, INNOVATION_AREA_NAME_THREE));
        assertEquals(3, savedCompetition.getInnovationAreaNames().size());
    }

    @Test
    public void saveCompetition_removeInnovationArea() throws Exception {
        checkCompetitionCount(2);

        CompetitionResource competition = createNewCompetition();

        checkCompetitionCount(3);

        competition.setInnovationAreas(newHashSet(INNOVATION_AREA_ID));

        RestResult<CompetitionResource> saveResult = controller.saveCompetition(competition, competition.getId());
        assertTrue("Assert save is success", saveResult.isSuccess());

        CompetitionResource firstCompetitionSaved = saveResult.getSuccessObjectOrThrowException();

        assertEquals(1, firstCompetitionSaved.getInnovationAreas().size());
        assertTrue("innovationAreas contains " + INNOVATION_AREA_ID, firstCompetitionSaved.getInnovationAreas().contains(INNOVATION_AREA_ID));
        assertTrue("innovationAreaNames contains " + INNOVATION_AREA_NAME, firstCompetitionSaved.getInnovationAreaNames().contains(INNOVATION_AREA_NAME));

        firstCompetitionSaved.setInnovationAreas(null);

        CompetitionResource secondCompetitionSaved = controller.saveCompetition(firstCompetitionSaved, firstCompetitionSaved.getId())
                .getSuccessObjectOrThrowException();

        assertEquals(0, secondCompetitionSaved.getInnovationAreas().size());
    }

    @Test
    public void saveCompetition_removeResearchCategory() throws Exception {
        checkCompetitionCount(2);

        CompetitionResource competition = createNewCompetition();

        checkCompetitionCount(3);

        competition.setResearchCategories(newHashSet(RESEARCH_CATEGORY_ID_ONE));

        RestResult<CompetitionResource> saveResult = controller.saveCompetition(competition, competition.getId());
        assertTrue("Assert save is success", saveResult.isSuccess());

        CompetitionResource firstCompetitionSaved = saveResult.getSuccessObjectOrThrowException();

        assertEquals(1, firstCompetitionSaved.getResearchCategories().size());
        assertTrue("researchCategories contains " + RESEARCH_CATEGORY_ID_ONE, firstCompetitionSaved.getResearchCategories().contains(RESEARCH_CATEGORY_ID_ONE));

        firstCompetitionSaved.setResearchCategories(null);

        CompetitionResource secondCompetitionSaved = controller.saveCompetition(firstCompetitionSaved, firstCompetitionSaved.getId())
                .getSuccessObjectOrThrowException();

        assertEquals(0, secondCompetitionSaved.getResearchCategories().size());
    }

    @Test
    public void closeAssessment() throws Exception {
        RestResult<Void> closeResult = controller.closeAssessment(COMPETITION_ID);
        assertTrue("Assert close assessment is success", closeResult.isSuccess());
        RestResult<CompetitionResource> getResult = controller.getCompetitionById(COMPETITION_ID);
        assertTrue("Assert get is success", getResult.isSuccess());
        CompetitionResource retrievedCompetition = getResult.getSuccessObject();
        retrievedCompetition.getCompetitionStatus();
    }

    @Test
    public void updateCompetitionCoFunders() throws Exception {
        checkCompetitionCount(2);

        // Create new competition
        CompetitionResource competition = createNewCompetition();

        checkCompetitionCount(3);

        // Update competition
        competition.setName(COMPETITION_NAME_UPDATED);
        Long sectorId = INNOVATION_SECTOR_ID;
        Set<Long> areaIds = new HashSet<>(singletonList(INNOVATION_AREA_ID));
        competition.setInnovationSector(sectorId);
        competition.setInnovationAreas(areaIds);

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
    public void competitionCompletedSections() throws Exception {
        Long competitionId = 7L;
        RestResult<CompetitionResource> competitionsResult = controller.getCompetitionById(competitionId);

        assertEquals(0, competitionsResult.getSuccessObject().getSectionSetupStatus().size());
    }

    @Test
    public void competitionCompleteSection() throws Exception {
        Long competitionId = 7L;

        controller.markSectionComplete(competitionId, CompetitionSetupSection.INITIAL_DETAILS);

        RestResult<CompetitionResource> competitionsResult = controller.getCompetitionById(competitionId);
        assertEquals(Boolean.TRUE, competitionsResult.getSuccessObject().getSectionSetupStatus().get(CompetitionSetupSection.INITIAL_DETAILS));
    }

    @Test
    public void competitionInCompleteSection() throws Exception {
        Long competitionId = 7L;

        controller.markSectionInComplete(competitionId, CompetitionSetupSection.INITIAL_DETAILS);

        RestResult<CompetitionResource> competitionsResult = controller.getCompetitionById(competitionId);
        assertEquals(Boolean.FALSE, competitionsResult.getSuccessObject().getSectionSetupStatus().get(CompetitionSetupSection.INITIAL_DETAILS));
    }

    @Test
    public void markAsSetup() throws Exception {
        checkCompetitionCount(2);

        // Create new competition
        CompetitionResource competition = createNewCompetition();

        checkCompetitionCount(3);

        controller.markAsSetup(competition.getId()).getSuccessObject();

        RestResult<CompetitionResource> competitionsResult = controller.getCompetitionById(competition.getId());
        competitionsResult.getSuccessObject().getCompetitionStatus().equals(CompetitionStatus.READY_TO_OPEN);
    }

    @Test
    @Transactional
    public void notifyAssessors() throws Exception {
        CompetitionResource closedCompetition = createWithDates(twoDaysAgo, oneDayAgo, twoDaysAhead, threeDaysAhead, fourDaysAhead, fiveDaysAhead, sixDaysAhead, sevenDaysAhead);
        closedCompetition.setName("Closed competition name");

        controller.saveCompetition(closedCompetition, closedCompetition.getId());

        UserResource user = getPaulPlum();
        ActivityState createdActivityState = activityStateRepository.findOneByActivityTypeAndState(APPLICATION, CREATED);
        List<Long> assessmentIds = createCreatedAssessmentsWithCompetition(closedCompetition.getId(), user, 2, createdActivityState);

        RestResult<Void> notifyResult = controller.notifyAssessors(closedCompetition.getId());
        assertTrue("Notify assessors is a success", notifyResult.isSuccess());

        RestResult<CompetitionResource> getResult = controller.getCompetitionById(closedCompetition.getId());
        assertTrue("Assert get is success", getResult.isSuccess());

        CompetitionResource retrievedCompetition = getResult.getSuccessObject();
        assertEquals(IN_ASSESSMENT, retrievedCompetition.getCompetitionStatus());

        List<Assessment> updatedAssessments = assessmentRepository.findByActivityStateStateAndTargetCompetitionId(PENDING, closedCompetition.getId());
        assertEquals(assessmentIds.size(), updatedAssessments.size());
        assertThat(updatedAssessments.stream().map(Assessment::getId).collect(Collectors.toList()), is(assessmentIds));
    }

    @Test
    public void returnToSetup() throws Exception {
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
    public void competitionSearch() throws Exception {
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
    public void searchSpecialCharacters() throws Exception {
        String specialChar = "!@£$%^&*(*()_";
        int size = 20;
        int pageOne = 0;

        CompetitionResource comp = controller.create().getSuccessObjectOrThrowException();
        comp.setName(specialChar);
        controller.saveCompetition(comp, comp.getId()).getSuccessObjectOrThrowException();
        milestoneRepository.save(createNewMilestones(comp, oneDayAhead, twoDaysAhead, threeDaysAhead, fourDaysAhead, fiveDaysAhead, sixDaysAhead, sevenDaysAhead));

        CompetitionSearchResult pageOneResult = controller.search(specialChar, pageOne, size).getSuccessObjectOrThrowException();
        assertThat(pageOneResult.getNumber(), equalTo(pageOne));
        assertThat(pageOneResult.getTotalElements(), equalTo(1L));
        assertEquals(comp.getId(), pageOneResult.getContent().get(0).getId());
    }

    @Test
    @Transactional
    public void findMethods() throws Exception {
        List<CompetitionResource> existingComps = checkCompetitionCount(2);

        CompetitionResource notStartedCompetition = createWithDates(oneDayAhead, twoDaysAhead, threeDaysAhead, fourDaysAhead, fiveDaysAhead, sixDaysAhead, sevenDaysAhead, null);
        assertThat(notStartedCompetition.getCompetitionStatus(), equalTo(CompetitionStatus.READY_TO_OPEN));

        CompetitionResource openCompetition = createWithDates(oneDayAgo, oneDayAhead, twoDaysAhead, threeDaysAhead, fourDaysAhead, fiveDaysAhead, sixDaysAhead, null);
        assertThat(openCompetition.getCompetitionStatus(), equalTo(CompetitionStatus.OPEN));

        CompetitionResource closedCompetition = createWithDates(twoDaysAgo, oneDayAgo, twoDaysAhead, threeDaysAhead, fourDaysAhead, fiveDaysAhead, sixDaysAhead, null);
        assertThat(closedCompetition.getCompetitionStatus(), equalTo(CompetitionStatus.CLOSED));

        CompetitionResource inAssessmentCompetition = createWithDates(fiveDaysAgo, fourDaysAgo, twoDaysAgo, oneDayAgo, fourDaysAhead, fiveDaysAhead, sixDaysAhead, null);
        assertThat(inAssessmentCompetition.getCompetitionStatus(), equalTo(IN_ASSESSMENT));

        CompetitionResource inPanelCompetition = createWithDates(fiveDaysAgo, fourDaysAgo, threeDaysAgo, twoDaysAgo, oneDayAgo, fiveDaysAhead, sixDaysAhead, null);
        assertThat(inPanelCompetition.getCompetitionStatus(), equalTo(CompetitionStatus.FUNDERS_PANEL));

        CompetitionResource assessorFeedbackCompetition = createWithDates(sevenDaysAgo, sixDaysAgo, fiveDaysAgo, fourDaysAgo, threeDaysAgo, twoDaysAgo, oneDayAgo, null);
        assertThat(assessorFeedbackCompetition.getCompetitionStatus(), equalTo(CompetitionStatus.ASSESSOR_FEEDBACK));

        CompetitionResource projectSetup = createWithDates(eightDaysAgo, sevenDaysAgo, sixDaysAgo, fiveDaysAgo, fourDaysAgo, threeDaysAgo, twoDaysAgo, oneDayAgo);
        assertThat(projectSetup.getCompetitionStatus(), equalTo(CompetitionStatus.PROJECT_SETUP));

        Application app = newApplication().withManageFundingEmailDate(ZonedDateTime.now()).withFundingDecision(FundingDecisionStatus.FUNDED).withCompetition(competitionMapper.mapToDomain(projectSetup)).build();
        applicationRepository.save(app);

        CompetitionCountResource counts = controller.count().getSuccessObjectOrThrowException();

        List<CompetitionSearchResultItem> liveCompetitions = controller.live().getSuccessObjectOrThrowException();

        Set<Long> expectedLiveCompetitionIds = newHashSet(openCompetition.getId(),
                closedCompetition.getId(), inAssessmentCompetition.getId(), inPanelCompetition.getId(),
                assessorFeedbackCompetition.getId());
        Set<Long> expectedNotLiveCompetitionIds = newHashSet(notStartedCompetition.getId(), projectSetup.getId());

        //Live competitions plus one the test data.
        assertThat(liveCompetitions.size(), equalTo(expectedLiveCompetitionIds.size() + 1));
        assertThat(counts.getLiveCount(), equalTo(expectedLiveCompetitionIds.size() + 1L));


        liveCompetitions.forEach(competitionResource -> {
            //Existing competitions in the db should be ignored.
            if (!existingComps.get(0).getId().equals(competitionResource.getId())
                    && !existingComps.get(1).getId().equals(competitionResource.getId())) {
                assertTrue(expectedLiveCompetitionIds.contains(competitionResource.getId()));
                assertFalse(expectedNotLiveCompetitionIds.contains(competitionResource.getId()));
            }
        });

        List<CompetitionSearchResultItem> projectSetupCompetitions = controller.projectSetup().getSuccessObjectOrThrowException();

        Set<Long> projectSetupCompetitionIds = newHashSet(projectSetup.getId());
        Set<Long> notProjectSetupCompetitionIds = newHashSet(notStartedCompetition.getId(), openCompetition.getId(),
                closedCompetition.getId(), inAssessmentCompetition.getId(), inPanelCompetition.getId(),
                assessorFeedbackCompetition.getId());

        assertThat(projectSetupCompetitions.size(), equalTo(projectSetupCompetitionIds.size()));
        assertThat(counts.getProjectSetupCount(), equalTo((long) projectSetupCompetitionIds.size()));

        projectSetupCompetitions.forEach(competitionResource -> {
            assertTrue(projectSetupCompetitionIds.contains(competitionResource.getId()));
            assertFalse(notProjectSetupCompetitionIds.contains(competitionResource.getId()));
        });

        List<CompetitionSearchResultItem> upcomingCompetitions = controller.upcoming().getSuccessObjectOrThrowException();

        //One existing comp is upcoming and the new one.
        assertThat(upcomingCompetitions.size(), equalTo(2));
        assertThat(counts.getUpcomingCount(), equalTo(2L));
        Set<Long> upcomingCompetitionIds = newHashSet(notStartedCompetition.getId());
        Set<Long> notUpcomingCompetitionIds = newHashSet(projectSetup.getId(), openCompetition.getId(),
                closedCompetition.getId(), inAssessmentCompetition.getId(), inPanelCompetition.getId(),
                assessorFeedbackCompetition.getId());

        upcomingCompetitions.forEach(competitionResource -> {
            //Existing competitions in the db should be ignored.
            if (!existingComps.get(0).getId().equals(competitionResource.getId())
                    && !existingComps.get(1).getId().equals(competitionResource.getId())) {
                assertTrue(upcomingCompetitionIds.contains(competitionResource.getId()));
                assertFalse(notUpcomingCompetitionIds.contains(competitionResource.getId()));
            }
        });

        List<CompetitionSearchResultItem> previousCompetitions = controller.feedbackReleased().getSuccessObjectOrThrowException();

        Set<Long> previousCompetitionIds = newHashSet(projectSetup.getId());
        Set<Long> notPreviousCompetitionIds = newHashSet(notStartedCompetition.getId(), openCompetition.getId(),
                closedCompetition.getId(), inAssessmentCompetition.getId(), inPanelCompetition.getId(),
                assessorFeedbackCompetition.getId());

        assertThat(previousCompetitions.size(), equalTo(previousCompetitionIds.size()));
        assertThat(counts.getProjectSetupCount(), equalTo((long) previousCompetitionIds.size()));

        previousCompetitions.forEach(competitionResource -> {
            assertTrue(previousCompetitionIds.contains(competitionResource.getId()));
            assertFalse(notPreviousCompetitionIds.contains(competitionResource.getId()));
        });

    }

    @Test
    public void initApplicationFormByType() throws Exception {
        Long competitionId = 7L;
        Long competitionTypeId = 1L;

        controller.initialiseForm(competitionId, competitionTypeId);

        RestResult<CompetitionResource> competitionsResult = controller.getCompetitionById(competitionId);
        assertEquals(competitionTypeId, competitionsResult.getSuccessObject().getCompetitionType());
    }

    @Test
    @Transactional
    public void countOpenQueriesOneCreatedByProjectFinance() throws Exception {
        setupUserData();
        Competition competition1 = newCompetition().build();
        competition1 = competitionRepository.save(competition1);
        Application application1 = newApplication().withCompetition(competition1).build();
        application1 = applicationRepository.save(application1);
        Project project1 = newProject().withId(27L).withName("project1").withApplication(application1).build();
        project1 = projectRepository.save(project1);
        Organisation organisation1 = newOrganisation().withId(23L).withName("Org1").build();
        organisation1 = organisationRepository.save(organisation1);
        ProjectFinance projectFinance1 = newProjectFinance().withOrganisation(organisation1).withProject(project1).build();
        projectFinanceRepository.save(projectFinance1);

        Post postCreatedByProjectFinanceOnThreadCreatedByProjectFinance = new Post(1L, projectFinanceUser, "query1", emptyList(), ZonedDateTime.now());

        Query queryCreatedByProjectFinance = new Query(1L,
                projectFinance1.getId(),
                "org.innovateuk.ifs.finance.domain.ProjectFinance",
                singletonList(postCreatedByProjectFinanceOnThreadCreatedByProjectFinance),
                FinanceChecksSectionType.ELIGIBILITY,
                "title1",
                ZonedDateTime.now());
        queryRepository.save(queryCreatedByProjectFinance);

        setLoggedInUser(projectFinanceUserLogin);

        RestResult<Long> openQueriesCountResult = controller.countOpenQueries(competition1.getId());
        assertTrue(openQueriesCountResult.isSuccess());
        assertEquals(0L, openQueriesCountResult.getSuccessObject().longValue());
    }

    @Test
    @Transactional
    public void countOpenQueriesOneCreatedByProjectManager() throws Exception {
        // Note that this should not happen with the real system as only Project Finance user can create Queries

        setupUserData();
        Competition competition1 = newCompetition().build();
        competition1 = competitionRepository.save(competition1);
        Application application1 = newApplication().withCompetition(competition1).build();
        application1 = applicationRepository.save(application1);
        Project project1 = newProject().withId(27L).withName("project1").withApplication(application1).build();
        project1 = projectRepository.save(project1);
        Organisation organisation1 = newOrganisation().withId(23L).withName("Org1").build();
        organisation1 = organisationRepository.save(organisation1);
        ProjectFinance projectFinance1 = newProjectFinance().withOrganisation(organisation1).withProject(project1).build();
        projectFinanceRepository.save(projectFinance1);

        Post postCreatedByProjectFinanceOnThreadCreatedByProjectFinance = new Post(1L, projectManagerUser, "query1", emptyList(), ZonedDateTime.now());

        Query queryCreatedByProjectFinance = new Query(1L,
                projectFinance1.getId(),
                "org.innovateuk.ifs.finance.domain.ProjectFinance",
                singletonList(postCreatedByProjectFinanceOnThreadCreatedByProjectFinance),
                FinanceChecksSectionType.ELIGIBILITY,
                "title1",
                ZonedDateTime.now());
        queryRepository.save(queryCreatedByProjectFinance);

        setLoggedInUser(projectFinanceUserLogin);

        RestResult<Long> openQueriesCountResult = controller.countOpenQueries(competition1.getId());
        assertTrue(openQueriesCountResult.isSuccess());
        assertEquals(0L, openQueriesCountResult.getSuccessObject().longValue());
    }

    @Test
    @Transactional
    public void countOpenQueriesOneCreatedByProjectFinancePMResponded() throws Exception {
        setupUserData();
        Competition competition1 = newCompetition().withName("comp1").build();
        competition1 = competitionRepository.save(competition1);
        Application application1 = newApplication().withCompetition(competition1).withName("app1").build();
        application1 = applicationRepository.save(application1);
        Project project1 = newProject().withId(27L).withName("project1").withApplication(application1).build();
        project1 = projectRepository.save(project1);
        Organisation organisation1 = newOrganisation().withId(23L).withName("Org1").build();
        organisation1 = organisationRepository.save(organisation1);
        ProjectFinance projectFinance1 = newProjectFinance().withOrganisation(organisation1).withProject(project1).build();
        projectFinance1 = projectFinanceRepository.save(projectFinance1);

        Post postCreatedByProjectFinanceOnThreadCreatedByProjectFinance = new Post(1L, projectFinanceUser, "query1", emptyList(), ZonedDateTime.now());

        Post postCreatedByProjectManagerOnThreadCreatedByProjectFinance = new Post(2L, projectManagerUser, "response", emptyList(), ZonedDateTime.now().plusMinutes(1L));

        Query queryCreatedByProjectFinance = new Query(1L,
                projectFinance1.getId(),
                "org.innovateuk.ifs.finance.domain.ProjectFinance",
                asList(postCreatedByProjectFinanceOnThreadCreatedByProjectFinance, postCreatedByProjectManagerOnThreadCreatedByProjectFinance),
                FinanceChecksSectionType.ELIGIBILITY,
                "title1",
                ZonedDateTime.now());
        queryRepository.save(queryCreatedByProjectFinance);

        // Check different thread on same project/org is ignored
        Post postCreatedByProjectFinanceOnDifferentThread = new Post(3L, projectFinanceUser, "query1", emptyList(), ZonedDateTime.now());

        Post postCreatedByProjectManagerOnDifferent = new Post(4L, projectManagerUser, "response", emptyList(), ZonedDateTime.now().plusMinutes(1L));

        Query queryCreatedByProjectFinanceOnDifferentThread = new Query(2L,
                projectFinance1.getId(),
                "org.innovateuk.ifs.finance.domain.ProjectFinance",
                asList(postCreatedByProjectFinanceOnDifferentThread, postCreatedByProjectManagerOnDifferent),
                FinanceChecksSectionType.ELIGIBILITY,
                "title2",
                ZonedDateTime.now());
        queryRepository.save(queryCreatedByProjectFinanceOnDifferentThread);

        // Check that query from a different competition is ignored
        Competition competition2 = newCompetition().withId(competition1.getId() + 1).withName("comp2").build();
        competition2 = competitionRepository.save(competition2);
        Application application2 = newApplication().withCompetition(competition2).withId(application1.getId() + 1).withName("app2").build();
        application2 = applicationRepository.save(application2);
        Project project2 = newProject().withId(project1.getId() + 1).withName("project2").withApplication(application2).build();
        project2 = projectRepository.save(project2);
        ProjectFinance projectFinance2 = newProjectFinance().withOrganisation(organisation1).withProject(project2).build();
        projectFinance2 = projectFinanceRepository.save(projectFinance2);
        Post postCreatedByProjectFinanceOnDifferentCompetition = new Post(5L, projectFinanceUser, "query2", emptyList(), ZonedDateTime.now());

        Post postCreatedByProjectManagerOnDifferentCompetition = new Post(6L, projectManagerUser, "response2", emptyList(), ZonedDateTime.now().plusMinutes(1L));

        Query queryCreatedByProjectFinanceOnDifferentCompetition = new Query(3L,
                projectFinance2.getId(),
                "org.innovateuk.ifs.finance.domain.ProjectFinance",
                asList(postCreatedByProjectFinanceOnDifferentCompetition, postCreatedByProjectManagerOnDifferentCompetition),
                FinanceChecksSectionType.ELIGIBILITY,
                "title3",
                ZonedDateTime.now());
        queryRepository.save(queryCreatedByProjectFinanceOnDifferentCompetition);

        // Check that Notes are not considered in the query count/get
        Post postCreatedByProjectManager = new Post(7L, projectManagerUser, "response", emptyList(), ZonedDateTime.now().plusMinutes(1L));

        Note noteCreatedByProjectFinance = new Note(3L,
                projectFinance1.getId(),
                "org.innovateuk.ifs.finance.domain.ProjectFinance",
                asList(postCreatedByProjectManager),
                "title4",
                ZonedDateTime.now());
        noteRepository.save(noteCreatedByProjectFinance);

        // Check that queries not keyed on ProjectFinance class are ignored

        Post postCreatedByProjectManagerInvalidClass = new Post(8L, projectManagerUser, "response", emptyList(), ZonedDateTime.now().plusMinutes(1L));

        Query queryCreatedByProjectFinanceInvalidClass = new Query(4L,
                projectFinance1.getId(),
                "org.innovateuk.ifs.finance.domain.Wobble",
                asList(postCreatedByProjectManagerInvalidClass),
                FinanceChecksSectionType.ELIGIBILITY,
                "title5",
                ZonedDateTime.now());
        queryRepository.save(queryCreatedByProjectFinanceInvalidClass);

        setLoggedInUser(projectFinanceUserLogin);

        RestResult<Long> openQueriesCountResult = controller.countOpenQueries(competition1.getId());
        assertTrue(openQueriesCountResult.isSuccess());
        assertEquals(1L, openQueriesCountResult.getSuccessObject().longValue());

        RestResult<List<CompetitionOpenQueryResource>> openQueriesResult = controller.getOpenQueries(competition1.getId());
        assertTrue(openQueriesResult.isSuccess());
        assertEquals(1, openQueriesResult.getSuccessObject().size());
        assertEquals(new CompetitionOpenQueryResource(application1.getId(), organisation1.getId(), organisation1.getName(), project1.getId(), project1.getName()), openQueriesResult.getSuccessObject().get(0));
    }

    @Test
    @Transactional
    public void countOpenQueriesTwoPartnersHaveOpenQueries() throws Exception {
        setupUserData();
        Competition competition1 = newCompetition().build();
        competition1 = competitionRepository.save(competition1);
        Application application1 = newApplication().withCompetition(competition1).build();
        application1 = applicationRepository.save(application1);
        Project project1 = newProject().withId(27L).withName("project1").withApplication(application1).build();
        project1 = projectRepository.save(project1);
        Organisation organisation1 = newOrganisation().withId(23L).withName("Org1").build();
        organisation1 = organisationRepository.save(organisation1);
        ProjectFinance projectFinance1 = newProjectFinance().withOrganisation(organisation1).withProject(project1).build();
        projectFinance1 = projectFinanceRepository.save(projectFinance1);

        Post postCreatedByProjectFinanceOnThreadCreatedByProjectFinance = new Post(1L, projectFinanceUser, "query1", emptyList(), ZonedDateTime.now());

        Post postCreatedByProjectManagerOnThreadCreatedByProjectFinance = new Post(2L, projectManagerUser, "response", emptyList(), ZonedDateTime.now().plusMinutes(1L));

        Query queryCreatedByProjectFinance = new Query(1L,
                projectFinance1.getId(),
                "org.innovateuk.ifs.finance.domain.ProjectFinance",
                asList(postCreatedByProjectFinanceOnThreadCreatedByProjectFinance, postCreatedByProjectManagerOnThreadCreatedByProjectFinance),
                FinanceChecksSectionType.ELIGIBILITY,
                "title1",
                ZonedDateTime.now());
        queryRepository.save(queryCreatedByProjectFinance);

        Organisation organisation2 = newOrganisation().withId(27L).withName("aOrg2").build();
        organisation2 = organisationRepository.save(organisation2);
        ProjectFinance projectFinance2 = newProjectFinance().withOrganisation(organisation2).withProject(project1).build();
        projectFinance2 = projectFinanceRepository.save(projectFinance2);

        Post postCreatedByProjectFinancePartner2 = new Post(3L, projectFinanceUser, "query2", emptyList(), ZonedDateTime.now());

        Post postCreatedByProjectManagerPartner2 = new Post(4L, projectManagerUser, "response2", emptyList(), ZonedDateTime.now().plusMinutes(1L));

        Query queryCreatedByProjectFinancePartner2 = new Query(2L,
                projectFinance2.getId(),
                "org.innovateuk.ifs.finance.domain.ProjectFinance",
                asList(postCreatedByProjectFinancePartner2, postCreatedByProjectManagerPartner2),
                FinanceChecksSectionType.ELIGIBILITY,
                "title2",
                ZonedDateTime.now());
        queryRepository.save(queryCreatedByProjectFinancePartner2);

        setLoggedInUser(projectFinanceUserLogin);

        RestResult<Long> openQueriesCountResult = controller.countOpenQueries(competition1.getId());
        assertTrue(openQueriesCountResult.isSuccess());
        assertEquals(2L, openQueriesCountResult.getSuccessObject().longValue());

        RestResult<List<CompetitionOpenQueryResource>> openQueriesResult = controller.getOpenQueries(competition1.getId());
        assertTrue(openQueriesResult.isSuccess());
        assertEquals(2, openQueriesResult.getSuccessObject().size());
        // check ordering by application id and organisation name
        assertEquals(new CompetitionOpenQueryResource(application1.getId(), organisation2.getId(), organisation2.getName(), project1.getId(), project1.getName()), openQueriesResult.getSuccessObject().get(0));
        assertEquals(new CompetitionOpenQueryResource(application1.getId(), organisation1.getId(), organisation1.getName(), project1.getId(), project1.getName()), openQueriesResult.getSuccessObject().get(1));
    }

    @Test
    @Transactional
    public void countOpenQueriesTwoProjectsHaveOpenQueries() throws Exception {
        setupUserData();
        Competition competition1 = newCompetition().build();
        competition1 = competitionRepository.save(competition1);
        Application application1 = newApplication().withCompetition(competition1).build();
        application1 = applicationRepository.save(application1);
        Project project1 = newProject().withId(27L).withName("project1").withApplication(application1).build();
        project1 = projectRepository.save(project1);
        Organisation organisation1 = newOrganisation().withId(23L).withName("Org1").build();
        organisation1 = organisationRepository.save(organisation1);
        ProjectFinance projectFinance1 = newProjectFinance().withOrganisation(organisation1).withProject(project1).build();
        projectFinance1 = projectFinanceRepository.save(projectFinance1);

        Post postCreatedByProjectFinanceOnThreadCreatedByProjectFinance = new Post(1L, projectFinanceUser, "query1", emptyList(), ZonedDateTime.now());

        Post postCreatedByProjectManagerOnThreadCreatedByProjectFinance = new Post(2L, projectManagerUser, "response", emptyList(), ZonedDateTime.now().plusMinutes(1L));

        Query queryCreatedByProjectFinance = new Query(1L,
                projectFinance1.getId(),
                "org.innovateuk.ifs.finance.domain.ProjectFinance",
                asList(postCreatedByProjectFinanceOnThreadCreatedByProjectFinance, postCreatedByProjectManagerOnThreadCreatedByProjectFinance),
                FinanceChecksSectionType.ELIGIBILITY,
                "title1",
                ZonedDateTime.now());
        queryRepository.save(queryCreatedByProjectFinance);

        Application application2 = newApplication().withId(application1.getId() + 1).withCompetition(competition1).build();
        application2 = applicationRepository.save(application2);
        Project project2 = newProject().withId(29L).withName("aproject2").withApplication(application2).build();
        project2 = projectRepository.save(project2);
        ProjectFinance projectFinance2 = newProjectFinance().withOrganisation(organisation1).withProject(project2).build();
        projectFinance2 = projectFinanceRepository.save(projectFinance2);

        Post postCreatedByProjectFinancePartner2 = new Post(3L, projectFinanceUser, "query2", emptyList(), ZonedDateTime.now());

        Post postCreatedByProjectManagerPartner2 = new Post(4L, projectManagerUser, "response2", emptyList(), ZonedDateTime.now().plusMinutes(1L));

        Query queryCreatedByProjectFinancePartner2 = new Query(2L,
                projectFinance2.getId(),
                "org.innovateuk.ifs.finance.domain.ProjectFinance",
                asList(postCreatedByProjectFinancePartner2, postCreatedByProjectManagerPartner2),
                FinanceChecksSectionType.ELIGIBILITY,
                "title2",
                ZonedDateTime.now());
        queryRepository.save(queryCreatedByProjectFinancePartner2);

        setLoggedInUser(projectFinanceUserLogin);

        RestResult<Long> openQueriesCountResult = controller.countOpenQueries(competition1.getId());
        assertTrue(openQueriesCountResult.isSuccess());
        assertEquals(2L, openQueriesCountResult.getSuccessObject().longValue());

        RestResult<List<CompetitionOpenQueryResource>> openQueriesResult = controller.getOpenQueries(competition1.getId());
        assertTrue(openQueriesResult.isSuccess());
        assertEquals(2, openQueriesResult.getSuccessObject().size());
        // check ordering by application id and organisation name
        assertEquals(new CompetitionOpenQueryResource(application1.getId(), organisation1.getId(), organisation1.getName(), project1.getId(), project1.getName()), openQueriesResult.getSuccessObject().get(0));
        assertEquals(new CompetitionOpenQueryResource(application2.getId(), organisation1.getId(), organisation1.getName(), project2.getId(), project2.getName()), openQueriesResult.getSuccessObject().get(1));
    }

    private List<Long> createCreatedAssessmentsWithCompetition(Long competitionId, UserResource assessor, int numberOfAssessments, ActivityState created) {
        List<Application> applications = newApplication()
                .withCompetition(competitionRepository.findById(competitionId))
                .withApplicationState(ApplicationState.CREATED)
                .build(numberOfAssessments);

        applications.stream().forEach(application -> application.getApplicationProcess().setActivityState(created));

        applicationRepository.save(applications);

        ActivityState activityState = activityStateRepository.findOneByActivityTypeAndState(APPLICATION_ASSESSMENT, CREATED);

        List<Assessment> assessments = simpleMap(
                applications,
                application -> newAssessment()
                        .withApplication(application)
                        .withActivityState(activityState)
                        .withParticipant(
                                newProcessRole()
                                        .withUser(userMapper.mapToDomain(assessor))
                                        .withApplication(application)
                                        .build()
                        )
                        .build()
        );

        List<Assessment> savedAssessments = new ArrayList();
        assessmentRepository.save(assessments).forEach(a -> savedAssessments.add(a));

        return savedAssessments.stream().map(Assessment::getId).collect(Collectors.toList());
    }

    private CompetitionResource createWithDates(ZonedDateTime startDate,
                                                ZonedDateTime endDate,
                                                ZonedDateTime assessorAcceptsDate,
                                                ZonedDateTime assessorsNotifiedDate,
                                                ZonedDateTime assessmentClosedDate,
                                                ZonedDateTime fundersPanelDate,
                                                ZonedDateTime fundersPanelEndDate,
                                                ZonedDateTime feedbackReleasedDate
    ) {
        CompetitionResource comp = controller.create().getSuccessObjectOrThrowException();

        List<Milestone> milestones = createNewMilestones(comp, startDate, endDate, assessorAcceptsDate,
                fundersPanelDate, fundersPanelEndDate, assessorsNotifiedDate, assessmentClosedDate);

        if (feedbackReleasedDate != null) {
            milestones.add(new Milestone(FEEDBACK_RELEASED, feedbackReleasedDate, milestones.get(0).getCompetition()));
        }

        milestones.forEach(milestone -> milestoneRepository.save(milestone));

        controller.saveCompetition(comp, comp.getId()).getSuccessObjectOrThrowException();

        //TODO replace with controller endpoint for competition setup finished
        Competition compEntity = competitionRepository.findById(comp.getId());
        compEntity.setSetupComplete(true);
        competitionRepository.save(compEntity);
        flushAndClearSession();

        return controller.getCompetitionById(comp.getId()).getSuccessObjectOrThrowException();
    }

    private List<Milestone> createNewMilestones(CompetitionResource comp, ZonedDateTime startDate,
                                                ZonedDateTime endDate, ZonedDateTime assessorAcceptsDate,
                                                ZonedDateTime fundersPanelDate, ZonedDateTime fundersPanelEndDate,
                                                ZonedDateTime assessorsNotifiedDate, ZonedDateTime assessmentClosedDate) {

        return EnumSet.allOf(MilestoneType.class).stream().filter(milestoneType -> milestoneType != FEEDBACK_RELEASED).map(milestoneType -> {
            Competition competition = assignCompetitionId(comp);
            final ZonedDateTime milestoneDate;
            switch (milestoneType) {
                case OPEN_DATE:
                    milestoneDate = startDate;
                    break;
                case SUBMISSION_DATE:
                    milestoneDate = endDate;
                    break;
                case ASSESSOR_ACCEPTS:
                    milestoneDate = assessorAcceptsDate;
                    break;
                case ASSESSORS_NOTIFIED:
                    milestoneDate = assessorsNotifiedDate;
                    break;
                case ASSESSMENT_CLOSED:
                    milestoneDate = assessmentClosedDate;
                    break;
                case FUNDERS_PANEL:
                    milestoneDate = fundersPanelDate;
                    break;
                case NOTIFICATIONS:
                    milestoneDate = fundersPanelEndDate;
                    break;
                default:
                    milestoneDate = ZonedDateTime.now();
            }
            return new Milestone(milestoneType, milestoneDate, competition);
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

    private void checkUpdatedCompetitionCategories(CompetitionResource savedCompetition) {
        assertEquals(COMPETITION_NAME_UPDATED, savedCompetition.getName());

        assertThat(savedCompetition.getInnovationSector(), is(INNOVATION_SECTOR_ID));
        assertThat(savedCompetition.getInnovationSectorName(), is(INNOVATION_SECTOR_NAME));
        assertThat(savedCompetition.getResearchCategories(), hasItem(RESEARCH_CATEGORY_ID_ONE));

        assertThat(savedCompetition.getInnovationAreas(), hasItem(INNOVATION_AREA_ID));
        assertEquals(1, savedCompetition.getInnovationAreas().size());
        assertThat(savedCompetition.getInnovationAreaNames(), hasItem(INNOVATION_AREA_NAME));
    }

    private void checkExistingCompetition(CompetitionResource competition) {
        assertThat(competition, notNullValue());
        assertThat(competition.getName(), is(EXISTING_COMPETITION_NAME));
        assertThat(competition.getCompetitionStatus(), is(CompetitionStatus.OPEN));
        assertThat(competition.isUseResubmissionQuestion(), is(true));
    }

    private void checkNewCompetition(CompetitionResource competition) {
        assertThat(competition, notNullValue());
        assertThat(competition.getName(), isEmptyOrNullString());
        assertThat(competition.getCompetitionStatus(), is(CompetitionStatus.COMPETITION_SETUP));
        assertThat(competition.isUseResubmissionQuestion(), is(true));
        assertThat(competition.getInnovationSector(), is(INNOVATION_SECTOR_ID));
        assertThat(competition.getResearchCategories(), hasItem(RESEARCH_CATEGORY_ID_ONE));
    }
}
