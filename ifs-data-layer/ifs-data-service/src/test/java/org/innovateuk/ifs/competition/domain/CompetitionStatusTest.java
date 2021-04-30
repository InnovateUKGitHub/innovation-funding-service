package org.innovateuk.ifs.competition.domain;

import org.innovateuk.ifs.assessment.period.domain.AssessmentPeriod;
import org.innovateuk.ifs.competition.resource.CompetitionCompletionStage;
import org.innovateuk.ifs.competition.resource.CompetitionStatus;
import org.junit.Before;
import org.junit.Test;

import java.time.ZonedDateTime;

import static com.google.common.collect.Lists.newArrayList;
import static org.innovateuk.ifs.competition.builder.AssessmentPeriodBuilder.newAssessmentPeriod;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.competition.builder.CompetitionTypeBuilder.newCompetitionType;
import static org.innovateuk.ifs.competition.resource.CompetitionCompletionStage.PROJECT_SETUP;
import static org.innovateuk.ifs.competition.resource.CompetitionCompletionStage.RELEASE_FEEDBACK;
import static org.junit.Assert.*;

public class CompetitionStatusTest {

	private Competition competition;
    
    private ZonedDateTime currentDate;
    private ZonedDateTime future;
    private ZonedDateTime past;
    private CompetitionType competitionType;
    private AssessmentPeriod assessmentPeriod;
	
    @Before
    public void setUp() {
    	currentDate = ZonedDateTime.now();
    	future = currentDate.plusMinutes(1);
    	past = currentDate.minusMinutes(1);
    	assessmentPeriod = newAssessmentPeriod().build();
        competitionType = newCompetitionType()
                .withName("Sector")
                .build();
    	competition = new Competition();
        competition.setSetupComplete(true);
        competition.setCompetitionType(competitionType);
        competition.setAssessmentPeriods(newArrayList(assessmentPeriod));
    }

    @Test
    public void competitionInSetup(){
        competition.setSetupComplete(false);

        assertEquals(CompetitionStatus.COMPETITION_SETUP, competition.getCompetitionStatus());
        assertFalse(competition.getCompetitionStatus().isFeedbackReleased());
    }

    @Test
    public void competitionStatusReadyToOpen(){
    	competition.setStartDate(future);
        
        assertEquals(CompetitionStatus.READY_TO_OPEN, competition.getCompetitionStatus());
        assertFalse(competition.getCompetitionStatus().isFeedbackReleased());
    }

    @Test
    public void competitionStatusOpenIfStartDateInPastAndEndDateInFuture(){
    	competition.setStartDate(past);
    	competition.setEndDate(future);

        assertEquals(CompetitionStatus.OPEN, competition.getCompetitionStatus());
        assertFalse(competition.getCompetitionStatus().isFeedbackReleased());
    }
    
    @Test
    public void competitionStatusOpenIfStartDateMetAndEndDateInFuture(){
    	competition.setStartDate(currentDate);
    	competition.setEndDate(future);

        assertEquals(CompetitionStatus.OPEN, competition.getCompetitionStatus());
        assertFalse(competition.getCompetitionStatus().isFeedbackReleased());
    }
    
    @Test
    public void competitionStatusInAssessmentIfEndDateInPastAndAssessmentEndDateInFuture(){
    	competition.setStartDate(past);
    	competition.setEndDate(past);
        competition.notifyAssessors(past, assessmentPeriod);
    	competition.setFundersPanelDate(future);

        assertEquals(CompetitionStatus.IN_ASSESSMENT, competition.getCompetitionStatus());
        assertFalse(competition.getCompetitionStatus().isFeedbackReleased());
    }
    
    @Test
    public void competitionStatusInAssessmentIfEndDateMetAndAssessmentEndDateInFuture(){
    	competition.setStartDate(past);
    	competition.setEndDate(currentDate);
        competition.notifyAssessors(past, assessmentPeriod);
    	competition.setFundersPanelDate(future);

        assertEquals(CompetitionStatus.IN_ASSESSMENT, competition.getCompetitionStatus());
        assertFalse(competition.getCompetitionStatus().isFeedbackReleased());
    }
    
