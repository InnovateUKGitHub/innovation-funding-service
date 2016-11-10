package com.worth.ifs.competition.domain;

import com.worth.ifs.competition.domain.Competition.DateProvider;
import com.worth.ifs.competition.resource.CompetitionStatus;
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
        competition.setSetupComplete(true);
    	competition.setDateProvider(dateProvider);
    }
    @Test
    public void competitionInSetup(){
        competition.setSetupComplete(false);

        assertEquals(CompetitionStatus.COMPETITION_SETUP, competition.getCompetitionStatus());
    }
    @Test
    public void competitionStatusReadyToOpen(){
    	competition.setStartDate(future);
        
        assertEquals(CompetitionStatus.READY_TO_OPEN, competition.getCompetitionStatus());
    }

    @Test
    public void competitionStatusOpenIfStartDateInPastAndEndDateInFuture(){
    	competition.setStartDate(past);
    	competition.setEndDate(future);

        assertEquals(CompetitionStatus.OPEN, competition.getCompetitionStatus());
    }
    
    @Test
    public void competitionStatusOpenIfStartDateMetAndEndDateInFuture(){
    	competition.setStartDate(currentDate);
    	competition.setEndDate(future);

        assertEquals(CompetitionStatus.OPEN, competition.getCompetitionStatus());
    }
    
    @Test
    public void competitionStatusInAssessmentIfEndDateInPastAndAssessmentEndDateInFuture(){
    	competition.setStartDate(past);
    	competition.setEndDate(past);
    	competition.setFundersPanelDate(future);

        assertEquals(CompetitionStatus.IN_ASSESSMENT, competition.getCompetitionStatus());
    }
    
    @Test
    public void competitionStatusInAssessmentIfEndDateMetAndAssessmentEndDateInFuture(){
    	competition.setStartDate(past);
    	competition.setEndDate(currentDate);
    	competition.setFundersPanelDate(future);

        assertEquals(CompetitionStatus.IN_ASSESSMENT, competition.getCompetitionStatus());
    }
    
    @Test
    public void competitionStatusFundersPanelIfFundersPanelDateInPastAndFundersPanelEndDateInFuture(){
    	competition.setStartDate(past);
    	competition.setEndDate(past);
    	competition.setFundersPanelDate(past);
    	competition.setFundersPanelEndDate(future);

        assertEquals(CompetitionStatus.FUNDERS_PANEL, competition.getCompetitionStatus());
    }
    
    @Test
    public void competitionStatusFundersPanelIfFundersPanelDateMetAndFundersPanelEndDateInFuture(){
    	competition.setStartDate(past);
    	competition.setEndDate(past);
    	competition.setFundersPanelDate(currentDate);
    	competition.setFundersPanelEndDate(future);

        assertEquals(CompetitionStatus.FUNDERS_PANEL, competition.getCompetitionStatus());
    }
    
    @Test
    public void competitionStatusFundersPanelIfFundersPanelDateInPastAndFundersPanelEndDateNotSet(){
    	competition.setStartDate(past);
    	competition.setEndDate(past);
    	competition.setFundersPanelDate(past);

        assertEquals(CompetitionStatus.FUNDERS_PANEL, competition.getCompetitionStatus());
    }
    
    @Test
    public void competitionStatusFundersPanelIfFundersPanelDateMetAndFundersPanelEndDateNotSet(){
    	competition.setStartDate(past);
    	competition.setEndDate(past);
    	competition.setFundersPanelDate(currentDate);

        assertEquals(CompetitionStatus.FUNDERS_PANEL, competition.getCompetitionStatus());
    }
    
    @Test
    public void competitionStatusAssessorFeedbackIfFundersPanelEndDateInPastAndAssessorFeedbackDateInFuture(){
    	competition.setStartDate(past);
    	competition.setEndDate(past);
    	competition.setFundersPanelDate(past);
    	competition.setFundersPanelEndDate(past);
    	competition.setAssessorFeedbackDate(future);

        assertEquals(CompetitionStatus.ASSESSOR_FEEDBACK, competition.getCompetitionStatus());
    }
    
    @Test
    public void competitionStatusAssessorFeedbackIfFundersPanelEndDateMetAndAssessorFeedbackDateInFuture(){
    	competition.setStartDate(past);
    	competition.setEndDate(past);
    	competition.setFundersPanelDate(past);
    	competition.setFundersPanelEndDate(currentDate);
    	competition.setAssessorFeedbackDate(future);

        assertEquals(CompetitionStatus.ASSESSOR_FEEDBACK, competition.getCompetitionStatus());
    }
    
    @Test
    public void competitionStatusAssessorFeedbackIfFundersPanelEndDateInPastAndAssessorFeedbackDateNotSet(){
    	competition.setStartDate(past);
    	competition.setEndDate(past);
    	competition.setFundersPanelDate(past);
    	competition.setFundersPanelEndDate(past);

        assertEquals(CompetitionStatus.ASSESSOR_FEEDBACK, competition.getCompetitionStatus());
    }
    
    @Test
    public void competitionStatusAssessorFeedbackIfFundersPanelEndDateMetAndAssessorFeedbackDateNotSet(){
    	competition.setStartDate(past);
    	competition.setEndDate(past);
    	competition.setFundersPanelDate(past);
    	competition.setFundersPanelEndDate(currentDate);
    	competition.setAssessorFeedbackDate(future);

        assertEquals(CompetitionStatus.ASSESSOR_FEEDBACK, competition.getCompetitionStatus());
    }
    
    @Test
    public void competitionStatusProjectSetupIfAssessorFeedbackDateInPast(){
    	competition.setStartDate(past);
    	competition.setEndDate(past);
        competition.setFundersPanelDate(past);
    	competition.setFundersPanelEndDate(past);
    	competition.setAssessorFeedbackDate(past);

        assertEquals(CompetitionStatus.PROJECT_SETUP, competition.getCompetitionStatus());
    }
    
    @Test
    public void competitionStatusProjectSetupIfAssessorFeedbackDateMet(){
    	competition.setStartDate(past);
    	competition.setEndDate(past);
        competition.setFundersPanelDate(past);
    	competition.setFundersPanelEndDate(past);
    	competition.setAssessorFeedbackDate(currentDate);

        assertEquals(CompetitionStatus.PROJECT_SETUP, competition.getCompetitionStatus());
    }

    /**
     * By default the competition status of a new competition should be COMPETITION_SETUP. When this state is finished, the status is changed to
     * COMPETITION_SETUP_FINISHED, then the other statusses are used.
     */
    @Test
    public void competitionStatusProjectSetupForNewCompetition(){
        Competition newCompetition = new Competition();
        assertEquals(CompetitionStatus.COMPETITION_SETUP, newCompetition.getCompetitionStatus());
    }
    
}
