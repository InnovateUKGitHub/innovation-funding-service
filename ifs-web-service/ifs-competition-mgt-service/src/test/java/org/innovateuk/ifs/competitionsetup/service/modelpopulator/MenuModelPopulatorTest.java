package org.innovateuk.ifs.competitionsetup.service.modelpopulator;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSection;
import org.innovateuk.ifs.competition.service.CompetitionSetupRestService;
import org.innovateuk.ifs.competitionsetup.viewmodel.MenuViewModel;
import org.innovateuk.ifs.competitionsetup.viewmodel.fragments.GeneralSetupViewModel;
import org.innovateuk.ifs.publiccontent.service.PublicContentService;
import org.innovateuk.ifs.util.CollectionFunctions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.time.ZonedDateTime;
import java.util.Map;
import java.util.Optional;

import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.publiccontent.builder.PublicContentResourceBuilder.newPublicContentResource;
import static org.innovateuk.ifs.util.MapFunctions.asMap;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class MenuModelPopulatorTest {

    private static final Long COMPETITION_ID = 23L;

	@InjectMocks
	private MenuModelPopulator populator;
	
	@Mock
	private PublicContentService publicContentService;

	@Mock
	private CompetitionSetupRestService competitionSetupRestService;

	@Before
    public void setup() {
        Map<CompetitionSetupSection, Optional<Boolean>> statuses = asMap(CompetitionSetupSection.INITIAL_DETAILS, true, CompetitionSetupSection.CONTENT, false);
        when(competitionSetupRestService.getSectionStatuses(COMPETITION_ID)).thenReturn(RestResult.restSuccess(statuses));
    }

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
				.withId(COMPETITION_ID)
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
				.withId(COMPETITION_ID)
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
