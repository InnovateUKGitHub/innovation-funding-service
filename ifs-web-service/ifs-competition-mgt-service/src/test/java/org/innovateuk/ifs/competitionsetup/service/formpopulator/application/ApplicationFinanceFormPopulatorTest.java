package org.innovateuk.ifs.competitionsetup.service.formpopulator.application;

import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupFinanceResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSubsection;
import org.innovateuk.ifs.competitionsetup.form.CompetitionSetupForm;
import org.innovateuk.ifs.competitionsetup.form.application.ApplicationFinanceForm;
import org.innovateuk.ifs.competitionsetup.service.CompetitionSetupFinanceService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Optional;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.competition.builder.CompetitionSetupFinanceResourceBuilder.newCompetitionSetupFinanceResource;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ApplicationFinanceFormPopulatorTest {

	@InjectMocks
	private ApplicationFinanceFormPopulator populator;

	@Mock
	private CompetitionSetupFinanceService competitionSetupFinanceService;

	@Test
	public void testSectionToFill() {
		CompetitionSetupSubsection result = populator.sectionToFill();
		assertEquals(CompetitionSetupSubsection.FINANCES, result);
	}
				
	@Test
	public void testPopulateWithoutErrors() {

		long compId = 8L;
		boolean isFullApplication = true;
		boolean isIncludeGrowthTable = true;

		CompetitionSetupFinanceResource csfr = newCompetitionSetupFinanceResource()
				.withIncludeGrowthTable(isIncludeGrowthTable)
				.withFullApplicationFinance(isFullApplication)
				.build();

		when(competitionSetupFinanceService.getByCompetitionId(compId)).thenReturn(serviceSuccess(csfr));

		CompetitionResource competition = newCompetitionResource()
				.withId(compId)
				.build();

		CompetitionSetupForm result = populator.populateForm(competition, Optional.empty());
		
		assertTrue(result instanceof ApplicationFinanceForm);
		ApplicationFinanceForm form = (ApplicationFinanceForm) result;
		assertEquals(isFullApplication, form.isFullApplicationFinance());
		assertEquals(isIncludeGrowthTable, form.isIncludeGrowthTable());
	}
}
