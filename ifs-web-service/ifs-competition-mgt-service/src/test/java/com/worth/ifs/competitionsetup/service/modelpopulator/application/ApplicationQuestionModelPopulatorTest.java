package com.worth.ifs.competitionsetup.service.modelpopulator.application;

import com.worth.ifs.application.service.CompetitionService;
import com.worth.ifs.application.service.SectionService;
import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.competition.resource.CompetitionSetupSubsection;
import com.worth.ifs.competitionsetup.form.application.ApplicationFinanceForm;
import com.worth.ifs.util.CollectionFunctions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import java.util.Optional;

import static com.worth.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class ApplicationQuestionModelPopulatorTest {

	@InjectMocks
	private ApplicationQuestionModelPopulator populator;

	@Mock
	private CompetitionService competitionService;

	@Mock
	private SectionService sectionService;
	
	@Test
	public void testSectionToPopulateModel() {
		CompetitionSetupSubsection result = populator.sectionToPopulateModel();
		
		assertEquals(CompetitionSetupSubsection.QUESTIONS, result);
	}
	
	@Test
	public void testPopulateModel() {
		Model model = new ExtendedModelMap();
		CompetitionResource competitionResource = newCompetitionResource()
				.withCompetitionCode("code")
				.withName("name")
				.withId(8L)
				.withResearchCategories(CollectionFunctions.asLinkedSet(2L, 3L))
				.build();
		competitionResource.setFullApplicationFinance(true);
		competitionResource.setIncludeGrowthTable(false);
		ApplicationFinanceForm form = new ApplicationFinanceForm();
		form.setFullApplicationFinance(competitionResource.isFullApplicationFinance());
		form.setIncludeGrowthTable(competitionResource.isIncludeGrowthTable());


		populator.populateModel(model, competitionResource, Optional.empty());
		assertEquals(0, model.asMap().size());
	}
}
