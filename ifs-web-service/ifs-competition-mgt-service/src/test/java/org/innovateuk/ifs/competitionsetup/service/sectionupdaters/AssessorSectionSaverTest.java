package org.innovateuk.ifs.competitionsetup.service.sectionupdaters;

import org.innovateuk.ifs.application.service.CompetitionService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competitionsetup.form.AssessorsForm;
import org.innovateuk.ifs.competitionsetup.form.CompetitionSetupForm;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;

import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.junit.Assert.*;
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
}
