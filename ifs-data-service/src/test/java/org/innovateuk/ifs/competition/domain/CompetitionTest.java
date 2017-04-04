package org.innovateuk.ifs.competition.domain;

import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.domain.Question;
import org.innovateuk.ifs.application.domain.Section;
import org.innovateuk.ifs.competition.mapper.CompetitionMapper;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.ZonedDateTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import static junit.framework.TestCase.assertFalse;
import static org.innovateuk.ifs.competition.resource.CompetitionStatus.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CompetitionTest {
    private Competition competition;
    @Mock
    CompetitionMapper competitionMapper;

    private Long id;
    private List<Application> applications;
    private List<Question> questions;
    private List<Section> sections;
    private String name;
    private String description;
    private ZonedDateTime startDate;
    private ZonedDateTime endDate;
    private Integer maxResearchRatio;
    private Integer academicGrantPercentage;

    private String budgetCode;

    private String activityCode;
    private String funder;
    private BigDecimal funderBudget;

    @Before
    public void setUp() throws Exception {
        id = 0L;

        name = "testCompetitionName";
        description = "testCompetitionDescription";
        startDate = ZonedDateTime.now().minusDays(5);
        endDate = startDate.plusDays(15);

        maxResearchRatio = 10;
        academicGrantPercentage = 30;

        budgetCode = "BudgetCode";
        activityCode = "ActivityCode";
        funder = "Funder";
        funderBudget = new BigDecimal(0);

        sections = new ArrayList<>();
        sections.add(new Section());
        sections.add(new Section());
        sections.add(new Section());

        competition = new Competition(id, applications, questions, sections, name, description, startDate, endDate);
        competition.setMaxResearchRatio(maxResearchRatio);
        competition.setAcademicGrantPercentage(academicGrantPercentage);


        competition.setBudgetCode(budgetCode);
        competition.setActivityCode(activityCode);
        competition.setFunders(getTestFunders(competition));
    }


    private List<CompetitionFunder> getTestFunders(Competition competition) {
        List<CompetitionFunder> returnList = new ArrayList<>();
        CompetitionFunder Funder1 = new CompetitionFunder();
        Funder1.setId(1L);
        Funder1.setCompetition(competition);
        Funder1.setFunder("Funder1");
        Funder1.setFunderBudget(BigInteger.valueOf(1));
        Funder1.setCoFunder(false);
        returnList.add(Funder1);


        CompetitionFunder coFunder1 = new CompetitionFunder();
        coFunder1.setId(1L);
        coFunder1.setCompetition(competition);
        coFunder1.setFunder("CoFunder1");
        coFunder1.setFunderBudget(BigInteger.valueOf(1));
        coFunder1.setCoFunder(true);
        returnList.add(coFunder1);

        CompetitionFunder coFunder2 = new CompetitionFunder();
        coFunder2.setId(2L);
        coFunder2.setCompetition(competition);
        coFunder2.setFunder("CoFunder2");
        coFunder2.setFunderBudget(BigInteger.valueOf(2));
        coFunder1.setCoFunder(true);
        returnList.add(coFunder2);

        return returnList;
    }

    @Test
    public void competitionShouldReturnCorrectAttributeValues() throws Exception {
        assertEquals(competition.getId(), id);
        assertEquals(competition.getName(), name);
        assertEquals(competition.getDescription(), description);
        assertEquals(competition.getSections(), sections);
        assertEquals(competition.getMaxResearchRatio(), maxResearchRatio);
        assertEquals(competition.getAcademicGrantPercentage(), academicGrantPercentage);

        assertEquals(competition.getBudgetCode(), budgetCode);
        assertEquals(competition.getActivityCode(), activityCode);
        assertEquals(competition.getFunders().size(), getTestFunders(competition).size());
    }

    @Test
    public void competitionStatusOpen() {
        assertEquals(OPEN, competition.getCompetitionStatus());
    }

    @Test
    public void competitionStatusReadyToOpen() {
        competition.setStartDate(ZonedDateTime.now().plusDays(1));
        assertEquals(READY_TO_OPEN, competition.getCompetitionStatus());
    }

    @Test
    public void competitionClosingSoon() {
        CompetitionResource competitionResource = new CompetitionResource();
        competitionResource.setCompetitionStatus(OPEN);
        competitionResource.setStartDate(ZonedDateTime.now().minusDays(4));
        competitionResource.setEndDate(ZonedDateTime.now().plusHours(1));
        assertTrue(competitionResource.isClosingSoon());
    }

    @Test
    public void competitionNotClosingSoon() {
        CompetitionResource competitionResource = new CompetitionResource();
        competitionResource.setCompetitionStatus(OPEN);
        competitionResource.setStartDate(ZonedDateTime.now().minusDays(4));
        competitionResource.setEndDate(ZonedDateTime.now().plusHours(3).plusMinutes(1));
        assertFalse(competitionResource.isClosingSoon());
    }

    @Test
    public void competitionStatusInAssessment() {
        competition.setEndDate(ZonedDateTime.now().minusDays(1));
        competition.notifyAssessors(ZonedDateTime.now().minusDays(1));
        competition.setFundersPanelDate(ZonedDateTime.now().plusDays(1));
        assertEquals(IN_ASSESSMENT, competition.getCompetitionStatus());
    }

    @Test
    public void competitionStatusInAssessment_notCompetitionClosed() throws Exception {
        competition.setEndDate(ZonedDateTime.now().minusDays(3));
        competition.notifyAssessors(ZonedDateTime.now().minusDays(2));
        competition.closeAssessment(ZonedDateTime.now().minusDays(1));

        try {
            competition.notifyAssessors(ZonedDateTime.now());
        } catch (IllegalStateException e) {
            assertEquals("Tried to notify assessors when in competitionStatus=FUNDERS_PANEL. " +
                    "Applications can only be distributed when competitionStatus=CLOSED", e.getMessage());
        }
    }

    @Test
    public void competitionStatusInAssessment_alreadyInAssessment() throws Exception {
        competition.setEndDate(ZonedDateTime.now().minusDays(3));
        competition.notifyAssessors(ZonedDateTime.now().minusDays(2));
        competition.notifyAssessors(ZonedDateTime.now().minusDays(1));
        assertEquals(IN_ASSESSMENT, competition.getCompetitionStatus());
    }

    @Test
    public void competitionStatusFundersPanelAsFundersPanelEndDateAbsent() {
        competition.setEndDate(ZonedDateTime.now().minusDays(5));
        competition.notifyAssessors(ZonedDateTime.now().minusDays(4));
        competition.closeAssessment(ZonedDateTime.now().minusDays(3));
        competition.setFundersPanelDate(ZonedDateTime.now().minusDays(2));
        assertEquals(FUNDERS_PANEL, competition.getCompetitionStatus());
    }

    @Test
    public void competitionStatusFundersPanelAsFundersPanelEndDatePresentButInFuture() {
        competition.setEndDate(ZonedDateTime.now().minusDays(6));
        competition.setAssessorAcceptsDate(ZonedDateTime.now().minusDays(5));
        competition.notifyAssessors(ZonedDateTime.now().minusDays(4));
        competition.closeAssessment(ZonedDateTime.now().minusDays(3));
        competition.setFundersPanelDate(ZonedDateTime.now().minusDays(2));
        competition.setFundersPanelEndDate(ZonedDateTime.now().plusDays(1));
        assertEquals(FUNDERS_PANEL, competition.getCompetitionStatus());
    }

    @Test
    public void competitionStatusAssessorFeedback() {
        competition.setEndDate(ZonedDateTime.now().minusDays(6));
        competition.setAssessorAcceptsDate(ZonedDateTime.now().minusDays(5));
        competition.notifyAssessors(ZonedDateTime.now().minusDays(4));
        competition.closeAssessment(ZonedDateTime.now().minusDays(3));
        competition.setFundersPanelDate(ZonedDateTime.now().minusDays(2));
        competition.setFundersPanelEndDate(ZonedDateTime.now().minusDays(1));
        assertEquals(ASSESSOR_FEEDBACK, competition.getCompetitionStatus());
    }

    @Test
    public void competitionStatusReleaseFeedback() {
        competition.setEndDate(ZonedDateTime.now().minusDays(7));
        competition.setAssessorAcceptsDate(ZonedDateTime.now().minusDays(6));
        competition.notifyAssessors(ZonedDateTime.now().minusDays(5));
        competition.closeAssessment(ZonedDateTime.now().minusDays(4));
        competition.setFundersPanelDate(ZonedDateTime.now().minusDays(3));
        competition.setFundersPanelEndDate(ZonedDateTime.now().minusDays(2));
        competition.setReleaseFeedbackDate(ZonedDateTime.now().minusDays(1));
        assertEquals(PROJECT_SETUP, competition.getCompetitionStatus());
    }
}
