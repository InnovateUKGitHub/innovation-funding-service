package com.worth.ifs.competitionsetup.service.formpopulator.application;

import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.competition.resource.CompetitionSetupSubsection;
import com.worth.ifs.competitionsetup.form.CompetitionSetupForm;
import com.worth.ifs.competitionsetup.form.application.ApplicationDetailsForm;
import org.junit.Before;
import org.junit.Test;

import java.util.Optional;

import static com.worth.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ApplicationDetailsFormPopulatorTest {

	private ApplicationDetailsFormPopulator service;
	
	@Before
	public void setUp() {
		service = new ApplicationDetailsFormPopulator();
	}
	@Test
	public void testSectionToFill() {
		CompetitionSetupSubsection result = service.sectionToFill();
		assertEquals(CompetitionSetupSubsection.APPLICATION_DETAILS, result);
	}
				
	@Test
	public void testGetSectionFormDataInitialDetails() {

		boolean isResubmissionQuestion = true;
		CompetitionResource competition = newCompetitionResource()
				.withUseResubmissionQuestion(isResubmissionQuestion)
				.build();

		CompetitionSetupForm result = service.populateForm(competition, Optional.empty());
		
		assertTrue(result instanceof ApplicationDetailsForm);
		ApplicationDetailsForm form = (ApplicationDetailsForm) result;
		assertEquals(isResubmissionQuestion, form.isUseResubmissionQuestion());
	}
}
