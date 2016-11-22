package com.worth.ifs.invite.resource;

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

    @Before
    public void setUp() throws Exception {
        competitionParticipant = newCompetitionParticipantResource()
                .withAssessorAcceptsDate(LocalDate.now().atStartOfDay().minusDays(2))
                .withAssessorDeadlineDate(LocalDate.now().atStartOfDay().plusDays(4))
                .build();
        competitionParticipantEndingToday = newCompetitionParticipantResource()
                .withAssessorAcceptsDate(LocalDate.now().atStartOfDay().minusDays(2))
                .withAssessorDeadlineDate(LocalDate.now().atStartOfDay())
                .build();
        competitionParticipantEndingTommorrow = newCompetitionParticipantResource()
                .withAssessorAcceptsDate(LocalDate.now().atStartOfDay().minusDays(2))
                .withAssessorDeadlineDate(LocalDate.now().atStartOfDay().plusDays(1))
                .build();
        competitionParticipantEndedYesterday = newCompetitionParticipantResource()
                .withAssessorAcceptsDate(LocalDate.now().atStartOfDay().minusDays(2))
                .withAssessorDeadlineDate(LocalDate.now().atStartOfDay().minusDays(1))
                .build();
        competitionParticipantStartedToday = newCompetitionParticipantResource()
                .withAssessorAcceptsDate(LocalDate.now().atStartOfDay())
                .withAssessorDeadlineDate(LocalDate.now().atStartOfDay().plusDays(6))
                .build();
        competitionParticipantStartingTommorrow = newCompetitionParticipantResource()
                .withAssessorAcceptsDate(LocalDate.now().atStartOfDay().plusDays(1))
                .withAssessorDeadlineDate(LocalDate.now().atStartOfDay().plusDays(7))
                .build();
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
        assertTrue(competitionParticipant.isInAssessment());
        assertFalse(competitionParticipantEndingToday.isInAssessment());
        assertTrue(competitionParticipantEndingTommorrow.isInAssessment());
        assertFalse(competitionParticipantEndedYesterday.isInAssessment());
        assertTrue(competitionParticipantStartedToday.isInAssessment());
        assertFalse(competitionParticipantStartingTommorrow.isInAssessment());
    }

    @Test
    public void isAnUpcomingAssessment() throws Exception {
        assertFalse(competitionParticipant.isAnUpcomingAssessment());
        assertFalse(competitionParticipantEndingToday.isAnUpcomingAssessment());
        assertFalse(competitionParticipantEndingTommorrow.isAnUpcomingAssessment());
        assertFalse(competitionParticipantEndedYesterday.isAnUpcomingAssessment());
        assertFalse(competitionParticipantStartedToday.isAnUpcomingAssessment());
        assertTrue(competitionParticipantStartingTommorrow.isAnUpcomingAssessment());
    }
}