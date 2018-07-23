package org.innovateuk.ifs.competitionsetup.assessor.sectionupdater;

import org.innovateuk.ifs.competition.resource.AssessorCountOptionResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.AssessorCountOptionsRestService;
import org.innovateuk.ifs.competition.service.CompetitionSetupRestService;
import org.innovateuk.ifs.competitionsetup.assessor.form.AssessorsForm;
import org.innovateuk.ifs.competitionsetup.core.form.CompetitionSetupForm;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;

import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.builder.AssessorCountOptionResourceBuilder.newAssessorCountOptionResource;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.competition.resource.AssessorFinanceView.OVERVIEW;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class AssessorSectionSaverTest {

	@InjectMocks
	private AssessorsSectionUpdater saver;
	
	@Mock
	private AssessorCountOptionsRestService assessorCountOptionsRestService;

	@Mock
	private CompetitionSetupRestService competitionSetupRestService;

	@Test
	public void testSaveSection() {
		AssessorsForm competitionSetupForm = new AssessorsForm();
		competitionSetupForm.setAssessorCount(1);
		competitionSetupForm.setAssessorPay(BigDecimal.TEN);
		competitionSetupForm.setHasAssessmentPanel(Boolean.FALSE);
		competitionSetupForm.setHasInterviewStage(Boolean.FALSE);
		competitionSetupForm.setAssessorFinanceView(OVERVIEW);

		CompetitionResource competition = newCompetitionResource()
				.withId(1L).build();

        List<AssessorCountOptionResource> assessorCounts = newAssessorCountOptionResource()
                .withAssessorOptionName("1", "3", "5")
                .withAssessorOptionValue(1, 3, 5)
                .build(3);

        when(assessorCountOptionsRestService.findAllByCompetitionType(competition.getCompetitionType()))
				.thenReturn(restSuccess(assessorCounts));
        when(competitionSetupRestService.update(competition)).thenReturn(restSuccess());

        saver.saveSection(competition, competitionSetupForm);

		assertEquals(Integer.valueOf(1), competition.getAssessorCount());
		assertEquals(BigDecimal.TEN, competition.getAssessorPay());
		assertEquals(Boolean.FALSE, competition.isHasAssessmentPanel());
		assertEquals(Boolean.FALSE, competition.isHasInterviewStage());
		assertEquals(OVERVIEW, competition.getAssessorFinanceView());

        verify(assessorCountOptionsRestService).findAllByCompetitionType(competition.getCompetitionType());
		verify(competitionSetupRestService).update(competition);
	}

	@Test
	public void testsSupportsForm() {
		assertTrue(saver.supportsForm(AssessorsForm.class));
		assertFalse(saver.supportsForm(CompetitionSetupForm.class));
	}

	@Test
	public void testOnlyAssessorCountIsUpdatedAfterSetupAndLive() {
		Integer newAssessorCount = 5;
		BigDecimal newAssessorPay = new BigDecimal("10000");

		Integer oldAssessorCount = 3;
		BigDecimal oldAssessorPay = new BigDecimal("15000");

		ZonedDateTime yesterday = ZonedDateTime.now().minusDays(1);
		ZonedDateTime tomorrow = ZonedDateTime.now().plusDays(1);

		AssessorsForm assessorsForm = new AssessorsForm();
		assessorsForm.setAssessorCount(newAssessorCount);
		assessorsForm.setAssessorPay(newAssessorPay);

		CompetitionResource competition = newCompetitionResource()
				.withId(1L)
				.withAssessorCount(oldAssessorCount)
				.withAssessorPay(oldAssessorPay)
				.withSetupComplete(true)
				.withStartDate(yesterday)
				.withFundersPanelDate(tomorrow)
				.build();

        List<AssessorCountOptionResource> assessorCounts = newAssessorCountOptionResource()
                .withAssessorOptionName("1", "3", "5")
                .withAssessorOptionValue(1, 3, 5)
                .build(3);

		when(assessorCountOptionsRestService.findAllByCompetitionType(competition.getCompetitionType()))
				.thenReturn(restSuccess(assessorCounts));
		when(competitionSetupRestService.update(competition)).thenReturn(restSuccess());

		saver.saveSection(competition, assessorsForm);

		ArgumentCaptor<CompetitionResource> argumentCaptor = ArgumentCaptor.forClass(CompetitionResource.class);
		verify(assessorCountOptionsRestService).findAllByCompetitionType(competition.getCompetitionType());
		verify(competitionSetupRestService).update(argumentCaptor.capture());

		assertEquals(oldAssessorPay, argumentCaptor.getValue().getAssessorPay());
		assertEquals(newAssessorCount, argumentCaptor.getValue().getAssessorCount());

		verify(competitionSetupRestService).update(competition);
	}

	@Test
	public void testUpdateResultsInFailureAfterCompetitionNotificationsWereSent() {

		Integer newAssessorCount = 5;
		BigDecimal newAssessorPay = new BigDecimal("10000");

		ZonedDateTime yesterday = ZonedDateTime.now().minusDays(1);

		AssessorsForm assessorsForm = new AssessorsForm();
		assessorsForm.setAssessorCount(newAssessorCount);
		assessorsForm.setAssessorPay(newAssessorPay);

		CompetitionResource competition = newCompetitionResource()
				.withId(1L)
				.withPafCode()
				.withSetupComplete(true)
				.withStartDate(yesterday)
				.withFundersPanelDate(yesterday)
				.build();

		assertTrue(saver.saveSection(competition, assessorsForm).isFailure());

		verify(competitionSetupRestService, never()).update(competition);
	}
}
