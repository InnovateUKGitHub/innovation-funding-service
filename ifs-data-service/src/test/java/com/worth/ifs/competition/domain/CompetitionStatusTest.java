package com.worth.ifs.competition.domain;

import com.worth.ifs.competition.domain.Competition.DateProvider;
import com.worth.ifs.competition.resource.CompetitionResource;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CompetitionStatusTest {

	private Competition competition;
    
    private DateProvider dateProvider;

    private LocalDateTime currentDate;
    private LocalDateTime future;
    private LocalDateTime past;
	
    @Before
    public void setUp() {
    	currentDate = LocalDateTime.now();
    	future = currentDate.plusNanos(1);
    	past = currentDate.minusNanos(1);
    	
    	dateProvider = mock(DateProvider.class);
    	when(dateProvider.provideDate()).thenReturn(currentDate);
    	
    	competition = new Competition();
    	competition.setDateProvider(dateProvider);
    }
    
    @Test
    public void competitionStatusNotStarted(){
    	competition.setStartDate(future);
        
        assertEquals(CompetitionResource.Status.NOT_STARTED, competition.getCompetitionStatus());
    }

    @Test
    public void competitionStatusOpenIfStartDateInPastAndEndDateInFuture(){
    	competition.setStartDate(past);
    	competition.setEndDate(future);

        assertEquals(CompetitionResource.Status.OPEN, competition.getCompetitionStatus());
    }
    
    @Test
    public void competitionStatusOpenIfStartDateMetAndEndDateInFuture(){
    	competition.setStartDate(currentDate);
    	competition.setEndDate(future);

        assertEquals(CompetitionResource.Status.OPEN, competition.getCompetitionStatus());
    }
    
    @Test
    public void competitionStatusInAssessmentIfEndDateInPastAndAssessmentEndDateInFuture(){
    	competition.setStartDate(past);
    	competition.setEndDate(past);
    	competition.setAssessmentEndDate(future);

        assertEquals(CompetitionResource.Status.IN_ASSESSMENT, competition.getCompetitionStatus());
    }
    
    @Test
    public void competitionStatusInAssessmentIfEndDateMetAndAssessmentEndDateInFuture(){
    	competition.setStartDate(past);
    	competition.setEndDate(currentDate);
    	competition.setAssessmentEndDate(future);

        assertEquals(CompetitionResource.Status.IN_ASSESSMENT, competition.getCompetitionStatus());
    }
    
    @Test
    public void competitionStatusFundersPanelIfAssessmentEndDateInPastAndFundersPanelEndDateInFuture(){
    	competition.setStartDate(past);
    	competition.setEndDate(past);
    	competition.setAssessmentEndDate(past);
    	competition.setFundersPanelEndDate(future);

        assertEquals(CompetitionResource.Status.FUNDERS_PANEL, competition.getCompetitionStatus());
    }
    
    @Test
    public void competitionStatusFundersPanelIfAssessmentEndDateMetAndFundersPanelEndDateInFuture(){
    	competition.setStartDate(past);
    	competition.setEndDate(past);
    	competition.setAssessmentEndDate(currentDate);
    	competition.setFundersPanelEndDate(future);

        assertEquals(CompetitionResource.Status.FUNDERS_PANEL, competition.getCompetitionStatus());
    }
    
    @Test
    public void competitionStatusFundersPanelIfAssessmentEndDateInPastAndFundersPanelEndDateNotSet(){
    	competition.setStartDate(past);
    	competition.setEndDate(past);
    	competition.setAssessmentEndDate(past);

        assertEquals(CompetitionResource.Status.FUNDERS_PANEL, competition.getCompetitionStatus());
    }
    
    @Test
    public void competitionStatusFundersPanelIfAssessmentEndDateMetAndFundersPanelEndDateNotSet(){
    	competition.setStartDate(past);
    	competition.setEndDate(past);
    	competition.setAssessmentEndDate(currentDate);

        assertEquals(CompetitionResource.Status.FUNDERS_PANEL, competition.getCompetitionStatus());
    }
    
    @Test
    public void competitionStatusAssessorFeedbackIfFundersPanelEndDateInPastAndAssessorFeedbackDateInFuture(){
    	competition.setStartDate(past);
    	competition.setEndDate(past);
    	competition.setAssessmentEndDate(past);
    	competition.setFundersPanelEndDate(past);
    	competition.setAssessorFeedbackDate(future);

        assertEquals(CompetitionResource.Status.ASSESSOR_FEEDBACK, competition.getCompetitionStatus());
    }
    
    @Test
    public void competitionStatusAssessorFeedbackIfFundersPanelEndDateMetAndAssessorFeedbackDateInFuture(){
    	competition.setStartDate(past);
    	competition.setEndDate(past);
    	competition.setAssessmentEndDate(past);
    	competition.setFundersPanelEndDate(currentDate);
    	competition.setAssessorFeedbackDate(future);

        assertEquals(CompetitionResource.Status.ASSESSOR_FEEDBACK, competition.getCompetitionStatus());
    }
    
    @Test
    public void competitionStatusAssessorFeedbackIfFundersPanelEndDateInPastAndAssessorFeedbackDateNotSet(){
    	competition.setStartDate(past);
    	competition.setEndDate(past);
    	competition.setAssessmentEndDate(past);
    	competition.setFundersPanelEndDate(past);

        assertEquals(CompetitionResource.Status.ASSESSOR_FEEDBACK, competition.getCompetitionStatus());
    }
    
    @Test
    public void competitionStatusAssessorFeedbackIfFundersPanelEndDateMetAndAssessorFeedbackDateNotSet(){
    	competition.setStartDate(past);
    	competition.setEndDate(past);
    	competition.setAssessmentEndDate(past);
    	competition.setFundersPanelEndDate(currentDate);
    	competition.setAssessorFeedbackDate(future);

        assertEquals(CompetitionResource.Status.ASSESSOR_FEEDBACK, competition.getCompetitionStatus());
    }
    
    @Test
    public void competitionStatusProjectSetupIfAssessorFeedbackDateInPast(){
    	competition.setStartDate(past);
    	competition.setEndDate(past);
    	competition.setAssessmentEndDate(past);
    	competition.setFundersPanelEndDate(past);
    	competition.setAssessorFeedbackDate(past);

        assertEquals(CompetitionResource.Status.PROJECT_SETUP, competition.getCompetitionStatus());
    }
    
    @Test
    public void competitionStatusProjectSetupIfAssessorFeedbackDateMet(){
    	competition.setStartDate(past);
    	competition.setEndDate(past);
    	competition.setAssessmentEndDate(past);
    	competition.setFundersPanelEndDate(past);
    	competition.setAssessorFeedbackDate(currentDate);

        assertEquals(CompetitionResource.Status.PROJECT_SETUP, competition.getCompetitionStatus());
    }

    @Test
    public void competitionStatusProjectSetupForNewCompetition(){
        Competition newCompetition = new Competition();
        assertEquals(CompetitionResource.Status.PROJECT_SETUP, newCompetition.getCompetitionStatus());
    }
    
}
