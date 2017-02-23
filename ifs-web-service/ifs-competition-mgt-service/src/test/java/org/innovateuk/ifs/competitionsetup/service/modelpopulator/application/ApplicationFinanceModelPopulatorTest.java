package org.innovateuk.ifs.competitionsetup.service.modelpopulator.application;

import org.innovateuk.ifs.application.service.CompetitionService;
import org.innovateuk.ifs.application.service.SectionService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSubsection;
import org.innovateuk.ifs.util.CollectionFunctions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import java.util.Optional;

import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class ApplicationFinanceModelPopulatorTest {

	@InjectMocks
	private ApplicationFinanceModelPopulator populator;

	@Mock
	private CompetitionService competitionService;

	@Mock
	private SectionService sectionService;
	
	@Test
	public void testSectionToPopulateModel() {
		CompetitionSetupSubsection result = populator.sectionToPopulateModel();
		assertEquals(CompetitionSetupSubsection.FINANCES, result);
	}
	
	@Test
	public void testPopulateModel() {
		long competitionId = 8L;
		CompetitionResource cr = newCompetitionResource()
				.withCompetitionCode("code")
				.withName("name")
				.withId(competitionId)
				.withResearchCategories(CollectionFunctions.asLinkedSet(2L, 3L))
				.withCompetitionTypeName("programme")
				.build();

		Model model = new ExtendedModelMap();
		// Method under test
		populator.populateModel(model, cr, Optional.empty());
		// Assertions
		// First check that there is not more than we expect
		assertEquals(2, model.asMap().size());
		assertEquals(competitionId, model.asMap().get("competitionId"));
		assertEquals(false, model.asMap().get("isSectorCompetition"));
	}
}
