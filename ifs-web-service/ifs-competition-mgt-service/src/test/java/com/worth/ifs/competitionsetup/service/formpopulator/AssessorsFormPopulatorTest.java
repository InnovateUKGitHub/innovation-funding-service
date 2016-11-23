package com.worth.ifs.competitionsetup.service.formpopulator;

import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.competitionsetup.form.AssessorsForm;
import com.worth.ifs.competitionsetup.form.CompetitionSetupForm;
import com.worth.ifs.fixtures.CompetitionFundersFixture;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;

import static com.worth.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class AssessorsFormPopulatorTest {

	private AssessorsFormPopulator populator;
	
	@Before
	public void setUp() {
		populator = new AssessorsFormPopulator();
	}

	@Test
	public void testPopulateForm() {
		CompetitionResource competition = newCompetitionResource()
				.withActivityCode("Activity Code")
				.withInnovateBudget("Innovate Budget")
				.withCompetitionCode("c123")
				.withPafCode("p123")
				.withBudgetCode("b123")
				.withFunders(CompetitionFundersFixture.getTestCoFunders())
				.withId(8L)
				.withAssessorCount(1)
				.withAssessorPay(BigDecimal.TEN).build();

		CompetitionSetupForm result = populator.populateForm(competition);
		
		assertTrue(result instanceof AssessorsForm);
		AssessorsForm form = (AssessorsForm) result;
		assertEquals(Integer.valueOf(1), form.getAssessorCount());
		assertEquals(BigDecimal.TEN, form.getAssessorPay());
	}
}
