package com.worth.ifs.competition.domain;

import com.worth.ifs.application.domain.Application;
import com.worth.ifs.application.domain.Question;
import com.worth.ifs.application.domain.Section;
import com.worth.ifs.competition.mapper.CompetitionMapper;
import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.competition.resource.CompetitionStatus;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.worth.ifs.competition.resource.CompetitionStatus.*;
import static junit.framework.TestCase.assertFalse;
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
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Integer maxResearchRatio;
    private Integer academicGrantPercentage;

    private String budgetCode;

    private String activityCode;
    private String innovateBudget;
    private String funder;
    private BigDecimal funderBudget;

    @Before
    public void setUp() throws Exception {
        id = 0L;

        name = "testCompetitionName";
        description = "testCompetitionDescription";
        startDate = LocalDateTime.now().minusDays(5);
        endDate = startDate.plusDays(15);

        maxResearchRatio = 10;
        academicGrantPercentage = 30;

        budgetCode = "BudgetCode";
        activityCode = "ActivityCode";
        innovateBudget = "Innovate Budget";
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
        competition.setInnovateBudget(innovateBudget);
        competition.setFunders(getTestFunders(competition));
    }


    private List<CompetitionFunder> getTestFunders(Competition competition) {
        List<CompetitionFunder> returnList = new ArrayList<>();
        CompetitionFunder Funder1 = new CompetitionFunder();
        Funder1.setId(1L);
        Funder1.setCompetition(competition);
        Funder1.setFunder("Funder1");
        Funder1.setFunderBudget(new BigDecimal(1));
        Funder1.setCoFunder(false);
        returnList.add(Funder1);


        CompetitionFunder coFunder1 = new CompetitionFunder();
        coFunder1.setId(1L);
        coFunder1.setCompetition(competition);
        coFunder1.setFunder("CoFunder1");
        coFunder1.setFunderBudget(new BigDecimal(1));
        coFunder1.setCoFunder(true);
        returnList.add(coFunder1);

        CompetitionFunder coFunder2 = new CompetitionFunder();
        coFunder2.setId(2L);
        coFunder2.setCompetition(competition);
        coFunder2.setFunder("CoFunder2");
        coFunder2.setFunderBudget(new BigDecimal(2));
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
        assertEquals(competition.getInnovateBudget(), innovateBudget);
        assertEquals(competition.getFunders().size(), getTestFunders(competition).size());
    }

    @Test
    public void competitionStatusOpen(){
        assertEquals(OPEN, competition.getCompetitionStatus());
    }

    @Test
    public void competitionStatusReadyToOpen(){
        competition.setStartDate(LocalDateTime.now().plusDays(1));
        assertEquals(READY_TO_OPEN, competition.getCompetitionStatus());
    }

    @Test
    public void competitionClosingSoon(){
        CompetitionResource competitionResource = new CompetitionResource();
        competitionResource.setCompetitionStatus(OPEN);
        competitionResource.setStartDate(LocalDateTime.now().minusDays(4));
        competitionResource.setEndDate(LocalDateTime.now().plusHours(1));
        assertTrue(competitionResource.isClosingSoon());
    }

    @Test
    public void competitionNotClosingSoon(){
        CompetitionResource competitionResource = new CompetitionResource();
        competitionResource.setCompetitionStatus(OPEN);
        competitionResource.setStartDate(LocalDateTime.now().minusDays(4));
        competitionResource.setEndDate(LocalDateTime.now().plusHours(3));
        assertFalse(competitionResource.isClosingSoon());
    }

    @Test
    public void competitionStatusInAssessment(){
        competition.setEndDate(LocalDateTime.now().minusDays(1));
        competition.notifyAssessors(LocalDateTime.now().minusDays(1));
        competition.setFundersPanelDate(LocalDateTime.now().plusDays(1));
        assertEquals(IN_ASSESSMENT, competition.getCompetitionStatus());
    }
    
    @Test
    public void competitionStatusFundersPanelAsFundersPanelEndDateAbsent(){
        competition.setEndDate(LocalDateTime.now().minusDays(5));
        competition.notifyAssessors(LocalDateTime.now().minusDays(4));
        competition.closeAssessment(LocalDateTime.now().minusDays(3));
        competition.setFundersPanelDate(LocalDateTime.now().minusDays(2));
        assertEquals(FUNDERS_PANEL, competition.getCompetitionStatus());
    }
    
    @Test
    public void competitionStatusFundersPanelAsFundersPanelEndDatePresentButInFuture(){
        competition.setEndDate(LocalDateTime.now().minusDays(6));
        competition.setAssessorAcceptsDate(LocalDateTime.now().minusDays(5));
        competition.notifyAssessors(LocalDateTime.now().minusDays(4));
        competition.closeAssessment(LocalDateTime.now().minusDays(3));
        competition.setFundersPanelDate(LocalDateTime.now().minusDays(2));
        competition.setFundersPanelEndDate(LocalDateTime.now().plusDays(1));
        assertEquals(FUNDERS_PANEL, competition.getCompetitionStatus());
    }
    
    @Test
    public void competitionStatusAssessorFeedback(){
        competition.setEndDate(LocalDateTime.now().minusDays(6));
        competition.setAssessorAcceptsDate(LocalDateTime.now().minusDays(5));
        competition.notifyAssessors(LocalDateTime.now().minusDays(4));
        competition.closeAssessment(LocalDateTime.now().minusDays(3));
        competition.setFundersPanelDate(LocalDateTime.now().minusDays(2));
        competition.setFundersPanelEndDate(LocalDateTime.now().minusDays(1));
        assertEquals(ASSESSOR_FEEDBACK, competition.getCompetitionStatus());
    }
}