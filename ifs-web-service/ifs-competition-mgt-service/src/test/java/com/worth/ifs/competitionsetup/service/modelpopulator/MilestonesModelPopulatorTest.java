package com.worth.ifs.competitionsetup.service.modelpopulator;

import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.competition.resource.CompetitionSetupSection;
import com.worth.ifs.competitionsetup.form.application.ApplicationFinanceForm;
import com.worth.ifs.util.CollectionFunctions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import static com.worth.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class MilestonesModelPopulatorTest {

	@InjectMocks
	private MilestonesModelPopulator populator;
	
	@Test
	public void testSectionToPopulateModel() {
		CompetitionSetupSection result = populator.sectionToPopulateModel();

		assertEquals(CompetitionSetupSection.MILESTONES, result);
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


		populator.populateModel(model, competitionResource);
		assertEquals(0, model.asMap().size());
	}
}
