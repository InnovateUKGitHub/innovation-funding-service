package com.worth.ifs.competitionsetup.service.sectionupdaters;

import com.worth.ifs.application.service.CompetitionService;
import com.worth.ifs.commons.error.Error;
import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.competitionsetup.form.AssessorsForm;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.List;

import static com.worth.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

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
	public void testAutoSaveAssessorCount() {
		CompetitionResource competition = newCompetitionResource().build();
		List<Error> errors = saver.autoSaveSectionField(competition, "assessorCount", "1", null);
		assertTrue(errors.isEmpty());
		verify(competitionService).update(competition);
	}

	@Test
	public void testAutoSaveAssessorPay() {
		CompetitionResource competition = newCompetitionResource().build();
		List<Error> errors = saver.autoSaveSectionField(competition, "assessorPay", "10", null);
		assertTrue(errors.isEmpty());
		verify(competitionService).update(competition);
	}

	@Test
	public void testAutoSaveAssessorPayInvalid_String() {
		CompetitionResource competition = newCompetitionResource().build();
		List<Error> errors = saver.autoSaveSectionField(competition, "assessorPay", "TEN", null);
		assertEquals(1, errors.size());
		assertEquals("validation.assessorsform.assessorPay.only.numbers", errors.get(0).getErrorKey());
		verifyZeroInteractions(competitionService);
	}

	@Test
	public void testAutoSaveAssessorPayInvalid_Null() {
		CompetitionResource competition = newCompetitionResource().build();
		List<Error> errors = saver.autoSaveSectionField(competition, "assessorPay", null, null);
		assertEquals(1, errors.size());
		assertEquals("validation.assessorsform.assessorPay.required", errors.get(0).getErrorKey());
		verifyZeroInteractions(competitionService);
	}

	@Test
	public void testAutoSaveAssessorPayInvalid_Range() {
		CompetitionResource competition = newCompetitionResource().build();
		List<Error> errors = saver.autoSaveSectionField(competition, "assessorPay", "9999999999999", null);
		assertEquals(1, errors.size());
		assertEquals("validation.assessorsform.assessorPay.max.amount.invalid", errors.get(0).getErrorKey());
		verifyZeroInteractions(competitionService);
	}

	@Test
	public void testAutoSaveAssessorPayInvalid_FieldName() {
		CompetitionResource competition = newCompetitionResource().build();
		List<Error> errors = saver.autoSaveSectionField(competition, "assessorPay_invalid", "10", null);
		assertEquals(1, errors.size());
		assertEquals("Field not found", errors.get(0).getErrorKey());
		verifyZeroInteractions(competitionService);
	}

	@Test
	public void testAutoSaveAssessorCountInvalid_Integer() {
		CompetitionResource competition = newCompetitionResource().build();
		List<Error> errors = saver.autoSaveSectionField(competition, "assessorCount", "Invalid_int", null);
		assertEquals(1, errors.size());
		assertEquals("validation.standard.only.numbers", errors.get(0).getErrorKey());
		verifyZeroInteractions(competitionService);
	}

}
