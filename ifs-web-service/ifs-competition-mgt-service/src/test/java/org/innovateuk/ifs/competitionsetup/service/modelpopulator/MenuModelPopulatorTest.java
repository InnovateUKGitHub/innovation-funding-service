package org.innovateuk.ifs.competitionsetup.service.modelpopulator;

import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSection;
import org.innovateuk.ifs.competitionsetup.viewmodel.MenuViewModel;
import org.innovateuk.ifs.competitionsetup.viewmodel.fragments.GeneralSetupViewModel;
import org.innovateuk.ifs.publiccontent.service.PublicContentService;
import org.innovateuk.ifs.util.CollectionFunctions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.time.ZonedDateTime;

import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.publiccontent.builder.PublicContentResourceBuilder.newPublicContentResource;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class MenuModelPopulatorTest {

	@InjectMocks
	private MenuModelPopulator populator;
	
	@Mock
	private PublicContentService publicContentService;
	
	@Test
	public void testSectionToPopulateModel() {
		CompetitionSetupSection result = populator.sectionToPopulateModel();
		
		assertEquals(CompetitionSetupSection.HOME, result);
	}
	
	@Test
	public void testPopulateModel() {
		CompetitionResource competition = newCompetitionResource()
				.withCompetitionCode("code")
				.withName("name")
				.withId(8L)
				.withResearchCategories(CollectionFunctions.asLinkedSet(2L, 3L))
				.build();

		ZonedDateTime today = ZonedDateTime.now();
		PublicContentResource publicContentResource = newPublicContentResource().withPublishDate(today).build();
		when(publicContentService.getCompetitionById(any(Long.class))).thenReturn(publicContentResource);

        MenuViewModel viewModel = (MenuViewModel) populator.populateModel(getBasicGeneralSetupView(competition), competition);

        assertEquals(today, viewModel.getPublishDate());
        assertEquals(Boolean.TRUE, viewModel.isPublicContentPublished());
        assertEquals(CompetitionSetupSection.HOME, viewModel.getGeneral().getCurrentSection());
	}

	@Test
	public void testPopulateModelWithUnpublishedContent() {
		CompetitionResource competition = newCompetitionResource()
				.withCompetitionCode("code")
				.withName("name")
				.withId(8L)
				.withResearchCategories(CollectionFunctions.asLinkedSet(2L, 3L))
				.build();

		PublicContentResource publicContentResource = newPublicContentResource().withPublishDate(null).build();
		when(publicContentService.getCompetitionById(any(Long.class))).thenReturn(publicContentResource);

        MenuViewModel viewModel = (MenuViewModel) populator.populateModel(getBasicGeneralSetupView(competition), competition);

		assertEquals(null, viewModel.getPublishDate());
		assertEquals(Boolean.FALSE, viewModel.isPublicContentPublished());
        assertEquals(CompetitionSetupSection.HOME, viewModel.getGeneral().getCurrentSection());
	}

	private GeneralSetupViewModel getBasicGeneralSetupView(CompetitionResource competition) {
	    return new GeneralSetupViewModel(Boolean.FALSE, competition, CompetitionSetupSection.HOME, CompetitionSetupSection.values(), Boolean.TRUE);
    }
}
