package org.innovateuk.ifs.management.competition.setup.assessor.populator;

import org.innovateuk.ifs.competition.resource.AssessorFinanceView;
import org.innovateuk.ifs.competition.resource.CompetitionAssessmentConfigResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionAssessmentConfigRestService;
import org.innovateuk.ifs.management.competition.setup.assessor.form.AssessorsForm;
import org.innovateuk.ifs.management.competition.setup.core.form.CompetitionSetupForm;
import org.innovateuk.ifs.management.fixtures.CompetitionFundersFixture;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.math.BigDecimal;

import static java.lang.Boolean.FALSE;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionAssessmentConfigResourceBuilder.newCompetitionAssessmentConfigResource;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.competition.resource.AssessorFinanceView.OVERVIEW;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class AssessorsFormPopulatorTest {

	private AssessorsFormPopulator populator;

	@Mock
	private CompetitionAssessmentConfigRestService competitionAssessmentConfigRestService;
	
	@Before
	public void setUp() {
		populator = new AssessorsFormPopulator();
	}

	@Test
	public void populateForm() {
		CompetitionResource competition = newCompetitionResource()
				.withActivityCode("Activity Code")
				.withCompetitionCode("c123")
				.withPafCode("p123")
				.withBudgetCode("b123")
				.withFunders(CompetitionFundersFixture.getTestCoFunders())
				.withId(8L)
				.build();

		CompetitionAssessmentConfigResource competitionAssessmentConfigResource = newCompetitionAssessmentConfigResource()
				.withAverageAssessorScore(false)
				.withAssessorCount(1)
				.withAssessorPay(BigDecimal.TEN)
				.withHasAssessmentPanel(FALSE)
				.withHasInterviewStage(FALSE)
				.withAssessorFinanceView(AssessorFinanceView.OVERVIEW)
				.build();

		when(competitionAssessmentConfigRestService.findOneByCompetitionId(competition.getId())).thenReturn(restSuccess(competitionAssessmentConfigResource));
		CompetitionSetupForm result = populator.populateForm(competition);

		assertTrue(result instanceof AssessorsForm);
		AssessorsForm form = (AssessorsForm) result;
		assertEquals(Integer.valueOf(1), form.getAssessorCount());
		assertEquals(BigDecimal.TEN, form.getAssessorPay());
		assertEquals(FALSE, form.getHasAssessmentPanel());
		assertEquals(FALSE, form.getHasInterviewStage());
		assertEquals(OVERVIEW, form.getAssessorFinanceView());
	}
}
