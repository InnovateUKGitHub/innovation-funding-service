package org.innovateuk.ifs.management.competition.setup.assessor.populator;

import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.management.competition.setup.assessor.form.AssessorsForm;
import org.innovateuk.ifs.management.competition.setup.core.form.CompetitionSetupForm;
import org.innovateuk.ifs.management.fixtures.CompetitionFundersFixture;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;

import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.competition.resource.AssessorFinanceView.OVERVIEW;
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
				.withCompetitionCode("c123")
				.withPafCode("p123")
				.withBudgetCode("b123")
				.withFunders(CompetitionFundersFixture.getTestCoFunders())
				.withId(8L)
				.withAssessorCount(1)
				.withAssessorPay(BigDecimal.TEN)
				.withHasAssessmentPanel(Boolean.FALSE)
				.withHasInterviewStage(Boolean.FALSE)
				.withAssessorFinanceView(OVERVIEW).build();

		CompetitionSetupForm result = populator.populateForm(competition);
		
		assertTrue(result instanceof AssessorsForm);
		AssessorsForm form = (AssessorsForm) result;
		assertEquals(Integer.valueOf(1), form.getAssessorCount());
		assertEquals(BigDecimal.TEN, form.getAssessorPay());
		assertEquals(Boolean.FALSE, form.getHasAssessmentPanel());
		assertEquals(Boolean.FALSE, form.getHasInterviewStage());
		assertEquals(OVERVIEW, form.getAssessorFinanceView());
	}
}
