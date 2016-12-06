package com.worth.ifs.invite.resource;

import com.worth.ifs.competition.resource.CompetitionStatus;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;

import static com.worth.ifs.invite.builder.CompetitionParticipantResourceBuilder.newCompetitionParticipantResource;
import static org.junit.Assert.*;

public class CompetitionParticipantResourceTest {

    private CompetitionParticipantResource competitionParticipant;
    private CompetitionParticipantResource competitionParticipantEndingToday;
    private CompetitionParticipantResource competitionParticipantEndingTommorrow;
    private CompetitionParticipantResource competitionParticipantEndedYesterday;
    private CompetitionParticipantResource competitionParticipantStartedToday;
    private CompetitionParticipantResource competitionParticipantStartingTommorrow;

    private CompetitionParticipantResource competitionParticipantCompetitionSetup;
    private CompetitionParticipantResource competitionParticipantReadyToOpen;
    private CompetitionParticipantResource competitionParticipantOpen;
    private CompetitionParticipantResource competitionParticipantClosed;
    private CompetitionParticipantResource competitionParticipantInAssessment;
    private CompetitionParticipantResource competitionParticipantAssessmentClosed;
    private CompetitionParticipantResource competitionParticipantAssessorFeedback;
    private CompetitionParticipantResource competitionParticipantProjectSetup;

    @Before
    public void setUp() throws Exception {
        competitionParticipant = newCompetitionParticipantResource()
                .withCompetitionStatus(CompetitionStatus.CLOSED)
                .withAssessorAcceptsDate(LocalDate.now().atStartOfDay().minusDays(2))
                .withAssessorDeadlineDate(LocalDate.now().atStartOfDay().plusDays(4))
                .build();
        competitionParticipantEndingToday = newCompetitionParticipantResource()
                .withCompetitionStatus(CompetitionStatus.CLOSED)
                .withAssessorAcceptsDate(LocalDate.now().atStartOfDay().minusDays(2))
                .withAssessorDeadlineDate(LocalDate.now().atStartOfDay())
                .build();
        competitionParticipantEndingTommorrow = newCompetitionParticipantResource()
                .withCompetitionStatus(CompetitionStatus.CLOSED)
                .withAssessorAcceptsDate(LocalDate.now().atStartOfDay().minusDays(2))
                .withAssessorDeadlineDate(LocalDate.now().atStartOfDay().plusDays(1))
                .build();
        competitionParticipantEndedYesterday = newCompetitionParticipantResource()
                .withCompetitionStatus(CompetitionStatus.CLOSED)
                .withAssessorAcceptsDate(LocalDate.now().atStartOfDay().minusDays(2))
                .withAssessorDeadlineDate(LocalDate.now().atStartOfDay().minusDays(1))
                .build();
        competitionParticipantStartedToday = newCompetitionParticipantResource()
                .withCompetitionStatus(CompetitionStatus.CLOSED)
                .withAssessorAcceptsDate(LocalDate.now().atStartOfDay())
                .withAssessorDeadlineDate(LocalDate.now().atStartOfDay().plusDays(6))
                .build();
        competitionParticipantStartingTommorrow = newCompetitionParticipantResource()
                .withCompetitionStatus(CompetitionStatus.CLOSED)
                .withAssessorAcceptsDate(LocalDate.now().atStartOfDay().plusDays(1))
                .withAssessorDeadlineDate(LocalDate.now().atStartOfDay().plusDays(7))
                .build();

        competitionParticipantCompetitionSetup = newCompetitionParticipantResource().withCompetitionStatus(CompetitionStatus.COMPETITION_SETUP).build();
        competitionParticipantReadyToOpen = newCompetitionParticipantResource().withCompetitionStatus(CompetitionStatus.READY_TO_OPEN).build();
        competitionParticipantOpen = newCompetitionParticipantResource().withCompetitionStatus(CompetitionStatus.OPEN).build();
        competitionParticipantClosed = newCompetitionParticipantResource().withCompetitionStatus(CompetitionStatus.CLOSED).build();
        competitionParticipantInAssessment = newCompetitionParticipantResource().withCompetitionStatus(CompetitionStatus.IN_ASSESSMENT).build();
        competitionParticipantAssessmentClosed = newCompetitionParticipantResource().withCompetitionStatus(CompetitionStatus.FUNDERS_PANEL).build();
        competitionParticipantAssessorFeedback = newCompetitionParticipantResource().withCompetitionStatus(CompetitionStatus.ASSESSOR_FEEDBACK).build();
        competitionParticipantProjectSetup = newCompetitionParticipantResource().withCompetitionStatus(CompetitionStatus.PROJECT_SETUP).build();
    }

    @Test
    public void getAssessmentDaysLeft() throws Exception {
        assertEquals(3, competitionParticipant.getAssessmentDaysLeft());
        assertEquals(0, competitionParticipantEndingToday.getAssessmentDaysLeft());
        assertEquals(0, competitionParticipantEndingTommorrow.getAssessmentDaysLeft());
        assertEquals(-1, competitionParticipantEndedYesterday.getAssessmentDaysLeft());
        assertEquals(5, competitionParticipantStartedToday.getAssessmentDaysLeft());
        assertEquals(6, competitionParticipantStartingTommorrow.getAssessmentDaysLeft());
    }

    @Test
    public void getAssessmentDaysLeftPercentage() throws Exception {
        assertEquals(50, competitionParticipant.getAssessmentDaysLeftPercentage());
        assertEquals(100, competitionParticipantEndingToday.getAssessmentDaysLeftPercentage());
        assertEquals(100, competitionParticipantEndingTommorrow.getAssessmentDaysLeftPercentage());
        assertEquals(100, competitionParticipantEndedYesterday.getAssessmentDaysLeftPercentage());
        assertEquals(16, competitionParticipantStartedToday.getAssessmentDaysLeftPercentage());
        assertEquals(0, competitionParticipantStartingTommorrow.getAssessmentDaysLeftPercentage());
    }

    @Test
    public void isInAssessment() throws Exception {
        assertFalse(competitionParticipantCompetitionSetup.isInAssessment());
        assertFalse(competitionParticipantReadyToOpen.isInAssessment());
        assertFalse(competitionParticipantOpen.isInAssessment());
        assertFalse(competitionParticipantAssessmentClosed.isAnUpcomingAssessment());
        assertTrue(competitionParticipantInAssessment.isInAssessment());
        assertFalse(competitionParticipantAssessmentClosed.isInAssessment());
        assertFalse(competitionParticipantAssessorFeedback.isInAssessment());
        assertFalse(competitionParticipantProjectSetup.isInAssessment());
    }

    @Test
    public void isAnUpcomingAssessment() throws Exception {
        assertFalse(competitionParticipantCompetitionSetup.isAnUpcomingAssessment());
        assertTrue(competitionParticipantReadyToOpen.isAnUpcomingAssessment());
        assertTrue(competitionParticipantOpen.isAnUpcomingAssessment());
        assertFalse(competitionParticipantClosed.isInAssessment());
        assertFalse(competitionParticipantInAssessment.isAnUpcomingAssessment());
        assertFalse(competitionParticipantAssessmentClosed.isAnUpcomingAssessment());
        assertFalse(competitionParticipantAssessorFeedback.isAnUpcomingAssessment());
        assertFalse(competitionParticipantProjectSetup.isAnUpcomingAssessment());

    }
}