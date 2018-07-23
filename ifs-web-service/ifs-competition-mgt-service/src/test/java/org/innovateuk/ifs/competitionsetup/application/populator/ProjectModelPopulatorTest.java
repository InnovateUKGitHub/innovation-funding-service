package org.innovateuk.ifs.competitionsetup.application.populator;

import org.innovateuk.ifs.application.service.SectionService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSubsection;
import org.innovateuk.ifs.competitionsetup.application.viewmodel.ProjectViewModel;
import org.innovateuk.ifs.util.CollectionFunctions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;

import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class ProjectModelPopulatorTest {

	@InjectMocks
	private ProjectModelPopulator populator;

	@Mock
	private SectionService sectionService;
	
	@Test
	public void testSectionToPopulateModel() {
		CompetitionSetupSubsection result = populator.sectionToPopulateModel();
		
		assertEquals(CompetitionSetupSubsection.PROJECT_DETAILS, result);
	}
	
	@Test
	public void testPopulateModel() {
		CompetitionResource competitionResource = newCompetitionResource()
				.withCompetitionCode("code")
				.withName("name")
				.withId(8L)
				.withResearchCategories(CollectionFunctions.asLinkedSet(2L, 3L))
				.build();
		ProjectViewModel viewModel = (ProjectViewModel) populator.populateModel(competitionResource, Optional.empty());
		assertEquals(ProjectViewModel.class, viewModel.getClass());
	}
}
