package com.worth.ifs.competition.domain;

import com.worth.ifs.application.domain.Application;
import com.worth.ifs.application.domain.Question;
import com.worth.ifs.application.domain.Section;
import com.worth.ifs.competition.mapper.CompetitionMapper;
import com.worth.ifs.competition.resource.CompetitionResource;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
    private LocalDateTime assemmentStartDate;
    private LocalDateTime assemmentEndDate;

    private String activityCode;
    private String budgetCode;
    private String coFunders;
    private String coFundersBudget;

    @Before
    public void setUp() throws Exception {
        id = 0L;

        name = "testCompetitionName";
        description = "testCompetitionDescription";
        startDate = LocalDateTime.now().minusDays(5);
        endDate = startDate.plusDays(15);
        assemmentStartDate = endDate;
        assemmentEndDate = endDate.plusDays(15);

        maxResearchRatio = 10;
        academicGrantPercentage = 30;

        activityCode = "ActivityCode";
        budgetCode = "BudgetCode";
        coFunders = "CoFunders";
        coFundersBudget = "CoFundersBudget";

        sections = new ArrayList<>();
        sections.add(new Section());
        sections.add(new Section());
        sections.add(new Section());

        competition = new Competition(id, applications, questions, sections, name, description, startDate, endDate);
        competition.setAssessmentStartDate(assemmentStartDate);
        competition.setAssessmentEndDate(assemmentEndDate);
        competition.setMaxResearchRatio(maxResearchRatio);
        competition.setAcademicGrantPercentage(academicGrantPercentage);

        competition.setActivityCode(activityCode);
        competition.setBudgetCode(budgetCode);
        competition.setCoFunders(coFunders);
        competition.setCoFundersBudget(coFundersBudget);
    }

    @Test
    public void competitionShouldReturnCorrectAttributeValues() throws Exception {
        assertEquals(competition.getId(), id);
        assertEquals(competition.getName(), name);
        assertEquals(competition.getDescription(), description);
        assertEquals(competition.getSections(), sections);
        assertEquals(competition.getMaxResearchRatio(), maxResearchRatio);
        assertEquals(competition.getAcademicGrantPercentage(), academicGrantPercentage);

        assertEquals(competition.getActivityCode(), activityCode);
        assertEquals(competition.getBudgetCode(), budgetCode);
        assertEquals(competition.getCoFunders(), coFunders);
        assertEquals(competition.getCoFundersBudget(), coFundersBudget);
    }

    @Test
    public void competitionStatusOpen(){
        assertEquals(CompetitionResource.Status.OPEN, competition.getCompetitionStatus());
    }

    @Test
    public void competitionStatusNotStarted(){
        competition.setStartDate(LocalDateTime.now().plusDays(1));
        assertEquals(CompetitionResource.Status.NOT_STARTED, competition.getCompetitionStatus());
    }

    @Test
    public void competitionClosingSoon(){
        CompetitionResource competitionResource = new CompetitionResource();
        competitionResource.setCompetitionStatus(CompetitionResource.Status.OPEN);
        competitionResource.setStartDate(LocalDateTime.now().minusDays(4));
        competitionResource.setEndDate(LocalDateTime.now().plusHours(1));
        assertTrue(competitionResource.isClosingSoon());
    }

    @Test
    public void competitionNotClosingSoon(){
        CompetitionResource competitionResource = new CompetitionResource();
        competitionResource.setCompetitionStatus(CompetitionResource.Status.OPEN);
        competitionResource.setStartDate(LocalDateTime.now().minusDays(4));
        competitionResource.setEndDate(LocalDateTime.now().plusHours(1));
        assertTrue(competitionResource.isClosingSoon());
    }

    @Test
    public void competitionStatusInAssessment(){
        competition.setEndDate(LocalDateTime.now().minusDays(1));
        competition.setAssessmentStartDate(LocalDateTime.now().minusDays(1));
        assertEquals(CompetitionResource.Status.IN_ASSESSMENT, competition.getCompetitionStatus());
    }
    
    @Test
    public void competitionStatusFundersPanelAsFundersPanelEndDateAbsent(){
        competition.setEndDate(LocalDateTime.now().minusDays(4));
        competition.setAssessmentStartDate(LocalDateTime.now().minusDays(3));
        competition.setAssessmentEndDate(LocalDateTime.now().minusDays(2));
        assertEquals(CompetitionResource.Status.FUNDERS_PANEL, competition.getCompetitionStatus());
    }
    
    @Test
    public void competitionStatusFundersPanelAsFundersPanelEndDatePresentButInFuture(){
        competition.setEndDate(LocalDateTime.now().minusDays(4));
        competition.setAssessmentStartDate(LocalDateTime.now().minusDays(3));
        competition.setAssessmentEndDate(LocalDateTime.now().minusDays(2));
        competition.setFundersPanelEndDate(LocalDateTime.now().plusDays(1));
        assertEquals(CompetitionResource.Status.FUNDERS_PANEL, competition.getCompetitionStatus());
    }
    
    @Test
    public void competitionStatusAssessorFeedback(){
        competition.setEndDate(LocalDateTime.now().minusDays(4));
        competition.setAssessmentStartDate(LocalDateTime.now().minusDays(3));
        competition.setAssessmentEndDate(LocalDateTime.now().minusDays(2));
        competition.setFundersPanelEndDate(LocalDateTime.now().minusDays(1));
        assertEquals(CompetitionResource.Status.ASSESSOR_FEEDBACK, competition.getCompetitionStatus());
    }
}