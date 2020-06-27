package org.innovateuk.ifs.management.competition.setup.milestone.populator;

import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSection;
import org.innovateuk.ifs.management.competition.setup.core.viewmodel.GeneralSetupViewModel;
import org.innovateuk.ifs.management.competition.setup.milestone.viewmodel.MilestonesViewModel;
import org.innovateuk.ifs.util.CollectionFunctions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.Silent.class)
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
		CompetitionResource competitionResource = newCompetitionResource()
				.withCompetitionCode("code")
				.withName("name")
				.withId(8L)
				.withResearchCategories(CollectionFunctions.asLinkedSet(2L, 3L))
				.build();
		MilestonesViewModel viewModel = (MilestonesViewModel) populator.populateModel(getBasicGeneralSetupView(competitionResource), competitionResource);

		assertEquals(CompetitionSetupSection.MILESTONES, viewModel.getGeneral().getCurrentSection());
	}

	private GeneralSetupViewModel getBasicGeneralSetupView(CompetitionResource competition) {
		return new GeneralSetupViewModel(Boolean.FALSE, competition, CompetitionSetupSection.MILESTONES, CompetitionSetupSection.values(), Boolean.TRUE, Boolean.FALSE);
	}
}
