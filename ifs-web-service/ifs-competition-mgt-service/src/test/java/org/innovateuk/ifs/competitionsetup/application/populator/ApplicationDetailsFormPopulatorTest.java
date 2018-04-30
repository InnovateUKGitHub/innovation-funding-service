package org.innovateuk.ifs.competitionsetup.application.populator;

import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSubsection;
import org.innovateuk.ifs.competitionsetup.core.form.CompetitionSetupForm;
import org.innovateuk.ifs.competitionsetup.application.form.ApplicationDetailsForm;
import org.junit.Before;
import org.junit.Test;

import java.util.Optional;

import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
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
	public void testPopulateFormDataApplicationDetails() {

		boolean isResubmissionQuestion = true;
		CompetitionResource competition = newCompetitionResource()
				.withUseResubmissionQuestion(isResubmissionQuestion)
				.build();

		CompetitionSetupForm result = service.populateForm(competition, Optional.empty());
		
		assertTrue(result instanceof ApplicationDetailsForm);
		ApplicationDetailsForm form = (ApplicationDetailsForm) result;
		assertEquals(isResubmissionQuestion, form.getUseResubmissionQuestion());
	}
}
