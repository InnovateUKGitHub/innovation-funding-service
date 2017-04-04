package org.innovateuk.ifs.competitionsetup.service.modelpopulator;

import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSection;
import org.innovateuk.ifs.publiccontent.service.PublicContentService;
import org.innovateuk.ifs.util.CollectionFunctions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import java.time.ZonedDateTime;
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
		Model model = new ExtendedModelMap();
		CompetitionResource competition = newCompetitionResource()
				.withCompetitionCode("code")
				.withName("name")
				.withId(8L)
				.withResearchCategories(CollectionFunctions.asLinkedSet(2L, 3L))
				.build();

		ZonedDateTime today = ZonedDateTime.now();
		PublicContentResource publicContentResource = newPublicContentResource().withPublishDate(today).build();
		when(publicContentService.getCompetitionById(any(Long.class))).thenReturn(publicContentResource);

		populator.populateModel(model, competition);
		
		assertEquals(2, model.asMap().size());
		assertEquals(today, model.asMap().get("publishDate"));
		assertEquals(true, model.asMap().get("isPublicContentPublished"));
	}

	@Test
	public void testPopulateModelWithUnpublishedContent() {
		Model model = new ExtendedModelMap();
		CompetitionResource competition = newCompetitionResource()
				.withCompetitionCode("code")
				.withName("name")
				.withId(8L)
				.withResearchCategories(CollectionFunctions.asLinkedSet(2L, 3L))
				.build();

		ZonedDateTime today = ZonedDateTime.now();
		PublicContentResource publicContentResource = newPublicContentResource().withPublishDate(null).build();
		when(publicContentService.getCompetitionById(any(Long.class))).thenReturn(publicContentResource);

		populator.populateModel(model, competition);

		assertEquals(2, model.asMap().size());
		assertEquals(null, model.asMap().get("publishDate"));
		assertEquals(false, model.asMap().get("isPublicContentPublished"));
	}
}
