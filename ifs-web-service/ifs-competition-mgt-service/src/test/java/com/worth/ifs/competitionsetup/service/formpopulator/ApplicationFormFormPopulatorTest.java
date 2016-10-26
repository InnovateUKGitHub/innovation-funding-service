package com.worth.ifs.competitionsetup.service.formpopulator;

import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.competitionsetup.form.ApplicationFormForm;
import com.worth.ifs.competitionsetup.form.CompetitionSetupForm;
import com.worth.ifs.fixtures.CompetitionFundersFixture;
import org.junit.Before;
import org.junit.Test;

import static com.worth.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ApplicationFormFormPopulatorTest {

	private ApplicationFormFormPopulator service;

	@Before
	public void setUp() {
		service = new ApplicationFormFormPopulator();
	}

	@Test
	public void testGetSectionFormDataApplicationForm() {
		CompetitionResource competition = newCompetitionResource()
				.withActivityCode("Activity Code")
				.withInnovateBudget("Innovate Budget")
				.withCompetitionCode("c123")
				.withPafCode("p123")
				.withBudgetCode("b123")
				.withFunders(CompetitionFundersFixture.getTestCoFunders())
				.withId(8L).build();

		CompetitionSetupForm result = service.populateForm(competition);
		
		assertTrue(result instanceof ApplicationFormForm);
		ApplicationFormForm form = (ApplicationFormForm) result;
		assertEquals(null, form.getQuestion());
	}
}
