package com.worth.ifs.service.competitionsetup.sectionupdaters;

import com.worth.ifs.application.service.CompetitionService;
import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.controller.form.competitionsetup.AdditionalInfoForm;
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
		AdditionalInfoForm competitionSetupForm = new AdditionalInfoForm("Activity", "Innovate", "Funder", "Funder Budget");

		CompetitionResource competition = newCompetitionResource()
				.withId(1L).build();

		service.saveSection(competition, competitionSetupForm);
		
		assertEquals("Activity", competition.getActivityCode());
		assertEquals("Innovate", competition.getInnovateBudget());
		assertEquals("Funder", competition.getCoFunders());
		assertEquals("Funder Budget", competition.getCoFundersBudget());

		verify(competitionService).update(competition);
	}
}
