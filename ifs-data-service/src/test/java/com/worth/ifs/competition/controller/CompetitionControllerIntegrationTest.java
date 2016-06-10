package com.worth.ifs.competition.controller;

import com.worth.ifs.BaseControllerIntegrationTest;
import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.competition.resource.CompetitionSetupCompletedSectionResource;
import com.worth.ifs.competition.resource.CompetitionSetupSectionResource;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Integration test for testing the rest servcies of the competition controller
 */
@Rollback
@Transactional
public class CompetitionControllerIntegrationTest extends BaseControllerIntegrationTest<CompetitionController> {


    public static final String COMPETITION_NAME_UPDATED = "Competition name updated";
    public static final int INNOVATION_SECTOR_ID = 1;
    public static final String INNOVATION_SECTOR_NAME = "Health and life sciences";
    public static final int INNOVATION_AREA_ID = 9;
    public static final String INNOVATION_AREA_NAME = "Agriculture and food";
    public static final String EXISTING_COMPETITION_NAME = "Connected digital additive manufacturing";
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
        getAllCompetitions(2);
        createNewCompetition();
        createNewCompetition();
        createNewCompetition();
        List<CompetitionResource> competitions = getAllCompetitions(5);
        competitions.get(0).setCode("1606-1");
        controller.saveCompetition(competitions.get(0), competitions.get(0).getId());

        flushAndClearSession();

        RestResult<String> generatedCode = controller.generateCompetitionCode(LocalDateTime.of(2016, 6, 5, 12, 00), competitions.get(1).getId());
        assertTrue(generatedCode.isSuccess());
        assertEquals("1606-2", generatedCode.getSuccessObject());

        flushAndClearSession();

        generatedCode = controller.generateCompetitionCode(LocalDateTime.of(2016, 6, 5, 12, 00), competitions.get(2).getId());
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
    public void testCompetitionSetupSectionStatus() throws Exception {
        getAllCompetitions(2);

        // Create new competition
        CompetitionResource competition = createNewCompetition();

        RestResult<List<CompetitionSetupCompletedSectionResource>> statuses = controller.findAllCompetitionSection(competition.getId());
        assertTrue(statuses.isSuccess());
        assertTrue(statuses.getSuccessObject().isEmpty());
    }

    @Rollback
    @Test
    public void testCompetitionSetupSections() throws Exception {
        RestResult<List<CompetitionSetupSectionResource>> sectionsResult = controller.findAllCompetitionSections();
        assertTrue(sectionsResult.isSuccess());
        List<CompetitionSetupSectionResource> sections = sectionsResult.getSuccessObject();

        // Check if all the sections are here.
        assertEquals(7L, (long) sections.size());

        // Test ordering.
        assertEquals("Initial details", sections.get(0).getName());
        assertEquals("Finance", sections.get(6).getName());
    }

    @Rollback
    @Test
    public void testCompetitionCompletedSections() throws Exception {
        Long competitionId = 7L;
        checkCompletedSections(competitionId);
    }

    @Rollback
    @Test
    public void testCompetitionCompleteSection() throws Exception {
        Long competitionId = 7L;
        checkCompletedSections(competitionId);
        controller.markSectionComplete(competitionId, 4L);
        checkCompletedSectionsAfterCompletingSection(competitionId);
    }

    @Rollback
    @Test
    public void testCompetitionInCompleteSection() throws Exception {
        Long competitionId = 7L;
        checkCompletedSections(competitionId);
        controller.markSectionInComplete(competitionId, 3L);
        checkCompletedSectionsAfterIncompletingSection(competitionId);
    }

    private void checkCompletedSections(Long competitionId) {
        RestResult<List<CompetitionSetupCompletedSectionResource>> sectionsResult = controller.findAllCompetitionSection(competitionId);
        assertTrue(sectionsResult.isSuccess());
        List<CompetitionSetupCompletedSectionResource> sections = sectionsResult.getSuccessObject();
        // Check if all the sections are here.
        assertEquals(2L, (long) sections.size());
        // Test ordering.
        assertEquals(2, (long) sections.get(0).getCompetitionSetupSection());
        assertEquals(3, (long) sections.get(1).getCompetitionSetupSection());
    }

    private void checkCompletedSectionsAfterCompletingSection(Long competitionId) {
        RestResult<List<CompetitionSetupCompletedSectionResource>> sectionsResult = controller.findAllCompetitionSection(competitionId);
        assertTrue(sectionsResult.isSuccess());
        List<CompetitionSetupCompletedSectionResource> sections = sectionsResult.getSuccessObject();
        // Check if all the sections are here.
        assertEquals(3L, (long) sections.size());
        // Test ordering.
        assertEquals(2, (long) sections.get(0).getCompetitionSetupSection());
        assertEquals(3, (long) sections.get(1).getCompetitionSetupSection());
        assertEquals(4, (long) sections.get(2).getCompetitionSetupSection());
    }

    private void checkCompletedSectionsAfterIncompletingSection(Long competitionId) {
        RestResult<List<CompetitionSetupCompletedSectionResource>> sectionsResult = controller.findAllCompetitionSection(competitionId);
        assertTrue(sectionsResult.isSuccess());
        List<CompetitionSetupCompletedSectionResource> sections = sectionsResult.getSuccessObject();
        // Check if all the sections are here.
        assertEquals(1L, (long) sections.size());
        // Test ordering.
        assertEquals(2, (long) sections.get(0).getCompetitionSetupSection());
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
