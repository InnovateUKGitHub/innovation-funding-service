package com.worth.ifs.competitionsetup.service.sectionupdaters;

import com.worth.ifs.application.service.CompetitionService;
import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.competitionsetup.form.AdditionalInfoForm;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static com.worth.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class AdditionalInfoSectionSaverTest {

	@InjectMocks
	private AdditionalInfoSectionSaver service;
	
	@Mock
	private CompetitionService competitionService;
	
	@Test
	public void testSaveCompetitionSetupSection() {
		AdditionalInfoForm competitionSetupForm = new AdditionalInfoForm("Activity", "Innovate", "Funder", 0.0);

		CompetitionResource competition = newCompetitionResource()
				.withId(1L).build();

		service.saveSection(competition, competitionSetupForm);

		assertEquals("Activity", competition.getActivityCode());
		assertEquals("Innovate", competition.getInnovateBudget());
		assertEquals("Funder", competition.getFunder());
		assertEquals(Double.valueOf(0), competition.getFunderBudget());

		verify(competitionService).update(competition);
	}
}
