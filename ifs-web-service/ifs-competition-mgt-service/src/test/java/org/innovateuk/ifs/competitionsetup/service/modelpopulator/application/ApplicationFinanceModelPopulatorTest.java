package org.innovateuk.ifs.competitionsetup.service.modelpopulator.application;

import org.innovateuk.ifs.application.service.CompetitionService;
import org.innovateuk.ifs.application.service.SectionService;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.builder.CompetitionSetupFinanceResourceBuilder;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupFinanceResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSubsection;
import org.innovateuk.ifs.competitionsetup.form.application.ApplicationFinanceForm;
import org.innovateuk.ifs.competitionsetup.service.CompetitionSetupFinanceService;
import org.innovateuk.ifs.util.CollectionFunctions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import java.util.Optional;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.competition.builder.CompetitionSetupFinanceResourceBuilder.newCompetitionSetupFinanceResource;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ApplicationFinanceModelPopulatorTest {

	@InjectMocks
	private ApplicationFinancesModelPopulator populator;

	@Mock
	private CompetitionService competitionService;

	@Mock
	private SectionService sectionService;

	@Mock
	private CompetitionSetupFinanceService competitionSetupFinanceService;
	
	@Test
	public void testSectionToPopulateModel() {
		CompetitionSetupSubsection result = populator.sectionToPopulateModel();
		assertEquals(CompetitionSetupSubsection.FINANCES, result);
	}
	
	@Test
	public void testPopulateModel() {
		long competitionId = 1L;
		boolean isFullApplication = true;
		boolean isIncludeGrowthTable = true;
		CompetitionResource cr = newCompetitionResource().withId(competitionId).build();
		CompetitionSetupFinanceResource csfr = newCompetitionSetupFinanceResource().
				withIncludeGrowthTable(isIncludeGrowthTable).
				withFullApplicationFinance(isFullApplication).
				build();
		Model model = new ExtendedModelMap();
		when(competitionSetupFinanceService.getByCompetitionId(cr.getId())).thenReturn(serviceSuccess(csfr));
		// Method under test
		populator.populateModel(model, cr, Optional.empty());
		// Assertions
		assertEquals(2, model.asMap().size());
		assertEquals(competitionId, model.asMap().get("competitionId"));
		assertEquals(isFullApplication, ((ApplicationFinanceForm)model.asMap().get("competitionSetupForm")).isFullApplicationFinance());
		assertEquals(isIncludeGrowthTable, ((ApplicationFinanceForm)model.asMap().get("competitionSetupForm")).isIncludeGrowthTable());
	}
}
