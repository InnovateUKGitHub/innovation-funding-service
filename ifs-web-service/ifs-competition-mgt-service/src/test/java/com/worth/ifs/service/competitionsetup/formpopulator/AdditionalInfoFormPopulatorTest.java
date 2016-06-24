package com.worth.ifs.service.competitionsetup.formpopulator;

import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.controller.form.competitionsetup.AdditionalInfoForm;
import com.worth.ifs.controller.form.competitionsetup.CompetitionSetupForm;
import org.junit.Before;
import org.junit.Test;

import static com.worth.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class AdditionalInfoFormPopulatorTest {

	private AdditionalInfoFormPopulator service;
	
	@Before
	public void setUp() {
		service = new AdditionalInfoFormPopulator();
	}
	
	@Test
	public void testGetSectionFormDataAdditionalInfo() {
		CompetitionResource competition = newCompetitionResource()
				.withActivityCode("Activity Code")
				.withInnovateBudget("Innovate Budget")
				.withCoFunders("Funders")
				.withCoFundersBudget("Funders budget")
				.withId(8L).build();

		CompetitionSetupForm result = service.populateForm(competition);
		
		assertTrue(result instanceof AdditionalInfoForm);
        AdditionalInfoForm form = (AdditionalInfoForm) result;
		assertEquals("Activity Code", form.getActivityCode());
		assertEquals("Innovate Budget", form.getInnovateBudget());
		assertEquals("Funders", form.getCoFunders());
		assertEquals("Funders budget", form.getCoFundersBudget());
	}
}