    @Test
    public void competitionStatusFundersPanelIfFundersPanelDateInPastAndFundersPanelEndDateInFuture(){
    	competition.setStartDate(past);
    	competition.setEndDate(past);
        competition.notifyAssessors(past, assessmentPeriod);
        competition.closeAssessment(past, assessmentPeriod);
    	competition.setFundersPanelDate(past);
    	competition.setFundersPanelEndDate(future);

        assertEquals(CompetitionStatus.FUNDERS_PANEL, competition.getCompetitionStatus());
        assertFalse(competition.getCompetitionStatus().isFeedbackReleased());
    }
    
    @Test
    public void competitionStatusFundersPanelIfFundersPanelDateMetAndFundersPanelEndDateInFuture(){
    	competition.setStartDate(past);
    	competition.setEndDate(past);
        competition.notifyAssessors(past, assessmentPeriod);
        competition.closeAssessment(past, assessmentPeriod);
    	competition.setFundersPanelDate(currentDate);
    	competition.setFundersPanelEndDate(future);

        assertEquals(CompetitionStatus.FUNDERS_PANEL, competition.getCompetitionStatus());
        assertFalse(competition.getCompetitionStatus().isFeedbackReleased());
    }
    
    @Test
    public void competitionStatusFundersPanelIfFundersPanelDateInPastAndFundersPanelEndDateNotSet(){
    	competition.setStartDate(past);
    	competition.setEndDate(past);
        competition.notifyAssessors(past, assessmentPeriod);
        competition.closeAssessment(past, assessmentPeriod);
    	competition.setFundersPanelDate(past);

        assertEquals(CompetitionStatus.FUNDERS_PANEL, competition.getCompetitionStatus());
        assertFalse(competition.getCompetitionStatus().isFeedbackReleased());
    }
    
    @Test
    public void competitionStatusFundersPanelIfFundersPanelDateMetAndFundersPanelEndDateNotSet(){
    	competition.setStartDate(past);
    	competition.setEndDate(past);
        competition.notifyAssessors(past, assessmentPeriod);
        competition.closeAssessment(past, assessmentPeriod);
    	competition.setFundersPanelDate(currentDate);

        assertEquals(CompetitionStatus.FUNDERS_PANEL, competition.getCompetitionStatus());
        assertFalse(competition.getCompetitionStatus().isFeedbackReleased());
    }
    
    @Test
    public void competitionStatusAssessorFeedbackIfFundersPanelEndDateInPastAndAssessorFeedbackDateInFuture(){
    	competition.setStartDate(past);
    	competition.setEndDate(past);
    	competition.setFundersPanelDate(past);
    	competition.setFundersPanelEndDate(past);
        competition.notifyAssessors(past, assessmentPeriod);
        competition.closeAssessment(past, assessmentPeriod);
    	competition.setAssessorFeedbackDate(future);

        assertEquals(CompetitionStatus.ASSESSOR_FEEDBACK, competition.getCompetitionStatus());
        assertFalse(competition.getCompetitionStatus().isFeedbackReleased());
    }
    
    @Test
    public void competitionStatusAssessorFeedbackIfFundersPanelEndDateMetAndAssessorFeedbackDateInFuture(){
    	competition.setStartDate(past);
    	competition.setEndDate(past);
    	competition.setFundersPanelDate(past);
        competition.notifyAssessors(past, assessmentPeriod);
        competition.closeAssessment(past, assessmentPeriod);
    	competition.setFundersPanelEndDate(currentDate);
    	competition.setAssessorFeedbackDate(future);

        assertEquals(CompetitionStatus.ASSESSOR_FEEDBACK, competition.getCompetitionStatus());
        assertFalse(competition.getCompetitionStatus().isFeedbackReleased());
    }
    
