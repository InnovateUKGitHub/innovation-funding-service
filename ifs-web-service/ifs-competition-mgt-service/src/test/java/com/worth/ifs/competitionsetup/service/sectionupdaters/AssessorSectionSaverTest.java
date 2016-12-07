package com.worth.ifs.competitionsetup.service.sectionupdaters;

import com.worth.ifs.application.service.CompetitionService;
import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.competitionsetup.form.AssessorsForm;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;

import static com.worth.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
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

		saver.saveSection(competition, competitionSetupForm, false);

		assertEquals(Integer.valueOf(1), competition.getAssessorCount());
		assertEquals(BigDecimal.TEN, competition.getAssessorPay());

		verify(competitionService).update(competition);
	}
}
