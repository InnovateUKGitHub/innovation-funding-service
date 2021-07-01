package org.innovateuk.ifs.management.competition.setup.milestone.populator;

import org.innovateuk.ifs.competition.resource.CompetitionCompletionStage;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSection;
import org.innovateuk.ifs.management.competition.setup.completionstage.util.CompletionStageUtils;
import org.innovateuk.ifs.management.competition.setup.core.viewmodel.GeneralSetupViewModel;
import org.innovateuk.ifs.management.competition.setup.milestone.viewmodel.MilestonesViewModel;
import org.innovateuk.ifs.util.CollectionFunctions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class MilestonesModelPopulatorTest {

	@Mock
	private CompletionStageUtils completionStageUtils;

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
				.withCompletionStage(CompetitionCompletionStage.RELEASE_FEEDBACK)
				.build();

		when(completionStageUtils.isApplicationSubmissionEnabled(CompetitionCompletionStage.RELEASE_FEEDBACK)).thenReturn(true);

		MilestonesViewModel viewModel = (MilestonesViewModel) populator.populateModel(getBasicGeneralSetupView(competitionResource), competitionResource);

		assertEquals(CompetitionSetupSection.MILESTONES, viewModel.getGeneral().getCurrentSection());
		assertThat(viewModel.isApplicationSubmissionEnabled()).isEqualTo(true);
	}

	@Test
	public void testPopulateModelForCompetitionClose() {
		CompetitionResource competitionResource = newCompetitionResource()
				.withCompetitionCode("code")
				.withName("name")
				.withId(8L)
				.withResearchCategories(CollectionFunctions.asLinkedSet(2L, 3L))
				.withCompletionStage(CompetitionCompletionStage.COMPETITION_CLOSE)
				.build();

		when(completionStageUtils.isApplicationSubmissionEnabled(CompetitionCompletionStage.COMPETITION_CLOSE)).thenReturn(false);

		MilestonesViewModel viewModel = (MilestonesViewModel) populator.populateModel(getBasicGeneralSetupView(competitionResource), competitionResource);

		assertEquals(CompetitionSetupSection.MILESTONES, viewModel.getGeneral().getCurrentSection());
		assertThat(viewModel.isApplicationSubmissionEnabled()).isEqualTo(false);
	}

	private GeneralSetupViewModel getBasicGeneralSetupView(CompetitionResource competition) {
		return new GeneralSetupViewModel(Boolean.FALSE, false, competition, CompetitionSetupSection.MILESTONES, CompetitionSetupSection.values(), Boolean.TRUE, Boolean.FALSE);
	}
}
