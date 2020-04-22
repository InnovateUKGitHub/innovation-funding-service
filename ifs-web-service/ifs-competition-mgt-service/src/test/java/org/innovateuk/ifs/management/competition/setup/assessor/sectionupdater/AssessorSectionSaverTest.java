package org.innovateuk.ifs.management.competition.setup.assessor.sectionupdater;

import org.innovateuk.ifs.competition.resource.AssessorCountOptionResource;
import org.innovateuk.ifs.competition.resource.AssessorFinanceView;
import org.innovateuk.ifs.competition.resource.CompetitionAssessmentConfigResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.AssessorCountOptionsRestService;
import org.innovateuk.ifs.competition.service.CompetitionAssessmentConfigRestService;
import org.innovateuk.ifs.competition.service.CompetitionSetupRestService;
import org.innovateuk.ifs.management.competition.setup.assessor.form.AssessorsForm;
import org.innovateuk.ifs.management.competition.setup.core.form.CompetitionSetupForm;
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
import static org.innovateuk.ifs.competition.builder.CompetitionAssessmentConfigResourceBuilder.newCompetitionAssessmentConfigResource;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.competition.resource.AssessorFinanceView.OVERVIEW;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.Silent.class)
public class AssessorSectionSaverTest {

	@InjectMocks
	private AssessorsSectionUpdater saver;
	
	@Mock
	private AssessorCountOptionsRestService assessorCountOptionsRestService;

	@Mock
	private CompetitionSetupRestService competitionSetupRestService;

	@Mock
	private CompetitionAssessmentConfigRestService competitionAssessmentConfigRestService;

	@Test
	public void testSaveSection() {
		AssessorsForm competitionSetupForm = new AssessorsForm();
		competitionSetupForm.setAssessorCount(1);
		competitionSetupForm.setAssessorPay(BigDecimal.TEN);
		competitionSetupForm.setHasAssessmentPanel(Boolean.FALSE);
		competitionSetupForm.setHasInterviewStage(Boolean.FALSE);
		competitionSetupForm.setAssessorFinanceView(OVERVIEW);
		competitionSetupForm.setAverageAssessorScore(Boolean.FALSE);

		CompetitionResource competition = newCompetitionResource()
				.withId(1L).build();

		CompetitionAssessmentConfigResource competitionAssessmentConfigResource = newCompetitionAssessmentConfigResource()
				.withAverageAssessorScore(false)
				.withAssessorCount(3)
				.withAssessorPay(BigDecimal.valueOf(10))
				.withHasAssessmentPanel(false)
				.withHasInterviewStage(false)
				.withAssessorFinanceView(AssessorFinanceView.OVERVIEW)
				.build();

        List<AssessorCountOptionResource> assessorCounts = newAssessorCountOptionResource()
                .withAssessorOptionName("1", "3", "5")
                .withAssessorOptionValue(1, 3, 5)
                .build(3);

		when(assessorCountOptionsRestService.findAllByCompetitionType(competition.getCompetitionType()))
				.thenReturn(restSuccess(assessorCounts));
        when(competitionAssessmentConfigRestService.findOneByCompetitionId(competition.getId())).thenReturn(restSuccess(competitionAssessmentConfigResource));
        when(competitionAssessmentConfigRestService.update(competition.getId(), competitionAssessmentConfigResource)).thenReturn(restSuccess(competitionAssessmentConfigResource));

        saver.saveSection(competition, competitionSetupForm);

		assertEquals(Integer.valueOf(1), competitionAssessmentConfigResource.getAssessorCount());
		assertEquals(BigDecimal.TEN, competitionAssessmentConfigResource.getAssessorPay());
		assertEquals(Boolean.FALSE, competitionAssessmentConfigResource.getHasAssessmentPanel());
		assertEquals(Boolean.FALSE, competitionAssessmentConfigResource.getHasInterviewStage());
		assertEquals(OVERVIEW, competitionAssessmentConfigResource.getAssessorFinanceView());

        verify(assessorCountOptionsRestService).findAllByCompetitionType(competition.getCompetitionType());
		verify(competitionAssessmentConfigRestService).update(competition.getId(), competitionAssessmentConfigResource);
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
		BigDecimal oldAssessorPay = new BigDecimal("15000");

		ZonedDateTime yesterday = ZonedDateTime.now().minusDays(1);
		ZonedDateTime tomorrow = ZonedDateTime.now().plusDays(1);

		AssessorsForm assessorsForm = new AssessorsForm();
		assessorsForm.setAssessorCount(newAssessorCount);
		assessorsForm.setAssessorPay(newAssessorPay);
		assessorsForm.setAverageAssessorScore(Boolean.FALSE);
		assessorsForm.setHasInterviewStage(Boolean.FALSE);
		assessorsForm.setAssessorFinanceView(OVERVIEW);
		assessorsForm.setHasAssessmentPanel(Boolean.FALSE);

		CompetitionResource competition = newCompetitionResource()
				.withId(1L)
				.withSetupComplete(true)
				.withStartDate(yesterday)
				.withFundersPanelDate(tomorrow)
				.withCompetitionType(1L)
				.build();

		CompetitionAssessmentConfigResource competitionAssessmentConfigResource = newCompetitionAssessmentConfigResource()
				.withAverageAssessorScore(false)
				.withAssessorCount(newAssessorCount)
				.withAssessorPay(BigDecimal.valueOf(15000))
				.withHasAssessmentPanel(false)
				.withHasInterviewStage(false)
				.withAssessorFinanceView(AssessorFinanceView.OVERVIEW)
				.build();

		List<AssessorCountOptionResource> assessorCounts = newAssessorCountOptionResource()
				.withCompetitionType(competition.getCompetitionType())
                .withAssessorOptionName("1", "3", "5")
                .withAssessorOptionValue(1, 3, 5)
                .build(3);

		when(competitionAssessmentConfigRestService.findOneByCompetitionId(competition.getId())).thenReturn(restSuccess(competitionAssessmentConfigResource));

		when(assessorCountOptionsRestService.findAllByCompetitionType(competition.getCompetitionType()))
				.thenReturn(restSuccess(assessorCounts));
		when(competitionAssessmentConfigRestService.update(competition.getId(), competitionAssessmentConfigResource)).thenReturn(restSuccess(competitionAssessmentConfigResource));

		saver.saveSection(competition, assessorsForm);

		ArgumentCaptor<CompetitionAssessmentConfigResource> argumentCaptor = ArgumentCaptor.forClass(CompetitionAssessmentConfigResource.class);
		verify(assessorCountOptionsRestService).findAllByCompetitionType(competition.getCompetitionType());
		verify(competitionAssessmentConfigRestService).findOneByCompetitionId(competition.getId());
		verify(competitionAssessmentConfigRestService).update(anyLong(), argumentCaptor.capture());
		assertEquals(newAssessorCount, argumentCaptor.getValue().getAssessorCount());

		assertEquals(oldAssessorPay, argumentCaptor.getValue().getAssessorPay());
		assertEquals(newAssessorCount, argumentCaptor.getValue().getAssessorCount());
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
