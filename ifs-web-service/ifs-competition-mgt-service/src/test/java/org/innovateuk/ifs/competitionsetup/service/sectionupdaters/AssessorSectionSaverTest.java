package org.innovateuk.ifs.competitionsetup.service.sectionupdaters;

import org.innovateuk.ifs.application.service.CompetitionService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competitionsetup.form.AssessorsForm;
import org.innovateuk.ifs.competitionsetup.form.CompetitionSetupForm;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.junit.Assert.*;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class AssessorSectionSaverTest {

	@InjectMocks
	private AssessorsSectionSaver saver;
	
	@Mock
	private CompetitionService competitionService;
	
	@Test
	public void testSaveSection() {
		AssessorsForm competitionSetupForm = new AssessorsForm();
		competitionSetupForm.setAssessorCount(1);
		competitionSetupForm.setAssessorPay(BigDecimal.TEN);

		CompetitionResource competition = newCompetitionResource()
				.withId(1L).build();

		saver.saveSection(competition, competitionSetupForm);

		assertEquals(Integer.valueOf(1), competition.getAssessorCount());
		assertEquals(BigDecimal.TEN, competition.getAssessorPay());

		verify(competitionService).update(competition);
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

		saver.saveSection(competition, assessorsForm);

		ArgumentCaptor<CompetitionResource> argumentCaptor = ArgumentCaptor.forClass(CompetitionResource.class);
		verify(competitionService).update(argumentCaptor.capture());

		assertEquals(oldAssessorPay, argumentCaptor.getValue().getAssessorPay());
		assertEquals(newAssessorCount, argumentCaptor.getValue().getAssessorCount());

		verify(competitionService).update(competition);
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

		verify(competitionService, never()).update(competition);
	}
}
