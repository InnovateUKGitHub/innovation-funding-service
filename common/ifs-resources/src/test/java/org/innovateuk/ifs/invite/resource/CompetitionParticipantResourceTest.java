package org.innovateuk.ifs.invite.resource;

import org.innovateuk.ifs.competition.resource.CompetitionStatus;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.time.ZoneId;

import static org.innovateuk.ifs.invite.builder.CompetitionParticipantResourceBuilder.newCompetitionParticipantResource;
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
                .withAssessorAcceptsDate(LocalDate.now().atStartOfDay().minusDays(2).atZone(ZoneId.systemDefault()))
                .withAssessorDeadlineDate(LocalDate.now().atStartOfDay().plusDays(4).atZone(ZoneId.systemDefault()))
                .build();
        competitionParticipantEndingToday = newCompetitionParticipantResource()
                .withCompetitionStatus(CompetitionStatus.CLOSED)
                .withAssessorAcceptsDate(LocalDate.now().atStartOfDay().minusDays(2).atZone(ZoneId.systemDefault()))
                .withAssessorDeadlineDate(LocalDate.now().atStartOfDay().atZone(ZoneId.systemDefault()))
                .build();
        competitionParticipantEndingTommorrow = newCompetitionParticipantResource()
                .withCompetitionStatus(CompetitionStatus.CLOSED)
                .withAssessorAcceptsDate(LocalDate.now().atStartOfDay().minusDays(2).atZone(ZoneId.systemDefault()))
                .withAssessorDeadlineDate(LocalDate.now().atStartOfDay().plusDays(1).atZone(ZoneId.systemDefault()))
                .build();
        competitionParticipantEndedYesterday = newCompetitionParticipantResource()
                .withCompetitionStatus(CompetitionStatus.CLOSED)
                .withAssessorAcceptsDate(LocalDate.now().atStartOfDay().minusDays(2).atZone(ZoneId.systemDefault()))
                .withAssessorDeadlineDate(LocalDate.now().atStartOfDay().minusDays(1).atZone(ZoneId.systemDefault()))
                .build();
        competitionParticipantStartedToday = newCompetitionParticipantResource()
                .withCompetitionStatus(CompetitionStatus.CLOSED)
                .withAssessorAcceptsDate(LocalDate.now().atStartOfDay().atZone(ZoneId.systemDefault()))
                .withAssessorDeadlineDate(LocalDate.now().atStartOfDay().plusDays(6).atZone(ZoneId.systemDefault()))
                .build();
        competitionParticipantStartingTommorrow = newCompetitionParticipantResource()
                .withCompetitionStatus(CompetitionStatus.CLOSED)
                .withAssessorAcceptsDate(LocalDate.now().atStartOfDay().plusDays(1).atZone(ZoneId.systemDefault()))
                .withAssessorDeadlineDate(LocalDate.now().atStartOfDay().plusDays(7).atZone(ZoneId.systemDefault()))
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