    @Test
    public void competitionStatusAssessorFeedbackIfFundersPanelEndDateInPastAndAssessorFeedbackDateNotSet(){
    	competition.setStartDate(past);
    	competition.setEndDate(past);
    	competition.setFundersPanelDate(past);
        competition.notifyAssessors(past, assessmentPeriod);
        competition.closeAssessment(past, assessmentPeriod);
    	competition.setFundersPanelEndDate(past);

        assertEquals(CompetitionStatus.ASSESSOR_FEEDBACK, competition.getCompetitionStatus());
        assertFalse(competition.getCompetitionStatus().isFeedbackReleased());
    }
    
    @Test
    public void competitionStatusAssessorFeedbackIfFundersPanelEndDateMetAndFeedbackReleasedDateNotSet(){
    	competition.setStartDate(past);
    	competition.setEndDate(past);
    	competition.setFundersPanelDate(past);
        competition.notifyAssessors(past, assessmentPeriod);
        competition.closeAssessment(past, assessmentPeriod);
    	competition.setFundersPanelEndDate(currentDate);
    	competition.setAssessorFeedbackDate(future);

        assertEquals(CompetitionStatus.ASSESSOR_FEEDBACK, competition.getCompetitionStatus());
        assertFalse(competition.getCompetitionStatus().isFeedbackReleased());
    }
    
    @Test
    public void competitionStatusProjectSetupIfFeedbackReleasedDateInPast(){
    	competition.setStartDate(past);
    	competition.setEndDate(past);
        competition.setFundersPanelDate(past);
        competition.notifyAssessors(past, assessmentPeriod);
        competition.closeAssessment(past, assessmentPeriod);
    	competition.setFundersPanelEndDate(past);
    	competition.setReleaseFeedbackDate(past);
    	competition.releaseFeedback(past);

        assertEquals(CompetitionStatus.PROJECT_SETUP, competition.getCompetitionStatus());
        assertTrue(competition.getCompetitionStatus().isFeedbackReleased());
    }
    
    @Test
    public void competitionStatusPreviousIfFeedbackReleasedDateMetAndReleaseFeedbackCompletionStage(){
        assertCompetitionStatusUponReleasingFeedback(RELEASE_FEEDBACK, CompetitionStatus.PREVIOUS);
    }

    @Test
    public void competitionStatusProjectSetupIfFeedbackReleasedDateMetAndProjectSetupCompletionStage(){
        assertCompetitionStatusUponReleasingFeedback(PROJECT_SETUP, CompetitionStatus.PROJECT_SETUP);
    }

    private void assertCompetitionStatusUponReleasingFeedback(
            CompetitionCompletionStage completionStage,
            CompetitionStatus expectedStatus) {

        Competition competition = newCompetition().
                withCompletionStage(completionStage).
                build();

        competition.setSetupComplete(true);
        competition.setStartDate(past);
        competition.setEndDate(past);
        competition.setFundersPanelDate(past);
        competition.notifyAssessors(past, assessmentPeriod);
        competition.closeAssessment(past, assessmentPeriod);
        competition.setFundersPanelEndDate(past);
        competition.setReleaseFeedbackDate(currentDate);
        competition.releaseFeedback(currentDate);
        competition.setCompetitionType(competitionType);

        assertEquals(expectedStatus, competition.getCompetitionStatus());
        assertTrue(competition.getCompetitionStatus().isFeedbackReleased());
    }

    /**
     * By default the competition status of a new competition should be COMPETITION_SETUP. When this state is finished, the status is changed to
     * COMPETITION_SETUP_FINISHED, then the other statusses are used.
     */
    @Test
    public void competitionStatusProjectSetupForNewCompetition(){
        Competition newCompetition = new Competition();
        assertEquals(CompetitionStatus.COMPETITION_SETUP, newCompetition.getCompetitionStatus());
        assertFalse(competition.getCompetitionStatus().isFeedbackReleased());
    }
    
}
